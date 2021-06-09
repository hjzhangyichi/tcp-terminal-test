package terminaltest.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jdk.nashorn.internal.ir.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import terminaltest.service.ClientService;
import terminaltest.template.SimpleInitializer;
import terminaltest.protocol.gb17691.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyichi
 */
@Component
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "gb17691", name = "usable", havingValue = "true")
public class Gb17691ClientService implements ClientService {

    @Value("${gb17691.file.input-path}")
    private String inputFilePath;

    @Value("${gb17691.file.output-path}")
    private String outputFilePath;

    @Value("${gb17691.file.size}")
    private int size;


    private NioEventLoopGroup group;
    private Bootstrap bootstrap;

    private ConcurrentHashMap<String, Gb17691Client> clientMap = new ConcurrentHashMap<>();

    private List<Gb17691ExcelRow> rows = null;



    @RequestMapping("clients")
    List<Gb17691ExcelRow> getClients(){
        List<Gb17691ExcelRow> rows = new ArrayList<Gb17691ExcelRow>();
        clientMap.forEach((k,v)->{
            addTerminalInfoRow(rows, v);
        });
        return rows;
    }

    private void addTerminalInfoRow(List<Gb17691ExcelRow> rows, Gb17691Client v) {
        Gb17691Terminal terminal = v.getTerminal();
        Gb17691ExcelRow row = new Gb17691ExcelRow();
        row.setVin(new String(terminal.getVin()));
        row.setCvn(new String(terminal.getCvn()));
        row.setScid(new String(terminal.getScid()));
        row.setIupr(new String(terminal.getIupr()));
        row.setSim(new String(terminal.getSim()));
        row.setJd(String.valueOf(terminal.getJd()*0.00001));
        row.setWd(String.valueOf(terminal.getWd()*0.00001));
        row.setZxslc(String.valueOf(terminal.getWd()*0.1));
        rows.add(row);
    }


    @Override
    public void init(String host, int port){
        log.info("Service initialized");
        this.init(host,port,this.inputFilePath);
    }

    public void init(String host,int port,String path){
        File file = new File(path);
        if (!file.canRead()){
            log.error("没这个文件");
            return;
        }
        try {
            rows = EasyExcel.read(new BufferedInputStream(new FileInputStream(file))).head(Gb17691ExcelRow.class).sheet().doReadSync();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.group  = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new SimpleInitializer<Gb17691Message>(Gb17691MessageDecoder.class, Gb17691MessageEncoder.class));
        } catch (Exception e){
            //我就啥都不做，诶，就是玩
        }
        if (rows != null){
            for (int i=0;i<rows.size() && (size == 0 || i<size);i++){
                Gb17691ExcelRow row = rows.get(i);
                try {
                    Gb17691Terminal terminal = new Gb17691Terminal();
                    terminal.setVin(row.getVin().getBytes());
                    terminal.setSim(row.getSim().getBytes());
                    terminal.setScid(row.getScid().getBytes());
                    terminal.setCvn(row.getCvn().getBytes());
                    terminal.setIupr(row.getIupr().getBytes());
                    terminal.setZxslc((int) (Double.parseDouble(row.getZxslc())*10));
                    terminal.setJd((int) (Double.parseDouble(row.getJd())*100000));
                    terminal.setWd((int) (Double.parseDouble(row.getWd())*100000));
                    Gb17691Client client = new Gb17691Client();
                    client.init(host,port,this.bootstrap,terminal);
                    clientMap.put(row.getVin(),client);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void start(){
        clientMap.forEach((k,v)->{
            v.run();
        });
    }

    @Override
    public void stop() throws IOException {
        List<Gb17691ExcelRow> rows = new ArrayList<Gb17691ExcelRow>();
        clientMap.forEach((k,v)->{
            v.stop();
            addTerminalInfoRow(rows, v);
        });

        for (int i = size;i<this.rows.size();i++){
            rows.add(this.rows.get(i));
        }

        File file = new File(outputFilePath);
        if (!file.exists()){
            file.createNewFile();
        }else {
            file.delete();
            file.createNewFile();
        }

        ExcelWriter excelWriter = EasyExcel.write(new FileOutputStream(file), Gb17691ExcelRow.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("").build();
        excelWriter.write(rows, writeSheet);
        excelWriter.finish();
    }

    @Override
    public void close() throws Exception {
        this.stop();
        group.shutdownGracefully();
    }


}

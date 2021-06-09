package terminaltest.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.CustomMessage;
import terminaltest.protocol.custom.fragment.PeriodTask;
import terminaltest.protocol.custom.fragment.impl.*;
import terminaltest.protocol.gb17691.Gb17691ExcelRow;
import terminaltest.service.ClientService;
import terminaltest.template.CustomInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "custom", name = "usable", havingValue = "true")
public class CustomClientService implements ClientService {

    private NioEventLoopGroup group;
    private Bootstrap bootstrap;

    private ConcurrentHashMap<String, CustomClient> clientMap = new ConcurrentHashMap<>();


    @Override
    public void stop() throws IOException {
        clientMap.forEach((k,v)->{
            v.stop();
        });
    }

    @Override
    public void start() {
        clientMap.forEach((k,v)->{
            v.run();
        });
    }


    @RequestMapping("clients")
    List<Map<String,byte[]>> getClients(){
        List<Map<String,byte[]>> rows = new ArrayList<Map<String,byte[]>>();
        clientMap.forEach((k,v)->{
            rows.add(v.getTerminal());
        });
        return rows;
    }

    @Override
    public void init(String host, int port) {
        log.info("Service initialized");
        FixedFragment start = new FixedFragment(new byte[]{0x23,0x23});
        FixedFragment one = new FixedFragment(new byte[]{0x01});
        FixedFragment two = new FixedFragment(new byte[]{0x02});
        ParamFragment vin = new ParamFragment(17,"vin");
        TimeFragment time = new TimeFragment(1);

        SerialFragment loginOrOutSerial = new SerialFragment(2);
        SerialFragment realTimeDataSerial = new SerialFragment(2);
        RandomFragment word64255Random = new RandomFragment(2,64255,0);
        RandomFragment wordRandom = new RandomFragment(2,65536,0);
        RandomFragment byte250Random = new RandomFragment(1,250,-125);
        FixedFragment thirtySix0 = new FixedFragment(new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,
                0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,
                0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,
                0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30});

        StructFragment loginMsg = new StructFragment(3);
        loginMsg.addFragment(start);
        loginMsg.addFragment(one);
        loginMsg.addFragment(vin);
        loginMsg.addFragment(one);
        loginMsg.addFragment(one);
        StructFragment loginData = new StructFragment(0);
        loginData.addFragment(time);
        loginData.addFragment(loginOrOutSerial);
        loginData.addFragment(new ParamFragment(20,"sim"));
        loginMsg.addFragment(new LengthFragment(2,loginData));
        loginMsg.addFragment(loginData);

        StructFragment logoutMsg = new StructFragment(3);
        logoutMsg.addFragment(start);
        logoutMsg.addFragment(new FixedFragment(new byte[]{0x04}));
        logoutMsg.addFragment(vin);
        logoutMsg.addFragment(one);
        logoutMsg.addFragment(one);
        StructFragment logoutData = new StructFragment(0);
        logoutData.addFragment(time);
        logoutData.addFragment(loginOrOutSerial);
        logoutMsg.addFragment(new LengthFragment(2,logoutData));
        logoutMsg.addFragment(logoutData);



        StructFragment dataflowMsg = new StructFragment(3);
        dataflowMsg.addFragment(start);
        dataflowMsg.addFragment(two);
        dataflowMsg.addFragment(vin);
        dataflowMsg.addFragment(one);
        dataflowMsg.addFragment(one);
        StructFragment dataflowData = new StructFragment(0);
        dataflowData.addFragment(time);
        dataflowData.addFragment(two);
        dataflowData.addFragment(realTimeDataSerial);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(word64255Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(byte250Random);
        dataflowData.addFragment(new RandomFragment(4,15*100000,100*100000));
        dataflowData.addFragment(new RandomFragment(4,10*100000,30*100000));
        dataflowData.addFragment(new RandomFragment(4,100000*10,0));
        dataflowMsg.addFragment(new LengthFragment(2,dataflowData));
        dataflowMsg.addFragment(dataflowData);

        StructFragment obdMsg = new StructFragment(3);
        obdMsg.addFragment(start);
        obdMsg.addFragment(new FixedFragment(new byte[]{0x02}));
        obdMsg.addFragment(vin);
        obdMsg.addFragment(one);
        obdMsg.addFragment(one);
        StructFragment obdData = new StructFragment(0);
        obdData.addFragment(time);
        obdData.addFragment(one);
        obdData.addFragment(realTimeDataSerial);
        obdData.addFragment(one);
        obdData.addFragment(one);
        obdData.addFragment(new RandomFragment(2,65536,0));
        obdData.addFragment(new RandomFragment(2,65536,0));
        obdData.addFragment(vin);
        obdData.addFragment(new FixedFragment(new byte[18]));
        obdData.addFragment(new FixedFragment(new byte[18]));
        obdData.addFragment(thirtySix0);
        obdData.addFragment(two);
        obdData.addFragment(wordRandom);
        obdData.addFragment(wordRandom);
        obdData.addFragment(wordRandom);
        obdData.addFragment(wordRandom);
        obdMsg.addFragment(new LengthFragment(2,obdData));
        obdMsg.addFragment(obdData);


        Map<String,StructFragment> fragmentMap = new HashMap<>();
        fragmentMap.put("01",loginMsg);
        fragmentMap.put("021",obdMsg);
        fragmentMap.put("022",dataflowMsg);
        fragmentMap.put("04",logoutMsg);

        this.group  = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new CustomInitializer(fragmentMap));
        } catch (Exception e){
            //我就啥都不做，诶，就是玩
        }

        List<Map<String,byte[]>> clientsParams = new ArrayList<>();
        Map<String,byte[]> client1 = new HashMap<>();
        client1.put("clientName","WJLWDWDSSN1000499".getBytes());
        client1.put("vin","WJLWDWDSSN1000499".getBytes());
        client1.put("sim","WJLWDWDSSN1000000499".getBytes());
        clientsParams.add(client1);
        Map<String,byte[]> client2 = new HashMap<>();
        client2.put("clientName","WJLWDWDSSN1000498".getBytes());
        client2.put("vin","WJLWDWDSSN1000498".getBytes());
        client2.put("sim","WJLWDWDSSN1000000498".getBytes());
        clientsParams.add(client2);

        List<String> initialTaskList = new ArrayList<>();
        List<String> closeTaskList = new ArrayList<>();
        List<PeriodTask> periodTaskList = new ArrayList<>();
        initialTaskList.add("01");
        closeTaskList.add("04");
        periodTaskList.add(new PeriodTask("021",30,24*60*60));
        periodTaskList.add(new PeriodTask("022",10,10));

        for (Map<String,byte[]> params : clientsParams){
            CustomClient client = new CustomClient();
            client.setInitialTaskList(initialTaskList);
            client.setCloseTaskList(closeTaskList);
            client.setPeriodTaskList(periodTaskList);
            client.init(host,port,bootstrap,params);
            clientMap.put(new String(params.get("clientName")),client);
        }
    }

    @Override
    public void close() throws Exception {
        this.stop();
        group.shutdownGracefully();
    }
}

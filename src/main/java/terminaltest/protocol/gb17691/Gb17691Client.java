package terminaltest.protocol.gb17691;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import terminaltest.template.Client;
import terminaltest.unit.MessageTimeUnit;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangyichi
 */
@Slf4j
public class Gb17691Client extends Client<Gb17691Message,Gb17691Terminal> {



    private byte[] vin;
    private byte[] sim;
    private byte[] scid;
    private byte[] cvn;
    private byte[] iupr;
    private int jd;
    private int wd;
    private int zxslc;

    ScheduledFuture<?> obdFuture = null;
    ScheduledFuture<?> dataflowFuture = null;




    private int loginSerial = 0;
    private int dataSerial = 0;

    @Override
    public void init(String host, int port,Bootstrap bootstrap, Gb17691Terminal terminal){
        this.host = host;
        this.port = port;
        this.vin = terminal.getVin();
        this.sim = terminal.getSim();
        this.scid = terminal.getScid();
        this.cvn = terminal.getCvn();
        this.iupr = terminal.getIupr();
        this.jd = terminal.getJd();
        this.wd = terminal.getWd();
        this.zxslc = terminal.getZxslc();
        this.bootstrap = bootstrap;
    }


    @Override
    public Gb17691Terminal getTerminal(){
        return new Gb17691Terminal(vin,sim,scid,cvn,iupr,jd,wd,zxslc);
    }

    @Override
    public void doLogin(){
        if (!loginSign){
            try {
                Gb17691Message msg = new Gb17691Message((byte)0x01,vin,(byte)0x01,(byte)0x01,loginData());
                if(socketChannel != null){
                    socketChannel.writeAndFlush(msg);
                    loginSign = true;
                    log.info(new String(vin)+"登入成功, 开始实时消息上报!");
                    obdFuture =  socketChannel.eventLoop().scheduleAtFixedRate(this::doObd, 24*60*60,30, TimeUnit.SECONDS);
                    dataflowFuture = socketChannel.eventLoop().scheduleAtFixedRate(this::doDataflow, 10,10, TimeUnit.SECONDS);
                }else {
                    log.info(new String(vin)+"登入消息发送失败, Unable to get channel!");
                    socketChannel.eventLoop().schedule(this::doLogin, 60, TimeUnit.SECONDS);

                }
            }catch (Exception e){
                log.info(new String(vin)+"登入消息发送失败, Catch exception: "+e.getMessage(),e);
                socketChannel.eventLoop().schedule(this::doLogin, 60, TimeUnit.SECONDS);
            }
            //60s后重试
        }
    }

    private void doDataflow(){
        if (loginSign&&socketChannel!=null){
            try {
                Gb17691Message msg = new Gb17691Message((byte)0x02,vin,(byte)0x01,(byte)0x01,dataFlowData());
                socketChannel.writeAndFlush(msg);
            }catch (Exception e){
                log.info(new String(vin)+"实时消息发送失败, Catch exception: "+e.getMessage(),e);
            }
        }
    }

    private void doObd(){
        if (loginSign&&socketChannel!=null){
            try {
                Gb17691Message msg = new Gb17691Message((byte)0x02,vin,(byte)0x01,(byte)0x01,obdData());
                socketChannel.writeAndFlush(msg);
            }catch (Exception e){
                log.info(new String(vin)+"实时消息发送失败, Catch exception: "+e.getMessage(),e);
            }
        }
    }

     @Override
     public void doLogout(){
        if (loginSign){
            try {
                Gb17691Message msg = new Gb17691Message((byte)0x04,vin,(byte)0x01,(byte)0x01,logoutData());
                if(socketChannel != null){
                    socketChannel.writeAndFlush(msg);
                    loginSign = false;
                    log.info(new String(vin)+"登出成功!");
                    if (obdFuture != null){
                        obdFuture.cancel(false);
                    }
                    if (dataflowFuture != null){
                        dataflowFuture.cancel(false);
                    }
                }else {
                    log.info(new String(vin)+"登出消息发送失败, Unable to get channel!");
                }
            }catch (Exception e){
                log.info(new String(vin)+"登出消息发送失败, Catch exception: "+e.getMessage(),e);
            }
        }
    }

    private byte[] loginData() {
        byte[] data = new byte[28];
        System.arraycopy(MessageTimeUnit.nowTime2Byte(),0,data,0,6);
        data[6] = (byte)((loginSerial >> 8) & 0xFF);
        data[7] = (byte)(loginSerial & 0xFF);
        System.arraycopy(sim,0,data,8,20);
        return data;
    }
    private byte[] logoutData() {
        byte[] data = new byte[8];
        System.arraycopy(MessageTimeUnit.nowTime2Byte(),0,data,0,6);
        data[6] = (byte)((loginSerial >> 8) & 0xFF);
        data[7] = (byte)(loginSerial++ & 0xFF);
        return data;
    }
    private byte[] dataFlowData() {
        byte[] data = new byte[46];
        System.arraycopy(MessageTimeUnit.nowTime2Byte(),0,data,0,6);
        data[6] = (byte)(0x02);
        data[7] = (byte)((dataSerial >> 8) & 0xFF);
        data[8] = (byte)(dataSerial++ & 0xFF);
        double rand = Math.random();
        //车速高位
        data[9] = (byte) (rand*150);
        //车速低位
        data[10] = (byte) (rand*255);
        //大气压
        data[11] = (byte) 0xCA;
        //净输出扭矩
        data[12] = (byte) (25+rand*200);
        //摩擦扭矩
        data[13] = (byte) (25+rand*100);
        //发动机转速高位
        data[14] = (byte) (rand*244);
        //发动机转速低位
        data[15] = (byte) (rand*256);
        //燃料流量高位
        data[16] = (byte) (rand*240);
        //燃料流量低位
        data[17] = (byte) (rand*256);
        //上游nox高位
        data[18] = (byte) (rand*251);
        //上游nox低位
        data[19] = (byte) (rand*256);
        //下游nox高位
        data[20] = (byte) (rand*220);
        //下游nox低位
        data[21] = (byte) (rand*256);
        //反应剂余量
        data[22] = (byte) (rand*251);
        //进气量高位
        data[23] = (byte) (rand*249);
        //进气量低位
        data[24] = (byte) (rand*256);
        //入口温度高位
        data[25] = (byte) (rand*247);
        //入口温度低位
        data[26] = (byte) (rand*256);
        //出口温度高位
        data[27] = (byte) (rand*248);
        //出口温度低位
        data[28] = (byte) (rand*256);
        //dpf压差高位
        data[29] = (byte) (rand*246);
        //dpf压差低位
        data[30] = (byte) (rand*256);
        //冷却液温度
        data[31] = (byte) (rand*245);
        //油箱液位
        data[32] = (byte) (rand*243);
        //定位状态
        data[33] = (byte) 0x00;
        //经度
        data[34] = (byte) ((jd>>24)&0xFF);
        data[35] = (byte) ((jd>>16)&0xFF);
        data[36] = (byte) ((jd>>8)&0xFF);
        data[37] = (byte) (jd&0xFF);
        //纬度
        data[38] = (byte) ((wd>>24)&0xFF);
        data[39] = (byte) ((wd>>16)&0xFF);
        data[40] = (byte) ((wd>>8)&0xFF);
        data[41] = (byte) (wd&0xFF);
        //总里程
        data[42] = (byte) ((zxslc>>24)&0xFF);
        data[43] = (byte) ((zxslc>>16)&0xFF);
        data[44] = (byte) ((zxslc>>8)&0xFF);
        data[45] = (byte) (zxslc&0xFF);
        //经度随机±0.002以内
        jd += (rand*400-200);
        //纬度随机±0.001以内
        wd += (rand*200-100);
        //里程随机+0.3km以内
        zxslc += rand*3;
        return data;
    }

    private byte[] obdData() {
        int dtcCnt = (int) (Math.random()*5);
        byte[] data = new byte[105+dtcCnt*4];
        System.arraycopy(MessageTimeUnit.nowTime2Byte(),0,data,0,6);
        data[6] = (byte)(0x01);
        data[7] = (byte)((dataSerial >> 8) & 0xFF);
        data[8] = (byte)(dataSerial++ & 0xFF);
        data[9] = (byte) 0x00;
        data[10] = (byte) 0x01;
        data[11] = (byte) (Math.random()*256);
        data[12] = (byte) (Math.random()*256);
        data[13] = (byte) ((int)(Math.random()*256)&data[11]);
        data[14] = (byte) ((int)(Math.random()*256)&data[12]);
        System.arraycopy(vin,0,data,15,17);
        System.arraycopy(scid,0,data,32,18);
        System.arraycopy(cvn,0,data,50,18);
        System.arraycopy(iupr,0,data,68,36);
        data[104] = (byte) dtcCnt;
        for (int i=0;i<data[104];i++){
            data[105+i*4] = (byte) (Math.random()*256);
            data[106+i*4] = (byte) (Math.random()*256);
            data[107+i*4] = (byte) (Math.random()*256);
            data[108+i*4] = (byte) (Math.random()*256);
        }
        return data;
    }

    @Override
    public void onReceiveData(Gb17691Message msg){

    }

}

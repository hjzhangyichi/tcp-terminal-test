package terminaltest.protocol.custom;

import com.sun.xml.internal.ws.resources.ClientMessages;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import terminaltest.protocol.custom.fragment.PeriodTask;
import terminaltest.protocol.gb17691.Gb17691Message;
import terminaltest.template.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class CustomClient extends Client<CustomMessage, Map<String,byte[]>> {

    private String clientKey;
    private Map<String,byte[]> paramsMap;
    private List<String> initialTaskList;
    private List<PeriodTask> periodTaskList;
    private List<String> closeTaskList;

    public void setInitialTaskList(List<String> initialTaskList) {
        this.initialTaskList = initialTaskList;
    }

    public void setPeriodTaskList(List<PeriodTask> periodTaskList) {
        this.periodTaskList = new ArrayList<>();
        for (PeriodTask task:periodTaskList){
            this.periodTaskList.add(new PeriodTask(task.getType(),task.getInitialDelay(),task.getPeriodDelay()));
        }
    }

    public void setCloseTaskList(List<String> closeTaskList) {
        this.closeTaskList = closeTaskList;
    }

    public byte[] getParam(String key){
        return paramsMap.get(key);
    }


    @Override
    public void init(String host, int port, Bootstrap bootstrap, Map<String, byte[]> terminal) {
        this.host = host;
        this.port = port;
        this.paramsMap = terminal;
        this.bootstrap = bootstrap;
        this.clientKey = new String(paramsMap.get("clientName"));
    }

    @Override
    public Map<String, byte[]> getTerminal() {
        return paramsMap;
    }

    @Override
    public void doLogin() {
        if (!loginSign){
            try {
                if(socketChannel != null){
                    for (String type:initialTaskList){
                        sendMessage(type);
                    }
                    loginSign = true;
                    log.info(clientKey+"登入成功, 开始实时消息上报!");
                    for (PeriodTask task:periodTaskList){
                        ScheduledFuture<?> scheduledFuture = socketChannel.eventLoop().scheduleAtFixedRate(()->{
                            sendMessage(task.getType());
                        },task.getInitialDelay(),task.getPeriodDelay(),TimeUnit.SECONDS);
                        task.setScheduledFuture(scheduledFuture);
                    }
                }else {
                    log.info(clientKey+"登入消息发送失败, Unable to get channel!");
                    socketChannel.eventLoop().schedule(this::doLogin, 60, TimeUnit.SECONDS);

                }
            }catch (Exception e){
                log.info(clientKey+"登入消息发送失败, Catch exception: "+e.getMessage(),e);
                socketChannel.eventLoop().schedule(this::doLogin, 60, TimeUnit.SECONDS);
            }
            //60s后重试
        }
    }

    private void sendMessage(String type){
        socketChannel.writeAndFlush(new CustomMessage(type,this));
    }

    @Override
    public void doLogout() {
        if (loginSign){
            try {
                if(socketChannel != null){
                    for (String type:closeTaskList){
                        sendMessage(type);
                    }
                    loginSign = false;
                    log.info(clientKey+"登出成功!");
                    for (PeriodTask task:periodTaskList){
                        task.getScheduledFuture().cancel(false);
                    }
                }else {
                    log.info(clientKey+"登出消息发送失败, Unable to get channel!");
                }
            }catch (Exception e){
                log.info(clientKey+"登出消息发送失败, Catch exception: "+e.getMessage(),e);
            }
        }
    }

    @Override
    public void onReceiveData(CustomMessage msg) {
    }
}

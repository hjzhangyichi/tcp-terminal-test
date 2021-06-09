package terminaltest.template;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;


public abstract class Client<M,T> {

    protected String host;
    protected int port;

    private int delay = 10;

    protected Channel socketChannel;
    protected Bootstrap bootstrap;

    protected boolean loginSign = false;

    abstract public void init(String host, int port, Bootstrap bootstrap, T terminal);

    abstract public T getTerminal();

    public void run(){
        doConnect();
    }

    public void doConnect() {
        socketChannel = null;
        loginSign = false;
        try {
            bootstrap.connect(host,port).addListener((ChannelFuture futureListener)->{
                if (futureListener.isSuccess()) {
                    socketChannel = futureListener.channel();
                    doLogin();
                }else {
                    socketChannel.eventLoop().schedule(this::doConnect, delay, TimeUnit.SECONDS);
                }
            });
        } catch (Exception e) {
            socketChannel.eventLoop().schedule(this::doConnect, delay, TimeUnit.SECONDS);
        }
    }

    public void stop(){
        if (socketChannel!=null){
            doLogout();
            socketChannel.close();
            socketChannel = null;
        }
    }

    abstract public void doLogin();

    abstract public void doLogout();

    abstract public void onReceiveData(M msg);
}

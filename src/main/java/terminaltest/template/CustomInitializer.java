package terminaltest.template;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import terminaltest.protocol.custom.CustomMessageDecoder;
import terminaltest.protocol.custom.CustomMessageEncoder;
import terminaltest.protocol.custom.fragment.impl.StructFragment;

import java.util.Map;

/**
 * @author zhangyichi
 */
@Slf4j
public class CustomInitializer extends ChannelInitializer<SocketChannel> {

    Map<String, StructFragment> structFragMentMap;

    public CustomInitializer(Map<String, StructFragment> structFragMentMap){
        this.structFragMentMap = structFragMentMap;

    }

    @Override
    protected void initChannel(SocketChannel channel) throws IllegalAccessException, InstantiationException {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder",new CustomMessageDecoder());
        pipeline.addLast("encoder",new CustomMessageEncoder(this.structFragMentMap));
        pipeline.addLast(new SimpleChannelInboundHandler<StructFragment>(){
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, StructFragment message) {
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                cause.printStackTrace();
                ctx.close();
            }
        });
    }
}

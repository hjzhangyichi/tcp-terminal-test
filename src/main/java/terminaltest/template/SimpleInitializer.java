package terminaltest.template;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangyichi
 */
@Slf4j
public class SimpleInitializer<M> extends ChannelInitializer<SocketChannel> {

    Class<? extends ByteToMessageDecoder> decoder;

    Class<? extends MessageToByteEncoder<M>> encoder;

    public SimpleInitializer(Class<? extends ByteToMessageDecoder> decoder, Class<? extends MessageToByteEncoder<M>> encoder){
        this.decoder = decoder;
        this.encoder = encoder;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws IllegalAccessException, InstantiationException {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder",decoder.newInstance());
        pipeline.addLast("encoder",encoder.newInstance());
        pipeline.addLast(new SimpleChannelInboundHandler<M>(){
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, M message) {
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                cause.printStackTrace();
                ctx.close();
            }
        });
    }
}

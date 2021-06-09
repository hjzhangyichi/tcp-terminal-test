package terminaltest.protocol.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import terminaltest.protocol.custom.fragment.impl.StructFragment;

import java.util.Map;

/**
 * @author zhangyichi
 */
public class CustomMessageEncoder extends MessageToByteEncoder<CustomMessage> {
    Map<String, StructFragment> structFragMentMap;

    public CustomMessageEncoder(Map<String, StructFragment> structFragMentMap){
        this.structFragMentMap = structFragMentMap;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(structFragMentMap.get(msg.getType()).doRandom(msg.getContext()));
    }
}

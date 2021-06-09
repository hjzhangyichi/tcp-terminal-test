package terminaltest.protocol.gb17691;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhangyichi
 */
public class Gb17691MessageEncoder extends MessageToByteEncoder<Gb17691Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Gb17691Message msg, ByteBuf out) throws Exception {
        byte[] data = new byte[msg.getData().length + 25];
        data[0] = 0x23;
        data[1] = 0x23;
        data[2] = msg.getCmd();
        System.arraycopy(msg.getVin(), 0, data, 3, msg.getVin().length);
        data[20] = msg.getSoftwareVersion();
        data[21] = msg.getEncryptType();
        data[22] = (byte) ((msg.getData().length & 0xFF00) >> 8);
        data[23] = (byte) ((msg.getData().length & 0xFF));
        System.arraycopy(msg.getData(), 0, data, 24, msg.getData().length);
        data[data.length - 1] = msg.cks();
        out.writeBytes(data);
    }
}

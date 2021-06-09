package terminaltest.protocol.gb17691;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhangyichi
 */
@Slf4j
public class Gb17691MessageDecoder extends ByteToMessageDecoder {




    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        // 查找Header 23 23
        short header = 0;
        byte cmd = 0;
        byte[] vin = new byte[17];
        byte softVer = 0;
        byte encryptType = 0;
        int dataLen = 0;
        int headerIndex = 0;
        if(in.readableBytes() < 2){
            log.info("without header");
            return;
        }
        headerIndex = in.readerIndex();
        header = in.readShort();
        if(header != 0x2323){
            log.info("wrong header");
            in.resetReaderIndex();
            in.readByte();
            return;
        }
        // 找到 Header 23 23
        // 查找命令单元
        if(in.readableBytes() < 22){
            log.info("msg too short");
            in.readerIndex(headerIndex);
            return;
        }
        // 1 byte
        cmd = in.readByte();
        // 17 bytes
        in.readBytes(vin);
        // 1 byte
        softVer = in.readByte();
        // 1 byte
        encryptType = in.readByte();

        // 2 bytes
        dataLen = in.readUnsignedShort();

        if(in.readableBytes() < dataLen){
            log.info("data is shorter than "+dataLen+" : ");
            in.readerIndex(headerIndex);
            byte[] dt = new byte[in.readableBytes()];
            in.readBytes(dt);
            for(byte b : dt){
                String hs = Integer.toHexString(b & 0xFF);
//                System.out.print(hs.length() < 2 ? "0" + hs : hs);
            }
            in.readerIndex(headerIndex);
            return;
        }
        // dataLen bytes
        byte[] data = new byte[dataLen];
        in.readBytes(data);

        if(in.readableBytes() < 1){
            in.readerIndex(headerIndex);
            log.info("without cks code");
            return;
        }
        byte cks = in.readByte();
        Gb17691Message msg = new Gb17691Message(cmd,vin,softVer,encryptType,data);
        if(cks == msg.cks()){
            //System.out.printf("%tF %<tT:%s\n",new Date(),msg.toHex().toUpperCase());
            out.add(msg);
        } else {
            log.info("cks error");
        }
    }
}

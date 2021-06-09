package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerialFragment extends Fragment {

    //长度
    int length;
    //下个字段
    Map<CustomClient,Integer> serialMap;

    public SerialFragment(int length) {
        this.length = length;
        this.serialMap = new ConcurrentHashMap<>();
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        if (serialMap.containsKey(ctx)){
            int serial = serialMap.get(ctx);
            byte[] bytes = new byte[length];
            for (int i=length-1;i>=0;i--,serial = serial>>8){
                bytes[i] = (byte) (serial&0xff);
            }
            return bytes;
        }
        serialMap.put(ctx,0);
        return new byte[length];
    }

    @Override
    public int getLength() {
        return this.length;
    }
}

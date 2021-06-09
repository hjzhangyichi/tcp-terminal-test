package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;

public class RandomFragment extends Fragment {

    //数据波动范围
    long dataRange;

    //数据固定偏移
    long dataOffset;

    public RandomFragment(int length,long dataRange, long dataOffset) {
        this.length = length;
        this.dataRange = dataRange;
        this.dataOffset = dataOffset;
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        long random = (long) (Math.random()*dataRange)+dataOffset;
        byte[] bytes = new byte[length];
        for (int i=length-1;i>=0;i--,random = random>>8){
            bytes[i] = (byte) (random&0xff);
        }
        return bytes;
    }
    @Override
    public int getLength() {
        return this.length;
    }

}

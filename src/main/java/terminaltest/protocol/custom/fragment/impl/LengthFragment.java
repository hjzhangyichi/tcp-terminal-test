package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;

public class LengthFragment extends Fragment {

    //下个字段
    Fragment target;

    public LengthFragment(int length, Fragment target) {
        this.length = length;
        this.target = target;
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        int nextlength = target.getLength();
        byte[] bytes = new byte[length];
        for (int i=length-1;i>=0;i--,nextlength = nextlength>>8){
            bytes[i] = (byte) (nextlength&0xff);
        }
        return bytes;
    }

    @Override
    public int getLength() {
        return this.length;
    }
}

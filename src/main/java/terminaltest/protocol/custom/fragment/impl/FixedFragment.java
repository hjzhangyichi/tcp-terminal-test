package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;

public class FixedFragment extends Fragment {

    //固定的字节数据
    byte[] data;

    public FixedFragment(byte[] data){
        this.length = data.length;
        this.data = data;
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        return data;
    }

    @Override
    public int getLength() {
        return this.length;
    }
}

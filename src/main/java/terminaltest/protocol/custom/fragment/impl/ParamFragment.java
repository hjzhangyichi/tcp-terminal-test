package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;

public class ParamFragment extends Fragment {

    //固定的字节数据
    String paramName;

    public ParamFragment(int length,String paramName) {
        this.length = length;
        this.paramName = paramName;
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        byte[] bytes = new byte[length];
        byte[] param = ctx.getParam(paramName);
        System.arraycopy(param,0,bytes,0,Math.min(length,param.length));
        return bytes;
    }
    @Override
    public int getLength() {
        return this.length;
    }

}

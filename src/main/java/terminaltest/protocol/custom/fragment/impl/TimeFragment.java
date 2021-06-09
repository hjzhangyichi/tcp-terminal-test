package terminaltest.protocol.custom.fragment.impl;

import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;
import terminaltest.unit.MessageTimeUnit;

public class TimeFragment extends Fragment {

    /**
     * 类型,以2021年1月1日12:00:00为例
     * 0,6字节bcd码: 21 01 01 12 00 00
     * 1,6字节无符号数: 15 01 01 0C 00 00
     */
    int type;


    public TimeFragment(int type) {
        this.type = type;
        this.length = 6;
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        switch (type){
            case 0:
                return MessageTimeUnit.nowTime2BCDByte();
            case 1:
                return MessageTimeUnit.nowTime2Byte();
            default:
                return MessageTimeUnit.nowTime2Byte();
        }
    }

    @Override
    public int getLength() {
        return this.length;
    }


}

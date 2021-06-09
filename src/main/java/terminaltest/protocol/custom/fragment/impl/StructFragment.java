package terminaltest.protocol.custom.fragment.impl;


import terminaltest.protocol.custom.CustomClient;
import terminaltest.protocol.custom.fragment.Fragment;
import terminaltest.unit.BytesBuilder;

import java.util.ArrayList;
import java.util.List;

public class StructFragment extends Fragment {

    List<Fragment> fragmentList;

    //校验码类型     0:不校验   1:奇校验   2:偶校验   3按位异或
    int checkCodeType;


    public StructFragment(int type) {
        this.length = 0;
        this.fragmentList = new ArrayList<>();
        this.checkCodeType = type;
    }


    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public void addFragment(Fragment fragment){
        this.fragmentList.add(fragment);
    }

    @Override
    public byte[] doRandom(CustomClient ctx) {
        BytesBuilder builder = new BytesBuilder();
        for (Fragment fragment:fragmentList){
            builder.append(fragment.doRandom(ctx));
        }
        byte[] content = builder.toArray();
        if (checkCodeType == 0){
            return content;
        }
        byte cks = 0;
        for (byte b : content) {
            cks ^= b;
        }
        switch (checkCodeType){
            case 1:
                builder.append((byte) ~cks);
                break;
            case 2:
            case 3:
                builder.append(cks);
                break;
            default:
        }
        return builder.toArray();
    }

    @Override
    public int getLength() {
        int length = 0;
        for (Fragment fragment:fragmentList){
            length += fragment.getLength();
        }
        this.length = length;
        return length;
    }
}

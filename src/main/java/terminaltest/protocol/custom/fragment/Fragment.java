package terminaltest.protocol.custom.fragment;

import terminaltest.protocol.custom.CustomClient;

public abstract class Fragment {
    protected int length;


    public abstract  byte[] doRandom(CustomClient ctx);

    public abstract  int getLength();
}

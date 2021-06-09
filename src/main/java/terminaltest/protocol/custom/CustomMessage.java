package terminaltest.protocol.custom;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangyichi
 */
public class CustomMessage {
    private String type;
    private CustomClient context;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CustomClient getContext() {
        return context;
    }

    public void setContext(CustomClient context) {
        this.context = context;
    }

    public CustomMessage(String type, CustomClient ctx) {
        this.type = type;
        this.context = ctx;
    }

}

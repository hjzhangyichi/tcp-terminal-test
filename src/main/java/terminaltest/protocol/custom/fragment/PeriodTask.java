package terminaltest.protocol.custom.fragment;

import io.netty.util.concurrent.ScheduledFuture;

public class PeriodTask {
    private String type;
    private Integer initialDelay;
    private Integer periodDelay;
    private ScheduledFuture<?> scheduledFuture;

    public PeriodTask(String type, Integer initialDelay, Integer periodDelay) {
        this.type = type;
        this.initialDelay = initialDelay;
        this.periodDelay = periodDelay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Integer initialDelay) {
        this.initialDelay = initialDelay;
    }

    public Integer getPeriodDelay() {
        return periodDelay;
    }

    public void setPeriodDelay(Integer periodDelay) {
        this.periodDelay = periodDelay;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}

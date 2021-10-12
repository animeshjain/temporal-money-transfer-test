package moneytransferapp;

public class SignalContext {
    private String traceId;

    public SignalContext() {
    }

    public SignalContext(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}

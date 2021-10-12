package moneytransferapp;

import org.slf4j.MDC;

public abstract class BaseWorkflow {
    SignalContext latestSignalContext;

    void updateSignalContext(SignalContext signalContext) {
        latestSignalContext = signalContext;
    }

    void insertSignalContextToMDC() {
        MDC.put("signalTraceId", latestSignalContext.getTraceId());
    }

}

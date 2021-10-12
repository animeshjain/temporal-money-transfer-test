package moneytransferapp;

import io.temporal.api.common.v1.Payload;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.DataConverter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;

/**
 * Needs to be set in:
 *  1. WorkflowOptions when instantiating a new workflow stub {@link InitiateMoneyTransfer}
 *  2. WorkflowClientOptions when instantiating a WorkerFactory to create workers {@link MoneyTransferWorker}
 *  3. ActivityOptions when creating the activity stub {@link MoneyTransferWorkflowImpl}
 */
@Slf4j
public class TraceContextPropagator implements ContextPropagator {

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Map<String, Payload> serializeContext(Object context) {
        String traceId = (String) context;
        log.trace("serialize context called, context = {}", traceId);
        if (traceId != null) {
            return Collections.singletonMap("traceId", DataConverter.getDefaultInstance().toPayload(traceId).get());
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Object deserializeContext(Map<String, Payload> context) {
        log.trace("deserialize context called with context {}", context);
        if (context.containsKey("traceId")) {
            return DataConverter.getDefaultInstance().fromPayload(context.get("traceId"), String.class, String.class);
        } else {
            return null;
        }
    }

    @Override
    public Object getCurrentContext() {
        String traceId = MDC.get("traceId");
        log.trace("getting currentContext = {}", traceId);
        return traceId;
    }

    @Override
    public void setCurrentContext(Object context) {
        String traceId = String.valueOf(context);
        log.trace("setCurrentContext = {}", traceId);
        MDC.put("traceId", traceId);
    }
}

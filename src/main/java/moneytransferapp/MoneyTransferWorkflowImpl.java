package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.WorkflowThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.Collections;

@Slf4j
public class MoneyTransferWorkflowImpl extends BaseWorkflow implements MoneyTransferWorkflow {
    // RetryOptions specify how to automatically handle retries when Activities fail.
    private final RetryOptions retryoptions = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(500)
            .build();
    private final ActivityOptions options = ActivityOptions.newBuilder()
            // Timeout options specify when to automatically timeout Activities if the process is taking too long.
            .setStartToCloseTimeout(Duration.ofSeconds(15))
            .setContextPropagators(Collections.singletonList(new TraceContextPropagator()))
            // Optionally provide customized RetryOptions.
            // Temporal retries failures by default, this is simply an example.
            .setRetryOptions(retryoptions)
            .build();
    // ActivityStubs enable calls to methods as if the Activity object is local, but actually perform an RPC.
    private final AccountActivity account = Workflow.newActivityStub(AccountActivity.class, options);

    boolean approve = false;

    // The transfer method is the entry point to the Workflow.
    // Activity method executions can be orchestrated here or from within other Activity methods.
    @Override
    public void transfer(String fromAccountId, String toAccountId, String referenceId, double amount) {
        String traceId = MDC.get("traceId");
        log.info("!! traceId = {}", traceId);
        String workflowId = Workflow.getInfo().getWorkflowId();
        int workflowAttempt = Workflow.getInfo().getAttempt();
        long threadId = Thread.currentThread().getId();

        Workflow.await(() -> approve);

        insertSignalContextToMDC();
        log.info("Signal trace id = {}", MDC.get("signalTraceId"));
        log.info("** WORKFLOW (Calling WITHDRAW) ** [MoneyTransferWorkflowImpl.transfer] workflowId = {}, workflow attempt count = {}, thread = {}", workflowId, workflowAttempt, threadId);
        account.withdraw(fromAccountId, referenceId, amount);
        log.info("** WORKFLOW (Calling DEPOSIT) ** [MoneyTransferWorkflowImpl.transfer] workflowId = {}, workflow attempt count = {}, thread = {}", workflowId, workflowAttempt, threadId);
        account.deposit(toAccountId, referenceId, amount);
        log.info("* ------ WORKFLOW (FIN) ------- * [MoneyTransferWorkflowImpl.transfer] workflowId = {}, workflow attempt count = {}, thread = {}\n", workflowId, workflowAttempt, threadId);
    }

    @Override
    public void approve(SignalContext signalContext) {
        updateSignalContext(signalContext);
        approve = true;
    }
}

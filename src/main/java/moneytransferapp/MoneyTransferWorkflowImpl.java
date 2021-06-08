package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.common.RetryOptions;

import java.time.Duration;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {
    // RetryOptions specify how to automatically handle retries when Activities fail.
    private final RetryOptions retryoptions = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(500)
            .build();
    private final ActivityOptions options = ActivityOptions.newBuilder()
            // Timeout options specify when to automatically timeout Activities if the process is taking too long.
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            // Optionally provide customized RetryOptions.
            // Temporal retries failures by default, this is simply an example.
            .setRetryOptions(retryoptions)
            .build();
    // ActivityStubs enable calls to methods as if the Activity object is local, but actually perform an RPC.
    private final AccountActivity account = Workflow.newActivityStub(AccountActivity.class, options);

    // The transfer method is the entry point to the Workflow.
    // Activity method executions can be orchestrated here or from within other Activity methods.
    @Override
    public void transfer(String fromAccountId, String toAccountId, String referenceId, double amount) {
        String workflowId = Workflow.getInfo().getWorkflowId();
        int workflowAttempt = Workflow.getInfo().getAttempt();
        long threadId = Thread.currentThread().getId();
        System.out.printf("** WORKFLOW (Calling WITHDRAW) ** [MoneyTransferWorkflowImpl.transfer] workflowId = %s, workflow attempt count = %d, thread = %d\n", workflowId, workflowAttempt, threadId);
        account.withdraw(fromAccountId, referenceId, amount);
        System.out.printf("** WORKFLOW (Calling DEPOSIT) ** [MoneyTransferWorkflowImpl.transfer] workflowId = %s, workflow attempt count = %d, thread = %d\n", workflowId, workflowAttempt, threadId);
        account.deposit(toAccountId, referenceId, amount);
        System.out.printf("* ------ WORKFLOW (FIN) ------- * [MoneyTransferWorkflowImpl.transfer] workflowId = %s, workflow attempt count = %d, thread = %d\n\n", workflowId, workflowAttempt, threadId);
    }
}

package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class InitiateMoneyTransfer {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hhmmss");

    static String generateWorkflowId() {
        return "money-transfer-" + simpleDateFormat.format(new Date());
    }

    public static void main(String[] args) throws Exception {
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        MDC.put("traceId", "trace-id-"+UUID.randomUUID().getLeastSignificantBits());
        WorkflowServiceStubsOptions workflowServiceStubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setTarget("100.91.145.58:7233")
                .build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(workflowServiceStubsOptions);
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.

        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setNamespace("animesh-dev")
                .build();
        WorkflowClient client = WorkflowClient.newInstance(service, clientOptions);

        String workflowId = generateWorkflowId();
        log.info("Workflow id for initiate transfer = {}", workflowId);
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                .setContextPropagators(Collections.singletonList(new TraceContextPropagator()))
                .setWorkflowId(workflowId)
                .build();
        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);

        String referenceId = UUID.randomUUID().toString();
        String fromAccount = "001-001";
        String toAccount = "002-002";
        double amount = 18.74;
        // Asynchronous execution. This process will exit after making this call.
        // WorkflowExecution we = WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);
        log.info("Transfer of {} from account {} to account {} is processing", amount, fromAccount, toAccount);
        // System.out.printf("WorkflowID: %s RunID: %s\n", we.getWorkflowId(), we.getRunId());
        workflow.transfer(fromAccount, toAccount, referenceId, amount);
        System.exit(0);
    }

}

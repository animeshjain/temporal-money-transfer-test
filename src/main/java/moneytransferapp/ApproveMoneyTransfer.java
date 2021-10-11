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
public class ApproveMoneyTransfer {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hhmmss");

    static String generateWorkflowId() {
        return "money-transfer-" + simpleDateFormat.format(new Date());
    }

    public static void main(String[] args) throws Exception {
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        MDC.put("traceId", "abcdef");
        WorkflowServiceStubsOptions workflowServiceStubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setTarget("100.91.145.58:7233")
                .build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(workflowServiceStubsOptions);
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.

        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setNamespace("animesh-dev")
                .setContextPropagators(Collections.singletonList(new TraceContextPropagator()))
                .build();
        WorkflowClient client = WorkflowClient.newInstance(service, clientOptions);

        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, "money-transfer-2021-10-11-040845");

        workflow.approve();

        // Asynchronous execution. This process will exit after making this call.
        // WorkflowExecution we = WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);
        // log.info("WorkflowID: {} RunID: {}", we.getWorkflowId(), we.getRunId());

        System.exit(0);
    }

}

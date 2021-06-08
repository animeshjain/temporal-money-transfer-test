package moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.UUID;

public class InitiateMoneyTransfer {

  private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hhmmss");

  static String generateWorkflowId() {
    return "money-transfer-"+simpleDateFormat.format(new Date());
  }

  public static void main(String[] args) throws Exception {

    // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
    WorkflowClient client = WorkflowClient.newInstance(service);

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
        // A WorkflowId prevents this it from having duplicate instances, remove it to duplicate.
        .setWorkflowId(generateWorkflowId())
        .build();
    // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
    MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);

    String referenceId = UUID.randomUUID().toString();
    String fromAccount = "001-001";
    String toAccount = "002-002";
    double amount = 18.74;
    // Asynchronous execution. This process will exit after making this call.
    WorkflowExecution we = WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);
    System.out.printf("Transfer of $%f from account %s to account %s is processing\n", amount, fromAccount, toAccount);
    System.out.printf("WorkflowID: %s RunID: %s\n", we.getWorkflowId(), we.getRunId());
    System.exit(0);
  }

}

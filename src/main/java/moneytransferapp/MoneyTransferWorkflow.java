package moneytransferapp;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

@WorkflowInterface
public interface MoneyTransferWorkflow {

  // The Workflow method is called by the initiator either via code or CLI.
  @WorkflowMethod
  void transfer(String fromAccountId, String toAccountId, String referenceId, double amount);

  @SignalMethod
  void approve(SignalContext signalContext);
}


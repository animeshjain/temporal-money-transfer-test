package moneytransferapp;

import io.temporal.activity.Activity;

public class AccountActivityImpl implements AccountActivity {

  @Override
  public void withdraw(String accountId, String referenceId, double amount) {
    int activityAttempt = Activity.getExecutionContext().getInfo().getAttempt();
    System.out.printf("*ACTIVITY (attempt %d)* [AccountActivityImpl.withdraw] Withdrawing $%f from account %s. ReferenceId: %s\n", activityAttempt, amount, accountId, referenceId);
  }

  @Override
  public void deposit(String accountId, String referenceId, double amount) {
    int activityAttempt = Activity.getExecutionContext().getInfo().getAttempt();

    if (activityAttempt < 2) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        System.out.println("Interrupt Exception");
      }
//      throw new RuntimeException("simulated");
    }

    System.out.printf("*ACTIVITY (attempt %d)* [AccountActivityImpl.deposit] Depositing $%f into account %s. ReferenceId: %s\n", activityAttempt, amount, accountId, referenceId);
  }

}


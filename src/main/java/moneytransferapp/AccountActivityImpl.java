package moneytransferapp;

import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class AccountActivityImpl implements AccountActivity {

  @Override
  public void withdraw(String accountId, String referenceId, double amount) {
    int activityAttempt = Activity.getExecutionContext().getInfo().getAttempt();
    String traceId = MDC.get("traceId");
    log.info("*ACTIVITY (attempt {})* [AccountActivityImpl.withdraw] Withdrawing ${} from account {}. ReferenceId: {}, traceId: {}", activityAttempt, amount, accountId, referenceId, traceId);
  }

  @Override
  public void deposit(String accountId, String referenceId, double amount) {
    int activityAttempt = Activity.getExecutionContext().getInfo().getAttempt();
    String traceId = MDC.get("traceId");

    if (activityAttempt < 2) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        log.error("Interrupt Exception", e);
      }
//      throw new RuntimeException("simulated");
    }

    log.info("*ACTIVITY (attempt {})* [AccountActivityImpl.deposit] Depositing ${} into account {}. ReferenceId: {}, traceId: {}", activityAttempt, amount, accountId, referenceId, traceId);
  }

}


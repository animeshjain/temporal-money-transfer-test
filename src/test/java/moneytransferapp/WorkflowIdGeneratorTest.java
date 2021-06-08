package moneytransferapp;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkflowIdGeneratorTest {

  @Test
  public void generateUniqueWorkflowId() {
    Date date = new Date();
    String formattedDate = new SimpleDateFormat("yyyy-MM-dd-hhmmss").format(date);
    System.out.println(formattedDate);
  }

}

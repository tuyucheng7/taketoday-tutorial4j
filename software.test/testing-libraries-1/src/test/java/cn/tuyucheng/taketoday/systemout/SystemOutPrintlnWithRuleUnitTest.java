package cn.tuyucheng.taketoday.systemout;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SystemOutPrintlnWithRuleUnitTest {

   @Rule
   public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

   @Test
   public void givenSystemOutRule_whenInvokePrintln_thenLogSuccess() {
      print("Hello Tuyucheng Readers!!");

      Assert.assertEquals("Hello Tuyucheng Readers!!", systemOutRule.getLog()
            .trim());

      Assert.assertEquals("Hello Tuyucheng Readers!!\n", systemOutRule.getLogWithNormalizedLineSeparator());
   }

   private void print(String output) {
      System.out.println(output);
   }

}

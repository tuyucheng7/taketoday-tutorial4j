package cn.tuyucheng.taketoday.cucumber.e2e.hooks;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class HookDefinition {

   TestContext testContext;

   public HookDefinition(TestContext testContext) {
      this.testContext = testContext;
   }

   @Before
   public void beforeScenario() {

   }

   @After
   public void AfterSteps() {
      testContext.getWebDriverManager().closeDriver();
   }
}
package cn.tuyucheng.taketoday.cucumber.e2e.context;

import cn.tuyucheng.taketoday.cucumber.e2e.managers.PageObjectManager;
import cn.tuyucheng.taketoday.cucumber.e2e.managers.WebDriverManager;

public class TestContext {

   private final WebDriverManager webDriverManager;
   private final PageObjectManager pageObjectManager;
   public ScenarioContext scenarioContext;

   public TestContext() {
      webDriverManager = new WebDriverManager();
      pageObjectManager = new PageObjectManager(webDriverManager.getDriver());
      scenarioContext = new ScenarioContext();
   }

   public WebDriverManager getWebDriverManager() {
      return webDriverManager;
   }

   public PageObjectManager getPageObjectManager() {
      return pageObjectManager;
   }

   public ScenarioContext getScenarioContext() {
      return scenarioContext;
   }
}
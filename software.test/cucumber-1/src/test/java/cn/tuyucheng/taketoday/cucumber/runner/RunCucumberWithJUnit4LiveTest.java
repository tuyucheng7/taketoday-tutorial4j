package cn.tuyucheng.taketoday.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
      features = "source/testing/resources/cn/tuyucheng/taketoday/cucumber/features",
      glue = {"cn/tuyucheng/taketoday/cucumber/steps", "cn/tuyucheng/taketoday/cucumber/e2e/steps"},
      tags = "@End2EndIntegrationTest"
)
public class RunCucumberWithJUnit4LiveTest {

}
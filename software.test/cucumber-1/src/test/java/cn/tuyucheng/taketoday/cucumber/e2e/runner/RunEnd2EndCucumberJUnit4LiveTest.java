package cn.tuyucheng.taketoday.cucumber.e2e.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
      features = "source/testing/resources/cn/tuyucheng/taketoday/cucumber/features/e2e",
      glue = {"cn/tuyucheng/taketoday/cucumber/e2e/steps"},
      monochrome = true
)
public class RunEnd2EndCucumberJUnit4LiveTest {
}
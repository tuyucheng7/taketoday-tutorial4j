package cn.tuyucheng.taketoday.calculator;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
      features = {"classpath:features/calculator.feature", "classpath:features/calculator-scenario-outline.feature"}
      , plugin = {"pretty", "json:target/reports/json/calculator.json"}
)
public class CalculatorIntegrationTest {
}

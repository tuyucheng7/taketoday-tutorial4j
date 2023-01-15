package cn.tuyucheng.taketoday.shopping;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(features = {"classpath:features/shopping.feature"})
public class ShoppingIntegrationTest {

}

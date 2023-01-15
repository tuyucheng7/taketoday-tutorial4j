package cn.tuyucheng.taketoday.fullstack.restassured.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
	features = "src/test/resources/features",
	glue = {"cn/tuyucheng/taketoday/fullstack/restassured/cucumber/steps"},
	monochrome = true
)
public class RunCucumberLiveTest {

}
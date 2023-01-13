package cn.tuyucheng.taketoday.cucumber.e2e.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("cn/tuyucheng/taketoday/cucumber/features/e2e")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "cn.tuyucheng.taketoday.cucumber.e2e.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@End2EndIntegrationTest")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "com.cucumber.listener.ExtentCucumberFormatter:target/cucumber-reports/report.html")
public class RunEnd2EndCucumberLiveTest {
}
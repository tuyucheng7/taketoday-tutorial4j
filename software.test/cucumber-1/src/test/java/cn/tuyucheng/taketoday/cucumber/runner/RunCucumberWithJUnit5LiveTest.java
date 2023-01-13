package cn.tuyucheng.taketoday.cucumber.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("cn/tuyucheng/taketoday/cucumber/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "cn.tuyucheng.taketoday.cucumber.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@HookTest")
public class RunCucumberWithJUnit5LiveTest {
}
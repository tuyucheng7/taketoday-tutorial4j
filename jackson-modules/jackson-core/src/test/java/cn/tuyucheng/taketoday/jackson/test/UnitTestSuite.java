package cn.tuyucheng.taketoday.jackson.test;

import cn.tuyucheng.taketoday.jackson.sandbox.JacksonPrettyPrintUnitTest;
import cn.tuyucheng.taketoday.jackson.sandbox.SandboxUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ // @formatter:off
	JacksonPrettyPrintUnitTest.class
	, SandboxUnitTest.class
}) // @formatter:on
public class UnitTestSuite {
}
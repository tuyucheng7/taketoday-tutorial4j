package cn.tuyucheng.taketoday.testsuite.suites;

import cn.tuyucheng.taketoday.testsuite.ClassOneUnitTest;
import org.junit.platform.suite.api.SelectMethod;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("My Test Suite")
@SelectMethod(type = ClassOneUnitTest.class, name = "whenFalse_thenFalse")
@SelectMethod("cn.tuyucheng.taketoday.testsuite.subpackage.ClassTwoUnitTest#whenFalse_thenFalse")
public class JUnitSelectMethodsSuite {
   // runs ClassOneUnitTest and ClassTwoUnitTest
}
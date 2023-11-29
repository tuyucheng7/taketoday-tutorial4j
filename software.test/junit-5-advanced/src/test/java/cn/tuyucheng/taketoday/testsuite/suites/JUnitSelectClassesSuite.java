package cn.tuyucheng.taketoday.testsuite.suites;

import cn.tuyucheng.taketoday.testsuite.ClassOneUnitTest;
import cn.tuyucheng.taketoday.testsuite.subpackage.ClassTwoUnitTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("My Test Suite")
@SelectClasses({ClassOneUnitTest.class, ClassTwoUnitTest.class})
public class JUnitSelectClassesSuite {
   // runs ClassOneUnitTest and ClassTwoUnitTest
}
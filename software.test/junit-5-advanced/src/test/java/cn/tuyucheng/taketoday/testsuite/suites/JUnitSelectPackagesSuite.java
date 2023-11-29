package cn.tuyucheng.taketoday.testsuite.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"cn.tuyucheng.taketoday.testsuite", "cn.tuyucheng.taketoday.testsuitetwo"})
public class JUnitSelectPackagesSuite {
   // runs ClassOneUnitTest, ClassTwoUnitTest and ClassThreeUnitTest
}
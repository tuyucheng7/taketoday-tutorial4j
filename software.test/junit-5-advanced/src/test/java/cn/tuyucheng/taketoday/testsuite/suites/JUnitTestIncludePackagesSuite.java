package cn.tuyucheng.taketoday.testsuite.suites;

import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@IncludePackages("cn.tuyucheng.taketoday.testsuite.subpackage")
public class JUnitTestIncludePackagesSuite {
   // runs ClassTwoUnitTest
}
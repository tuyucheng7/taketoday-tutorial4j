package cn.tuyucheng.taketoday.testsuite.suites;

import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@ExcludePackages("cn.tuyucheng.taketoday.testsuite.subpackage")
public class JUnitExcludePackagesSuite {
   // runs ClassOneUnitTest and ClassThreeUnitTest
}
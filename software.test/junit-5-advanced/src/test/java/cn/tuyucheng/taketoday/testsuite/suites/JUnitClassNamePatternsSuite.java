package cn.tuyucheng.taketoday.testsuite.suites;

import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@IncludeClassNamePatterns("cn.tuyucheng.taketoday.testsuite.Class.*UnitTest")
@ExcludeClassNamePatterns("cn.tuyucheng.taketoday.testsuite.ClassTwoUnitTest")
public class JUnitClassNamePatternsSuite {
   // runs ClassOneUnitTest and ClassThreeUnitTest
}
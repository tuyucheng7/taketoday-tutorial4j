package cn.tuyucheng.taketoday.testsuite.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("cn.tuyucheng.taketoday.testsuite")
@IncludeTags("slow")
public class JUnitTestIncludeTagsSuite {
   // runs ClassTwoUnitTest
}
package cn.tuyucheng.taketoday.suites;

import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("cn.tuyucheng.taketoday")
@ExcludePackages("cn.tuyucheng.taketoday.suites")
public class AllUnitTest {

}
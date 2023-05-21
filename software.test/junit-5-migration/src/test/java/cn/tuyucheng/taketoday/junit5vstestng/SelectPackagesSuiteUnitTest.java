package cn.tuyucheng.taketoday.junit5vstestng;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"cn.tuyucheng.taketoday.junit4", "cn.tuyucheng.taketoday.junit5"})
public class SelectPackagesSuiteUnitTest {

}
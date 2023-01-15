package cn.tuyucheng.taketoday.junit5vstestng;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"cn.tuyucheng.taketoday.java.suite.childpackage1", "cn.tuyucheng.taketoday.java.suite.childpackage2"})
public class SelectPackagesSuiteUnitTest {

}
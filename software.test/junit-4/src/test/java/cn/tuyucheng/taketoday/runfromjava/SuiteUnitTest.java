package cn.tuyucheng.taketoday.runfromjava;

import cn.tuyucheng.taketoday.junit.RegistrationUnitTest;
import cn.tuyucheng.taketoday.junit.SignInUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({RegistrationUnitTest.class, SignInUnitTest.class})
public class SuiteUnitTest {
}
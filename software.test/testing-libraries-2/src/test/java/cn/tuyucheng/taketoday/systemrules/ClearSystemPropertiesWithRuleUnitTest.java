package cn.tuyucheng.taketoday.systemrules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;

import static org.junit.Assert.assertNull;

public class ClearSystemPropertiesWithRuleUnitTest {

   @Rule
   public final ClearSystemProperties userNameIsClearedRule = new ClearSystemProperties("user.name");

   @Test
   public void givenClearUsernameProperty_whenGetUserName_thenNull() {
      assertNull(System.getProperty("user.name"));
   }
}
package cn.tuyucheng.taketoday.securitymanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.security.AccessControlException;

public class SecurityManagerUnitTest {

   private static final String TESTING_SECURITY_POLICY = "file:src/test/resources/testing.policy";

   @BeforeEach
   public void setUp() {
      System.setProperty("java.security.policy", TESTING_SECURITY_POLICY);
      System.setSecurityManager(new SecurityManager());
   }

   @AfterEach
   public void tearDown() {
      System.setSecurityManager(null);
   }

   @Test(expected = AccessControlException.class)
   public void whenSecurityManagerIsActive_thenNetworkIsNotAccessibleByDefault() throws IOException {
      new URL("http://www.google.com").openConnection().connect();
   }

   @Test(expected = AccessControlException.class)
   public void whenUnauthorizedClassTriesToAccessProtectedOperation_thenAnExceptionIsThrown() {
      new Service().operation();
   }
}

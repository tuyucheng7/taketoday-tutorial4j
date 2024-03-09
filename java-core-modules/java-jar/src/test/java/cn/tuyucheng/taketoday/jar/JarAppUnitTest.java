package cn.tuyucheng.taketoday.jar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class JarAppUnitTest {

   @Test
   public void findClassTest() {
      Pattern databindPattern = Pattern.compile(".*jackson-databind-(\\d)+\\.(\\d)+\\.(\\d)\\.jar$");

      Assertions.assertTrue(databindPattern.matcher(JarApp.findObjectMapperClass()).matches());
   }
}

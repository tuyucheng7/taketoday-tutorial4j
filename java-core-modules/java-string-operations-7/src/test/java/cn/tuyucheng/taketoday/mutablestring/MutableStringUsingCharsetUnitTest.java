package cn.tuyucheng.taketoday.mutablestring;

import cn.tuyucheng.taketoday.mutablestrings.MutableStringUsingCharset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MutableStringUsingCharsetUnitTest {
   @Test
   @Disabled
   /**
    * This test is disabled as it works well for Java 8 and below
    */
   public void givenCustomCharSet_whenStringUpdated_StringGetsMutated() throws Exception {
      MutableStringUsingCharset ms = new MutableStringUsingCharset();
      String s = ms.createModifiableString("Hello");
      Assertions.assertEquals("Hello", s);
      ms.modifyString();
      Assertions.assertEquals("something", s);
   }

}


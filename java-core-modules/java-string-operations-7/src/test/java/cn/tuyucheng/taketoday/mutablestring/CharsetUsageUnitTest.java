package cn.tuyucheng.taketoday.mutablestring;

import cn.tuyucheng.taketoday.mutablestrings.CharsetUsageExample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharsetUsageUnitTest {

   @Test
   public void givenCharset_whenStringIsEncodedAndDecoded_thenGivesCorrectResult() {
      CharsetUsageExample ch = new CharsetUsageExample();
      String inputString = "hello दुनिया";
      String result = ch.decodeString(ch.encodeString(inputString));
      Assertions.assertEquals(inputString, result);
   }
}

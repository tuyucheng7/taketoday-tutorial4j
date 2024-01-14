package cn.tuyucheng.taketoday.interview;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringInternUnitTest {
   @Test
   public void whenCallingStringIntern_thenStringsInterned() {
      String s1 = "Tuyucheng";
      String s2 = new String("Tuyucheng");
      String s3 = new String("Tuyucheng").intern();

      assertThat(s1 == s2).isFalse();
      assertThat(s1 == s3).isTrue();
   }
}

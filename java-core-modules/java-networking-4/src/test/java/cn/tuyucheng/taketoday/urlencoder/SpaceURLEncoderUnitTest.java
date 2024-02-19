package cn.tuyucheng.taketoday.urlencoder;

import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpaceURLEncoderUnitTest {

   @Test
   void givenSpaceInString_whenUsingDefaultEncoding_thenReturnPlusSign() {
      String originalString = "Welcome to the Tuyucheng Website!";
      String encodedString = URLEncoder.encode(originalString);
      assertEquals("Welcome+to+the+Tuyucheng+Website%21", encodedString);
   }

   @Test
   void givenSpaceInString_whenUsingUTF8Encoding_thenReturnPlusSign() {
      String originalString = "Welcome to the Tuyucheng Website!";
      String encodedString = URLEncoder.encode(originalString, StandardCharsets.UTF_8);
      assertEquals("Welcome+to+the+Tuyucheng+Website%21", encodedString);
   }

   @Test
   void givenSpaceInString_whenUsingDefaultEncodingAndReplace_thenReturnPct20() {
      String originalString = "Welcome to the Tuyucheng Website!";
      String encodedString = URLEncoder.encode(originalString)
            .replace("+", "%20");
      assertEquals("Welcome%20to%20the%20Tuyucheng%20Website%21", encodedString);
   }

   @Test
   void givenSpaceInString_whenUsingDefaultEncodingAndReplaceAll_thenReturnPct20() {
      String originalString = "Welcome to the Tuyucheng Website!";
      String encodedString = URLEncoder.encode(originalString)
            .replaceAll("\\+", "%20");
      assertEquals("Welcome%20to%20the%20Tuyucheng%20Website%21", encodedString);
   }
}
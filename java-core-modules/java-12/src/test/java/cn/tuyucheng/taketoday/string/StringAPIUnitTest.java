package cn.tuyucheng.taketoday.string;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class StringAPIUnitTest {

   @Test
   void whenPositiveArgument_thenReturnIndentedString() {
      String multilineStr = "This is\na multiline\nstring.";
      String outputStr = "   This is\n   a multiline\n   string.\n";

      String postIndent = multilineStr.indent(3);

      assertThat(postIndent, equalTo(outputStr));
   }

   @Test
   void whenNegativeArgument_thenReturnReducedIndentedString() {
      String multilineStr = "   This is\n   a multiline\n   string.";
      String outputStr = " This is\n a multiline\n string.\n";

      String postIndent = multilineStr.indent(-2);

      assertThat(postIndent, equalTo(outputStr));
   }

   @Test
   void whenTransformUsingLambda_thenReturnTransformedString() {
      String result = "hello".transform(input -> input + " world!");

      assertThat(result, equalTo("hello world!"));
   }

   @Test
   void whenTransformUsingParseInt_thenReturnInt() {
      int result = "42".transform(Integer::parseInt);

      assertThat(result, equalTo(42));
   }
}
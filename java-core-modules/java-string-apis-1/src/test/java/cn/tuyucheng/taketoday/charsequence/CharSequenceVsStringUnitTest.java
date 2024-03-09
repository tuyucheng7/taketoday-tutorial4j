package cn.tuyucheng.taketoday.charsequence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharSequenceVsStringUnitTest {

   @Test
   public void givenUsingString_whenInstantiatingString_thenWrong() {
      CharSequence firstString = "bealdung";
      String secondString = "tuyucheng";

      assertNotEquals(firstString, secondString);
   }

   @Test
   public void givenIdenticalCharSequences_whenCastToString_thenEqual() {
      CharSequence charSequence1 = "tuyucheng_1";
      CharSequence charSequence2 = "tuyucheng_2";

      assertTrue(charSequence1.toString().compareTo(charSequence2.toString()) < 0);
   }

   @Test
   public void givenString_whenAppended_thenUnmodified() {
      String test = "a";
      int firstAddressOfTest = System.identityHashCode(test);
      test += "b";
      int secondAddressOfTest = System.identityHashCode(test);

      assertNotEquals(firstAddressOfTest, secondAddressOfTest);
   }

   @Test
   public void givenStringBuilder_whenAppended_thenModified() {
      StringBuilder test = new StringBuilder();
      test.append("a");
      int firstAddressOfTest = System.identityHashCode(test);
      test.append("b");
      int secondAddressOfTest = System.identityHashCode(test);

      assertEquals(firstAddressOfTest, secondAddressOfTest);
   }

   @Test
   public void givenCharSequenceAsString_whenConvertingUsingCasting_thenCorrect() {
      String expected = "tuyucheng";
      CharSequence charSequence = "tuyucheng";
      String explicitCastedString = (String) charSequence;

      assertEquals(expected, charSequence);
      assertEquals(expected, explicitCastedString);
   }

   @Test(expected = ClassCastException.class)
   public void givenCharSequenceAsStringBuiler_whenConvertingUsingCasting_thenThrowException() {
      CharSequence charSequence = new StringBuilder("tuyucheng");
      String castedString = (String) charSequence;
   }

   @Test
   public void givenCharSequence_whenConvertingUsingToString_thenCorrect() {
      String expected = "tuyucheng";
      CharSequence charSequence1 = "tuyucheng";
      CharSequence charSequence2 = new StringBuilder("tuyucheng");

      assertEquals(expected, charSequence1.toString());
      assertEquals(expected, charSequence2.toString());
   }

   @Test
   public void givenCharSequence_whenConvertingUsingValueOf_thenCorrect() {
      String expected = "tuyucheng";
      CharSequence charSequence1 = "tuyucheng";
      CharSequence charSequence2 = new StringBuilder("tuyucheng");
      CharSequence charSequence3 = null;

      assertEquals(expected, String.valueOf(charSequence1));
      assertEquals(expected, String.valueOf(charSequence2));
      assertEquals("null", String.valueOf(charSequence3));
   }

}

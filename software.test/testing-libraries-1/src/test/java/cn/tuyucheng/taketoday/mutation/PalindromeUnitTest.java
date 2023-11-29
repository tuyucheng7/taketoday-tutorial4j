package cn.tuyucheng.taketoday.mutation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PalindromeUnitTest {
   @Test
   public void whenEmptyString_thenAccept() {
      Palindrome palindromeTester = new Palindrome();
      assertTrue(palindromeTester.isPalindrome(""));
   }

   @Test
   public void whenPalindrom_thenAccept() {
      Palindrome palindromeTester = new Palindrome();
      assertTrue(palindromeTester.isPalindrome("noon"));
   }

   @Test
   public void whenNotPalindrom_thenReject() {
      Palindrome palindromeTester = new Palindrome();
      assertFalse(palindromeTester.isPalindrome("box"));
   }

   @Test
   public void whenNearPalindrom_thenReject() {
      Palindrome palindromeTester = new Palindrome();
      assertFalse(palindromeTester.isPalindrome("neon"));
   }
}
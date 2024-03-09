package cn.tuyucheng.taketoday.firstnonrepeatingcharacter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FirstNonRepeatingCharacterUnitTest {

   @Test
   public void testNonRepeatingCharacterBruteForce() {
      FirstNonRepeatingCharacter program = new FirstNonRepeatingCharacter();

      Assertions.assertEquals(program.firstNonRepeatingCharBruteForce("tuyucheng"), Character.valueOf('b'));
      Assertions.assertEquals(program.firstNonRepeatingCharBruteForce("lullaby"), Character.valueOf('u'));
      Assertions.assertEquals(program.firstNonRepeatingCharBruteForce("hello"), Character.valueOf('h'));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForce("mahimahi"));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForce(""));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForce(null));
   }

   @Test
   public void testNonRepeatingCharacterBruteForceNaive() {
      FirstNonRepeatingCharacter program = new FirstNonRepeatingCharacter();
      Assertions.assertEquals(program.firstNonRepeatingCharBruteForceNaive("tuyucheng"), Character.valueOf('b'));
      Assertions.assertEquals(program.firstNonRepeatingCharBruteForceNaive("lullaby"), Character.valueOf('u'));
      Assertions.assertEquals(program.firstNonRepeatingCharBruteForceNaive("hello"), Character.valueOf('h'));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForceNaive("mahimahi"));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForceNaive(""));
      Assertions.assertNull(program.firstNonRepeatingCharBruteForceNaive(null));
   }

   @Test
   public void testNonRepeatingCharacterWithMap() {
      FirstNonRepeatingCharacter program = new FirstNonRepeatingCharacter();
      Assertions.assertEquals(program.firstNonRepeatingCharWithMap("tuyucheng"), Character.valueOf('b'));
      Assertions.assertEquals(program.firstNonRepeatingCharWithMap("lullaby"), Character.valueOf('u'));
      Assertions.assertEquals(program.firstNonRepeatingCharWithMap("hello"), Character.valueOf('h'));
      Assertions.assertNull(program.firstNonRepeatingCharWithMap("mahimahi"));
      Assertions.assertNull(program.firstNonRepeatingCharWithMap(""));
      Assertions.assertNull(program.firstNonRepeatingCharWithMap(null));
   }

   @Test
   public void testNonRepeatingCharacterWithArray() {
      FirstNonRepeatingCharacter program = new FirstNonRepeatingCharacter();
      Assertions.assertEquals(program.firstNonRepeatingCharWithArray("tuyucheng"), Character.valueOf('b'));
      Assertions.assertEquals(program.firstNonRepeatingCharWithArray("lullaby"), Character.valueOf('u'));
      Assertions.assertEquals(program.firstNonRepeatingCharWithArray("hello"), Character.valueOf('h'));
      Assertions.assertNull(program.firstNonRepeatingCharWithArray("mahimahi"));
      Assertions.assertNull(program.firstNonRepeatingCharWithArray(""));
      Assertions.assertNull(program.firstNonRepeatingCharWithArray(null));
   }
}
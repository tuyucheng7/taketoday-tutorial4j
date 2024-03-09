package cn.tuyucheng.taketoday.switchstatement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SwitchStatementUnitTest {
   private SwitchStatement s = new SwitchStatement();


   @Test
   public void whenDog_thenDomesticAnimal() {

      String animal = "DOG";
      Assertions.assertEquals("domestic animal", s.exampleOfSwitch(animal));
   }

   @Test
   public void whenNoBreaks_thenGoThroughBlocks() {
      String animal = "DOG";
      Assertions.assertEquals("unknown animal", s.forgetBreakInSwitch(animal));
   }

   @Test(expected = NullPointerException.class)
   public void whenSwitchAgumentIsNull_thenNullPointerException() {
      String animal = null;
      Assertions.assertEquals("domestic animal", s.exampleOfSwitch(animal));
   }


   @Test
   public void whenCompareStrings_thenByEqual() {
      String animal = new String("DOG");
      Assertions.assertEquals("domestic animal", s.exampleOfSwitch(animal));
   }


}

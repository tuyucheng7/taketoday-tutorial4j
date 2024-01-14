package cn.tuyucheng.taketoday.stringpool;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringPoolUnitTest {

   @Test
   public void whenCreatingConstantStrings_thenTheirAddressesAreEqual() {
      String constantString1 = "Tuyucheng";
      String constantString2 = "Tuyucheng";

      assertThat(constantString1).isSameAs(constantString2);
   }

   @Test
   public void whenCreatingStringsWithTheNewOperator_thenTheirAddressesAreDifferent() {
      String newString1 = new String("Tuyucheng");
      String newString2 = new String("Tuyucheng");

      assertThat(newString1).isNotSameAs(newString2);
   }

   @Test
   public void whenComparingConstantAndNewStrings_thenTheirAddressesAreDifferent() {
      String constantString = "Tuyucheng";
      String newString = new String("Tuyucheng");

      assertThat(constantString).isNotSameAs(newString);
   }

   @Test
   public void whenInterningAStringWithIdenticalValueToAnother_thenTheirAddressesAreEqual() {
      String constantString = "interned Tuyucheng";
      String newString = new String("interned Tuyucheng");

      assertThat(constantString).isNotSameAs(newString);

      String internedString = newString.intern();

      assertThat(constantString).isSameAs(internedString);
   }
}

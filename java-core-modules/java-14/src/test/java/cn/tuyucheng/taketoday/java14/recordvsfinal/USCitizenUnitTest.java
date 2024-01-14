package cn.tuyucheng.taketoday.java14.recordvsfinal;

import org.junit.jupiter.api.Test;

public class USCitizenUnitTest {

   @Test
   public void givenName_whenGetNameAndCode_thenExpectedValuesReturned() {

      String firstName = "Joan";
      String lastName = "Winn";
      String address = "1892 Black Stallion Road";
      int countryCode = 1;

      USCitizen citizen = new USCitizen(firstName, lastName, address);

      assertEquals(firstName + " " + lastName, citizen.getFullName());
      assertEquals(countryCode, USCitizen.getCountryCode());
   }

}
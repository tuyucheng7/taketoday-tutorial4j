package cn.tuyucheng.taketoday.soap.ws.client;

import cn.tuyucheng.taketoday.soap.ws.client.generated.CountryService;
import cn.tuyucheng.taketoday.soap.ws.client.generated.CountryServiceImplService;
import cn.tuyucheng.taketoday.soap.ws.client.generated.Currency;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//Ensure that com.tuyucheng.soap.ws.server.CountryServicePublisher is running before executing this test
public class CountryClientLiveTest {

   private static CountryService countryService;

   @BeforeClass
   public static void setup() {
      CountryServiceImplService service = new CountryServiceImplService();
      countryService = service.getCountryServiceImplPort();
   }

   @Test
   public void givenCountryService_whenCountryIndia_thenCapitalIsNewDelhi() {
      assertEquals("New Delhi", countryService.findByName("India").getCapital());
   }

   @Test
   public void givenCountryService_whenCountryFrance_thenPopulationCorrect() {
      assertEquals(66710000, countryService.findByName("France").getPopulation());
   }

   @Test
   public void givenCountryService_whenCountryUSA_thenCurrencyUSD() {
      Assert.assertEquals(Currency.USD, countryService.findByName("USA").getCurrency());
   }


}

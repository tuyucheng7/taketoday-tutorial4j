package cn.tuyucheng.taketoday.springsoap.client;

import cn.tuyucheng.taketoday.springsoap.client.gen.Currency;
import cn.tuyucheng.taketoday.springsoap.client.gen.GetCountryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Ensure that the server - cn.tuyucheng.taketoday.springsoap.Application - is running before executing this test
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CountryClientConfig.class, loader = AnnotationConfigContextLoader.class)
class CountryClientLiveTest {

   @Autowired
   CountryClient client;

   @Test
   void givenCountryService_whenCountryPoland_thenCapitalIsWarsaw() {
      GetCountryResponse response = client.getCountry("Poland");
      assertEquals("Warsaw", response.getCountry()
            .getCapital());
   }

   @Test
   void givenCountryService_whenCountrySpain_thenCurrencyEUR() {
      GetCountryResponse response = client.getCountry("Spain");
      assertEquals(Currency.EUR, response.getCountry()
            .getCurrency());
   }
}
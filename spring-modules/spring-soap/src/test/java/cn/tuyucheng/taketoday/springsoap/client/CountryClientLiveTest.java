package cn.tuyucheng.taketoday.springsoap.client;

import cn.tuyucheng.taketoday.springsoap.client.gen.Currency;
import cn.tuyucheng.taketoday.springsoap.client.gen.GetCountryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;

// Ensure that the server - cn.tuyucheng.taketoday.springsoap.Application - is running before executing this test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CountryClientConfig.class, loader = AnnotationConfigContextLoader.class)
public class CountryClientLiveTest {

	@Autowired
	CountryClient client;

	@Test
	public void givenCountryService_whenCountryPoland_thenCapitalIsWarsaw() {
		GetCountryResponse response = client.getCountry("Poland");
		assertEquals("Warsaw", response.getCountry()
			.getCapital());
	}

	@Test
	public void givenCountryService_whenCountrySpain_thenCurrencyEUR() {
		GetCountryResponse response = client.getCountry("Spain");
		assertEquals(Currency.EUR, response.getCountry()
			.getCurrency());
	}
}

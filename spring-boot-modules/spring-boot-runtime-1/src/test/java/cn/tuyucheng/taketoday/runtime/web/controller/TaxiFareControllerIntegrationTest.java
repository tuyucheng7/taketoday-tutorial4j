package cn.tuyucheng.taketoday.runtime.web.controller;

import cn.tuyucheng.taketoday.runtime.web.log.app.Application;
import cn.tuyucheng.taketoday.runtime.web.log.data.TaxiRide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TaxiFareControllerIntegrationTest.SecurityConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaxiFareControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Test
	void givenRequest_whenFetchTaxiFareRateCard_thanOK() {
		System.out.println(port);
		String URL = "http://localhost:" + port + "/spring-rest";
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		TaxiRide taxiRide = new TaxiRide(true, 10L);
		String fare = testRestTemplate.postForObject(URL + "/taxifare/calculate/", taxiRide, String.class);

		assertThat(fare, equalTo("200"));
	}

	@Configuration
	static class SecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			System.out.println("security being set");
			http.authorizeRequests()
				.anyRequest().permitAll()
				.and()
				.csrf().disable();
		}
	}
}
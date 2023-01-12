package cn.tuyucheng.taketoday.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonApiLiveTest {

	private final static String URL_PREFIX = "http://localhost:8082/spring-katharsis/users";

	@Test
	public void whenGettingAllUsers_thenCorrect() {
		final Response response = RestAssured.get(URL_PREFIX);
		assertEquals(200, response.statusCode());
		System.out.println(response.asString());
	}

}

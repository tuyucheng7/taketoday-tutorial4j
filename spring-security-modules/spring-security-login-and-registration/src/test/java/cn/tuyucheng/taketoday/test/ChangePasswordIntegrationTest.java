package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.Application;
import cn.tuyucheng.taketoday.persistence.dao.UserRepository;
import cn.tuyucheng.taketoday.persistence.model.User;
import cn.tuyucheng.taketoday.spring.TestDbConfig;
import cn.tuyucheng.taketoday.spring.TestIntegrationConfig;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Disabled("fails on CI")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChangePasswordIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${local.server.port}")
	int port;

	private FormAuthConfig formConfig;
	private String URL;

	//

	@BeforeEach
	public void init() {
		User user = userRepository.findByEmail("test@test.com");
		if (user == null) {
			user = new User();
			user.setFirstName("Test");
			user.setLastName("Test");
			user.setPassword(passwordEncoder.encode("test"));
			user.setEmail("test@test.com");
			user.setEnabled(true);
			userRepository.save(user);
		} else {
			user.setPassword(passwordEncoder.encode("test"));
			userRepository.save(user);
		}

		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
		URL = "/user/updatePassword";
		formConfig = new FormAuthConfig("/login", "username", "password");
	}

	@AfterEach
	public void resetUserPassword() {
		final User user = userRepository.findByEmail("test@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		userRepository.save(user);
	}

	@Test
	public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
		final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

		request.when().get("/console").then().assertThat().statusCode(200).and().body(containsString("home"));
	}

	@Test
	public void givenNotAuthenticatedUser_whenBadPasswordLoggingIn_thenCorrect() {
		final RequestSpecification request = RestAssured.given().auth().form("XXXXXXXX@XXXXXXXXX.com", "XXXXXXXX", formConfig).redirects().follow(false);

		request.when().get("/console").then().statusCode(IsNot.not(200)).body(is(emptyOrNullString()));
	}

	@Test
	public void givenLoggedInUser_whenChangingPassword_thenCorrect() {
		final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

		final Map<String, String> params = new HashMap<>();
		params.put("oldPassword", "test");
		params.put("newPassword", "newTest&12");

		final Response response = request.with().queryParams(params).post(URL);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().asString().contains("Password updated successfully"));
	}

	@Test
	public void givenWrongOldPassword_whenChangingPassword_thenBadRequest() {
		final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

		final Map<String, String> params = new HashMap<>();
		params.put("oldPassword", "abc");
		params.put("newPassword", "newTest&12");

		final Response response = request.with().queryParams(params).post(URL);

		assertEquals(400, response.statusCode());
		assertTrue(response.body().asString().contains("Invalid Old Password"));
	}

	@Test
	public void givenNotAuthenticatedUser_whenChangingPassword_thenRedirect() {
		final Map<String, String> params = new HashMap<>();
		params.put("oldPassword", "abc");
		params.put("newPassword", "xyz");

		final Response response = RestAssured.with().params(params).post(URL);

		assertEquals(302, response.statusCode());
		assertFalse(response.body().asString().contains("Password updated successfully"));
	}

}

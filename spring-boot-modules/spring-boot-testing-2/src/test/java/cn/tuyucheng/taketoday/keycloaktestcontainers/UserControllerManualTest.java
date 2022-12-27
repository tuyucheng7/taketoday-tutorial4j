package cn.tuyucheng.taketoday.keycloaktestcontainers;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Requires Docker running on the machine to run without errors
 * Therefore, skipped from pipeline
 */
class UserControllerManualTest extends KeycloakTestContainers {

	@Test
	void givenAuthenticatedUser_whenGetMe_shouldReturnMyInfo() {

		given().header("Authorization", getJaneDoeBearer())
			.when()
			.get("/users/me")
			.then()
			.body("username", equalTo("janedoe"))
			.body("lastname", equalTo("Doe"))
			.body("firstname", equalTo("Jane"))
			.body("email", equalTo("jane.doe@baeldung.com"));
	}
}
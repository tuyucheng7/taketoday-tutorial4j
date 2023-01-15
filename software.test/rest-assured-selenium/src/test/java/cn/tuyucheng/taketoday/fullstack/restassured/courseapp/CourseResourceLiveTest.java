package cn.tuyucheng.taketoday.fullstack.restassured.courseapp;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

// the SpringBootCourseCurdApplication.java should be running before.
class CourseResourceLiveTest {

	@BeforeEach
	void setUp() {
		baseURI = "http://localhost:8080/instructors/courses";
	}

	@Test
	void whenCallGetRequest_thenShouldReturnCorrectRecord() {
		Response response = given().get("/{id}", 2);
		JsonPath jsonPath = response.jsonPath();

		assertEquals(200, response.getStatusCode());
		assertEquals("Learn Full stack with Spring Boot and React", jsonPath.getString("description"));
	}

	@Test
	void whenCallPutRequest_thenShouldUpdateTheCourse() {
		JsonPath jsonPath = given()
			.get("/{id}", 4)
			.jsonPath();

		assertEquals("Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes", jsonPath.getString("description"));

		String toUpdated = """
			{
			    "username": "tuyucheng",
			    "description": "Learn Full stack with Spring Boot and React"
			}
			""";
		Response response = given()
			.contentType(ContentType.JSON)
			.body(toUpdated)
			.put("/{id}", 4);

		assertEquals(200, response.getStatusCode());

		jsonPath = given()
			.get("/{id}", 4)
			.jsonPath();

		assertEquals("Learn Full stack with Spring Boot and React", jsonPath.getString("description"));
	}

	@Nested
	class DeleteCourseLiveTest {

		@BeforeAll
		static void beforeAll() {
			baseURI = "http://localhost:8080/instructors/courses";
		}

		@BeforeEach
		void setUp() {
			JsonPath responseBody = given()
				.get("/{id}", 4)
				.body()
				.jsonPath();

			assertEquals("Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes", responseBody.getString("description"));
		}

		@Test
		void whenCallDeleteRequest_andGivenCourseIdWith4_thenShouldDeleteCourse() {
			Response response = given().delete("/{id}", 4);

			assertEquals(204, response.getStatusCode());
		}

		@AfterEach
		void tearDown() {
			ResponseBody responseBody = given()
				.get("/{id}", 4)
				.getBody();

			assertThat(responseBody.asString()).isEmpty();
		}
	}
}
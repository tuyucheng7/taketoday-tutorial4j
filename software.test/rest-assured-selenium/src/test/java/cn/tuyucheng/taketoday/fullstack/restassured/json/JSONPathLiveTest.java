package cn.tuyucheng.taketoday.fullstack.restassured.json;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class JSONPathLiveTest {

	@BeforeEach
	void setUp() {
		baseURI = "http://localhost:8080/instructors/courses";
	}

	@Test
	void givenGetCoursesIsCalled_whenConvertedResponseJsonToStringList_thenShouldCorrect() {
		JsonPath jsonPath = given().get("/").jsonPath();

		// Read all the Course as a List of String. Each item in the list represent a course node in the Rest service Response.
		List<String> allDescriptions = jsonPath.getList("description");

		assertThat(allDescriptions).containsExactly(
			"Learn Full stack with Spring Boot and Angular",
			"Learn Full stack with Spring Boot and React",
			"Master Microservices with Spring Boot and Spring Cloud",
			"Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes"
		);
	}

	@Test
	void givenGetCoursesIsCalled_whenConvertedResponseJsonToObjList_thenShouldCorrect() {
		JsonPath jsonPath = given().get("/").jsonPath();

		List<CourseCopy> copyCourses = jsonPath.getList("$", CourseCopy.class);

		assertThat(copyCourses).containsExactly(
			new CourseCopy(1L, "Take-Today", "Learn Full stack with Spring Boot and Angular"),
			new CourseCopy(2L, "Take-Today", "Learn Full stack with Spring Boot and React"),
			new CourseCopy(3L, "Take-Today", "Master Microservices with Spring Boot and Spring Cloud"),
			new CourseCopy(4L, "Take-Today", "Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes")
		);
	}

	@Test
	void givenGetCoursesIsCalled_whenConvertedResponseJsonToObjArray_thenShouldCorrect() {
		JsonPath jsonPath = given().get("/").jsonPath();

		CourseCopy[] copyCourses = jsonPath.getObject("$", CourseCopy[].class);

		assertThat(copyCourses).containsExactly(
			new CourseCopy(1L, "Take-Today", "Learn Full stack with Spring Boot and Angular"),
			new CourseCopy(2L, "Take-Today", "Learn Full stack with Spring Boot and React"),
			new CourseCopy(3L, "Take-Today", "Master Microservices with Spring Boot and Spring Cloud"),
			new CourseCopy(4L, "Take-Today", "Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes")
		);
	}
}
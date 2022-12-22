package cn.tuyucheng.taketoday.springwithgroovy

import cn.tuyucheng.taketoday.springwithgroovy.entity.Todo
import io.restassured.RestAssured
import io.restassured.response.Response
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

// This test requires the cn.tuyucheng.taketoday.springwithgroovy.SpringBootGroovyApplication to be up
// For that, run the maven build - spring-boot:run on the module

class TodoAppLiveTest {
	static API_ROOT = 'http://localhost:8080/todo'
	static readingTodoId
	static writingTodoId

	@BeforeAll
	static void populateDummyData() {
		Todo readingTodo = new Todo(task: 'Reading', isCompleted: false)
		Todo writingTodo = new Todo(task: 'Writing', isCompleted: false)

		final Response readingResponse =
			RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(readingTodo).post(API_ROOT)

		Todo cookingTodoResponse = readingResponse.as Todo.class
		readingTodoId = cookingTodoResponse.getId()

		final Response writingResponse =
			RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(writingTodo).post(API_ROOT)

		Todo writingTodoResponse = writingResponse.as Todo.class
		writingTodoId = writingTodoResponse.getId()
	}

	@Test
	void whenGetAllTodoList_thenOk() {
		final Response response = RestAssured.get(API_ROOT)

		assertEquals HttpStatus.OK.value(), response.getStatusCode()
		assertTrue response.as(List.class).size() > 0
	}

	@Test
	void whenGetTodoById_thenOk() {
		final Response response =
			RestAssured.get("$API_ROOT/$readingTodoId")

		assertEquals HttpStatus.OK.value(), response.getStatusCode()
		Todo todoResponse = response.as Todo.class
		assertEquals readingTodoId, todoResponse.getId()
	}

	@Test
	void whenUpdateTodoById_thenOk() {
		Todo todo = new Todo(id: readingTodoId, isCompleted: true)
		final Response response =
			RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(todo).put(API_ROOT)

		assertEquals HttpStatus.OK.value(), response.getStatusCode()
		Todo todoResponse = response.as Todo.class
		assertTrue todoResponse.getIsCompleted()
	}

	@Test
	void whenDeleteTodoById_thenOk() {
		final Response response =
			RestAssured.given()
				.delete("$API_ROOT/$writingTodoId")

		assertEquals HttpStatus.OK.value(), response.getStatusCode()
	}

	@Test
	void whenSaveTodo_thenOk() {
		Todo todo = new Todo(task: 'Blogging', isCompleted: false)
		final Response response =
			RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(todo).post(API_ROOT)

		assertEquals HttpStatus.OK.value(), response.getStatusCode()
	}
}
package cn.tuyucheng.taketoday.fullstack.restassured.courseapp.handler;

public class CourseExistsException extends RuntimeException {

	public CourseExistsException(String message) {
		super(message);
	}
}
package cn.tuyucheng.taketoday.fullstack.restassured.courseapp.handler;

import cn.tuyucheng.taketoday.fullstack.restassured.courseapp.vo.CourseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseRestControllerAdvice {

	@ExceptionHandler(CourseExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CourseVO> handlerCourseExistsEx(CourseExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CourseVO(ex.getMessage(), 400));
	}
}
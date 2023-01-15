package cn.tuyucheng.taketoday.fullstack.restassured.authentication.resource;

import cn.tuyucheng.taketoday.fullstack.restassured.authentication.service.CoursesHardcodedService;
import cn.tuyucheng.taketoday.fullstack.restassured.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:8081"})
@RestController
public class CourseResource {

	@Autowired
	private CoursesHardcodedService courseManagementService;

	@GetMapping("/instructors/courses")
	public List<Course> getAllCourses() {
		return courseManagementService.findAll();
	}
}
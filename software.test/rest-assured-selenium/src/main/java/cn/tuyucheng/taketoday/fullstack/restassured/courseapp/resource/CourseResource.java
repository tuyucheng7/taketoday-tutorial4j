package cn.tuyucheng.taketoday.fullstack.restassured.courseapp.resource;

import cn.tuyucheng.taketoday.fullstack.restassured.courseapp.service.CoursesHardcodedService;
import cn.tuyucheng.taketoday.fullstack.restassured.courseapp.vo.CourseVO;
import cn.tuyucheng.taketoday.fullstack.restassured.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:8081"})
@RestController
@RequestMapping("/instructors/courses")
public class CourseResource {

	@Autowired
	private CoursesHardcodedService courseManagementService;

	@GetMapping("/")
	public List<Course> getAllCourses() {
		return courseManagementService.findAll();
	}

	@GetMapping("/{id}")
	public Course getCourse(@PathVariable long id) {
		return courseManagementService.findById(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable long id) {

		Course course = courseManagementService.deleteById(id);

		if (course != null) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Course> updateCourse(@PathVariable long id, @RequestBody Course course) {
		course.setId(id);

		Course courseUpdated = courseManagementService.update(course);

		return new ResponseEntity<>(course, HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<CourseVO> createCourse(@RequestBody Course course) {
		courseManagementService.save(course);

		return ResponseEntity.ok().body(CourseVO.buildSuccessVO());
	}
}
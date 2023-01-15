package cn.tuyucheng.taketoday.fullstack.restassured.courseapp.service;

import cn.tuyucheng.taketoday.fullstack.restassured.courseapp.handler.CourseExistsException;
import cn.tuyucheng.taketoday.fullstack.restassured.model.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoursesHardcodedService {

	private static final List<Course> courses = new ArrayList<>();
	private static long idCounter = 0;

	static {
		courses.add(new Course(++idCounter, "Take-Today", "Learn Full stack with Spring Boot and Angular"));
		courses.add(new Course(++idCounter, "Take-Today", "Learn Full stack with Spring Boot and React"));
		courses.add(new Course(++idCounter, "Take-Today", "Master Microservices with Spring Boot and Spring Cloud"));
		courses.add(new Course(++idCounter, "Take-Today", "Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes"));
	}

	public List<Course> findAll() {
		return courses;
	}

	public Course save(Course course) {
		if (courses.stream().anyMatch(x -> x.getId().equals(course.getId())))
			throw new CourseExistsException("Course Id: " + course.getId() + " has been exists.");
		if (course.getId() == -1 || course.getId() == 0) {
			course.setId(++idCounter);
			courses.add(course);
		} else {
			courses.add(course);
		}
		return course;
	}

	public Course update(Course course) {
		if (courses.stream().noneMatch(x -> x.getId().equals(course.getId()))) {
			courses.add(course);
			return course;
		} else {
			courses.stream().filter(x -> x.getId().equals(course.getId()))
				.findFirst()
				.ifPresent(x -> {
					x.setUsername(course.getUsername());
					x.setDescription(course.getDescription());
				});
		}
		return course;
	}

	public Course deleteById(long id) {
		Course course = findById(id);

		if (course == null)
			return null;

		if (courses.remove(course)) {
			return course;
		}

		return null;
	}

	public Course findById(long id) {
		for (Course course : courses) {
			if (course.getId() == id) {
				return course;
			}
		}
		return null;
	}
}
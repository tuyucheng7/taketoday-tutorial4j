package cn.tuyucheng.taketoday.fullstack.restassured.authentication.service;

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
}
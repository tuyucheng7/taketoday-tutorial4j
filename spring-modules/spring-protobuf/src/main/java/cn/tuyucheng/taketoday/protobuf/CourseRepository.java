package cn.tuyucheng.taketoday.protobuf;

import java.util.Map;

public class CourseRepository {

	private final Map<Integer, BaeldungTraining.Course> courses;

	public CourseRepository(Map<Integer, BaeldungTraining.Course> courses) {
		this.courses = courses;
	}

	public BaeldungTraining.Course getCourse(int id) {
		return courses.get(id);
	}
}

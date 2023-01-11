package cn.tuyucheng.taketoday.batchinserts;

import cn.tuyucheng.taketoday.batchinserts.model.School;
import cn.tuyucheng.taketoday.batchinserts.model.Student;

public class TestObjectHelper {

	public static School createSchool(int nameIdentifier) {
		School school = new School();
		school.setName("School" + (nameIdentifier + 1));
		return school;
	}

	public static Student createStudent(School school) {
		Student student = new Student();
		student.setName("Student-" + school.getName());
		student.setSchool(school);
		return student;
	}
}
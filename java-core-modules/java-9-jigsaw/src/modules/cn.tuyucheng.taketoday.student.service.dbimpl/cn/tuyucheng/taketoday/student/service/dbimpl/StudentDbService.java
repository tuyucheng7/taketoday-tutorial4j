package cn.tuyucheng.taketoday.student.service.dbimpl;

import cn.tuyucheng.taketoday.student.model.Student;
import cn.tuyucheng.taketoday.student.service.StudentService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDbService implements StudentService {

	private static final Logger logger = Logger.getLogger("StudentDbService");

	public String create(Student student) {
		logger.log(Level.INFO, "Creating student in DB...");
		return student.getRegistrationId();
	}

	public Student read(String registrationId) {
		logger.log(Level.INFO, "Reading student from DB...");
		return new Student();
	}

	public Student update(Student student) {
		logger.log(Level.INFO, "Updating student in DB...");
		return student;
	}

	public String delete(String registrationId) {
		logger.log(Level.INFO, "Deleting student in DB...");
		return registrationId;
	}
}
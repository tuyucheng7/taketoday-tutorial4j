package cn.tuyucheng.taketoday.student.client;

import cn.tuyucheng.taketoday.student.model.Student;
import cn.tuyucheng.taketoday.student.service.StudentService;
import cn.tuyucheng.taketoday.student.service.dbimpl.StudentDbService;

public class StudentClient {

	public static void main(String[] args) {
		StudentService service = new StudentDbService();
		service.create(new Student());
		service.read("17SS0001");
		service.update(new Student());
		service.delete("17SS0001");
	}
}
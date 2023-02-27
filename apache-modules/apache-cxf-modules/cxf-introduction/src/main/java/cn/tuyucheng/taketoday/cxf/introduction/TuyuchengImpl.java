package cn.tuyucheng.taketoday.cxf.introduction;

import jakarta.jws.WebService;

import java.util.LinkedHashMap;
import java.util.Map;

@WebService(endpointInterface = "cn.tuyucheng.taketoday.cxf.introduction.Tuyucheng")
public class TuyuchengImpl implements Tuyucheng {
	private Map<Integer, Student> students = new LinkedHashMap<>();

	public String hello(String name) {
		return "Hello " + name;
	}

	public String helloStudent(Student student) {
		students.put(students.size() + 1, student);
		return "Hello " + student.getName();
	}

	public Map<Integer, Student> getStudents() {
		return students;
	}
}
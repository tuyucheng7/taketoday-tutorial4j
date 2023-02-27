package cn.tuyucheng.taketoday.cxf.introduction;

import java.util.Map;

import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@WebService
public interface Tuyucheng {
	public String hello(String name);

	public String helloStudent(Student student);

	@XmlJavaTypeAdapter(StudentMapAdapter.class)
	public Map<Integer, Student> getStudents();
}
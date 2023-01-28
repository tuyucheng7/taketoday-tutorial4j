package cn.tuyucheng.taketoday.cxf.introduction;

import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@WebService
public interface Tuyucheng {
	public String hello(String name);

	public String helloStudent(Student student);

	@XmlJavaTypeAdapter(StudentMapAdapter.class)
	public Map<Integer, Student> getStudents();
}
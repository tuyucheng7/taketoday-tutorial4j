package cn.tuyucheng.taketoday.largeresultset.service;

import cn.tuyucheng.taketoday.largeresultset.Student;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public void sendEmailToStudent(Student student) {
		System.out.println("sending email to: " + student);
	}
}
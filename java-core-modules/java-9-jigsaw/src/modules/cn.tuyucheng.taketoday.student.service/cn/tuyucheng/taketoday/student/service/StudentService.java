package cn.tuyucheng.taketoday.student.service;

import cn.tuyucheng.taketoday.student.model.Student;

public interface StudentService {

	String create(Student student);

	Student read(String registrationId);

	Student update(Student student);

	String delete(String registrationId);
}
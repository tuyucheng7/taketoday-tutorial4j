package cn.tuyucheng.taketoday.spring.data.couchbase.service;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Student;

import java.util.List;

public interface StudentService {

    Student findOne(String id);

    List<Student> findAll();

    List<Student> findByFirstName(String firstName);

    List<Student> findByLastName(String lastName);

    void create(Student student);

    void update(Student student);

    void delete(Student student);
}

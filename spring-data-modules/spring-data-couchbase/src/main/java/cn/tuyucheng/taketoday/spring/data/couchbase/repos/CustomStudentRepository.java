package cn.tuyucheng.taketoday.spring.data.couchbase.repos;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Student;

import java.util.List;

public interface CustomStudentRepository {
    List<Student> findByFirstNameStartsWith(String s);
}
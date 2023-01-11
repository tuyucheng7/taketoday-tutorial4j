package cn.tuyucheng.taketoday.spring.data.couchbase.service;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Person;

import java.util.List;

public interface PersonService {

    Person findOne(String id);

    List<Person> findAll();

    List<Person> findByFirstName(String firstName);

    List<Person> findByLastName(String lastName);

    void create(Person person);

    void update(Person person);

    void delete(Person person);
}

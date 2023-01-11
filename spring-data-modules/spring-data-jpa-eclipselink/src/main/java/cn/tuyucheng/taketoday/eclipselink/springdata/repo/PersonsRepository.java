package cn.tuyucheng.taketoday.eclipselink.springdata.repo;

import org.springframework.data.repository.CrudRepository;

import cn.tuyucheng.taketoday.eclipselink.springdata.model.Person;

public interface PersonsRepository extends CrudRepository<Person, Long> {
    Person findByFirstName(String firstName);
}
package cn.tuyucheng.taketoday.spring.data.jpa.listrepositories.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.spring.data.jpa.listrepositories.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
   Person findByFirstName(String firstName);

   List<Person> findByLastName(String lastName);

   Person findOneByFirstName(String firstName);

   List<Person> findOneByLastName(String lastName);
}
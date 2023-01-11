package cn.tuyucheng.taketoday.spring.data.couchbase2b.repos;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, String> {
    List<Person> findByFirstName(String firstName);

    List<Person> findByLastName(String lastName);
}

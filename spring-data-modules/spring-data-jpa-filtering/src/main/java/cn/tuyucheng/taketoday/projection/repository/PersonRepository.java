package cn.tuyucheng.taketoday.projection.repository;

import cn.tuyucheng.taketoday.projection.model.Person;
import cn.tuyucheng.taketoday.projection.view.PersonView;
import cn.tuyucheng.taketoday.projection.view.PersonDto;
import org.springframework.data.repository.Repository;

public interface PersonRepository extends Repository<Person, Long> {
	PersonView findByLastName(String lastName);

	PersonDto findByFirstName(String firstName);

	<T> T findByLastName(String lastName, Class<T> type);
}
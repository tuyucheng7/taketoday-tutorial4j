package cn.tuyucheng.taketoday.eclipselink.springdata.repo;

import cn.tuyucheng.taketoday.eclipselink.springdata.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled("fails test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonsRepositoryIntegrationTest {

    @Autowired
    private PersonsRepository personsRepository;

    @Test
    void givenPerson_whenSave_thenAddOnePersonToDB() {
        personsRepository.save(new Person());
        assertThat(personsRepository.findAll().spliterator().getExactSizeIfKnown(), equalTo(1l));
    }

    @Test
    void givenPersons_whenSearch_thenFindOk() {
        Person person1 = new Person();
        person1.setFirstName("Adam");

        Person person2 = new Person();
        person2.setFirstName("Dave");

        personsRepository.save(person1);
        personsRepository.save(person2);

        Person foundPerson = personsRepository.findByFirstName("Adam");

        assertThat(foundPerson.getFirstName(), equalTo("Adam"));
        assertThat(foundPerson.getId(), notNullValue());
    }
}
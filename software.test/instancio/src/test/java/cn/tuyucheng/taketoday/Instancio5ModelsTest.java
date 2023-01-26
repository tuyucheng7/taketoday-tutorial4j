package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.person.Address;
import cn.tuyucheng.taketoday.person.Gender;
import cn.tuyucheng.taketoday.person.Person;
import cn.tuyucheng.taketoday.person.Phone;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Examples of using {@link org.instancio.Model}.
 * A model is a template for creating objects.
 */
class Instancio5ModelsTest {

    /*
     Define a model and create objects from it based on the template.
     Tests create objects based on the model and apply customisations on top of it if needed
    */
    private final Model<Person> simpsonsModel = Instancio.of(Person.class)
          .set(field(Address::getCity), "Springfield")
          .set(field(Address::getCountry), "US")
          .set(field(Phone::getCountryCode), "+1")
          .generate(field(Phone::getNumber), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
          .toModel();

    @Test
    void createSimpsonsFromModel() {
        Person homer = Instancio.of(simpsonsModel)
              .set(field(Person::getName), "Homer")
              .set(all(Gender.class), Gender.MALE)
              .create();

        Person marge = Instancio.of(simpsonsModel)
              .set(field(Person::getName), "Marge")
              .set(all(Gender.class), Gender.FEMALE)
              .create();

        assertSimpson(homer, "Homer", Gender.MALE);
        assertSimpson(marge, "Marge", Gender.FEMALE);
    }

    @Test
    @DisplayName("Models can be created from other Models")
    void createModelFromModel() {
        Model<Person> simpsonsKid = Instancio.of(simpsonsModel)
              .generate(field("age"), gen -> gen.ints().range(5, 10))
              .toModel();

        Person bart = Instancio.of(simpsonsKid)
              .set(field(Person.class, "name"), "Bart")
              .set(all(Gender.class), Gender.MALE)
              .create();

        assertSimpson(bart, "Bart", Gender.MALE);
        assertThat(bart.getAge()).isBetween(5, 10);
    }

    @Test
    void createCollectionFromModel() {
        final int numberOfFamilyMembers = 5;

        List<Person> simpsons = Instancio.ofList(simpsonsModel)
              .size(numberOfFamilyMembers)
              .create();

        assertThat(simpsons)
              .as("All family members live at the same address")
              .hasSize(numberOfFamilyMembers)
              .allSatisfy(simpson -> assertAddress(simpson.getAddress()));
    }

    private static void assertSimpson(Person simpson, String name, Gender gender) {
        assertThat(simpson.getName()).isEqualTo(name);
        assertThat(simpson.getGender()).isEqualTo(gender);

        // Address is based on the model and was not modified
        assertAddress(simpson.getAddress());
    }

    private static void assertAddress(Address address) {
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getCountry()).isEqualTo("US");
        assertThat(address.getPhoneNumbers())
              .isNotEmpty()
              .allSatisfy(phone -> assertThat(phone.getCountryCode()).isEqualTo("+1"));
    }

}

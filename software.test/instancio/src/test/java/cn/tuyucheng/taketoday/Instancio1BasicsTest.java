package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.person.Address;
import cn.tuyucheng.taketoday.person.Gender;
import cn.tuyucheng.taketoday.person.Person;
import cn.tuyucheng.taketoday.person.Phone;
import cn.tuyucheng.taketoday.person.PhoneWithExtension;
import org.instancio.Instancio;
import org.instancio.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * This class demonstrates basic usage of the API:
 *
 * <ul>
 *   <li>using create() to create objects</li>
 *   <li>using asResult() to get seed value</li>
 *   <li>using set() and generate() to customise objects</li>
 *   <li>using onComplete() callbacks</li>
 *   <li>using subtype() to specify a subclass</li>
 *   <li>using ignore() to ignore fields/classes</li>
 *   <li>using withNullable() to allow null values to be generated</li>
 * </ul>
 */
class Instancio1BasicsTest {

    @Test
    @DisplayName("Create random values; by default, generates positive numbers and non-empty strings")
    void randomValues() {
        int i = Instancio.create(int.class);
        String str = Instancio.create(String.class);
        LocalDateTime ldt = Instancio.create(LocalDateTime.class);

        assertThat(i).isPositive();
        assertThat(str).isNotBlank();
        assertThat(ldt).isNotNull();
    }

    @Test
    @DisplayName("Create a fully-populate object")
    void fullyPopulateObject() {
        Phone phone = Instancio.create(Phone.class);

        assertThat(phone.getCountryCode()).isNotNull();
        assertThat(phone.getNumber()).isNotNull();
    }

    @Test
    @DisplayName("asResult() can be used when seed value is needed, e.g. for logging")
    void asResult() {
        Result<Phone> result = Instancio.of(Phone.class).asResult();
        Phone phone = result.get();

        assertThat(phone).isNotNull();
        System.out.println("Phone was created using seed: " + result.getSeed());
    }

    @Test
    @DisplayName("generate() is for customising randomly generated values of string, numbers, dates, etc")
    void customiseObjectUsingGenerate() {
        Phone phone = Instancio.of(Phone.class)
              .generate(field(Phone::getCountryCode), gen -> gen.oneOf("+1", "+44"))
              .generate(field(Phone::getNumber), gen -> gen.string().digits().length(7))
              .create();

        assertThat(phone.getNumber()).containsOnlyDigits().hasSize(7);
        assertThat(phone.getCountryCode()).isIn("+1", "+44");
    }

    @Test
    @DisplayName("set() is for setting non-random (expected) values")
    void customiseObjectUsingSet() {
        Phone phone = Instancio.of(Phone.class)
              .set(field(Phone::getCountryCode), "+1")
              .create();

        assertThat(phone.getCountryCode()).isEqualTo("+1");
    }

    @Test
    @DisplayName("onComplete() callback is invoked after object has been fully populated")
    void oncComplete() {
        Person person = Instancio.of(Person.class)
              .onComplete(all(Person.class), (Person p) -> {
                  String name = p.getGender() == Gender.FEMALE ? "Marge" : "Homer";
                  p.setName(name);
              })
              .create();

        assertThat(person.getName()).isIn("Marge", "Homer");
    }

    @Test
    @DisplayName("subtype() allows specifying implementations for abstract types, or subclasses for concrete types")
    void usingSubtype() {
        Address address = Instancio.of(Address.class)
              .subtype(all(Phone.class), PhoneWithExtension.class)
              .create();

        assertThat(address.getPhoneNumbers())
              .isNotEmpty()
              .hasOnlyElementsOfType(PhoneWithExtension.class)
              .allSatisfy(phone -> {
                  PhoneWithExtension phoneExt = (PhoneWithExtension) phone;
                  assertThat(phoneExt.getExtension()).isNotBlank();
              });
    }


    @Test
    void usingIgnore() {
        Phone phone = Instancio.of(Phone.class)
              .ignore(field(Phone::getNumber))
              .create();

        assertThat(phone.getNumber()).isNull();
    }

    @Test
    void withNullable() {
        Set<String> results = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            Phone phone = Instancio.of(Phone.class)
                  .withNullable(field(Phone::getNumber)) // approx 1/6 probability of null
                  .create();

            results.add(phone.getNumber());
        }

        assertThat(results).containsNull();
    }
}
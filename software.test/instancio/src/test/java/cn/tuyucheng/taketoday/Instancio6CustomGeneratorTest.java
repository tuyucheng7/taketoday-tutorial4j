package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.person.Address;
import cn.tuyucheng.taketoday.person.Person;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * An example of using a custom generator.
 */
class Instancio6CustomGeneratorTest {

    private static final String ARGENTINA = "Argentina";
    private static final int PHONES_SIZE = 5;

    private static final Generator<Address> ADDRESS_GENERATOR = new Generator<Address>() {
        @Override
        public Address generate(final Random random) {
            Address address = new Address();
            address.setCountry(ARGENTINA);
            return address;
        }

        @Override
        public Hints hints() {
            // The hint telling the engine to populate any field that has a null value
            return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
        }
    };

    @Test
    void usingCustomGenerator() {
        Person person = Instancio.of(Person.class)
              .supply(all(Address.class), ADDRESS_GENERATOR)
              .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(PHONES_SIZE))
              .create();

        Address address = person.getAddress();

        assertThat(address.getPhoneNumbers()).hasSize(PHONES_SIZE);
        assertThat(address.getCountry()).isEqualTo(ARGENTINA);

        // null fields were populated with random values
        assertThat(address.getStreet()).isNotNull();
        assertThat(address.getCity()).isNotNull();
    }

}

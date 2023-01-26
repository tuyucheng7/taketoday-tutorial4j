package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.person.Address;
import cn.tuyucheng.taketoday.person.Person;
import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The Instancio extension for JUnit 5 provides a few additional features:
 *
 * <ul>
 *   <li>Reporting seed value when a test fails</li>
 *   <li>Seed annotation</li>
 *   <li>Settings injection</li>
 *   <li>Parameterized test arguments</li>
 * </ul>
 */
@ExtendWith(InstancioExtension.class)
class Instancio7JUnitExtensionTest {

    private static final int MIN_COLLECTION_SIZE = 5;

    @WithSettings
    private static final Settings settings = Settings.create()
          // generated strings will be prefixed with field names
          .set(Keys.STRING_FIELD_PREFIX_ENABLED, true)
          .set(Keys.COLLECTION_MIN_SIZE, MIN_COLLECTION_SIZE)
          .lock();

    @Test
    @DisplayName("Should use Settings overrides for all strings and collections")
    void withCustomSettings() {
        Person person = Instancio.create(Person.class);

        assertThat(person.getName()).startsWith("name_");
        assertThat(person.getAddress().getCity()).startsWith("city_");
        assertThat(person.getAddress().getPhoneNumbers())
              .hasSizeGreaterThanOrEqualTo(MIN_COLLECTION_SIZE);
    }

    @ParameterizedTest
    @InstancioSource({UUID.class, Address.class})
    @DisplayName("Using Instancio to provide any number of parameterized test arguments")
    void parameterizedExample(UUID uuid, Address address) {
        assertThat(uuid).isNotNull();
        assertThat(address).isNotNull();
    }

    /**
     * When a test that uses {@link InstancioExtension} fails,
     * it reports the seed value that was used to generate the data.
     *
     * <p>Using the {@code @Seed} annotation allows reproducing
     * the data in case of test failure.
     */
    @Seed(12345)
    @Test
    @DisplayName("Reproducing data using the @Seed annotation")
    void shouldGenerateDataBasedOnGivenSeed() {
        Result<Address> result = Instancio.of(Address.class).asResult();

        assertThat(result.get()).isNotNull();
        assertThat(result.getSeed()).isEqualTo(12345);
    }
}
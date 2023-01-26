package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.generics.Item;
import cn.tuyucheng.taketoday.generics.Pair;
import cn.tuyucheng.taketoday.person.Address;
import cn.tuyucheng.taketoday.person.Gender;
import cn.tuyucheng.taketoday.person.Person;
import cn.tuyucheng.taketoday.person.Phone;
import cn.tuyucheng.taketoday.person.PhoneWithExtension;
import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

/**
 * Creating collections
 *
 * <ul>
 *   <li>ofList(), ofSet(), ofMap() - collection APIs</li>
 *   <li>stream() - creating collections via {@link java.util.stream.Stream}</li>
 *   <li>customising collections via generator specs</li>
 * </ul>
 */
class Instancio4CollectionsTest {

    @Test
    void ofList() {
        List<Person> list = Instancio.ofList(Person.class).create();
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("collections can be returned as a 'Result' object containing the result and the seed")
    void asResult() {
        Result<List<Person>> result = Instancio.ofList(Person.class).size(3).asResult();
        List<Person> list = result.get();

        assertThat(list).isNotEmpty();
        System.out.println("Seed to create the list: " + result.getSeed());
    }

    @Test
    void ofSet() {
        Set<Phone> set = Instancio.ofSet(Phone.class).size(5).create();
        assertThat(set).hasSize(5);
    }

    @Test
    @DisplayName("Using root() selector to specify the map type")
    void ofMapWithSubtype() {
        Map<String, Address> map = Instancio.ofMap(String.class, Address.class)
              .size(5)
              .subtype(root(), TreeMap.class)
              .create();

        assertThat(map).isInstanceOf(TreeMap.class).hasSize(5);
    }

    @Test
    @DisplayName("Collections can be customised using the builder API")
    void ofListWithBuilderAPI() {
        List<Person> persons = Instancio.ofList(Person.class).size(10)
              .set(field(Person::getName), "Homer Simpson")
              .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
              .withNullable(all(Gender.class))
              .subtype(all(Phone.class), PhoneWithExtension.class)
              .create();

        assertThat(persons).allSatisfy(person -> {
            assertThat(person.getName()).isEqualTo("Homer Simpson");
            assertThat(person.getAge()).isBetween(40, 50);
            assertThat(person.getAddress().getPhoneNumbers()).allSatisfy(phone -> {
                assertThat(phone).isInstanceOf(PhoneWithExtension.class);
            });
        });
    }

    //
    // Stream API: alternative to the collection API
    // Note: stream API returns infinite streams, therefore limit() must be called
    //

    @Test
    void streamClass() {
        final Set<Phone> phones = Instancio.stream(Phone.class)
              .limit(5)
              .collect(Collectors.toSet());

        assertThat(phones).isNotEmpty();
    }

    @Test
    @DisplayName("Streams can be customised using the builder API")
    void streamTypeToken() {
        final List<Pair<Integer, Item<String>>> list = Instancio.of(new TypeToken<Pair<Integer, Item<String>>>() {
              })
              .generate(allStrings(), gen -> gen.string().digits().length(3))
              .generate(allInts(), gen -> gen.ints().range(1, 9))
              .stream()
              .limit(5)
              .collect(Collectors.toList());

        assertThat(list).isNotEmpty().allSatisfy(pair -> {
            assertThat(pair.getLeft()).isBetween(1, 9);

            final Item<String> right = pair.getRight();
            assertThat(right.getValue()).containsOnlyDigits().hasSize(3);
        });
    }


    //
    // Customising collections via generator specs
    //

    @Test
    @DisplayName("Setting up a map with expected entries")
    void mapWithExpectedEntries() {
        Map<String, Integer> map = Instancio.ofMap(String.class, Integer.class)
              .generate(root(), gen -> gen.map()
                    .minSize(5)
                    .with("bar", 100)
                    .with("baz", 200))
              .create();

        assertThat(map)
              .hasSizeGreaterThan(5)
              .containsEntry("bar", 100)
              .containsEntry("baz", 200);
    }

    @Test
    @DisplayName("Setting up a map with expected keys")
    void usingMapGeneratorSpec() {
        Map<String, Address> map = Instancio.ofMap(String.class, Address.class)
              .generate(all(Map.class), gen -> gen.map()
                    .subtype(TreeMap.class)
                    .size(5)
                    .withKeys("foo", "bar"))
              .create();

        assertThat(map).isInstanceOf(TreeMap.class)
              .hasSize(5)
              .containsKeys("foo", "bar");
    }

    @Test
    @DisplayName("Using list generator spec to customise the list with expected elements")
    void usingListGeneratorSpec() {
        Phone expectedPhone1 = new Phone("+33", "12345678");
        Phone expectedPhone2 = new Phone("+49", "345678901");

        List<Phone> list = Instancio.ofList(Phone.class)
              .generate(root(), gen -> gen.collection()
                    .minSize(100)
                    .nullableElements()
                    .with(expectedPhone1, expectedPhone2))
              .create();

        assertThat(list)
              .hasSizeGreaterThan(100)
              .containsNull()
              .contains(expectedPhone1, expectedPhone2);
    }
}

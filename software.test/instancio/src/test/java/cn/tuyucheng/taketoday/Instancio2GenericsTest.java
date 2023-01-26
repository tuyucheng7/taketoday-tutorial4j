package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.generics.Item;
import cn.tuyucheng.taketoday.generics.Pair;
import cn.tuyucheng.taketoday.generics.Triplet;
import cn.tuyucheng.taketoday.person.Address;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;

/**
 * Demonstrates how to use {@link TypeToken}
 *
 * <ul>
 *   <li>create instances of generic classes</li>
 *   <li>customise values</li>
 *   <li>complex types with nested generics</li>
 *   <li>using "create(Class).withTypeParameters()" method</li>
 * </ul>
 */
class Instancio2GenericsTest {

    @Test
    @DisplayName("Simplest case: class Item<T> containing a single value")
    void createItem() {
        Item<String> item = Instancio.create(new TypeToken<Item<String>>() {
        });

        assertThat(item.getValue()).isNotBlank();
    }

    @Test
    @DisplayName("Type token API allows customising generated values")
    void createCustomisedItem() {
        Pair<String, Long> pair = Instancio.of(new TypeToken<Pair<String, Long>>() {
              })
              .generate(allStrings(), gen -> gen.oneOf("foo", "bar"))
              .generate(allLongs(), gen -> gen.longs().range(5L, 10L))
              .create();

        assertThat(pair.getLeft()).isIn("foo", "bar");
        assertThat(pair.getRight()).isBetween(5L, 10L);
    }

    @Test
    @DisplayName("Create an instance of Triplet<L, M, R>")
    void createTriplet() {
        Triplet<String, UUID, Long> triplet = Instancio.create(new TypeToken<Triplet<String, UUID, Long>>() {
        });

        assertThat(triplet.getLeft()).isNotBlank();
        assertThat(triplet.getMiddle()).isNotNull();
        assertThat(triplet.getRight()).isNotNull();
    }

    @Test
    @DisplayName("TypeToken can be used to create collections; there's also a separate API for creating collections")
    void createCollections() {
        List<String> list = Instancio.create(new TypeToken<List<String>>() {
        });
        Map<UUID, Address> map = Instancio.create(new TypeToken<Map<UUID, Address>>() {
        });

        assertThat(list).isNotEmpty().doesNotContainNull();
        assertThat(map).isNotEmpty();
    }

    @Test
    @DisplayName("Using type token to create more complex generic objects")
    void moreComplexGenerics() {
        List<Triplet<Integer, LocalDate, Item<String>>> list = Instancio.create(
              new TypeToken<List<Triplet<Integer, LocalDate, Item<String>>>>() {
              });

        assertThat(list)
              .isNotEmpty()
              .allSatisfy(triplet -> {
                  assertThat(triplet.getLeft()).isInstanceOf(Integer.class);
                  assertThat(triplet.getMiddle()).isInstanceOf(LocalDate.class);
                  assertThat(triplet.getRight())
                        .isInstanceOf(Item.class)
                        .satisfies(item -> assertThat(item.getValue()).isNotBlank());
              });

        // Sample output
        list.forEach(System.out::println);
    }


    /**
     * Alternative way to create generic objects is using 'withTypeParameters'.
     * However, this approach generates an "unchecked assignment" warning.
     */
    @Test
    @SuppressWarnings("unchecked")
    void createPersonMapUsingTypeParameters() {
        Map<UUID, Address> map = Instancio.of(Map.class)
              .withTypeParameters(UUID.class, Address.class)
              .create();

        assertThat(map).isNotEmpty();
    }

}

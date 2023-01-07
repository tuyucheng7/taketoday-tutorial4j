package cn.tuyucheng.taketoday.collections.comparation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ListVsMapUnitTest {

    @Test
    void givenList_whenIteratingTroughValues_thenEachValueIsPresent() {
        List<String> list = new ArrayList<>();
        list.add("Daniel");
        list.add("Marko");
        for (String name : list) {
            assertThat(name).isIn(list);
        }
        assertThat(list).containsExactly("Daniel", "Marko");
    }

    @Test
    void givenMap_whenIteratingTroughValues_thenEachValueIsPresent() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "Daniel");
        map.put(2, "Marko");
        for (String name : map.values()) {
            assertThat(name).isIn(map.values());
        }
        assertThat(map.values()).containsExactlyInAnyOrder("Daniel", "Marko");
    }

}

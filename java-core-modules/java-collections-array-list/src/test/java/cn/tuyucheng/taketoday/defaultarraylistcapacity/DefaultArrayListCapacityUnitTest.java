package cn.tuyucheng.taketoday.defaultarraylistcapacity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultArrayListCapacityUnitTest {

    @Test
    void givenEmptyArrayList_whenGetDefaultCapacity_thenReturnZero() throws Exception {
        ArrayList<Integer> myList = new ArrayList<>();
        int defaultCapacity = DefaultArrayListCapacity.getDefaultCapacity(myList);

        assertEquals(0, defaultCapacity);
    }

    @Test
    void givenEmptyArrayList_whenAddItemAndGetDefaultCapacity_thenReturn10() throws Exception {
        ArrayList<String> myList = new ArrayList<>();
        myList.add("ITEM 1");

        int defaultCapacity = DefaultArrayListCapacity.getDefaultCapacity(myList);

        assertEquals(10, defaultCapacity);
    }
}
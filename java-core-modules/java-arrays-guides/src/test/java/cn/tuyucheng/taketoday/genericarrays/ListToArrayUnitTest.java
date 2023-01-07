package cn.tuyucheng.taketoday.genericarrays;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListToArrayUnitTest {

    @Test
    void givenListOfItems_whenToArray_thenReturnArrayOfItems() {
        List<String> items = new LinkedList<>();
        items.add("first item");
        items.add("second item");

        String[] itemsAsArray = items.toArray(new String[0]);

        assertEquals("first item", itemsAsArray[0]);
        assertEquals("second item", itemsAsArray[1]);
    }
}
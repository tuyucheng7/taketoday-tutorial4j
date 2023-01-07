package cn.tuyucheng.taketoday.arrays;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JavaArraysToStringUnitTest {

    @Test
    void givenInstanceOfArray_whenTryingToConvertToString_thenNameOfClassIsShown() {
        Object[] arrayOfObjects = {"John", 2, true};
        assertTrue(arrayOfObjects.toString().startsWith("[Ljava.lang.Object;"));
    }

    @Test
    void givenInstanceOfArray_whenUsingArraysToStringToConvert_thenValueOfObjectsAreShown() {
        Object[] arrayOfObjects = {"John", 2, true};
        assertEquals(Arrays.toString(arrayOfObjects), "[John, 2, true]");
    }

    @Test
    void givenInstanceOfDeepArray_whenUsingArraysDeepToStringToConvert_thenValueOfInnerObjectsAreShown() {
        Object[] innerArray = {"We", "Are", "Inside"};
        Object[] arrayOfObjects = {"John", 2, innerArray};
        assertEquals(Arrays.deepToString(arrayOfObjects), "[John, 2, [We, Are, Inside]]");
    }

    @Test
    void givenInstanceOfDeepArray_whenUsingStreamsToConvert_thenValueOfObjectsAreShown() {
        Object[] arrayOfObjects = {"John", 2, true};
        List<String> listOfString = Stream.of(arrayOfObjects)
              .map(Object::toString)
              .collect(Collectors.toList());
        assertEquals(listOfString.toString(), "[John, 2, true]");
    }
}
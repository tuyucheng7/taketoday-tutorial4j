package cn.tuyucheng.taketoday.array.conversions;

import com.google.common.collect.Iterators;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamArrayConversionUnitTest {

    private String[] stringArray = new String[]{"tuyucheng", "convert", "to", "string", "array"};
    private Integer[] integerArray = new Integer[]{1, 2, 3, 4, 5, 6, 7};
    private int[] intPrimitiveArray = new int[]{1, 2, 3, 4, 5, 6, 7};

    @Test
    void givenStringStream_thenConvertToStringArrayUsingFunctionalInterface() {
        Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
        assertArrayEquals(stringArray, StreamArrayConversion.stringStreamToStringArrayUsingFunctionalInterface(stringStream));
    }

    @Test
    void givenStringStream_thenConvertToStringArrayUsingMethodReference() {
        Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
        assertArrayEquals(stringArray, StreamArrayConversion.stringStreamToStringArrayUsingMethodReference(stringStream));
    }

    @Test
    void givenStringStream_thenConvertToStringArrayUsingLambda() {
        Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
        assertArrayEquals(stringArray, StreamArrayConversion.stringStreamToStringArrayUsingLambda(stringStream));
    }

    @Test
    void givenIntegerStream_thenConvertToIntegerArray() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7);
        assertArrayEquals(integerArray, StreamArrayConversion.integerStreamToIntegerArray(integerStream));
    }

    @Test
    void givenIntStream_thenConvertToIntegerArray() {
        Stream<Integer> integerStream = IntStream.rangeClosed(1, 7).boxed();
        assertArrayEquals(intPrimitiveArray, StreamArrayConversion.intStreamToPrimitiveIntArray(integerStream));
    }

    @Test
    void givenStringArray_whenConvertedTwoWays_thenConvertedStreamsAreEqual() {
        assertTrue(Iterators
              .elementsEqual(StreamArrayConversion.stringArrayToStreamUsingArraysStream(stringArray).iterator(),
                    StreamArrayConversion.stringArrayToStreamUsingStreamOf(stringArray).iterator()));
    }

    @Test
    void givenPrimitiveArray_whenConvertedTwoWays_thenConvertedStreamsAreNotEqual() {
        assertFalse(Iterators.elementsEqual(
              StreamArrayConversion.primitiveIntArrayToStreamUsingArraysStream(intPrimitiveArray).iterator(),
              StreamArrayConversion.primitiveIntArrayToStreamUsingStreamOf(intPrimitiveArray).iterator()));
    }
}
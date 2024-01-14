package cn.tuyucheng.taketoday.array.conversions;

import com.google.common.collect.Iterators;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.intStreamToPrimitiveIntArray;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.integerStreamToIntegerArray;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.primitiveIntArrayToStreamUsingArraysStream;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.primitiveIntArrayToStreamUsingStreamOf;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.stringArrayToStreamUsingArraysStream;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.stringArrayToStreamUsingStreamOf;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.stringStreamToStringArrayUsingFunctionalInterface;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.stringStreamToStringArrayUsingLambda;
import static cn.tuyucheng.taketoday.array.conversions.StreamArrayConversion.stringStreamToStringArrayUsingMethodReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamArrayConversionUnitTest {

   private String[] stringArray = new String[]{"tuyucheng", "convert", "to", "string", "array"};
   private Integer[] integerArray = new Integer[]{1, 2, 3, 4, 5, 6, 7};
   private int[] intPrimitiveArray = new int[]{1, 2, 3, 4, 5, 6, 7};

   @Test
   public void givenStringStream_thenConvertToStringArrayUsingFunctionalInterface() {
      Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
      assertArrayEquals(stringArray, stringStreamToStringArrayUsingFunctionalInterface(stringStream));
   }

   @Test
   public void givenStringStream_thenConvertToStringArrayUsingMethodReference() {
      Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
      assertArrayEquals(stringArray, stringStreamToStringArrayUsingMethodReference(stringStream));
   }

   @Test
   public void givenStringStream_thenConvertToStringArrayUsingLambda() {
      Stream<String> stringStream = Stream.of("tuyucheng", "convert", "to", "string", "array");
      assertArrayEquals(stringArray, stringStreamToStringArrayUsingLambda(stringStream));
   }

   @Test
   public void givenIntegerStream_thenConvertToIntegerArray() {
      Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7);
      assertArrayEquals(integerArray, integerStreamToIntegerArray(integerStream));
   }

   @Test
   public void givenIntStream_thenConvertToIntegerArray() {
      Stream<Integer> integerStream = IntStream.rangeClosed(1, 7).boxed();
      assertArrayEquals(intPrimitiveArray, intStreamToPrimitiveIntArray(integerStream));
   }

   @Test
   public void givenStringArray_whenConvertedTwoWays_thenConvertedStreamsAreEqual() {
      assertTrue(Iterators
            .elementsEqual(stringArrayToStreamUsingArraysStream(stringArray).iterator(),
                  stringArrayToStreamUsingStreamOf(stringArray).iterator()));
   }

   @Test
   public void givenPrimitiveArray_whenConvertedTwoWays_thenConvertedStreamsAreNotEqual() {
      assertFalse(Iterators.elementsEqual(
            primitiveIntArrayToStreamUsingArraysStream(intPrimitiveArray).iterator(),
            primitiveIntArrayToStreamUsingStreamOf(intPrimitiveArray).iterator()));
   }
}

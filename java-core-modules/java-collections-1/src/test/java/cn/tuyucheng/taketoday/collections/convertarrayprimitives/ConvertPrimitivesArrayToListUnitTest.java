package cn.tuyucheng.taketoday.collections.convertarrayprimitives;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertPrimitivesArrayToListUnitTest {

   @Test
   public void givenArrayWithPrimitives_whenIterativeConvert_thenArrayGetsConverted() {
      Assertions.assertEquals(Arrays.asList(1, 2, 3, 4), ConvertPrimitivesArrayToList.iterateConvert(new int[]{1, 2, 3, 4}));
   }

   @Test
   public void givenArrayWithPrimitives_whenStreamConvert_thenArrayGetsConverted() {
      assertEquals(Arrays.asList(1, 2, 3, 4), ConvertPrimitivesArrayToList.streamConvert(new int[]{1, 2, 3, 4}));
   }

   @Test
   public void givenArrayWithPrimitives_whenIntStreamConvert_thenArrayGetsConverted() {
      assertEquals(Arrays.asList(1, 2, 3, 4), ConvertPrimitivesArrayToList.streamConvertIntStream(new int[]{1, 2, 3, 4}));
   }

   @Test
   public void givenArrayWithPrimitives_whenGuavaConvert_thenArrayGetsConverted() {
      assertEquals(Arrays.asList(1, 2, 3, 4), ConvertPrimitivesArrayToList.guavaConvert(new int[]{1, 2, 3, 4}));
   }

   @Test
   public void givenArrayWithPrimitives_whenApacheCommonConvert_thenArrayGetsConverted() {
      assertEquals(Arrays.asList(1, 2, 3, 4), ConvertPrimitivesArrayToList.apacheCommonConvert(new int[]{1, 2, 3, 4}));
   }
}

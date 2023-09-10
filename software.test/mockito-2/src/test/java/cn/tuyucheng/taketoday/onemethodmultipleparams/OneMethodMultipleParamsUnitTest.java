package cn.tuyucheng.taketoday.onemethodmultipleparams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OneMethodMultipleParamsUnitTest {

   @Mock
   ExampleService exampleService;

   @Test
   void givenAMethod_whenStubbingForMultipleArguments_thenExpectDifferentResults() {
      when(exampleService.getValue(10)).thenReturn(100);
      when(exampleService.getValue(20)).thenReturn(200);
      when(exampleService.getValue(30)).thenReturn(300);

      assertEquals(100, exampleService.getValue(10));
      assertEquals(200, exampleService.getValue(20));
      assertEquals(300, exampleService.getValue(30));
   }

   @Test
   void givenAMethod_whenUsingThenAnswer_thenExpectDifferentResults() {
      when(exampleService.getValue(anyInt())).thenAnswer(invocation -> {
         int argument = (int) invocation.getArguments()[0];
         return switch (argument) {
            case 25 -> 125;
            case 50 -> 150;
            case 75 -> 175;
            default -> 0;
         };
      });
      assertEquals(125, exampleService.getValue(25));
      assertEquals(150, exampleService.getValue(50));
      assertEquals(175, exampleService.getValue(75));
   }

   @Test
   void givenAMethod_whenUsingConsecutiveStubbing_thenExpectResultsInOrder() {
      when(exampleService.getValue(anyInt())).thenReturn(9, 18, 27);
      assertEquals(9, exampleService.getValue(1));
      assertEquals(18, exampleService.getValue(1));
      assertEquals(27, exampleService.getValue(1));
      assertEquals(27, exampleService.getValue(1));
   }
}
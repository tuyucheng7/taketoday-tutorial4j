package cn.tuyucheng.taketoday.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodParameterInjectionUnitTest {

   @Test
   void whenMockInjectedViaArgumentParameters_thenSetupCorrectly(@Mock Function<String, String> mockFunction) {
      when(mockFunction.apply("tu")).thenReturn("yucheng");
      assertEquals("yucheng", mockFunction.apply("tu"));
   }

   @Test
   void whenArgumentCaptorInjectedViaArgumentParameters_thenSetupCorrectly(@Mock Function<String, String> mockFunction, @Captor ArgumentCaptor<String> captor) {
      mockFunction.apply("tuyucheng");
      verify(mockFunction).apply(captor.capture());
      assertEquals("tuyucheng", captor.getValue());
   }

   @RepeatedTest(2)
   void whenInjectedInRepeatedTest_thenSetupCorrectly(@Mock Function<String, String> mockFunction, @Captor ArgumentCaptor<String> captor) {
      mockFunction.apply("tuyucheng");
      verify(mockFunction).apply(captor.capture());
      assertEquals("tuyucheng", captor.getValue());
   }

   @ParameterizedTest
   @ValueSource(strings = {"", "tu", "yucheng"})
   void whenInjectedInParameterizedTest_thenSetupCorrectly(String input, @Mock Function<String, String> mockFunction, @Captor ArgumentCaptor<String> captor) {
      when(mockFunction.apply(input)).thenReturn("tuyucheng");
      assertEquals("tuyucheng", mockFunction.apply(input));
      verify(mockFunction).apply(captor.capture());
      assertEquals(input, captor.getValue());
   }
}
package cn.tuyucheng.taketoday.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConstructorInjectionUnitTest {

   Function<String, String> function;

   ArgumentCaptor<String> captor;

   public ConstructorInjectionUnitTest(@Mock Function<String, String> function, @Captor ArgumentCaptor<String> captor) {
      this.function = function;
      this.captor = captor;
   }

   @Test
   void whenInjectedViaArgumentParameters_thenSetupCorrectly() {
      when(function.apply("tu")).thenReturn("yucheng");
      assertEquals("yucheng", function.apply("tu"));
      verify(function).apply(captor.capture());
      assertEquals("tu", captor.getValue());
   }
}
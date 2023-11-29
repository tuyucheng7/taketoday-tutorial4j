package cn.tuyucheng.taketoday.mockito.whenvsdomethods;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

public class WhenVsDoMethodsUnitTest {

   @Mock
   private Employee employee;

   @BeforeEach
   void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   void givenNonVoidMethod_callingWhen_shouldConfigureBehavior() {
      // given
      when(employee.greet()).thenReturn("Hello");

      // when
      String greeting = employee.greet();

      // then
      assertThat(greeting, is("Hello"));
   }

   @Test
   void givenNonVoidMethod_callingDoReturn_shouldConfigureBehavior() {
      // given
      doReturn("Hello").when(employee).greet();

      // when
      String greeting = employee.greet();

      // then
      assertThat(greeting, is("Hello"));
   }

   @Test
   void givenVoidMethod_callingDoThrow_shouldConfigureBehavior() {
      // given
      doThrow(new IAmOnHolidayException()).when(employee).work(DayOfWeek.SUNDAY);

      // when
      Executable workCall = () -> employee.work(DayOfWeek.SUNDAY);

      // then
      assertThrows(IAmOnHolidayException.class, workCall);
   }

   @Test
   void givenNonVoidMethod_callingGiven_shouldConfigureBehavior() {
      // given
      given(employee.greet()).willReturn("Hello");

      // when
      String greeting = employee.greet();

      // then
      assertThat(greeting, is("Hello"));
   }

   @Test
   void givenVoidMethod_callingWillThrow_shouldConfigureBehavior() {
      // given
      willThrow(new IAmOnHolidayException()).given(employee).work(DayOfWeek.SUNDAY);

      // when
      Executable workCall = () -> employee.work(DayOfWeek.SUNDAY);

      // then
      assertThrows(IAmOnHolidayException.class, workCall);
   }
}
package cn.tuyucheng.taketoday.mockito.java8;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobServiceUnitTest {

   @Mock
   private JobService jobService;

   @Test
   public void givenDefaultMethod_whenCallRealMethod_thenNoExceptionIsRaised() {
      Person person = new Person();

      when(jobService.findCurrentJobPosition(person)).thenReturn(Optional.of(new JobPosition()));
      doCallRealMethod().when(jobService).assignJobPosition(Mockito.any(Person.class), Mockito.any(JobPosition.class));

      assertFalse(jobService.assignJobPosition(person, new JobPosition()));
   }

   @Test
   public void givenReturnIsOfTypeOptional_whenDefaultValueIsReturned_thenValueIsEmpty() {
      Person person = new Person();

      when(jobService.findCurrentJobPosition(person)).thenReturn(Optional.empty());
      doCallRealMethod().when(jobService).assignJobPosition(Mockito.any(Person.class), Mockito.any(JobPosition.class));

      assertTrue(jobService.assignJobPosition(person, new JobPosition()));
   }
}
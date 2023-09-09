package cn.tuyucheng.taketoday.mockito.java8;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAnswerWithLambdaUnitTest {

   @InjectMocks
   private UnemploymentServiceImpl unemploymentService;

   @Mock
   private JobService jobService;

   @Test
   public void whenPersonWithJobHistory_thenSearchReturnsValue() {
      Person peter = new Person("Peter");

      assertEquals("Teacher", unemploymentService.searchJob(peter, "").get().getTitle());
   }

   @Test
   public void whenPersonWithNoJobHistory_thenSearchReturnsEmpty() {
      Person linda = new Person("Linda");

      assertFalse(unemploymentService.searchJob(linda, "").isPresent());
   }

   @BeforeEach
   public void init() {
      when(jobService.listJobs(any(Person.class))).then(i ->
            Stream.of(new JobPosition("Teacher"))
                  .filter(p -> ((Person) i.getArgument(0)).getName().equals("Peter")));
   }
}
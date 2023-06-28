package cn.tuyucheng.taketoday.javaxval.notnull;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ValidatingComponentIntegrationTest {

   @Autowired
   ValidatingComponent component;

   @Test
   void givenValue_whenValidate_thenSuccess() {
      assertThat(component.validateNotNull("Not null!"), is(9));
   }

   @Test
   void givenNull_whenValidate_thenConstraintViolationException() {
      ConstraintViolationException constraintViolationException = assertThrows(ConstraintViolationException.class, () -> component.validateNotNull(null));
      Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
      assertThat(constraintViolations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType(), is(NotNull.class));
   }

   @Test
   void givenNull_whenOnlyCalledMethodHasAnnotation_thenNoValidation() {
      assertThrows(NullPointerException.class, () -> component.callAnnotatedMethod(null));
   }

   @SpringBootApplication
   static class TestApplication {
   }
}
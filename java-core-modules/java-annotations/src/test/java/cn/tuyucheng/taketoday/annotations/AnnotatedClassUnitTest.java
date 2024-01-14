package cn.tuyucheng.taketoday.annotations;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThat;

public class AnnotatedClassUnitTest {

   @Test
   public void whenAnnotationRetentionPolicyRuntime_shouldAccess() {
      AnnotatedClass anAnnotatedClass = new AnnotatedClass();
      Annotation[] annotations = anAnnotatedClass.getClass().getAnnotations();
      assertThat(annotations.length, is(1));
   }
}

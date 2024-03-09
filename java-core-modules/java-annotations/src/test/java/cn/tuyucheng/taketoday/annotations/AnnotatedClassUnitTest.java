package cn.tuyucheng.taketoday.annotations;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AnnotatedClassUnitTest {

   @Test
   public void whenAnnotationRetentionPolicyRuntime_shouldAccess() {
      AnnotatedClass anAnnotatedClass = new AnnotatedClass();
      Annotation[] annotations = anAnnotatedClass.getClass().getAnnotations();
      assertThat(annotations.length, is(1));
   }
}
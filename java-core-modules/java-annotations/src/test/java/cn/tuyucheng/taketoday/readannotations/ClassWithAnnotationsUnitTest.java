package cn.tuyucheng.taketoday.readannotations;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class ClassWithAnnotationsUnitTest {

    @Test
    void whenCallingGetDeclaredAnnotations_thenOnlyRuntimeAnnotationsAreAvailable() throws NoSuchFieldException {
        Field classMemberField = ClassWithAnnotations.class.getDeclaredField("classMember");
        Annotation[] annotations = classMemberField.getDeclaredAnnotations();
        assertThat(annotations).hasSize(2);
    }

    @Test
    void whenCallingIsAnnotationPresent_thenOnlyRuntimeAnnotationsAreAvailable() throws NoSuchFieldException {
        Field classMemberField = ClassWithAnnotations.class.getDeclaredField("classMember");
        assertThat(classMemberField.isAnnotationPresent(FirstAnnotation.class)).isTrue();
        assertThat(classMemberField.isAnnotationPresent(SecondAnnotation.class)).isTrue();
        assertThat(classMemberField.isAnnotationPresent(ThirdAnnotation.class)).isFalse();
    }

    @Test
    void whenCallingGetDeclaredAnnotationsOrGetAnnotations_thenSameAnnotationsAreReturned() throws NoSuchFieldException {
        Field classMemberField = ClassWithAnnotations.class.getDeclaredField("classMember");
        Annotation[] declaredAnnotations = classMemberField.getDeclaredAnnotations();
        Annotation[] annotations = classMemberField.getAnnotations();
        assertThat(declaredAnnotations).containsExactly(annotations);
    }
}
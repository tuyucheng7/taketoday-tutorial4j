package cn.tuyucheng.taketoday.countingbeans.olderspring.qualifier;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MyCustomAnnotation {

}
package cn.tuyucheng.taketoday.cartesianproduct;

import org.junitpioneer.jupiter.cartesian.CartesianArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@CartesianArgumentsSource(IntArgumentsProvider.class)
public @interface Ints {

   int[] value();
}
package cn.tuyucheng.taketoday.cartesianproduct;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junitpioneer.jupiter.cartesian.CartesianParameterArgumentsProvider;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

class IntArgumentsProvider implements CartesianParameterArgumentsProvider<Integer> {

   @Override
   public Stream<Integer> provideArguments(ExtensionContext context, Parameter parameter) {
      Ints source = Objects.requireNonNull(parameter.getAnnotation(Ints.class));
      return Arrays.stream(source.value()).boxed();
   }
}
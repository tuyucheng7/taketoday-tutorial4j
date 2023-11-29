package cn.tuyucheng.taketoday.cartesianproduct;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junitpioneer.jupiter.cartesian.CartesianParameterArgumentsProvider;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PeopleProvider implements CartesianParameterArgumentsProvider<Person> {

   @Override
   public Stream<Person> provideArguments(ExtensionContext context, Parameter parameter) {
      People source = Objects.requireNonNull(parameter.getAnnotation(People.class));
      return IntStream
            .range(0, source.names().length)
            .mapToObj(i -> new Person(source.names()[i], source.ages()[i]));
   }
}
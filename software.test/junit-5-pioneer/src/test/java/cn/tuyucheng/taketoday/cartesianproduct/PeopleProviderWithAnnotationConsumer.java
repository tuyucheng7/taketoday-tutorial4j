package cn.tuyucheng.taketoday.cartesianproduct;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junitpioneer.jupiter.cartesian.CartesianParameterArgumentsProvider;

import java.lang.reflect.Parameter;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PeopleProviderWithAnnotationConsumer implements CartesianParameterArgumentsProvider<Person>, AnnotationConsumer<People> {

   private People source;

   @Override
   public Stream<Person> provideArguments(ExtensionContext context, Parameter parameter) {
      return IntStream
            .range(0, source.names().length)
            .mapToObj(i -> new Person(source.names()[i], source.ages()[i]));
   }

   @Override
   public void accept(People source) {
      this.source = source;
   }
}
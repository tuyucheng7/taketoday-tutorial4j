package cn.tuyucheng.taketoday.param;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class ValidPersonParameterResolver implements ParameterResolver {

   public static Person[] VALID_PERSONS = {
         new Person().setId(1L).setLastName("Adams").setFirstName("Jill"),
         new Person().setId(2L).setLastName("Baker").setFirstName("James"),
         new Person().setId(3L).setLastName("Carter").setFirstName("Samanta"),
         new Person().setId(4L).setLastName("Daniels").setFirstName("Joseph"),
         new Person().setId(5L).setLastName("English").setFirstName("Jane"),
         new Person().setId(6L).setLastName("Fontana").setFirstName("Enrique"),
   };

   @Override
   public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      Object ret = null;
      // Return a random, valid Person object if Person.class is the type of Parameter to be resolved. Otherwise, return null.
      if (parameterContext.getParameter().getType() == Person.class) {
         ret = VALID_PERSONS[new Random().nextInt(VALID_PERSONS.length)];
      }
      return ret;
   }

   @Override
   public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      // If the Parameter.type == Person.class, then we support it, otherwise, get outta here!
      return parameterContext.getParameter().getType() == Person.class;
   }
}
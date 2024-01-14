package cn.tuyucheng.taketoday.emptystringoptional;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EmptyStringToEmptyOptionalUnitTest {

   @Test
   public void givenEmptyString_whenFilteringOnOptional_thenEmptyOptionalIsReturned() {
      String str = "";
      Optional<String> opt = Optional.ofNullable(str).filter(s -> !s.isEmpty());
      Assertions.assertFalse(opt.isPresent());
   }

//    Uncomment code when code base is compatible with Java 11
//    @Test
//    public void givenEmptyString_whenFilteringOnOptionalInJava11_thenEmptyOptionalIsReturned() {
//        String str = "";
//        Optional<String> opt = Optional.ofNullable(str).filter(Predicate.not(String::isEmpty));
//        Assert.assertFalse(opt.isPresent());
//    }

   @Test
   public void givenEmptyString_whenPassingResultOfEmptyToNullToOfNullable_thenEmptyOptionalIsReturned() {
      String str = "";
      Optional<String> opt = Optional.ofNullable(Strings.emptyToNull(str));
      Assertions.assertFalse(opt.isPresent());
   }
}

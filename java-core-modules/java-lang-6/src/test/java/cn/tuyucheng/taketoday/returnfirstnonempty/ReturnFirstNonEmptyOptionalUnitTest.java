package cn.tuyucheng.taketoday.returnfirstnonempty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ReturnFirstNonEmptyOptionalUnitTest {

   private List<Optional<String>> optionals;

   @BeforeEach
   public void init() {
      optionals = Arrays.asList(Optional.<String>empty(), Optional.of("first non empty"), Optional.of("second non empty"));
   }

   @Test
   void givenListOfOptionals_whenStreaming_thenReturnFirstNonEmpty() {
      Optional<String> object = optionals.stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

      assertThat(object).contains("first non empty");
   }
}
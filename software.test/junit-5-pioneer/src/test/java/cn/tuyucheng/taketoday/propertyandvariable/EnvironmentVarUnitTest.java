package cn.tuyucheng.taketoday.propertyandvariable;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("fails on CI")
@ClearEnvironmentVariable(key = "class variable")
public class EnvironmentVarUnitTest {

   @Test
   @ClearEnvironmentVariable(key = "some variable")
   void whenClearEnvProperty_thenShouldBeNull() {
      assertThat(System.getenv("some variable")).isNull();
   }

   @Test
   @SetEnvironmentVariable(key = "some variable", value = "new value")
   void givenEnvValue_whenGetIt_thenCorrect() {
      assertThat(System.getenv("some variable")).isEqualTo("new value");
   }

   @Test
   @ClearEnvironmentVariable(key = "1st variable")
   @ClearEnvironmentVariable(key = "2nd variable")
   @SetEnvironmentVariable(key = "3rd variable", value = "new value")
   void testClearingAndEnvironmentVariable() {
      assertThat(System.getenv("1st variable")).isNull();
      assertThat(System.getenv("2nd variable")).isNull();
      assertThat(System.getenv("3rd variable")).isEqualTo("new value");
   }

   @Test
   @SetEnvironmentVariable(key = "class variable", value = "new value")
   void givenClassLevelAnno_whenSetValueAtMethod_thenShouldCorrect() {
      assertThat(System.getenv("class variable")).isEqualTo("new value");
   }
}
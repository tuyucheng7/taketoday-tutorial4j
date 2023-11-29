package cn.tuyucheng.taketoday.propertyandvariable;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

@ClearSystemProperty(key = "class property")
public class SystemPropertiesUnitTest {

   @Test
   @ClearSystemProperty(key = "some property")
   void whenClearProperty_thenShouldBeNull() {
      assertThat(System.getProperty("some property")).isNull();
   }

   @Test
   @SetSystemProperty(key = "some property", value = "new value")
   void givenPropertyValue_whenGetIt_thenCorrect() {
      assertThat(System.getProperty("some property")).isEqualTo("new value");
   }

   @Test
   @ClearSystemProperty(key = "1st property")
   @ClearSystemProperty(key = "2nd property")
   @SetSystemProperty(key = "3rd property", value = "new value")
   void testClearingAndSettingProperty() {
      assertThat(System.getProperty("1st property")).isNull();
      assertThat(System.getProperty("2nd property")).isNull();
      assertThat(System.getProperty("3rd property")).isEqualTo("new value");
   }

   @Test
   @SetSystemProperty(key = "class property", value = "new value")
   void givenClassLevelAnno_whenSetValueAtMethod_thenShouldCorrect() {
      assertThat(System.getProperty("class property")).isEqualTo("new value");
   }
}
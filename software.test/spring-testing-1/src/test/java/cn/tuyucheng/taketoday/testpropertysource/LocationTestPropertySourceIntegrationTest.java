package cn.tuyucheng.taketoday.testpropertysource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClassUsingProperty.class)
@TestPropertySource(locations = "/other-location.properties")
class LocationTestPropertySourceIntegrationTest {

   @Autowired
   ClassUsingProperty classUsingProperty;

   @Test
   void givenDefaultTestPropertySource_whenVariableOneRetrieved_thenValueInDefaultFileReturned() {
      String output = classUsingProperty.retrievePropertyOne();

      assertThat(output).isEqualTo("other-location-value");
   }
}
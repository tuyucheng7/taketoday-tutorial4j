package cn.tuyucheng.taketoday.sample.test.slices;

import cn.tuyucheng.taketoday.sample.pets.domain.PetServiceRepository;
import cn.tuyucheng.taketoday.sample.pets.domain.PetsDomainLayer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

import static org.mockito.Mockito.mock;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = PetsDomainLayer.class)
@Import(PetsDomainTest.PetServiceTestConfiguration.class)
// further features that can help to configure and execute tests
@ActiveProfiles({"test", "domain-test"})
@Tag("integration-test")
@Tag("domain-test")
public @interface PetsDomainTest {

   @TestConfiguration
   class PetServiceTestConfiguration {

      @Primary
      @Bean
      PetServiceRepository createPetsRepositoryMock() {
         return mock(
               PetServiceRepository.class,
               MockReset.withSettings(MockReset.AFTER)
         );
      }
   }
}
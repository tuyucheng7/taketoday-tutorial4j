package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.h2db.springboot.SpringBootH2Application;
import cn.tuyucheng.taketoday.h2db.springboot.daos.CountryRepository;
import cn.tuyucheng.taketoday.h2db.springboot.models.Country;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootH2Application.class)
class SpringBootH2IntegrationTest {

   private static final Country AN_EXPECTED_COUNTRY = buildCountry();

   @Autowired
   private CountryRepository countryRepository;

   @Test
   void givenInitData_whenApplicationStarts_thenDataIsLoaded() {
      Iterable<Country> users = countryRepository.findAll();

      assertThat(users)
            .hasSize(5)
            .contains(AN_EXPECTED_COUNTRY);
   }

   private static Country buildCountry() {
      Country c = new Country();
      c.setId(5);
      c.setName("Canada");
      return c;
   }
}
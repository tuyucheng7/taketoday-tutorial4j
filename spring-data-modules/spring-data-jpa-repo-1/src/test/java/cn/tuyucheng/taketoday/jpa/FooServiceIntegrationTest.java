package cn.tuyucheng.taketoday.jpa;

import cn.tuyucheng.taketoday.jpa.domain.Foo;
import cn.tuyucheng.taketoday.jpa.service.IFooService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JpaApplication.class})
@DirtiesContext
class FooServiceIntegrationTest {

   @Autowired
   private IFooService service;

   @Autowired
   private DataSource dataSource;

   @Test
   void whenInvalidEntityIsCreated_thenDataException() {
      assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo()));
   }
}
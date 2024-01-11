package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class FooServicePersistenceIntegrationTest {

   @Autowired
   @Qualifier("fooHibernateService")
   private IFooService service;

   @Test
   final void whenContextIsBootstrapped_thenNoExceptions() {
      //
   }

   @Test
   final void whenEntityIsCreated_thenNoExceptions() {
      service.create(new Foo(randomAlphabetic(6)));
   }

   @Test
   @Disabled("work in progress")
   final void whenInvalidEntityIsCreated_thenDataException() {
      assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo()));
   }

   @Test
   final void whenEntityWithLongNameIsCreated_thenDataException() {
      assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
   }

   @Test
   @Disabled("Right now, persist has saveOrUpdate semantics, so this will no longer fail")
   final void whenSameEntityIsCreatedTwice_thenDataException() {
      final Foo entity = new Foo(randomAlphabetic(8));
      service.create(entity);
      assertThrows(InvalidDataAccessApiUsageException.class, () -> service.create(entity));
   }

   @Test
   final void temp_whenInvalidEntityIsCreated_thenDataException() {
      assertThrows(InvalidDataAccessApiUsageException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
   }
}
package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.config.PersistenceJPAConfig;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceJPAConfig.class}, loader = AnnotationConfigContextLoader.class)
@DirtiesContext
class FooServicePersistenceIntegrationTest {

   @Autowired
   private FooService service;

   // tests

   @Test
   final void whenContextIsBootstrapped_thenNoExceptions() {
      //
   }

   @Test
   final void whenEntityIsCreated_thenNoExceptions() {
      service.create(new Foo(randomAlphabetic(6)));
   }

   @Test
   final void whenInvalidEntityIsCreated_thenDataException() {
      assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
   }

   @Test
   final void whenEntityWithLongNameIsCreated_thenDataException() {
      assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
   }

   @Test
   final void whenSameEntityIsCreatedTwice_thenDataException() {
      final Foo entity = new Foo(randomAlphabetic(8));
      service.create(entity);
      assertThrows(InvalidDataAccessApiUsageException.class, () -> service.create(entity));
   }

   @Test
   final void temp_whenInvalidEntityIsCreated_thenDataException() {
      assertThrows(DataAccessException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
   }

   @Test
   final void whenEntityIsCreated_thenFound() {
      final Foo fooEntity = new Foo("abc");
      service.create(fooEntity);
      final Foo found = service.findOne(fooEntity.getId());
      Assertions.assertNotNull(found);
   }
}
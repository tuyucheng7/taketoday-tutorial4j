package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.model.Child;
import cn.tuyucheng.taketoday.persistence.model.Parent;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ParentServicePersistenceIntegrationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(ParentServicePersistenceIntegrationTest.class);

   @Autowired
   private IParentService service;

   @Autowired
   private IChildService childService;

   @Test
   final void whenContextIsBootstrapped_thenNoExceptions() {
      //
   }

   @Test
   final void whenOneToOneEntitiesAreCreated_thenNoExceptions() {
      final Child childEntity = new Child();
      childService.create(childEntity);

      final Parent parentEntity = new Parent(childEntity);
      service.create(parentEntity);

      LOGGER.debug("Child = {}", childService.findOne(childEntity.getId()));
      LOGGER.debug("Child - parent = {}", childService.findOne(childEntity.getId()).getParent());

      LOGGER.debug("Parent = {}", service.findOne(parentEntity.getId()));
      LOGGER.debug("Parent - child = {}", service.findOne(parentEntity.getId()).getChild());
   }

   @Test
   final void whenChildIsDeletedWhileParentStillHasForeignKeyToIt_thenDataException() {
      final Child childEntity = new Child();
      childService.create(childEntity);

      final Parent parentEntity = new Parent(childEntity);
      service.create(parentEntity);

      assertThrows(DataIntegrityViolationException.class, () -> childService.delete(childEntity));
   }

   @Test
   final void whenChildIsDeletedAfterTheParent_thenNoExceptions() {
      final Child childEntity = new Child();
      childService.create(childEntity);

      final Parent parentEntity = new Parent(childEntity);
      service.create(parentEntity);

      service.delete(parentEntity);
      childService.delete(childEntity);
   }
}
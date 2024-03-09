package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.config.PersistenceJPAConfig;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceJPAConfig.class}, loader = AnnotationConfigContextLoader.class)
@DirtiesContext
class FooServiceSortingWitNullsManualIntegrationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(FooServiceSortingWitNullsManualIntegrationTest.class);

   @PersistenceContext
   private EntityManager entityManager;

   @Autowired
   private FooService service;

   // tests

   @SuppressWarnings("unchecked")
   @Test
   final void whenSortingByStringNullLast_thenLastNull() {
      service.create(new Foo());
      service.create(new Foo(randomAlphabetic(6)));

      final String jql = "Select f from Foo as f order by f.name desc NULLS LAST";
      final Query sortQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = sortQuery.getResultList();
      assertNull(fooList.get(fooList.toArray().length - 1).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}", foo.getName());
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   final void whenSortingByStringNullFirst_thenFirstNull() {
      service.create(new Foo());

      final String jql = "Select f from Foo as f order by f.name desc NULLS FIRST";
      final Query sortQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = sortQuery.getResultList();
      assertNull(fooList.get(0).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}", foo.getName());
      }
   }
}
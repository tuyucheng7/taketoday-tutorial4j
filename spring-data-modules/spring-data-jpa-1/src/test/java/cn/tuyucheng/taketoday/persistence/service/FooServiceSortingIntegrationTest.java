package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.config.PersistenceJPAConfig;
import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceJPAConfig.class}, loader = AnnotationConfigContextLoader.class)
@DirtiesContext
@SuppressWarnings("unchecked")
class FooServiceSortingIntegrationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(FooServiceSortingIntegrationTest.class);

   @PersistenceContext
   private EntityManager entityManager;

   // tests

   @Test
   final void whenSortingByOneAttributeDefaultOrder_thenPrintSortedResult() {
      final String jql = "Select f from Foo as f order by f.id";
      final Query sortQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = sortQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}-------Id:{}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenSortingByOneAttributeSetOrder_thenSortedPrintResult() {
      final String jql = "Select f from Foo as f order by f.id desc";
      final Query sortQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = sortQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}-------Id:{}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenSortingByTwoAttributes_thenPrintSortedResult() {
      final String jql = "Select f from Foo as f order by f.name asc, f.id desc";
      final Query sortQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = sortQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}-------Id:{}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenSortingFooByBar_thenBarsSorted() {
      final String jql = "Select f from Foo as f order by f.name, f.bar.id";
      final Query barJoinQuery = entityManager.createQuery(jql);
      final List<Foo> fooList = barJoinQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}", foo.getName());
         if (foo.getBar() != null) {
            LOGGER.debug("-------BarId:{}", foo.getBar().getId());
         }
      }
   }

   @Test
   final void whenSortinfBar_thenPrintBarsSortedWithFoos() {
      final String jql = "Select b from Bar as b order by b.id";
      final Query barQuery = entityManager.createQuery(jql);
      final List<Bar> barList = barQuery.getResultList();
      for (final Bar bar : barList) {
         LOGGER.debug("Bar Id:{}", bar.getId());
         for (final Foo foo : bar.getFooList()) {
            LOGGER.debug("FooName:{}", foo.getName());
         }
      }
   }

   @Test
   final void whenSortingFooWithCriteria_thenPrintSortedFoos() {
      final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      final CriteriaQuery<Foo> criteriaQuery = criteriaBuilder.createQuery(Foo.class);
      final Root<Foo> from = criteriaQuery.from(Foo.class);
      final CriteriaQuery<Foo> select = criteriaQuery.select(from);
      criteriaQuery.orderBy(criteriaBuilder.asc(from.get("name")));
      final TypedQuery<Foo> typedQuery = entityManager.createQuery(select);
      final List<Foo> fooList = typedQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}-------Id:{}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenSortingFooWithCriteriaAndMultipleAttributes_thenPrintSortedFoos() {
      final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      final CriteriaQuery<Foo> criteriaQuery = criteriaBuilder.createQuery(Foo.class);
      final Root<Foo> from = criteriaQuery.from(Foo.class);
      final CriteriaQuery<Foo> select = criteriaQuery.select(from);
      criteriaQuery.orderBy(criteriaBuilder.asc(from.get("name")), criteriaBuilder.desc(from.get("id")));
      final TypedQuery<Foo> typedQuery = entityManager.createQuery(select);
      final List<Foo> fooList = typedQuery.getResultList();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name:{}-------Id:{}", foo.getName(), foo.getId());
      }
   }
}
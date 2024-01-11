package cn.tuyucheng.taketoday.persistence.hibernate;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NullPrecedence;
import org.hibernate.query.Order;
import org.hibernate.query.Query;
import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
@SuppressWarnings("unchecked")
public class FooSortingPersistenceIntegrationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(FooSortingPersistenceIntegrationTest.class);

   @Autowired
   private SessionFactory sessionFactory;

   private Session session;

   @BeforeEach
   void before() {
      session = sessionFactory.openSession();

      session.beginTransaction();

      final FooFixtures fooData = new FooFixtures(sessionFactory);
      fooData.createBars();
   }

   @AfterEach
   void after() {
      session.getTransaction().commit();
      session.close();
   }

   @Test
   final void whenHQlSortingByOneAttribute_thenPrintSortedResults() {
      final String hql = "FROM Foo f ORDER BY f.name";
      final Query query = session.createQuery(hql);
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {} , Id: {}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenHQlSortingByStringNullLast_thenLastNull() {
      final String hql = "FROM Foo f ORDER BY f.name NULLS LAST";
      final Query query = session.createQuery(hql);
      final List<Foo> fooList = query.list();

      assertNull(fooList.get(fooList.toArray().length - 1).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {}, Id: {}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenSortingByStringNullsFirst_thenReturnNullsFirst() {
      final String hql = "FROM Foo f ORDER BY f.name NULLS FIRST";
      final Query query = session.createQuery(hql);
      final List<Foo> fooList = query.list();
      assertNull(fooList.get(0).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {}", foo.getName());
      }
   }

   @Test
   final void whenHQlSortingByOneAttribute_andOrderDirection_thenPrintSortedResults() {
      final String hql = "FROM Foo f ORDER BY f.name ASC";
      Query<Foo> query = session.createQuery(hql, Foo.class);
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {}, Id: {}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenHQlSortingByMultipleAttributes_thenSortedResults() {
      final String hql = "FROM Foo f ORDER BY f.name, f.id";
      Query<Foo> query = session.createQuery(hql, Foo.class);
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {}, Id: {}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenHQlSortingByMultipleAttributes_andOrderDirection_thenPrintSortedResults() {
      final String hql = "FROM Foo f ORDER BY f.name DESC, f.id ASC";
      Query<Foo> query = session.createQuery(hql, Foo.class);
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Name: {}, Id: {}", foo.getName(), foo.getId());
      }
   }

   @Test
   final void whenHQLCriteriaSortingByOneAttr_thenPrintSortedResults() {
      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);
      Query<Foo> query = session.createQuery(selectQuery);

      query.setOrder(Collections.singletonList(Order.asc(Foo.class, "id")));
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Id: {}, FirstName: {}", foo.getId(), foo.getName());
      }
   }

   @Test
   final void whenHQLCriteriaSortingByMultipAttr_thenSortedResults() {
      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);
      Query<Foo> query = session.createQuery(selectQuery);

      List<Order<? super Foo>> orderBy = new ArrayList<>(2);
      orderBy.add(Order.asc(Foo.class, "name"));
      orderBy.add(Order.asc(Foo.class, "id"));
      query.setOrder(orderBy);
      final List<Foo> fooList = query.list();
      for (final Foo foo : fooList) {
         LOGGER.debug("Id: {}, FirstName: {}", foo.getId(), foo.getName());
      }
   }

   @Test
   final void whenCriteriaSortingStringNullsLastAsc_thenNullsLast() {
      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);
      Query<Foo> query = session.createQuery(selectQuery);

      List<Order<? super Foo>> orderBy = new ArrayList<>(2);
      orderBy.add(Order.by(Foo.class, "name", SortDirection.ASCENDING, NullPrecedence.LAST));
      query.setOrder(orderBy);

      final List<Foo> fooList = query.list();
      assertNull(fooList.get(fooList.toArray().length - 1).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Id: {}, FirstName: {}", foo.getId(), foo.getName());
      }
   }

   @Test
   final void whenCriteriaSortingStringNullsFirstDesc_thenNullsFirst() {
      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);
      Query<Foo> query = session.createQuery(selectQuery);

      List<Order<? super Foo>> orderBy = new ArrayList<>(2);
      orderBy.add(Order.by(Foo.class, "name", SortDirection.ASCENDING, NullPrecedence.FIRST));
      query.setOrder(orderBy);

      final List<Foo> fooList = query.list();
      assertNull(fooList.get(0).getName());
      for (final Foo foo : fooList) {
         LOGGER.debug("Id: {}, FirstName: {}", foo.getId(), foo.getName());
      }
   }

   @Test
   final void whenSortingBars_thenBarsWithSortedFoos() {
      final String hql = "FROM Bar b ORDER BY b.id";
      final Query<Bar> query = session.createQuery(hql, Bar.class);
      final List<Bar> barList = query.list();
      for (final Bar bar : barList) {
         final Set<Foo> fooSet = bar.getFooSet();
         LOGGER.debug("Bar Id:{}", bar.getId());
         for (final Foo foo : fooSet) {
            LOGGER.debug("FooName:{}", foo.getName());
         }
      }
   }
}
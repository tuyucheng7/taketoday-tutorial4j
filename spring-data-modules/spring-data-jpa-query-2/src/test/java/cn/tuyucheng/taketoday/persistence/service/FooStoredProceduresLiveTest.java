package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.config.PersistenceConfig;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.Query;
import org.junit.Assume;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceConfig.class}, loader = AnnotationConfigContextLoader.class)
class FooStoredProceduresLiveTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(FooStoredProceduresLiveTest.class);

   @Autowired
   private SessionFactory sessionFactory;

   @Autowired
   private IFooService fooService;

   private Session session;

   @Autowired
   @Qualifier("jpaEntityManager")
   private EntityManagerFactory entityManagerFactory;

   private EntityManager entityManager;

   @BeforeEach
   final void before() {
      entityManager = entityManagerFactory.createEntityManager();
      session = sessionFactory.openSession();
      assumeTrue(getAllFoosExists());
      assumeTrue(getFoosByNameExists());
   }

   private boolean getFoosByNameExists() {
      try {
         Query<Foo> sqlQuery = session.createNativeQuery("CALL GetFoosByName()", Foo.class);
         sqlQuery.list();
         return true;
      } catch (SQLGrammarException e) {
         LOGGER.error("WARNING : GetFoosByName() Procedure may be missing ", e);
         return false;
      }
   }

   private boolean getAllFoosExists() {
      try {
         Query<Foo> sqlQuery = session.createNativeQuery("CALL GetAllFoos()", Foo.class);
         sqlQuery.list();
         return true;
      } catch (SQLGrammarException e) {
         LOGGER.error("WARNING : GetAllFoos() Procedure may be missing ", e);
         return false;
      }
   }

   @AfterEach
   final void after() {
      session.close();
      entityManager.close();
   }

   @Test
   final void getAllFoosUsingStoredProcedures() {
      fooService.create(new Foo(randomAlphabetic(6)));

      // Stored procedure getAllFoos using createQuery
      Query<Foo> sqlQuery = session.createNativeQuery("CALL GetAllFoos()", Foo.class);

      List<Foo> allFoos = sqlQuery.list();
      for (Foo foo : allFoos) {
         LOGGER.info("getAllFoos() SQL Query result : {}", foo.getName());
      }
      assertEquals(allFoos.size(), fooService.findAll().size());

      // Stored procedure getAllFoos using a Named Query
      Query<Foo> namedQuery = session.createNamedQuery("callGetAllFoos", Foo.class);

      List<Foo> allFoos2 = namedQuery.list();
      for (Foo foo : allFoos2) {
         LOGGER.info("getAllFoos() NamedQuery result : {}", foo.getName());
      }
      assertEquals(allFoos2.size(), fooService.findAll().size());

      StoredProcedureQuery spQuery =
            entityManager.createNamedStoredProcedureQuery("GetAllFoos");
      @SuppressWarnings("unchecked")
      List<Foo> allFoos3 = spQuery.getResultList();
      for (Foo foo : allFoos3) {
         LOGGER.info("getAllFoos() StoredProcedureQuery result : {}", foo.getName());
      }
      assertEquals(allFoos3.size(), fooService.findAll().size());
   }

   @Test
   final void getFoosByNameUsingStoredProcedures() {
      fooService.create(new Foo("NewFooName"));

      // Stored procedure getFoosByName using createSQLQuery()
      Query<Foo> sqlQuery = session.createNativeQuery("CALL GetFoosByName(:fooName)", Foo.class).setParameter("fooName", "NewFooName");

      List<Foo> allFoosByName = sqlQuery.list();
      for (Foo foo : allFoosByName) {
         LOGGER.info("getFoosByName() using SQL Query : found => {}", foo.toString());
      }

      // Stored procedure getFoosByName using getNamedQuery()
      Query<Foo> namedQuery = session.createQuery("callGetFoosByName", Foo.class).setParameter("fooName", "NewFooName");

      List<Foo> allFoosByName2 = namedQuery.list();
      for (Foo foo : allFoosByName2) {
         LOGGER.info("getFoosByName() using Native Query : found => {}", foo.toString());
      }

      StoredProcedureQuery spQuery = entityManager.
            createNamedStoredProcedureQuery("GetFoosByName")
            .setParameter("fooName", "NewFooName");
      @SuppressWarnings("unchecked")
      List<Foo> allFoosByName3 = spQuery.getResultList();
      assertEquals(1, allFoosByName3.size());
      for (Foo foo : allFoosByName3) {
         LOGGER.info("getFoosByName() using StoredProcedureQuery : found => {}", foo.toString());
         assertEquals("NewFooName", foo.getName());
      }
   }
}
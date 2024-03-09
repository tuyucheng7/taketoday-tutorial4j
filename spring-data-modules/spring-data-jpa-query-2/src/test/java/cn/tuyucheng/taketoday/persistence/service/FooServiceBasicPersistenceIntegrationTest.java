package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class FooServiceBasicPersistenceIntegrationTest {

   @Autowired
   private SessionFactory sessionFactory;

   @Autowired
   private IFooService fooService;

   private Session session;

   @BeforeEach
   final void before() {
      session = sessionFactory.openSession();
   }

   @AfterEach
   final void after() {
      session.close();
   }

   @Test
   final void whenContextIsBootstrapped_thenNoExceptions() {
      //
   }

   @Test
   final void whenEntityIsCreated_thenNoExceptions() {
      fooService.create(new Foo(randomAlphabetic(6)));
   }
}
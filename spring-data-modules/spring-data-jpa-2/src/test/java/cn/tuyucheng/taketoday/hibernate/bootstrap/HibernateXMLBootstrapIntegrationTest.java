package cn.tuyucheng.taketoday.hibernate.bootstrap;

import cn.tuyucheng.taketoday.hibernate.bootstrap.model.TestEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateXMLConf.class})
@Transactional
class HibernateXMLBootstrapIntegrationTest {

   @Autowired
   private SessionFactory sessionFactory;

   @Test
   void whenBootstrapHibernateSession_thenNoException() {
      Session session = sessionFactory.getCurrentSession();

      TestEntity newEntity = new TestEntity();
      newEntity.setId(1);
      session.persist(newEntity);

      TestEntity searchEntity = session.find(TestEntity.class, 1);

      Assertions.assertNotNull(searchEntity);
   }
}
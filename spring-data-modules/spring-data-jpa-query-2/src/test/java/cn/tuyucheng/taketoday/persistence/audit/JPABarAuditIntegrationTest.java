package cn.tuyucheng.taketoday.persistence.audit;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.service.IBarService;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
class JPABarAuditIntegrationTest {

   private static final Logger logger = LoggerFactory.getLogger(JPABarAuditIntegrationTest.class);

   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      logger.info("setUpBeforeClass()");
   }

   @AfterAll
   static void tearDownAfterClass() throws Exception {
      logger.info("tearDownAfterClass()");
   }

   @Autowired
   @Qualifier("barJpaService")
   private IBarService barService;

   @Autowired
   @Qualifier("jpaEntityManager")
   private EntityManagerFactory entityManagerFactory;

   private EntityManager em;

   @BeforeEach
   void setUp() throws Exception {
      logger.info("setUp()");
      em = entityManagerFactory.createEntityManager();
   }

   @AfterEach
   void tearDown() throws Exception {
      logger.info("tearDown()");
      em.close();
   }

   @Test
   final void whenBarsModified_thenBarsAudited() {
      // insert BAR1
      Bar bar1 = new Bar("BAR1");
      barService.create(bar1);

      // update BAR1
      bar1.setName("BAR1a");
      barService.update(bar1);

      // insert BAR2
      Bar bar2 = new Bar("BAR2");
      barService.create(bar2);

      // update BAR1
      bar1.setName("BAR1b");
      barService.update(bar1);

      // get BAR1 and BAR2 from the DB and check the audit values
      // detach instances from persistence context to make sure we fire db
      em.detach(bar1);
      em.detach(bar2);
      bar1 = barService.findOne(bar1.getId());
      bar2 = barService.findOne(bar2.getId());

      assertNotNull(bar1);
      assertNotNull(bar2);
      Assertions.assertEquals(Bar.OPERATION.UPDATE, bar1.getOperation());
      Assertions.assertEquals(Bar.OPERATION.INSERT, bar2.getOperation());
      assertTrue(bar1.getTimestamp() > bar2.getTimestamp());

      barService.deleteById(bar1.getId());
      barService.deleteById(bar2.getId());
   }
}
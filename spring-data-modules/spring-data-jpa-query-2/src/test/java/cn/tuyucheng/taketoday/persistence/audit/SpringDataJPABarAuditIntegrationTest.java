package cn.tuyucheng.taketoday.persistence.audit;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.service.IBarService;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
class SpringDataJPABarAuditIntegrationTest {

   private static final Logger logger = LoggerFactory.getLogger(SpringDataJPABarAuditIntegrationTest.class);

   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      logger.info("setUpBeforeClass()");
   }

   @AfterAll
   static void tearDownAfterClass() throws Exception {
      logger.info("tearDownAfterClass()");
   }

   @Autowired
   @Qualifier("barSpringDataJpaService")
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
   @WithMockUser(username = "tutorialuser")
   final void whenBarsModified_thenBarsAudited() {
      Bar bar = new Bar("BAR1");
      barService.create(bar);

      assertEquals(bar.getCreatedDate(), bar.getModifiedDate());
      assertEquals("tutorialuser", bar.getCreatedBy(), bar.getModifiedBy());

      bar.setName("BAR2");
      bar = barService.update(bar);

      assertTrue(bar.getCreatedDate() < bar.getModifiedDate());
      assertEquals("tutorialuser", bar.getCreatedBy(), bar.getModifiedBy());
   }
}
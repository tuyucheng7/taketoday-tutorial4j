package cn.tuyucheng.taketoday.boot.services;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.domain.Bar;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
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
    private BarService barService;

    @Autowired
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
        assertEquals(bar.getCreatedBy(), bar.getModifiedBy(), "tutorialuser");
        bar.setName("BAR2");
        bar = barService.update(bar);
        assertTrue(bar.getCreatedDate() < bar.getModifiedDate());
        assertEquals(bar.getCreatedBy(), bar.getModifiedBy(), "tutorialuser");
    }
}
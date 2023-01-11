package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class FooServiceBasicPersistenceIntegrationTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IFooService fooService;

    private Session session;

    @Before
    public final void before() {
        session = sessionFactory.openSession();
    }

    @After
    public final void after() {
        session.close();
    }

    @Test
    public final void whenContextIsBootstrapped_thenNoExceptions() {
        //
    }

    @Test
    public final void whenEntityIsCreated_thenNoExceptions() {
        fooService.create(new Foo(randomAlphabetic(6)));
    }
}
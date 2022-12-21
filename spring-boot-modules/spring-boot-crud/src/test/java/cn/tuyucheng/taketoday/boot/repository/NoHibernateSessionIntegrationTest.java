package cn.tuyucheng.taketoday.boot.repository;

import cn.tuyucheng.taketoday.boot.ApplicationIntegrationTest;
import cn.tuyucheng.taketoday.demo.model.Foo;
import cn.tuyucheng.taketoday.session.exception.repository.FooRepository;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@TestPropertySource("classpath:exception-hibernate.properties")
class NoHibernateSessionIntegrationTest extends ApplicationIntegrationTest {

    @Autowired
    private FooRepository fooRepository;

    @Test
    void whenSavingWithoutCurrentSession_thenThrowException() {
        Foo foo = new Foo("Exception Thrown");
        assertThrows(HibernateException.class, () -> fooRepository.save(foo));
    }
}
package cn.tuyucheng.taketoday.boot.repository;

import cn.tuyucheng.taketoday.demo.DemoApplicationIntegrationTest;
import cn.tuyucheng.taketoday.demo.model.Foo;
import cn.tuyucheng.taketoday.demo.repository.FooRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
class HibernateSessionIntegrationTest extends DemoApplicationIntegrationTest {

    @Autowired
    private FooRepository fooRepository;

    @Test
    void whenSavingWithCurrentSession_thenThrowNoException() {
        fooRepository.save(new Foo("Exception Solved"));

        Foo foo = fooRepository.findByName("Exception Solved");

        assertThat(foo, notNullValue());
        assertThat(foo.getId(), notNullValue());
        assertThat(foo.getName(), is("Exception Solved"));
    }
}
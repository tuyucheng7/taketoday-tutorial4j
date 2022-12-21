package cn.tuyucheng.taketoday.boot.repository;

import cn.tuyucheng.taketoday.demo.DemoApplicationIntegrationTest;
import cn.tuyucheng.taketoday.demo.model.Foo;
import cn.tuyucheng.taketoday.demo.repository.FooRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
class FooRepositoryIntegrationTest extends DemoApplicationIntegrationTest {

    @Autowired
    private FooRepository fooRepository;

    @BeforeEach
    void setUp() {
        fooRepository.save(new Foo("Foo"));
        fooRepository.save(new Foo("Bar"));
    }

    @Test
    void testFindByName() {
        Foo foo = fooRepository.findByName("Bar");

        assertThat(foo, notNullValue());
        assertThat(foo.getId(), notNullValue());
        assertThat(foo.getName(), is("Bar"));
    }

    @AfterEach
    void tearDown() {
        fooRepository.deleteAll();
    }
}
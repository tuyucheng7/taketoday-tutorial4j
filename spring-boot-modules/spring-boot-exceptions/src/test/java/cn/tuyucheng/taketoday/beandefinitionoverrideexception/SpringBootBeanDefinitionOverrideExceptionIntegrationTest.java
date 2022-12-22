package cn.tuyucheng.taketoday.beandefinitionoverrideexception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfiguration1.class, TestConfiguration2.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
class SpringBootBeanDefinitionOverrideExceptionIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void whenBeanOverridingAllowed_thenTestBean2OverridesTestBean1() {
        Object testBean = applicationContext.getBean("testBean");

        assertThat(testBean.getClass()).isEqualTo(TestConfiguration2.TestBean2.class);
    }
}
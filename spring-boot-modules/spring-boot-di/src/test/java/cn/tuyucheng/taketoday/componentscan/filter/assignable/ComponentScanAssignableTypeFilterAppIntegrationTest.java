package cn.tuyucheng.taketoday.componentscan.filter.assignable;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ComponentScanAssignableTypeFilterApp.class)
class ComponentScanAssignableTypeFilterAppIntegrationTest {

    @Test
    void whenAssignableTypeFilterIsUsed_thenComponentScanShouldRegisterBean() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanAssignableTypeFilterApp.class);
        List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
              .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanAssignableTypeFilterApp"))
              .collect(Collectors.toList());

        assertThat(beans.size(), equalTo(2));
        assertThat(beans.contains("cat"), equalTo(true));
        assertThat(beans.contains("elephant"), equalTo(true));
    }
}
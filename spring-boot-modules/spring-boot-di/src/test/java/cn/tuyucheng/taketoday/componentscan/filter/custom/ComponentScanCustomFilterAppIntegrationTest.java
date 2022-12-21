package cn.tuyucheng.taketoday.componentscan.filter.custom;

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
@SpringBootTest(classes = ComponentScanCustomFilterApp.class)
class ComponentScanCustomFilterAppIntegrationTest {

    @Test
    void whenCustomFilterIsUsed_thenComponentScanShouldRegisterBeanMatchingCustomFilter() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanCustomFilterApp.class);
        List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
              .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanCustomFilterApp")
                    && !bean.contains("componentScanCustomFilter"))
              .collect(Collectors.toList());

        assertThat(beans.size(), equalTo(1));
        assertThat(beans.get(0), equalTo("elephant"));
    }
}
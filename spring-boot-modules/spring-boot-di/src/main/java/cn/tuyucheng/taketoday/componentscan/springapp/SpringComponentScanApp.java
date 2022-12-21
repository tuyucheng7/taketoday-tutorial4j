package cn.tuyucheng.taketoday.componentscan.springapp;

import cn.tuyucheng.taketoday.componentscan.ExampleBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Rose.class))
// @ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp")
// @ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals")
// @ComponentScan(basePackages = {"cn.tuyucheng.taketoday.componentscan.springapp.animals","cn.tuyucheng.taketoday.componentscan.springapp.flowers"})
// @ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals;cn.tuyucheng.taketoday.componentscan.springapp.flowers")
// @ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals,cn.tuyucheng.taketoday.componentscan.springapp.flowers")
// @ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals cn.tuyucheng.taketoday.componentscan.springapp.flowers")
// @ComponentScan (excludeFilters = @ComponentScan.Filter(type=FilterType.REGEX,pattern="cn\\.tuyucheng\\.taketoday\\.componentscan\\.springapp\\.flowers\\..*"))
public class SpringComponentScanApp {

    private static ApplicationContext applicationContext;

    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }

    public static void main(String[] args) {
        applicationContext = new AnnotationConfigApplicationContext(SpringComponentScanApp.class);

        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
    }
}
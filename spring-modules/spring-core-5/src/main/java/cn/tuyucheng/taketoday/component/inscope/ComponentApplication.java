package cn.tuyucheng.taketoday.component.inscope;

import cn.tuyucheng.taketoday.component.outsidescope.OutsideScopeBeanExample;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan({"cn.tuyucheng.taketoday.component.inscope", "cn.tuyucheng.taketoday.component.scannedscope"})
@Configuration
public class ComponentApplication {

	@Value("${ambiguous-bean}")
	public String ambiguousBean;

	public static void main(String[] args) {
		SpringApplication.run(ComponentApplication.class, args);
	}

	@Bean
	public BeanExample beanExample() {
		return new BeanExample();
	}

	@Bean
	public OutsideScopeBeanExample outsideScopeBeanExample() {
		return new OutsideScopeBeanExample();
	}

	@Bean
	public AmbiguousBean ambiguousBean() {
		if (ambiguousBean.equals("A")) {
			return new BeanImplA();
		} else {
			return new BeanImplB();
		}
	}
}
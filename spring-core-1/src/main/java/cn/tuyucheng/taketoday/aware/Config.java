package cn.tuyucheng.taketoday.aware;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ExcludeFromJacocoGeneratedReport
public class Config {

	@Bean(name = "myCustomBeanName")
	public MyBeanName getMyBeanName() {
		return new MyBeanName();
	}

	@Bean
	public MyBeanFactory getMyBeanFactory() {
		return new MyBeanFactory();
	}
}
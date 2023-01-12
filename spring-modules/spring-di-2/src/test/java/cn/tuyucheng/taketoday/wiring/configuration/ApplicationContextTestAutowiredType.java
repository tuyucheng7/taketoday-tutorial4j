package cn.tuyucheng.taketoday.wiring.configuration;

import cn.tuyucheng.taketoday.dependency.ArbitraryDependency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTestAutowiredType {

	@Bean
	public ArbitraryDependency autowiredFieldDependency() {
		ArbitraryDependency autowiredFieldDependency = new ArbitraryDependency();
		return autowiredFieldDependency;
	}
}

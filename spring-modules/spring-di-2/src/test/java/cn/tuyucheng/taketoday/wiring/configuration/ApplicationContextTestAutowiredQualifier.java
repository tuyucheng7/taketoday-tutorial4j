package cn.tuyucheng.taketoday.wiring.configuration;

import cn.tuyucheng.taketoday.dependency.AnotherArbitraryDependency;
import cn.tuyucheng.taketoday.dependency.ArbitraryDependency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTestAutowiredQualifier {

	@Bean
	public ArbitraryDependency autowiredFieldDependency() {
		ArbitraryDependency autowiredFieldDependency = new ArbitraryDependency();

		return autowiredFieldDependency;
	}

	@Bean
	public ArbitraryDependency anotherAutowiredFieldDependency() {
		ArbitraryDependency anotherAutowiredFieldDependency = new AnotherArbitraryDependency();

		return anotherAutowiredFieldDependency;
	}
}

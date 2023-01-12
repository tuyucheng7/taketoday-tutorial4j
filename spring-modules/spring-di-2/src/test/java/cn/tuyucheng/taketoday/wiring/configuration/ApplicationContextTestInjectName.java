package cn.tuyucheng.taketoday.wiring.configuration;

import cn.tuyucheng.taketoday.dependency.ArbitraryDependency;
import cn.tuyucheng.taketoday.dependency.YetAnotherArbitraryDependency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTestInjectName {

	@Bean
	public ArbitraryDependency yetAnotherFieldInjectDependency() {
		ArbitraryDependency yetAnotherFieldInjectDependency = new YetAnotherArbitraryDependency();
		return yetAnotherFieldInjectDependency;
	}
}

package cn.tuyucheng.taketoday.wiring.configuration;

import cn.tuyucheng.taketoday.dependency.AnotherArbitraryDependency;
import cn.tuyucheng.taketoday.dependency.ArbitraryDependency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTestInjectQualifier {

	@Bean
	public ArbitraryDependency defaultFile() {
		ArbitraryDependency defaultFile = new ArbitraryDependency();
		return defaultFile;
	}

	@Bean
	public ArbitraryDependency namedFile() {
		ArbitraryDependency namedFile = new AnotherArbitraryDependency();
		return namedFile;
	}
}

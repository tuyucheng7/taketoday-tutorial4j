package cn.tuyucheng.taketoday.startup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.startup")
public class SpringStartupConfig {

	@Bean(initMethod = "init")
	public InitMethodExampleBean initMethodExampleBean() {
		return new InitMethodExampleBean();
	}
}
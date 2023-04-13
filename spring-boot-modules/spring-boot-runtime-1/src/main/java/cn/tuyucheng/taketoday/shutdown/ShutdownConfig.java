package cn.tuyucheng.taketoday.shutdown;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.shutdown")
public class ShutdownConfig {

	@Bean
	public TerminateBean getTerminateBean() {
		return new TerminateBean();
	}
}
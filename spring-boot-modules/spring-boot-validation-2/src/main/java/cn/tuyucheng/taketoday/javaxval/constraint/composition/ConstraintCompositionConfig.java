package cn.tuyucheng.taketoday.javaxval.constraint.composition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan({"cn.tuyucheng.taketoday.javaxval.constraint.composition"})
public class ConstraintCompositionConfig {

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean
	public AccountService accountService() {
		return new AccountService();
	}
}
package cn.tuyucheng.taketoday.ex.dataintegrityviolationexception.spring;

import cn.tuyucheng.taketoday.spring.config.PersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@Configuration
@ComponentScan("com.baeldung.ex.dataIntegrityviolationexception.cause1")
@Import(PersistenceConfig.class)
public class Cause1DataContextWithJavaConfig {

	public Cause1DataContextWithJavaConfig() {
		super();
	}

	// beans

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
package cn.tuyucheng.taketoday.spring.jinq.config;

import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class JinqProviderConfiguration {

	@Bean
	@Autowired
	JinqJPAStreamProvider jinqProvider(EntityManagerFactory emf) {
		return new JinqJPAStreamProvider(emf);
	}
}

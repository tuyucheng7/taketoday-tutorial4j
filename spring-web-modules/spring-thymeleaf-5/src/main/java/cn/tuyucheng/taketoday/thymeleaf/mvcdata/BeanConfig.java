package cn.tuyucheng.taketoday.thymeleaf.mvcdata;

import cn.tuyucheng.taketoday.thymeleaf.mvcdata.repository.EmailData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
	@Bean
	public EmailData emailData() {
		return new EmailData();
	}
}
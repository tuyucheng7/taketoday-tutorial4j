package cn.tuyucheng.taketoday.springbean.naming.configuration;

import cn.tuyucheng.taketoday.springbean.naming.service.AuditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

	@Bean
	public AuditService audit() {
		return new AuditService();
	}
}

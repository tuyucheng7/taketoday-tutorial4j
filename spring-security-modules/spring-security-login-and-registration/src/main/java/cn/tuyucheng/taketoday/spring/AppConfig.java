package cn.tuyucheng.taketoday.spring;

import cn.tuyucheng.taketoday.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	// beans

	@Bean
	public ActiveUserStore activeUserStore() {
		return new ActiveUserStore();
	}

}
package cn.tuyucheng.taketoday.springintegration.config;

import cn.tuyucheng.taketoday.springintegration.dao.UserManagementDAO;
import cn.tuyucheng.taketoday.springintegration.dao.UserManagementDAOImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCoreConfig {

	@Bean
	public UserManagementDAO userManagementDAO() {
		return new UserManagementDAOImpl();
	}
}

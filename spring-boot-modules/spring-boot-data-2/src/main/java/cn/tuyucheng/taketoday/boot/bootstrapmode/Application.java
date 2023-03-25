package cn.tuyucheng.taketoday.boot.bootstrapmode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	AsyncTaskExecutor delayedTaskExecutor() {
		return new ThreadPoolTaskExecutor() {
			@Override
			public <T> Future<T> submit(Callable<T> task) {
				return super.submit(() -> {
					Thread.sleep(5000);
					return task.call();
				});
			}
		};
	}

	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, AsyncTaskExecutor delayedTaskExecutor) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPackagesToScan("cn.tuyucheng.taketoday.boot.bootstrapmode");
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.setDataSource(dataSource);
		factory.setBootstrapExecutor(delayedTaskExecutor);
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		factory.setJpaPropertyMap(properties);
		return factory;
	}
}


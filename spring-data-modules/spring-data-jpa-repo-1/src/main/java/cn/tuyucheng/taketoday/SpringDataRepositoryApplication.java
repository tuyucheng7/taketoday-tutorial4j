package cn.tuyucheng.taketoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import cn.tuyucheng.taketoday.boot.daos.impl.ExtendedRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = ExtendedRepositoryImpl.class)
public class SpringDataRepositoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataRepositoryApplication.class, args);
	}
}
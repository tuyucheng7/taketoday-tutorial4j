package cn.tuyucheng.taketoday.spring.data.jpa.naturalid;

import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.repository.NaturalIdRepositoryImpl;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryBaseClass = NaturalIdRepositoryImpl.class)
public class NaturalIdRepositoryConfig {
}
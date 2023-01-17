package cn.tuyucheng.taketoday.pattern.hexagonal.config;

import cn.tuyucheng.taketoday.pattern.hexagonal.persistence.MongoRepoEx;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = MongoRepoEx.class)
public class MongoConfig {
}

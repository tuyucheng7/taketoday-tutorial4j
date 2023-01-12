package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.configuration;

import cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.repository.mongo.SpringDataMongoOrderRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoOrderRepository.class)
public class MongoDBConfiguration {
}

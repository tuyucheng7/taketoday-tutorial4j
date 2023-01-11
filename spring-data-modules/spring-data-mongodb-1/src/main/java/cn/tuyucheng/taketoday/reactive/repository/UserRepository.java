package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface UserRepository extends ReactiveMongoRepository<User, String> {
}

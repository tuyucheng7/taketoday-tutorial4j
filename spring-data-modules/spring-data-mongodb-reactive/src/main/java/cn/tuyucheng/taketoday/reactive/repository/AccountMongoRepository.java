package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.reactive.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AccountMongoRepository extends ReactiveMongoRepository<Account, String> {
}

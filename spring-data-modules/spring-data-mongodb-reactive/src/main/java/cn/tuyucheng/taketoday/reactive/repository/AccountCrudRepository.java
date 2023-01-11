package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.reactive.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountCrudRepository extends ReactiveCrudRepository<Account, String> {

	public Flux<Account> findAllByValue(Double value);

	public Mono<Account> findFirstByOwner(Mono<String> owner);
}

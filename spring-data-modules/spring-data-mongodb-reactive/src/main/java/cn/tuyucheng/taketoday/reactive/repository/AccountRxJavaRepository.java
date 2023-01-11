package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.reactive.model.Account;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRxJavaRepository extends RxJava2CrudRepository<Account, String> {

	public Observable<Account> findAllByValue(Double value);

	public Single<Account> findFirstByOwner(Single<String> owner);
}

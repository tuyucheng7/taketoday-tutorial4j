package cn.tuyucheng.taketoday.schemageneration.repository;

import cn.tuyucheng.taketoday.schemageneration.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
	Account findByName(String name);
}
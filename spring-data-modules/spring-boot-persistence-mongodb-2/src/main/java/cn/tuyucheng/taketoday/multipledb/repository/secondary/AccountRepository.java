package cn.tuyucheng.taketoday.multipledb.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.multipledb.model.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

	Account findByAccountDomain(String account);
}
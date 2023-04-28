package cn.tuyucheng.taketoday.springvault;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, String> {

}

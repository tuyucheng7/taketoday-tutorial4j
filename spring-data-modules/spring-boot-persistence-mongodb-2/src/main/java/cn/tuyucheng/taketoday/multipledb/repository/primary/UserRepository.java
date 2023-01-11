package cn.tuyucheng.taketoday.multipledb.repository.primary;

import cn.tuyucheng.taketoday.multipledb.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);
}
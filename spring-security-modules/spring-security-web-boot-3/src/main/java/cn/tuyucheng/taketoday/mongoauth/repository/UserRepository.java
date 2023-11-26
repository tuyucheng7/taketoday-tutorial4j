package cn.tuyucheng.taketoday.mongoauth.repository;

import cn.tuyucheng.taketoday.mongoauth.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

   @Query("{username:'?0'}")
   User findUserByUsername(String username);

}

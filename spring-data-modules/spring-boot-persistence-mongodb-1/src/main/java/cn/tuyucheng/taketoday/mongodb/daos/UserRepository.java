package cn.tuyucheng.taketoday.mongodb.daos;


import cn.tuyucheng.taketoday.mongodb.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, Long> {

}
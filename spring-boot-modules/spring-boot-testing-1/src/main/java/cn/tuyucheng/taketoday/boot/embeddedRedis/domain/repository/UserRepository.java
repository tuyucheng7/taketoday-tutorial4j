package cn.tuyucheng.taketoday.boot.embeddedRedis.domain.repository;

import cn.tuyucheng.taketoday.boot.embeddedRedis.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

}
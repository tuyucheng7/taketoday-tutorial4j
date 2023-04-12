package cn.tuyucheng.taketoday.startdbwithawssecretsmanager.repository;

import cn.tuyucheng.taketoday.startdbwithawssecretsmanager.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
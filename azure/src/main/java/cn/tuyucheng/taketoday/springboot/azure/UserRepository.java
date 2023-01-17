package cn.tuyucheng.taketoday.springboot.azure;

import org.springframework.data.repository.CrudRepository;

/**
 * @author tuyucheng
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
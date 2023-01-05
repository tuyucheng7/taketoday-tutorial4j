package cn.tuyucheng.taketoday.repositories;

import cn.tuyucheng.taketoday.vavr.User;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.springframework.data.repository.Repository;

public interface VavrUserRepository extends Repository<User, Long> {

	Option<User> findById(long id);

	Seq<User> findByName(String name);

	User save(User user);
}
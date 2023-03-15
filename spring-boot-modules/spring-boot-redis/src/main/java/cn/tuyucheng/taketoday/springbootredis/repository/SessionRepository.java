package cn.tuyucheng.taketoday.springbootredis.repository;

import cn.tuyucheng.taketoday.springbootredis.model.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, String> {
}
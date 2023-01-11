package cn.tuyucheng.taketoday.zoneddatetime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.zoneddatetime.model.Action;

public interface ActionRepository extends MongoRepository<Action, String> {
}
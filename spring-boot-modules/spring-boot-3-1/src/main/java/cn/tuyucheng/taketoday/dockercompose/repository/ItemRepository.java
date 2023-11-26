package cn.tuyucheng.taketoday.dockercompose.repository;

import cn.tuyucheng.taketoday.dockercompose.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {

   @Query("{name:'?0'}")
   Item findItemByName(String name);

   @Query(value = "{category:'?0'}")
   List<Item> findAllByCategory(String category);

   long count();
}
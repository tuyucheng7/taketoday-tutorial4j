package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.Item;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomItemRepository {

   void deleteCustom(Item entity);

   Item findItemById(Long id);

   void findThenDelete(Long id);
}
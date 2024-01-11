package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.ItemType;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomItemTypeRepository {

   void deleteCustom(ItemType entity);

   void findThenDelete(Long id);
}
package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.MerchandiseEntity;
import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<MerchandiseEntity, Long> {
}
package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.repository.CrudRepository;

import cn.tuyucheng.taketoday.boot.domain.MerchandiseEntity;

public interface InventoryRepository extends CrudRepository<MerchandiseEntity, Long> {
}
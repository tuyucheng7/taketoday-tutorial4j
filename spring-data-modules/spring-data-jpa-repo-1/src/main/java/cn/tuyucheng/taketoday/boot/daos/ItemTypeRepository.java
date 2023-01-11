package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.boot.domain.ItemType;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long>, CustomItemTypeRepository, CustomItemRepository {
}
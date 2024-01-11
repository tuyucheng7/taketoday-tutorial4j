package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long>, CustomItemTypeRepository, CustomItemRepository {
}
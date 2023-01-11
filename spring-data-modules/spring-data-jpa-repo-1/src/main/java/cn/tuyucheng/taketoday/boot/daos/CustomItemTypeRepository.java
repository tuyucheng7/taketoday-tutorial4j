package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.boot.domain.ItemType;

@Repository
public interface CustomItemTypeRepository {

	void deleteCustom(ItemType entity);

	void findThenDelete(Long id);
}
package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.boot.domain.Item;

@Repository
public interface CustomItemRepository {

	void deleteCustom(Item entity);

	Item findItemById(Long id);

	void findThenDelete(Long id);
}
package cn.tuyucheng.taketoday.boot.daos.impl;

import cn.tuyucheng.taketoday.boot.daos.CustomItemRepository;
import cn.tuyucheng.taketoday.boot.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class CustomItemRepositoryImpl implements CustomItemRepository {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void deleteCustom(Item item) {
		entityManager.remove(item);
	}

	@Override
	public Item findItemById(Long id) {
		return entityManager.find(Item.class, id);
	}

	@Override
	public void findThenDelete(Long id) {
		final Item item = entityManager.find(Item.class, id);
		entityManager.remove(item);
	}
}
package cn.tuyucheng.taketoday.boot.daos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.boot.daos.CustomItemTypeRepository;
import cn.tuyucheng.taketoday.boot.domain.ItemType;

import jakarta.persistence.EntityManager;

@Repository
public class CustomItemTypeRepositoryImpl implements CustomItemTypeRepository {

   @Autowired
   private EntityManager entityManager;

   @Override
   public void deleteCustom(ItemType itemType) {
      entityManager.remove(itemType);
   }

   @Override
   public void findThenDelete(Long id) {
      ItemType itemTypeToDelete = entityManager.find(ItemType.class, id);
      entityManager.remove(itemTypeToDelete);
   }
}
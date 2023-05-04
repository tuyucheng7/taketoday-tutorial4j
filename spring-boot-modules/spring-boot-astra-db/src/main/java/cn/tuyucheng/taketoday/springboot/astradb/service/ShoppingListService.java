package cn.tuyucheng.taketoday.springboot.astradb.service;

import cn.tuyucheng.taketoday.springboot.astradb.entity.ShoppingList;
import cn.tuyucheng.taketoday.springboot.astradb.repository.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class ShoppingListService {

   @Autowired
   private ShoppingListRepository shoppingListRepository;

   public List<ShoppingList> findAll() {
      return shoppingListRepository.findAll(CassandraPageRequest.first(10)).toList();
   }

   public ShoppingList findByTitle(String title) {
      return shoppingListRepository.findByTitleAllIgnoreCase(title);
   }

   @PostConstruct
   public void insert() {
      ShoppingList groceries = new ShoppingList("Groceries");
      groceries.setItems(Arrays.asList("Bread", "Milk, Apples"));

      ShoppingList pharmacy = new ShoppingList("Pharmacy");
      pharmacy.setCompleted(true);
      pharmacy.setItems(Arrays.asList("Nappies", "Suncream, Aspirin"));

      shoppingListRepository.save(groceries);
      shoppingListRepository.save(pharmacy);
   }
}
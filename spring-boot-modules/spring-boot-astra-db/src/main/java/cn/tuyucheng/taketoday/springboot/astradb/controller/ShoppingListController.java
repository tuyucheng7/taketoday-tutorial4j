package cn.tuyucheng.taketoday.springboot.astradb.controller;

import cn.tuyucheng.taketoday.springboot.astradb.entity.ShoppingList;
import cn.tuyucheng.taketoday.springboot.astradb.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/shopping")
public class ShoppingListController {

   @Autowired
   private ShoppingListService shoppingListService;

   @GetMapping("/list")
   public List<ShoppingList> findAll() {
      return shoppingListService.findAll();
   }
}
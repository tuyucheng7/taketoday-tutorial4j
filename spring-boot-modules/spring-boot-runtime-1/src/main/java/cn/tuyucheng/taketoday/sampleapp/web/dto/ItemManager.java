package cn.tuyucheng.taketoday.sampleapp.web.dto;

public class ItemManager {

   public static Item getById(final int id) {
      return new Item(2, "book", "John");
   }
}
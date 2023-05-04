package cn.tuyucheng.taketoday.springboot.astradb.entity;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table
public class ShoppingList {

   @PrimaryKey
   @CassandraType(type = Name.UUID)
   private UUID uid = UUID.randomUUID();

   private String title;
   private boolean completed = false;

   private List<String> items = new ArrayList<>();

   public ShoppingList() {
   }

   public ShoppingList(String title) {
      this.title = title;
   }

   public UUID getUid() {
      return uid;
   }

   public void setUid(UUID uid) {
      this.uid = uid;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean isCompleted() {
      return completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

   public List<String> getItems() {
      return items;
   }

   public void setItems(List<String> items) {
      this.items = items;
   }
}
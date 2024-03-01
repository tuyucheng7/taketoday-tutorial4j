package cn.tuyucheng.taketoday.skipselectbeforeinsert.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Task {

   @Id
   private Integer id;
   private String description;

   public Integer getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
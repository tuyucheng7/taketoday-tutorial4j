package cn.tuyucheng.taketoday.springpagination.model;

import javax.persistence.*;

@Entity
public class Subject {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(nullable = false)
   private String name;

   public Subject() {
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
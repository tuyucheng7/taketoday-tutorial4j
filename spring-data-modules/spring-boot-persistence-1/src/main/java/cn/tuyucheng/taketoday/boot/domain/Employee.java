package cn.tuyucheng.taketoday.boot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {

   @Id
   @GeneratedValue
   private int id;
   private String name;
   private String title;

   public Employee() {
   }

   public Employee(String name, String title) {
      this.name = name;
      this.title = title;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public int getId() {
      return id;
   }
}
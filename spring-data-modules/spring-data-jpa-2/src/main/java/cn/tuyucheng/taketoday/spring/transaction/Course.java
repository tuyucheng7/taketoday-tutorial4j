package cn.tuyucheng.taketoday.spring.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "course")
public class Course implements Serializable {

   private static final long serialVersionUID = 1L;

   @Id
   @Column(name = "id")
   private Long id;

   public Course() {
   }

   public Course(Long id) {
      this.id = id;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }
}
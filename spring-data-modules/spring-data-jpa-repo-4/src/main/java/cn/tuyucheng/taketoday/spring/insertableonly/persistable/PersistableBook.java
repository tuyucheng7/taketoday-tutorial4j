package cn.tuyucheng.taketoday.spring.insertableonly.persistable;

import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PersistableBook implements Persistable<Long> {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String title;

   public PersistableBook() {
   }

   public PersistableBook(String title) {
      this.title = title;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Long getId() {
      return id;
   }

   @Override
   public boolean isNew() {
      return true;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getTitle() {
      return title;
   }
}
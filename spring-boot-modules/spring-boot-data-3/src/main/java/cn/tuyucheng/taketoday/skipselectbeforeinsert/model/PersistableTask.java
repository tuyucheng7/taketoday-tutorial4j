package cn.tuyucheng.taketoday.skipselectbeforeinsert.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PersistableTask implements Persistable<Integer> {

   @Id
   private int id;
   private String description;

   @Transient
   private boolean isNew = true;

   public void setNew(boolean isNew) {
      this.isNew = isNew;
   }

   @Override
   public Integer getId() {
      return id;
   }

   @Override
   public boolean isNew() {
      return isNew;
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
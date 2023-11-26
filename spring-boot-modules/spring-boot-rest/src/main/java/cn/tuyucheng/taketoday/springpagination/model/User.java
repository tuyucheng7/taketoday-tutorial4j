package cn.tuyucheng.taketoday.springpagination.model;

import javax.persistence.*;

@Entity
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   private String name;

   @OneToOne
   Preference preference;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Preference getPreference() {
      return preference;
   }

   public void setPreference(Preference preference) {
      this.preference = preference;
   }
}

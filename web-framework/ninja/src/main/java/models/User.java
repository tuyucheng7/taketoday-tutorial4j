package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class User {

   @NotNull
   public String firstName;
   public String email;
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   Long id;

   public String toString() {
      return firstName + " : " + email;
   }

}

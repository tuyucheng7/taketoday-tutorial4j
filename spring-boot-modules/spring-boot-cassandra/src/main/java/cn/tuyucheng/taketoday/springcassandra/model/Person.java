package cn.tuyucheng.taketoday.springcassandra.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Table
public class Person {

   @PrimaryKey
   private UUID id;

   private String firstName;

   private String lastName;

   private LocalDate birthDate;

   private LocalDateTime lastVisitedDate;

   private Date lastPurchasedDate;

   public Person(UUID id, String firstName, String lastName) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
   }

   public UUID getId() {
      return id;
   }

   public void setId(UUID id) {
      this.id = id;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public LocalDate getBirthDate() {
      return birthDate;
   }

   public void setBirthDate(LocalDate birthDate) {
      this.birthDate = birthDate;
   }

   public LocalDateTime getLastVisitedDate() {
      return lastVisitedDate;
   }

   public void setLastVisitedDate(LocalDateTime lastVisitedDate) {
      this.lastVisitedDate = lastVisitedDate;
   }

   public Date getLastPurchasedDate() {
      return lastPurchasedDate;
   }

   public void setLastPurchasedDate(Date lastPurchasedDate) {
      this.lastPurchasedDate = lastPurchasedDate;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Person person = (Person) o;
      return id.equals(person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(birthDate, person.birthDate) && Objects.equals(lastVisitedDate, person.lastVisitedDate) && Objects.equals(
            lastPurchasedDate, person.lastPurchasedDate);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, firstName, lastName, birthDate, lastVisitedDate, lastPurchasedDate);
   }
}
package cn.tuyucheng.taketoday.jasperreports.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Person implements Serializable {

   private static final long serialVersionUID = 6682316458433091580L;
   @Id
   private Integer id;
   @Column(name = "first_name")
   private String firstName;
   @Column(name = "last_name")
   private String lastName;
   private String pesel;
   private int age;

   public Person() {

   }

   public Person(Integer id, String firstName, String lastName, String pesel, int age) {
      super();
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.pesel = pesel;
      this.age = age;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
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

   public String getPesel() {
      return pesel;
   }

   public void setPesel(String pesel) {
      this.pesel = pesel;
   }

   public int getAge() {
      return age;
   }

   public void setAge(int age) {
      this.age = age;
   }
}
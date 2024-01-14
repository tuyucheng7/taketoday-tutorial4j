package cn.tuyucheng.taketoday.compareobjects;

import java.util.Objects;

public class PhoneNumber {
   private String type;
   private String number;

   public PhoneNumber(String type, String number) {
      this.type = type;
      this.number = number;
   }

   public String getType() {
      return type;
   }

   public String getNumber() {
      return number;
   }

   @Override
   public String toString() {
      return "PhoneNumber{" + "type='" + type + '\'' + ", number='" + number + '\'' + '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      PhoneNumber that = (PhoneNumber) o;
      return Objects.equals(number, that.number);
   }

   @Override
   public int hashCode() {
      return Objects.hash(number);
   }
}

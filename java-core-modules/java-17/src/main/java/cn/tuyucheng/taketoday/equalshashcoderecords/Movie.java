package cn.tuyucheng.taketoday.equalshashcoderecords;

import java.util.Objects;

record Movie(String name, Integer yearOfRelease, String distributor) {

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }
      if (other == null) {
         return false;
      }
      Movie movie = (Movie) other;
      return movie.name.equals(this.name) && movie.yearOfRelease.equals(this.yearOfRelease);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, yearOfRelease);
   }
}
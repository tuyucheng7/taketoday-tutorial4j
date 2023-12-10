package cn.tuyucheng.taketoday.entitydtodifferences.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserResponseDto {

   @JsonProperty("ID")
   private final Long id;

   @JsonProperty("FIRST_NAME")
   private final String firstName;

   @JsonProperty("LAST_NAME")
   private final String lastName;

   @JsonProperty("BOOKS")
   private final List<BookDto> books;

   // Default constructor for Jackson deserialization
   public UserResponseDto() {
      this(null, null, null, null);
   }

   public UserResponseDto(Long id, String firstName, String lastName, List<BookDto> books) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.books = books;
   }

   public Long getId() {
      return id;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public List<BookDto> getBooks() {
      return books;
   }
}
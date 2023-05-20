package cn.tuyucheng.taketoday.spring.reactive.customexception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
   private Long id;
   private String firstName;
   private String lastName;
   private String email;
   private LocalDate dateOfBirth;
}
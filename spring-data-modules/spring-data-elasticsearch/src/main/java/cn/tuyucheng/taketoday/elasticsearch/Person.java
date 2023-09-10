package cn.tuyucheng.taketoday.elasticsearch;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Person {

   private int age;
   private String fullName;
   private Date dateOfBirth;
}
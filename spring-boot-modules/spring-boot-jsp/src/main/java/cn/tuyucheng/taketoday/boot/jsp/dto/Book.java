package cn.tuyucheng.taketoday.boot.jsp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {
   private String isbn;
   private String name;
   private String author;
}
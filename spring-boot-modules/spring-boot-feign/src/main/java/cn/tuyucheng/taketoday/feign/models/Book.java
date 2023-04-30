package cn.tuyucheng.taketoday.feign.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	private String isbn;
	private String author;
	private String title;
	private String synopsis;
	private String language;
}
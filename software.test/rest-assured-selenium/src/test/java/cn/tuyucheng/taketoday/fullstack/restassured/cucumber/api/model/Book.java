package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	private String isbn;
	private String title;
	private String subTitle;
	private String author;
	private String publish_date;
	private String publisher;
	private int pages;
	private String description;
	private String website;
}
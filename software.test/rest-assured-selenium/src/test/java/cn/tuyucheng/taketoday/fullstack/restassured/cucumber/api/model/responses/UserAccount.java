package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

	private String userId;
	private String username;
	private List<Book> books;
}
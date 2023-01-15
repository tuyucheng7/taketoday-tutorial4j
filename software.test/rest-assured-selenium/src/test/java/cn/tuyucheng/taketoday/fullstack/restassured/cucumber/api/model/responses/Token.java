package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

	private String token;
	private String expires;
	private String status;
	private String result;
}
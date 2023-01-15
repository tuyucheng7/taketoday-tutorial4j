package cn.tuyucheng.taketoday.fullstack.restassured.authentication.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationBean {

	private String message;

	@Override
	public String toString() {
		return String.format("HelloWorldBean [message=%s]", message);
	}
}
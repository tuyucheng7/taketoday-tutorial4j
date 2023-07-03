package cn.tuyucheng.taketoday.optionalreturntype;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OptionalToJsonExample {
	public static void main(String[] args) {
		UserOptional user = new UserOptional();
		user.setUserId(1L);
		user.setFirstName("Tu Yucheng");

		ObjectMapper om = new ObjectMapper();
		try {
			System.out.print("user in json is:" + om.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

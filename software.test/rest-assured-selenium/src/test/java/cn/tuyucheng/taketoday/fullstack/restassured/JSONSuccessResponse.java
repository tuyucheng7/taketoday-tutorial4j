package cn.tuyucheng.taketoday.fullstack.restassured;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JSONSuccessResponse {

	private Integer statusCode;
	private String message;
}
package cn.tuyucheng.taketoday.fullstack.restassured.courseapp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CourseVO {

	private String message;
	private int statusCode;

	public static CourseVO buildSuccessVO() {
		return new CourseVO("saved successful", 200);
	}
}
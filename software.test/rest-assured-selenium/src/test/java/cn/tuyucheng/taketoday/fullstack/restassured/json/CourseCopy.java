package cn.tuyucheng.taketoday.fullstack.restassured.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCopy {

	private Long id;
	private String username;
	private String description;
}
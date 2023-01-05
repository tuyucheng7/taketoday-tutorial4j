package cn.tuyucheng.taketoday.validations.functional.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnnotatedRequestEntity {
	@NotNull
	private String user;

	@NotNull
	@Size(min = 4, max = 7)
	private String password;
}
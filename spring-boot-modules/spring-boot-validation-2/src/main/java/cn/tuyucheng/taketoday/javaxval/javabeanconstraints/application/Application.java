package cn.tuyucheng.taketoday.javaxval.javabeanconstraints.application;

import cn.tuyucheng.taketoday.javaxval.javabeanconstraints.entities.UserNotBlank;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class Application {

	public static void main(String[] args) {
		Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();
		UserNotBlank user = new UserNotBlank(" ");
		validator.validate(user)
			.stream()
			.forEach(violation -> System.out.println(violation.getMessage()));
	}
}

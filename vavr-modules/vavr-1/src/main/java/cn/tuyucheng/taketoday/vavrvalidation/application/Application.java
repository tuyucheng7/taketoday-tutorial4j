package cn.tuyucheng.taketoday.vavrvalidation.application;

import cn.tuyucheng.taketoday.vavrvalidation.model.User;
import cn.tuyucheng.taketoday.vavrvalidation.validator.UserValidator;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

public class Application {

	public static void main(String[] args) {
		UserValidator userValidator = new UserValidator();
		Validation<Seq<String>, User> validation = userValidator.validateUser("John", "john@domain.com");

		// process validation results here
	}
}
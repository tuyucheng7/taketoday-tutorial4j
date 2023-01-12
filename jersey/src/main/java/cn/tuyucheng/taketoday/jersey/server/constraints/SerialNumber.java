package cn.tuyucheng.taketoday.jersey.server.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SerialNumber.Validator.class})
public @interface SerialNumber {

	String message()

		default "Fruit serial number is not valid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class Validator implements ConstraintValidator<SerialNumber, String> {
		@Override
		public void initialize(final SerialNumber serial) {
		}

		@Override
		public boolean isValid(final String serial, final ConstraintValidatorContext constraintValidatorContext) {
			final String serialNumRegex = "^\\d{3}-\\d{3}-\\d{4}$";
			return Pattern.matches(serialNumRegex, serial);
		}
	}
}

package cn.tuyucheng.taketoday.javaxval.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import cn.tuyucheng.taketoday.javaxval.enums.constraints.EnumNamePattern;

public class EnumNamePatternValidator implements ConstraintValidator<EnumNamePattern, Enum<?>> {
	private Pattern pattern;

	@Override
	public void initialize(EnumNamePattern constraintAnnotation) {
		try {
			pattern = Pattern.compile(constraintAnnotation.regexp());
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException("Given regex is invalid", e);
		}
	}

	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		Matcher m = pattern.matcher(value.name());
		return m.matches();
	}
}

package cn.tuyucheng.taketoday.validations.functional.validators;

import cn.tuyucheng.taketoday.validations.functional.model.OtherEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class OtherEntityValidator implements Validator {

	private static final int MIN_ITEM_QUANTITY = 1;

	@Override
	public boolean supports(Class<?> clazz) {
		return OtherEntity.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "item", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "quantity", "field.required");
		OtherEntity request = (OtherEntity) target;
		if (request.getQuantity() != null && request.getQuantity() < MIN_ITEM_QUANTITY) {
			errors.rejectValue("quantity", "field.min.length", new Object[]{MIN_ITEM_QUANTITY}, "There must be at least one item");
		}
	}
}
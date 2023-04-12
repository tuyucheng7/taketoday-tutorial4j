package cn.tuyucheng.taketoday.javaxval.enums;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import cn.tuyucheng.taketoday.javaxval.enums.constraints.CustomerTypeSubset;
import cn.tuyucheng.taketoday.javaxval.enums.demo.CustomerType;

public class CustomerTypeSubSetValidator implements ConstraintValidator<CustomerTypeSubset, CustomerType> {
	private CustomerType[] subset;

	@Override
	public void initialize(CustomerTypeSubset constraint) {
		this.subset = constraint.anyOf();
	}

	@Override
	public boolean isValid(CustomerType value, ConstraintValidatorContext context) {
		return value == null || Arrays.asList(subset)
			.contains(value);
	}
}

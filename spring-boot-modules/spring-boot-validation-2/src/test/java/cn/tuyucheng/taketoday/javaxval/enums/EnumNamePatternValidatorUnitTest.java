package cn.tuyucheng.taketoday.javaxval.enums;

import cn.tuyucheng.taketoday.javaxval.enums.demo.Customer;
import cn.tuyucheng.taketoday.javaxval.enums.demo.CustomerType;
import cn.tuyucheng.taketoday.javaxval.enums.demo.CustomerUnitTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumNamePatternValidatorUnitTest {

	private static Validator validator;

	@BeforeClass
	public static void setupValidatorInstance() {
		validator = Validation.buildDefaultValidatorFactory()
			.getValidator();
	}

	@Test
	public void whenEnumMatchesRegex_thenShouldNotReportConstraintViolations() {
		Customer customer = new Customer.Builder().withCustomerTypeMatchesPattern(CustomerType.DEFAULT)
			.build();
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertThat(violations.isEmpty()).isTrue();
	}

	@Test
	public void whenEnumNull_thenShouldNotReportConstraintViolations() {
		Customer customer = new Customer.Builder().withCustomerTypeMatchesPattern(null)
			.build();
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertThat(violations.isEmpty()).isTrue();
	}

	@Test
	public void whenEnumDoesNotMatchRegex_thenShouldGiveOccurrenceOfConstraintViolations() {
		Customer customer = new Customer.Builder().withCustomerTypeMatchesPattern(CustomerType.OLD)
			.build();
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertThat(violations.size()).isEqualTo(1);

		assertThat(violations).anyMatch(CustomerUnitTest.havingPropertyPath("customerTypeMatchesPattern")
			.and(CustomerUnitTest.havingMessage("must match \"NEW|DEFAULT\"")));
	}
}
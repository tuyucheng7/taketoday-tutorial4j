package cn.tuyucheng.taketoday.tostring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerPrimitiveToStringUnitTest {

	private static final String CUSTOMER_PRIMITIVE_TO_STRING = "Customer [balance=110, getFirstName()=Rajesh, getLastName()=Bhojwani]";

	@Test
	public void givenPrimitive_whenToString_thenCustomerDetails() {
		CustomerPrimitiveToString customer = new CustomerPrimitiveToString();
		customer.setFirstName("Rajesh");
		customer.setLastName("Bhojwani");
		customer.setBalance(110);

		assertEquals(CUSTOMER_PRIMITIVE_TO_STRING, customer.toString());
	}
}

package cn.tuyucheng.taketoday.creational.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderPatternUnitTest {
	@Test
	void whenCreatingObjectThroughBuilder_thenObjectValid() {
		BankAccount newAccount = new BankAccount
			.BankAccountBuilder("Jon", "22738022275")
			.withEmail("jon@example.com")
			.wantNewsletter(true)
			.build();

		assertEquals("Jon", newAccount.getName());
		assertEquals("22738022275", newAccount.getAccountNumber());
		assertEquals("jon@example.com", newAccount.getEmail());
		assertTrue(newAccount.isNewsletter());
	}

	@Test
	void whenSkippingOptionalParameters_thenObjectValid() {
		BankAccount newAccount = new BankAccount
			.BankAccountBuilder("Jon", "22738022275")
			.build();

		assertEquals("Jon", newAccount.getName());
		assertEquals("22738022275", newAccount.getAccountNumber());
		assertNull(newAccount.getEmail());
		assertFalse(newAccount.isNewsletter());
	}
}
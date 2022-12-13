package cn.tuyucheng.taketoday.groupedvsmultipleassertions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupedvsMultipleAssertionsUnitTest {

	@Disabled("Failed assertions")
	@Test
	void givenAssertAll_whenSingleAssertStatementFails_thenRestWillStillBeExecuted() {
		User user = new User("tuyucheng", "support@tuyucheng.com", false);
		assertAll("Grouped Assertions of User",
			() -> assertEquals("admin", user.getUsername(), "Username should be admin"),
			() -> assertEquals("admin@tuyucheng.com", user.getEmail(), "Email should be admin@tuyucheng.com"),
			() -> assertTrue(user.getActivated(), "User should be activated")
		);
	}

	@Disabled("Failed assertions")
	@Test
	void givenMultipleAssertions_whenSingleAssertStatementFails_thenRestWillNotBeExecuted() {
		User user = new User("tuyucheng", "support@tuyucheng.com", false);
		assertEquals("admin", user.getUsername(), "Username should be admin");
		assertEquals("admin@tuyucheng.com", user.getEmail(), "Email should be admin@tuyucheng.com");
		assertTrue(user.getActivated(), "User should be activated");
	}
}
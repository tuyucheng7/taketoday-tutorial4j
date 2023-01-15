package cn.tuyucheng.taketoday.java8;

import cn.tuyucheng.taketoday.java_8_features.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Java8MethodReferenceUnitTest {

	private List<String> list;

	@BeforeEach
	void init() {
		list = new ArrayList<>();
		list.add("One");
		list.add("OneAndOnly");
		list.add("Derek");
		list.add("Change");
		list.add("factory");
		list.add("justBefore");
		list.add("Italy");
		list.add("Italy");
		list.add("Thursday");
		list.add("");
		list.add("");
	}

	@Test
	void checkStaticMethodReferences_whenWork_thenCorrect() {
		List<User> users = new ArrayList<>();
		users.add(new User());
		users.add(new User());
		boolean isReal = users.stream().anyMatch(User::isRealUser);
		boolean isRealRef = users.stream().anyMatch(User::isRealUser);

		assertTrue(isReal);
		assertTrue(isRealRef);
	}

	@Test
	void checkInstanceMethodReferences_whenWork_thenCorrect() {
		User user = new User();
		boolean isLegalName = list.stream().anyMatch(user::isLegalName);

		assertTrue(isLegalName);
	}

	@Test
	void checkParticularTypeReferences_whenWork_thenCorrect() {
		long count = list.stream().filter(String::isEmpty).count();

		assertEquals(count, 2);
	}

	@Test
	void checkConstructorReferences_whenWork_thenCorrect() {
		Stream<User> stream = list.stream().map(User::new);
		List<User> userList = stream.collect(Collectors.toList());
		assertEquals(userList.size(), list.size());
		assertNotNull(userList.get(0));
	}
}
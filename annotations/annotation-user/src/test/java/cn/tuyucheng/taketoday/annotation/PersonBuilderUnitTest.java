package cn.tuyucheng.taketoday.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonBuilderUnitTest {

	@Test
	void whenBuildPersonWithBuilder_thenObjectHasPropertyValues() {
		Person person = new PersonBuilder().setAge(25).setName("John").build();

		assertEquals(25, person.getAge());
		assertEquals("John", person.getName());
	}
}
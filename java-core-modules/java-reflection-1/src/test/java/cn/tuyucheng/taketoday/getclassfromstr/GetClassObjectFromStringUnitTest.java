package cn.tuyucheng.taketoday.getclassfromstr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetClassObjectFromStringUnitTest {
	@Test
	void givenQualifiedClsName_whenUsingClassForName_shouldGetExpectedClassObject() throws ReflectiveOperationException {
		Class cls = Class.forName("cn.tuyucheng.taketoday.getclassfromstr.MyNiceClass");
		assertNotNull(cls);

		MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
		assertNotNull(myNiceObject);
		assertEquals("Hi there, I wish you all the best!", myNiceObject.greeting());
	}

	@Test
	void givenSimpleName_whenUsingClassForName_shouldGetExpectedException() {
		assertThrows(ClassNotFoundException.class, () -> Class.forName("MyNiceClass"));
	}
}
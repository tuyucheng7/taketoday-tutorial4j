package cn.tuyucheng.taketoday.typeinference;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeInferenceUnitTest {

	@Test
	void givenNoTypeInference_whenInvokingGenericMethodsWithTypeParameters_ObjectsAreCreated() {
		// Without type inference. code is verbose.
		Map<String, Map<String, String>> mapOfMaps = new HashMap<String, Map<String, String>>();
		List<String> strList = Collections.<String>emptyList();
		List<Integer> intList = Collections.<Integer>emptyList();

		assertTrue(mapOfMaps.isEmpty());
		assertTrue(strList.isEmpty());
		assertTrue(intList.isEmpty());
	}

	@Test
	void givenTypeInference_whenInvokingGenericMethodsWithoutTypeParameters_ObjectsAreCreated() {
		// With type inference. code is concise.
		List<String> strListInferred = Collections.emptyList();
		List<Integer> intListInferred = Collections.emptyList();

		assertTrue(strListInferred.isEmpty());
		assertTrue(intListInferred.isEmpty());
	}

	@Test
	void givenJava7_whenInvokingConstructorWithoutTypeParameters_ObjectsAreCreated() {
		// Type Inference for constructor using diamond operator.
		Map<String, Map<String, String>> mapOfMapsInferred = new HashMap<>();

		assertTrue(mapOfMapsInferred.isEmpty());
		assertEquals("public class java.util.HashMap<K,V>", mapOfMapsInferred.getClass()
			.toGenericString());
	}

	public static <T> List<T> add(List<T> list, T a, T b) {
		list.add(a);
		list.add(b);
		return list;
	}

	@Test
	void givenGenericMethod_WhenInvokedWithoutExplicitTypes_TypesAreInferred() {
		// Generalized target-type inference
		List<String> strListGeneralized = add(new ArrayList<>(), "abc", "def");
		List<Integer> intListGeneralized = add(new ArrayList<>(), 1, 2);
		List<Number> numListGeneralized = add(new ArrayList<>(), 1, 2.0);

		assertEquals("public class java.util.ArrayList<E>", strListGeneralized.getClass()
			.toGenericString());
		assertFalse(intListGeneralized.isEmpty());
		assertEquals(2, numListGeneralized.size());
	}

	@Test
	void givenLambdaExpressions_whenParameterTypesNotSpecified_ParameterTypesAreInferred() {
		// Type Inference and Lambda Expressions.
		List<Integer> intList = Arrays.asList(5, 3, 4, 2, 1);
		Collections.sort(intList, (a, b) -> {
			assertEquals("java.lang.Integer", a.getClass().getName());
			return a.compareTo(b);
		});
		assertEquals("[1, 2, 3, 4, 5]", Arrays.toString(intList.toArray()));

		List<String> strList = Arrays.asList("Red", "Blue", "Green");
		Collections.sort(strList, (a, b) -> {
			assertEquals("java.lang.String", a.getClass().getName());
			return a.compareTo(b);
		});
		assertEquals("[Blue, Green, Red]", Arrays.toString(strList.toArray()));
	}
}
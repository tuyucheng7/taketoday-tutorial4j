package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.Observable;

import static cn.tuyucheng.taketoday.rxjava.ObservableImpl.getTitle;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ObservableUnitTest {

	private String result = "";

	@Test
	void givenString_whenJustAndSubscribe_thenEmitsSingleItem() {
		Observable<String> observable = Observable.just("Hello");
		observable.subscribe(s -> result = s);
		assertEquals("Hello", result);
	}

	@Test
	void givenArray_whenFromAndSubscribe_thenEmitsItems() {
		String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
		Observable<String> observable = Observable.from(letters);
		observable.subscribe(
			i -> result += i,
			Throwable::printStackTrace,
			() -> result += "_Complete"
		);
		assertEquals("abcdefg_Complete", result);
	}

	@Test
	void givenArray_whenConvertsObservabletoBlockingObservable_thenReturnFirstElement() {
		String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
		Observable<String> observable = Observable.from(letters);
		String blockingObservable = observable.toBlocking().first();

		observable.subscribe(
			i -> result += i,
			Throwable::printStackTrace,
			() -> result += "_Completed"
		);
		assertEquals(String.valueOf(result.charAt(0)), blockingObservable);
	}

	@Test
	void givenArray_whenMapAndSubscribe_thenReturnCapitalLetters() {
		String[] letters = {"a", "b", "c", "d", "e", "f", "g"};

		Observable.from(letters)
			.map(String::toUpperCase)
			.subscribe(letter -> result += letter);

		assertEquals("ABCDEFG", result);
	}

	@Test
	void givenArray_whenFlatMapAndSubscribe_thenReturnUpperAndLowerCaseLetters() {

		Observable.just("book1", "book2")
			.flatMap(s -> getTitle())
			.subscribe(l -> result += l);

		assertEquals("titletitle", result);
	}

	@Test
	void givenArray_whenScanAndSubscribe_thenReturnTheSumOfAllLetters() {
		String[] letters = {"a", "b", "c"};

		Observable.from(letters)
			.scan(new StringBuilder(), StringBuilder::append)
			.subscribe(total -> result += total.toString());

		assertEquals("aababc", result);
	}

	@Test
	void givenArrayOfNumbers_whenGroupBy_thenCreateTwoGroupsBasedOnParity() {
		Integer[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		String[] EVEN = {""};
		String[] ODD = {""};

		Observable.from(numbers)
			.groupBy(i -> 0 == (i % 2) ? "EVEN" : "ODD")
			.subscribe(group ->
				group.subscribe((number) -> {
					if (group.getKey().equals("EVEN")) {
						EVEN[0] += number;
					} else {
						ODD[0] += number;
					}
				})
			);

		assertEquals("0246810", EVEN[0]);
		assertEquals("13579", ODD[0]);
	}

	@Test
	void givenArrayOfNumbers_whenFilter_thenGetAllOddNumbers() {
		Integer[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

		Observable.from(numbers)
			.filter(i -> (i % 2 == 1))
			.subscribe(i -> result += i);

		assertEquals("13579", result);
	}

	@Test
	void givenEmptyObservable_whenDefaultIfEmpty_thenGetDefaultMessage() {

		Observable.empty()
			.defaultIfEmpty("Observable is empty")
			.subscribe(s -> result += s);

		assertEquals("Observable is empty", result);
	}

	@Test
	void givenObservableFromArray_whenDefaultIfEmptyAndFirst_thenGetFirstLetterFromArray() {
		String[] letters = {"a", "b", "c", "d", "e", "f", "g"};

		Observable.from(letters)
			.defaultIfEmpty("Observable is empty")
			.first()
			.subscribe(s -> result += s);

		assertEquals("a", result);
	}

	@Test
	void givenObservableFromArray_whenTakeWhile_thenGetSumOfNumbersFromCondition() {
		Integer[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final Integer[] sum = {0};

		Observable.from(numbers)
			.takeWhile(i -> i < 5)
			.subscribe(s -> sum[0] += s);

		assertEquals(10, (int) sum[0]);
	}
}
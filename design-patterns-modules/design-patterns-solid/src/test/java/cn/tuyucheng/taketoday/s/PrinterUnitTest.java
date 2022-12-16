package cn.tuyucheng.taketoday.s;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrinterUnitTest {

	@Test
	void whenCreateBadBook() {
		BadBook badBook = new BadBook("tuyucheng", "tuyucheng", "tuyucheng");

		badBook.printTextToConsole();
		assertTrue(badBook.isWordInText("tuyucheng"));
		assertEquals("taketoday", badBook.replaceWordInText("tuyucheng", "taketoday"));
	}

	@Test
	void whenCreateBook() {
		Book book = new Book("tuyucheng", "tuyucheng", "tuyucheng");

		assertTrue(book.isWordInText("tuyucheng"));
		assertEquals("taketoday", book.replaceWordInText("tuyucheng", "taketoday"));
	}

	@Test
	void whenCreateGoodBook() {
		GoodBook goodBook = new GoodBook("tuyucheng", "tuyucheng", "tuyucheng");

		assertTrue(goodBook.isWordInText("tuyucheng"));
		assertEquals("taketoday", goodBook.replaceWordInText("tuyucheng", "taketoday"));
	}

	@Test
	void whenCreateBookPrinter() {
		BookPrinter bookPrinter = new BookPrinter();

		bookPrinter.printTextToAnotherMedium("tuyucheng");
		bookPrinter.printTextToConsole("tuyucheng");
	}

	@Test
	void whenCreateTextManipulator() {
		TextManipulator textManipulator = new TextManipulator("tuyucheng");

		textManipulator.findWordAndReplace("tuyucheng", "taketoday");
		textManipulator.appendText("tuyucheng");
		textManipulator.findWordAndDelete("tuyucheng");

		TextPrinter textPrinter = new TextPrinter(textManipulator);
		textPrinter.printText();
		textPrinter.printOutEachWordOfText();
		textPrinter.printRangeOfCharacters(0, 1);
	}
}
package cn.tuyucheng.taketoday.doublecolon;

import cn.tuyucheng.taketoday.doublecolon.function.TriFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComputerUtilsUnitTest {

	@BeforeEach
	void setup() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testConstructorReference() {
		Computer c1 = new Computer(2015, "white");
		Computer c2 = new Computer(2009, "black");
		Computer c3 = new Computer(2014, "black");

		BiFunction<Integer, String, Computer> c4Function = Computer::new;
		Computer c4 = c4Function.apply(2013, "white");
		BiFunction<Integer, String, Computer> c5Function = Computer::new;
		Computer c5 = c5Function.apply(2010, "black");
		BiFunction<Integer, String, Computer> c6Function = Computer::new;
		Computer c6 = c6Function.apply(2008, "black");

		List<Computer> inventory = Arrays.asList(c1, c2, c3, c4, c5, c6);

		List<Computer> blackComputer = ComputerUtils.filter(inventory, ComputerUtils.blackPredicate);
		assertEquals(blackComputer.size(), 4, "The black Computers are: ");

		List<Computer> after2010Computer = ComputerUtils.filter(inventory, ComputerUtils.after2010Predicate);
		assertEquals(after2010Computer.size(), 3, "The Computer bought after 2010 are: ");

		List<Computer> before2011Computer = ComputerUtils.filter(inventory, c -> c.getAge() < 2011);
		assertEquals(before2011Computer.size(), 3, "The Computer bought before 2011 are: ");

		inventory.sort(Comparator.comparing(Computer::getAge));

		assertEquals(c6, inventory.get(0), "Oldest Computer in inventory");
	}

	@Test
	void testStaticMethodReference() {
		Computer c1 = new Computer(2015, "white", 35);
		Computer c2 = new Computer(2009, "black", 65);
		TriFunction<Integer, String, Integer, Computer> c6Function = Computer::new;
		Computer c3 = c6Function.apply(2008, "black", 90);

		List<Computer> inventory = Arrays.asList(c1, c2, c3);
		inventory.forEach(ComputerUtils::repair);

		assertEquals(Integer.valueOf(100), c1.getHealty(), "Computer repaired");
	}

	@Test
	void testInstanceMethodArbitraryObjectParticularType() {
		Computer c1 = new Computer(2015, "white", 35);
		Computer c2 = new MacbookPro(2009, "black", 65);
		List<Computer> inventory = Arrays.asList(c1, c2);
		inventory.forEach(Computer::turnOnPc);
	}

	@Test
	void testSuperMethodReference() {
		final TriFunction<Integer, String, Integer, MacbookPro> integerStringIntegerObjectTriFunction = MacbookPro::new;
		final MacbookPro macbookPro = integerStringIntegerObjectTriFunction.apply(2010, "black", 100);
		Double initialValue = 999.99;
		final Double actualValue = macbookPro.calculateValue(initialValue);

		assertEquals(766.659, actualValue, 0.0);
	}
}
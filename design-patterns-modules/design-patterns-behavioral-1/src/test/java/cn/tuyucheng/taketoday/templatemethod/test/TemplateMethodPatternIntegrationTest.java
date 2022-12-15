package cn.tuyucheng.taketoday.templatemethod.test;

import cn.tuyucheng.taketoday.templatemethod.model.Computer;
import cn.tuyucheng.taketoday.templatemethod.model.HighEndComputerBuilder;
import cn.tuyucheng.taketoday.templatemethod.model.StandardComputerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateMethodPatternIntegrationTest {

	private static StandardComputerBuilder standardComputerBuilder;
	private static HighEndComputerBuilder highEndComputerBuilder;

	@BeforeAll
	static void setUpStandardComputerBuilderInstance() {
		standardComputerBuilder = new StandardComputerBuilder();
	}

	@BeforeAll
	static void setUpHighEndComputerBuilderInstance() {
		highEndComputerBuilder = new HighEndComputerBuilder();
	}

	@Test
	void givenStandardMotherBoard_whenAddingMotherBoard_thenEqualAssertion() {
		standardComputerBuilder.addMotherboard();
		assertEquals("Standard Motherboard", standardComputerBuilder.getComputerParts().get("Motherboard"));
	}

	@Test
	void givenStandardMotherboard_whenSetup_thenTwoEqualAssertions() {
		standardComputerBuilder.setupMotherboard();
		assertEquals("Screwing the standard motherboard to the case.", standardComputerBuilder.getMotherboardSetupStatus().get(0));
		assertEquals("Pluging in the power supply connectors.", standardComputerBuilder.getMotherboardSetupStatus().get(1));
	}

	@Test
	void givenStandardProcessor_whenAddingProcessor_thenEqualAssertion() {
		standardComputerBuilder.addProcessor();
		assertEquals("Standard Processor", standardComputerBuilder.getComputerParts().get("Processor"));
	}

	@Test
	void givenAllStandardParts_whenBuildingComputer_thenTwoParts() {
		standardComputerBuilder.buildComputer();
		assertEquals(2, standardComputerBuilder.getComputerParts().size());
	}

	@Test
	void givenAllStandardParts_whenComputerisBuilt_thenComputerInstance() {
		assertThat(standardComputerBuilder.buildComputer(), instanceOf(Computer.class));
	}

	@Test
	void givenHighEnddMotherBoard_whenAddingMotherBoard_thenEqualAssertion() {
		highEndComputerBuilder.addMotherboard();
		assertEquals("High-end Motherboard", highEndComputerBuilder.getComputerParts().get("Motherboard"));
	}

	@Test
	void givenHighEnddMotheroboard_whenSetup_thenTwoEqualAssertions() {
		highEndComputerBuilder.setupMotherboard();
		assertEquals("Screwing the high-end motherboard to the case.", highEndComputerBuilder.getMotherboardSetupStatus().get(0));
		assertEquals("Pluging in the power supply connectors.", highEndComputerBuilder.getMotherboardSetupStatus().get(1));
	}

	@Test
	void givenHightEndProcessor_whenAddingProcessor_thenEqualAssertion() {
		highEndComputerBuilder.addProcessor();
		assertEquals("High-end Processor", highEndComputerBuilder.getComputerParts().get("Processor"));
	}

	@Test
	void givenAllHighEnddParts_whenBuildingComputer_thenTwoParts() {
		highEndComputerBuilder.buildComputer();
		assertEquals(2, highEndComputerBuilder.getComputerParts().size());
	}

	@Test
	void givenAllHighEndParts_whenComputerisBuilt_thenComputerInstance() {
		assertThat(standardComputerBuilder.buildComputer(), instanceOf(Computer.class));
	}
}
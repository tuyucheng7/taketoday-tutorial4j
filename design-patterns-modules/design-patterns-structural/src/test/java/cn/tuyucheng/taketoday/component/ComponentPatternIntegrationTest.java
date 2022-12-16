package cn.tuyucheng.taketoday.component;

import cn.tuyucheng.taketoday.composite.Department;
import cn.tuyucheng.taketoday.composite.FinancialDepartment;
import cn.tuyucheng.taketoday.composite.HeadDepartment;
import cn.tuyucheng.taketoday.composite.SalesDepartment;
import cn.tuyucheng.taketoday.util.InMemoryCustomTestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentPatternIntegrationTest {

	private InMemoryCustomTestAppender appender;


	@BeforeEach
	void setUp() {
		appender = new InMemoryCustomTestAppender();
	}

	@AfterEach
	void tearDown() {
		appender.stop();
	}

	@Test
	void givenTwoLeafComponents_whenCombiningThemAsRootComponent_thenShouldCorrect() {
		Department salesDepartment = new SalesDepartment(1, "Sales department");
		Department financialDepartment = new FinancialDepartment(2, "Financial department");

		HeadDepartment headDepartment = new HeadDepartment(3, "Head department");

		headDepartment.addDepartMent(salesDepartment);
		headDepartment.addDepartMent(financialDepartment);

		headDepartment.printDepartmentName();

		assertTrue(appender.logContains("SalesDepartment"));
		assertTrue(appender.logContains("FinancialDepartment"));
	}
}
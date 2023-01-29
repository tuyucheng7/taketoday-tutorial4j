package cn.tuyucheng.taketoday.concurrent.atomic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicMarkableReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicMarkableReferenceUnitTest {

	static class Employee {
		private int id;
		private String name;

		Employee(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	void givenMarkValueAsTrue_whenUsingIsMarkedMethod_thenMarkValueShouldBeTrue() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenMarkValueAsFalse_whenUsingIsMarkedMethod_thenMarkValueShouldBeFalse() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, false);

		assertFalse(employeeNode.isMarked());
	}

	@Test
	void whenUsingGetReferenceMethod_thenCurrentReferenceShouldBeReturned() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

		assertEquals(employee, employeeNode.getReference());
	}

	@Test
	void whenUsingGetMethod_thenCurrentReferenceAndMarkShouldBeReturned() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

		boolean[] markHolder = new boolean[1];
		Employee currentEmployee = employeeNode.get(markHolder);

		assertEquals(employee, currentEmployee);
		assertTrue(markHolder[0]);
	}

	@Test
	void givenNewReferenceAndMark_whenUsingSetMethod_thenCurrentReferenceAndMarkShouldBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

		Employee newEmployee = new Employee(124, "John");
		employeeNode.set(newEmployee, false);

		assertEquals(newEmployee, employeeNode.getReference());
		assertFalse(employeeNode.isMarked());
	}

	@Test
	void givenTheSameObjectReference_whenUsingAttemptMarkMethod_thenMarkShouldBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

		assertTrue(employeeNode.attemptMark(employee, false));
		assertFalse(employeeNode.isMarked());
	}

	@Test
	void givenDifferentObjectReference_whenUsingAttemptMarkMethod_thenMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee expectedEmployee = new Employee(123, "Mike");

		assertFalse(employeeNode.attemptMark(expectedEmployee, false));
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenCurrentReferenceAndCurrentMark_whenUsingCompareAndSet_thenReferenceAndMarkShouldBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertTrue(employeeNode.compareAndSet(employee, newEmployee, true, false));
		assertEquals(newEmployee, employeeNode.getReference());
		assertFalse(employeeNode.isMarked());
	}

	@Test
	void givenNotCurrentReferenceAndCurrentMark_whenUsingCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.compareAndSet(new Employee(1234, "Steve"), newEmployee, true, false));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenCurrentReferenceAndNotCurrentMark_whenUsingCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.compareAndSet(employee, newEmployee, false, true));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenNotCurrentReferenceAndNotCurrentMark_whenUsingCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.compareAndSet(new Employee(1234, "Steve"), newEmployee, false, true));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenCurrentReferenceAndCurrentMark_whenUsingWeakCompareAndSet_thenReferenceAndMarkShouldBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertTrue(employeeNode.weakCompareAndSet(employee, newEmployee, true, false));
		assertEquals(newEmployee, employeeNode.getReference());
		assertFalse(employeeNode.isMarked());
	}

	@Test
	void givenNotCurrentReferenceAndCurrentMark_whenUsingWeakCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.weakCompareAndSet(new Employee(1234, "Steve"), newEmployee, true, false));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenCurrentReferenceAndNotCurrentMark_whenUsingWeakCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.weakCompareAndSet(employee, newEmployee, false, true));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}

	@Test
	void givenNotCurrentReferenceAndNotCurrentMark_whenUsingWeakCompareAndSet_thenReferenceAndMarkShouldNotBeUpdated() {
		Employee employee = new Employee(123, "Mike");
		AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
		Employee newEmployee = new Employee(124, "John");

		assertFalse(employeeNode.weakCompareAndSet(new Employee(1234, "Steve"), newEmployee, false, true));
		assertEquals(employee, employeeNode.getReference());
		assertTrue(employeeNode.isMarked());
	}
}
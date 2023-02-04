package cn.tuyucheng.taketoday.suppliercallable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;
import cn.tuyucheng.taketoday.suppliercallable.data.User;
import cn.tuyucheng.taketoday.suppliercallable.service.Service;
import cn.tuyucheng.taketoday.suppliercallable.service.callable.CallableServiceImpl;
import cn.tuyucheng.taketoday.suppliercallable.service.supplier.SupplierServiceImpl;

public class CallableSupplierUnitTest {

	@Test
	void givenCallableService_whenUserIsAnAdult_thenCanDriveACar() {
		User user = new User("Test", "Test", LocalDate.of(2000, Month.JANUARY, 19));
		Service service = new CallableServiceImpl();
		service.execute(user);
		assertEquals(true, user.getCanDriveACar());
	}

	@Test
	void givenCallableService_whenUserIsNotAnAdult_thenCannotDriveACar() {
		User user = new User("Test", "Test", LocalDate.of(2010, Month.JANUARY, 19));
		Service service = new CallableServiceImpl();
		service.execute(user);
		assertEquals(false, user.getCanDriveACar());
	}

	@Test
	void givenCallableService_whenBirthDateIsNull_thenShouldThrowAnException() {
		User user = new User("Test", "Test", null);
		Service service = new CallableServiceImpl();
		assertThrows(RuntimeException.class, () -> service.execute(user));
	}

	@Test
	void givenSupplierService_whenUserIsAnAdult_thenCanDriveACar() {
		User user = new User("Test", "Test", LocalDate.of(2000, Month.JANUARY, 19));
		Service service = new SupplierServiceImpl();
		service.execute(user);
		assertEquals(true, user.getCanDriveACar());
	}

	@Test
	void givenSupplierService_whenUserIsNotAnAdult_thenCannotDriveACar() {
		User user = new User("Test", "Test", LocalDate.of(2010, Month.JANUARY, 19));
		Service service = new SupplierServiceImpl();
		service.execute(user);
		assertEquals(false, user.getCanDriveACar());
	}

	@Test
	void givenSupplierService_whenBirthDateIsNull_thenCannotDriveACarAndAgeIsNull() {
		User user = new User("Test", "Test", null);
		Service service = new SupplierServiceImpl();
		service.execute(user);
		assertEquals(false, user.getCanDriveACar());
		assertNull(user.getBirthDate());
	}

}

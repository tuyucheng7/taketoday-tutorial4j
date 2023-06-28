package cn.tuyucheng.taketoday.javaxval.methodvalidation;

import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Customer;
import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Reservation;
import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.ReservationManagement;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationIntegrationTest {

   private ExecutableValidator executableValidator;

   @BeforeEach
   public void getExecutableValidator() {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      this.executableValidator = factory.getValidator()
            .forExecutables();
   }

   @Test
   void whenValidationWithInvalidMethodParameters_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", LocalDate.class, int.class, Customer.class);
      Object[] parameterValues = {LocalDate.now(), 0, null};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(3, violations.size());
   }

   @Test
   void whenValidationWithValidMethodParameters_thenZeroVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", LocalDate.class, int.class, Customer.class);
      Object[] parameterValues = {LocalDate.now()
            .plusDays(1), 1, new Customer("John", "Doe")};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(0, violations.size());
   }

   @Test
   void whenCrossParameterValidationWithInvalidParameters_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", LocalDate.class, LocalDate.class, Customer.class);
      Object[] parameterValues = {LocalDate.now(), LocalDate.now(), new Customer("John", "Doe")};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(1, violations.size());
   }

   @Test
   void whenCrossParameterValidationWithValidParameters_thenZeroVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", LocalDate.class, LocalDate.class, Customer.class);
      Object[] parameterValues = {LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            new Customer("John", "Doe")};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(0, violations.size());
   }

   @Test
   void whenValidationWithInvalidConstructorParameters_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      Constructor<Customer> constructor = Customer.class.getConstructor(String.class, String.class);
      Object[] parameterValues = {"John", "Doe"};
      Set<ConstraintViolation<Customer>> violations = executableValidator.validateConstructorParameters(constructor, parameterValues);

      assertEquals(2, violations.size());
   }

   @Test
   void whenValidationWithValidConstructorParameters_thenZeroVoilations() throws NoSuchMethodException {
      Constructor<Customer> constructor = Customer.class.getConstructor(String.class, String.class);
      Object[] parameterValues = {"William", "Smith"};
      Set<ConstraintViolation<Customer>> violations = executableValidator.validateConstructorParameters(constructor, parameterValues);

      assertEquals(0, violations.size());
   }

   @Test
   void whenCrossParameterValidationWithInvalidConstructorParameters_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      Constructor<Reservation> constructor = Reservation.class.getConstructor(LocalDate.class, LocalDate.class, Customer.class, int.class);
      Object[] parameterValues = {LocalDate.now(), LocalDate.now(), new Customer("William", "Smith"), 1};
      Set<ConstraintViolation<Reservation>> violations = executableValidator.validateConstructorParameters(constructor, parameterValues);

      assertEquals(1, violations.size());
   }

   @Test
   void whenCrossParameterValidationWithValidConstructorParameters_thenZeroVoilations() throws NoSuchMethodException {
      Constructor<Reservation> constructor = Reservation.class.getConstructor(LocalDate.class, LocalDate.class, Customer.class, int.class);
      Object[] parameterValues = {LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            new Customer("William", "Smith"), 1};
      Set<ConstraintViolation<Reservation>> violations = executableValidator.validateConstructorParameters(constructor, parameterValues);

      assertEquals(0, violations.size());
   }

   @Test
   void whenValidationWithInvalidReturnValue_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("getAllCustomers");
      Object returnValue = Collections.<Customer>emptyList();
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateReturnValue(object, method, returnValue);

      assertEquals(1, violations.size());
   }

   @Test
   void whenValidationWithValidReturnValue_thenZeroVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("getAllCustomers");
      Object returnValue = Collections.singletonList(new Customer("William", "Smith"));
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateReturnValue(object, method, returnValue);

      assertEquals(0, violations.size());
   }

   @Test
   void whenValidationWithInvalidConstructorReturnValue_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      Constructor<Reservation> constructor = Reservation.class.getConstructor(LocalDate.class, LocalDate.class, Customer.class, int.class);
      Reservation createdObject = new Reservation(LocalDate.now(), LocalDate.now(), new Customer("William", "Smith"), 0);
      Set<ConstraintViolation<Reservation>> violations = executableValidator.validateConstructorReturnValue(constructor, createdObject);

      assertEquals(1, violations.size());
   }

   @Test
   void whenValidationWithValidConstructorReturnValue_thenZeroVoilations() throws NoSuchMethodException {
      Constructor<Reservation> constructor = Reservation.class.getConstructor(LocalDate.class, LocalDate.class, Customer.class, int.class);
      Reservation createdObject = new Reservation(LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            new Customer("William", "Smith"), 1);
      Set<ConstraintViolation<Reservation>> violations = executableValidator.validateConstructorReturnValue(constructor, createdObject);

      assertEquals(0, violations.size());
   }

   @Test
   void whenValidationWithInvalidCascadedValue_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", Reservation.class);
      Customer customer = new Customer();
      customer.setFirstName("John");
      customer.setLastName("Doe");
      Reservation reservation = new Reservation(LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            customer, 1);
      Object[] parameterValues = {reservation};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(2, violations.size());
   }

   @Test
   void whenValidationWithValidCascadedValue_thenCorrectNumberOfVoilations() throws NoSuchMethodException {
      ReservationManagement object = new ReservationManagement();
      Method method = ReservationManagement.class.getMethod("createReservation", Reservation.class);
      Customer customer = new Customer();
      customer.setFirstName("William");
      customer.setLastName("Smith");
      Reservation reservation = new Reservation(LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            customer, 1);
      Object[] parameterValues = {reservation};
      Set<ConstraintViolation<ReservationManagement>> violations = executableValidator.validateParameters(object, method, parameterValues);

      assertEquals(0, violations.size());
   }
}
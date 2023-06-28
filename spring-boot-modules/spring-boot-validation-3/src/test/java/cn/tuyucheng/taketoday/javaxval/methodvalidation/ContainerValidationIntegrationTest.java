package cn.tuyucheng.taketoday.javaxval.methodvalidation;

import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Customer;
import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.Reservation;
import cn.tuyucheng.taketoday.javaxval.methodvalidation.model.ReservationManagement;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MethodValidationConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ContainerValidationIntegrationTest {

   @Autowired
   ReservationManagement reservationManagement;

   @Test
   public void whenValidationWithInvalidMethodParameters_thenConstraintViolationException() {
      assertThrows(ConstraintViolationException.class, () -> reservationManagement.createReservation(LocalDate.now(), 0, null));
   }

   @Test
   public void whenValidationWithValidMethodParameters_thenNoException() {
      reservationManagement.createReservation(LocalDate.now()
            .plusDays(1), 1, new Customer("William", "Smith"));
   }

   @Test
   public void whenCrossParameterValidationWithInvalidParameters_thenConstraintViolationException() {
      assertThrows(ConstraintViolationException.class, () -> reservationManagement.createReservation(LocalDate.now(), LocalDate.now(), null));
   }

   @Test
   public void whenCrossParameterValidationWithValidParameters_thenNoException() {
      reservationManagement.createReservation(LocalDate.now()
                  .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            new Customer("William", "Smith"));
   }

   @Test
   public void whenValidationWithInvalidReturnValue_thenConstraintViolationException() {
      assertThrows(ConstraintViolationException.class, () -> reservationManagement.getAllCustomers());
   }

   @Test
   public void whenValidationWithInvalidCascadedValue_thenConstraintViolationException() {
      Customer customer = new Customer();
      customer.setFirstName("John");
      customer.setLastName("Doe");
      Reservation reservation = new Reservation(LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            customer, 1);

      assertThrows(ConstraintViolationException.class, () -> reservationManagement.createReservation(reservation));
   }

   @Test
   public void whenValidationWithValidCascadedValue_thenCNoException() {
      Customer customer = new Customer();
      customer.setFirstName("William");
      customer.setLastName("Smith");
      Reservation reservation = new Reservation(LocalDate.now()
            .plusDays(1),
            LocalDate.now()
                  .plusDays(2),
            customer, 1);

      reservationManagement.createReservation(reservation);
   }
}
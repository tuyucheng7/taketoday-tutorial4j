package cn.tuyucheng.taketoday.equalshashcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoneyUnitTest {

   @Test
   public void givenMoneyInstancesWithSameAmountAndCurrency_whenEquals_thenReturnsTrue() {
      Money income = new Money(55, "USD");
      Money expenses = new Money(55, "USD");

      assertTrue(income.equals(expenses));
      assertTrue(expenses.equals(income));
   }

   @Test
   public void givenMoneyAndWrongVoucherInstances_whenEquals_thenReturnValuesArentSymmetric() {
      Money money = new Money(42, "USD");
      WrongVoucher voucher = new WrongVoucher(42, "USD", "Amazon");

      assertFalse(voucher.equals(money));
      assertTrue(money.equals(voucher));
   }

   @Test
   public void givenMoneyAndVoucherInstances_whenEquals_thenReturnValuesArentSymmetric() {
      Money money = new Money(42, "USD");
      Voucher voucher = new Voucher(42, "USD", "Amazon");

      assertFalse(voucher.equals(money));
      assertFalse(money.equals(voucher));
   }

}

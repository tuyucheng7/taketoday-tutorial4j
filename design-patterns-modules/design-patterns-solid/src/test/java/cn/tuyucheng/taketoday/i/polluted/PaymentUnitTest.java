package cn.tuyucheng.taketoday.i.polluted;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentUnitTest {

	@Test
	void whenCreateBankPayment_thenShouldCreateBankPayment() {
		BankPayment bankPayment = new BankPayment();

		bankPayment.getPayments();
		bankPayment.initiatePayments();
		bankPayment.status();
		assertThrows(UnsupportedOperationException.class, bankPayment::intiateLoanSettlement);
		assertThrows(UnsupportedOperationException.class, bankPayment::initiateRePayment);
	}

	@Test
	void whenCreateLoanPayment_thenShouldCreateLoanPayment() {
		LoanPayment loanPayment = new LoanPayment();

		loanPayment.getPayments();
		loanPayment.initiatePayments();
		loanPayment.status();
		loanPayment.intiateLoanSettlement();
		loanPayment.initiateRePayment();
	}
}
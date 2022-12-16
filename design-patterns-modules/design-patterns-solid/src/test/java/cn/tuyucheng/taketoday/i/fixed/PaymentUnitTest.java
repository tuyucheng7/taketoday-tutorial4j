package cn.tuyucheng.taketoday.i.fixed;

import org.junit.jupiter.api.Test;

class PaymentUnitTest {

	@Test
	void whenCreateBankPayment_thenShouldCreateBankPayment() {
		BankPayment bankPayment = new BankPayment();

		bankPayment.getPayments();
		bankPayment.initiatePayments();
		bankPayment.status();
	}

	@Test
	void whenCreateLoanPayment_thenShouldCreateLoanPayment() {
		LoanPayment loanPayment = new LoanPayment();

		loanPayment.getPayments();
		loanPayment.status();
		loanPayment.intiateLoanSettlement();
		loanPayment.initiateRePayment();
	}
}
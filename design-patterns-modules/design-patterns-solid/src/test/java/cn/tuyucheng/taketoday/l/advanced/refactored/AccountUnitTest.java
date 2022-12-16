package cn.tuyucheng.taketoday.l.advanced.refactored;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountUnitTest {

	@Test
	void whenCreateCurrentAccount() {
		CurrentAccount currentAccount = new CurrentAccount();

		currentAccount.deposit(BigDecimal.valueOf(100));
		currentAccount.withdraw(BigDecimal.valueOf(50));
	}

	@Test
	void whenCreateSavingsAccount() {
		SavingsAccount savingsAccount = new SavingsAccount();

		savingsAccount.deposit(BigDecimal.valueOf(100));
		savingsAccount.withdraw(BigDecimal.valueOf(50));
	}

	@Test
	void whenCreateFixedDepositAccount() {
		FixedTermDepositAccount fixedDepositAccount = new FixedTermDepositAccount();

		fixedDepositAccount.deposit(BigDecimal.valueOf(100));
	}

	@Test
	void whenPassCurrentAccountToService() {
		CurrentAccount currentAccount = new CurrentAccount();
		BankingAppWithdrawalService bankingAppWithdrawalService = new BankingAppWithdrawalService(currentAccount);

		bankingAppWithdrawalService.withdraw(BigDecimal.valueOf(50));
	}
}
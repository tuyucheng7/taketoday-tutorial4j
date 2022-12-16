package cn.tuyucheng.taketoday.l.advanced;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountUnitTest {

	@Test
	void whenCreateCurrentAccount() {
		Account account = new CurrentAccount();

		account.deposit(BigDecimal.valueOf(20));
		account.withdraw(BigDecimal.valueOf(50));
	}

	@Test
	void whenCreateSavingsAccount() {
		Account account = new SavingsAccount();

		account.deposit(BigDecimal.valueOf(20));
		account.withdraw(BigDecimal.valueOf(50));
	}

	@Test
	void whenCreateFixedDepositAccount() {
		Account account = new FixedTermDepositAccount();

		account.deposit(BigDecimal.valueOf(20));
		assertThrows(UnsupportedOperationException.class, () -> account.withdraw(BigDecimal.valueOf(50)));
	}

	@Test
	void whenPassCurrentAccountToService() {
		BankingAppWithdrawalService accountService = new BankingAppWithdrawalService(new CurrentAccount());

		accountService.withdraw(BigDecimal.valueOf(50));
	}
}
package cn.tuyucheng.taketoday.l.advanced.refactored;

import java.math.BigDecimal;

public class BankingAppWithdrawalService {
	private WithdrawableAccount withdrawableAccount;

	public BankingAppWithdrawalService(WithdrawableAccount withdrawableAccount) {
		this.withdrawableAccount = withdrawableAccount;
	}

	public void withdraw(BigDecimal amount) {
		withdrawableAccount.withdraw(amount);
	}
}
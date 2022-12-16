package cn.tuyucheng.taketoday.i.fixed;

public interface Loan extends Payment {
	void intiateLoanSettlement();

	void initiateRePayment();
}
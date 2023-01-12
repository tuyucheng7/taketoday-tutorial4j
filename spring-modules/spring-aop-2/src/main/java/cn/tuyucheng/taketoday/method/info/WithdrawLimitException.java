package cn.tuyucheng.taketoday.method.info;

public class WithdrawLimitException extends RuntimeException {
	public WithdrawLimitException(String message) {
		super(message);
	}
}

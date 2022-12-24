package cn.tuyucheng.taketoday.annotation.scanner;

public class UnexpectedScanException extends RuntimeException {
	public UnexpectedScanException(Throwable ex) {
		super(ex);
	}
}
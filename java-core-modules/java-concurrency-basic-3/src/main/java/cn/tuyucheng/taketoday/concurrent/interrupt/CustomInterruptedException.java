package cn.tuyucheng.taketoday.concurrent.interrupt;

import java.io.Serial;

public class CustomInterruptedException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;

	CustomInterruptedException(String message) {
		super(message);
	}
}
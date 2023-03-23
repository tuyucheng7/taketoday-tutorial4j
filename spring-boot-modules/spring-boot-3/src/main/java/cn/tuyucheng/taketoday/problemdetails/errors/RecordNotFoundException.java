package cn.tuyucheng.taketoday.problemdetails.errors;

public class RecordNotFoundException extends RuntimeException {

	private final String message;

	public RecordNotFoundException(String message) {
		this.message = message;
	}
}
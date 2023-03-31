package cn.tuyucheng.taketoday.httpinterface;

public class MyServiceException extends RuntimeException {

	MyServiceException(String msg) {
		super(msg);
	}
}
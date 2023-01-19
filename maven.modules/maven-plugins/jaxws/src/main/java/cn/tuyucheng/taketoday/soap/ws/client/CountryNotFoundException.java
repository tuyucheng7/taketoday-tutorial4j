package cn.tuyucheng.taketoday.soap.ws.client;

public class CountryNotFoundException extends RuntimeException {

	public CountryNotFoundException() {
		super("Country not found!");
	}
}
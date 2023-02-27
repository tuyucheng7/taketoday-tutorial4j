package cn.tuyucheng.taketoday.cxf.introduction;

import jakarta.xml.ws.Endpoint;

public class Server {
	public static void main(String args[]) throws InterruptedException {
		TuyuchengImpl implementor = new TuyuchengImpl();
		String address = "http://localhost:8080/tuyucheng";
		Endpoint.publish(address, implementor);
		System.out.println("Server ready...");
		Thread.sleep(60 * 1000);
		System.out.println("Server exiting");
		System.exit(0);
	}
}
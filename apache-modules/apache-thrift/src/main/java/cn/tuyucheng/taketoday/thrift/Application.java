package cn.tuyucheng.taketoday.thrift;

import org.apache.thrift.transport.TTransportException;

public class Application {

	public static void main(String[] args) throws TTransportException {
		CrossPlatformServiceServer server = new CrossPlatformServiceServer();
		server.start();
	}
}

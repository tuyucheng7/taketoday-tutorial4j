package cn.tuyucheng.taketoday.httpsclientauthentication;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SSLSocketClient {

	static void startClient(String host, int port) throws IOException {
		SocketFactory factory = SSLSocketFactory.getDefault();

		try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
			socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
			socket.setEnabledProtocols(new String[]{"TLSv1.3"});
			InputStream is = new BufferedInputStream(socket.getInputStream());
			String message = "Hello World Message";
			System.out.println("sending message: " + message);
			OutputStream os = new BufferedOutputStream(socket.getOutputStream());
			os.write(message.getBytes());
			os.flush();
			byte[] data = new byte[2048];
			int len = is.read(data);
			if (len <= 0) {
				throw new IOException("no data received");
			}
			System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));
		}
	}

	public static void main(String[] args) throws IOException {
		startClient("localhost", 8443);
	}
}
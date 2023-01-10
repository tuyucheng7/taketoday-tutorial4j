package cn.tuyucheng.taketoday.ssh;

import cn.tuyucheng.taketoday.ssh.jsch.JschDemo;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JSchLiveTest {

	@Test
	public void givenValidCredentials_whenConnectionIsEstablished_thenServerReturnsResponse() throws Exception {
		String username = "demo";
		String password = "password";
		String host = "test.rebex.net";
		int port = 22;
		String command = "ls";
		String responseString = JschDemo.listFolderStructure(username, password, host, port, command);

		assertNotNull(responseString);
	}

	@Test(expected = Exception.class)
	public void givenInvalidCredentials_whenConnectionAttemptIsMade_thenServerReturnsErrorResponse() throws Exception {
		String username = "invalidUsername";
		String password = "password";
		String host = "test.rebex.net";
		int port = 22;
		String command = "ls";
		String responseString = JschDemo.listFolderStructure(username, password, host, port, command);

		assertNull(responseString);
	}
}

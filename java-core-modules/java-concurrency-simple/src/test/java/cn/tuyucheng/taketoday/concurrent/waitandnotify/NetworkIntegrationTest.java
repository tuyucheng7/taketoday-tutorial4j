package cn.tuyucheng.taketoday.concurrent.waitandnotify;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class NetworkIntegrationTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private String expected;

	@BeforeEach
	void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@BeforeEach
	void setUpExpectedOutput() {
		StringWriter expectedStringWriter = new StringWriter();

		PrintWriter printWriter = new PrintWriter(expectedStringWriter);
		printWriter.println("First packet");
		printWriter.println("Second packet");
		printWriter.println("Third packet");
		printWriter.println("Fourth packet");
		printWriter.close();

		expected = expectedStringWriter.toString();
	}

	@AfterEach
	void cleanUpStreams() {
		System.setOut(null);
		System.setErr(null);
	}

	@Test
	void givenSenderAndReceiver_whenSendingPackets_thenNetworkSynchronized() {
		Data data = new Data();
		Thread sender = new Thread(new Sender(data));
		Thread receiver = new Thread(new Receiver(data));

		sender.start();
		receiver.start();

		// wait for sender and receiver to finish before we test against expected
		try {
			sender.join();
			receiver.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("context ", e);
		}

		assertEquals(expected, outContent.toString());
	}
}
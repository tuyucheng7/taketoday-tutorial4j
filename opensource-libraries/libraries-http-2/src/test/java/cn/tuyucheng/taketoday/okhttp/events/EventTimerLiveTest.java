package cn.tuyucheng.taketoday.okhttp.events;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EventTimerLiveTest {

	@Test
	public void givenSimpleEventTimer_whenRequestSent_thenCallsLogged() throws IOException {

		OkHttpClient client = new OkHttpClient.Builder()
			.eventListener(new EventTimer())
			.build();

		Request request = new Request.Builder()
			.url("https://www.baeldung.com/")
			.build();

		try (Response response = client.newCall(request).execute()) {
			assertEquals("Response code should be: ", 200, response.code());
		}
	}

}

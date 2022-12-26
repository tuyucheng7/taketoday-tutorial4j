package cn.tuyucheng.taketoday.maxhttpheadersize.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Start MaxHttpHeaderSizeController Spring Boot App(MaxHttpHeaderSizeApplication) first
class MaxHttpHeaderSizeControllerLiveTest {

	@Test
	void givenTokenWithGreaterThan8KBLegth_whenSendGetRequest_thenThrowsBadRequest() throws Exception {
		final String url = "http://localhost:8080/request-header-test";
		HttpHeaders headers = new HttpHeaders();
		headers.set("token", readRandomStringFromFile());

		HttpEntity entity = new HttpEntity(headers);
		assertThrows(HttpClientErrorException.class, () -> new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class));
	}

	static String readRandomStringFromFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/randomSringForheader.txt"));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();
		return stringBuilder.toString();
	}
}
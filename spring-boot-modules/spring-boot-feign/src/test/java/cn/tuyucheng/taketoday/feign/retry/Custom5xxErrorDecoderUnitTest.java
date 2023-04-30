package cn.tuyucheng.taketoday.feign.retry;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Custom5xxErrorDecoderUnitTest {
	@Test
	void given5xxResponse_whenDecode_thenReturnRetryableException() {
		// given
		ErrorDecoder decoder = new Custom5xxErrorDecoder();
		Response response = responseStub(500);

		// when
		Exception exception = decoder.decode("GET", response);

		// then
		assertTrue(exception instanceof RetryableException);
	}

	@Test
	void given4xxResponse_whenDecode_thenReturnFeignException() {
		// given
		ErrorDecoder decoder = new Custom5xxErrorDecoder();
		Response response = responseStub(400);

		// when
		Exception exception = decoder.decode("GET", response);

		// then
		assertTrue(exception instanceof FeignException);
		assertFalse(exception instanceof RetryableException);
	}

	private Response responseStub(int status) {
		return Response.builder().request(Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), new byte[0], Charset.defaultCharset(), new RequestTemplate())).status(status).build();
	}
}
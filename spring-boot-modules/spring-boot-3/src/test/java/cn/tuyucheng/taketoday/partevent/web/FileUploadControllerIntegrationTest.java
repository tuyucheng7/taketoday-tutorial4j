package cn.tuyucheng.taketoday.partevent.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@WebFluxTest
public class FileUploadControllerIntegrationTest {

	@Autowired
	FileUploadController fileUploadController;

	@Autowired
	WebTestClient client;

	@Test
	public void testHandleSimpleFileUpload() {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("name", "test");
		multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
		var multipartBody = multipartBodyBuilder.build();
		this.client
			.post().uri("/simple-form-upload")
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(multipartBody))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.name").isEqualTo("test")
			.jsonPath("$.filename").isEqualTo("spring.png");
	}

	@Test
	public void testHandleRequestParts() {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("name", "test");
		multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
		var multipartBody = multipartBodyBuilder.build();
		this.client
			.post().uri("/upload-with-request-parts")
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(multipartBody))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.name").isEqualTo("test")
			.jsonPath("$.filename").isEqualTo("spring.png");
	}

	@Test
	public void testHandleMultiValueMap() {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("name", "test");
		multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
		var multipartBody = multipartBodyBuilder.build();
		this.client
			.post().uri("/upload-with-multi-value-map")
			.contentType(MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(multipartBody))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.size()").isEqualTo(2);
	}

	@Test
	public void testUploadUsingPartEvents() {
		this.client
			.post().uri("/upload-with-part-events")
			.contentType(MULTIPART_FORM_DATA)
			.body(Flux.concat(
					FormPartEvent.create("name", "test"),
					FilePartEvent.create("file", new ClassPathResource("spring.png"))
				),
				PartEvent.class
			)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(String.class).hasSize(2);
	}
}
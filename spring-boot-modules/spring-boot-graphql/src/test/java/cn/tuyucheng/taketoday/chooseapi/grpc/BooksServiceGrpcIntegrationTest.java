package cn.tuyucheng.taketoday.chooseapi.grpc;

import cn.tuyucheng.taketoday.chooseapi.BooksServiceGrpc;
import cn.tuyucheng.taketoday.chooseapi.BooksServiceOuterClass;
import cn.tuyucheng.taketoday.chooseapi.dtos.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(properties = {
	"grpc.server.inProcessName=test", // Enable inProcess server
	"grpc.server.port=-1", // Disable external server
	"grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@SpringJUnitConfig(GrpcIntegrationTestConfig.class)
@DirtiesContext // Ensures that the grpc-server is properly shutdown after each test
class BooksServiceGrpcIntegrationTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@GrpcClient("inProcess")
	private BooksServiceGrpc.BooksServiceBlockingStub booksServiceGrpc;

	@Test
	@DirtiesContext
	void givenBooksServiceThatReturnThreeBooks_whenCallingGrpcEndpoint_thenThreeBooksAreReturned() throws IOException, JSONException {
		Path expectedResponse = Paths.get("src/test/resources/graphql-test/books_expected_response.json");
		String expectedJson = new String(Files.readAllBytes(expectedResponse));

		BooksServiceOuterClass.BooksRequest request = BooksServiceOuterClass.BooksRequest.newBuilder().build();
		BooksServiceOuterClass.BooksResponse response = booksServiceGrpc.books(request);

		List<Book> books = response.getBookList().stream()
			.map(GrpcBooksMapper::mapProtoToBook)
			.collect(Collectors.toList());

		JSONAssert.assertEquals(objectMapper.writeValueAsString(books), expectedJson, true);
	}
}
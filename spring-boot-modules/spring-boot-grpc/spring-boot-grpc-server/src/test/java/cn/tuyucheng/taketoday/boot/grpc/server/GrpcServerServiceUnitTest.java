package cn.tuyucheng.taketoday.boot.grpc.server;

import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloReply;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloRequest;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class GrpcServerServiceUnitTest {

	private GrpcServerService grpcServerService;

	@BeforeEach
	void setUp() {
		grpcServerService = new GrpcServerService();
	}

	@Test
	void whenUsingValidRequest_thenReturnResponse() throws Exception {
		HelloRequest request = HelloRequest.newBuilder()
			.setName("Test")
			.build();
		StreamRecorder<HelloReply> responseObserver = StreamRecorder.create();
		grpcServerService.sayHello(request, responseObserver);
		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			fail("The call did not terminate in time");
		}

		assertNull(responseObserver.getError());

		List<HelloReply> results = responseObserver.getValues();
		assertEquals(1, results.size());

		HelloReply response = results.get(0);
		assertEquals(HelloReply.newBuilder()
			.setMessage("Hello ==> Test")
			.build(), response);
	}
}
package cn.tuyucheng.taketoday.boot.grpc.client;

import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloReply;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloRequest;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.SimpleGrpc.SimpleBlockingStub;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrpcClientServiceUnitTest {

	private final GrpcClientService grpcClientService = new GrpcClientService();

	private final SimpleBlockingStub simpleStub = mock(SimpleBlockingStub.class);

	@BeforeEach
	void setUp() {
		grpcClientService.setSimpleStub(simpleStub);
	}

	@Test
	void givenAnyRequest_whenStubValidResponse_thenCorrect() {
		HelloRequest request = any(HelloRequest.class);
		HelloReply response = HelloReply.newBuilder()
			.setMessage("Hello")
			.build();
		when(simpleStub.sayHello(request)).thenReturn(response);

		assertThat(grpcClientService.sendMessage("Tuyucheng")).isEqualTo("Hello");
	}

	@Test
	void givenAnyRequest_whenStubInternalError_thenShouldThrowException() {
		HelloRequest request = any(HelloRequest.class);
		when(simpleStub.sayHello(request)).thenThrow(new StatusRuntimeException(io.grpc.Status.INTERNAL));

		assertThat(grpcClientService.sendMessage("Tuyucheng")).isEqualTo("FAILED with INTERNAL");
	}
}
package cn.tuyucheng.taketoday.boot.grpc.client;

import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloReply;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloRequest;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.SimpleGrpc.SimpleBlockingStub;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

	private SimpleBlockingStub simpleStub;

	@GrpcClient("local-grpc-server")
	public void setSimpleStub(final SimpleBlockingStub simpleStub) {
		this.simpleStub = simpleStub;
	}

	public String sendMessage(final String name) {
		try {
			HelloRequest request = HelloRequest.newBuilder()
				.setName(name)
				.build();
			final HelloReply response = this.simpleStub.sayHello(request);
			return response.getMessage();
		} catch (final StatusRuntimeException e) {
			return "FAILED with " + e.getStatus().getCode().name();
		}
	}
}
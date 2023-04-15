package cn.tuyucheng.taketoday.boot.grpc.server;

import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloReply;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.HelloRequest;
import cn.tuyucheng.taketoday.boot.grpc.examples.lib.SimpleGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {

	@Override
	public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply = HelloReply.newBuilder()
			.setMessage("Hello ==> " + request.getName())
			.build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
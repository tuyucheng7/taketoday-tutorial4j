package cn.tuyucheng.taketoday.grpc.server

import cn.tuyucheng.taketoday.grpc.helloworld.HelloReply
import cn.tuyucheng.taketoday.grpc.helloworld.HelloRequest
import cn.tuyucheng.taketoday.grpc.helloworld.HelloServiceGrpcKt
import io.grpc.ServerBuilder

class HelloService : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
	override suspend fun hello(request: HelloRequest): HelloReply {
		return HelloReply.newBuilder()
			.setMessage("Hello, ${request.name}")
			.build()
	}
}

fun helloServer() {
	val helloService = cn.tuyucheng.taketoday.grpc.server.HelloService()
	val server = ServerBuilder
		.forPort(15001)
		.addService(helloService)
		.build()

	Runtime.getRuntime().addShutdownHook(Thread {
		server.shutdown()
		server.awaitTermination()
	})

	server.start()
	server.awaitTermination()
}

fun main(args: Array<String>) {
	cn.tuyucheng.taketoday.grpc.server.helloServer()
}
package cn.tuyucheng.taketoday.grpc.client

import cn.tuyucheng.taketoday.grpc.helloworld.HelloReply
import cn.tuyucheng.taketoday.grpc.helloworld.HelloRequest
import cn.tuyucheng.taketoday.grpc.helloworld.HelloServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

fun asyncHelloClient() {
	val channel = ManagedChannelBuilder.forAddress("localhost", 15001)
		.usePlaintext()
		.build()
	HelloServiceGrpc.newStub(channel).hello(
		HelloRequest.newBuilder().setName("Baeldung").build(), object : StreamObserver<HelloReply> {
			override fun onNext(response: HelloReply?) {
				println(response?.message)
			}

			override fun onError(throwable: Throwable?) {
				throwable?.printStackTrace()
			}

			override fun onCompleted() {
				println("Completed!")
			}
		}
	)
}

fun main(args: Array<String>) {
	cn.tuyucheng.taketoday.grpc.client.asyncHelloClient()
}
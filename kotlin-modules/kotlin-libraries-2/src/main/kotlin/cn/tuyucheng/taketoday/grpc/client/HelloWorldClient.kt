package cn.tuyucheng.taketoday.grpc.client

import cn.tuyucheng.taketoday.grpc.helloworld.HelloRequest
import cn.tuyucheng.taketoday.grpc.helloworld.HelloServiceGrpc
import io.grpc.ManagedChannelBuilder

fun helloClient() {
   val channel = ManagedChannelBuilder.forAddress("localhost", 15001)
         .usePlaintext()
         .build()
   val stub = HelloServiceGrpc.newBlockingStub(channel)
   val response = stub.hello(HelloRequest.newBuilder().setName("Tuyucheng").build())
   println(response.message)
}

fun main(args: Array<String>) {
   helloClient()
}

## 一、简介

[gRPC](https://grpc.io/)是由 Google 开发的开源 RPC(远程过程调用)平台，可在任何类型的环境和跨数据中心提供高性能和高效的通信。此外，gRPC 对负载均衡、跟踪、健康检查和身份验证的可插拔支持使其成为分布式计算和微服务的良好候选者。

使用 gRPC，客户端应用程序使用生成的存根调用服务器应用程序上的方法，这些存根隐藏了分布式应用程序和服务的复杂性。

在本教程中，我们将探索 gRPC 组件，并了解如何在 Kotlin 中实现 gRPC 服务器和客户端。

## 2. 组件

gRPC 从服务定义开始。服务定义是服务的接口，包含方法、参数和预期的返回类型。

然后，基于定义的服务接口，客户端使用存根调用服务器。另一方面，服务器为接口实现服务并运行 gRPC 服务器来处理客户端请求。

gRPC 默认使用[Protobuf](https://developers.google.com/protocol-buffers)作为接口描述语言来描述服务定义和负载。但是，我们可以使用[Gson](https://github.com/google/gson)等其他库来编码和解码，而不是Protobuf。

现在我们对 gRPC 的工作原理有了一些了解，让我们来看看实现。

## 3.依赖关系

让我们从将 gRPC 的依赖项添加到我们的 POM 开始：

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty</artifactId>
    <version>1.46.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.39.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.46.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-kotlin-stub</artifactId>
    <version>1.2.0</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-kotlin</artifactId>
    <version>3.18.1</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.18.1</version> 
</dependency>
```

## 4. 原型文件

现在让我们在.proto文件中定义我们的服务。对于我们的教程，我们将使用我们之前在[Java 教程](https://www.baeldung.com/grpc-introduction#defining-the-service)中使用的相同服务定义：

```kotlin
syntax = "proto3";

package com.baeldung.grpc.helloworld;

option java_multiple_files = true;

service HelloService {
  rpc hello (HelloRequest) returns (HelloReply) {}
}

message HelloRequest {
  string name = 1;
}

message HelloReply {
  string message = 1;
}
```

要在我们的代码中使用这些原型文件，我们必须创建它们的存根。为了实现这个目标，我们将使用[协议缓冲区编译器protoc](https://grpc.io/docs/protoc-installation/)：

```bash
protoc --plugin=protoc-gen-grpc-java=build/exe/java_plugin/protoc-gen-grpc-java 
  --grpc-java_out="$OUTPUT_FILE" --proto_path="$DIR_OF_PROTO_FILE" "$PROTO_FILE"
```

此外，我们还有一个更简单的选择——将这个存根创建步骤嵌入到我们的pom.xml中，如下所示。

## 5. Maven 插件

现在，让我们将org.xolstice.maven.plugins:protobuf-maven-plugin插件添加到我们的pom.xml中，以便我们可以在执行 Maven编译目标时从原型文件创建请求、响应和存根。

这利用了[grpc-kotlin](https://github.com/grpc/grpc-kotlin)实现：

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <executions>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
                <protocPlugins>
                    <protocPlugin>
                        <id>grpc-kotlin</id>
                        <groupId>io.grpc</groupId>
                        <artifactId>protoc-gen-grpc-kotlin</artifactId>
                        <version>${grpc.kotlin.version}</version>
                        <classifier>jdk7</classifier>
                        <mainClass>io.grpc.kotlin.generator.GeneratorRunner</mainClass>
                    </protocPlugin>
                </protocPlugins>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 6.服务器实现

要实现服务的功能，让我们从覆盖HelloServiceCoroutineImplBase开始：

```kotlin
class HelloService : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
    override suspend fun hello(request: HelloRequest): HelloReply {
        return HelloReply.newBuilder()
            .setMessage("Hello, ${request.name}")
            .build()
    }
}
```

然后，我们可以使用HelloService启动 gRPC 服务器：

```kotlin
fun helloServer() {
    val helloService = HelloService()
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
    helloServer()
}
```

## 7.客户端实现

要设置客户端，让我们构建ManagedChannel：


```kotlin
val channel = ManagedChannelBuilder.forAddress("localhost", 15001)
    .usePlaintext()
    .build()
```

通过使用[gRPC 通道](https://grpc.io/docs/what-is-grpc/core-concepts/#channels)，客户端连接到指定主机和端口上的 gRPC 服务器。

现在，让我们创建存根：

```kotlin
val stub = HelloServiceGrpc.newBlockingStub(channel)
```

最后，让我们创建请求并调用服务器：

```kotlin
val response = stub.hello(HelloRequest.newBuilder().setName("Baeldung").build())

```

在这里，我们创建了一个阻塞存根，但也有未来或异步存根的选项。

我们可以调用 Future stub 来获得com.google.common.util.concurrent.ListenableFuture 的响应。然后，我们有可能取消呼叫或获得超时响应：

```kotlin
HelloServiceGrpc.newFutureStub(channel)
```

此外，我们可以创建一个异步存根并在回调中以反应方式获取响应：

```kotlin
HelloServiceGrpc.newStub(channel).hello(
    HelloRequest.newBuilder().setName("Baeldung").build(), object: StreamObserver<HelloReply> {
        override fun onNext(response: HelloReply?) {
            //consume response
        }

        override fun onError(throwable: Throwable?) {
            //handle error
        }

        override fun onCompleted() {
            //on complete
        }
    }
)
```

## 八、总结

在本文中，我们研究了 gRPC 以及如何使用 Kotlin 在服务器端和客户端上实现它。
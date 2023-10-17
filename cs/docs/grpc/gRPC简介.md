## 1. 简介

**[gRPC](http://www.grpc.io/)是一个高性能、开源的RPC框架，最初由谷歌开发**。它有助于消除样板代码，并有助于连接数据中心内和跨数据中心的多语言服务。

## 2. 概述

该框架基于远程过程调用的客户端-服务器模型，**客户端应用程序可以直接调用服务器应用程序上的方法，就好像它是本地对象一样**。

本文将通过以下步骤使用gRPC创建一个典型的客户端-服务器应用程序：

1.  在.proto文件中定义服务
2.  使用协议缓冲区(protocol buffer)编译器生成服务器和客户端代码
3.  创建服务器应用程序，实现生成的服务接口并生成gRPC服务器
4.  创建客户端应用程序，使用生成的存根进行RPC调用

让我们定义一个简单的HelloService，它返回问候语以交换名字和姓氏。

## 3. Maven依赖

让我们添加[grpc-netty](https://search.maven.org/classic/#search|ga|1|a%3A"grpc-netty")、[grpc-protobuf](https://search.maven.org/classic/#search|ga|1|a%3A"grpc-protobuf")和[grpc-stub](https://search.maven.org/classic/#search|ga|1|a%3A"grpc-stub")依赖项：

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty</artifactId>
    <version>1.16.1</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.16.1</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.16.1</version>
</dependency>
```

## 4. 定义服务

首先我们定义一个服务，**指定可以远程调用的方法及其参数和返回类型**。

这是使用[协议缓冲区]()在.proto文件中完成的，它们还用于描述有效负载消息的结构。

### 4.1 基本配置

让我们为我们的示例HelloService创建一个HelloService.proto文件，我们首先添加一些基本的配置细节：

```protobuf
syntax = "proto3";
option java_multiple_files = true;
package cn.tuyucheng.taketoday.grpc;
```

第一行告诉编译器这个文件中使用了什么语法，默认情况下，编译器在单个Java文件中生成所有Java代码。第二行覆盖此设置，所有内容都将在单独的文件中生成。

最后，我们指定要用于生成的Java类的包。

### 4.2 定义消息结构

接下来，我们定义消息：

```protobuf
message HelloRequest {
    string firstName = 1;
    string lastName = 2;
}
```

这定义了请求有效负载，在这里，将定义消息中的每个属性及其类型。

需要为每个属性分配一个唯一编号(上面的1和2)，称为标记。**协议缓冲区使用此标记来表示属性，而不是使用属性名称**。

因此，与每次都会传递属性名称firstName的JSON不同，协议缓冲区将使用数字1来表示firstName，响应有效负载定义与请求类似。

请注意，我们可以在多种消息类型中使用相同的标签：

```protobuf
message HelloResponse {
    string greeting = 1;
}
```

### 4.3 定义服务契约

最后，让我们定义服务契约。对于我们的HelloService，我们定义了一个hello()操作：

```protobuf
service HelloService {
    rpc hello(HelloRequest) returns (HelloResponse);
}
```

hello()操作接受一元请求并返回一元响应，gRPC还通过在请求和响应前加上stream关键字来支持流式处理。

## 5. 生成代码

现在我们将HelloService.proto文件传递给协议缓冲区编译器protoc以生成Java文件，有多种方法可以触发此目的。

### 5.1 使用Protocol Buffer编译器

首先，我们需要协议缓冲区编译器，我们可以从[此处](https://github.com/protocolbuffers/protobuf/releases)提供的许多预编译二进制文件中进行选择。

此外，我们需要获取[gRPC Java Codegen Plugin](https://github.com/grpc/grpc-java/tree/master/compiler)。

最后，我们可以使用以下命令来生成代码：

```bash
protoc --plugin=protoc-gen-grpc-java=$PATH_TO_PLUGIN -I=$SRC_DIR 
  --java_out=$DST_DIR --grpc-java_out=$DST_DIR $SRC_DIR/HelloService.proto
```

### 5.2 使用Maven插件

作为开发人员，你可能会希望代码的生成与构建系统紧密集成，gRPC为Maven构建系统提供了一个[protobuf-maven-plugin](https://search.maven.org/classic/#search|ga|1|g%3A"org.xolstice.maven.plugins" AND a%3A"protobuf-maven-plugin")：

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.6.1</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <protocArtifact>
                    com.google.protobuf:protoc:3.3.0:exe:${os.detected.classifier}
                </protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>
                    io.grpc:protoc-gen-grpc-java:1.4.0:exe:${os.detected.classifier}
                </pluginArtifact>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

[os-maven-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"kr.motd.maven" AND a%3A"os-maven-plugin")扩展/插件生成各种有用的平台相关项目属性，如${os.detected.classifier}。

## 6. 创建服务器

无论使用哪种方法生成代码，都会生成以下关键文件：

-   HelloRequest.java：包含HelloRequest类型定义
-   HelloResponse.java：这包含HelleResponse类型定义
-   HelloServiceImplBase.java：它包含抽象类HelloServiceImplBase，它提供了我们在服务接口中定义的所有操作的实现

### 6.1 覆盖服务基类

**抽象类HelloServiceImplBase的默认实现是抛出运行时异常io.grpc.StatusRuntimeException，表示该方法未实现**。

我们将扩展此类并覆盖服务定义中提到的hello()方法：

```java
public class HelloServiceImpl extends HelloServiceImplBase {

    @Override
    public void hello(
          HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
              .append("Hello, ")
              .append(request.getFirstName())
              .append(" ")
              .append(request.getLastName())
              .toString();

        HelloResponse response = HelloResponse.newBuilder()
              .setGreeting(greeting)
              .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

如果我们将hello()的签名与我们在HellService.proto文件中编写的签名进行比较，我们会注意到它没有返回HelloResponse。相反，它将第二个参数作为StreamObserver<HelloResponse\>，它是一个响应观察器，是服务器调用其响应的回调。

这样**客户端就可以选择进行阻塞调用或非阻塞调用**。

gRPC使用构建器来创建对象，我们使用HelloResponse.newBuilder()并设置问候语文本来构建HelloResponse对象。我们将这个对象设置为responseObserver的onNext()方法以将其发送给客户端。

最后，我们需要调用onCompleted()来表明我们已经完成了对RPC的处理，否则连接将被挂起，客户端只会等待更多的信息进来。

### 6.2 运行Grpc服务器

接下来，我们需要启动gRPC服务器来监听传入的请求：

```java
public class GrpcServer {
    public static void main(String[] args) {
        Server server = ServerBuilder
              .forPort(8080)
              .addService(new HelloServiceImpl()).build();

        server.start();
        server.awaitTermination();
    }
}
```

在这里，我们再次使用构建器在端口8080上创建一个gRPC服务器，并添加我们定义的HelloServiceImpl服务。start()将启动服务器，在我们的示例中，我们将调用awaitTermination()以保持服务器在前台运行以阻止提示。

## 7. 创建客户端

**gRPC提供了一个通道结构，可以抽象出连接、连接池、负载平衡等底层细节**。

我们将使用ManagedChannelBuilder创建一个通过，在这里，我们指定服务器地址和端口。

我们将使用没有任何加密的纯文本：

```java
public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
              .usePlaintext()
              .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub
              = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
              .setFirstName("Tuyucheng")
              .setLastName("gRPC")
              .build());

        channel.shutdown();
    }
}
```

接下来，我们需要创建一个存根，我们将使用它来对hello()进行实际的远程调用。**存根是客户端与服务器交互的主要方式**，使用自动生成的存根时，存根类将具有用于包装通道的构造函数。

在这里我们使用阻塞/同步存根，以便RPC调用等待服务器响应，然后返回响应或引发异常。gRPC提供了另外两种类型的存根，它们有助于非阻塞/异步调用。

最后，是时候进行hello() RPC调用了。这里我们传递了HelloRequest，我们可以使用自动生成的设置器来设置HelloRequest对象的firstName和lastName属性。

我们取回从服务器返回的HelloResponse对象。

## 8. 总结

在本教程中，我们了解了如何使用gRPC来简化两个服务之间通信的开发，方法是专注于定义服务并让gRPC处理所有样板代码。
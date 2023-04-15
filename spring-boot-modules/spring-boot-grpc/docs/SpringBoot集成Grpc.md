## 1. 概述

本节描述将你的应用程序转换为grpc-spring-boot-starter应用程序所需的步骤。

## 2. 项目设置

在我们开始添加依赖项之前，让我们从我们对你的项目设置的一些建议开始。

![项目设置](https://yidongnan.github.io/grpc-spring-boot-starter/assets/images/server-project-setup.svg)

我们建议将你的项目拆分为2-3个独立的模块。

1.  **接口项目spring-boot-grpc-api：**包含原始protobuf文件并生成Java模型和服务类。你可以分享这部分。
2.  **服务器项目spring-boot-grpc-server：**包含项目的实际实现，并使用接口项目作为依赖项。
3.  **客户端项目spring-boot-grpc-client：**(可选，可以有多个)使用预先生成的存根来访问服务器的任何客户端项目。

## 3. Maven依赖项

### 3.1 接口项目

```xml
<dependencies>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>jakarta.annotation</groupId>
        <artifactId>jakarta.annotation-api</artifactId>
        <version>1.3.5</version>
        <optional>true</optional>
    </dependency>
</dependencies>

<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.7.0</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>${protobuf-plugin.version}</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
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

<properties>
    <protobuf.version>3.19.1</protobuf.version>
    <protobuf-plugin.version>0.6.1</protobuf-plugin.version>
    <grpc.version>1.51.0</grpc.version>
</properties>
```

最新版本的[grpc-stub](https://mvnrepository.com/artifact/io.grpc/grpc-stub)、[grpc-protobuf](https://mvnrepository.com/artifact/io.grpc/grpc-protobuf)、[protobuf-maven-plugin](https://mvnrepository.com/artifact/org.xolstice.maven.plugins/protobuf-maven-plugin)可以在Maven Central上找到。

### 3.2 服务器项目

```xml
<dependencies>
    <dependency>
    	<groupId>net.devh</groupId>
    	<artifactId>grpc-server-spring-boot-starter</artifactId>
    	<version>2.14.0.RELEASE</version>
    </dependency>
    <dependency>
    	<groupId>cn.tuyucheng.taketoday.spring-boot-modules</groupId>
    	<artifactId>spring-boot-grpc-api</artifactId>
    	<version>1.0.0</version>
    </dependency>
</dependencies>
```

最先版本的的[grpc-server-spring-boot-starter](https://mvnrepository.com/artifact/net.devh/grpc-server-spring-boot-starter)可以在Maven Central上找到。

### 3.3 客户端项目

```xml
<dependencies>
    <dependency>
        <groupId>net.devh</groupId>
        <artifactId>grpc-client-spring-boot-starter</artifactId>
        <version>2.14.0.RELEASE</version>
    </dependency>
    <dependency>
    	<groupId>cn.tuyucheng.taketoday.spring-boot-modules</groupId>
    	<artifactId>spring-boot-grpc-api</artifactId>
    	<version>1.0.0</version>
    </dependency>
</dependencies>
```

最先版本的的[grpc-client-spring-boot-starter](https://mvnrepository.com/artifact/net.devh/grpc-client-spring-boot-starter)可以在Maven Central上找到。

## 4. 创建gRPC服务定义

将你的protobuf定义/.proto文件放在src/main/proto目录下。如需编写protobuf文件，请参阅[官方protobuf文档](https://developers.google.com/protocol-buffers/docs/proto3)。

这是示例helloworld.proto文件：

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "cn.tuyucheng.taketoday.boot.grpc.examples.lib";
option java_outer_classname = "HelloWorldProto";

// The greeting service definition.
service Simple {
    // Sends a greeting
    rpc SayHello (HelloRequest) returns (HelloReply) {
    }
}

// The request message containing the user's name.
message HelloRequest {
    string name = 1;
}

// The response message containing the greetings
message HelloReply {
    string message = 1;
}
```

配置好的maven-protobuf插件将使用插件调用[protoc](https://mvnrepository.com/artifact/com.google.protobuf/protoc)编译器[protoc-gen-grpc-java](https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-java)并生成数据类、grpc服务ImplBase和Stub。请注意，其他插件(例如[reactive-grpc)](https://github.com/salesforce/reactive-grpc)可能会生成你必须使用的其他/替代类。然而，它们可以以类似的方式使用。

-   这些ImplBase类包含将虚拟实现映射到grpc服务方法的基本逻辑。[在实现服务](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html#implementing-the-service)主题中有更多相关信息。
-   这些Stub类是完整的客户端实现。有关更多信息，请访问[“让客户开始”](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/getting-started.html)页面。

## 5. 实现服务

protoc-gen-grpc-java插件为你的每个grpc服务生成一个类。例如：MyServiceGrpc哪里MyService是proto文件中grpc服务的名称。此类包含ImplBase你需要扩展的客户端存根和服务器。

之后你只有四个任务要做：

1.  确保你的MyServiceImpl扩展MyServiceGrpc.MyServiceImplBase
2.  将注释添加@GrpcService到你的MyServiceImpl班级
3.  确保MyServiceImpl已添加到你的应用程序上下文中，
    -   通过@Bean在你的一个@Configuration类中创建定义
    -   或者将它放在Spring的自动检测路径中(例如，在你的类的相同或子包中Main)
4.  实际实现grpc服务方法。

你的grpc服务类将类似于下面的示例：

```java
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
```

>   **注意**：理论上没有必要扩展ImplBase，而是自己实现BindableService。但是，这样做可能会绕过Spring Security的检查。

仅此而已。现在你可以启动你的Spring Boot应用程序并开始向你的grpc-service发送请求。

9090默认情况下，grpc-server将在端口使用PLAINTEXT模式下启动。

你可以通过运行以下[gRPCurl](https://github.com/fullstorydev/grpcurl)命令来测试你的应用程序是否按预期工作：

```shell
grpcurl --plaintext localhost:9090 list
grpcurl --plaintext localhost:9090 list cn.tuyucheng.taketoday.boot.grpc.server.GrpcServerService
# Linux (Static content)
grpcurl --plaintext -d '{"name": "test"}' cn.tuyucheng.taketoday.boot.grpc.server.GrpcServerService/sayHello
# Windows or Linux (dynamic content)
grpcurl --plaintext -d "{\"name\": \"test\"}" cn.tuyucheng.taketoday.boot.grpc.server.GrpcServerService/sayHello
```

有关gRPCurl示例命令输出和其他信息，请参见[此处](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/testing.html#grpcurl)。

## 6. 测试服务

现在，让我们测试上一小节中实现的GrpcServerService。

### 6.1 Maven依赖

首先，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-testing</artifactId>
    <scope>test</scope>
    <version>1.51.0</version>
</dependency>
```

最新版本的[grpc-testing](https://mvnrepository.com/artifact/io.grpc/grpc-testing)可以在Maven Central上找到。

### 6.2 单元测试

在直接测试中，我们直接在grpc-service bean/实例上调用方法。

> 如果你自己创建grpc-service实例，请确保首先填充所需的依赖。如果你使用Spring，它会为你处理依赖关系，但你必须配置Spring。

### 6.3 独立测试

独立测试对外部库没有任何依赖性(事实上你甚至不需要这个)。但是，没有外部依赖项并不总能让你的生活更轻松，因为你可能必须复制其他库可能为你执行的行为。使用诸如Mockito之类的模拟库 将为你简化该过程，因为它限制了依赖树的深度。

```java
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
```

### 6.4 基于Spring的测试

如果你使用Spring为自己管理依赖项，那么你实际上是在进入集成测试领域。确保你没有启动整个应用程序，而只是提供所需的依赖项作为mock bean。

> 注意：在测试期间，Spring不会自动设置所有必需的bean。你必须在你的@Configuration类中手动创建它们。

```java
@SpringBootTest
@SpringJUnitConfig(classes = {GrpcServerServiceTestConfiguration.class})
public class GrpcServerServiceIntegrationTest {

	@Autowired
	private GrpcServerService grpcServerService;

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
```

和所需的配置类：

```java
@Configuration
public class GrpcServerServiceTestConfiguration {

	@Bean
	public GrpcServerService grpcServerService() {
		return new GrpcServerService();
	}
}
```

### 6.5 集成测试

然而，有时你需要测试整个堆栈。例如，身份验证是否起作用。但在这种情况下，建议限制测试范围以避免可能的外部影响，如空数据库。

在这一点上，在没有Spring的情况下测试基于Spring的应用程序没有任何意义。

> 注意：在测试期间，Spring不会自动设置所有必需的 bean。你必须在你的@Configuration或明确包含相关的自动配置类中手动创建它们。

## 7. 使用存根连接到服务器

### 7.1 解释客户端组件

以下列表包含你可能在客户端遇到的所有功能。如果你不想使用任何高级功能，那么前两个元素可能就是你需要使用的全部。

-   [@GrpcClient](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/inject/GrpcClient.html)：标记用于客户端自动注入的字段和设置器的注释。对构造函数和@Bean工厂方法参数的支持是实验性的。支持Channels，以及各种Stubs。与或 一起@GrpcClientBean使用时使用。**注意：**同一应用程序提供的服务只能在 . 连接到应用程序外部服务的存根可以更早地使用；从 /开始。@Autowired``@Inject
    ApplicationStartedEvent``@PostConstruct``InitializingBean#afterPropertiesSet()
-   [@GrpcClientBean](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/inject/GrpcClientBean.html)：注解有助于@GrpcClient在 Spring 上下文中注册 bean，以便与@Autowired和 一起 使用@Qualifier。注释可以重复添加到你的任何@Configuration类中。
-   [Channel](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/Channel.html): Channel是一个单一地址的连接池。不过，目标服务器可能会提供多个 grpc 服务。该地址将使用 a 进行解析，NameResolver并且可能指向固定或动态数量的服务器。
-   [ManagedChannel](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/ManagedChannel.html): ManagedChannel 是 Channel 的一个特殊变体，因为它允许对连接池进行管理操作，例如关闭它。
-   [NameResolver](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/NameResolver.html)respectively [NameResolver.Factory](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/NameResolver.Factory.html)：一个将用于将地址解析为SocketAddresses 列表的类，当与先前列出的服务器的连接失败或通道空闲时，通常会重新解析地址。另请参阅[配置 -> 选择目标](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/configuration.html#choosing-the-target)。
-   [ClientInterceptor](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/ClientInterceptor.html): 在将每个呼叫交给 之前拦截每个呼叫Channel。可用于日志记录、监控、元数据处理和请求/响应重写。grpc-spring-boot-starter 将自动获取所有带有注释 [@GrpcGlobalClientInterceptor](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/interceptor/GrpcGlobalClientInterceptor.html) 或手动注册到 [GlobalClientInterceptorRegistry](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/interceptor/GlobalClientInterceptorRegistry.html). 另请参阅[配置 -> 客户端拦截器](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/configuration.html#clientinterceptor)。
-   [CallCredentials](https://javadoc.io/page/io.grpc/grpc-all/latest/io/grpc/CallCredentials.html)：管理调用身份验证的潜在活动组件。它可用于存储凭据或会话令牌。它还可用于在身份验证提供程序处进行身份验证，然后使用返回的令牌（例如 OAuth）来授权实际请求。除此之外，如果令牌过期并重新发送请求，它还可以更新令牌。如果CallCredentials你的应用程序上下文中恰好存在一个 bean，那么 spring 将自动将其附加到所有Stubs（**NOT** Channel s）。该 [CallCredentialsHelper](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/security/CallCredentialsHelper.html) 实用程序类可帮助你创建常用CallCredentials类型和相关的StubTransformer.
-   [StubFactory](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/stubfactory/StubFactory.html): 可用于Stub从Channel. StubFactory可以注册多个以支持不同的存根类型。另请参阅[配置 -> StubFactory](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/configuration.html#stubfactory)。
-   [StubTransformer](https://javadoc.io/page/net.devh/grpc-client-spring-boot-autoconfigure/latest/net/devh/boot/grpc/client/inject/StubTransformer.html)Stub:在注入之前将应用于所有 client 的转换器。另请参阅[配置 -> StubTransformer](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/configuration.html#stubtransformer)。

### 7.2 访问客户端

```java
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
```

## 7. 测试Grpc-Stubs

通常有两种方法来测试包含 grpc 存根的组件：

-   [使用模拟存根](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/testing.html#using-a-mocked-stub)
-   [运行虚拟服务器](https://yidongnan.github.io/grpc-spring-boot-starter/en/client/testing.html#running-a-dummy-server)

>   注意：两种变体之间存在非常重要的差异，可能会在测试期间影响您。请仔细考虑每种变体列出的优缺点。

### 7.1 Maven依赖

在开始编写自己的测试框架之前，你可能希望使用以下库来简化您的工作。

```xml
<!-- Grpc-Test-Support -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-testing</artifactId>
    <scope>test</scope>
</dependency>
```

### 7.2 使用Mock存根

为了测试该方法，我们mock存根并使用setter注入它。

优点：

+ 快速
+ 支持知名的Mock框架

缺点：

+ 需要“魔法”来取消最终存根方法
+ 开箱即用
+ 不适用于使用存根的 bean@PostContruct
+ 对于间接(通过其他bean)使用存根的bean效果不佳
+ 不适用于启动Spring的测试

```java
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
}
```
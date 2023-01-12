## 1. 简介

[Dubbo](http://dubbo.io/)是阿里巴巴开源的 RPC 和微服务框架。

除其他外，它有助于增强服务治理，并使传统的整体式应用程序可以顺利重构为可扩展的分布式架构。

在本文中，我们将介绍 Dubbo 及其最重要的特性。

## 2. 架构

Dubbo 区分了几个角色：

1.  提供者——暴露服务的地方；提供商将其服务注册到注册中心
2.  容器——服务启动、加载和运行的地方
3.  消费者——调用远程服务的人；消费者将订阅注册表中所需的服务
4.  Registry——注册和发现服务的地方
5.  Monitor – 记录服务的统计信息，例如，在给定时间间隔内服务调用的频率

[![Dubbo架构](https://www.baeldung.com/wp-content/uploads/2017/12/dubbo-architecture-1024x639-1024x639.png)](https://www.baeldung.com/wp-content/uploads/2017/12/dubbo-architecture-1024x639.png)

(来源：http://dubbo.io/images/dubbo-architecture.png)

提供者、消费者和注册中心之间的连接是持久的，因此每当服务提供者关闭时，注册中心可以检测到故障并通知消费者。

注册表和监视器是可选的。消费者可以直接连接到服务提供者，但会影响整个系统的稳定性。

## 3.Maven依赖

在我们深入之前，让我们将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.5.7</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.alibaba" AND a%3A"dubbo")找到。

## 4.引导

下面我们来体验一下Dubbo的基本功能。

这是一个微创的框架，它的很多特性都依赖于外部配置或注解。

官方建议我们应该使用 XML 配置文件，因为它依赖于 Spring 容器(目前是 Spring 4.3.10)。

我们将使用 XML 配置来演示它的大部分功能。

### 4.1. 多播注册 – 服务提供商

作为快速开始，我们只需要一个服务提供者、一个消费者和一个“隐形”注册中心。注册表是不可见的，因为我们使用的是多播网络。

在以下示例中，提供者仅向其消费者说“嗨”：

```java
public interface GreetingsService {
    String sayHi(String name);
}

public class GreetingsServiceImpl implements GreetingsService {

    @Override
    public String sayHi(String name) {
        return "hi, " + name;
    }
}
```

要进行远程过程调用，消费者必须与服务提供者共享一个公共接口，因此接口GreetingsService必须与消费者共享。

### 4.2. 多播注册——服务注册

现在让我们将GreetingsService注册到注册表。如果提供者和消费者都在同一个本地网络上，一种非常方便的方法是使用多播注册表：

```xml
<dubbo:application name="demo-provider" version="1.0"/>
<dubbo:registry address="multicast://224.1.1.1:9090"/>
<dubbo:protocol name="dubbo" port="20880"/>
<bean id="greetingsService" class="com.baeldung.dubbo.remote.GreetingsServiceImpl"/>
<dubbo:service interface="com.baeldung.dubbo.remote.GreetingsService"
  ref="greetingsService"/>
```

通过上面的 beans 配置，我们刚刚将我们的GreetingsService暴露到dubbo://127.0.0.1:20880下的一个 url，并将该服务注册到<dubbo:registry />中指定的多播地址。

在提供者的配置中，我们还分别通过<dubbo:application />、<dubbo:service />和<beans />声明了我们的应用元数据、要发布的接口及其实现。

dubbo协议是框架支持的众多协议之一。它建立在JavaNIO 非阻塞特性之上，并且是默认使用的协议。

我们将在本文后面更详细地讨论它。

### 4.3. 多播注册表——服务消费者

通常，消费者需要指定调用的接口和远程服务的地址，而这正是消费者所需要的：

```xml
<dubbo:application name="demo-consumer" version="1.0"/>
<dubbo:registry address="multicast://224.1.1.1:9090"/>
<dubbo:reference interface="com.baeldung.dubbo.remote.GreetingsService"
  id="greetingsService"/>
```

现在一切就绪，让我们看看它们是如何工作的：

```java
public class MulticastRegistryTest {

    @Before
    public void initRemote() {
        ClassPathXmlApplicationContext remoteContext
          = new ClassPathXmlApplicationContext("multicast/provider-app.xml");
        remoteContext.start();
    }

    @Test
    public void givenProvider_whenConsumerSaysHi_thenGotResponse(){
        ClassPathXmlApplicationContext localContext 
          = new ClassPathXmlApplicationContext("multicast/consumer-app.xml");
        localContext.start();
        GreetingsService greetingsService
          = (GreetingsService) localContext.getBean("greetingsService");
        String hiMessage = greetingsService.sayHi("baeldung");

        assertNotNull(hiMessage);
        assertEquals("hi, baeldung", hiMessage);
    }
}
```

当 provider 的remoteContext启动时，Dubbo 会自动加载GreetingsService并注册到给定的注册中心。在这种情况下，它是一个多播注册表。

消费者订阅多播注册表并在上下文中创建GreetingsService的代理。当我们的本地客户端调用sayHi方法时，它会透明地调用远程服务。

我们提到注册表是可选的，这意味着消费者可以通过公开的端口直接连接到提供者：

```xml
<dubbo:reference interface="com.baeldung.dubbo.remote.GreetingsService"
  id="greetingsService" url="dubbo://127.0.0.1:20880"/>
```

基本上流程和传统的web service差不多，只是Dubbo做到了简单、轻量。

### 4.4. 简单注册表

请注意，当使用“不可见”的多播注册表时，注册表服务不是独立的。但是，它仅适用于受限的本地网络。

要显式设置可管理的注册表，我们可以使用[SimpleRegistryService](https://github.com/apache/incubator-dubbo/blob/master/dubbo-registry/dubbo-registry-default/src/test/java/org/apache/dubbo/registry/dubbo/SimpleRegistryService.java)。

将以下 beans 配置加载到 Spring 上下文中后，将启动一个简单的注册服务：

```xml
<dubbo:application name="simple-registry" />
<dubbo:protocol port="9090" />
<dubbo:service interface="com.alibaba.dubbo.registry.RegistryService"
  ref="registryService" registry="N/A" ondisconnect="disconnect">
    <dubbo:method name="subscribe">
        <dubbo:argument index="1" callback="true" />
    </dubbo:method>
    <dubbo:method name="unsubscribe">
        <dubbo:argument index="1" callback="true" />
    </dubbo:method>
</dubbo:service>

<bean class="com.alibaba.dubbo.registry.simple.SimpleRegistryService"
  id="registryService" />
```

请注意，SimpleRegistryService类不包含在工件中，因此我们直接从 Github 存储库中[源代码。](https://github.com/apache/incubator-dubbo/blob/master/dubbo-registry/dubbo-registry-default/src/test/java/org/apache/dubbo/registry/dubbo/SimpleRegistryService.java)

然后我们将调整提供者和消费者的注册表配置：

```xml
<dubbo:registry address="127.0.0.1:9090"/>
```

SimpleRegistryService在测试时可以作为一个独立的注册中心使用，但不建议在生产环境中使用。

### 4.5. Java配置

还支持通过JavaAPI、属性文件和注解进行配置。但是，属性文件和注解仅适用于我们的架构不是很复杂的情况。

让我们看看我们之前的多播注册表 XML 配置如何转换为 API 配置。首先，provider设置如下：

```java
ApplicationConfig application = new ApplicationConfig();
application.setName("demo-provider");
application.setVersion("1.0");

RegistryConfig registryConfig = new RegistryConfig();
registryConfig.setAddress("multicast://224.1.1.1:9090");

ServiceConfig<GreetingsService> service = new ServiceConfig<>();
service.setApplication(application);
service.setRegistry(registryConfig);
service.setInterface(GreetingsService.class);
service.setRef(new GreetingsServiceImpl());

service.export();
```

现在服务已经通过多播注册表公开，让我们在本地客户端中使用它：

```java
ApplicationConfig application = new ApplicationConfig();
application.setName("demo-consumer");
application.setVersion("1.0");

RegistryConfig registryConfig = new RegistryConfig();
registryConfig.setAddress("multicast://224.1.1.1:9090");

ReferenceConfig<GreetingsService> reference = new ReferenceConfig<>();
reference.setApplication(application);
reference.setRegistry(registryConfig);
reference.setInterface(GreetingsService.class);

GreetingsService greetingsService = reference.get();
String hiMessage = greetingsService.sayHi("baeldung");
```

尽管上面的代码片段与前面的 XML 配置示例一样很有魅力，但它更简单一些。就目前而言，想要充分利用Dubbo，XML配置应该是首选。

## 5.协议支持

该框架支持多种协议，包括dubbo、RMI、hessian、HTTP、web service、thrift、memcached和redis。大多数协议看起来很熟悉，除了dubbo。让我们看看这个协议有什么新内容。

dubbo协议在提供者和消费者之间保持持久连接。长连接和NIO无阻塞网络通信在传输小规模数据包(<100K)时具有相当好的性能。

有几个可配置的属性，例如端口、每个消费者的连接数、最大接受连接数等。

```xml
<dubbo:protocol name="dubbo" port="20880"
  connections="2" accepts="1000" />
```

Dubbo 还支持通过不同的协议同时暴露服务：

```xml
<dubbo:protocol name="dubbo" port="20880" />
<dubbo:protocol name="rmi" port="1099" />

<dubbo:service interface="com.baeldung.dubbo.remote.GreetingsService"
  version="1.0.0" ref="greetingsService" protocol="dubbo" />
<dubbo:service interface="com.bealdung.dubbo.remote.AnotherService"
  version="1.0.0" ref="anotherService" protocol="rmi" />
```

是的，我们可以使用不同的协议公开不同的服务，如上面的代码片段所示。底层传输器、序列化实现和其他与网络相关的通用属性也是可配置的。

## 6. 结果缓存

支持本地远程结果缓存，以加速访问热点数据。就像向 bean 引用添加缓存属性一样简单：

```xml
<dubbo:reference interface="com.baeldung.dubbo.remote.GreetingsService"
  id="greetingsService" cache="lru" />
```

这里我们配置了一个最近最少使用的缓存。为了验证缓存行为，我们将在之前的标准实现中稍微改变一下(我们称之为“特殊实现”)：

```java
public class GreetingsServiceSpecialImpl implements GreetingsService {
    @Override
    public String sayHi(String name) {
        try {
            SECONDS.sleep(5);
        } catch (Exception ignored) { }
        return "hi, " + name;
    }
}
```

启动提供者后，我们可以在消费者端验证，在多次调用时结果是否被缓存：

```java
@Test
public void givenProvider_whenConsumerSaysHi_thenGotResponse() {
    ClassPathXmlApplicationContext localContext
      = new ClassPathXmlApplicationContext("multicast/consumer-app.xml");
    localContext.start();
    GreetingsService greetingsService
      = (GreetingsService) localContext.getBean("greetingsService");

    long before = System.currentTimeMillis();
    String hiMessage = greetingsService.sayHi("baeldung");

    long timeElapsed = System.currentTimeMillis() - before;
    assertTrue(timeElapsed > 5000);
    assertNotNull(hiMessage);
    assertEquals("hi, baeldung", hiMessage);

    before = System.currentTimeMillis();
    hiMessage = greetingsService.sayHi("baeldung");
    timeElapsed = System.currentTimeMillis() - before;
 
    assertTrue(timeElapsed < 1000);
    assertNotNull(hiMessage);
    assertEquals("hi, baeldung", hiMessage);
}
```

这里消费者调用的是特殊的服务实现，所以第一次调用完成需要5秒多的时间。当我们再次调用时，sayHi方法几乎立即完成，因为结果从缓存中返回。

请注意，还支持线程本地缓存和 JCache。

## 7.集群支持

Dubbo 以其负载均衡能力和多种容错策略帮助我们自由地扩展我们的服务。在这里，假设我们有 Zookeeper 作为我们的注册表来管理集群中的服务。提供者可以像这样在 Zookeeper 中注册他们的服务：

```xml
<dubbo:registry address="zookeeper://127.0.0.1:2181"/>
```

请注意，我们需要POM中的这些附加依赖项：

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.11</version>
</dependency>
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.10</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.zookeeper" AND a%3A"zookeeper")和[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.101tec" AND a%3A"zkclient")找到最新版本的zookeeper依赖项和zkclient。

### 7.1. 负载均衡

目前，该框架支持一些负载均衡策略：

-   随机的
-   循环法
-   最不活跃
-   一致性哈希。

在以下示例中，我们有两个服务实现作为集群中的提供者。使用循环方法路由请求。

首先，让我们设置服务提供者：

```java
@Before
public void initRemote() {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.submit(() -> {
        ClassPathXmlApplicationContext remoteContext 
          = new ClassPathXmlApplicationContext("cluster/provider-app-default.xml");
        remoteContext.start();
    });
    executorService.submit(() -> {
        ClassPathXmlApplicationContext backupRemoteContext
          = new ClassPathXmlApplicationContext("cluster/provider-app-special.xml");
        backupRemoteContext.start();
    });
}
```

现在我们有一个标准的“快速提供者”，它会立即响应，还有一个特殊的“慢速提供者”，它在每次请求时休眠 5 秒。

使用循环策略运行 6 次后，我们预计平均响应时间至少为 2.5 秒：

```java
@Test
public void givenProviderCluster_whenConsumerSaysHi_thenResponseBalanced() {
    ClassPathXmlApplicationContext localContext
      = new ClassPathXmlApplicationContext("cluster/consumer-app-lb.xml");
    localContext.start();
    GreetingsService greetingsService
      = (GreetingsService) localContext.getBean("greetingsService");

    List<Long> elapseList = new ArrayList<>(6);
    for (int i = 0; i < 6; i++) {
        long current = System.currentTimeMillis();
        String hiMessage = greetingsService.sayHi("baeldung");
        assertNotNull(hiMessage);
        elapseList.add(System.currentTimeMillis() - current);
    }

    OptionalDouble avgElapse = elapseList
      .stream()
      .mapToLong(e -> e)
      .average();
    assertTrue(avgElapse.isPresent());
    assertTrue(avgElapse.getAsDouble() > 2500.0);
}
```

此外，采用动态负载平衡。下一个示例演示，使用循环策略，消费者会在新服务提供者上线时自动选择新服务提供者作为候选者。

“慢速提供者”在系统启动后 2 秒后注册：

```java
@Before
public void initRemote() {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.submit(() -> {
        ClassPathXmlApplicationContext remoteContext
          = new ClassPathXmlApplicationContext("cluster/provider-app-default.xml");
        remoteContext.start();
    });
    executorService.submit(() -> {
        SECONDS.sleep(2);
        ClassPathXmlApplicationContext backupRemoteContext
          = new ClassPathXmlApplicationContext("cluster/provider-app-special.xml");
        backupRemoteContext.start();
        return null;
    });
}
```

消费者每秒调用一次远程服务。运行 6 次后，我们预计平均响应时间大于 1.6 秒：

```java
@Test
public void givenProviderCluster_whenConsumerSaysHi_thenResponseBalanced()
  throws InterruptedException {
    ClassPathXmlApplicationContext localContext
      = new ClassPathXmlApplicationContext("cluster/consumer-app-lb.xml");
    localContext.start();
    GreetingsService greetingsService
      = (GreetingsService) localContext.getBean("greetingsService");
    List<Long> elapseList = new ArrayList<>(6);
    for (int i = 0; i < 6; i++) {
        long current = System.currentTimeMillis();
        String hiMessage = greetingsService.sayHi("baeldung");
        assertNotNull(hiMessage);
        elapseList.add(System.currentTimeMillis() - current);
        SECONDS.sleep(1);
    }

    OptionalDouble avgElapse = elapseList
      .stream()
      .mapToLong(e -> e)
      .average();
 
    assertTrue(avgElapse.isPresent());
    assertTrue(avgElapse.getAsDouble() > 1666.0);
}
```

请注意，负载均衡器可以在消费者端和提供者端进行配置。这是消费者端配置的示例：

```xml
<dubbo:reference interface="com.baeldung.dubbo.remote.GreetingsService"
  id="greetingsService" loadbalance="roundrobin" />
```

### 7.2. 容错

Dubbo 支持多种容错策略，包括：

-   故障转移
-   故障安全
-   快速失败
-   故障回复
-   分叉。

在故障转移的情况下，当一个提供者失败时，消费者可以尝试使用集群中的其他一些服务提供者。

服务提供者的容错策略配置如下：

```xml
<dubbo:service interface="com.baeldung.dubbo.remote.GreetingsService"
  ref="greetingsService" cluster="failover"/>
```

为了演示实际的服务故障转移，让我们创建GreetingsService的故障转移实现：

```java
public class GreetingsFailoverServiceImpl implements GreetingsService {

    @Override
    public String sayHi(String name) {
        return "hi, failover " + name;
    }
}
```

我们可以回想一下，我们的特殊服务实现GreetingsServiceSpecialImpl为每个请求休眠 5 秒。

当任何超过 2 秒的响应被视为消费者的请求失败时，我们有一个故障转移场景：

```xml
<dubbo:reference interface="com.baeldung.dubbo.remote.GreetingsService"
  id="greetingsService" retries="2" timeout="2000" />
```

启动两个提供程序后，我们可以使用以下代码片段验证故障转移行为：

```java
@Test
public void whenConsumerSaysHi_thenGotFailoverResponse() {
    ClassPathXmlApplicationContext localContext
      = new ClassPathXmlApplicationContext(
      "cluster/consumer-app-failtest.xml");
    localContext.start();
    GreetingsService greetingsService
      = (GreetingsService) localContext.getBean("greetingsService");
    String hiMessage = greetingsService.sayHi("baeldung");

    assertNotNull(hiMessage);
    assertEquals("hi, failover baeldung", hiMessage);
}
```

## 8.总结

在本教程中，我们对 Dubbo 进行了一些尝试。大多数用户都被它的简洁和丰富强大的功能所吸引。

除了我们在本文中介绍的内容之外，该框架还有许多特性有待探索，例如参数验证、通知和回调、通用实现和引用、远程结果分组和合并、服务升级和向后兼容，仅举几例一些。
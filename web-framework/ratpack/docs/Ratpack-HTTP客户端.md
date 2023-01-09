## 1. 简介

在过去的几年里，我们见证了用Java创建应用程序的函数式和反应式方式的兴起。Ratpack 提供了一种按照相同思路创建 HTTP 应用程序的方法。

由于它使用 Netty 来满足其网络需求，因此它是完全异步和非阻塞的。Ratpack 还通过提供配套测试库来支持测试。

在本教程中，我们将介绍 Ratpack HTTP 客户端和相关组件的使用。

[在这样做的过程中，我们将尝试从Ratpack 介绍性教程](https://www.baeldung.com/ratpack)结束时离开的地方进一步加深我们的理解。

## 2.Maven依赖

首先，让我们添加所需的[Ratpack 依赖](https://search.maven.org/search?q=io.ratpack)项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-core</artifactId>
    <version>1.5.4</version>
</dependency>
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-test</artifactId>
    <version>1.5.4</version>
    <scope>test</scope>
</dependency>
```

有趣的是，我们只需要这么多来创建和测试我们的应用程序。

然而，我们总是可以选择使用其他 Ratpack 库来添加和扩展。

## 三、背景

在我们深入研究之前，让我们先了解一下 Ratpack 应用程序中的工作方式。

### 3.1. 基于处理程序的方法

Ratpack 使用基于处理程序的方法来处理请求。这个想法本身很简单。

在最简单的形式中，我们可以让每个处理程序为每个特定路径上的请求提供服务：

```java
public class FooHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.getResponse().send("Hello Foo!");
    }
}
```

### 3.2. 链、注册表和上下文

处理程序使用 Context 对象与传入请求进行交互。通过它，我们可以访问 HTTP 请求和响应，以及委托给其他处理程序的能力。

以以下处理程序为例：

```java
Handler allHandler = context -> {
    Long id = Long.valueOf(context.getPathTokens().get("id"));
    Employee employee = new Employee(id, "Mr", "NY");
    context.next(Registry.single(Employee.class, employee));
};
```

该处理程序负责进行一些预处理，将结果放入注册表中，然后将请求委托给其他处理程序。

通过使用Registry ，我们可以实现处理程序间的通信。以下处理程序使用对象类型从Registry查询先前计算的结果：

```java
Handler empNameHandler = ctx -> {
    Employee employee = ctx.get(Employee.class);
    ctx.getResponse()
      .send("Name of employee with ID " + employee.getId() + " is " + employee.getName());
};
```

我们应该记住，在生产应用程序中，我们会将这些处理程序作为单独的类，以便更好地抽象、调试和开发复杂的业务逻辑。

现在我们可以在链 中使用这些处理程序来创建复杂的 自定义请求处理管道。

例如：

```java
Action<Chain> chainAction = chain -> chain.prefix("employee/:id", empChain -> {
    empChain.all(allHandler)
      .get("name", empNameHandler)
      .get("title", empTitleHandler);
});
```

我们可以通过使用Chain中的insert(..) 方法将多个链组合在一起 并让每个链负责不同的关注点来进一步采用这种方法。

以下测试用例展示了这些结构的使用：

```java
@Test
public void givenAnyUri_GetEmployeeFromSameRegistry() throws Exception {
    EmbeddedApp.fromHandlers(chainAction)
      .test(testHttpClient -> {
          assertEquals("Name of employee with ID 1 is NY", testHttpClient.get("employee/1/name")
            .getBody()
            .getText());
          assertEquals("Title of employee with ID 1 is Mr", testHttpClient.get("employee/1/title")
            .getBody()
            .getText());
      });
}
```

在这里，我们使用 Ratpack 的测试库来单独测试我们的功能，而无需启动实际的服务器。

## 4. 带有 Ratpack 的 HTTP

### 4.1. 致力于异步

HTTP 协议本质上是同步的。因此，通常情况下，Web 应用程序是同步的，因此是阻塞的。这是一种资源密集型方法，因为我们为每个传入请求创建一个线程。

我们宁愿创建非阻塞和异步应用程序。这将确保我们只需要使用一小部分线程来处理请求。

### 4.2. 回调函数

在处理异步 API 时，我们通常会为接收者提供一个回调函数，以便将数据返回给调用者。 在Java中，这通常采用匿名内部类和 lambda 表达式的形式。但是随着我们的应用程序扩展，或者因为有多个嵌套的异步调用，这样的解决方案将难以维护并且更难调试。

Ratpack 以Promise的形式提供了一个优雅的解决方案来处理这种复杂性。

### 4.3. 鼠群承诺

Ratpack Promise 可以被认为类似于JavaFuture对象。它本质上是一个稍后可用的值的表示。

我们可以指定值可用时将通过的操作管道。每个操作都会返回一个新的 promise 对象，即先前 promise 对象的转换版本。

正如我们所料，这导致线程之间的上下文切换很少，并使我们的应用程序高效。

以下是使用Promise的处理程序实现：

```java
public class EmployeeHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        EmployeeRepository repository = ctx.get(EmployeeRepository.class);
        Long id = Long.valueOf(ctx.getPathTokens().get("id"));
        Promise<Employee> employeePromise = repository.findEmployeeById(id);
        employeePromise.map(employee -> employee.getName())
          .then(name -> ctx.getResponse()
          .send(name));
    }
}
```

我们需要记住，当我们定义如何处理最终值时，承诺特别有用。我们可以通过调用终端操作then(Action) 来做到这一点。

如果我们需要发回一个承诺但数据源是同步的，我们仍然可以这样做：

```java
@Test
public void givenSyncDataSource_GetDataFromPromise() throws Exception {
    String value = ExecHarness.yieldSingle(execution -> Promise.sync(() -> "Foo"))
      .getValueOrThrow();
    assertEquals("Foo", value);
}
```

### 4.4. HTTP 客户端

Ratpack 提供了一个异步 HTTP 客户端，它的实例可以从服务器注册表中检索。但是，我们鼓励创建和使用替代实例，因为默认实例不使用连接池并且具有相当保守的默认值。

我们可以使用of(Action)方法创建一个实例，该方法将HttpClientSpec类型 的Action 作为参数。

使用它，我们可以根据我们的喜好调整我们的客户：

```java
HttpClient httpClient = HttpClient.of(httpClientSpec -> {
    httpClientSpec.poolSize(10)
      .connectTimeout(Duration.of(60, ChronoUnit.SECONDS))
      .maxContentLength(ServerConfig.DEFAULT_MAX_CONTENT_LENGTH)
      .responseMaxChunkSize(16384)
      .readTimeout(Duration.of(60, ChronoUnit.SECONDS))
      .byteBufAllocator(PooledByteBufAllocator.DEFAULT);
});
```

正如我们可能已经通过其异步性质猜测的那样，HttpClient返回一个Promise对象。因此，我们可以以非阻塞方式拥有复杂的操作管道。

为了便于说明，让我们让客户使用此HttpClient调用我们的EmployeeHandler：

```java
public class RedirectHandler implements Handler {
 
    @Override
    public void handle(Context ctx) throws Exception {
        HttpClient client = ctx.get(HttpClient.class);
        URI uri = URI.create("http://localhost:5050/employee/1");
        Promise<ReceivedResponse> responsePromise = client.get(uri);
        responsePromise.map(response -> response.getBody()
          .getText()
          .toUpperCase())
          .then(responseText -> ctx.getResponse()
            .send(responseText));
    }
}
```

一个快速的[cURL](https://www.baeldung.com/curl-rest)调用将确认我们得到了预期的响应：

```plaintext
curl http://localhost:5050/redirect
JANE DOE
```

## 5.总结

在本文中，我们回顾了 Ratpack 中可用的主要库结构，它们使我们能够开发非阻塞和异步 Web 应用程序。

我们看了一下 Ratpack HttpClient 和伴随的Promise 类，它代表了 Ratpack 中所有异步的东西。我们还看到了如何使用附带的TestHttpClient轻松测试我们的 HTTP 应用程序 。
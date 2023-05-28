## 1. 简介

之前，我们[展示了](https://www.baeldung.com/ratpack)如何使用 Ratpack 构建高性能和响应式应用程序。

在本文中，我们将了解如何将 Netflix Hystrix 与 Ratpack 应用程序集成。

Netflix Hystrix 通过隔离访问点以停止级联故障并提供容错回退选项来帮助控制分布式服务之间的交互。它可以帮助我们构建更具弹性的应用程序。请参阅我们[对 Hystrix 的介绍](https://www.baeldung.com/introduction-to-hystrix)以进行快速回顾。

因此，这就是我们将如何使用它——我们将使用 Hystrix 提供的这些有用功能来增强我们的 Ratpack 应用程序。

## 2.Maven依赖

要将 Hystrix 与 Ratpack 一起使用，我们需要在项目pom.xml中添加 ratpack-hystrix 依赖项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-hystrix</artifactId>
    <version>1.4.6</version>
</dependency>
```

[最新版本的 ratpack-hystrix 可以在这里](https://search.maven.org/classic/#search|ga|1|a%3A"ratpack-hystrix" AND g%3A"io.ratpack")找到。ratpack-hystrix 包括[ratpack-core](https://search.maven.org/classic/#search|ga|1|g%3A"io.ratpack" AND a%3A"ratpack-core")和[hystrix-core](https://search.maven.org/classic/#search|ga|1|g%3A"com.netflix.hystrix" AND a%3A"hystrix-core")。

要使用 Ratpack 的反应特性，我们还需要 ratpack-rx：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-rx</artifactId>
    <version>1.4.6</version>
</dependency>
```

最新版本的 ratpack-rx 可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"io.ratpack" AND a%3A"ratpack-rx")找到。

## 3. 使用 Hystrix 命令服务

使用 Hystrix 时，底层服务通常包装在[HystrixCommand](https://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/HystrixCommand.html)或[HystrixObservableCommand](https://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/HystrixObservableCommand.html)中。Hystrix 支持以同步、异步和反应的方式执行这些命令。其中只有reactive是非阻塞的，官方推荐。

[在以下示例中，我们将构建一些从Github REST API](https://docs.github.com/en/rest)获取配置文件的端点。

### 3.1. 响应式命令执行

首先，让我们使用 Hystrix 构建一个响应式后端服务：

```java
public class HystrixReactiveHttpCommand extends HystrixObservableCommand<String> {

    //...

    @Override
    protected Observable<String> construct() {
        return RxRatpack.observe(httpClient
          .get(uri, r -> r.headers(h -> h.add("User-Agent", "Baeldung HttpClient")))
          .map(res -> res.getBody().getText()));
    }

    @Override
    protected Observable<String> resumeWithFallback() {
        return Observable.just("eugenp's reactive fallback profile");
    }
}
```

这里使用 Ratpack 响应式[HttpClient](https://hc.apache.org/httpcomponents-client-5.1.x/index.html)来发出 GET 请求。HystrixReactiveHttpCommand可以作为反应式处理程序执行：

```java
chain.get("rx", ctx -> 
  new HystrixReactiveHttpCommand(
    ctx.get(HttpClient.class), eugenGithubProfileUri, timeout)
    .toObservable()
    .subscribe(ctx::render));
```

端点可以通过以下测试进行验证：

```java
@Test
public void whenFetchReactive_thenGotEugenProfile() {
    assertThat(appUnderTest.getHttpClient().getText("rx"), 
      containsString("www.baeldung.com"));
}
```

### 3.2. 异步命令执行

HystrixCommand的异步执行在线程池中对命令进行排队并返回Future：

```java
chain.get("async", ctx -> ctx.render(
  new HystrixAsyncHttpCommand(eugenGithubProfileUri, timeout)
    .queue()
    .get()));
```

HystrixAsyncHttpCommand看起来像：

```java
public class HystrixAsyncHttpCommand extends HystrixCommand<String> {

    //...

    @Override
    protected String run() throws Exception {
        return EntityUtils.toString(HttpClientBuilder.create()
          .setDefaultRequestConfig(requestConfig)
          .setDefaultHeaders(Collections.singleton(
            new BasicHeader("User-Agent", "Baeldung Blocking HttpClient")))
          .build().execute(new HttpGet(uri)).getEntity());
    }

    @Override
    protected String getFallback() {
        return "eugenp's async fallback profile";
    }

}
```

这里我们使用阻塞[HttpClient](https://hc.apache.org/httpcomponents-client-5.1.x/examples.html)而不是非阻塞 HttpClient ，因为我们希望 Hystrix 控制实际命令的执行超时，以便我们在从Future获得响应时不需要自己处理它。这也允许 Hystrix 回退或缓存我们的请求。

异步执行也会产生预期的结果：

```java
@Test
public void whenFetchAsync_thenGotEugenProfile() {
    assertThat(appUnderTest.getHttpClient().getText("async"),
      containsString("www.baeldung.com"));
}
```

### 3.3. 同步命令执行

同步执行直接在当前线程中执行命令：

```java
chain.get("sync", ctx -> ctx.render(
  new HystrixSyncHttpCommand(eugenGithubProfileUri, timeout).execute()));
```

HystrixSyncHttpCommand的实现几乎与HystrixAsyncHttpCommand相同，只是我们给它一个不同的回退结果。当不回退时，它的行为与反应式和异步执行一样：

```java
@Test
public void whenFetchSync_thenGotEugenProfile() {
    assertThat(appUnderTest.getHttpClient().getText("sync"),
      containsString("www.baeldung.com"));
}
```

## 4.指标

通过将[Guice 模块](https://www.baeldung.com/ratpack-google-guice)- [HystrixModule](https://ratpack.io/manual/current/api/ratpack/hystrix/HystrixModule.html)注册到 Ratpack注册表中，我们可以流式传输请求范围内的指标并通过GET端点公开事件流：

```java
serverSpec.registry(
  Guice.registry(spec -> spec.module(new HystrixModule().sse())))
  .handlers(c -> c.get("hystrix", new HystrixMetricsEventStreamHandler()));
```

HystrixMetricsEventStreamHandler帮助以文本/事件流格式流[式](https://ratpack.io/manual/current/api/ratpack/hystrix/HystrixMetricsEventStreamHandler.html)传输 Hystrix 指标，这样我们就可以在[Hystrix Dashboard](https://github.com/Netflix-Skunkworks/hystrix-dashboard)中监控指标。

我们可以设置一个[独立的 Hystrix 仪表板](https://github.com/kennedyoliveira/standalone-hystrix-dashboard)，并将我们的 Hystrix 事件流添加到监视器列表中，以查看我们的 Ratpack 应用程序的执行情况：

[![片段20170815 1](https://www.baeldung.com/wp-content/uploads/2017/08/Snip20170815_1-300x143.png)](https://www.baeldung.com/wp-content/uploads/2017/08/Snip20170815_1.png)

在多次请求我们的 Ratpack 应用程序后，我们可以在仪表板中看到 Hystrix 相关命令。

### 4.1. 引擎盖下

在HystrixModule中，[Hystrix Concurrency Strategy](https://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/strategy/concurrency/HystrixConcurrencyStrategy.html)通过[HystrixPlugin](https://github.com/Netflix/Hystrix/wiki/Plugins)注册到 Hystrix，以使用 Ratpack注册表管理请求上下文。这消除了在每个请求开始之前初始化 Hystrix 请求上下文的必要性。

```java
public class HystrixModule extends ConfigurableModule<HystrixModule.Config> {

    //...
  
    @Override
    protected void configure() {
      try {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(
          new HystrixRegistryBackedConcurrencyStrategy());
      } catch (IllegalStateException e) {
        //...
      }
    }

    //...

}
```

## 5.总结

在这篇快速文章中，我们展示了如何将 Hystrix 集成到 Ratpack 中，以及如何将 Ratpack 应用程序的指标推送到 Hystrix Dashboard，以便更好地查看应用程序性能。
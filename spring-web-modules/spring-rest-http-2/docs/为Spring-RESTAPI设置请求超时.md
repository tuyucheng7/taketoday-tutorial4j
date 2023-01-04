## 1. 概述

在本教程中，我们将探索几种可能的方法来实现Spring REST API的请求超时。

然后我们将讨论每种方法的优点和缺点。请求超时对于防止糟糕的用户体验很有用，特别是如果有一个替代方案我们可以默认为资源花费太长时间。这种设计模式称为[断路器模式](https://www.baeldung.com/spring-cloud-circuit-breaker)，这里不再赘述。

## 2. @Transactional超时

我们可以在数据库调用上实现请求超时的一种方法是利用Spring的@Transactional注解。它有一个我们可以设置的超时属性。此属性的默认值为-1，相当于根本没有任何超时。对于超时值的外部配置，我们必须改用不同的属性timeoutString。

例如，假设我们将这个超时设置为30。如果被注解的方法执行时间超过这个秒数，就会抛出异常。这对于回滚长时间运行的数据库查询可能很有用。

为查看实际效果，我们将编写一个非常简单的JPA Repository层，该层将表示完成时间过长并导致超时的外部服务。这个JpaRepository扩展中有一个耗时的方法：

```java
public interface BookRepository extends JpaRepository<Book, String> {

    default int wasteTime() {
        Stopwatch watch = Stopwatch.createStarted();

        // delay for 2 seconds
        while (watch.elapsed(SECONDS) < 2) {
            int i = Integer.MIN_VALUE;
            while (i < Integer.MAX_VALUE) {
                i++;
            }
        }
    }
}
```

如果我们在超时为1秒的事务中调用我们的wasteTime()方法，则超时将在方法完成执行之前结束：

```java
@GetMapping("/author/transactional")
@Transactional(timeout = 1)
public String getWithTransactionTimeout(@RequestParam String title) {
    bookRepository.wasteTime();
    return bookRepository.findById(title)
        .map(Book::getAuthor)
        .orElse("No book found for this title.");
}
```

调用此端点会导致500 HTTP错误，我们可以将其转换为更有意义的响应。它还需要很少的设置来实现。

但是，这种超时解决方案有一些缺点。

首先，它依赖于具有Spring管理的事务的数据库。其次，它不是全局适用于项目，因为注解必须出现在需要它的每个方法或类上。它也不允许亚秒级精度。最后，它不会在达到超时时缩短请求，因此请求实体仍然需要等待全部时间。

让我们考虑一些备选方案。

## 3. Resilience4j时间限制器

Resilience4j是一个主要管理远程通信容错的库。它的[TimeLimiter模块](https://resilience4j.readme.io/docs/timeout)是我们在这里感兴趣的。

首先，我们必须在项目中包含[resilience4j-timelimiter](https://search.maven.org/artifact/io.github.resilience4j/resilience4j-timelimiter)依赖项：

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-timelimiter</artifactId>
    <version>1.6.1</version>
</dependency>
```

接下来，我们将定义一个简单的TimeLimiter，其超时持续时间为500毫秒：

```java
private TimeLimiter ourTimeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
  .timeoutDuration(Duration.ofMillis(500)).build());
```

我们可以很容易地在外部配置它。

我们可以使用我们的TimeLimiter来包装我们的@Transactional示例使用的相同逻辑：

```java
@GetMapping("/author/resilience4j")
public Callable<String> getWithResilience4jTimeLimiter(@RequestParam String title) {
    return TimeLimiter.decorateFutureSupplier(ourTimeLimiter, () -> CompletableFuture.supplyAsync(() -> {
        bookRepository.wasteTime();
        return bookRepository.findById(title)
            .map(Book::getAuthor)
            .orElse("No book found for this title.");
    }));
}
```

与@Transactional解决方案相比，TimeLimiter具有多项优势。即，它支持亚秒级精度和超时响应的即时通知。但是，我们仍然必须手动将其包含在所有需要超时的端点中。它还需要一些冗长的包装代码，并且它产生的错误仍然是一般的500 HTTP错误。最后，它需要返回一个Callable<String\>而不是原始字符串。

TimeLimiter仅包含[Resilience4j的一部分功能](https://www.baeldung.com/resilience4j)，并与断路器模式很好地交互。

## 4. Spring MVC请求超时

Spring为我们提供了一个名为spring.mvc.async.request-timeout的属性，此属性允许我们以毫秒精度定义请求超时。

让我们定义具有750毫秒超时的属性：

```properties
spring.mvc.async.request-timeout=750
```

此属性是全局的且可在外部配置，但与TimeLimiter解决方案一样，它仅适用于返回Callable的端点。让我们定义一个类似于TimeLimiter示例的端点，但不需要将逻辑包装在Futures中，或提供TimeLimiter：

```java
@GetMapping("/author/mvc-request-timeout")
public Callable<String> getWithMvcRequestTimeout(@RequestParam String title) {
    return () -> {
        bookRepository.wasteTime();
        return bookRepository.findById(title)
            .map(Book::getAuthor)
            .orElse("No book found for this title.");
    };
}
```

我们可以看到代码不那么冗长，当我们定义应用程序属性时，Spring会自动实现配置。一旦达到超时，就会立即返回响应，它甚至会返回更具描述性的503 HTTP错误，而不是通用的500。我们项目中的每个端点都将自动继承此超时配置。

现在让我们考虑另一个选项，它允许我们以更细粒度的方式定义超时。

## 5. WebClient超时

与其为整个端点设置超时，我们可能只想为单个外部调用设置超时。[WebClient](https://www.baeldung.com/spring-5-webclient)是Spring的响应式Web客户端，它允许我们配置响应超时。

也可以在Spring的旧[RestTemplate对象](https://www.baeldung.com/rest-template)上配置超时；但是，大多数开发人员现在更喜欢WebClient而不是RestTemplate。

要使用WebClient，我们必须首先将Spring的[WebFlux依赖项](https://search.maven.org/search?q=a:spring-boot-starter-webflux)添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.4.2</version>
</dependency>
```

让我们定义一个响应超时为250毫秒的WebClient，我们可以使用它通过其基本URL中的localhost调用我们自己：

```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
        .baseUrl("http://localhost:8080")
        .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().responseTimeout(Duration.ofMillis(250))
         ))
        .build();
}
```

显然，我们可以很容易地在外部配置这个超时值。我们还可以在外部配置基本URL，以及其他几个可选属性。

现在我们可以将我们的WebClient注入我们的控制器，并使用它来调用我们自己的/transactional端点，它仍然有1秒的超时。由于我们将WebClient配置为在250毫秒内超时，我们应该看到它的失败速度比1秒快得多。

这是我们的新端点：

```java
@GetMapping("/author/webclient")
public String getWithWebClient(@RequestParam String title) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/author/transactional")
            .queryParam("title", title)
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();
}
```

调用此端点后，我们可以看到我们确实以500 HTTP错误响应的形式收到了WebClient的超时。我们还可以检查日志以查看下游的@Transactional超时，但如果我们调用外部服务而不是本地主机，它的超时将被远程打印。

可能需要为不同的后端服务配置不同的请求超时，并且可以使用此解决方案。此外，WebClient返回的发布者的Mono或Flux响应包含大量[错误处理方法](https://www.baeldung.com/spring-webflux-errors)，用于处理一般超时错误响应。

## 6. 总结

在本文中，我们探索了几种实现请求超时的不同解决方案。在决定使用哪一个时，有几个因素需要考虑。

如果我们想对我们的数据库请求设置超时，我们可能需要使用Spring的@Transactional方法及其超时属性。如果我们试图与更广泛的断路器模式集成，使用Resilience4j的TimeLimiter会很有意义。使用Spring MVC请求超时属性最适合为所有请求设置全局超时，但我们也可以使用WebClient轻松地为每个资源定义更细粒度的超时。
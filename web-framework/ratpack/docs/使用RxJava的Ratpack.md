## 1. 简介

[RxJava](https://www.baeldung.com/rxjava-tutorial)是最流行的响应式编程库之一。

Ratpack 是一组Java库，用于创建基于 Netty 构建的精简而强大的 Web 应用程序[。](https://www.baeldung.com/ratpack)

在本教程中，我们将讨论将 RxJava 合并到 Ratpack 应用程序中以创建一个漂亮的响应式 Web 应用程序。

## 2.Maven依赖

现在，我们首先需要[ratpack-core](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-core) 和 [ratpack-rx](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-rx) 依赖项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-core</artifactId>
    <version>1.6.0</version>
</dependency>
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-rx</artifactId>
    <version>1.6.0</version>
</dependency>

```

请注意，顺便说一句， ratpack-rx为我们导入了rxjava依赖项。

## 3.初始设置

RxJava 使用其插件系统支持第三方库的集成。因此，我们可以将不同的执行策略融入到 RxJava 的执行模型中。 

Ratpack 通过我们在启动时初始化的RxRatpack插入这个执行模型：

```java
RxRatpack.initialise();

```

现在，重要的是要注意每次 JVM 运行只需要调用一次该方法。

结果是我们将能够将 RxJava 的Observable映射到 RxRatpack 的Promise类型，反之亦然。

## 4. Observable到Promises _

我们可以将RxJava 中的Observable转换为 Ratpack Promise。

但是，有一点不匹配。看，Promise 发出单个值，但 Observable 可以发出它们的流。

RxRatpack 通过提供两种不同的方法来处理这个问题： promiseSingle()和promise()。

因此，假设我们有一个名为MovieService的服务，它在 getMovie() 上发出一个承诺。 我们会使用 promiseSingle()因为我们知道它只会发出一次：

```java
Handler movieHandler = (ctx) -> {
    MovieService movieSvc = ctx.get(MovieService.class);
    Observable<Movie> movieObs = movieSvc.getMovie();
    RxRatpack.promiseSingle(movieObs)
      .then(movie -> ctx.render(Jackson.json(movie)));
};
```

另一方面，如果 getMovies()可以返回电影结果流，我们将使用promise()：

```java
Handler moviesHandler = (ctx) -> {
    MovieService movieSvc = ctx.get(MovieService.class);
    Observable<Movie> movieObs = movieSvc.getMovies();
    RxRatpack.promise(movieObs)
      .then(movie -> ctx.render(Jackson.json(movie)));
};
```

然后，我们可以像往常一样将这些处理程序添加到我们的 Ratpack 服务器：

```java
RatpackServer.start(def -> def.registryOf(rSpec -> rSpec.add(MovieService.class, new MovieServiceImpl()))
  .handlers(chain -> chain
    .get("movie", movieHandler)
    .get("movies", moviesHandler)));
```

## 5. Promises到Observable s

相反，我们可以将Ratpack 中的Promise类型映射回 RxJava Observable。 

RxRatpack 同样有两个方法： observe()和observeEach()。

在这种情况下，我们假设我们有一个返回Promise而不是 Observable 的电影服务。

对于我们的 getMovie()， 我们将使用 observe()：

```java
Handler moviePromiseHandler = ctx -> {
    MoviePromiseService promiseSvc = ctx.get(MoviePromiseService.class);
    Promise<Movie> moviePromise = promiseSvc.getMovie();
    RxRatpack.observe(moviePromise)
      .subscribe(movie -> ctx.render(Jackson.json(movie)));
};
```

当我们取回一个列表时，就像使用getMovies()一样，我们将使用 observeEach()：

```java
Handler moviesPromiseHandler = ctx -> {
    MoviePromiseService promiseSvc = ctx.get(MoviePromiseService.class);
    Promise<List<Movie>> moviePromises = promiseSvc.getMovies();
    RxRatpack.observeEach(moviePromises)
        .toList()
        .subscribe(movie -> ctx.render(Jackson.json(movie)));
};
```

然后，我们可以再次按预期添加处理程序：

```java
RatpackServer.start(def -> def.registryOf(regSpec -> regSpec
  .add(MoviePromiseService.class, new MoviePromiseServiceImpl()))
    .handlers(chain -> chain
      .get("movie", moviePromiseHandler)
      .get("movies", moviesPromiseHandler)));
```

## 6.并行处理

RxRatpack 借助 fork()和forkEach()方法支持并行性。

它遵循我们已经在每个方面看到的模式。

fork()采用单个 Observable并将其执行并行化到不同的计算线程上。然后，它会自动将数据绑定回原始执行。

另一方面，forkEach()对Observable的值流 发出的每个元素执行相同的操作 。

让我们想象一下，我们想要将我们的电影片名大写，而这是一项昂贵的操作。

简而言之，我们可以使用forkEach()将 each 的执行卸载到线程池中：

```java
Observable<Movie> movieObs = movieSvc.getMovies();
Observable<String> upperCasedNames = movieObs.compose(RxRatpack::forkEach)
  .map(movie -> movie.getName().toUpperCase())
  .serialize();
```

## 7.隐式错误处理

最后，隐式错误处理是 RxJava 集成中的关键特性之一。

默认情况下，RxJava 可观察序列会将任何异常转发给执行上下文异常处理程序。因此，不需要在可观察序列中定义错误处理程序。

因此，我们可以配置 Ratpack 来处理 RxJava 引发的这些错误。

例如，我们希望在 HTTP 响应中打印每个错误。

请注意，我们通过Observable抛出的异常会被我们的ServerErrorHandler捕获和处理 ：

```java
RatpackServer.start(def -> def.registryOf(regSpec -> regSpec
  .add(ServerErrorHandler.class, (ctx, throwable) -> {
        ctx.render("Error caught by handler : " + throwable.getMessage());
    }))
  .handlers(chain -> chain
    .get("error", ctx -> {
        Observable.<String> error(new Exception("Error from observable")).subscribe(s -> {});
    })));
```

但是请注意，任何订阅者级别的错误处理都具有优先权。 如果我们的 Observable想做自己的错误处理，它可以，但因为它没有，异常会渗透到 Ratpack。

## 八. 总结

在本文中，我们讨论了如何使用 Ratpack 配置 RxJava。

我们探索了 RxJava 中的Observable到 Ratpack 中的Promise类型的转换，反之亦然。我们还研究了集成支持的并行性和隐式错误处理功能。
## 1. 概述

[Retrofit](https://square.github.io/retrofit/)是适用于 Android 和Java的类型安全 HTTP 客户端 – 由 Square([Dagger](https://square.github.io/dagger/)，[Okhttp](https://square.github.io/okhttp/))开发。

在本文中，我们将解释如何使用 Retrofit，重点介绍其最有趣的功能。更值得注意的是，我们将讨论同步和异步 API，如何将其与身份验证、日志记录和一些良好的建模实践一起使用。

## 2.设置示例

我们将从添加 Retrofit 库和 Gson 转换器开始：

```xml
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>retrofit</artifactId>
    <version>2.3.0</version>
</dependency>  
<dependency>  
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>converter-gson</artifactId>
    <version>2.3.0</version>
</dependency>
```

对于最新版本，请查看 Maven Central 存储库上的[Retrofit](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.retrofit2" AND a%3A"retrofit")和[converter-gson](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.retrofit2" AND a%3A"converter-gson")。

## 3. API建模

Retrofit 将 REST 端点建模为Java接口，使它们非常易于理解和使用。

我们将从 GitHub为[用户 API建模；](https://api.github.com/users)这有一个以 JSON 格式返回的GET端点：

```java
{
  login: "mojombo",
  id: 1,
  url: "https://api.github.com/users/mojombo",
  ...
}
```

Retrofit 通过在基本 URL 上建模并使接口从 REST 端点返回实体来工作。

为了简单起见，我们将通过对我们的用户类建模来获取一小部分 JSON，该类将在我们收到值时获取值：

```java
public class User {
    private String login;
    private long id;
    private String url;
    // ...

    // standard getters an setters

}
```

我们可以看到，我们只为这个例子获取了属性的一个子集。Retrofit 不会抱怨缺少属性——因为它只映射我们需要的东西，如果我们要添加不在 JSON 中的属性，它甚至不会抱怨。

现在我们可以转到界面建模，并解释一些 Retrofit 注解：

```java
public interface UserService {

    @GET("/users")
    public Call<List<User>> getUsers(
      @Query("per_page") int per_page, 
      @Query("page") int page);

    @GET("/users/{username}")
    public Call<User> getUser(@Path("username") String username);
}
```

带有注解的元数据足以让工具生成有效的实现。

@GET注解告诉客户端使用哪个 HTTP 方法以及哪个资源，例如，通过提供“https://api.github.com”的基本 URL，它会将请求发送到“https://api .github.com/用户”。

相对 URL 上的前导“/”告诉 Retrofit 它是主机上的绝对路径。

另一件需要注意的事情是，我们使用完全可选的@Query参数，如果我们不需要它们，可以将其作为 null 传递，如果它们没有值，该工具将忽略这些参数。

最后但并非最不重要的一点是，@Path允许我们指定将放置的路径参数，而不是我们在路径中使用的标记。

## 4.同步/异步API

要构建 HTTP 请求调用，我们需要先构建我们的 Retrofit 对象：

```java
OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
Retrofit retrofit = new Retrofit.Builder()
  .baseUrl("https://api.github.com/")
  .addConverterFactory(GsonConverterFactory.create())
  .client(httpClient.build())
  .build();
```

Retrofit 为构建我们所需的对象提供了一个方便的构建器。它需要将用于每个服务调用的基本 URL 和一个转换器工厂——负责解析我们发送的数据以及我们得到的响应。

在此示例中，我们将使用GsonConverterFactory，它将我们的 JSON 数据映射到我们之前定义的User类。

重要的是要注意不同的工厂服务于不同的目的，所以请记住，我们也可以将工厂用于 XML、原型缓冲区，甚至可以为自定义协议创建一个工厂。对于已经实施的工厂列表，我们可以在[这里查看](https://github.com/square/retrofit/tree/master/retrofit-converters)。

最后一个依赖项是OKHttpClient——它是用于 Android 和Java应用程序的 HTTP 和 HTTP/2 客户端。这将负责连接到服务器以及信息的发送和检索。我们还可以为每个调用添加标头和拦截器，我们将在身份验证部分中看到它们。

现在我们有了 Retrofit 对象，我们可以构建我们的服务调用，让我们看看如何以同步方式执行此操作：

```java
UserService service = retrofit.create(UserService.class);
Call<User> callSync = service.getUser("eugenp");

try {
    Response<User> response = callSync.execute();
    User user = response.body();
} catch (Exception ex) { ... }
```

在这里，我们可以看到 Retrofit 如何根据我们之前的注解，通过注入发出请求所需的代码来处理我们服务接口的构建。

之后，我们得到一个Call<User>对象，该对象用于执行对 GitHub API 的请求。这里最重要的方法是execute，它用于同步执行调用，并在传输数据时阻塞当前线程。

调用成功执行后，我们可以检索响应的主体 - 已经在用户对象上 - 感谢我们的GsonConverterFactory。

进行同步调用非常容易，但通常，我们使用非阻塞异步请求：

```java
UserService service = retrofit.create(UserService.class);
Call<User> callAsync = service.getUser("eugenp");

callAsync.enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        User user = response.body();
    }

    @Override
    public void onFailure(Call<User> call, Throwable throwable) {
        System.out.println(throwable);
    }
});
```

现在我们不使用 execute 方法，而是使用enqueue方法——它以Callback<User>接口作为参数来处理请求的成功或失败。请注意，这将在单独的线程中执行。

调用成功完成后，我们可以像之前一样检索正文。

## 5. 制作可重用的ServiceGenerator类

现在我们看到了如何构建我们的 Retrofit 对象以及如何使用 API，我们可以看到我们不想一遍又一遍地编写构建器。

我们想要的是一个可重用的类，它允许我们创建这个对象一次并在我们的应用程序的生命周期中重用它：

```java
public class GitHubServiceGenerator {

    private static final String BASE_URL = "https://api.github.com/";

    private static Retrofit.Builder builder
      = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient
      = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
```

创建 Retrofit 对象的所有逻辑现在都移到了这个GitHubServiceGenerator类，这使它成为一个可持续的客户端类，可以防止代码重复。

这是一个如何使用它的简单示例：

```java
UserService service 
  = GitHubServiceGenerator.createService(UserService.class);
```

例如，现在如果我们要创建一个RepositoryService，我们可以重用这个类并简化创建。

在下一节中，我们将扩展它并添加身份验证功能。

## 6.认证

大多数 API 都有一些身份验证来保护对其的访问。

考虑到我们之前的生成器类，我们将添加一个创建服务方法，该方法采用带有Authorization标头的 JWT 令牌：

```java
public static <S> S createService(Class<S> serviceClass, final String token ) {
   if ( token != null ) {
       httpClient.interceptors().clear();
       httpClient.addInterceptor( chain -> {
           Request original = chain.request();
           Request request = original.newBuilder()
             .header("Authorization", token)
             .build();
           return chain.proceed(request);
       });
       builder.client(httpClient.build());
       retrofit = builder.build();
   }
   return retrofit.create(serviceClass);
}
```

要为我们的请求添加标头，我们需要使用OkHttp的拦截器功能；我们通过使用我们之前定义的构建器并通过重建 Retrofit 对象来做到这一点。

请注意，这是一个简单的身份验证示例，但通过使用拦截器，我们可以使用任何身份验证，例如 OAuth、用户/密码等。

## 7. 日志记录

在本节中，我们将进一步扩展GitHubServiceGenerator的日志记录功能，这对于每个项目的调试目的都非常重要。

我们将使用我们之前的拦截器知识，但我们需要一个额外的依赖项，即来自 OkHttp 的HttpLoggingInterceptor，让我们将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>logging-interceptor</artifactId>
    <version>3.9.0</version>
</dependency>
```

现在让我们扩展我们的GitHubServiceGenerator类：

```java
public class GitHubServiceGenerator {

    private static final String BASE_URL = "https://api.github.com/";

    private static Retrofit.Builder builder
      = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient
      = new OkHttpClient.Builder();

    private static HttpLoggingInterceptor logging
      = new HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC);

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final String token) {
        if (token != null) {
            httpClient.interceptors().clear();
            httpClient.addInterceptor( chain -> {
                Request original = chain.request();
                Request.Builder builder1 = original.newBuilder()
                  .header("Authorization", token);
                Request request = builder1.build();
                return chain.proceed(request);
            });
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }
}
```

这是我们类的最终形式，我们可以看到我们是如何添加HttpLoggingInterceptor的，我们将它设置为基本日志记录，它将记录发出请求所花费的时间、端点、每个请求的状态等。

重要的是看一下我们如何检查拦截器是否存在，这样我们就不会不小心添加了两次。

## 八. 总结

在这份详尽的指南中，我们通过关注其同步/异步 API、建模、身份验证和日志记录的一些最佳实践，了解了优秀的 Retrofit 库。

该库可以以非常复杂和有用的方式使用；有关 RxJava 的高级用例，请查看[本教程](https://www.baeldung.com/retrofit-rxjava)。
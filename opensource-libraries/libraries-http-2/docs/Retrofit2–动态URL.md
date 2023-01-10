## 1. 概述

在这个简短的教程中，我们将学习如何在[Retrofit2](https://www.baeldung.com/retrofit)中创建动态 URL。

## 2. @Url注解

在某些情况下，我们需要在运行时在应用程序中使用动态 URL。[Retrofit 库](https://search.maven.org/search?q=g: com.squareup.retrofit2 AND a: retrofit)的版本 2引入了 @Url注解，它允许我们为端点传递完整的 URL：

```java
@GET
Call<ResponseBody> reposList(@Url String url);
```

这个注解是基于[OkHttp库中的](https://search.maven.org/search?q=g:com.squareup.okhttp3 AND a:okhttp)[HttpUrl](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/)类，URL地址像页面上的链接一样使用<a href= “” >来解析。使用@Url参数时，我们不需要在@GET 注解中指定地址。

@Url参数替换了 服务实现中的baseUrl：

```java
Retrofit retrofit = new Retrofit.Builder()
  .baseUrl("https://api.github.com/")
  .addConverterFactory(GsonConverterFactory.create()).build();
```

重要的是，如果我们要使用 @Url注解，必须将其设置为服务方法中的第一个参数。

## 3.路径参数

如果我们知道我们的基本 URL 的某些部分将是常量，但我们不知道它的扩展名或将使用的参数数量，我们可以使用@Path注解和 编码 标志：

```java
@GET("{fullUrl}")
Call<List<Contributor>> contributorsList(@Path(value = "fullUrl", encoded = true) String fullUrl);
```

这样，所有“/”都不会被%2F替换，就好像我们没有使用编码参数一样。但是，所有字符“？” 在传递的地址中仍然会被替换为 %3F 。

## 4.总结

Retrofit 库允许我们仅使用@Url注解在应用程序运行时轻松提供动态 URL 。
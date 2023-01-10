## 1. 概述

本文重点介绍如何使用[Retrofit实现一个简单的](https://square.github.io/retrofit/)[RxJava 就绪](https://github.com/ReactiveX/RxJava)REST 客户端。

我们将构建一个与 GitHub API 交互的示例应用程序——使用标准的 Retrofit 方法，然后我们将使用 RxJava 增强它以利用响应式编程的优势。

## 2.普通改造

让我们首先使用 Retrofit 构建一个示例。我们将使用 GitHub API 获取在任何存储库中贡献超过 100 的所有贡献者的排序列表。

### 2.1. Maven 依赖项

要使用 Retrofit 启动一个项目，让我们包含这些 Maven 工件：

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

对于最新版本，请查看Maven Central 存储库上的[retrofit](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.retrofit2" AND a%3A"retrofit")和[converter-gson 。](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.retrofit2" AND a%3A"converter-gson")

### 2.2. API接口

让我们创建一个简单的界面：

```java
public interface GitHubBasicApi {

    @GET("users/{user}/repos")
    Call<List> listRepos(@Path("user") String user);
    
    @GET("repos/{user}/{repo}/contributors")
    Call<List> listRepoContributors(
      @Path("user") String user,
      @Path("repo") String repo);   
}
```

listRepos ()方法检索作为路径参数传递的给定用户的存储库列表。

listRepoContributers ()方法检索给定用户和存储库的贡献者列表，两者都作为路径参数传递。

### 2.3. 逻辑

让我们使用 Retrofit Call对象和普通Java代码来实现所需的逻辑：

```java
class GitHubBasicService {

    private GitHubBasicApi gitHubApi;

    GitHubBasicService() {
        Retrofit retrofit = new Retrofit.Builder()
          .baseUrl("https://api.github.com/")
          .addConverterFactory(GsonConverterFactory.create())
          .build();

        gitHubApi = retrofit.create(GitHubBasicApi.class);
    }

    List<String> getTopContributors(String userName) throws IOException {
        List<Repository> repos = gitHubApi
          .listRepos(userName)
          .execute()
          .body();

        repos = repos != null ? repos : Collections.emptyList();

        return repos.stream()
          .flatMap(repo -> getContributors(userName, repo))
          .sorted((a, b) -> b.getContributions() - a.getContributions())
          .map(Contributor::getName)
          .distinct()
          .sorted()
          .collect(Collectors.toList());
    }

    private Stream<Contributor> getContributors(String userName, Repository repo) {
        List<Contributor> contributors = null;
        try {
            contributors = gitHubApi
              .listRepoContributors(userName, repo.getName())
              .execute()
              .body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        contributors = contributors != null ? contributors : Collections.emptyList();

        return contributors.stream()
          .filter(c -> c.getContributions() > 100);
    }
}
```

## 3. 与 RxJava 集成

Retrofit 允许我们通过使用 Retrofit Call适配器使用自定义处理程序而不是普通的Call对象接收调用结果。这使得在这里使用 RxJava Observables和Flowables成为可能。

### 3.1. Maven 依赖项

要使用 RxJava 适配器，我们需要包含这个 Maven 工件：

```xml
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>adapter-rxjava</artifactId>
    <version>2.3.0</version>
</dependency>
```

对于最新版本，请检查Maven 中央存储库中的[adapter-rxjava](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.retrofit2" AND a%3A"adapter-rxjava")。

### 3.2. 注册 RxJava 调用适配器

让我们将RxJavaCallAdapter添加到构建器中：

```java
Retrofit retrofit = new Retrofit.Builder()
  .baseUrl("https://api.github.com/")
  .addConverterFactory(GsonConverterFactory.create())
  .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
  .build();
```

### 3.3. API接口

此时，我们可以将接口方法的返回类型更改为使用Observable<…>而不是Call<…>。我们可以使用其他 Rx 类型，如Observable、Flowable、Single、Maybe、Completable。

让我们修改我们的 API 接口以使用Observable：

```java
public interface GitHubRxApi {

    @GET("users/{user}/repos")
    Observable<List<Repository>> listRepos(@Path("user") String user);
    
    @GET("repos/{user}/{repo}/contributors")
    Observable<List<Contributer>> listRepoContributors(
      @Path("user") String user,
      @Path("repo") String repo);   
}
```

### 3.4. 逻辑

让我们使用 RxJava 来实现它：

```java
class GitHubRxService {

    private GitHubRxApi gitHubApi;

    GitHubRxService() {
        Retrofit retrofit = new Retrofit.Builder()
          .baseUrl("https://api.github.com/")
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build();

        gitHubApi = retrofit.create(GitHubRxApi.class);
    }

    Observable<String> getTopContributors(String userName) {
        return gitHubApi.listRepos(userName)
          .flatMapIterable(x -> x)
          .flatMap(repo -> gitHubApi.listRepoContributors(userName, repo.getName()))
          .flatMapIterable(x -> x)
          .filter(c -> c.getContributions() > 100)
          .sorted((a, b) -> b.getContributions() - a.getContributions())
          .map(Contributor::getName)
          .distinct();
    }
}
```

## 4. 总结

对比使用RxJava前后的代码，我们发现它在以下几个方面进行了改进：

-   反应性——因为我们的数据现在以流的形式流动，它使我们能够在非阻塞背压下进行异步流处理
-   清晰——由于其声明性
-   简洁——整个操作可以表示为一个操作链

本文中的所有代码都可以[在 GitHub 上找到。](https://github.com/eugenp/tutorials/tree/master/libraries-http)

包com.baeldung.retrofit.basic包含基本改造示例，而包com.baeldung.retrofit. rx 包含与 RxJava 集成的改造示例。
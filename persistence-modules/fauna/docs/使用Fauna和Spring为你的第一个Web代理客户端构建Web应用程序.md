## 1. 简介

在本文中，我们将使用 Spring 和Java17构建由[Fauna 数据库服务提供支持的博客服务的后端。](https://fauna.com/)

## 2.项目设置

在开始构建我们的服务之前，我们需要执行一些初始设置步骤——具体来说，我们需要创建一个 Fauna 数据库和一个空白的 Spring 应用程序。

### 2.1. 创建动物数据库

在开始之前，我们需要一个 Fauna 数据库来使用。如果我们还没有，则需要[使用 Fauna 创建一个新帐户](https://dashboard.fauna.com/accounts/register)。

一旦完成，我们就可以创建一个新的数据库。为其命名和区域，并选择不包含演示数据，因为我们要构建自己的模式：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.39.16.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.39.16.png)

接下来，我们需要创建一个安全密钥以从我们的应用程序访问它。我们可以从数据库中的“安全”选项卡执行此操作：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.42.27-1024x531.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.42.27.png)

在这里，我们需要选择“服务器”的“角色”，并且可以选择为密钥命名。这意味着密钥可以访问这个数据库，但只能访问这个数据库。或者，我们有一个“管理员”选项，可用于访问我们帐户中的任何数据库：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.45.21-1024x730.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.45.21.png)

完成后，我们需要写下我们的秘密。这是访问服务所必需的，但出于安全原因，一旦我们离开此页面就无法再次获得。

### 2.2. 创建一个 Spring 应用程序

一旦我们有了我们的数据库，我们就可以创建我们的应用程序。由于这将是一个 Spring webapp，我们最好从[Spring Initializr](https://start.spring.io/)引导它。

我们要选择使用最新版本的 Spring 和最新的JavaLTS 版本创建 Maven 项目的选项——在撰写本文时，这些是 Spring 2.6.2 和Java17。我们还想选择 Spring Web 和 Spring安全性作为我们服务的依赖项：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.57.39-1024x597.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-17-at-07.57.39.png)

一旦我们在这里完成，我们可以点击“生成”按钮来下载我们的启动项目。

接下来，我们需要将 Fauna 驱动程序添加到我们的项目中。这是通过将对它们的依赖添加到生成的pom.xml文件来完成的：

```xml
<dependency>
    <groupId>com.faunadb</groupId>
    <artifactId>faunadb-java</artifactId>
    <version>4.2.0</version>
    <scope>compile</scope>
</dependency>
```

此时，我们应该能够执行mvn install并让构建成功下载我们需要的一切。

### 2.3. 配置 Fauna 客户端

一旦我们有一个 Spring webapp 可以使用，我们就需要一个 Fauna 客户端来使用数据库。

首先，我们需要做一些配置。为此，我们将向application.properties文件添加两个属性，为我们的数据库提供正确的值：

```plaintext
fauna.region=us
fauna.secret=<Secret>
```

然后，我们需要一个新的 Spring 配置类来构造 Fauna 客户端：

```java
@Configuration
class FaunaConfiguration {
    @Value("https://db.${fauna.region}.fauna.com/")
    private String faunaUrl;

    @Value("${fauna.secret}")
    private String faunaSecret;

    @Bean
    FaunaClient getFaunaClient() throws MalformedURLException {
        return FaunaClient.builder()
          .withEndpoint(faunaUrl)
          .withSecret(faunaSecret)
          .build();
    }
}
```

这使得FaunaClient的实例可用于 Spring 上下文以供其他 bean 使用。

## 3.增加对用户的支持

在向我们的 API 添加对帖子的支持之前，我们需要为将要创作它们的用户提供支持。为此，我们将利用 Spring Security 并将其连接到代表用户记录的 Fauna 集合。

### 3.1. 创建用户集合

我们要做的第一件事是创建集合。这是通过导航到我们数据库中的“收藏”屏幕，使用“新建收藏”按钮并填写表格来完成的。在这种情况下，我们要使用默认设置创建一个“用户”集合：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-18-at-08.16.36-1024x625.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-18-at-08.16.36.png)

接下来，我们将添加一条用户记录。为此，我们按下集合中的“新建文档”按钮并提供以下 JSON：

```json
{
  "username": "baeldung",
  "password": "Pa55word",
  "name": "Baeldung"
}
```

请注意，我们在这里以明文形式存储密码。请记住，这是一种糟糕的做法，这样做只是为了本教程的方便。

最后，我们需要一个索引。任何时候我们想要通过除引用之外的任何字段访问记录，我们都需要创建一个索引来让我们这样做。在这里，我们想通过用户名访问记录。这是通过按“新索引”按钮并填写表格来完成的：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-18-at-09.01.13-1024x985.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-18-at-09.01.13.png)

现在，我们将能够使用“users_by_username”索引编写 FQL 查询来查找我们的用户。例如：

```less
Map(
  Paginate(Match(Index("users_by_username"), "baeldung")),
  Lambda("user", Get(Var("user")))
)
```

以上将返回我们之前创建的记录。

### 3.2. 动物群认证

现在我们在 Fauna 中有了一组用户，我们可以配置 Spring Security 来对此进行身份验证。

为此，我们首先需要一个[UserDetailsService](https://www.baeldung.com/spring-security-authentication-with-a-database)来查找用户与 Fauna 的对比：

```java
public class FaunaUserDetailsService implements UserDetailsService {
    private final FaunaClient faunaClient;

    // standard constructors

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Value user = faunaClient.query(Map(
              Paginate(Match(Index("users_by_username"), Value(username))),
              Lambda(Value("user"), Get(Var("user")))))
              .get();

            Value userData = user.at("data").at(0).orNull();
            if (userData == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return User.withDefaultPasswordEncoder()
              .username(userData.at("data", "username").to(String.class).orNull())
              .password(userData.at("data", "password").to(String.class).orNull())
              .roles("USER")
              .build();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

接下来，我们需要一些 Spring 配置来设置它。这是连接上述UserDetailsService的标准 Spring Security 配置：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    @Autowired
    private FaunaClient faunaClient;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable();
        http.authorizeRequests()
            .antMatchers("/")
            .permitAll()
            .and()
            .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new FaunaUserDetailsService(faunaClient);
    }
}
```

此时，我们可以在我们的代码中添加标准的@PreAuthorize注解，并根据身份验证详细信息是否存在于 Fauna 的“用户”集合中来接受或拒绝请求。

## 4.增加对列表帖子的支持

如果不支持 Posts 的概念，我们的博客服务就不会出色。这些是实际撰写的博客文章，可供其他人阅读。

### 4.1. 创建一个帖子集合

和以前一样，我们首先需要一个集合来存储帖子。这个创建是一样的，只是称为“帖子”而不是“用户”。我们将有四个字段：

-   title – 帖子的标题。
-   内容——帖子的内容。
-   created – 发布帖子的时间戳。
-   authorRef – 对帖子作者的“用户”记录的引用。

我们还需要两个索引。第一个是“posts_by_author”，它将让我们搜索具有特定作者的“帖子”记录：

[![img](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-19-at-07.53.11-1024x985.png)](https://www.baeldung.com/wp-content/uploads/2022/03/Screenshot-2022-01-19-at-07.53.11.png)

第二个索引将是“posts_sort_by_created_desc”。这将允许我们按创建日期对结果进行排序，以便首先返回最近创建的帖子。我们需要以不同的方式创建它，因为它依赖于 Web UI 中不可用的功能——表明索引以相反的顺序存储值。

为此，我们需要在 Fauna Shell 中执行一段 FQL：

```php
CreateIndex({
  name: "posts_sort_by_created_desc",
  source: Collection("posts"),
  terms: [ { field: ["ref"] } ],
  values: [
    { field: ["data", "created"], reverse: true },
    { field: ["ref"] }
  ]
})
```

Web UI 所做的一切都可以同样以这种方式完成，从而可以更好地精确控制所做的事情。

然后我们可以在 Fauna Shell 中创建一个帖子来获得一些起始数据：

```css
Create(
  Collection("posts"),
  {
    data: {
      title: "My First Post",
      contents: "This is my first post",
      created: Now(),
      authorRef: Select("ref", Get(Match(Index("users_by_username"), "baeldung")))
    }
  }
)
```

在这里，我们需要确保“authorRef”的值是我们之前创建的“users”记录中的正确值。我们通过查询“users_by_username”索引来实现这一点，通过查找我们的用户名来获取 ref。

### 4.2. 邮政服务

现在我们已经支持 Fauna 中的帖子，我们可以在我们的应用程序中构建一个服务层来使用它。

首先，我们需要一些Java记录来表示我们正在获取的数据。这将包括一个作者和一个帖子记录类：

```java
public record Author(String username, String name) {}

public record Post(String id, String title, String content, Author author, Instant created, Long version) {}
```

现在，我们可以启动我们的 Posts Service。这将是一个 Spring 组件，它包装了FaunaClient并使用它来访问数据存储：

```java
@Component
public class PostsService {
    @Autowired
    private FaunaClient faunaClient;
}

```

### 4.3. 获取所有帖子

在我们的PostsService中，我们现在可以实现一种方法来获取所有帖子。在这一点上，我们不会担心正确的分页，而是只使用默认值——这意味着结果集中的前 64 个文档。

为此，我们将向PostsService类添加以下方法：

```java
List<Post> getAllPosts() throws Exception {
    var postsResult = faunaClient.query(Map(
      Paginate(
        Join(
          Documents(Collection("posts")),
          Index("posts_sort_by_created_desc")
        )
      ),
      Lambda(
        Arr(Value("extra"), Value("ref")),
        Obj(
          "post", Get(Var("ref")),
          "author", Get(Select(Arr(Value("data"), Value("authorRef")), Get(Var("ref"))))
        )
      )
    )).get();

    var posts = postsResult.at("data").asCollectionOf(Value.class).get();
    return posts.stream().map(this::parsePost).collect(Collectors.toList());
}
```

这将执行一个查询以从“posts”集合中检索每个文档，并根据“posts_sort_by_created_desc”索引进行排序。然后它应用 Lambda 来构建响应，每个条目由两个文档组成——帖子本身和帖子的作者。

现在，我们需要能够将此响应转换回我们的Post对象：

```java
private Post parsePost(Value entry) {
    var author = entry.at("author");
    var post = entry.at("post");

    return new Post(
      post.at("ref").to(Value.RefV.class).get().getId(),
      post.at("data", "title").to(String.class).get(),
      post.at("data", "contents").to(String.class).get(),
      new Author(
        author.at("data", "username").to(String.class).get(),
        author.at("data", "name").to(String.class).get()
      ),
      post.at("data", "created").to(Instant.class).get(),
      post.at("ts").to(Long.class).get()
    );
}
```

这从我们的查询中获取一个结果，提取它的所有值，并构建我们更丰富的对象。

请注意，“ts”字段是记录最后更新时间的时间戳，但它不是 Fauna Timestamp类型。相反，它是一个Long表示自 UNIX 纪元以来的微秒数。在这种情况下，我们将其视为不透明的版本标识符，而不是将其解析为时间戳。

### 4.4. 获取单个作者的帖子

我们还想检索由特定作者撰写的所有帖子，而不仅仅是曾经写过的每篇帖子。这是使用我们的“posts_by_author”索引而不是仅仅匹配每个文档的问题。

我们还将链接到“users_by_username”索引以按用户名而不是用户记录的 ref 进行查询。

为此，我们将向PostsService类添加一个新方法：

```java
List<Post> getAuthorPosts(String author) throws Exception {
    var postsResult = faunaClient.query(Map(
      Paginate(
        Join(
          Match(Index("posts_by_author"), Select(Value("ref"), Get(Match(Index("users_by_username"), Value(author))))),
          Index("posts_sort_by_created_desc")
        )
      ),
      Lambda(
        Arr(Value("extra"), Value("ref")),
        Obj(
          "post", Get(Var("ref")),
          "author", Get(Select(Arr(Value("data"), Value("authorRef")), Get(Var("ref"))))
        )
      )
    )).get();

    var posts = postsResult.at("data").asCollectionOf(Value.class).get();
    return posts.stream().map(this::parsePost).collect(Collectors.toList());
}
```

### 4.5. 职位管理员

我们现在可以编写我们的帖子控制器，它将允许对我们服务的 HTTP 请求来检索帖子。这将侦听“/posts”URL 并将返回所有帖子或单个作者的帖子，具体取决于是否提供了“author”参数：

```java
@RestController
@RequestMapping("/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;

    @GetMapping
    public List<Post> listPosts(@RequestParam(value = "author", required = false) String author) 
        throws Exception {
        return author == null 
          ? postsService.getAllPosts() 
          : postsService.getAuthorPosts(author);
    }
}
```

此时，我们可以启动我们的应用程序并向/posts或/posts?author=baeldung发出请求并获得结果：

```json
[
    {
        "author": {
            "name": "Baeldung",
            "username": "baeldung"
        },
        "content": "Introduction to FaunaDB with Spring",
        "created": "2022-01-25T07:36:24.563534Z",
        "id": "321742264960286786",
        "title": "Introduction to FaunaDB with Spring",
        "version": 1643096184600000
    },
    {
        "author": {
            "name": "Baeldung",
            "username": "baeldung"
        },
        "content": "This is my second post",
        "created": "2022-01-25T07:34:38.303614Z",
        "id": "321742153548038210",
        "title": "My Second Post",
        "version": 1643096078350000
    },
    {
        "author": {
            "name": "Baeldung",
            "username": "baeldung"
        },
        "content": "This is my first post",
        "created": "2022-01-25T07:34:29.873590Z",
        "id": "321742144715882562",
        "title": "My First Post",
        "version": 1643096069920000
    }
]
```

## 5. 创建和更新帖子

到目前为止，我们有一个完全只读的服务，可以让我们获取最新的帖子。但是，为了提供帮助，我们也想创建和更新帖子。

### 5.1. 创建新帖子

首先，我们将支持创建新帖子。为此，我们将向PostsService添加一个新方法：

```java
public void createPost(String author, String title, String contents) throws Exception {
    faunaClient.query(
      Create(Collection("posts"),
        Obj(
          "data", Obj(
            "title", Value(title),
            "contents", Value(contents),
            "created", Now(),
            "authorRef", Select(Value("ref"), Get(Match(Index("users_by_username"), Value(author))))
          )
        )
      )
    ).get();
}
```

如果这看起来很眼熟，那就是我们之前在 Fauna shell 中创建新帖子时的Java等价物。

接下来，我们可以添加一个控制器方法来让客户端创建帖子。为此，我们首先需要一个Java记录来表示传入的请求数据：

```java
public record UpdatedPost(String title, String content) {}
```

现在，我们可以在PostsController中创建一个新的控制器方法来处理请求：

```java
@PostMapping
@ResponseStatus(HttpStatus.NO_CONTENT)
@PreAuthorize("isAuthenticated()")
public void createPost(@RequestBody UpdatedPost post) throws Exception {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    postsService.createPost(name, post.title(), post.content());
}
```

请注意，我们使用@PreAuthorize注解来确保请求经过身份验证，然后我们使用经过身份验证的用户的用户名作为新帖子的作者。

此时，启动服务并向端点发送 POST 将导致在我们的集合中创建一条新记录，然后我们可以使用较早的处理程序检索该记录。

### 5.2. 更新现有帖子

这对我们更新现有帖子而不是创建新帖子也很有帮助。我们将通过接受具有新标题和内容的 PUT 请求并更新帖子以具有这些值来对此进行管理。

和以前一样，我们首先需要的是PostsService上的一个新方法来支持这一点：

```java
public void updatePost(String id, String title, String contents) throws Exception {
    faunaClient.query(
      Update(Ref(Collection("posts"), id),
        Obj(
          "data", Obj(
            "title", Value(title),
            "contents", Value(contents)
          )
        )
      )
    ).get();
}
```

接下来，我们将我们的处理程序添加到PostsController：

```java
@PutMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
@PreAuthorize("isAuthenticated()")
public void updatePost(@PathVariable("id") String id, @RequestBody UpdatedPost post)
    throws Exception {
    postsService.updatePost(id, post.title(), post.content());
}
```

请注意，我们使用相同的请求主体来创建和更新帖子。这很好，因为两者具有相同的形状和含义——相关帖子的新细节。

此时，启动服务并将 PUT 发送到正确的 URL 将导致更新该记录。但是，如果我们使用未知 ID 调用，则会出现错误。我们可以使用异常处理方法来解决这个问题：

```java
@ExceptionHandler(NotFoundException.class)
@ResponseStatus(HttpStatus.NOT_FOUND)
public void postNotFound() {}
```

现在，这将导致请求更新未知帖子以返回 HTTP 404。

## 6. 检索帖子的过去版本

现在我们可以更新帖子了，查看它们的旧版本会很有帮助。

首先，我们将向PostsService添加一个新方法来检索帖子。这会获取帖子的 ID 以及我们想要获取的版本(可选)——换句话说，如果我们提供版本“5”，那么我们想要返回版本“4”：

```java
Post getPost(String id, Long before) throws Exception {
    var query = Get(Ref(Collection("posts"), id));
    if (before != null) {
        query = At(Value(before - 1), query);
    }

    var postResult = faunaClient.query(
      Let(
        "post", query
      ).in(
        Obj(
          "post", Var("post"),
          "author", Get(Select(Arr(Value("data"), Value("authorRef")), Var("post")))
        )
      )
    ).get();

  return parsePost(postResult);
}

```

在这里，我们引入At方法，该方法将使 Fauna 返回给定时间点的数据。我们的版本号只是以微秒为单位的时间戳，因此我们可以通过简单地在给定值之前 1μs 请求数据来获取给定点之前的值。

同样，我们需要一个控制器方法来处理传入的调用。我们将把它添加到我们的PostsController 中：

```java
@GetMapping("/{id}")
public Post getPost(@PathVariable("id") String id, @RequestParam(value = "before", required = false) Long before)
    throws Exception {
    return postsService.getPost(id, before);
}
```

现在，我们可以获得各个帖子的各个版本。调用/posts/321742144715882562将获得该帖子的最新版本，但调用/posts/321742144715882562?before=1643183487660000将获得该版本之前的帖子版本。

## 七. 总结

在这里，我们探索了 Fauna 数据库的一些特性以及如何使用它们构建应用程序。Fauna 还可以做很多我们这里没有涉及的事情，但为什么不尝试在你的下一个项目中探索它们呢？
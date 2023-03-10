## 1. 简介

[GraphQL](http://graphql.org/)是Facebook的一个相对较新的概念，被宣传为Web API的REST的替代品。

在本教程中，我们将学习如何使用Spring Boot设置GraphQL服务器，以便我们可以将其添加到现有应用程序或在新应用程序中使用它。

## 2. 什么是GraphQL？

传统的REST API使用服务器管理的资源概念，我们可以按照各种HTTP动词以一些标准方式操作这些资源，只要我们的API符合资源概念，它就可以很好地工作，但当我们需要偏离它时，它很快就会分崩离析。

当客户端同时需要来自多个资源的数据时，例如请求博客文章和评论，这也会受到影响。通常，这可以通过让客户端发出多个请求或让服务器提供可能并不总是需要的额外数据来解决，从而导致更大的响应大小。

**GraphQL为这两个问题提供了解决方案**，它允许客户端准确指定它想要的数据，包括在单个请求中导航子资源，并允许在单个请求中进行多个查询。

它还以更加RPC的方式工作，使用命名查询和突变而不是标准的强制操作集。**这适用于将控件放在它所属的位置，API开发人员指定可能的内容，而API使用者指定所需的内容**。

例如，博客可能允许以下查询：

```graphql
query {
    recentPosts(count: 10, offset: 0) {
        id
        title
        category
        author {
            id
            name
            thumbnail
        }
    }
}
```

该查询将：

-   请求最近的10个帖子
-   对于每个帖子，请求ID、标题和类别
-   对于每个帖子，请求作者，返回ID、名称和缩略图

在传统的REST API中，这需要11个请求，1个用于帖子，10个用于作者，或者需要在帖子详细信息中包含作者详细信息。

### 2.1 GraphQL模式

GraphQL服务器公开了一个描述API的模式，该模式由类型定义组成。每种类型都有一个或多个字段，每个字段接收零个或多个参数并返回特定类型。

该图派生自这些字段相互嵌套的方式。请注意，图不需要是非循环的，且循环是完全可以接受的，但它是有向的。客户端可以从一个字段获取到它的子字段，但它不能自动返回到父字段，除非模式明确定义了这一点。

博客的GraphQL Schema示例可能包含以下定义，描述帖子、帖子的作者和获取博客上最新帖子的根查询：

```graphql
type Post {
    id: ID!
    title: String!
    text: String!
    category: String
    author: Author!
}

type Author {
    id: ID!
    name: String!
    thumbnail: String
    posts: [Post]!
}

# The Root Query for the application
type Query {
    recentPosts(count: Int, offset: Int): [Post]!
}

# The Root Mutation for the application
type Mutation {
    createPost(title: String!, text: String!, category: String, authorId: String!) : Post!
}
```

某些名称末尾的“!”表示它是不可为空的类型，任何没有这个”!“的类型在服务器的响应中都可以为空。GraphQL服务可以正确处理这些字段，使我们能够安全地请求可空类型的子字段。

GraphQL服务还使用一组标准字段公开模式，允许任何客户端提前查询模式定义。

这允许客户端自动检测模式何时更改，并允许客户端动态适应模式的工作方式。一个非常有用的例子是GraphiQL工具，它允许我们与任何GraphQL API交互。

## 3. 介绍GraphQL Spring Boot Starter

**[Spring Boot GraphQL Starter](https://spring.io/projects/spring-graphql)提供了一种在最少的时间设置GraphQL服务器的绝佳方法**，使用自动配置和基于注解的编程方法，我们只需要编写服务所需的代码。

### 3.1 设置服务

我们需要的只是正确的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

因为GraphQL与传输无关，所以我们在配置中包含了Web Starter，这会在默认的/graphql端点上使用Spring MVC通过HTTP公开GraphQL API。其他的Starter可以用于其他的底层实现，比如Spring Webflux。

如有必要，我们还可以在application.properties文件中自定义此端点。

### 3.2 编写模式

GraphQL Boot Starter的工作方式是处理GraphQL Schema文件以构建正确的结构，然后将特殊的bean连接到该结构，**Spring Boot GraphQL Starter会自动找到这些模式文件**。

我们需要将这些“.graphqls”或“.gqls”模式文件保存在src/main/resources/graphql/**位置下，Spring Boot会自动获取它们。像往常一样，我们可以使用spring.graphql.schema.locations自定义位置，使用spring.graphql.schema.file-extensions配置属性自定义文件扩展名。

一个要求是必须只有一个根查询和最多一个根突变，与模式的其余部分不同，我们不能将其拆分到文件之间。这是GraphQL Schema定义的限制，而不是Java实现的限制。

### 3.3 根查询解析器

**根查询需要具有特别注解的方法来处理这个根查询中的各个字段**。与模式定义不同，根查询字段没有限制只有一个Spring bean。

**我们需要使用@QueryMapping注解来标注处理程序方法，并将它们放置在我们应用程序的标准@Controller组件中**，这会将带注解的类注册为我们的GraphQL应用程序中的数据获取组件：

```java
@Controller
public class PostController {

    private PostDao postDao;

    @QueryMapping
    public List<Post> recentPosts(@Argument int count, @Argument int offset) {
        return postDao.getRecentPosts(count, offset);
    }
}
```

上面定义了方法recentPosts，我们将使用它来处理前面定义的模式中recentPosts字段的任何GraphQL查询。此外，该方法必须具有用@Argument标注的参数，这些参数与模式中的相应参数相对应。

它还可以选择使用其他与GraphQL相关的参数，例如GraphQLContext、DataFetchingEnvironment等，用于访问底层上下文和环境。

该方法还必须为GraphQL模式中的类型返回正确的返回类型，正如我们即将看到的那样，我们可以将任何简单类型、String、Int、List等与等效的Java类型一起使用，系统会自动映射它们。

### 3.4 使用Bean表示类型

**GraphQL服务器中的每个复杂类型都由一个Java bean表示**，无论是从根查询加载还是从结构中的其他任何地方加载，同一个Java类必须始终表示相同的GraphQL类型，但类的名称不是必需的。

**Java bean中的字段将根据字段名称直接映射到GraphQL响应中的字段**：

```java
public class Post {
    private String id;
    private String title;
    private String category;
    private String authorId;
}
```

Java bean上未映射到GraphQL模式的任何字段或方法都将被忽略，但不会导致问题。这对于字段解析器的工作很重要。

例如，这里的authorId字段与我们之前定义的模式中的任何内容都不对应，但它可用于下一步。

### 3.5 复杂值的字段解析器

有时，字段的值对于加载来说并不重要，这可能涉及数据库查找、复杂计算或其他任何内容。**@SchemaMapping注解将处理程序方法映射到模式中具有相同名称的字段**，并将其用作该字段的DataFetcher。

```java
@SchemaMapping
public Author author(Post post) {
    return authorDao.getAuthor(post.getAuthorId());
}
```

重要的是，**如果客户端不请求字段，那么GraphQL服务器将不会执行检索它的工作**。这意味着如果客户端检索到一个Post并且不要求author字段，则不会执行上面的author()方法，也不会进行DAO调用。

或者，我们也可以在注解中指定父类型名称和字段名称：

```java
@SchemaMapping(typeName="Post", field="author")
public Author getAuthor(Post post) {
    return authorDao.getAuthor(post.getAuthorId());
}
```

在这里，注解属性用于将此声明为模式中author字段的处理程序。

### 3.6 可空值

GraphQL Schema的概念是某些类型可以为空，而其他类型则不可为空。

我们通过直接使用null值在Java代码中处理这个问题。相反，我们可以直接对可空类型使用Java 8中的新Optional类型，系统将对这些值执行正确的操作。

这非常有用，因为这意味着我们的Java代码更明显地与方法定义中的GraphQL模式相同。

### 3.7 突变

到目前为止，我们所做的一切都是关于从服务器检索数据，GraphQL还具有通过突变更新存储在服务器上的数据的能力。

从代码的角度来看，查询没有理由不能更改服务器上的数据。我们可以轻松编写接收参数、保存新数据并返回这些更改的查询解析器。这样做会给API客户端带来意想不到的副作用，被认为是不好的做法。

相反，**应该使用突变来通知客户端这将导致正在存储的数据发生变化**。

与查询类似，**突变是通过使用@MutationMapping标注处理程序方法在控制器中定义的**。然后，来自突变字段的返回值与来自查询字段的返回值完全相同，从而允许检索嵌套值：

```java
@MutationMapping
public Post createPost(@Argument String title, @Argument String text, @Argument String category, @Argument String authorId) {
    Post post = new Post();
    post.setId(UUID.randomUUID().toString());
    post.setTitle(title);
    post.setText(text);
    post.setCategory(category);
    post.setAuthorId(authorId);

    postDao.savePost(post);

    return post;
}
```

## 4. GraphQL

GraphQL还有一个名为[GraphiQL](https://github.com/graphql/graphiql)的配套工具，这个UI工具可以与任何GraphQL服务器通信，并有助于针对GraphQL API使用和开发。它的可下载版本作为Electron应用程序存在，可以从[此处](https://github.com/skevy/graphiql-app)检索。

Spring GraphQL附带了一个默认的GraphQL页面，该页面在/graphiql端点处公开。默认情况下端点处于禁用状态，但可以通过启用spring.graphql.graphiql.enabled属性来打开它，这提供了一个非常有用的浏览器内工具来编写和测试查询，特别是在开发和测试期间。

## 5. 总结

GraphQL是一项非常令人兴奋的新技术，它有可能会彻底改变我们开发Web API的方式。

Spring Boot GraphQL Starter使得将这项技术添加到任何新的或现有的Spring Boot应用程序变得异常容易。
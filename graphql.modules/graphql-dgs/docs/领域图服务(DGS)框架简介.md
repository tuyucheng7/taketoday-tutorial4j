## 1. 概述

在过去几年中，关于客户端/服务器通信的最重要的范式变化之一是[GraphQL](https://graphql.org/)，一种开源查询语言和用于操作API的运行时。我们可以使用它来请求我们需要的确切数据，从而限制我们需要的请求数量。

Netflix创建了一个域图服务框架(DGS)服务器框架，使事情变得更加简单。在本快速教程中，我们将介绍DGS框架的主要功能，我们将了解如何将此框架添加到我们的应用程序并检查其基本注解的工作方式。要了解有关GraphQL本身的更多信息，请查看我们的[GraphQL简介](../../graphql-java/docs/GraphQL简介.md)文章。

## 2. 领域图服务框架

[Netflix DGS](https://netflix.github.io/dgs/)(Domain Graph Service)是一个用Kotlin编写并基于Spring Boot的GraphQL服务器框架，除了Spring框架之外，它被设计为具有最小的外部依赖性。

**Netflix DGS框架使用构建在Spring Boot之上的基于注解的GraphQL Java库**，除了基于注解的编程模型外，它还提供了几个有用的特性，**例如允许从GraphQL模式生成源代码，让我们总结一些关键功能**：

-   基于注解的Spring Boot编程模型
-   将查询测试编写为单元测试的测试框架
-   Gradle/Maven代码生成插件，用于从模式创建类型
-   与GraphQL Federation轻松集成
-   与Spring Security集成
-   GraphQL订阅(WebSockets和SSE)
-   文件上传
-   错误处理
-   许多扩展点

## 3. 配置

首先，由于DGS框架是基于Spring Boot的，让我们创建一个[Spring Boot应用程序]()。然后，让我们将[DGS依赖项](https://search.maven.org/search?q=g:com.netflix.graphql.dgs a:graphql-dgs)添加到我们的项目中：

```xml
<dependency>
    <groupId>com.netflix.graphql.dgs</groupId>
    <artifactId>graphql-dgs-spring-boot-starter</artifactId>
    <version>4.9.16</version>
</dependency>
```

## 4. 模式

### 4.1 开发方法

**DGS框架支持两种开发方法-模式优先和代码优先**，但推荐的方法是模式优先，主要是因为它更容易跟上数据模型的变化。模式优先表示我们首先为GraphQL服务定义模式，然后我们通过匹配模式中的定义来实现代码。默认情况下，框架会选择src/main/resources/schema文件夹中的所有schema文件。

### 4.2 实现

让我们使用[模式定义语言(SDL)](https://www.howtographql.com/basics/2-core-concepts/)为我们的示例应用程序创建一个简单的GraphQL模式：

```graphql
type Query {
    albums(titleFilter: String): [Album]
}

type Album {
    title: String
    artist: String
    recordNo: Int
}
```

此模式允许查询专辑列表，并可选择按title进行过滤。

## 5. 基本注解

首先我们创建一个对应于我们的模式的Album类：

```java
public class Album {
    private final String title;
    private final String artist;
    private final Integer recordNo;

    public Album(String title, String artist, Integer recordNo) {
        this.title = title;
        this.recordNo = recordNo;
        this.artist = artist;
    }

    // standard getters
}
```

### 5.1 数据获取器

数据提取器负责为查询返回数据，**@DgsQuery、@DgsMutation和@DgsSubscription注解是在Query、Mutation和Subscription类型上定义数据提取器的简写**。所有提到的注解都等同于@DgsData注解，我们可以在Java方法上使用这些注解之一，使该方法成为数据获取器并定义带有参数的类型。

### 5.2 实现

**因此，要定义DGS数据获取器，我们需要在@DgsComponent类中创建一个查询方法**。我们想在我们的示例中查询专辑列表，所以让我们用@DgsQuery标记该方法：

```java
private final List<Album> albums = Arrays.asList(
  new Album("Rumours", "Fleetwood Mac", 20),
  new Album("What's Going On", "Marvin Gaye", 10), 
  new Album("Pet Sounds", "The Beach Boys", 12)
  );

@DgsQuery
public List<Album> albums(@InputArgument String titleFilter) {
    if (titleFilter == null) {
        return albums;
    }
    return albums.stream()
      .filter(s -> s.getTitle().contains(titleFilter))
      .collect(Collectors.toList());
}
```

我们还使用注解@InputArgument标记了方法的参数，此注解将使用方法参数的名称将其与查询中发送的输入参数的名称相匹配。

## 6. 代码生成插件

DGS还附带一个代码生成插件，用于从GraphQL Schema生成Java或Kotlin代码，代码生成通常与构建集成在一起。

**DGS代码生成插件可用于Gradle和Maven**，该插件在我们项目的构建过程中根据我们的域图服务的GraphQL模式文件生成代码。**该插件可以为类型、输入类型、枚举和接口、示例数据提取器和类型安全查询API生成数据类型**，还有一个包含类型和字段名称的DgsConstants类。

## 7. 测试

查询我们的API的一种便捷方式是[GraphiQL](https://github.com/graphql/graphiql)，**GraphiQL是一个与DGS框架一起开箱即用的查询编辑器**。让我们在默认的Spring Boot端口上启动我们的应用程序并检查URL”http://localhost:8080/graphiql” ，让我们尝试以下查询并测试结果：

```graphql
{
    albums{
        title
    }
}
```

请注意，与REST不同，我们必须明确列出我们希望从查询中返回的字段，下面是返回后的响应：

```json
{
    "data": {
        "albums": [
            {
                "title": "Rumours"
            },
            {
                "title": "What's Going On"
            },
            {
                "title": "Pet Sounds"
            }
        ]
    }
}
```

## 8. 总结

Domain Graph Service Framework是使用GraphQL的一种简单且非常有吸引力的方式，它使用更高级别的构建块来处理查询执行等。DGS框架通过方便的Spring Boot编程模型使所有这些都可用，这个框架有一些我们在本文中介绍的有用的特性。

我们讨论了在我们的应用程序中配置DGS并查看了它的一些基本注解。然后，我们编写了一个简单的应用程序来检查如何从模式创建数据并查询它们。最后，我们使用GraphiQL测试了我们的API。
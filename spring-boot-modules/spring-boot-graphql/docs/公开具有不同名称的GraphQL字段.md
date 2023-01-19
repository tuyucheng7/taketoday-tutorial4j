## 一、概述

[GraphQL](https://www.baeldung.com/graphql)已被广泛用作 Web 服务中的通信模式。GraphQL 的基本前提是客户端应用程序可以灵活使用。

在本教程中，我们将研究灵活性的另一个方面。我们还将探讨如何使用不同的名称公开 GraphQL 字段。

## 2.GraphQL 模式

让我们举一个[博客](https://www.baeldung.com/spring-graphql)的例子，它有不同作者的帖子。GraphQL 模式看起来像这样：

```json
query {
    recentPosts(count: 1, offset: 0){
        id
        title
        text
        category
        author {
            id
            name
            thumbnail
        }
    }
}

type Post {
    id: ID!
    title: String!
    text: String!
    category: String
    authorId: Author!
}

type Author {
    id: ID!
    name: String!
    thumbnail: String
    posts: [Post]!
}
```

在这里我们可以获取最近的帖子。每个帖子都将附有作者。查询结果如下：

```json
{
    "data": {
        "recentPosts": [
            {
                "id": "Post00",
                "title": "Post 0:0",
                "text": "Post 0 + by author 0",
                "category": null,
                "author": {
                    "id": "Author0",
                    "name": "Author 0",
                    "thumbnail": "http://example.com/authors/0"
                }
            }
        ]
    }
}
```

## 3. 以不同的名称公开 GraphQL 字段

客户端应用程序可能需要使用字段first_author。现在，它正在使用author。为了适应这个需求，我们有两种解决方案：

-   更改 GraphQL 服务器中模式的定义
-   在 GraphQL中使用[别名的概念](https://graphql.org/learn/queries/#aliases)

让我们一一看看。

### 3.1. 改变架构

让我们更新帖子的架构定义：

```yaml
type Post {
    id: ID!
    title: String!
    text: String!
    category: String
    first_author: Author!
}

```

作者不是一个微不足道的领域。这是一个复杂的问题。我们还必须更新处理程序方法以适应此更改。

在 PostController中用@SchemaMapping 标记 的 author (Post post)方法需要更新为getFirst_author (Post post)。或者，必须在 @SchemaMapping 中添加字段属性以反映新的字段名称。

这是查询：

```applescript
query{
    recentPosts(count: 1,offset: 0){
        id
        title
        text
        category
        first_author{
            id
            name
            thumbnail
        }
    }
}

```

上述查询结果如下：

```json
{
    "data": {
        "recentPosts": [
            {
                "id": "Post00",
                "title": "Post 0:0",
                "text": "Post 0 + by author 0",
                "category": null,
                "first_author": {
                    "id": "Author0",
                    "name": "Author 0",
                    "thumbnail": "http://example.com/authors/0"
                }
            }
        ]
    }
}

```

该解决方案有两个主要问题：

-   它正在引入对架构和服务器端实现的更改
-   它强制其他客户端应用程序遵循这个更新的模式定义

这些问题与 GraphQL 提供的灵活性特性相矛盾。

### 3.2. GraphQL 别名

在 GraphQL 中，别名让我们可以将字段的结果重命名为我们想要的任何名称，而无需更改模式定义。要在查询中引入别名，别名和冒号 (:) 必须位于 GraphQL 字段之前。

这是查询的演示：

```applescript
query {
    recentPosts(count: 1,offset: 0) {
        id
        title
        text
        category
        first_author:author {
            id
            name
            thumbnail
        }
    }
}

```

上述查询结果如下：

```json
{
    "data": {
        "recentPosts": [
            {
                "id": "Post00",
                "title": "Post 0:0",
                "text": "Post 0 + by author 0",
                "category": null,
                "first_author": {
                    "id": "Author0",
                    "name": "Author 0",
                    "thumbnail": "http://example.com/authors/0"
                }
            }
        ]
    }
}

```

让我们注意到查询本身正在请求第一篇文章。另一个客户端应用程序可能会请求使用first_post而不是recentPosts。再一次，别名会来拯救。

```applescript
query {
    first_post: recentPosts(count: 1,offset: 0) {
        id
        title
        text
        category
        author {
            id
            name
            thumbnail
        }
    }
}

```

上述查询结果如下：

```json
{
    "data": {
        "first_post": [
            {
                "id": "Post00",
                "title": "Post 0:0",
                "text": "Post 0 + by author 0",
                "category": null,
                "author": {
                    "id": "Author0",
                    "name": "Author 0",
                    "thumbnail": "http://example.com/authors/0"
                }
            }
        ]
    }
}

```

这两个示例清楚地展示了使用 GraphQL 是多么灵活。每个客户端应用程序都可以根据需要进行自我更新。同时，服务器端模式定义和实现保持不变。

## 4。总结

在本文中，我们研究了两种使用不同名称公开 graphQL 字段的方法。我们已经通过示例介绍了别名的概念，并解释了它为何是正确的方法。
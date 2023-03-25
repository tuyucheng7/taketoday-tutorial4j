## 1. 概述

在这个简短的教程中，我们将展示如何使用Postman测试GraphQL端点。

## 2. 模式概述和方法

我们将使用在我们的[GraphQL](https://www.baeldung.com/spring-graphql)教程中创建的端点。提醒一下，该模式包含描述帖子和作者的定义：

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
```

另外，我们有查询帖子和编写新帖子的方法：

```graphql
type Query {
    recentPosts(count: Int, offset: Int): [Post]!
}
 
type Mutation {
    createPost(title: String!, text: String!, category: String) : Post!
}
```

使用突变保存数据时，**必填字段用感叹号标记**。另请注意，在我们的Mutation中，返回的类型是Post，但在Query中，我们将获得一个Post对象列表。

上面的模式可以在Postman API部分加载-只需添加带有GraphQL类型的**New API**，然后按**Generate Collection**：

<img src="../assets/img.png">

加载模式后，我们可以**使用Postman对GraphQL的自动完成支持轻松编写示例查询**。

## 3. Postman中的GraphQL请求

首先，Postman允许我们**以GraphQL格式发送正文**-我们只需选择下面的GraphQL选项：

<img src="../assets/img_1.png">

然后，我们可以编写一个原生的GraphQL查询，比如将title、category和作者name获取到QUERY部分的查询：

```graphql
query {
    recentPosts(count: 1, offset: 0) {
        title
        category
        author {
            name
        }
    }
}
```

结果，我们将得到：

```json
{
    "data": {
        "recentPosts": [
            {
                "title": "Post",
                "category": "test",
                "author": {
                    "name": "Author 0"
                }
            }
        ]
    }
}
```

也可以**使用原始格式发送请求**，但我们必须将Content-Type: application/graphql添加到标头部分。而且，在这种情况下，请求体看起来是一样的。

例如，我们可以更新title、text、category，获取id和title作为响应：

```graphql
mutation {
    createPost (
        title: "Post", 
        text: "test", 
        category: "test",
    ) {
        id
        title
    }
}
```

只要我们使用速记语法，就可以在查询主体中省略操作类型(如query和mutation)。在这种情况下，我们不能使用操作的名称和变量，但建议使用操作名称以便于记录和调试。

## 4. 使用变量

在变量部分，我们可以创建一个JSON格式的模式，为变量赋值。这避免了在查询字符串中键入参数：

<img src="../assets/img_2.png">

因此，我们可以修改QUERY部分中的recentPosts正文，以从变量动态分配值：

```graphql
query recentPosts ($count: Int, $offset: Int) {
    recentPosts (count: $count, offset: $offset) {
        id
        title
        text
        category
    }
}
```

我们可以使用我们希望将变量设置为的内容来编辑GRAPHQL VARIABLES部分：

```java
{
  "count": 1,
  "offset": 0
}
```

## 5. 总结

我们可以使用Postman轻松测试GraphQL，它还允许我们导入模式并生成查询。
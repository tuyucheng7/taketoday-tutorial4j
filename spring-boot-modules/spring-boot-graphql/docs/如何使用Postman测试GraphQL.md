## 一、概述

在这个简短的教程中，我们将展示如何使用 Postman 测试 GraphQL 端点。

## 2. 模式概述和方法

我们将使用在我们的[GraphQL](https://www.baeldung.com/spring-graphql)教程中创建的端点。提醒一下，该模式包含描述帖子和作者的定义：

```javascript
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

另外，我们有显示帖子和写新帖子的方法：

```javascript
type Query {
    recentPosts(count: Int, offset: Int): [Post]!
}
 
type Mutation {
    createPost(title: String!, text: String!, category: String) : Post!
}
```

使用突变保存数据时，必填字段用感叹号标记。另请注意，在我们的Mutation中，返回的类型是Post，但在 Query 中，我们将获得一个Post对象列表。

上面的模式可以在 Postman API 部分加载——只需添加带有 GraphQL 类型 的新 API ，然后按生成集合：

[![img](https://www.baeldung.com/wp-content/uploads/2020/04/graphql_schema_generator-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/04/graphql_schema_generator-1.jpg)

加载模式后，我们可以使用 Postman 对 GraphQL 的自动完成支持轻松编写示例查询。

## 3. Postman 中的 GraphQL 请求

首先，Postman 允许我们以GraphQL 格式发送正文——我们只需选择下面的 GraphQL 选项：

[![img](https://www.baeldung.com/wp-content/uploads/2020/04/GraphQL-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/04/GraphQL-1.jpg)

然后，我们可以编写一个原生的 GraphQL 查询，比如将title、category和作者姓名获取到 QUERY 部分的查询：

```javascript
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

```javascript
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

也可以使用原始格式发送请求，但我们必须将Content-Type: application/graphql 添加到标头部分。而且，在这种情况下，身体看起来是一样的。

例如，我们可以更新标题、文本、类别，获取id和标题作为响应：

```javascript
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

只要我们使用速记语法，就可以在查询主体中省略操作类型(如查询和 变异)。在这种情况下，我们不能使用操作的名称和变量，但建议使用操作名称以便于记录和调试。

## 4. 使用变量

在变量部分，我们可以创建一个 JSON 格式的模式，为变量赋值。这避免了在查询字符串中键入参数：

[![img](https://www.baeldung.com/wp-content/uploads/2020/04/graphql-variables-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/04/graphql-variables-1.jpg)

因此，我们可以修改 QUERY 部分中的recentPosts 主体，以从变量动态分配值：

```javascript
query recentPosts ($count: Int, $offset: Int) {
    recentPosts (count: $count, offset: $offset) {
        id
        title
        text
        category
    }
}
```

我们可以使用我们希望将变量设置为的内容来编辑 GRAPHQL VARIABLES 部分：

```java
{
  "count": 1,
  "offset": 0
}
```

## 5.总结

我们可以使用 Postman 轻松测试 GraphQL，它还允许我们导入模式并生成查询。
## Spring Boot Graphql

此模块包含有关Spring Boot Graphql的文章

## 相关文章

+ [GraphQL和Spring Boot的快速使用](http://tu-yucheng.github.io/springboot/2023/05/12/spring-graphql.html)
+ [公开具有不同名称的GraphQL字段](http://tu-yucheng.github.io/springboot/2023/05/12/graphql-field-name.html)
+ [使用Spring Boot在GraphQL中进行错误处理](http://tu-yucheng.github.io/springboot/2023/05/12/spring-graphql-error-handling.html)
+ [如何使用Postman测试GraphQL](http://tu-yucheng.github.io/springboot/2023/05/12/graphql-postman.html)
+ [GraphQL与REST](http://tu-yucheng.github.io/springboot/2023/05/12/graphql-vs-rest.html)
+ [REST-GraphQL-gRPC：选择哪个API](http://tu-yucheng.github.io/springboot/2023/05/12/rest-vs-graphql-vs-grpc.html)

## GraphQL示例查询

Query

```shell script
curl \
--request POST 'localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{"query":"query {\n    recentPosts(count: 2, offset: 0) {\n        id\n        title\n        author {\n            id\n            posts {\n                id\n            }\n        }\n    }\n}"}'
```

Mutation

```shell script
curl \
--request POST 'localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{"query":"mutation {\n    createPost(title: \"New Title\", authorId: \"Author2\", text: \"New Text\") {\n id\n       category\n        author {\n            id\n            name\n        }\n    }\n}"}'
```
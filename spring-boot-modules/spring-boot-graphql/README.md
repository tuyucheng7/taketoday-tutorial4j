## Spring Boot Graphql

此模块包含有关Spring Boot Graphql的文章

## 相关文章

+ [GraphQL和Spring Boot的快速使用](docs/GraphQL和SpringBoot快速使用.md)
+ []()
+ []()
+ []()
+ []()
+ []()

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
## 相关文章

+ [Spring Data MongoDB简介](docs/SpringData-MongoDB简介.md)
+ [Spring Data MongoDB查询指南](docs/SpringData-MongoDB查询指南.md)
+ [Spring Data MongoDB索引、注解和转换器](docs/SpringData-MongoDB索引-注解和转换器.md)
+ [Spring Data MongoDB中的自定义级联](docs/SpringData-MongoDB中的自定义级联.md)
+ [Spring Data MongoDB：Projection和Aggregations](docs/SpringData-MongoDB投影和聚合.md)
+ [Spring Data注解](docs/SpringData注解.md)
+ [Spring Data MongoDB事务](docs/SpringData-MongoDB事务.md)

- 更多文章： [[next -->]](../spring-data-mongodb-2/README.md)

## Spring Data MongoDB实时测试

有3个脚本可以简化实时测试的运行：

1. [`live-test-setup.sh`](src/live-test/resources/live-test-setup.sh)使用必要的设置构建docker镜像，并运行它。环境已准备就绪，当日志停止时-大约需要30秒。
2. [`live-test.sh`](src/live-test/resources/live-test.sh)运行实时测试(但不运行其他测试)。
3. [`live-test-teardown.sh`](src/live-test/resources/live-test-teardown.sh)停止并删除docker镜像。
## 相关文章

+ [Spring Data MongoDB简介](http://tu-yucheng.github.io/springdata/2023/05/18/spring-data-mongodb-tutorial.html)
+ [Spring Data MongoDB查询指南](http://tu-yucheng.github.io/springdata/2023/05/18/queries-in-spring-data-mongodb.html)
+ [Spring Data MongoDB索引、注解和转换器](http://tu-yucheng.github.io/springdata/2023/05/18/spring-data-mongodb-index-annotations-converter.html)
+ [Spring Data MongoDB中的自定义级联](http://tu-yucheng.github.io/springdata/2023/05/18/cascading-with-dbref-and-lifecycle-events-in-spring-data-mongodb.html)
+ [Spring Data MongoDB：Projection和Aggregations](http://tu-yucheng.github.io/springdata/2023/05/18/spring-data-mongodb-projections-aggregations.html)
+ [Spring Data注解](http://tu-yucheng.github.io/springdata/2023/05/18/spring-data-annotations.html)
+ [Spring Data MongoDB事务](http://tu-yucheng.github.io/springdata/2023/05/18/spring-data-mongodb-transactions.html)

- 更多文章： [[next -->]](../spring-data-mongodb-2/README.md)

## Spring Data MongoDB实时测试

有3个脚本可以简化实时测试的运行：

1. [`live-test-setup.sh`](src/live-test/resources/live-test-setup.sh)使用必要的设置构建docker镜像，并运行它。环境已准备就绪，当日志停止时-大约需要30秒。
2. [`live-test.sh`](src/live-test/resources/live-test.sh)运行实时测试(但不运行其他测试)。
3. [`live-test-teardown.sh`](src/live-test/resources/live-test-teardown.sh)停止并删除docker镜像。
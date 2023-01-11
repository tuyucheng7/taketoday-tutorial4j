## 相关文章

+ [Spring Data Couchbase简介](docs/SpringData-Couchbase简介.md)
+ [Spring Data Couchbase中的实体验证、乐观锁定和查询一致性](docs/SpringData-Couchbase中的实体验证-乐观锁定和查询一致性.md)
+ [Spring Data Couchbase中的多桶和空间视图查询](docs/SpringData-Couchbase中的多桶和空间视图查询.md)

## 概述

这个Maven项目包含Spring Data Couchbase的Java代码实体、Repository和基于模板的服务如教程以及单元/集成测试中所述对于每个服务实现。

## 构建项目

你还可以在任何IDE之外使用Maven构建项目：

```shell
mvn clean install
```

## 包组织

上面列出的前两个教程的Java类位于包层次结构src/main/java中的cn.tuyucheng.taketoday.spring.data.couchbase中

多存储桶教程的Java类位于包层次结构src/main/java中的cn.tuyucheng.taketoday.spring.data.couchbase2b中

## 运行测试

单存储桶教程的测试类位于src/test/java中的cn.tuyucheng.taketoday.spring.data.couchbase.service包中：

- PersonServiceTest(abstract)
- PersonRepositoryTest(concrete)
- PersonTemplateServiceTest(concrete)
- StudentServiceTest(abstract)
- StudentRepositoryTest(concrete)
- StudentTemplateServiceTest(concrete)

多存储桶教程的具体测试类在src/test/java中的cn.tuyucheng.taketoday.spring.data.couchbase2b.service包中：

- CampusRepositoryServiceImplTest
- PersonRepositoryServiceImplTest
- StudentRepositoryServiceImplTest

具体的测试类可以作为IDE中的JUnit测试运行或使用Maven命令行：

```shell
mvn test
```
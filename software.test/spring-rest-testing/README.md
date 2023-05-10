## Spring REST Testing

本模块包含有关使用Spring测试 REST API的文章。

## 相关文章

+ [使用Maven Cargo插件进行集成测试](http://tu-yucheng.github.io/spring-test/2023/05/10/integration-testing-with-the-maven-cargo-plugin.html)
+ [使用Spring MockMvc测试异常](http://tu-yucheng.github.io/spring-test/2023/05/10/spring-mvc-test-exceptions.html)

## 构建项目

```shell
mvn clean install
```

## 设置MySQL

```shell
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```

## 访问REST Service

```shell
curl http://localhost:8082/spring-rest-full/auth/foos
```
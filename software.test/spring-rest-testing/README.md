## Spring REST Testing

本模块包含有关使用Spring测试 REST API的文章。

## 相关文章

+ [使用Maven Cargo插件进行集成测试](docs/使用Maven-Cargo插件进行集成测试.md)
+ [使用Spring MockMvc测试异常](docs/使用Spring-MockMvc测试异常.md)

## 构建项目

```
mvn clean install
```

## 设置MySQL

```
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```

## 访问REST Service

```
curl http://localhost:8082/spring-rest-full/auth/foos
```
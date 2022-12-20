## Spring Boot Admin

此模块包含有关Spring Boot Admin的文章

## 1. Spring Boot Admin Server

* mvn clean install
* mvn spring-boot:run
* 在8080端口启动
* 使用admin/admin登录
* 为了激活邮件通知，取消注释mail starter依赖项，以及来自应用程序属性的邮件配置
* 如果你希望应用程序发送电子邮件，请添加一些真实凭据
* 激活Hipchat通知与电子邮件相同

## 2. Spring Boot App Client

* mvn clean install
* mvn spring-boot:run
* 在8081端口启动
* 使用client/client进行基础认证

## 相关文章

+ [Spring Boot Admin指南](docs/SpringBoot-Admin指南.md)
+ [在运行时更改Spring Boot应用程序的日志记录级别](docs/在运行时更改SpringBoot应用程序的日志记录级别.md)
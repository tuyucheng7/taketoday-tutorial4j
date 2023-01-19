## Spring Boot Camel

本模块包含有关Apache Camel与Spring Boot的文章

## 关于使用Spring Boot的Camel API的文章示例

为了启动，请运行：

`mvn spring-boot:run`

然后，发出POST Http请求：

`http://localhost:8080/camel/api/bean`

包括标头: Content-Type: application/json,

和JSON响应，例如：

`{"id": 1,"name": "World"}`

We will get a return code of 201 and the response: `Hello, World` - if the transform() method from Application class is
uncommented and the process() method is commented

or return code of 201 and the response: `{"id": 10,"name": "Hello, World"}` - if the transform() method from Application
class is commented and the process() method is uncommented

## 相关文章

- [Apache Camel with Spring Boot](https://www.baeldung.com/apache-camel-spring-boot)
- [Apache Camel Routes Testing in Spring Boot](https://www.baeldung.com/spring-boot-apache-camel-routes-testing)
- [Apache Camel Conditional Routing](https://www.baeldung.com/spring-apache-camel-conditional-routing)
- [Apache Camel Exception Handling](https://www.baeldung.com/java-apache-camel-exception-handling)
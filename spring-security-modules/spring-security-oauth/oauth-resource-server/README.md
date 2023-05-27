## Spring Security OAuth Resource Servers

1. `authorization-server`是一个包装为Spring Boot应用程序的Keycloak授权服务器
2. 授权服务器中注册了两个OAuth客户端：
    1. Client Id: fooClient, barClient
    2. Client secret: fooClientSecret, barClientSecret
    3. Redirect Uri: http://localhost:8080/
3. 授权服务器中注册了两个用户：
    1. john@test.com/123
    2. mike@other.com/pass
4. `resource-server-jwt`是一个Spring Boot资源服务器，它与上面的authorization-server交换JWT OAuth令牌并输出/foos/**
5. `resource-server-opaque`是一个Spring Boot资源服务器，它与上面的authorization-server交换不透明的OAuth令牌并输出/bars/**
6. 该模块使用基于Java 13的新OAuth堆栈

## 相关文章

+ [使用Spring Security 5的OAuth 2.0资源服务器](https://tu-yucheng.github.io/springsecurity/2023/05/27/spring-security-oauth-resource-server.html)
+ [向Feign客户端提供OAuth2令牌](https://tu-yucheng.github.io/springcloud/2023/05/13/spring-cloud-feign-oauth-token.html)
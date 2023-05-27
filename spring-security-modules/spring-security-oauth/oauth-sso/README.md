## Spring Security OAuth - 动态客户端注册

1. `sso-authorization-server`是一个包装为Spring Boot应用程序的Keycloak授权服务器。
2. 这是在授权服务器中注册的两个OIDC-Connect客户端：

- 第一个
    1. Client Id: ssoClient-1
    2. Client secret: ssoClientSecret-1
    3. Redirect Uri: http://localhost:8082/ui-one/login/oauth2/code/custom
- 第二个
    1. Client Id: ssoClient-2
    2. Client secret: ssoClientSecret-2
    3. Redirect Uri: http://localhost:8084/ui-two/login/oauth2/code/custom

3. `sso-resource-server`是一个基于Spring Boot的RESTFul API，充当后端应用程序。
4. `sso-client-app-1`和`sso-client-app-2`是两个相同的Spring MVC Thymeleaf App，充当我们的前端。它们分别在[http://localhost:8082/ui-one/](http://localhost:8082/ui-one)和[http://localhost:8084/ui-two/](http://localhost:8084/ui-two/)可访问。
5. 授权服务器中注册了两个用户：
    1. john@test.com/123
    2. mike@other.com/pass
6. 该模块使用基于Java 13的新OAuth堆栈。

## 相关文章

+ [使用Spring Security OAuth2进行简单的单点登录](https://tu-yucheng.github.io/springsecurity/2023/05/27/sso-spring-security-oauth2.html)
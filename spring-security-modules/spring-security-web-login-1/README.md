## Spring Security Login

本模块包含有关使用Spring Security登录机制的文章。

## 相关文章

+ [Spring Security表单登录](docs/SpringSecurity表单登录.md)
+ [Spring Security注销](docs/SpringSecurity注销.md)
+ [Spring HTTP/HTTPS通道安全](docs/Spring-HTTP-HTTPS通道安全.md)
+ [Spring Security：自定义403 Forbidden/Access Denied页面](docs/SpringSecurity-自定义403Forbidden-AccessDenied页面.md)
+ [Spring Security：登录后重定向到上一个URL](docs/SpringSecurity-登录后重定向到上一个URL.md)
+ [Spring Security自定义AuthenticationFailureHandler](docs/SpringSecurity自定义AuthenticationFailureHandler.md)
+ [Spring Security的额外登录字段](docs/SpringSecurity的额外登录字段.md)

- 更多文章： [[next -->]](../spring-security-web-login-2/README.md)

## 构建项目

```bash 
mvn clean install
```

## 运行项目

- 使用Maven Cargo插件运行应用程序。

```bash
mvn cargo:run
```

- 转到登录页面 http://localhost:8082/spring-security-web-login/login.html
- 使用`user1/user1pass`详细信息登录。
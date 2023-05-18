## Spring Security Login

本模块包含有关使用Spring Security登录机制的文章。

## 相关文章

+ [Spring Security表单登录](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-login.html)
+ [Spring Security注销](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-logout.html)
+ [Spring HTTP/HTTPS通道安全](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-channel-security-https.html)
+ [Spring Security：自定义403 Forbidden/Access Denied页面](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-custom-access-denied-page.html)
+ [Spring Security：登录后重定向到上一个URL](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-redirect-login.html)
+ [Spring Security自定义AuthenticationFailureHandler](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-custom-authentication-failure-handler.html)
+ [Spring Security的额外登录字段](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-extra-login-fields.html)

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
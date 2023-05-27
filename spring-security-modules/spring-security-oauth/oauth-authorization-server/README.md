## Spring Security OAuth Authorization Server

该模块演示了使用Spring授权服务器、Spring OAuth资源服务器和Spring OAuth客户端。

- 从`spring-authorization-server`模块运行授权服务器
    - 重要提示：修改`/etc/hosts`文件并添加条目`127.0.0.1 auth-server`
- 从`resource-server`模块运行资源服务器
- 从`client-server`模块运行客户端
- 访问`http://127.0.0.1:8080/articles`
    - 输入凭据`admin/password`
- 该模块使用基于Java 11的新OAuth堆栈

## 相关文章

+ [Spring Security OAuth授权服务器](https://tu-yucheng.github.io/springsecurity/2023/05/27/spring-security-oauth-auth-server.html)
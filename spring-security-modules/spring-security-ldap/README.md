## Spring Security LDAP

此模块包含有关Spring Security LDAP的文章

## 相关文章

+ [Spring Security LDAP简介](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-ldap.html)
+ [Spring LDAP概述](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-ldap.html)
+ [Spring Data LDAP指南](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-data-ldap.html)

## 注意

- 该项目使用Spring Boot-只需运行`SampleLDAPApplication.java`即可使用Tomcat容器启动Spring Boot和嵌入式LDAP服务器。
- 启动后，打开“http://localhost:8080”
- 这将显示公开可用的主页
- 导航到“Secure Page”以触发身份验证
- 用户名: 'tuyucheng', 密码: 'password'
- 这将对你进行身份验证，并显示你分配的角色(如“users.ldif”文件中定义)
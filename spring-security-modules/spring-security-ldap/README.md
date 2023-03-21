## Spring Security LDAP

此模块包含有关Spring Security LDAP的文章

## 相关文章

+ [Spring Security LDAP简介](docs/SpringSecurity-LDAP简介.md)
+ [Spring LDAP概述](docs/SpringLDAP概述.md)
+ [Spring Data LDAP指南](docs/SpringData-LDAP指南.md)

## 注意

- 该项目使用Spring Boot-只需运行`SampleLDAPApplication.java`即可使用Tomcat容器启动Spring Boot和嵌入式LDAP服务器。
- 启动后，打开“http://localhost:8080”
- 这将显示公开可用的主页
- 导航到“Secure Page”以触发身份验证
- 用户名: 'tuyucheng', 密码: 'password'
- 这将对你进行身份验证，并显示你分配的角色(如“users.ldif”文件中定义)
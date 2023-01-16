## Spring Security Login And Registration

这是一个Spring Security登录和注册的示例应用程序。

## 相关文章

+ [Spring Security的注册过程](docs/SpringSecurity的注册过程.md)
+ [注册：通过电子邮件激活新帐户](docs/注册–通过电子邮件激活新帐户.md)
+ [Spring Security注册：密码编码](docs/注册SpringSecurity-密码编码.md)
+ [Spring Security：角色和权限](docs/SpringSecurity-角色和权限.md)
+ [使用Spring Security防止暴力认证尝试](docs/使用SpringSecurity防止暴力认证尝试.md)
+ [Spring Security：重置密码](docs/SpringSecurity–重置密码.md)
+ [Spring Security注册：重新发送验证邮件](docs/SpringSecurity注册-重新发送验证邮件.md)
+ [注册API变为RESTful](docs/注册API变为RESTful.md)
+ [注册：密码强度和规则](docs/注册-密码强度和规则.md)
+ [更新你的密码](docs/更新你的密码.md)
+ [使用Spring Security的两因素身份验证](docs/使用SpringSecurity的两因素身份验证.md)
+ [Spring注册：集成reCAPTCHA](docs/Spring注册-集成reCAPTCHA.md)
+ [清除注册生成的过期令牌](docs/清除注册生成的过期令牌.md)
+ [为返回用户自定义登录页面](docs/为返回用户自定义登录页面.md)
+ [仅使用Spring Security允许从接受的位置进行身份验证](docs/仅使用SpringSecurity允许从接受的位置进行身份验证.md)
+ [Spring Security：注册后自动登录用户](docs/SpringSecurity-注册后自动登录用户.md)
+ [使用Spring Security跟踪登录用户](docs/使用SpringSecurity跟踪登录用户.md)
+ [登录Spring Web应用程序：错误处理和国际化](docs/登录SpringWeb应用程序-错误处理和国际化.md)
+ [通知用户从新设备或位置登录](docs/通知用户从新设备或位置登录.md)
+ [使用Spring Security防止用户名枚举攻击](docs/使用SpringSecurity防止用户名枚举攻击.md)

## 构建和部署项目

```bash
mvn clean install
```

这是一个Spring Boot项目，因此你只需使用主类`Application.java`即可部署它

部署后，可以在以下位置访问应用：

https://localhost:8081

## 设置MySQL

默认情况下，项目配置为使用嵌入式H2数据库。如果你想改用MySQL，则需要取消注释[application.properties](src/main/resources/application.properties)相关部分并创建数据库用户，如下所示：

```bash
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```

## 设置电子邮件

你需要通过在应用程序中提供自己的用户名和密码来配置电子邮件。你还需要使用自己的主机，例如可以使用亚马逊或谷歌。

## 身份验证成功自定义登录页的处理程序配置文章

如果要激活文章[为返回用户自定义登录页面](docs/为返回用户自定义登录页面.md)，然后你需要在MySimpleUrlAuthenticationSuccessHandler类中注释@Component("myAuthenticationSuccessHandler")注解，并在MyCustomLoginAuthenticationSuccessHandler中取消注释。

## Geo IP库的功能切换

地理位置检查不适用于IP地址127.0.0.1和0.0.0.0，在本地或测试环境中运行应用程序时，这可能是一个问题。要启用/禁用对地理位置的检查，请将属性“geo.ip.lib.enabled”设置为true/false；这默认为false。
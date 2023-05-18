## Spring Security Login And Registration

这是一个Spring Security登录和注册的示例应用程序。

## 相关文章

+ [Spring Security的注册过程](http://tu-yucheng.github.io/springsecurity/2023/05/17/registration-with-spring-mvc-and-spring-security.html)
+ [注册：通过电子邮件激活新帐户](http://tu-yucheng.github.io/springsecurity/2023/05/17/registration-verify-user-by-email.html)
+ [Spring Security注册：密码编码](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-registration-password-encoding-bcrypt.html)
+ [Spring Security：角色和权限](http://tu-yucheng.github.io/springsecurity/2023/05/17/role-and-privilege-for-spring-security-registration.html)
+ [使用Spring Security防止暴力认证尝试](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-block-brute-force-authentication-attempts.html)
+ [Spring Security：重置密码](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-registration-i-forgot-my-password.html)
+ [Spring Security注册：重新发送验证邮件](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-registration-verification-email.html)
+ [注册API变为RESTful](http://tu-yucheng.github.io/springsecurity/2023/05/17/registration-restful-api.html)
+ [注册：密码强度和规则](http://tu-yucheng.github.io/springsecurity/2023/05/17/registration-password-strength-and-rules.html)
+ [更新你的密码](http://tu-yucheng.github.io/springsecurity/2023/05/17/updating-your-password.html)
+ [使用Spring Security的两因素身份验证](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-two-factor-authentication-with-soft-token.html)
+ [Spring注册：集成reCAPTCHA](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-registration-captcha.html)
+ [清除注册生成的过期令牌](http://tu-yucheng.github.io/springsecurity/2023/05/17/registration-token-cleanup.html)
+ [为返回用户自定义登录页面](http://tu-yucheng.github.io/springsecurity/2023/05/17/custom-login-page-for-returning-user.html)
+ [仅使用Spring Security允许从接受的位置进行身份验证](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-restrict-authentication-by-geography.html)
+ [Spring Security：注册后自动登录用户](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-auto-login-user-after-registration.html)
+ [使用Spring Security跟踪登录用户](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-track-logged-in-users.html)
+ [登录Spring Web应用程序：错误处理和国际化](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-login-error-handling-localization.html)
+ [通知用户从新设备或位置登录](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-login-new-device-location.html)
+ [使用Spring Security防止用户名枚举攻击](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-enumeration-attacks.html)

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
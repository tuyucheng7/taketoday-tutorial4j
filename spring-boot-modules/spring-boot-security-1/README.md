## Spring Boot Security

此模块包含有关Spring Boot Security的文章。

## 相关文章

+ [Spring Boot Security自动配置](http://tu-yucheng.github.io/springboot/2023/05/12/spring-boot-security-autoconfiguration.html)
+ [用于Spring Boot集成测试的Spring Security](http://tu-yucheng.github.io/springboot/2023/05/12/spring-security-integration-tests.html)
+ [Spring Security标签库简介](http://tu-yucheng.github.io/springboot/2023/05/12/spring-security-taglibs.html)
+ [Spring Security中的@CurrentSecurityContext指南](http://tu-yucheng.github.io/springboot/2023/05/12/spring-currentsecuritycontext.html)
+ [在Spring Boot中禁用特定Profile的安全性](http://tu-yucheng.github.io/springboot/2023/05/12/spring-security-disable-profile.html)
+ [Spring中的@EnableWebSecurity与@EnableGlobalMethodSecurity](http://tu-yucheng.github.io/springboot/2023/05/12/spring-enablewebsecurity-vs-enableglobalmethodsecurity.html)
+ [Spring Security配置不同的URL](http://tu-yucheng.github.io/springboot/2023/05/12/spring-security-configuring-urls.html)

## Spring Boot Security自动配置

- mvn clean install
- uncomment actuator dependency simultaneously with the line from basic auth main class
- uncomment security properties for easy testing. If not random will be generated.

## CURL命令

```shell
- curl -X POST -u tuyucheng-admin:tuyucheng -d grant_type=client_credentials -d username=tuyucheng-admin -d password=tuyucheng http://localhost:8080/oauth/token
```
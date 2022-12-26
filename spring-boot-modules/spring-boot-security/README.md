## Spring Boot Security

此模块包含有关Spring Boot Security的文章。

## 相关文章

+ [Spring Boot Security自动配置](docs/SpringBoot-Security自动配置.md)
+ [用于Spring Boot集成测试的Spring Security](docs/用于SpringBoot集成测试的SpringSecurity.md)
+ [Spring Security标签库简介](docs/SpringSecurity标签库简介.md)
+ [Spring Security中的@CurrentSecurityContext指南](docs/SpringSecurity中的@CurrentSecurityContext指南.md)
+ [在Spring Boot中禁用特定Profile的安全性](docs/在SpringBoot中禁用Profile的安全性.md)
+ [Spring中的@EnableWebSecurity与@EnableGlobalMethodSecurity](docs/Spring中的@EnableWebSecurity与@EnableGlobalMethodSecurity.md)
+ [Spring Security配置不同的URL](docs/SpringSecurity配置不同的URL.md)

## Spring Boot Security自动配置

- mvn clean install
- uncomment actuator dependency simultaneously with the line from basic auth main class
- uncomment security properties for easy testing. If not random will be generated.

## CURL commands

```shell
- curl -X POST -u tuyucheng-admin:tuyucheng -d grant_type=client_credentials -d username=tuyucheng-admin -d password=tuyucheng http://localhost:8080/oauth/token
```
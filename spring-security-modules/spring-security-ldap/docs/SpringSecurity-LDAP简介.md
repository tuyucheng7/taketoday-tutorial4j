## 1. 概述

在这个快速教程中，我们将学习如何设置 Spring Security LDAP。

在我们开始之前，先说明一下 LDAP 是什么——它代表轻量级目录访问协议，它是一种开放的、供应商中立的协议，用于通过网络访问目录服务。

## 进一步阅读：

## [Spring LDAP 概述](https://www.baeldung.com/spring-ldap)

了解如何使用 Spring LDAP API 对用户进行身份验证和搜索，以及在目录服务器中创建和修改用户。

[阅读更多](https://www.baeldung.com/spring-ldap)→

## [Spring Data LDAP 指南](https://www.baeldung.com/spring-data-ldap)

了解如何将 Spring Data 与 LDAP 一起使用。

[阅读更多](https://www.baeldung.com/spring-data-ldap)→

## [带有 Spring Security 的 Spring Data](https://www.baeldung.com/spring-data-security)

了解如何将 Spring Data 与 Spring Security 集成。

[阅读更多](https://www.baeldung.com/spring-data-security)→

## 2.Maven依赖

首先，让我们看一下我们需要的maven依赖：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-ldap</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.directory.server</groupId>
    <artifactId>apacheds-server-jndi</artifactId>
    <version>1.5.5</version>
</dependency>
```

注意：我们使用[ApacheDS](https://directory.apache.org/apacheds/)作为我们的 LDAP 服务器，它是一个可扩展和可嵌入的目录服务器。

## 3.Java配置

接下来，让我们讨论一下我们的 Spring SecurityJava配置：

```java
public class SecurityConfig {

    @Bean
    ApacheDSContainer ldapContainer() throws Exception {
        return new ApacheDSContainer("dc=baeldung,dc=com", "classpath:users.ldif");
    }

    @Bean
    LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
        String groupSearchBase = "ou=groups";
        DefaultLdapAuthoritiesPopulator authorities = new DefaultLdapAuthoritiesPopulator
           (contextSource, groupSearchBase);
        authorities.setGroupSearchFilter("(member={0})");
        return authorities;
    }

    @Bean
    AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource, 
        LdapAuthoritiesPopulator authorities) {
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory
           (contextSource);
        factory.setUserSearchBase("ou=people");
        factory.setUserSearchFilter("(uid={0})");
        return factory.createAuthenticationManager();
    }
 }
```

这当然只是配置中与 LDAP 相关的部分——完整的Java配置可以在[这里](https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-ldap/src/main/java/com/baeldung/config/SecurityConfig.java)找到。

## 4. XML 配置

现在，我们来看看对应的 XML 配置：

```xml
<authentication-manager>
    <ldap-authentication-provider
      user-search-base="ou=people"
      user-search-filter="(uid={0})"
      group-search-base="ou=groups"
      group-search-filter="(member={0})">
    </ldap-authentication-provider>
</authentication-manager>
   
<ldap-server root="dc=baeldung,dc=com" ldif="users.ldif"/>
```

同样，这只是配置的一部分——与 LDAP 相关的部分；完整的 XML 配置可以在[这里](https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-web-mvc/src/main/resources/webSecurityConfig.xml)找到。

## 5. LDAP 数据交换格式

LDAP 数据可以使用 LDAP 数据交换格式 (LDIF) 来表示——这是我们的用户数据示例：

```bash
dn: ou=groups,dc=baeldung,dc=com
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=baeldung,dc=com
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=baeldung,ou=people,dc=baeldung,dc=com
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Jim Beam
sn: Beam
uid: baeldung
userPassword: password

dn: cn=admin,ou=groups,dc=baeldung,dc=com
objectclass: top
objectclass: groupOfNames
cn: admin
member: uid=baeldung,ou=people,dc=baeldung,dc=com

dn: cn=user,ou=groups,dc=baeldung,dc=com
objectclass: top
objectclass: groupOfNames
cn: user
member: uid=baeldung,ou=people,dc=baeldung,dc=com
```

## 6. 使用 Spring Boot

在处理Spring Boot项目时，我们还可以使用[Spring Boot Starter Data Ldap](https://search.maven.org/search?q=a:spring-boot-starter-data-ldap) 依赖项，它将自动为我们检测 LdapContextSource 和 LdapTemplate 。 

要启用自动配置，我们需要确保在 pom.xml 中将 spring-boot-starter-data-ldap Starter 或 spring-ldap-core 定义为依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-ldap</artifactId>
</dependency>
```

要连接到 LDAP，我们需要在 application.properties 中提供连接设置：

```shell
spring.ldap.url=ldap://localhost:18889
spring.ldap.base=dc=example,dc=com
spring.ldap.username=uid=admin,ou=system
spring.ldap.password=secret
```

有关 Spring Data LDAP 自动配置的更多详细信息可以在 [官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.nosql.ldap)中找到。Spring Boot 引入了[LdapAutoConfiguration](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/ldap/LdapAutoConfiguration.html) ，它负责检测 LdapTemplate ，然后可以将其注入所需的服务类：

```java
@Autowired
private LdapTemplate ldapTemplate;
```

## 7. 申请

最后，这是我们的简单应用程序：

```java
@Controller
public class MyController {

    @RequestMapping("/secure")
    public String secure(Map<String, Object> model, Principal principal) {
        model.put("title", "SECURE AREA");
        model.put("message", "Only Authorized Users Can See This Page");
        return "home";
    }
}
```

## 8. 总结

在这个使用 LDAP 的 Spring Security 快速指南中，我们学习了如何使用 LDIF 配置基本系统并配置该系统的安全性。

本教程的完整实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-ldap)中找到——这是一个基于 Eclipse 的项目，所以它应该很容易导入和运行。
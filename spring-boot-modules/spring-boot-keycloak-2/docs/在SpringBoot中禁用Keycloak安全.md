## 1. 概述

[Keycloak]()是一个免费的开源身份和访问管理程序，如今经常在我们的软件堆栈中使用。在测试阶段，禁用它以专注于业务测试可能很有用，我们的测试环境中也可能没有Keycloak服务器。

在本教程中，我们将禁用Keycloak starter设置的配置，并查看在我们的项目中启用Spring Security时对其进行修改。

## 2. 在非Spring Security环境中禁用Keycloak

我们将首先了解如何在不使用Spring Security的应用程序中禁用Keycloak。

### 2.1 应用设置

首先我们将[keycloak-spring-boot-starter](https://search.maven.org/search?q=keycloak-spring-boot-starter)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>
```

另外，我们需要添加[keycloak-adapter-bom](https://search.maven.org/search?q=keycloak-adapter-bom)依赖带来的各种嵌入式容器的依赖：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.keycloak.bom</groupId>
            <artifactId>keycloak-adapter-bom</artifactId>
            <version>15.0.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

接下来，向我们的application.properties添加我们的Keycloak服务器的配置：

```properties
keycloak.auth-server-url=http://localhost:8080
keycloak.realm=SpringBootKeycloak
keycloak.resource=login-app
keycloak.public-client=true
keycloak.security-constraints[0].authRoles[0]=user
keycloak.security-constraints[0].securityCollections[0].patterns[0]=/users/*
```

**此配置可确保对/users URL的请求只能由具有user角色的经过身份验证的用户访问**。

最后，让我们添加一个检索User的UserController：

```java
@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping("/{userId}")
    public User getCustomer(@PathVariable Long userId) {
        return new User(userId, "John", "Doe");
    }
}
```

### 2.2 禁用Keycloak

现在我们的应用程序已经到位，让我们编写一个简单的测试来获取用户：

```java
@Test
void givenUnauthenticated_whenGettingUser_shouldReturnUser() {
    ResponseEntity<User> responseEntity = restTemplate.getForEntity("/users/1", User.class);

    assertEquals(HttpStatus.SC_OK, responseEntity.getStatusCodeValue());
    assertNotNull(responseEntity.getBody().getFirstname());
}
```

**该测试将失败，因为我们没有向restTemplate提供任何身份验证**，或者因为Keycloak服务器不可用。

Keycloak适配器实现了Keycloak安全性的[Spring自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.auto-configuration)，自动配置依赖于类路径中类的存在或属性的值。具体来说，@ConditionalOnProperty注解对于这种特殊需求非常方便。

**要禁用Keycloak安全性，我们需要通知适配器它不应该加载相应的配置**，我们可以通过分配属性来做到这一点，如下所示：

```properties
keycloak.enabled=false
```

如果我们再次启动我们的测试，它现在将在不涉及任何身份验证的情况下成功。

## 3. 在Spring Security环境中禁用Keycloak

我们经常将[Keycloak与Spring Security结合使用]()，**在这种情况下，仅仅禁用Keycloak配置是不够的，我们还需要修改Spring Security配置以允许匿名请求到达控制器**。

### 3.1 应用设置

首先我们将[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-security)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

接下来，我们实现WebSecurityConfigurerAdapter来定义Spring Security所需的配置，Keycloak适配器为此提供了一个抽象类和注解：

```java
@KeycloakConfiguration
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.csrf()
              .disable()
              .authorizeRequests()
              .anyRequest()
              .authenticated();
    }
}
```

在这里，我们将Spring Security配置为仅允许来自经过身份验证的用户的请求。

### 3.2 禁用Keycloak

除了像我们之前所做的那样禁用Keycloak之外，**我们现在还需要禁用Spring Security**。

我们可以使用[Profile]()来告诉Spring在我们的测试期间是否激活Keycloak配置：

```java
@KeycloakConfiguration
@Profile("tests")
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    // ...
}
```

**但是，一种更优雅的方法是重用keycloak.enable属性，类似于Keycloak适配器**：

```java
@KeycloakConfiguration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    // ...
}
```

因此，只有当keycloak.enable属性为true时，Spring才会启用Keycloak配置。如果缺少该属性，则默认情况下matchIfMissing会启用它。

由于我们使用的是Spring Security Starter，因此禁用我们的Spring Security配置是不够的。事实上，遵循Spring固执己见的默认配置原则，**Starter将创建一个默认的安全层**。

让我们创建一个配置类来禁用它：

```java
@Configuration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class DisableSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
              .disable()
              .authorizeRequests()
              .anyRequest()
              .permitAll();
        return http.build();
    }
}
```

我们仍在使用我们的keycloak.enable属性，但是这次如果Spring的值设置为false，则启用配置。

## 4. 总结

在本文中，我们介绍了如何在有或没有Spring Security的Spring环境中禁用Keycloak安全性。
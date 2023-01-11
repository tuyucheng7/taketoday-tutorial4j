## 1. 概述

在这篇快速文章中，我们将重点关注在Spring Security中使用多种机制对用户进行身份验证。

我们将通过配置多个AuthenticationProvider来做到这一点。

## 2. AuthenticationProvider

AuthenticationProvider是用于从特定存储库(如数据库、LDAP、自定义第三方源等)获取用户信息的抽象接口。它使用获取的用户信息来验证提供的凭据。

简而言之，当定义了多个AuthenticationProvider时，将按照声明的顺序对AuthenticationProvider进行查询。

为了快速演示，我们将配置两个AuthenticationProvider，一个自定义身份验证提供程序和一个内存中身份验证提供程序。

## 3. Maven依赖

让我们首先将必要的Spring Security依赖项添加到我们的Spring Boot应用程序中：

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

如果不使用Spring Boot：

```
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-web</artifactId>
  <version>5.2.2.RELEASE</version>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-core</artifactId>
  <version>5.2.2.RELEASE</version>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-config</artifactId>
  <version>5.2.2.RELEASE</version>
</dependency>
```

## 4. 自定义AuthenticationProvider

现在让我们通过实现AuthneticationProvider接口来创建自定义身份验证提供程序。

我们将实现authenticate方法，它尝试进行身份验证。接收包含用户提供的用户名和密码凭据Authentication对象。

如果身份验证成功，则authenticate方法会返回一个完全填充的Authentication对象。
如果身份验证失败，它会抛出AuthenticationException类型的异常：

```java

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        if ("externaluser".equals(username) && "pass".equals(password))
            return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
        else
            throw new BadCredentialsException("External system authentication failed");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

当然，对于我们这里的示例而言，这是一个简单的实现。

## 5. 配置多个AuthenticationProvider

现在让我们将CustomAuthenticationProvider和内存中的身份验证提供程序添加到Spring Security配置中。

### 5.1 Java配置

在我们的配置类中，我们使用AuthenticationManagerBuilder创建和添加AuthenticationProvider。

首先是CustomAuthenticationProvider，然后是使用inMemoryAuthentication()的内存中身份验证提供程序。

我们还需要确保验证对URL为“/api/”的访问：

```java

@Configuration
@EnableWebSecurity
public class MultipleAuthProvidersSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
        auth.inMemoryAuthentication()
                .withUser("memuser")
                .password(passwordEncoder().encode("pass"))
                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/api/")
                .authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 5.2 XML配置

或者，如果我们想使用XML配置而不是Java配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <security:authentication-manager>
        <security:authentication-provider>
            <security:user-service>
                <security:user name="memuser" password="pass" authorities="ROLE_USER"/>
            </security:user-service>
        </security:authentication-provider>

        <security:authentication-provider ref="customAuthenticationProvider"/>
    </security:authentication-manager>

    <security:http>
        <security:http-basic/>
        <security:intercept-url pattern="/api/" access="isAuthenticated()"/>
    </security:http>
</beans>
```

## 6. MultipleAuthController

接下来，让我们创建一个由我们的两个AuthenticationProvider保护的简单REST端点。

要访问此端点，必须提供有效的用户名和密码。我们的身份验证提供者将验证凭据并确定是否允许访问：

```java

@RestController
public class MultipleAuthController {

    @GetMapping("/api/ping")
    public String getPing() {
        return "OK";
    }
}
```

## 7. 测试

最后，现在让我们测试对我们的应用程序的访问。仅当提供有效凭据时才允许访问：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MultipleAuthProvidersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultipleAuthProvidersApplicationIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenMemUsers_whenGetPingWithValidUser_thenOk() {
        ResponseEntity<String> result = makeRestCallToGetPing("memUser", "pass");
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("OK");
    }

    @Test
    void givenExternalUsers_whenGetPingWithValidUser_thenOk() {
        ResponseEntity<String> result = makeRestCallToGetPing("externaluser", "pass");
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("OK");
    }

    @Test
    void givenAuthProviders_whenGetPingWithNoCred_then401() {
        ResponseEntity<String> result = makeRestCallToGetPing();
        assertThat(result.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    void givenAuthProvider_whenGetPingWithBadCred_then401() {
        ResponseEntity<String> result = makeRestCallToGetPing("user", "bad_password");
        assertThat(result.getStatusCodeValue()).isEqualTo(401);
    }

    private ResponseEntity<String> makeRestCallToGetPing(String username, String password) {
        return restTemplate.withBasicAuth(username, password).getForEntity("/api/ping", String.class, Collections.emptyMap());
    }

    private ResponseEntity<String> makeRestCallToGetPing() {
        return restTemplate.getForEntity("/api/ping", String.class, Collections.emptyMap());
    }
}
```

## 8. 总结

在本快速教程中，我们了解了如何在Spring Security中配置多个AuthenticationProvider。
我们使用自定义AuthenticationProvider序和内存中AuthenticationProvider保护了一个简单的API端点。

我们还编写了测试来验证对我们应用程序的访问需要可以由我们的至少一个AuthenticationProvider验证的凭据。
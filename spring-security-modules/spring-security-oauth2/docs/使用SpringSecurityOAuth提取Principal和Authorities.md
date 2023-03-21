## 1. 概述

在本教程中，我们将说明如何使用Spring Boot和Spring Security OAuth创建一个将用户身份验证委托给第三方以及自定义授权服务器的应用程序。

此外，**我们将演示如何使用Spring的[PrincipalExtractor](https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/api/org/springframework/boot/autoconfigure/security/oauth2/resource/PrincipalExtractor.html)和[AuthoritiesExtractor](https://docs.spring.io/spring-security-oauth2-boot/docs/current/api/org/springframework/boot/autoconfigure/security/oauth2/resource/AuthoritiesExtractor.html)接口提取Principal和Authorities**。

有关Spring Security OAuth2的介绍，请参阅[这些](https://www.baeldung.com/spring-security-oauth)文章。

## 2. Maven依赖

首先，我们需要将[spring-security-oauth2-autoconfigure](https://central.sonatype.com/artifact/org.springframework.security.oauth.boot/spring-security-oauth2-autoconfigure/2.6.8)依赖添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.6.8</version>
</dependency>
```

## 3. 使用Github进行OAuth身份验证

接下来，让我们创建应用程序的安全配置：

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
              .authorizeRequests()
              .antMatchers("/login**")
              .permitAll()
              .anyRequest()
              .authenticated()
              .and()
              .formLogin()
              .disable()
              .oauth2Login();
        return http.build();
    }
}
```

简而言之，我们说任何人都可以访问/login端点，并且所有其他端点都需要用户身份验证。

**为至少一个客户端添加以下属性将启用Oauth2ClientAutoConfiguration类**，它设置所有必要的beans。

```properties
spring.security.oauth2.client.registration.github.client-id=89a7c4facbb3434d599d
spring.security.oauth2.client.registration.github.client-secret=9b3b08e4a340bd20e866787e4645b54f73d74b6a
spring.security.oauth2.client.registration.github.scope=read:user,user:email

spring.security.oauth2.client.provider.github.token-uri=https://github.com/login/oauth/access_token
spring.security.oauth2.client.provider.github.authorization-uri=https://github.com/login/oauth/authorize
spring.security.oauth2.client.provider.github.user-info-uri=https://api.github.com/user
```

我们没有处理用户帐户管理，而是将其委托给第三方(在本例中为Github)，从而使我们能够专注于应用程序的逻辑。

## 4. 提取Principal和Authorities

在充当OAuth客户端并通过第三方对用户进行身份验证时，我们需要考虑三个步骤：

1. 用户身份验证：用户向第三方进行身份验证
2. 用户授权：在身份验证之后，即用户允许我们的应用程序代表他们执行某些操作时；这就是[作用域](https://www.oauth.com/oauth2-servers/scope/defining-scopes/)的用武之地
3. 获取用户数据：使用我们获得的OAuth令牌来检索用户数据

一旦我们检索到用户的数据，**Spring就能够自动创建用户的Principal和Authorities**。

虽然这可能是可以接受的，但我们经常会需要完全控制他们。

为此，**Spring为我们提供了两个接口，我们可以用来覆盖其默认行为**：

+ PrincipalExtractor：我们可以用来提供自定义逻辑来提取Principal的接口
+ AuthoritiesExtractor：类似于PrincipalExtractor，但它用于自定义Authorities提取

默认情况下，**Spring提供了两个组件[FixedPrincipalExtractor](https://docs.spring.io/spring-boot/docs/2.0.0.M4/api/index.html?org/springframework/boot/autoconfigure/security/oauth2/resource/PrincipalExtractor.html)和[FixedAuthoritiesExtractor](https://docs.spring.io/spring-boot/docs/2.0.0.M4/api/org/springframework/boot/autoconfigure/security/oauth2/resource/FixedAuthoritiesExtractor.html)**-它们实现了这些接口并有一个预定义的策略来为我们创建它们。

### 4.1 自定义Github的身份验证

在我们的例子中，我们知道Github的[用户数据](https://docs.github.com/en/rest/reference/users#get-the-authenticated-user)是什么样的，以及我们可以使用什么来根据我们的需要自定义它们。

因此，要覆盖Spring的默认组件，我们只需要创建两个也实现这些接口的bean。

对于我们应用程序的Principal，我们只需使用用户的Github用户名：

```java
public class GithubPrincipalExtractor implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        return map.get("login");
    }
}
```

根据我们用户的Github订阅(免费或其他方式)，我们将授予他们GITHUB_USER_SUBSCRIBED或GITHUB_USER_FREE权限：

```java
public class GithubAuthoritiesExtractor implements AuthoritiesExtractor {

    private final List<GrantedAuthority> GITHUB_FREE_AUTHORITIES = AuthorityUtils
          .commaSeparatedStringToAuthorityList("GITHUB_USER,GITHUB_USER_FREE");
    private final List<GrantedAuthority> GITHUB_SUBSCRIBED_AUTHORITIES = AuthorityUtils
          .commaSeparatedStringToAuthorityList("GITHUB_USER,GITHUB_USER_SUBSCRIBED");

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        if (Objects.nonNull(map.get("plan"))) {
            if (!((LinkedHashMap) map.get("plan")).get("name").equals("free")) {
                return GITHUB_SUBSCRIBED_AUTHORITIES;
            }
        }
        return GITHUB_FREE_AUTHORITIES;
    }
}
```

然后，我们还需要使用这些类创建bean：

```java
@Configuration
public class SecurityConfig {

    @Bean
    @Profile("oauth2-extractors-github")
    public PrincipalExtractor githubPrincipalExtractor() {
        return new GithubPrincipalExtractor();
    }

    @Bean
    @Profile("oauth2-extractors-github")
    public AuthoritiesExtractor githubAuthoritiesExtractor() {
        return new GithubAuthoritiesExtractor();
    }
}
```

### 4.2 使用自定义授权服务器

我们还可以为用户使用我们自己的授权服务器-而不是依赖第三方。

尽管我们决定使用授权服务器，但我们自定义Principal和Authorities所需的组件保持不变：**PrincipalExtractor**和**AuthoritiesExtractor**。

我们只需要**了解user-info-uri端点返回的数据**，并根据需要使用它。

让我们更改我们的应用程序以使用本文中所述的授权服务器对用户进行身份验证：

```properties
spring.security.oauth2.client.registration.baeldung.client-id=SampleClientId
spring.security.oauth2.client.registration.baeldung.client-secret=secret

spring.security.oauth2.client.provider.baeldung.token-uri=http://localhost:8081/auth/oauth/token
spring.security.oauth2.client.provider.baeldung.authorization-uri=http://localhost:8081/auth/oauth/authorize
spring.security.oauth2.client.provider.baeldung.user-info-uri=http://localhost:8081/auth/user/me
```

现在我们指向我们的授权服务器，我们需要创建两个提取器；在这种情况下，我们的PrincipalExtractor将通过name键从Map中提取Principal：

```java
public class TuyuchengPrincipalExtractor implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        return map.get("name");
    }
}
```

至于权限，我们的授权服务器已经将它们放在其user-info-uri的数据中。

因此，我们将提取和填充它们：

```java
public class TuyuchengAuthoritiesExtractor implements AuthoritiesExtractor {

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(asAuthorities(map));
    }

    private String asAuthorities(Map<String, Object> map) {
        List<String> authorities = new ArrayList<>();
        authorities.add("TUYUCHENG_USER");
        List<LinkedHashMap<String, String>> auth = (List<LinkedHashMap<String, String>>) map.get("authorities");
        for (LinkedHashMap<String, String> entry : auth) {
            authorities.add(entry.get("authority"));
        }
        return String.join(",", authorities);
    }
}
```

然后我们将bean添加到SecurityConfig类中：

```java
@Configuration
public class SecurityConfig {

    @Bean
    @Profile("oauth2-extractors-tuyucheng")
    public PrincipalExtractor tuyuchengPrincipalExtractor() {
        return new TuyuchengPrincipalExtractor();
    }

    @Bean
    @Profile("oauth2-extractors-tuyucheng")
    public AuthoritiesExtractor tuyuchengAuthoritiesExtractor() {
        return new TuyuchengAuthoritiesExtractor();
    }
}
```

## 5. 总结

在本文中，我们实现了一个将用户身份验证委托给第三方以及自定义授权服务器的应用程序，并演示了如何自定义Principal和Authorities。

在本地运行时，你可以在localhost:8082运行和测试应用程序。
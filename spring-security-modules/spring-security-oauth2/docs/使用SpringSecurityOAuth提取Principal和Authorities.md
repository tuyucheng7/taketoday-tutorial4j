## 1. 概述

在本教程中，我们将说明如何使用Spring Boot和Spring Security OAuth创建一个将用户身份验证委托给第三方以及自定义授权服务器的应用程序。

此外，我们还将演示如何使用Spring的PrincipalExtractor和AuthoritiesExtractor接口提取Principal和Authorities。

## 2. Maven依赖

首先，我们需要将spring-security-oauth2-autoconfigure依赖添加到我们的pom.xml中：

```xml

<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. 使用Github进行OAuth身份验证

首先，我们创建一个Security配置类：

```java

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/")
                .authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().disable();
    }
}
```

任何人都可以访问/login端点，并且所有其他端点都需要用户身份验证。

我们还使用@EnableOAuthSso标注了我们的配置类，它将我们的应用程序转换为OAuth客户端并为其创建必要的组件以使其正常运行。

虽然Spring默认为我们创建了大部分组件，但我们仍然需要配置一些属性：

```properties
security.oauth2.client.client-id=89a7c4facbb3434d599d
security.oauth2.client.client-secret=9b3b08e4a340bd20e866787e4645b54f73d74b6a
security.oauth2.client.access-token-uri=https://github.com/login/oauth/access_token
security.oauth2.client.user-authorization-uri=https://github.com/login/oauth/authorize
security.oauth2.client.scope=read:user,user:email
security.oauth2.resource.user-info-uri=https://api.github.com/user
```

我们没有处理用户帐户管理，而是将其委托给第三方(在本例中为Github)，从而使我们能够专注于应用程序的逻辑。

## 4. 提取Principal和Authorities

当充当OAuth客户端并通过第三方对用户进行身份验证时，我们需要考虑三个步骤：

1. 用户身份验证 - 用户与第三方进行身份验证。
2. 用户授权 - 在身份验证之后，用户允许我们的应用程序代表他们执行某些操作时；这就是作用域的用武之地。
3. 获取用户数据 - 使用我们获得的OAuth令牌来检索用户数据。

一旦我们检索到用户的数据，Spring就能够自动创建用户的Principal和Authorities。

虽然这可能是可以接受的，但我们经常会想完全控制他们。

为此，Spring为我们提供了两个接口，可以用来覆盖其默认行为：

+ PrincipalExtractor – 我们可以使用提供自定义逻辑来提取Principal。
+ AuthoritiesExtractor - 类似于PrincipalExtractor，但它用于自定义Authorities。

默认情况下，Spring提供了两个组件FixedPrincipalExtractor和FixedAuthoritiesExtractor，它们实现了这些接口并具有为我们创建它们的预定义策略。

## 4.1 自定义Github的身份验证

在我们的案例中，我们知道Github的用户数据是什么样的，以及我们可以使用什么来根据我们的需要定制它们。

因此，要覆盖Spring的默认组件，我们只需要创建两个Bean来实现这些接口。

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

然后，我们需要创建这些类的bean：

```java

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

我们也可以为用户使用我们自己的授权服务器，而不是依赖第三方。

尽管我们决定使用授权服务器，但我们需要自定义Principal和Authorities的组件保持不变：PrincipalExtractor和AuthoritiesExtractor。

我们只需要了解user-info-uri端点返回的数据，并在我们认为合适的时候使用它。

让我们更改我们的应用程序以使用本文中描述的授权服务器来验证我们的用户：

```properties
security.oauth2.client.client-id=SampleClientId
security.oauth2.client.client-secret=secret
security.oauth2.client.access-token-uri=http://localhost:8081/auth/oauth/token
security.oauth2.client.user-authorization-uri=http://localhost:8081/auth/oauth/authorize
security.oauth2.resource.user-info-uri=http://localhost:8081/auth/user/me
```

现在我们指向我们的授权服务器，我们需要创建两个提取器；
在这种情况下，我们的PrincipalExtractor将通过name从Map中提取Principal：

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
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

在本地运行时，可以在localhost:8082运行和测试应用程序
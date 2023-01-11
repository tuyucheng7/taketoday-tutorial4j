## 1. 简介

在本教程中，我们将展示如何自定义从[JWT](https://www.baeldung.com/java-json-web-tokens-jjwt)(JSON Web 令牌)声明到 Spring Security 的Authorities的映射。

## 2. 背景

当一个正确配置的基于 Spring Security 的应用程序接收到一个请求时，它会经历一系列步骤，本质上是为了两个目标：

-   对请求进行身份验证，以便应用程序知道谁在访问它
-   决定经过身份验证的请求是否可以执行关联的操作

对于使用[JWT 作为其主要安全机制](https://www.baeldung.com/spring-security-oauth-jwt)的应用程序，授权方面包括：

-   从 JWT 有效负载中提取声明值，通常是范围或scp声明
-   将这些声明映射到一组GrantedAuthority对象

一旦安全引擎设置了这些权限，它就可以评估是否有任何访问限制适用于当前请求并决定是否可以继续。

## 3. 默认映射

开箱即用，Spring 使用简单的策略将声明转换为GrantedAuthority实例。首先，它提取范围或scp声明并将其拆分为字符串列表。接下来，对于每个字符串，它使用前缀SCOPE_后跟范围值创建一个新的SimpleGrantedAuthority 。

为了说明这个策略，让我们创建一个简单的端点，它允许我们检查应用程序可用的Authentication实例的一些关键属性：

```java
@RestController
@RequestMapping("/user")
public class UserRestController {
    
    @GetMapping("/authorities")
    public Map<String,Object> getPrincipalInfo(JwtAuthenticationToken principal) {
        
        Collection<String> authorities = principal.getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toList());
        
        Map<String,Object> info = new HashMap<>();
        info.put("name", principal.getName());
        info.put("authorities", authorities);
        info.put("tokenAttributes", principal.getTokenAttributes());
        
        return info;
    }
}

```

在这里，我们使用JwtAuthenticationToken参数，因为我们知道，当使用基于 JWT 的身份验证时，这将是Spring Security 创建的实际身份验证实现。我们从其name属性、可用的GrantedAuthority实例和 JWT 的原始属性中提取结果来创建结果。

现在，假设我们调用包含此有效负载的此端点传递和编码和签名的 JWT：

```json
{
  "aud": "api://f84f66ca-591f-4504-960a-3abc21006b45",
  "iss": "https://sts.windows.net/2e9fde3a-38ec-44f9-8bcd-c184dc1e8033/",
  "iat": 1648512013,
  "nbf": 1648512013,
  "exp": 1648516868,
  "email": "psevestre@gmail.com",
  "family_name": "Sevestre",
  "given_name": "Philippe",
  "name": "Philippe Sevestre",
  "scp": "profile.read",
  "sub": "eXWysuqIJmK1yDywH3gArS98PVO1SV67BLt-dvmQ-pM",
  ... more claims omitted
}
```

响应应该是一个具有三个属性的 JSON 对象：

```json
{
  "tokenAttributes": {
     // ... token claims omitted
  },
  "name": "0047af40-473a-4dd3-bc46-07c3fe2b69a5",
  "authorities": [
    "SCOPE_profile",
    "SCOPE_email",
    "SCOPE_openid"
  ]
}
```

我们可以通过创建SecurityFilterChain使用这些范围来限制对应用程序某些部分的访问：

```java
@Bean
SecurityFilterChain customJwtSecurityChain(HttpSecurity http) throws Exception {
    return http.authorizeRequests(auth -> {
      auth.antMatchers("/user/")
        .hasAuthority("SCOPE_profile");
    })
    .build();
}
```

请注意，我们有意避免使用WebSecurityConfigureAdapter。如前所述，此类将在 Spring Security 版本 5.7[中](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)弃用，因此最好尽快开始使用新方法。

或者，我们可以使用方法级别的注解和 SpEL 表达式来实现相同的结果：

```java
@GetMapping("/authorities")
@PreAuthorize("hasAuthority('SCOPE_profile.read')")
public Map<String,Object> getPrincipalInfo(JwtAuthenticationToken principal) {
    // ... same code as before
}
```

最后，对于更复杂的场景，我们还可以求助于直接访问当前的JwtAuthenticationToken，从中我们可以直接访问所有GrantedAuthorities

## 4.自定义SCOPE_前缀

作为我们如何更改 Spring Security 的默认声明映射行为的第一个示例，让我们看看如何将SCOPE_前缀更改为其他内容。如文档中所述，此任务涉及两个类：

-   JwtAuthenticationConverter：将原始 JWT 转换为AbstractAuthenticationToken
-   JwtGrantedAuthoritiesConverter ：从原始 JWT中提取一组GrantedAuthority实例。

在内部，JwtAuthenticationConverter使用JwtGrantedAuthoritiesConverter用GrantedAuthority对象和其他属性填充JwtAuthenticationToken 。

更改此前缀的最简单方法是提供我们自己的JwtAuthenticationConverter bean，将JwtGrantedAuthoritiesConverter配置为我们自己的选择之一：

```java
@Configuration
@EnableConfigurationProperties(JwtMappingProperties.class)
@EnableMethodSecurity
public class SecurityConfig {
    // ... fields and constructor omitted
    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        if (StringUtils.hasText(mappingProps.getAuthoritiesPrefix())) {
            converter.setAuthorityPrefix(mappingProps.getAuthoritiesPrefix().trim());
        }
        return converter;
    }
    
    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter();
        return converter;
    }

```

在这里，JwtMappingProperties只是一个@ConfigurationProperties类，我们将使用它来外部化映射属性。尽管此代码段中未显示，但我们将使用构造函数注入来初始化mappingProps字段，并使用从任何已配置的PropertySource填充的实例，从而为我们提供足够的灵活性来在部署时更改其值。

这个@Configuration类有两个@Bean方法： jwtGrantedAuthoritiesConverter()创建创建 GrantedAuthority集合所需的 转换器 。在这种情况下，我们使用配置了配置属性中设置的前缀的股票JwtGrantedAuthoritiesConverter 。

接下来，我们有customJwtAuthenticationConverter()，我们在其中构造JwtAuthenticationConverter配置为使用我们的自定义转换器。从那里，Spring Security 将把它作为其标准自动配置过程的一部分，并替换默认的。

现在，一旦我们将baeldung.jwt.mapping.authorities-prefix属性设置为某个值，例如MY_SCOPE并调用 /user/authorities，我们将看到自定义权限：

```json
{
  "tokenAttributes": {
    // ... token claims omitted 
  },
  "name": "0047af40-473a-4dd3-bc46-07c3fe2b69a5",
  "authorities": [
    "MY_SCOPE_profile",
    "MY_SCOPE_email",
    "MY_SCOPE_openid"
  ]
}
```

## 5. 在安全结构中使用自定义前缀

需要注意的是，通过更改权限前缀，我们将影响任何依赖其名称的授权规则。例如，如果我们将前缀更改为MY_PREFIX_，任何假定默认前缀的@PreAuthorize表达式都将不再起作用。这同样适用于基于HttpSecurity的授权构造。

然而，解决这个问题很简单。首先，让我们在@Configuration类中添加一个返回配置前缀的@Bean方法。由于此配置是可选的，因此我们必须确保在没有人提供的情况下返回默认值：

```java
@Bean
public String jwtGrantedAuthoritiesPrefix() {
  return mappingProps.getAuthoritiesPrefix() != null ?
    mappingProps.getAuthoritiesPrefix() : 
      "SCOPE_";
}

```

[现在，我们可以在 SpEL 表达式中](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html#el-access-web-beans)使用[@](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html#el-access-web-beans)语法来引用这个 bean 。这就是我们将前缀 bean 与@PreAuthorize一起使用的方式：

```java
@GetMapping("/authorities")
@PreAuthorize("hasAuthority(@jwtGrantedAuthoritiesPrefix + 'profile.read')")
public Map<String,Object> getPrincipalInfo(JwtAuthenticationToken principal) {
    // ... method implementation omitted
}
```

在定义SecurityFilterChain时，我们也可以使用类似的方法 ：

```java
@Bean
SecurityFilterChain customJwtSecurityChain(HttpSecurity http) throws Exception {
    return http.authorizeRequests(auth -> {
        auth.antMatchers("/user/")
          .hasAuthority(mappingProps.getAuthoritiesPrefix() + "profile");
      })
      // ... other customizations omitted
      .build();
}

```

## 6.自定义校长姓名

有时， Spring 映射到Authentication的name属性的标准sub声明带有一个不是很有用的值。Keycloak 生成的 JWT 就是一个很好的例子：

```json
{
  // ... other claims omitted
  "sub": "0047af40-473a-4dd3-bc46-07c3fe2b69a5",
  "scope": "openid profile email",
  "email_verified": true,
  "name": "User Primo",
  "preferred_username": "user1",
  "given_name": "User",
  "family_name": "Primo"
}
```

在这种情况下，sub带有一个内部标识符，但我们可以看到preferred_username声明具有更友好的值。我们可以通过将其principalClaimName属性设置为所需的声明名称来轻松修改JwtAuthenticationConverter的行为：

```java
@Bean
public JwtAuthenticationConverter customJwtAuthenticationConverter() {

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());

    if (StringUtils.hasText(mappingProps.getPrincipalClaimName())) {
        converter.setPrincipalClaimName(mappingProps.getPrincipalClaimName());
    }
    return converter;
}

```

现在，如果我们将baeldung.jwt.mapping.authorities-prefix属性设置为“preferred_username”， /user/authorities结果将相应更改：

```json
{
  "tokenAttributes": {
    // ... token claims omitted 
  },
  "name": "user1",
  "authorities": [
    "MY_SCOPE_profile",
    "MY_SCOPE_email",
    "MY_SCOPE_openid"
  ]
}
```

## 7. 范围名称映射

有时，我们可能需要将 JWT 中接收的范围名称映射到内部名称。例如，这可能是同一应用程序需要使用由不同授权服务器生成的令牌的情况，具体取决于部署它的环境。

我们可能很想扩展JwtGrantedAuthoritiesConverter，但由于这是一个最终类，我们不能使用这种方法。相反，我们必须编写自己的 Converter 类并将其注入JwtAuthorizationConverter。这个增强的映射器 MappingJwtGrantedAuthoritiesConverter实现了Converter<Jwt, Collection<GrantedAuthority>> 并且看起来很像原来的：

```java
public class MappingJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private Map<String,String> scopes;
    private String authoritiesClaimName = null;
    private String authorityPrefix = "SCOPE_";
     
    // ... constructor and setters omitted

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        
        Collection<String> tokenScopes = parseScopesClaim(jwt);
        if (tokenScopes.isEmpty()) {
            return Collections.emptyList();
        }
        
        return tokenScopes.stream()
          .map(s -> scopes.getOrDefault(s, s))
          .map(s -> this.authorityPrefix + s)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toCollection(HashSet::new));
    }
    
    protected Collection<String> parseScopesClaim(Jwt jwt) {
       // ... parse logic omitted 
    }
}
```

在这里，这个类的关键方面是映射步骤，我们使用提供的范围映射将原始范围转换为映射的范围。此外，任何没有可用映射的传入范围都将被保留。

最后，我们在@Configuration的jwtGrantedAuthoritiesConverter()方法中使用这个增强型转换器：

```java
@Bean
public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    MappingJwtGrantedAuthoritiesConverter converter = new MappingJwtGrantedAuthoritiesConverter(mappingProps.getScopes());

    if (StringUtils.hasText(mappingProps.getAuthoritiesPrefix())) {
        converter.setAuthorityPrefix(mappingProps.getAuthoritiesPrefix());
    }
    if (StringUtils.hasText(mappingProps.getAuthoritiesClaimName())) {
        converter.setAuthoritiesClaimName(mappingProps.getAuthoritiesClaimName());
    }
    return converter;
}

```

## 8. 使用自定义JwtAuthenticationConverter

在这种情况下，我们将完全控制JwtAuthenticationToken生成过程。我们可以使用这种方法返回此类的扩展版本，其中包含从数据库中恢复的附加数据。

有两种可能的方法来替换标准JwtAuthenticationConverter。第一个，我们在前面的部分中使用过，是创建一个@Bean 方法来返回我们的自定义转换器。然而，这意味着我们的定制版本必须扩展 Spring 的JwtAuthenticationConverter，以便自动配置过程可以选择它。

第二种选择是使用基于HttpSecurity的 DSL 方法，我们可以在其中提供自定义转换器。我们将使用oauth2ResourceServer定制器来做到这一点，它允许我们插入任何实现更通用接口Converter<Jwt, AbstractAuthorizationToken>的转换器：

```java
@Bean
SecurityFilterChain customJwtSecurityChain(HttpSecurity http) throws Exception {
    return http.oauth2ResourceServer(oauth2 -> {
        oauth2.jwt()
          .jwtAuthenticationConverter(customJwtAuthenticationConverter());
      })
      .build();
}

```

我们的CustomJwtAuthenticationConverter使用AccountService(可在线获得)根据用户名声明值检索Account对象。然后它使用它来创建一个CustomJwtAuthenticationToken，并为帐户数据提供一个额外的访问器方法：

```java
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // ...private fields and construtor omitted
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(source);
        String principalClaimValue = source.getClaimAsString(this.principalClaimName);
        Account acc = accountService.findAccountByPrincipal(principalClaimValue);
        return new AccountToken(source, authorities, principalClaimValue, acc);
    }
}

```

现在，让我们修改我们的/user/authorities处理程序以使用我们增强的Authentication：

```java
@GetMapping("/authorities")
public Map<String,Object> getPrincipalInfo(JwtAuthenticationToken principal) {
    
    // ... create result map as before (omitted)
    if (principal instanceof AccountToken) {
        info.put( "account", ((AccountToken)principal).getAccount());
    }
    return info;
}

```

采用这种方法的一个优点是我们现在可以轻松地在应用程序的其他部分使用我们增强的身份验证对象。例如，我们可以直接从内置变量 authentication访问[SpEL](https://www.baeldung.com/spring-expression-language)表达式中的帐户信息：

```json
@GetMapping("/account/{accountNumber}")
@PreAuthorize("authentication.account.accountNumber == #accountNumber")
public Account getAccountById(@PathVariable("accountNumber") String accountNumber, AccountToken authentication) {
    return authentication.getAccount();
}

```

这里，@PreAuthorize表达式强制在路径变量中传递的 accountNumber属于用户。[如官方文档](https://docs.spring.io/spring-security/reference/servlet/integrations/data.html)中所述，此方法在与 Spring Data JPA 结合使用时特别有用。

## 9. 测试技巧

到目前为止给出的示例假设我们有一个有效的身份提供者 (IdP)，它发布基于 JWT 的访问令牌。一个不错的选择是使用我们已经[在这里介绍](https://www.baeldung.com/keycloak-embedded-in-spring-boot-app)过的嵌入式 Keycloak 服务器。[我们的使用 Keycloak 的快速指南](https://www.baeldung.com/spring-boot-keycloak)中还提供了其他配置说明。

请注意，这些说明涵盖了如何注册 OAuth客户端。对于现场测试，Postman 是一个很好的支持授权代码流的工具。这里的重要细节是如何正确配置 有效重定向 URI参数。由于 Postman 是一个桌面应用程序，它使用位于https://oauth.pstmn.io/v1/callback的帮助站点来捕获授权代码。因此，我们必须确保在测试期间有互联网连接。如果这不可能，我们可以使用安全性较低的密码授予流程。

无论选择何种 IdP 和客户端选择，我们都必须配置我们的资源服务器，以便它可以正确验证接收到的 JWT。对于标准 OIDC 提供者，这意味着为spring.security.oauth2.resourceserver.jwt.issuer-uri属性提供合适的值。然后 Spring 将使用那里可用的.well-known/openid-configuration文档获取所有配置详细信息。

在我们的例子中，我们的 Keycloak 领域的颁发者 URI 是http://localhost:8083/auth/realms/baeldung。我们可以将浏览器指向http://localhost:8083/auth/realms/baeldung/.well-known/openid-configuration以检索完整文档。

## 10. 总结

在本文中，我们展示了自定义 Spring Security 从 JWT 声明映射权限的不同方式。像往常一样，完整的代码可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-oidc)。
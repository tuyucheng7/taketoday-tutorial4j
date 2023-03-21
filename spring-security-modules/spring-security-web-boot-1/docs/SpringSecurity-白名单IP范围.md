## 1. 概述

在本教程中，我们将讨论**如何在Spring Security中将IP范围列入白名单**。

我们将看看Java和XML配置，并了解如何使用自定义AuthenticationProvider将IP范围列入白名单。

## 2. Java配置

首先，让我们探索Java配置。

**我们可以使用hasIpAddress()仅允许具有给定IP地址的用户访问特定资源**。

下面是使用hasIpAddress()的简单安全配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .antMatchers("/login").permitAll()
              .antMatchers("/foos/**").hasIpAddress("11.11.11.11")
              .anyRequest().authenticated()
              .and()
              .formLogin().permitAll()
              .and()
              .csrf().disable();
    }

    // ...
}
```

在此配置中，只有IP地址为“11.11.11.11”的用户才能访问"/foos"资源。具有白名单IP的用户在访问“/foos/“ URL之前也无需登录。

如果我们希望让IP为“11.11.11.11”的用户先登录，我们可以使用如下形式的表达式中的方法：

```java
// ...
.antMatchers("/foos/**")
.access("isAuthenticated() and hasIpAddress('11.11.11.11')")
// ...
```

## 3. XML配置

接下来，让我们看看如何使用XML配置将IP范围列入白名单：

我们也将在这里使用hasIpAddress()：

```xml
<security:http>
    <security:form-login/>
    <security:intercept-url pattern="/login" access="permitAll()" />
    <security:intercept-url pattern="/foos/**" access="hasIpAddress('11.11.11.11')" />
    <security:intercept-url pattern="/**" access="isAuthenticated()" />
</security:http>
```

## 4. 测试

现在，这是一个简单的实时测试，以确保一切正常。

首先，我们将确保任何用户在登录后都可以访问主页：

```java
// In order to execute these tests, cn.tuyucheng.taketoday.roles.ip.IpApplication needs to be running.
class WhiteListIPLiveTest {

    @Test
    void givenUser_whenGetHomePage_thenOK() {
        final Response response = RestAssured.given().auth().form("john", "123").get("http://localhost:8080/");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Welcome"));
    }
}
```

接下来，我们将确保即使是经过身份验证的用户也无法访问"/foos"资源，除非他们的IP被列入白名单：

```java
@Test
void givenUserWithWrongIP_whenGetFooById_thenForbidden() {
    final Response response = RestAssured.given().auth().form("john", "123").get("http://localhost:8080/foos/1");
    assertEquals(403, response.getStatusCode());
    assertTrue(response.asString().contains("Forbidden"));
}
```

请注意，我们无法从本机"127.0.0.1"访问"/foos"资源，因为只有IP为"11.11.11.11”的用户才能访问它。

## 5. 使用自定义AuthenticationProvider加入白名单

最后，**我们将了解如何通过构建自定义AuthenticationProvider将IP范围列入白名单**。

我们已经了解了如何使用hasIpAddress()将IP范围列入白名单以及如何将其与其他表达式混合使用。但有时，**我们需要更多的自定义**。

在以下示例中，我们将多个IP地址列入白名单，只有来自这些IP地址的用户才能登录到我们的系统：

```java
@Component
public class CustomIpAuthenticationProvider implements AuthenticationProvider {
    Set<String> whitelist = new HashSet<>();

    public CustomIpAuthenticationProvider() {
        whitelist.add("11.11.11.11");
        whitelist.add("127.0.0.1");
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        String userIp = details.getRemoteAddress();
        if (!whitelist.contains(userIp))
            throw new BadCredentialsException("Invalid IP Address");
        final String name = auth.getName();
        final String password = auth.getCredentials().toString();
        if (name.equals("john") && password.equals("123")) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(name, password, authorities);
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

现在，我们将在安全配置中使用CustomIpAuthenticationProvider：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomIpAuthenticationProvider authenticationProvider;

    @Bean
    public InMemoryUserDetailsManager userDetailsService(HttpSecurity http) throws Exception {
        UserDetails user = User.withUsername("john")
              .password("{noop}123")
              .authorities("ROLE_USER")
              .build();
        http.getSharedObject(AuthenticationManagerBuilder.class)
              .authenticationProvider(authenticationProvider)
              .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .antMatchers("/login")
              .permitAll()
              .antMatchers("/foos/**")
              .access("isAuthenticated() and hasIpAddress('11.11.11.11')")
              .anyRequest()
              .authenticated()
              .and()
              .formLogin()
              .permitAll()
              .and()
              .csrf()
              .disable();
        return http.build();
    }
}
```

在这里，我们使用WebAuthenticationDetails的getRemoteAddress()方法来获取用户的IP地址。

因此，只有拥有白名单IP的用户才能访问我们的系统。

这是一个基本的实现，但我们可以根据需要使用用户的IP自定义我们的AuthenticationProvider。例如，我们可以在注册时将IP地址与用户详细信息一起存储，并在AuthenticationProvider中进行身份验证时进行比较。

## 6. 总结

我们学习了如何使用Java和XML配置在Spring Security中将IP范围列入白名单。我们还学习了如何通过构建自定义的AuthenticationProvider将IP范围列入白名单。
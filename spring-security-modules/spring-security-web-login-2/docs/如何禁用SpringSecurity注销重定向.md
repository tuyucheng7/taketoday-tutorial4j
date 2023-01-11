## 1. 概述

在这个教程中，我们介绍如何在Spring Security中禁用注销重定向。

首先，简要介绍注销流程在Spring Security中的工作原理。
然后，我们将通过一个实际的例子来说明如何避免用户在成功注销后重定向。

## 2. Spring Security中的注销

简而言之，Spring Security通过logout()方法为注销机制提供了开箱即用的支持。
**基本上，当用户访问默认的注销URL(即/logout)时，Spring Security会触发注销**。

值得一提的是，在Spring Security 4之前，注销URL的默认值为/j_spring_security_logout。

Spring Security提供了在注销后将用户重定向到特定URL的可能性。但是，在某些情况下，我们希望避免这种行为。

那么，废话不多说，让我们看看如何在Spring Security中实现禁用注销重定向的逻辑。

## 3. 禁用Spring Security注销重定向

默认情况下，Spring Security会在成功注销后将用户重定向到/login?logout(登录页面)。
因此，在本节中，我们将重点介绍如何防止用户在注销后重定向到登录页面。

请注意，我们可以在logoutSuccessUrl()方法的帮助下覆盖默认的重定向URL。

但我们这里的**重点是演示如何在从REST客户端调用/logout URL时避免重定向**。

事实上，LogoutSuccessHandler接口提供了一种在注销过程成功执行时执行自定义逻辑的灵活方式。

所以在这里，**我们使用一个自定义的LogoutSuccessHandler仅返回一个干净的200状态代码**。这样，它不会将我们重定向到任何页面。

现在，让我们实现禁用注销重定向所需的必要Spring Security配置：

```java

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth.mvcMatchers("/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .logout(logout -> logout.permitAll()
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)));

    }
}
```

上述配置中需要注意的重要部分是logoutSuccessHandler()方法。如我们所见，我们使用lambda表达式来定义我们的自定义LogoutSuccessHandler。

我们也可以创建LogoutSuccessHandler接口的简单实现类，并将其实例传递给logoutSuccessHandler()方法。

## 4. 测试

现在编写一个测试用例，测试访问/logout端点以确认一切都按预期工作。

我们将在测试中使用MockMvc发送/logout请求。**首先，创建一个简单的测试类并在其中注入MockMvc对象**：

```java

@ExtendWith(SpringExtension.class)
@WebMvcTest
class LogoutApplicationUnitTest {

    @Autowired
    private MockMvc mockMvc;
}
```

现在，让我们编写一个方法来测试我们的/logout端点：

```java
class LogoutApplicationUnitTest {

    @WithMockUser(value = "spring")
    @Test
    void whenLogout_thenDisableRedirect() throws Exception {
        this.mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(unauthenticated())
                .andReturn();
    }
}
```

对于上面的测试用例，需要理解的是：

+ perform(post("/logout")) - 将/logout端点作为一个简单的POST请求调用。
+ with(csrf()) - 将预期的 _csrf参数添加到查询中。
+ status() - 返回HTTP响应的状态码。
+ jsonPath() - 允许访问和检查HTTP响应的正文。

## 5. 总结

我们演示并说明了如何解决在Spring Security和Spring Boot中禁用注销重定向的问题。
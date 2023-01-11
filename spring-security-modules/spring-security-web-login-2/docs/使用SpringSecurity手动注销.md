## 1. 概述

Spring Security是保护基于Spring应用程序的标准，它有几个功能来管理用户的身份验证，包括登录和注销。

在本教程中，我们将重点介绍如何使用Spring Security手动注销。

## 2. 基本注销

**当用户尝试注销时，会对其当前会话状态产生多种影响**。我们需要通过两个步骤来销毁会话：

1. 使HTTP Session信息无效。
2. 清除SecurityContext，因为它包含身份验证信息。

这两个操作由SecurityContextLogoutHandler执行。

让我们看看实际情况：

```java

@Configuration
public class DefaultLogoutConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutUrl("/basic/basiclogout")
                .addLogoutHandler(new SecurityContextLogoutHandler())
        );
    }
}
```

请注意，默认情况下，SecurityContextLogoutHandler是由Spring Security添加的，为了清楚起见，我们只是在这里显示添加它。

## 3. Cookie清除注销

通常，注销还需要我们清除用户的部分或全部cookie。

为此，我们可以创建自己的LogoutHandler循环遍历所有cookie，并在注销时使它们过期：

```java

@Configuration
public static class AllCookieClearingLogoutConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/cookies/**")
                .authorizeRequests(auth -> auth.anyRequest().permitAll())
                .logout(logout -> logout.logoutUrl("/cookies/cookielogout")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                        .addLogoutHandler((request, response, auth) -> {
                            for (Cookie cookie : request.getCookies()) {
                                String cookieName = cookie.getName();
                                Cookie cookieToDelete = new Cookie(cookieName, null);
                                cookieToDelete.setMaxAge(0);
                                response.addCookie(cookieToDelete);
                            }
                        }));
    }
}
```

事实上，Spring Security提供了CookieClearingLogoutHandler，它是一个现成的注销处理程序，用于删除cookie。

## 4. Clear-Site-Data头注销

同样，我们可以使用一个特殊的HTTP响应头来实现相同的功能；这就是**Clear-Site-Data**头发挥作用的地方。

基本上，Clear-Data-Site头清除与请求网站相关的浏览数据(cookie、存储、缓存)：

```java

@Configuration
public static class ClearSiteDataHeaderLogoutConfiguration extends WebSecurityConfigurerAdapter {

    private static final ClearSiteDataHeaderWriter.Directive[] SOURCE = {CACHE, COOKIES, STORAGE, EXECUTION_CONTEXTS};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/csd/**")
                .authorizeRequests(auth -> auth.anyRequest().permitAll())
                .logout(logout -> logout.logoutUrl("/csd/csdlogout")
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(SOURCE))));
    }
}
```

然而，当我们只清除一种类型的存储时，存储清理可能会破坏应用程序状态。因此，由于未完成清除，只有在请求安全时才应用头。

## 5. 根据Request注销

同样，我们可以使用HttpServletRequest.logout()方法来注销用户。

首先，让我们添加必要的配置，以便在Request上手动调用logout()：

```java

@Configuration
public static class LogoutOnRequestConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/request/**")
                .authorizeRequests(auth -> auth.anyRequest().permitAll())
                .logout(logout -> logout.logoutUrl("/request/logout")
                        .addLogoutHandler((request, response, auth) -> {
                            try {
                                request.logout();
                            } catch (ServletException e) {
                                logger.error(e.getMessage());
                            }
                        }));
    }
}
```

最后，我们编写一个测试用例来确认一切都按预期工作：

```java

@ExtendWith(SpringExtension.class)
@WebMvcTest()
class ManualLogoutIntegrationTest {

    private static final String CLEAR_SITE_DATA_HEADER = "Clear-Site-Data";
    public static final int EXPIRY = 60 * 10;
    public static final String COOKIE_NAME = "customerName";
    public static final String COOKIE_VALUE = "myName";
    public static final String ATTRIBUTE_NAME = "att";
    public static final String ATTRIBUTE_VALUE = "attvalue";

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(value = "spring")
    @Test
    public void givenLoggedUser_whenUserLogoutOnRequest_thenSessionCleared() throws Exception {
        this.mockMvc.perform(post("/request/logout").secure(true)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andReturn();
    }
}
```

## 6. 总结

总之，Spring Security有很多内置的特性来处理身份验证场景，掌握如何以编程方式使用这些功能是很方便的。
## 1. 概述

Spring Security框架为[身份验证](https://www.baeldung.com/spring-security-authentication-and-registration)提供了非常灵活和强大的支持。与用户标识一起，我们通常需要处理用户注销事件，并且在某些情况下添加一些自定义注销行为。其中一个可能的用例是使用户缓存无效或关闭经过身份验证的会话。

为此，Spring提供了LogoutHandler接口，在本教程中，我们将了解如何实现我们自己的自定义注销处理程序。

## 2. 处理注销请求

每个登录用户的Web应用程序都必须在某一天将其注销。Spring Security处理程序通常控制[注销过程](https://www.baeldung.com/spring-security-logout)。基本上，我们有两种处理注销的方法。正如我们将要看到的，其中之一是实现[LogoutHandler](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/authentication/logout/LogoutHandler.html)接口。

### 2.1 LogoutHandler接口

LogoutHandler接口的定义如下：

```java
public interface LogoutHandler {
    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}
```

可以根据需要向应用程序添加任意数量的注销处理程序。**实现的一个要求是不抛出异常**。这是因为处理程序操作不能在注销时破坏应用程序状态。

例如，其中一个处理程序可能会执行一些缓存清理，并且其方法必须成功完成，在教程示例中，我们将准确展示此用例。

### 2.2 LogoutSuccessHandler接口

另一方面，我们可以使用异常来控制用户注销策略。为此，我们有[LogoutSuccessHandler](https://www.baeldung.com/spring-security-track-logged-in-users#2-implementing-logoutsuccesshandler)接口和onLogoutSuccess方法。此方法可能会引发异常，以将用户重定向设置为适当的目标。

此外，**使用LogoutSuccessHandler类型时不可能添加多个处理程序**，因此应用程序只有一个可能的实现。一般来说，事实证明这是注销策略的最后一点。

## 3. LogoutHandler接口实践

现在，让我们创建一个简单的Web应用程序来演示注销处理过程。我们将实现一些简单的缓存逻辑来检索用户数据，以避免对数据库造成不必要的访问。

让我们从application.properties文件开始，它包含示例应用程序的数据库连接属性：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/test
spring.datasource.username=test
spring.datasource.password=test
spring.jpa.hibernate.ddl-auto=create
```

### 3.1 Web应用程序设置

接下来，我们将添加一个简单的User实体，用于登录和数据检索。正如我们所见，User类映射到我们数据库中的users表：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String login;

    private String password;

    private String role;

    private String language;

    // standard setters and getters
}
```

出于应用程序的缓存目的，我们将实现一个缓存服务，它在内部使用ConcurrentHashMap来存储User：

```java
@Service
public class UserCache {
    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>(256);
    
    @PersistenceContext
    private EntityManager entityManager;
}
```

使用此服务，我们可以通过用户名(登录名)从数据库中检索用户并将其存储在Map内部：

```java
public User getByUserName(String userName) {
    return store.computeIfAbsent(userName, k ->
          entityManager.createQuery("from User where login=:login", User.class)
                .setParameter("login", k)
                .getSingleResult());
}
```

此外，可以将用户从Map从移除。正如我们稍后将看到的，这将是我们将从注销处理程序调用的主要操作：

```java
public void evictUser(String userName) {
    store.remove(userName);
}
```

为了检索用户数据和language信息，我们将使用标准[Spring Controller](https://www.baeldung.com/spring-controllers)：

```java
@Controller
@RequestMapping(path = "/user")
public class UserController {
    private final UserCache userCache;

    public UserController(UserCache userCache) {
        this.userCache = userCache;
    }

    @GetMapping(path = "/language")
    @ResponseBody
    public String getLanguage() {
        String userName = UserUtils.getAuthenticatedUserName();
        User user = userCache.getByUserName(userName);
        return user.getLanguage();
    }
}
```

### 3.2 Web安全配置

我们将在应用程序中关注两个简单的操作-登录和注销。首先，我们需要设置MVC配置类以允许用户使用[基本HTTP认证](https://www.baeldung.com/httpclient-4-basic-authentication)进行身份验证：

```java
@Configuration
@EnableWebSecurity
public class MvcConfiguration {
    
    @Autowired
    private CustomLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
              .and()
              .authorizeRequests()
              .antMatchers(HttpMethod.GET, "/user/**")
              .hasRole("USER")
              .and()
              .logout()
              .logoutUrl("/user/logout")
              .addLogoutHandler(logoutHandler)
              .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
              .permitAll()
              .and()
              .csrf()
              .disable()
              .formLogin()
              .disable();
        return http.build();
    }
}
```

从上面的配置中需要注意的重要部分是addLogoutHandler方法。我们**在注销处理结束时传递并触发我们的CustomLogoutHandler**，其余设置微调HTTP基本身份验证。

### 3.3 自定义注销处理程序

最后，也是最重要的一点，我们将编写自定义注销处理程序来处理必要的用户缓存清理：

```java
@Service
public class CustomLogoutHandler implements LogoutHandler {
    private final UserCache userCache;

    public CustomLogoutHandler(UserCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String userName = UserUtils.getAuthenticatedUserName();
        userCache.evictUser(userName);
    }
}
```

正如我们所看到的，我们重写了logout方法并简单地将给定用户从用户缓存中逐出。

## 4. 集成测试

现在让我们测试一下功能。首先，我们需要验证缓存是否按预期工作-**也就是说，它将经过的授权用户加载到其Map存储中**：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CustomLogoutApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({ 
      @Sql(value = "classpath:customlogouthandler/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD), 
      @Sql(value = "classpath:customlogouthandler/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) })
@TestPropertySource(locations="classpath:customlogouthandler/application.properties")
class CustomLogoutHandlerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserCache userCache;

    @LocalServerPort
    private int port;

    @Test
    void whenLogin_thenUseUserCache() {
        // User cache should be empty on start
        assertThat(userCache.size()).isEqualTo(0);

        // Request using first login
        ResponseEntity<String> response = restTemplate.withBasicAuth("user", "pass")
                .getForEntity(getLanguageUrl(), String.class);

        assertThat(response.getBody()).contains("english");

        // User cache must contain the user
        assertThat(userCache.size()).isEqualTo(1);

        // Getting the session cookie
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", response.getHeaders()
                .getFirst(HttpHeaders.SET_COOKIE));

        // Request with the session cookie
        response = restTemplate.exchange(getLanguageUrl(), HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
        assertThat(response.getBody()).contains("english");

        // Logging out using the session cookies
        response = restTemplate.exchange(getLogoutUrl(), HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
        assertThat(response.getStatusCode()
                .value()).isEqualTo(200);
    }

    private String getLanguageUrl() {
        return "http://localhost:" + port + "/user/language";
    }

    private String getLogoutUrl() {
        return "http://localhost:" + port + "/user/logout";
    }
}
```

让我们逐一介绍这些步骤，以了解我们做了什么：

+ 首先，我们检查缓存是否为空
+ 接下来，我们通过withBasicAuth方法对用户进行身份验证
+ 现在我们可以验证检索到的用户数据和language值
+ 因此，我们可以验证用户现在必须在缓存中
+ 同样，我们通过访问language端点并使用会话cookie来检查用户数据
+ 最后，我们验证注销用户

在第二个测试中，我们将验证注销时用户缓存是否已清除。这是我们的注销处理程序将被调用的时刻：

```java
@Test
void whenLogout_thenCacheIsEmpty() {
    // User cache should be empty on start
    assertThat(userCache.size()).isEqualTo(0);

    // Request using first login
    ResponseEntity<String> response = restTemplate.withBasicAuth("user", "pass")
          .getForEntity(getLanguageUrl(), String.class);

    assertThat(response.getBody()).contains("english");

    // User cache must contain the user
    assertThat(userCache.size()).isEqualTo(1);

    // Getting the session cookie
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.add("Cookie", response.getHeaders()
          .getFirst(HttpHeaders.SET_COOKIE));

    // Logging out using the session cookies
    response = restTemplate.exchange(getLogoutUrl(), HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
    assertThat(response.getStatusCode()
          .value()).isEqualTo(200);

    // User cache must be empty now
    // this is the reaction on custom logout filter execution
    assertThat(userCache.size()).isEqualTo(0);

    // Assert unauthorized request
    response = restTemplate.exchange(getLanguageUrl(), HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
    assertThat(response.getStatusCode()
          .value()).isEqualTo(401);
}
```

+ 和以前一样，我们首先检查缓存是否为空
+ 然后我们对用户进行身份验证并检查用户是否在缓存中
+ 接下来，我们执行注销并检查用户是否已从缓存中删除
+ 最后，尝试访问language端点的结果是401响应码

## 5. 总结

在本教程中，我们学习了如何使用Spring的LogoutHandler接口实现自定义注销处理程序，用于从用户缓存中逐出用户。
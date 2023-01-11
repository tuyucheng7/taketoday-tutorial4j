## 1. 概述

在本教程中，我们将了解**如何在Spring Security应用程序中定义多个入口点**。

这主要需要在一个XML配置文件中定义多个http块，或者通过多次继承WebSecurityConfigurerAdapter类来定义多个HttpSecurity实例。

## 2. Maven依赖

对于开发，我们需要添加以下依赖项：

```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-web</artifactId> 
    <version>2.6.1</version> 
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.6.1</version>
</dependency>    
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <version>5.6.0</version>
</dependency>
```

## 3. 多个入口点

### 3.1 具有多个HTTP元素的多个入口点

让我们定义将保存用户源的主要配置类：

```java

@Configuration
@EnableWebSecurity
public class MultipleEntryPointsSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password(encoder().encode("userPass")).roles("USER").build());
        manager.createUser(User.withUsername("admin").password(encoder().encode("adminPass")).roles("ADMIN").build());
        return manager;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
```

现在，让我们看看如何在Security配置中定义多个入口点。

我们将在这里使用一个由Basic Authentication驱动的示例，并且我们将充分利用Spring Security支持在我们的配置中定义多个HTTP元素的功能。

使用Java配置时，定义多个Security realms的方法是创建多个继承WebSecurityConfigurerAdapter的@Configuration类，
每个类都有自己的Security配置。这些类可以是静态的并放置在主配置类中。

在一个应用程序中拥有多个入口点的主要动机是，如果有不同类型的用户可以访问应用程序的不同部分。

让我们定义一个具有三个入口点的配置，每个入口点具有不同的权限和身份验证模式：

+ 一个用于使用HTTP基本身份验证的admin用户。
+ 一个用于使用表单身份验证的普通用户。
+ 一个用于不需要身份验证的来宾用户。

为admin用户定义的入口点保护任何匹配/admin/**的URL，以仅允许具有ADMIN角色的用户访问，
并要求使用authenticationEntryPoint()方法设置的入口点类型为BasicAuthenticationEntryPoint的HTTP Basic身份验证：

```java

@Configuration
@Order(1)
public static class App1ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/admin/**")
                .authorizeRequests().anyRequest().hasRole("ADMIN")
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint())
                .and().exceptionHandling().accessDeniedPage("/403");
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
```

每个静态类上的@Order注解指示配置查找与请求的URL匹配的配置的顺序。**每个类的order值必须是唯一的**。

BasicAuthenticationEntryPoint类型的bean需要设置属性realName。

### 3.2 多个入口点，相同的HTTP元素

接下来，让我们为表单/user/**的URL定义配置，具有USER角色的普通用户可以使用表单身份验证访问这些URL：

```java

@Configuration
@Order(2)
public static class App2ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/user/**")
                .authorizeRequests().anyRequest().hasRole("ROLE")
                .and().formLogin().loginProcessingUrl("/user/login")
                .failureUrl("/userLogin?error=loginError").defaultSuccessUrl("/user/myUserPage")
                .and().logout().logoutUrl("/user/logout").logoutSuccessUrl("/multipleHttpLinks")
                .deleteCookies("JSESSIONID")
                .and().exceptionHandling()
                .defaultAuthenticationEntryPointFor(loginUrlAuthenticationEntryPointWithWarning(), new AntPathRequestMatcher("/user/private/**"))
                .defaultAuthenticationEntryPointFor(loginUrlAuthenticationEntryPoint(), new AntPathRequestMatcher("/user/general/**"))
                .accessDeniedPage("/403")
                .and().csrf().disable();
    }
}
```

正如我们所见，除了authenticationEntryPoint()方法之外，定义入口点的另一种方法是使用defaultAuthenticationEntryPointFor()方法。
这可以根据RequestMatcher对象定义匹配不同条件的多个入口点。

RequestMatcher接口的实现基于不同类型的条件，例如匹配路径、媒体类型或正则表达式。
在我们的示例中，我们使用AntPathRequestMatch为/user/private/**和/user/general/**形式的URL设置了两个不同的入口点。

接下来，我们需要在同一个静态配置类中定义入口点bean：

```java
public static class App2ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/userLogin");
    }

    @Bean
    public AuthenticationEntryPoint loginUrlAuthenticationEntryPointWithWarning() {
        return new LoginUrlAuthenticationEntryPoint("/userLoginWithWarning");
    }
}
```

这里的重点是如何设置这些多个入口点，而不一定是每个入口点的实现细节。

在这种情况下，入口点都是LoginUrlAuthenticationEntryPoint类型，
并使用不同的登录页面URL：/userLogin用于简单登录页面，使用/userLoginWithWarning用于登录页面，
该登录页面在尝试访问/user/private URL时会显示警告。

此配置还需要定义/userLogin和/userLoginWithWarning MVC映射以及带有标准登录表单的两个页面。

对于表单身份验证，非常重要的是要记住配置所需的任何URL，例如登录处理URL也需要遵循/user/**格式或以其他方式配置为可访问。

如果没有适当角色的用户尝试访问受保护的URL，上述两种配置都将重定向到/403 URL。

**需要小心的是为bean使用唯一名称，即使它们位于不同的静态类中，否则会出现覆盖情况**。

### 3.3 新的HTTP元素，没有入口点

最后，让我们为/guest/**形式的URL定义第三个配置，它将允许所有类型的用户，包括未经身份验证的用户：

```java

@Configuration
@Order(3)
public static class App3ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/guest/**").authorizeRequests().anyRequest().permitAll();
    }
}
```

### 3.4 XML配置

让我们看一下上一节中三个HttpSecurity实例的等效XML配置。

正如预期的那样，这将包含三个单独的<http\>块。

对于/admin/** URL，XML配置将使用http-basic标签的entry-point-ref属性：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <security:http pattern="/admin/**" use-expressions="true" auto-config="true">
        <security:intercept-url pattern="/**" access="hasRole('ROLE_ADMIN')"/>
        <security:http-basic entry-point-ref="authenticationEntryPoint"/>
        <security:access-denied-handler error-page="/403"/>
    </security:http>

    <bean id="authenticationEntryPoint"
          class="org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint">
        <property name="realmName" value="admin realm"/>
    </bean>
</beans>
```

**这里需要注意的是，如果使用XML配置，角色必须是ROLE_<ROLE_NAME>的格式**。

/user/** URL的配置必须在xml中分解为两个http块，因为没有直接等效于defaultAuthenticationEntryPointFor()的方法。

URL /user/general/**的配置为：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <security:http pattern="/user/general/**" use-expressions="true" auto-config="true"
                   entry-point-ref="loginUrlAuthenticationEntryPoint">
        <security:intercept-url pattern="/**" access="hasRole('ROLE_USER')"/>
        <security:form-login login-processing-url="/user/general/login"
                             authentication-failure-url="/userLogin?error=loginError"
                             default-target-url="/user/myUserPage"/>
        <security:csrf disabled="true"/>
        <security:access-denied-handler error-page="/403"/>
        <security:logout logout-url="/user/logout" delete-cookies="JSESSIONID" logout-success-url="/multipleHttpLinks"/>
    </security:http>

    <bean id="loginUrlAuthenticationEntryPoint"
          class="org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint">
        <constructor-arg name="loginFormUrl" value="/userLogin"/>
    </bean>
</beans>
```

对于/user/private/** URL，我们可以定义类似的配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <security:http pattern="/user/private/**" use-expressions="true" auto-config="true"
                   entry-point-ref="loginUrlAuthenticationEntryPointWithWarning">
        <security:intercept-url pattern="/**" access="hasRole('ROLE_USER')"/>
        <security:form-login login-processing-url="/user/private/login"
                             authentication-failure-url="/userLogin?error=loginError"
                             default-target-url="/user/myUserPage"/>
        <security:csrf disabled="true"/>
        <security:access-denied-handler error-page="/403"/>
        <security:logout logout-url="/user/logout" delete-cookies="JSESSIONID" logout-success-url="/multipleHttpLinks"/>
    </security:http>

    <bean id="loginUrlAuthenticationEntryPointWithWarning"
          class="org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint">
        <constructor-arg name="loginFormUrl" value="/userLoginWithWarning"/>
    </bean>
</beans>
```

对于/guest/** URL，我们配置以下http元素：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <security:http pattern="/**" use-expressions="true" auto-config="true">
        <security:intercept-url pattern="/guest/**" access="permitAll()"/>
    </security:http>
</beans>
```

同样重要的是，至少有一个XML <http\>块必须匹配/**模式。

## 4. 访问受保护的URL

### 4.1 MVC配置

接下来创建与我们保护的URL模式匹配的请求映射：

```java

@Controller
public class PagesController {

    @RequestMapping("/multipleHttpLinks")
    public String getMultipleHttpLinksPage() {
        return "multipleHttpElems/multipleHttpLinks";
    }

    @RequestMapping("/admin/myAdminPage")
    public String getAdminPage() {
        return "multipleHttpElems/myAdminPage";
    }

    @RequestMapping("/user/general/myUserPage")
    public String getUserPage() {
        return "multipleHttpElems/myUserPage";
    }

    @RequestMapping("/user/private/myPrivateUserPage")
    public String getPrivateUserPage() {
        return "multipleHttpElems/myPrivateUserPage";
    }

    @RequestMapping("/guest/myGuestPage")
    public String getGuestPage() {
        return "multipleHttpElems/myGuestPage";
    }

    @RequestMapping("/userLogin")
    public String getUserLoginPage() {
        return "multipleHttpElems/login";
    }

    @RequestMapping("/userLoginWithWarning")
    public String getUserLoginPageWithWarning() {
        return "multipleHttpElems/loginWithWarning";
    }

    @RequestMapping("/403")
    public String getAccessDeniedPage() {
        return "403";
    }
}
```

/multipleHttpLinks将返回一个简单的HTML页面，其中包含指向受保护URL的链接：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Multiple Http Elements Links</title>
</head>
<body>
<a th:href="@{/admin/myAdminPage}">Admin页面</a>
<br/>
<a th:href="@{/user/general/myUserPage}">User页面</a>
<br/>
<a th:href="@{/user/private/myPrivateUserPage}">Private User页面</a>
<br/>
<a th:href="@{/guest/myGuestPage}">Guest页面</a>
</body>
</html>
```

与受保护URL对应的每个HTML页面都有一个简单的文本和一个返回链接：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Admin Page</title>
</head>
<body>
Welcome admin!
<br/><br/>
<a th:href="@{/multipleHttpLinks}">Back to links</a>
</body>
</html>
```

### 4.2 初始化应用程序

我们将使用Spring Boot运行我们的代码，所以让我们使用main方法定义一个类：

```java

@SpringBootApplication
public class MultipleEntryPointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleEntryPointsApplication.class, args);
    }
}
```

如果我们想使用XML配置，我们还需要在我们的主类上添加@ImportResource({"classpath*:spring-security-multiple-entry.xml"})注解。

### 4.3 测试Security配置

让我们编写一个JUnit测试类，我们可以使用它来测试我们受保护的URL：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MultipleEntryPointsApplication.class)
@WebAppConfiguration
class MultipleEntryPointsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilter(filterChainProxy).build();
    }
}
```

接下来，让我们使用admin用户测试URL。

当在没有HTTP基本身份验证的情况下请求/admin/adminPage URL时，
我们应该会收到Unauthorized状态码，并且在完成身份验证后状态码应该是200。

如果尝试使用admin用户访问/user/userPage URL，我们应该收到302 Forbidden：

```java
class MultipleEntryPointsIntegrationTest {

    @Test
    void whenTestAdminCredentials_thenOk() throws Exception {
        mockMvc.perform(get("/admin/myAdminPage"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/admin/myAdminPage").with(httpBasic("admin", "adminPass")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/myUserPage").with(user("/admin").password("adminPass").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }
}
```

让我们使用常规用户凭据创建一个类似的测试来访问URL：

```java
class MultipleEntryPointsIntegrationTest {

    @Test
    void whenTestUserCredentials_thenOk() throws Exception {
        mockMvc.perform(get("/user/general/myUserPage"))
                .andExpect(status().isFound());

        mockMvc.perform(get("/user/general/myUserPage").with(user("user").password("userPass").roles("USER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/myAdminPage").with(user("user").password("userPass").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
```

在第二个测试中，我们可以看到缺少表单身份验证将导致状态为302 Found而不是Unauthorized，因为Spring Security将重定向到登录表单。

最后，让我们创建一个测试，其中我们访问/guest/guestPage URL将所有三种类型的身份验证并验证我们收到200 OK的状态码：

```java
class MultipleEntryPointsIntegrationTest {

    @Test
    void givenAnyUser_whenGetGuestPage_thenOk() throws Exception {
        mockMvc.perform(get("/guest/myGuestPage"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/guest/myGuestPage").with(user("user").password("userPass").roles("USER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/guest/myGuestPage").with(httpBasic("admin", "adminPass")))
                .andExpect(status().isOk());
    }
}
```

## 5. 总结

在本教程中，我们演示了如何在使用Spring Security时配置多个入口点。

案例的完整源代码可以在GitHub上找到。要运行应用程序，
请取消注释pom.xml中的MultipleEntryPointsApplication start-class标签并运行命令mvn spring-boot:run，
然后访问/multipleHttpLinks URL。

请注意，使用HTTP基本身份验证时无法注销，因此你必须关闭并重新打开浏览器才能删除此前的登录信息。

要运行JUnit测试，请使用已定义的Maven profile entryPoints和以下命令：

```shell
mvn clean install -PentryPoints
```
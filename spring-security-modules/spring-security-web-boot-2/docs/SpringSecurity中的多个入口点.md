## 1. 概述

在本快速教程中，我们将了解**如何在Spring Security应用程序中定义多个入口点**。

这主要需要在XML配置文件中定义多个http块，或者通过多次创建SecurityFilterChain bean来定义多个HttpSecurity实例。

## 2. Maven依赖

对于开发，我们需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-web</artifactId> 
    <version>2.7.2</version> 
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.7.2</version>
</dependency>    
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <version>5.4.0</version>
</dependency>
```

最新版本的[spring-boot-starter-security](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.4)、[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.4)、[spring-boot-starter-thymeleaf](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf/3.0.4)、[spring-boot-starter-test](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/3.0.4)、[spring-security-test](https://central.sonatype.com/artifact/org.springframework.security/spring-security-test/6.0.2)可以从Maven Central下载。

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

现在，让我们看看如何在安全配置中**定义多个入口点**。

我们将在这里使用一个由基本身份验证驱动的示例，我们将充分利用**Spring Security支持在我们的配置中定义多个HTTP元素**的事实。

使用Java配置时，定义多个安全域(realms)的方法是使用多个@Configuration类-每个类都有自己的安全配置。这些类可以是静态的并放置在主配置类中。

在一个应用程序中拥有多个入口点的主要动机是，如果有不同类型的用户可以访问应用程序的不同部分。

让我们定义一个具有三个入口点的配置，每个入口点具有不同的权限和身份验证模式：

+ 一个用于使用HTTP基本身份验证的管理员用户
+ 一个用于使用表单身份验证的普通用户
+ 一个用于不需要身份验证的来宾用户

为管理员用户定义的入口点保护任何匹配/admin/**的URL，以仅允许具有ADMIN角色的用户访问，并且需要使用authenticationEntryPoint()方法设置的入口点类型为BasicAuthenticationEntryPoint的HTTP基本身份验证：

```java
@Configuration
@Order(1)
public static class App1ConfigurationAdapter {

    @Bean
    public SecurityFilterChain filterChainApp1(HttpSecurity http) throws Exception {
        http.antMatcher("/admin/**")
              .authorizeRequests().anyRequest().hasRole("ADMIN")
              .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint());
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
```

每个静态类上的@Order注解指示将考虑配置以找到与请求的URL匹配的配置的顺序。**每个类的order值必须是唯一的**。

BasicAuthenticationEntryPoint类型的bean需要设置属性realName。

### 3.2 多个入口点，相同的HTTP元素

接下来，让我们为/user/**形式的URL定义配置，具有USER角色的普通用户可以使用表单身份验证访问这些URL：

```java
@Configuration
@Order(2)
public static class App2ConfigurationAdapter {

    @Bean
    public SecurityFilterChain filterChainApp2(HttpSecurity http) throws Exception {
        http.antMatcher("/user/**")
              .authorizeRequests().anyRequest().hasRole("USER")
              .and().formLogin().loginProcessingUrl("/user/login")
              .failureUrl("/userLogin?error=loginError").defaultSuccessUrl("/user/myUserPage")
              .and().logout().logoutUrl("/user/logout").logoutSuccessUrl("/multipleHttpLinks")
              .deleteCookies("JSESSIONID")
              .and().exceptionHandling()
              .defaultAuthenticationEntryPointFor(loginUrlauthenticationEntryPointWithWarning(),  new AntPathRequestMatcher("/user/private/**"))
              .defaultAuthenticationEntryPointFor(loginUrlauthenticationEntryPoint(), new AntPathRequestMatcher("/user/general/**"))
              .accessDeniedPage("/403")
              .and().csrf().disable();
        return http.build();
    }
}
```

正如我们所看到的，除了authenticationEntryPoint()方法之外，定义入口点的另一种方法是使用defaultAuthenticationEntryPointFor()方法。这可以根据RequestMatcher对象定义匹配不同条件的多个入口点。

RequestMatcher接口具有基于不同类型条件的实现，例如匹配路径、媒体类型或正则表达式。在我们的示例中，我们使用AntPathRequestMatch为/user/private/\*\*和/user/general/\*\*形式的URL设置了两个不同的入口点。

接下来，我们需要在同一个静态配置类中定义入口点bean：

```java
@Bean
public AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
    return new LoginUrlAuthenticationEntryPoint("/userLogin");
}

@Bean
public AuthenticationEntryPoint loginUrlAuthenticationEntryPointWithWarning() {
    return new LoginUrlAuthenticationEntryPoint("/userLoginWithWarning");
}
```

这里的重点是如何设置这些多个入口点-不一定是每个入口点的实现细节。

在这种情况下，入口点都是LoginUrlAuthenticationEntryPoint类型，并使用不同的登录页面URL：/userLogin用于简单登录页面，/userLoginWithWarning用于在尝试访问/user/私有URL时也会显示警告的登录页面。

此配置还需要定义/userLogin和/userLoginWithWarning MVC映射以及两个具有标准登录表单的页面。

对于表单身份验证，非常重要的是要记住配置所需的任何URL，例如登录处理URL也需要遵循/user/**格式或以其他方式配置为可访问。

如果没有适当角色的用户尝试访问受保护的URL，上述两种配置都将重定向到/403 URL。

**请注意为bean使用唯一的名称，即使它们位于不同的静态类中，否则会出现覆盖情况**。

### 3.3 新的HTTP元素，没有入口点

最后，让我们为/guest/**形式的URL定义第三个配置，该配置将允许所有类型的用户，包括未经身份验证的用户：

```java
@Configuration
@Order(3)
public static class App3ConfigurationAdapter {

    public SecurityFilterChain filterChainApp3(HttpSecurity http) throws Exception {
        http.antMatcher("/guest/**").authorizeRequests().anyRequest().permitAll();
        return http.build();
    }
}
```

### 3.4 XML配置

让我们看一下上一节中三个HttpSecurity实例的等效XML配置。

正如预期的那样，这将包含三个单独的<http\>块。

对于/admin/** URL，XML配置将使用http-basic元素的entry-point-ref属性：

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

**这里需要注意的是，如果使用XML配置，角色必须采用ROLE_<ROLE_NAME>的形式**。

/user/** URL的配置必须在xml中分解为两个http块，因为没有直接等效于defaultAuthenticationEntryPointFor()的方法。

URL /user/general/**的配置为：

```xml
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
```

对于/user/private/** URL，我们可以定义类似的配置：

```xml
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
```

对于/guest/** URL，我们配置以下http元素：

```xml
<security:http pattern="/**" use-expressions="true" auto-config="true">
    <security:intercept-url pattern="/guest/**" access="permitAll()"/>
</security:http>
```

这里同样重要的是，至少有一个XML <http\>块必须匹配/**模式。

## 4. 访问受保护的URL

### 4.1 MVC配置

让我们创建与我们保护的URL模式相匹配的请求映射：

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
}
```

/multipleHttpLinks映射将返回一个简单的HTML页面，其中包含指向受保护URL的链接：

```html
<a th:href="@{/admin/myAdminPage}">Admin page</a>
<a th:href="@{/user/general/myUserPage}">User page</a>
<a th:href="@{/user/private/myPrivateUserPage}">Private user page</a>
<a th:href="@{/guest/myGuestPage}">Guest page</a>
```

每个与受保护URL对应的HTML页面都将有一个简单的文本和一个返回链接：

```html
Welcome admin!

<a th:href="@{/multipleHttpLinks}" >Back to links</a>
```

### 4.2 初始化应用程序

我们将示例作为Spring Boot应用程序运行，所以让我们用main方法定义一个类：

```java
@SpringBootApplication
public class MultipleEntryPointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleEntryPointsApplication.class, args);
    }
}
```

如果我们想使用XML配置，我们还需要将@ImportResource({"classpath*:spring-security-multiple-entry.xml"})注解添加到主类上。

### 4.3 测试安全配置

让我们设置一个可用于测试受保护URL的JUnit测试类：

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

接下来，让我们使用管理员用户测试URL。

在没有HTTP基本身份验证的情况下请求/admin/adminPage URL时，我们应该会收到Unauthorized状态码，并且在完成身份验证后状态码应该是200 OK。

如果尝试使用admin用户访问/user/userPage URL，我们应该收到302 Forbidden：

```java
@Test
void whenTestAdminCredentials_thenOk() throws Exception {
    mockMvc.perform(get("/admin/myAdminPage"))
          .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/admin/myAdminPage").with(httpBasic("admin", "adminPass")))
          .andExpect(status().isOk());

    mockMvc.perform(get("/user/myUserPage").with(user("/admin").password("adminPass").roles("ADMIN")))
          .andExpect(status().isForbidden());
}
```

让我们使用常规用户凭据创建一个类似的测试来访问URL：

```java
@Test
void whenTestUserCredentials_thenOk() throws Exception {
    mockMvc.perform(get("/user/general/myUserPage"))
          .andExpect(status().isFound());

    mockMvc.perform(get("/user/general/myUserPage").with(user("user").password("userPass").roles("USER")))
          .andExpect(status().isOk());

    mockMvc.perform(get("/admin/myAdminPage").with(user("user").password("userPass").roles("USER")))
          .andExpect(status().isForbidden());
}
```

在第二个测试中，我们可以看到缺少表单身份验证将导致状态为302 Found而不是Unauthorized，因为Spring Security将重定向到登录表单。

最后，让我们创建一个测试，在其中我们访问/guest/guestPage URL将进行所有三种类型的身份验证并验证我们收到200 OK状态码：

```java
@Test
void givenAnyUser_whenGetGuestPage_thenOk() throws Exception {
    mockMvc.perform(get("/guest/myGuestPage"))
          .andExpect(status().isOk());

    mockMvc.perform(get("/guest/myGuestPage").with(user("user").password("userPass").roles("USER")))
          .andExpect(status().isOk());

    mockMvc.perform(get("/guest/myGuestPage").with(httpBasic("admin", "adminPass")))
          .andExpect(status().isOk());
}
```

## 5. 总结

在本教程中，我们演示了如何在使用Spring Security时配置多个入口点。

示例的完整源代码可以在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-security-modules/spring-security-web-boot-2)找到。要运行应用程序，请取消注释pom.xml中的MultipleEntryPointsApplication start-class标签并运行命令mvn spring-boot:run，然后访问/multipleHttpLinks URL。

请注意，使用HTTP基本身份验证时无法注销，因此你必须关闭并重新打开浏览器才能删除此前的登录信息。

要运行JUnit测试，请使用已定义的Maven Profile entryPoints和以下命令：

```shell
mvn clean install -PentryPoints
```
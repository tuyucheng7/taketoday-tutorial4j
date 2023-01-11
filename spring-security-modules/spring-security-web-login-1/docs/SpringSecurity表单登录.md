## 1. 概述

本教程将重点介绍使用Spring Security实现登录。

## 2. Maven依赖

使用Spring Boot时，spring-boot-starter-security包含所有Spring Security相关的依赖项，
例如spring-security-core、spring-security-web和spring-security-config等：

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. Spring Security Java配置

让我们首先创建一个继承WebSecurityConfigurerAdapter的Spring Security配置类，在该类中配置一个SecurityFilterChain bean。

通过添加@EnableWebSecurity，我们开启Spring Security和MVC集成支持：

```java

@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // InMemoryUserDetailsManager (see below)
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http builder configurations for authorize requests and form login (see below)
    }
}
```

在这个示例中，我们使用内存方式的身份认证并定义了三个用户。

接下来，我们将介绍用于创建表单登录配置的元素。

### 3.1 Authentication Manager

Authentication Provider由一个简单的内存实现InMemoryUserDetailsManager支持，这对于快速编写示例代码非常有用：

```java
public class SecSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder().encode("user1Pass"))
                .roles("USER")
                .build();
        UserDetails user2 = User.withUsername("user2")
                .password(passwordEncoder().encode("user2Pass"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("adminPass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }
}
```

在这里，我们使用硬编码的用户名、密码和角色配置三个了用户。

**从Spring 5开始，我们还必须定义一个密码编码器**。在我们的示例中，我们将使用BCryptPasswordEncoder：

```java
public class SecSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

接下来让我们配置HttpSecurity。

### 3.2 授权请求的配置

我们首先对授权请求进行必要的配置。在这里，我们允许匿名访问/login，以便用户可以进行身份验证。
我们将/admin限制为仅ADMIN角色可访问并保护其他所有内容：

```text

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/admin/**")
            .hasRole("ADMIN")
            .antMatchers("/anonymous*")
            .anonymous()
            .antMatchers("/login*")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            // ...
}
```

请注意，antMatchers()调用的顺序很重要；**更具体的规则需要首先定义，然后定义更一般的规则**。

### 3.3 表单登录配置

接下来我们将扩展上述配置以进行表单登录和注销：

```text
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            // ...
            .and()
            .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/homepage.html", true)
            .failureUrl("/login.html?error=true")
            .failureHandler(authenticationFailureHandler())
            .and()
            .logout()
            .logoutUrl("/perform_logout")
            .deleteCookies("JSESSIONID")
            .logoutSuccessHandler(logoutSuccessHandler());
    return http.build();
}
```

+ loginPage() - 自定义登录页面
+ loginProcessingUrl() - 提交用户名和密码的URL
+ defaultSuccessUrl() - 登录成功后的页面
+ failureUrl() - 登录失败后的页面
+ logoutUrl() - 自定义注销URL

## 4. 将Spring Security添加到Web应用程序

要使用上面定义的Spring Security配置，我们需要将其附加到Web应用程序。

我们将使用WebApplicationInitializer，因此不需要提供任何web.xml文件：

```java
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) {
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(SecSecurityConfig.class);
        sc.addListener(new ContextLoaderListener(root));
        sc.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain")).addMappingForUrlPatterns(null, false, "/*");
    }
}
```

请注意，如果我们使用Spring Boot应用程序，则不需要此AppInitializer类。

## 5. Spring Security XML配置

整个项目使用的是Java配置，因此我们需要通过@ImportResource注解来导入XML配置文件：

```java

@Configuration
@ImportResource({"classpath:webSecurityConfig.xml"})
public class SecSecurityConfig {
    public SecSecurityConfig() {
        super();
    }
}
```

下面是Spring Security XML配置文件webSecurityConfig.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xsi:schemaLocation="
		http://www.springframework.org/schema/security 
        https://www.springframework.org/schema/security/spring-security.xsd
		http://www.springframework.org/schema/beans 
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <http use-expressions="true">
        <intercept-url pattern="/admin/**" access="hasRole('ROLE_ADMIN')"/>
        <intercept-url pattern="/anonymous*" access="isAnonymous()"/>
        <intercept-url pattern="/login*" access="permitAll"/>
        <intercept-url pattern="/**" access="isAuthenticated()"/>

        <csrf disabled="true"/>

        <form-login login-page='/login.html' login-processing-url="/perform_login" default-target-url="/homepage.html"
                    always-use-default-target="true" authentication-failure-handler-ref="authenticationFailureHandler"/>

        <logout logout-url="/perform_logout" delete-cookies="JSESSIONID"
                success-handler-ref="customLogoutSuccessHandler"/>

        <!-- <access-denied-handler error-page="/accessDenied"/> -->
        <access-denied-handler ref="customAccessDeniedHandler"/>
    </http>

    <beans:bean id="encoder" class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"/>

    <authentication-manager>
        <authentication-provider>
            <user-service>
                <user name="user1" password="user1Pass" authorities="ROLE_USER"/>
                <user name="user2" password="user2Pass" authorities="ROLE_USER"/>
                <user name="admin" password="adminPass" authorities="ROLE_ADMIN"/>
            </user-service>
        </authentication-provider>
    </authentication-manager>
</beans:beans>
```

## 6. web.xml

在Spring 4之前，我们习惯于在web.xml中配置Spring Security；仅向标准web.xml中添加了一个附加过滤器：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- Spring Security -->
    <filter>
        <filter-name>springSecurityFilterChain</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>springSecurityFilterChain</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

过滤器DelegatingFilterProxy简单地委托给一个Spring管理的FilterChainProxy bean，它本身能够从Spring bean的全生命周期管理中受益。

## 7. 登录表单

登录表单页面将使用Spring MVC注册，使用简单的机制将视图名称映射到URL。这样，两者之间不需要显式的定义一个控制器：

```text
registry.addViewController("/login.jsp");
```

当然，这对应于login.jsp：

```html

<html>
<head></head>

<body>
<h1>Login</h1>
<form name='f' action="perform_login" method='POST'>
    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='username' value=''></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password'/></td>
        </tr>
        <tr>
            <td><input name="submit" type="submit" value="submit"/></td>
        </tr>
    </table>
</form>
</body>
</html>
```

+ /login – 提交表单以触发身份验证过程的URL
+ username - 用户名
+ password - 密码

## 8. Spring登录的更多配置

在上面介绍的Spring Security配置中，我们简要讨论了登录机制的一些配置，现在让我们更详细地介绍一下。

覆盖Spring Security中大多数默认值的一个原因是**隐藏应用程序是由Spring Security保护**的事实。
我们还希望尽可能减少潜在攻击者知道的有关应用程序的信息。

因此，HttpSecurity的最终配置如下所示：

```text

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.
            // ...
            .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/homepage.html", true)
            .failureUrl("/login.html?error=true");
    return http.build();
}
```

或相应的XML配置：

```xml

<form-login
        login-page='/login.html'
        login-processing-url="/perform_login"
        default-target-url="/homepage.html"
        authentication-failure-url="/login.html?error=true"
        always-use-default-target="true"/>
```

### 8.1 登录页面

接下来，我们将使用loginPage()方法配置自定义登录页面：

```text
http.formLogin().loginPage("/login.html")
```

同样，我们可以使用XML配置：

```text
login-page='/login.html'
```

如果我们不指定这一点，Spring Security会默认生成一个URL为/login的基本登录表单。

### 8.2 登录的POST URL

Spring将触发登录身份验证过程的默认URL是/login，在Spring Security 4之前是/j_spring_security_check。

我们可以使用loginProcessingUrl方法来覆盖这个URL：

```text
http.formLogin().loginProcessingUrl("/perform_login")
```

也可以使用XML配置：

```text
login-processing-url="/perform_login"
```

通过覆盖这个默认URL，我们隐藏了应用程序实际上是由Spring Security保护的。

### 8.3 登录成功后的页面

成功登录后，我们将被重定向到默认为Web应用程序根路径的页面。

我们可以通过defaultSuccessUrl()方法覆盖它：

```text
http.formLogin().defaultSuccessUrl("/homepage.html")
```

或者使用XML配置：

```text
default-target-url="/homepage.html"
```

如果always-use-default-target属性设置为true，则用户始终被重定向到此页面。
如果该属性设置为false，那么在提示用户进行身份验证之前，用户将被重定向到他们想要访问的上一个页面。

### 8.4 登录失败后的页面

与登录页面类似，Spring Security给我们提供的默认登录失败页面为/login?error。

要覆盖它，我们可以使用failureUrl()方法：

```text
http.formLogin().failureUrl("/login.html?error=true")
```

或使用XML：

```text
authentication-failure-url="/login.html?error=true"
```

## 9. 总结

在这个Spring Security登录案例中，我们配置了一个简单的身份验证过程。我们还介绍了Spring Security登录表单、安全配置以及一些更高级的自定义方式。


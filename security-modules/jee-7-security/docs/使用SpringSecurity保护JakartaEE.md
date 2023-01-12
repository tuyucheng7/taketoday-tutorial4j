## 1. 概述

在本快速教程中，我们将了解如何使用Spring Security 保护 Jakarta EE Web 应用程序。

## 2.Maven依赖

让我们从本教程所需的[Spring Security 依赖项](https://www.baeldung.com/spring-security-with-maven)开始：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.7.5</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.7.5</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
    <version>5.7.5</version>
</dependency>
```

最新的 Spring Security 版本(在撰写本教程时)是 5.7.5；与往常一样，我们可以检查[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.security")以获取最新版本。

## 3.安全配置

接下来，我们需要为现有的 Jakarta EE 应用程序设置安全配置：

```java
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user1")
            .password("{noop}user1Pass")
            .roles("USER")
            .build();
        UserDetails admin = User.withUsername("admin")
            .password("{noop}adminPass")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

为了简单起见，我们实现了一个简单的内存中身份验证。用户详细信息是硬编码的。

这意味着在不需要完整的持久性机制时用于快速原型制作。

接下来，让我们通过添加SecurityWebApplicationInitializer类将安全性集成到现有系统中：

```java
public class SecurityWebApplicationInitializer
  extends AbstractSecurityWebApplicationInitializer {

    public SecurityWebApplicationInitializer() {
        super(SpringSecurityConfig.class);
    }
}
```

此类将确保在应用程序启动期间加载SpringSecurityConfig。在这个阶段，我们已经实现了 Spring Security 的基本实现。通过此实现，Spring Security 将默认要求对所有请求和路由进行身份验证。

## 4.配置安全规则

我们可以通过创建一个SecurityFilterChain bean 来进一步自定义 Spring Security：

```java
 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http.csrf()
         .disable()
         .authorizeRequests()
         .antMatchers("/auth/login")
         .anonymous()
         .antMatchers("/home/admin")
         .hasRole("ADMIN")
         .anyRequest()
         .authenticated()
         .and()
         .formLogin()
         .loginPage("/auth/login")
         .defaultSuccessUrl("/home", true)
         .failureUrl("/auth/login?error=true")
         .and()
         .logout()
         .logoutSuccessUrl("/auth/login");
     return http.build();
 }
```

使用antMatchers()方法，我们将 Spring Security 配置为允许匿名访问/auth/login并 验证任何其他请求。

### 4.1. 自定义登录页面

使用formLogin()方法配置自定义登录页面：

```java
http.formLogin()
  .loginPage("/auth/login")
```

如果未指定，Spring Security 会在/login生成默认登录页面：

```html
<html>
<head></head>
<body>
<h1>Login</h1>
<form name='f' action="/auth/login" method='POST'>
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
            <td><input name="submit" type="submit" 
              value="submit"/></td>
        </tr>
    </table>
</form>
</body>
</html>
```

### 4.2. 自定义着陆页

成功登录后，Spring Security 将用户重定向到应用程序的根目录。我们可以通过指定默认的成功 URL 来覆盖它：

```java
http.formLogin()
  .defaultSuccessUrl("/home", true)
```

通过将defaultSuccessUrl()方法的alwaysUse参数设置为 true，用户将始终被重定向到指定页面。

如果alwaysUse参数未设置或设置为 false，用户将被重定向到他尝试访问的上一个页面，然后才会提示进行身份验证。

同样，我们也可以指定一个自定义的失败登陆页面：

```java
http.formLogin()
  .failureUrl("/auth/login?error=true")
```

### 4.3. 授权

我们可以按角色限制对资源的访问：

```java
http.formLogin()
  .antMatchers("/home/admin").hasRole("ADMIN")
```

如果非管理员用户尝试访问/home/admin端点，他/她将收到拒绝访问错误。

我们还可以根据用户角色限制 JSP 页面上的数据。这是使用<security:authorize>标签完成的：

```html
<security:authorize access="hasRole('ADMIN')">
    This text is only visible to an admin
    <br/>
    <a href="<c:url value="/home/admin" />">Admin Page</a>
    <br/>
</security:authorize>
```

要使用这个标签，我们必须在页面顶部包含 Spring Security 标签 taglib：

```html
<%@ taglib prefix="security" 
  uri="http://www.springframework.org/security/tags" %>
```

## 5.Spring Security XML配置

到目前为止，我们已经了解了在Java中配置 Spring Security。让我们看一下等效的 XML 配置。

首先，我们需要在包含我们的 XML 配置的web/WEB-INF/spring文件夹中创建一个security.xml文件。本文末尾提供了此类security.xml配置文件的示例。

让我们从配置身份验证管理器和身份验证提供程序开始。为了简单起见，我们使用简单的硬编码用户凭证：

```xml
<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user" 
              password="user123" 
              authorities="ROLE_USER" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

我们刚刚所做的是创建一个具有用户名、密码和角色的用户。

或者，我们可以使用密码编码器配置我们的身份验证提供程序：

```xml
<authentication-manager>
    <authentication-provider>
        <password-encoder hash="sha"/>
        <user-service>
            <user name="user"
              password="d7e6351eaa13189a5a3641bab846c8e8c69ba39f" 
              authorities="ROLE_USER" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

我们还可以指定 Spring 的UserDetailsService或数据源的自定义实现作为我们的身份验证提供程序。可以[在此处找到更多详细信息。](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/ns-config.html)

现在我们已经配置了身份验证管理器，让我们设置安全规则并应用访问控制：

```xml
<http auto-config='true' use-expressions="true">
    <form-login default-target-url="/secure.jsp" />
    <intercept-url pattern="/" access="isAnonymous()" />
    <intercept-url pattern="/index.jsp" access="isAnonymous()" />
    <intercept-url pattern="/secure.jsp" access="hasRole('ROLE_USER')" />
</http>
```

在上面的代码片段中，我们将HttpSecurity配置为使用表单登录，并将/secure.jsp设置为登录成功 URL。我们授予匿名访问/index.jsp和“/”路径。此外，我们指定访问/secure.jsp应该需要身份验证，并且经过身份验证的用户至少应该具有ROLE_USER级别的权限。

将http标记的auto-config属性设置为true指示 Spring Security 实现我们不必在配置中覆盖的默认行为。因此，/login和/logout将分别用于用户登录和注销。还提供了默认登录页面。

我们可以使用自定义登录和注销页面、URL 来进一步自定义表单登录标记，以处理身份验证失败和成功。[安全命名空间附录](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/appendix-namespace.html)列出了表单登录(和其他)标签的所有可能属性。某些 IDE 还可以通过在按住ctrl键的同时单击标签来进行检查。

最后，为了在应用程序启动期间加载security.xml配置，我们需要将以下定义添加到我们的web.xml中：

```xml
<context-param>                                                                           
    <param-name>contextConfigLocation</param-name>                                        
    <param-value>                                                                         
      /WEB-INF/spring/.xml                                                             
    </param-value>                                                                        
</context-param>                                                                          
                                                                                          
<filter>                                                                                  
    <filter-name>springSecurityFilterChain</filter-name>                                  
    <filter-class>
      org.springframework.web.filter.DelegatingFilterProxy</filter-class>     
</filter>                                                                                 
                                                                                          
<filter-mapping>                                                                          
    <filter-name>springSecurityFilterChain</filter-name>                                  
    <url-pattern>/</url-pattern>                                                         
</filter-mapping>                                                                         
                                                                                          
<listener>                                                                                
    <listener-class>
        org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>
```

请注意，尝试在同一个 JEE 应用程序中同时使用基于 XML 和Java的配置可能会导致错误。

## 六. 总结

在本文中，我们了解了如何使用 Spring Security 保护 Jakarta EE 应用程序，并演示了基于Java和基于 XML 的配置。

我们还讨论了根据用户角色授予或撤销对特定资源的访问权限的方法。
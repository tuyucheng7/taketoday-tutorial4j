## 1. 简介

在本教程中，我们将重点介绍 Spring Security 表达式和使用这些表达式的实际示例。

在查看更复杂的实现(例如 ACL)之前，重要的是要牢牢掌握安全表达式，因为如果使用得当，它们会非常灵活和强大。

## 2.Maven依赖

为了使用 Spring Security，我们需要在pom.xml文件中包含以下部分：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-web</artifactId>
        <version>5.6.0</version>
   </dependency>
</dependencies>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"spring-security-web")找到。

请注意，此依赖项仅涵盖 Spring Security；我们需要为完整的 Web 应用程序添加 s pring-core和spring-context。

## 3.配置

首先，我们来看一个Java配置：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("com.baeldung.security")
public class SecurityJavaConfig {
    ...
}
```

当然，我们也可以进行 XML 配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans ...>
    <global-method-security pre-post-annotations="enabled"/>
</beans:beans>
```

## 4. 网络安全表达式

现在让我们探索安全表达式：

-   hasRole , hasAnyRole
-   hasAuthority , hasAnyAuthority
-   全部允许，全部拒绝
-   isAnonymous , isRememberMe , isAuthenticated , isFullyAuthenticated
-   主体,认证
-   有权限

### 4.1。hasRole, hasAnyRole

这些表达式负责定义对我们应用程序中特定 URL 和方法的访问控制或授权：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    ...
    .antMatchers("/auth/admin/").hasRole("ADMIN")
    .antMatchers("/auth/").hasAnyRole("ADMIN","USER")
    ...
}

```

在上面的示例中，我们指定了对以/auth/开头的所有链接的访问权限，将它们限制为使用角色USER或角色ADMIN登录的用户。此外，要访问以/auth/admin/开头的链接，我们需要在系统中具有ADMIN角色。

我们可以通过编写以下方式在 XML 文件中实现相同的配置：

```xml
<http>
    <intercept-url pattern="/auth/admin/" access="hasRole('ADMIN')"/>
    <intercept-url pattern="/auth/" access="hasAnyRole('ADMIN','USER')"/>
</http>

```

### 4.2. hasAuthority, hasAnyAuthority

Spring 中的角色和权限是相似的。

主要区别在于角色具有特殊的语义。从 Spring Security 4 开始，任何与角色相关的方法都会自动添加“ ROLE_ ”前缀(如果还没有的话)。

所以hasAuthority('ROLE_ADMIN')类似于hasRole('ADMIN')因为' ROLE_ '前缀是自动添加的。

使用权限的好处是我们根本不必使用ROLE_前缀。

这是一个定义具有特定权限的用户的快速示例：

```java
@Bean
public InMemoryUserDetailsManager userDetailsService() {
    UserDetails admin = User.withUsername("admin")
        .password(encoder().encode("adminPass"))
        .roles("ADMIN")
        .build();
    UserDetails user = User.withUsername("user")
        .password(encoder().encode("userPass"))
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(admin, user);
}
```

然后我们可以使用这些权威表达：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    ...
    .antMatchers("/auth/admin/").hasAuthority("ADMIN")
    .antMatchers("/auth/").hasAnyAuthority("ADMIN", "USER")
    ...
}
```

正如我们所看到的，我们根本没有在这里提到角色。

此外，从 Spring 5 开始，我们需要一个[PasswordEncoder](https://www.baeldung.com/spring-security-5-default-password-encoder) bean：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

最后，我们还可以选择使用 XML 配置实现相同的功能：

```xml
<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user1" password="user1Pass" authorities="ROLE_USER"/>
            <user name="admin" password="adminPass" authorities="ROLE_ADMIN"/>
        </user-service>
    </authentication-provider>
</authentication-manager>
<bean name="passwordEncoder" 
  class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"/>
```

和：

```xml
<http>
    <intercept-url pattern="/auth/admin/" access="hasAuthority('ADMIN')"/>
    <intercept-url pattern="/auth/" access="hasAnyAuthority('ADMIN','USER')"/>
</http>
```

### 4.3. 全部允许，全部拒绝

这两个注解也很简单。我们可能允许或拒绝访问我们服务中的某些 URL。

让我们看一下这个例子：

```java
...
.antMatchers("/").permitAll()
...
```

使用此配置，我们将授权所有用户(包括匿名用户和登录用户)访问以“/”开头的页面(例如，我们的主页)。

我们还可以拒绝访问我们的整个 URL 空间：

```java
...
.antMatchers("/").denyAll()
...
```

同样，我们也可以对 XML 进行相同的配置：

```xml
<http auto-config="true" use-expressions="true">
    <intercept-url access="permitAll" pattern="/" /> <!-- Choose only one -->
    <intercept-url access="denyAll" pattern="/" /> <!-- Choose only one -->
</http>
```

### 4.4. isAnonymous, isRememberMe, isAuthenticated, isFullyAuthenticated

在本小节中，我们将重点关注与用户登录状态相关的表达式。让我们从一个没有登录到我们页面的用户开始。通过在Java配置中指定以下内容，我们将允许所有未经授权的用户访问我们的主页：

```java
...
.antMatchers("/").anonymous()
...
```

在 XML 配置中也是如此：

```xml
<http>
    <intercept-url pattern="/" access="isAnonymous()"/>
</http>
```

如果我们想保护网站，以便使用它的每个人都需要登录，我们需要使用isAuthenticated()方法：

```java
...
.antMatchers("/").authenticated()
...
```

或者我们可以使用 XML 版本：

```xml
<http>
    <intercept-url pattern="/" access="isAuthenticated()"/>
</http>
```

我们还有两个额外的表达式，isRememberMe()和isFullyAuthenticated()。通过使用 cookie，Spring 启用了 remember-me 功能，因此无需每次都登录系统。我们可以[在这里阅读](https://www.baeldung.com/spring-security-remember-me)[更多关于记住我](https://www.baeldung.com/spring-security-remember-me)的信息。

为了向通过记住我功能登录的用户授予访问权限，我们可以使用：

```java
...
.antMatchers("/").rememberMe()
...
```

我们也可以使用 XML 版本：

```xml
<http>
    <intercept-url pattern="" access="isRememberMe()"/>
</http>
```

最后，我们服务的某些部分要求用户再次进行身份验证，即使用户已经登录。例如，假设用户想要更改设置或支付信息；在系统的更敏感区域请求手动身份验证是一种很好的做法。

为此，我们可以指定isFullyAuthenticated() ，如果用户不是匿名用户或记住我的用户，则返回true ：

```java
...
.antMatchers("/").fullyAuthenticated()
...
```

这是 XML 版本：

```xml
<http>
    <intercept-url pattern="" access="isFullyAuthenticated()"/>
</http>
```

### 4.5. 主体，身份验证

这些表达式允许我们分别从SecurityContext访问代表当前授权(或匿名)用户和当前Authentication对象的主体对象。

例如，我们可以使用principal来加载用户的电子邮件、头像或任何其他可从登录用户访问的数据。

身份验证提供有关完整身份验证对象及其授予权限的信息。

[在 Spring Security 中检索用户信息](https://www.baeldung.com/get-user-in-spring-security)一文中更详细地描述了这两个表达式。

### 4.6. hasPermission API

此表达式已[记录在案](https://docs.spring.io/spring-security/site/docs/5.1.5.RELEASE/reference/html/authorization.html)，旨在成为表达式系统和 Spring Security 的 ACL 系统之间的桥梁，允许我们基于抽象权限指定单个域对象的授权约束。

让我们看一个例子。想象一下，我们有一项允许合作撰写文章的服务，由一位主编辑决定应该发表作者提出的哪些文章。

为了允许使用这样的服务，我们可以使用访问控制方法创建以下方法：

```java
@PreAuthorize("hasPermission(#articleId, 'isEditor')")
public void acceptArticle(Article article) {
   …
}
```

只有授权用户才能调用该方法，并且需要在服务中拥有isEditor权限。

我们还需要记住在应用程序上下文中显式配置PermissionEvaluator ，其中customInterfaceImplementation将是实现PermissionEvaluator的类：

```xml
<global-method-security pre-post-annotations="enabled">
    <expression-handler ref="expressionHandler"/>
</global-method-security>

<bean id="expressionHandler"
    class="org.springframework.security.access.expression
      .method.DefaultMethodSecurityExpressionHandler">
    <property name="permissionEvaluator" ref="customInterfaceImplementation"/>
</bean>
```

当然，我们也可以通过Java配置来做到这一点：

```java
@Override
protected MethodSecurityExpressionHandler expressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = 
      new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(new CustomInterfaceImplementation());
    return expressionHandler;
}
```

## 5. 总结

本文是对 Spring Security Expressions 的全面介绍和指南。
## 1. 概述

简单来说，Spring Security支持方法级别的授权语义。

通常，我们可以通过限制哪些角色能够执行特定方法来保护我们的服务层，并使用专用的方法级安全测试支持对其进行测试。

在本教程中，我们将回顾一些Security注解的使用。然后我们将专注于使用不同的策略测试我们方法安全性。

## 延伸阅读

### [Spring表达式语言指南](https://www.baeldung.com/spring-expression-language)

本文探讨了Spring表达式语言(SpEL)，这是一种强大的表达式语言，支持在运行时查询和操作对象图。

[阅读更多](https://www.baeldung.com/spring-expression-language)→

### [使用Spring Security的自定义安全表达式](https://www.baeldung.com/spring-security-create-new-custom-security-expression)

使用Spring Security创建新的自定义安全表达式，然后将新表达式与Pre和Post授权注解一起使用的指南。

[阅读更多](https://www.baeldung.com/spring-security-create-new-custom-security-expression)→

## 2. 启用方法级安全

首先，要使用Spring方法安全性，我们需要添加spring-security-config依赖：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>
```

我们可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.security/spring-security-config/6.0.2)上找到它的最新版本。

如果我们想使用Spring Boot，我们可以使用spring-boot-starter-security依赖项，其中包括spring-security-config：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

同样，最新版本可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.3)上找到。

**接下来，我们需要启用全局方法安全性**：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
}
```

+ prePostEnabled属性启用Spring Security pre/post注解
+ secureEnabled属性确定是否应启用@Secured注解
+ jsr250Enabled属性允许我们使用@RoleAllowed注解

我们将在下一节中探讨有关这些注解的更多信息。

## 3. 应用方法安全

### 3.1 使用@Secured注解

**@Secured注解用于指定方法上的角色列表**。因此，仅当用户至少具有一个指定角色时，她才能访问该方法。

让我们定义一个getUsername方法：

```java
@Secured("ROLE_VIEWER")
public String getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return securityContext.getAuthentication().getName();
}
```

这里的@Secured("ROLE_VIEWER")注解定义了只有拥有ROLE_VIEWER角色的用户才能执行getUsername方法。

此外，我们可以在@Secured注解中定义角色列表：

```java
@Secured({"ROLE_VIEWER", "ROLE_EDITOR"})
public boolean isValidUsername(String username) {
    return userRoleRepository.isValidUsername(username);
}
```

在这种情况下，配置指出如果用户具有ROLE_VIEWER或ROLE_EDITOR角色，则该用户可以调用isValidUsername方法。

**@Secured注解不支持Spring表达式语言(SpEL)**。

### 3.2 使用@RolesAllowed注解

**@RolesAllowed注解是JSR-250中与@Secured注解等效的注解**。

基本上，我们可以以与@Secured类似的方式使用@RolesAllowed注解。

这样，我们可以重新定义getUsername和isValidUsername方法以使用@RolesAllowed注解：

```java
@RolesAllowed("ROLE_VIEWER")
public String getUsername2() {
    // ...
}
    
@RolesAllowed({ "ROLE_VIEWER", "ROLE_EDITOR" })
public boolean isValidUsername2(String username) {
    // ...
}
```

同样，只有具有角色ROLE_VIEWER的用户才能执行getUsername2。

同样，只有当用户至少具有ROLE_VIEWER或ROLE_EDITOR其中一种角色时，他才能调用isValidUsername2。

### 3.3 使用@PreAuthorize和@PostAuthorize注解

**@PreAuthorize和@PostAuthorize注解都提供基于表达式的访问控制**。因此，可以使用SpEL编写谓词。

**@PreAuthorize注解在进入方法之前检查给定的表达式，而@PostAuthorize注解在方法执行后验证它，并可能改变结果**。

现在让我们声明一个getUsernameInUpperCase方法，如下所示：

```java
@PreAuthorize("hasRole('ROLE_VIEWER')")
public String getUsernameInUpperCase() {
    return getUsername().toUpperCase();
}
```

@PreAuthorize("hasRole('ROLE_VIEWER')")与我们在上一节中使用的@Secured("ROLE_VIEWER")具有相同的含义。请随时在以前的文章中发现更多[安全表达式详细信息](https://www.baeldung.com/spring-security-expressions)。

因此，注解@Secured({"ROLE_VIEWER","ROLE_EDITOR"})可以替换为@PreAuthorize("hasRole('ROLE_VIEWER') or hasRole('ROLE_EDITOR')")：

```java
@PreAuthorize("hasRole('ROLE_VIEWER') or hasRole('ROLE_EDITOR')")
public boolean isValidUsername3(String username) {
    // ...
}
```

此外，**我们实际上可以将方法参数用作表达式的一部分**：

```java
@PreAuthorize("#username == authentication.principal.username")
public String getMyRoles(String username) {
    // ...
}
```

在这里，只有当参数username的值与当前主体的用户名相同时，用户才能调用getMyRoles方法。

**值得注意的是，@PreAuthorize表达式可以替换为@PostAuthorize表达式**。

让我们重写getMyRoles：

```java
@PostAuthorize("#username == authentication.principal.username")
public String getMyRoles2(String username) {
    // ...
}
```

但是，在前面的示例中，授权会在执行目标方法后延迟。

此外，**@PostAuthorize注解提供了访问方法返回结果的能力**：

```java
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser loadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```

在这里，只有当返回的CustomUser的username等于当前身份验证主体的nickName时，loadUserDetail方法才会成功执行。

在本节中，我们主要使用简单的Spring表达式。对于更复杂的场景，我们可以创建[自定义安全表达式](https://www.baeldung.com/spring-security-create-new-custom-security-expression)。

### 3.4 使用@PreFilter和@PostFilter注解

**Spring Security提供了@PreFilter注解，用于在执行方法之前过滤集合参数**：

```java
@PreFilter("filterObject != authentication.principal.username")
public String joinUsernames(List<String> usernames) {
    return String.join(";", usernames);
}
```

在此示例中，我们拼接给定usernames集合中的所有字符串，但经过身份验证的username除外。

**在我们的表达式中，我们使用名称filterObject来表示集合中的当前对象**。

但是，如果该方法有多个集合类型的参数，我们需要使用filterTarget属性来指定要过滤的参数：

```java
@PreFilter(value = "filterObject != authentication.principal.username", filterTarget = "usernames")
public String joinUsernamesAndRoles(List<String> usernames, List<String> roles) {
    return String.join(";", usernames) + ":" + String.join(";", roles);
}
```

此外，**我们还可以使用@PostFilter注解过滤方法返回的集合**：

```java
@PostFilter("filterObject != authentication.principal.username")
public List<String> getAllUsernamesExceptCurrent() {
    return userRoleRepository.getAllUsernames();
}
```

在这种情况下，filterObject指的是方法返回的集合中的当前对象。

使用该配置，Spring Security将遍历返回的列表并删除与主体用户名匹配的任何值。

我们的[Spring Security–@PreFilter和@PostFilter](SpringSecurity_@PreFilter_@PostFilter.md)文章更详细地描述了这两个注解。

### 3.5 方法安全元注解

我们通常会面临使用相同的安全配置保护不同的方法的情况，这样很容易造成冗余。

在这种情况下，我们可以定义一个安全元注解：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('VIEWER')")
public @interface IsViewer {
}
```

接下来，我们可以直接使用@IsViewer注解来保护我们的方法：

```java
@IsViewer
public String getUsername4() {
    // ...
}
```

使用自定义的元注解是个不错的选择，因为**它们可以添加更多的安全配置语义，并将我们的业务逻辑与框架分离**。

### 3.6 类级别的安全注解

如果我们发现自己对一个类中的每个方法使用相同的安全注解，我们可以考虑将该注解放在类级别上：

```java
@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemService {

    public String getSystemYear() {
        return "2022";
    }

    public String getSystemDate() {
        return "2022-06-12";
    }
}
```

在上面的示例中，安全规则hasRole('ROLE_ADMIN')将同时作用于getSystemYear和getSystemDate方法。

### 3.7 方法上的多个安全注解

我们还可以在一个方法上使用多个安全注解：

```java
@PreAuthorize("#username == authentication.principal.username")
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser securedLoadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```

这样，Spring将在secureLoadUserDetail方法之前和之后验证授权。

## 4. 重要注意事项

关于方法安全性，我们需要强调两点：

+ **默认情况下，Spring AOP代理用于应用方法安全**。如果同一类中的另一个方法调用了受保护的方法A，则A中的安全性将被完全忽略。这意味着方法A将在没有任何安全检查的情况下执行。这同样适用于私有方法。
+ **Spring SecurityContext是线程绑定的**。默认情况下，安全上下文不会传播到子线程。有关更多信息，请参阅我们的[Spring Security Context Propagation](https://www.baeldung.com/spring-security-async-principal-propagation)文章。

## 5. 测试方法安全

### 5.1 配置

**要使用JUnit测试Spring Security，我们需要spring-security-test依赖项**：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
</dependency>
```

我们不需要指定依赖版本，因为我们使用的是Spring Boot插件。我们可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.security/spring-security-test/6.0.2)上找到这个依赖的最新版本。

接下来，让我们通过指定Extension和ApplicationContext配置来配置一个简单的Spring集成测试：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class MethodSecurityIntegrationTest {

    @Autowired
    UserRoleService userRoleService;

    @Configuration
    @ComponentScan("cn.tuyucheng.taketoday.methodsecurity.*")
    public static class SpringConfig {
    }
}
```

### 5.2 测试用户名和角色

现在我们的配置已经准备就绪，让我们尝试测试使用@Secured("ROLE_VIEWER")注解保护的getUsername方法：

```java
@Secured("ROLE_VIEWER")
public String getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return securityContext.getAuthentication().getName();
}
```

由于我们在这里使用了@Secured注解，因此需要对用户进行身份验证才能调用该方法。否则，我们将得到AuthenticationCredentialsNotFoundException。

因此，**我们需要提供一个用户来测试我们的安全方法**。

**为了实现这一点，我们使用@WithMockUser注解修饰测试方法，并提供username和roles**：

```java
@Test
@WithMockUser(username = "john", roles = {"VIEWER"})
void givenRoleViewer_whenCallGetUsername_thenReturnUsername() {
    String username = userRoleService.getUsername();
    assertEquals("john", username);
}
```

我们提供了一个经过身份验证的用户，其用户名是john，角色是ROLE_VIEWER。如果我们不指定用户名或角色，则默认用户名是user，默认角色是ROLE_USER。

**请注意，这里不需要添加“ROLE\_“前缀，因为Spring Security会自动添加该前缀**。

如果我们不想使用这个前缀，我们可以考虑使用authority而不是role。

例如，让我们声明一个getUsernameInLowerCase方法：

```java
@PreAuthorize("hasAuthority('SYS_ADMIN')")
public String getUsernameInLowerCase(){
    return getUsername().toLowerCase();
}
```

我们可以使用权限进行测试：

```java
@Test
@WithMockUser(username = "john", authorities = {"SYS_ADMIN"})
void givenAuthoritySysAdmin_whenCallGetUsernameLC_thenReturnUsername() {
    String username = userRoleService.getUsernameLC();
    assertEquals("john", username);
}
```

方便的是，**如果我们想对多个测试用例使用同一个用户，我们可以在测试类上声明@WithMockUser注解**：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WithMockUser(username = "john", roles = {"VIEWER"})
class MethodSecurityIntegrationTest {
    // ...
}
```

**如果我们想以匿名用户身份运行测试，我们可以使用@WithAnonymousUser注解**：

```java
@Test
@WithAnonymousUser
void givenAnonymousUser_whenCallGetUsername_thenAccessDenied() {
    assertThrows(AccessDeniedException.class, userRoleService::getUsername);
}
```

在上面的示例中，我们期望测试抛出AccessDeniedException，因为匿名用户未被授予角色ROLE_VIEWER或权限SYS_ADMIN。

### 5.3 使用自定义UserDetailsService进行测试

**对于大多数应用程序，通常使用自定义类作为身份验证主体(Principal)**。在这种情况下，自定义类需要实现org.springframework.security.core.userdetails.UserDetails接口。

在本文中，我们声明一个CustomUser类，该类扩展了UserDetails的现有实现，即org.springframework.security.core.userdetails.User：

```java
public class CustomUser extends User {
    private String nickName;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String nickName) {
        super(username, password, authorities);
        this.nickName = nickName;
    }
    // getter and setter ...
}
```

让我们回顾一下第3节中带有@PostAuthorize注解的示例：

```java
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser loadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```

在这种情况下，只有当返回的CustomUser的username等于当前身份验证主体的nickName时，该方法才会成功执行。

如果我们想测试该方法，**我们可以提供一个UserDetailsService的实现，它可以根据username加载我们的CustomUser**：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class UserDetailsIntegrationTest {

    @Autowired
    UserRoleService userService;

    @Configuration
    @ComponentScan("cn.tuyucheng.taketoday.methodsecurity.*")
    public static class SpringConfig {

    }

    @Test
    @WithUserDetails(value = "john", userDetailsServiceBeanName = "userDetailService")
    void whenJohn_callLoadUserDetail_thenOK() {
        CustomUser user = userService.loadUserDetail("jane");
        assertEquals("jane", user.getNickName());
    }
}
```

这里的@WithUserDetails注解表明我们将使用UserDetailsService来初始化经过身份验证的用户。userDetailService由userDetailsServiceBeanName属性引用。此UserDetailsService可以是真实的具体实现，也可以是用于测试目的而伪造的。

此外，userDetailService将使用注解value属性的值作为用户名来加载UserDetails。

方便的是，我们还可以在类级别使用@WithUserDetails注解进行修饰，类似于我们使用@WithMockUser一样。

### 5.4 使用元注解进行测试

我们经常面临着在各种测试中反复使用相同的用户/角色。对于这些情况，我们可以向之前那样创建自己的元注解。

再看前面的例子@WithMockUser(username="john", roles={"VIEWER"})，我们可以声明一个元注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "john", roles = "VIEWER")
public @interface WithMockJohnViewer {
}
```

然后我们可以简单地在测试中使用@WithMockJohnViewer：

```java
@Test
@WithMockJohnViewer
void givenMockedJohnViewer_whenCallGetUsername_thenReturnUsername() {
    String userName = userRoleService.getUsername();
    assertEquals("john", userName);
}
```

同样，我们可以使用元注解来使用@WithUserDetails创建特定于域的用户。

## 6. 总结

在本文中，我们探讨了在Spring Security中使用方法安全性的各种选项。

我们还针对Spring Security中的测试进行了介绍，并学习了如何在不同的测试中重用模拟用户。
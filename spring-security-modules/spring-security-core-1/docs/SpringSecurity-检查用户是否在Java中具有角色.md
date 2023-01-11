## 1. 概述

在Spring Security中，有时需要检查经过身份验证的用户是否具有特定角色。**这对于启用或禁用我们应用程序中的特定功能很有用**。

在本教程中，我们将看到在Java中为Spring Security检查用户角色的各种方法。

## 2. 检查用户角色

### 2.1 @PreAuthorize

第一种方法是使用Spring Security提供的@PreAuthorize注解。此注解可应用于类或方法，它接收表示SpEL表达式的单个字符串。

在我们使用这个注解之前，必须首先启用全局方法安全。
这可以在Java代码中通过将@EnableGlobalMethodSecurity注解添加到任何配置类上来完成。

然后，Spring Security提供了两个表达式，我们可以使用@PreAuthorize注解来检查用户角色：

```text
@PreAuthorize("hasRole('ROLE_ADMIN')")
@GetMapping("/user/{id}")
public String getUser(@PathVariable("id") String id) {
    // ...
}
```

我们还可以在一个表达式中检查多个角色：

```text
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
@GetMapping("/users")
public String getUsers() {
    // ...
}
```

在这种情况下，如果用户具有任何指定的角色，则该请求允许访问。

如果用户在没有适当角色的情况下调用该方法，Spring Security会抛出异常并重定向到错误页面。

### 2.2 SecurityContext

第二种方法是使用SecurityContext类。

默认情况下，Spring Security使用此类的线程本地副本。
**这意味着我们应用程序中的每个请求都有其SecurityContext，其中包含发出请求的用户的详细信息**。

要使用它，我们只需调用SecurityContextHolder中的静态方法：

```java

@Controller
public class UserController {

    @GetMapping("/v2/user/{id}")
    public String getUserUsingSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN")))
            return "user";
        throw new UnauthorizedException();
    }
}
```

请注意，我们在这里使用普通的角色名称"ADMIN"而不是完整的角色名称。

当我们需要更细粒度的检查时(例如，单个方法的特定部分)，这种方法很有用。
**但是，如果我们在Spring Security中使用全局上下文持有者模式，这种方法将不起作用**。

### 2.3 UserDetailsService

第三种方法是使用UserDetailsService。我们可以将这个bean注入应用程序的任何位置，并根据需要调用它：

```java

@Controller
public class UserController {
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/v2/users")
    public String getUsersUsingDetailsService() {
        UserDetails details = userDetailsService.loadUserByUsername("mike");
        if (details != null && details.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN")))
            return "users";
        throw new UnauthorizedException();
    }
}
```

同样，我们必须在这里使用角色名称，而不是带有前缀的完整角色名称。

**这种方法的好处是我们可以检查任何用户的角色，而不仅仅是发出此次请求的用户**。

### 2.4 Servlet Request

如果我们使用Spring MVC，我们还可以使用HttpServletRequest类检查用户角色：

```java

@Controller
public class UserController {

    @GetMapping("/v3/users")
    public String getUsers(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN"))
            return "users";
        throw new UnauthorizedException();
    }
}
```

## 3. 总结

在本文中，我们介绍了几种使用Java代码和Spring Security来检查角色的不同方法。
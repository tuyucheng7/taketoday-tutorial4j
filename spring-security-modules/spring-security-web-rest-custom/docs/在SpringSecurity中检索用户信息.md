## 1. 概述

本教程将展示如何在 Spring Security 中检索用户详细信息。

当前经过身份验证的用户可通过 Spring 中的许多不同机制获得。让我们首先介绍最常见的解决方案——编程访问。

## 进一步阅读：

## [使用 Spring Security 跟踪登录用户](https://www.baeldung.com/spring-security-track-logged-in-users)

在使用 Spring Security 构建的应用程序中跟踪登录用户的快速指南。

[阅读更多](https://www.baeldung.com/spring-security-track-logged-in-users)→

## [Spring Security – 角色和特权](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)

如何映射 Spring Security 应用程序的角色和权限：设置、身份验证和注册过程。

[阅读更多](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)→

## [Spring Security – 重置的密码](https://www.baeldung.com/spring-security-registration-i-forgot-my-password)

每个应用程序都应该允许用户更改自己的密码，以防他们忘记密码。

[阅读更多](https://www.baeldung.com/spring-security-registration-i-forgot-my-password)→

## 2. 获取 Bean 中的用户

检索当前经过身份验证的主体的最简单方法是通过对SecurityContextHolder的静态调用：

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String currentPrincipalName = authentication.getName();
```

这个片段的一个改进是在尝试访问之前首先检查是否有经过身份验证的用户：

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
if (!(authentication instanceof AnonymousAuthenticationToken)) {
    String currentUserName = authentication.getName();
    return currentUserName;
}
```

像这样进行静态调用当然有缺点，代码的可测试性降低是最明显的原因之一。相反，我们将探索针对这个非常常见的需求的替代解决方案。

## 3. 在控制器中获取用户

我们在@Controller注解的 bean中有其他选项。

我们可以直接将主体定义为方法参数，它会被框架正确解析：

```java
@Controller
public class SecurityController {

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {
        return principal.getName();
    }
}
```

或者，我们也可以使用身份验证令牌：

```java
@Controller
public class SecurityController {

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }
}
```

Authentication类的 API非常开放，因此框架尽可能保持灵活。因此，Spring Security 主体只能作为Object检索，并且需要转换为正确的UserDetails实例：

```java
UserDetails userDetails = (UserDetails) authentication.getPrincipal();
System.out.println("User has authorities: " + userDetails.getAuthorities());
```

最后，这里直接来自 HTTP 请求：

```java
@Controller
public class GetUserWithHTTPServletRequestController {

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameSimple(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal.getName();
    }
}
```

## 4.通过自定义界面获取用户

为了充分利用 Spring 依赖注入并能够在任何地方检索身份验证，而不仅仅是在@Controller beans中，我们需要将静态访问隐藏在一个简单的外观后面：

```java
public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
```

外观暴露了Authentication对象，同时隐藏了静态状态并保持代码解耦和完全可测试：

```java
@Controller
public class GetUserWithCustomInterfaceController {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameSimple() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return authentication.getName();
    }
}
```

## 5. JSP中获取用户

通过利用 Spring Security Taglib 支持，还可以在 JSP 页面中访问当前经过身份验证的主体。

首先，我们需要在页面中定义标签：

```xml
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
```

接下来，我们可以参考principal：

```xml
<security:authorize access="isAuthenticated()">
    authenticated as <security:authentication property="principal.username" /> 
</security:authorize>
```

## 6. 在 Thymeleaf 中获取用户

[Thymeleaf](http://www.thymeleaf.org/index.html)是一个现代的服务器端 Web 模板引擎，与Spring MVC框架具有良好[的集成。](https://www.baeldung.com/thymeleaf-in-spring-mvc)

让我们看看如何使用 Thymeleaf 引擎访问页面中当前经过身份验证的主体。

首先，我们需要添加[thymeleaf-spring5](https://search.maven.org/search?q=a:thymeleaf-spring5)和[thymeleaf-extras-springsecurity5](https://search.maven.org/search?q=a:thymeleaf-extras-springsecurity5)依赖项来集成 Thymeleaf 和 Spring Security：

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
</dependency>
```

现在我们可以使用sec:authorize属性来引用 HTML 页面中的主体：

```html
<html xmlns:th="https://www.thymeleaf.org" 
  xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity5">
<body>
    <div sec:authorize="isAuthenticated()">
      Authenticated as <span sec:authentication="name"></span></div>
</body>
</html>
```

## 7. 总结

本文展示了如何在 Spring 应用程序中获取用户信息，从常见的静态访问机制开始，然后介绍几种更好的注入主体的方法。

这些示例的实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-rest-custom)中找到。这是一个基于 Eclipse 的项目，因此它应该很容易导入并按原样运行。在本地运行项目时，我们可以在此处访问首页 HTML：

```bash
http://localhost:8080/spring-security-rest-custom/foos/1
```
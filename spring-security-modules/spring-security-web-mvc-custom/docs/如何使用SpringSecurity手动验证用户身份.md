## 1. 概述

在这篇快速文章中，我们将重点介绍如何在 Spring Security 和 Spring MVC 中以编程方式设置经过身份验证的用户。

## 2. Spring 安全

简单地说，Spring Security 将每个经过身份验证的用户的主体信息保存在一个ThreadLocal中——表示为一个Authentication对象。

为了构造和设置这个Authentication对象——我们需要使用 Spring Security 通常用来在标准身份验证上构建对象的相同方法。

为了，让我们手动触发身份验证，然后将生成的Authentication对象设置为框架使用的当前SecurityContext来保存当前登录的用户：

```java
UsernamePasswordAuthenticationToken authReq
 = new UsernamePasswordAuthenticationToken(user, pass);
Authentication auth = authManager.authenticate(authReq);
SecurityContext sc = SecurityContextHolder.getContext();
sc.setAuthentication(auth);
```

在上下文中设置身份验证后，我们现在可以使用securityContext.getAuthentication().isAuthenticated()检查当前用户是否已通过身份验证。

## 3. Spring MVC

默认情况下，Spring Security 在 Spring Security 过滤器链中添加了一个额外的过滤器——它能够持久化安全上下文(SecurityContextPersistenceFilter类)。

反过来，它将安全上下文的持久性委托给 SecurityContextRepository 的一个实例，默认为HttpSessionSecurityContextRepository类。

因此，为了对请求设置身份验证，从而使其可用于来自客户端的所有后续请求，我们需要在 HTTP 会话中手动设置包含身份验证的SecurityContext ：

```java
public void login(HttpServletRequest req, String user, String pass) { 
    UsernamePasswordAuthenticationToken authReq
      = new UsernamePasswordAuthenticationToken(user, pass);
    Authentication auth = authManager.authenticate(authReq);
    
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    HttpSession session = req.getSession(true);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
}
```

SPRING_SECURITY_CONTEXT_KEY是静态导入的HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY。

应该注意的是，我们不能直接使用HttpSessionSecurityContextRepository——因为它与SecurityContextPersistenceFilter 一起工作。

这是因为过滤器使用存储库在链中定义的其余过滤器执行之前和之后加载和存储安全上下文，但它对传递给链的响应使用自定义包装器。

所以在这种情况下，应该知道所使用的包装器的类类型，并将其传递给存储库中适当的保存方法。

## 4. 总结

在这个快速教程中，我们讨论了如何在 Spring Security 上下文中手动设置用户身份验证以及如何使其可用于 Spring MVC 目的，重点介绍了说明实现它的最简单方法的代码示例。
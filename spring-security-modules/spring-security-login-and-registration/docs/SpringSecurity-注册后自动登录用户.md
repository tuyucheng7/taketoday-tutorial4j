## 1. 概述

在本快速教程中，我们将讨论如何在注册过程后立即对用户进行自动身份验证-在Spring Security实现中。

简而言之，一旦用户完成注册，他们通常会被重定向到登录页面，现在必须重新输入他们的用户名和密码。

让我们看看如何通过自动验证用户来避免这种情况。

## 2. 使用HttpServletRequest

以编程方式强制进行身份验证的一种非常简单的方法是利用HttpServletRequest [login()](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#login(java.lang.String,java.lang.String))方法：

```java
public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
    try {
        request.login(username, password);
    } catch (ServletException e) {
        LOGGER.error("Error while login ", e);
    }
}
```

现在，在幕后，HttpServletRequest.login() API确实使用AuthenticationManager来执行身份验证。

了解和处理可能在此级别发生的ServletException也很重要。

## 3. 使用AuthenticationManager

接下来，我们也可以直接创建一个UsernamePasswordAuthenticationToken-然后手动通过标准的AuthenticationManager：

```java
public void authWithAuthManager(HttpServletRequest request, String username, String password) {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
    authToken.setDetails(new WebAuthenticationDetails(request));
    
    Authentication authentication = authenticationManager.authenticate(authToken);
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

请注意我们如何创建令牌请求，将其传递给标准身份验证流程，然后在当前安全上下文中显式设置结果。

## 4. 复杂注册

在某些更复杂的场景中，注册过程有多个阶段，例如-用户可以登录系统之前的确认步骤。

在这种情况下，准确了解我们可以在何处对用户进行自动身份验证当然很重要。我们不能在他们注册后立即执行此操作，因为此时新创建的帐户仍处于禁用状态。

简而言之-**我们必须在他们确认他们的帐户后执行自动身份验证**。

另外，请记住，在这一点上-我们无法再访问他们实际的、原始的凭据。我们只能访问用户的编码密码，这就是我们将在这里使用的：

```java
public void authWithoutPassword(User user){
    List<Privilege> privileges = user.getRoles().stream().map(Role::getPrivileges)
        .flatMap(Collection::stream).distinct().collect(Collectors.toList());
    List<GrantedAuthority> authorities = privileges.stream()
        .map(p -> new SimpleGrantedAuthority(p.getName()))
        .collect(Collectors.toList());

    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

请注意我们如何在此处正确设置身份验证权限，这通常是在AuthenticationProvider中完成的。

## 5. 总结

我们讨论了在注册过程后自动验证用户的不同方法。
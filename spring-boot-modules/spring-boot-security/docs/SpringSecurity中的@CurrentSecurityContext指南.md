## 1. 概述

Spring Security为我们处理接收和解析身份验证凭据。

在这个简短的教程中，我们将了解如何在我们的处理程序代码中从请求中获取SecurityContext信息。

## 2. @CurrentSecurityContext注解

我们可以使用一些样板代码来读取安全上下文：

```java
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
```

但是，**现在有一个@CurrentSecurityContext注解可以帮助我们**。此外，使用注解使代码更具声明性，并使身份验证对象可注入。使用@CurrentSecurityContext，我们还可以访问当前用户的Principal实现。

在下面的示例中，我们将介绍几种获取安全上下文数据的方法，例如Authentication和Principal的名称，并将了解如何测试我们的代码。

## 3. Maven依赖

如果我们有最新版本的Spring Boot，那么我们只需要包含spring-boot-starter-security的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

否则，我们可以将[spring-security-core](https://search.maven.org/artifact/org.springframework.security/spring-security-core/6.0.1/jar)升级到最低版本5.2.1.RELEASE：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-core</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>
```

## 4. 使用@CurrentSecurityContext实现

我们可以将SpEL(Spring表达式语言)与@CurrentSecurityContext注解结合使用来**注入Authentication对象或Principal**。SpEL与类型查找一起工作，默认情况下不强制执行类型检查，但我们可以通过@CurrentSecurityContext注解的errorOnInvalidType参数启用它。

### 4.1 获取Authentication对象

让我们读取Authentication对象，以便我们可以返回其详细信息：

```java
@GetMapping("/authentication")
public Object getAuthentication(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
    return authentication.getDetails();
}
```

请注意，SpEL表达式引用的是Authentication对象本身。

下面是一个简单的测试：

```java
@Test
public void givenOAuth2Context_whenAccessingAuthentication_ThenRespondTokenDetails() {
    ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("tuyucheng", singletonList("read"));
    OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);

    String authentication = executeGetRequest(restTemplate, "/authentication");

    Pattern pattern = Pattern.compile("\\{\"remoteAddress\":\".*"
      + "\",\"sessionId\":null,\"tokenValue\":\".*"
      + "\",\"tokenType\":\"Bearer\",\"decodedDetails\":null}");
    assertTrue("authentication", pattern.matcher(authentication).matches());
}
```

我们应该注意到，在这个例子中，我们正在获取连接的所有详细信息。由于我们的测试代码无法预测remoteAddress或tokenValue，因此我们使用正则表达式来检查生成的JSON。

### 4.2 获取Principal

如果我们只想从我们的身份验证数据中获取Principal，我们可以更改SpEL表达式和注入的对象：

```java
@GetMapping("/principal")
public String getPrincipal(@CurrentSecurityContext(expression = "authentication.principal") Principal principal) { 
    return principal.getName(); 
}
```

在本例中，我们使用getName方法仅返回主体名称。

同样，下面是一个测试：

```java
@Test
public void givenOAuth2Context_whenAccessingPrincipal_ThenRespondBaeldung() {
    ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("tuyucheng", singletonList("read"));
    OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);

    String principal = executeGetRequest(restTemplate, "/principal");

    assertEquals("tuyucheng", principal);
}
```

在这里，我们看到名称tuyucheng被添加到客户端凭据中，从注入处理程序的Principal对象内部找到并返回。

## 5. 总结

在本文中，我们介绍了如何访问当前安全上下文中的属性并将它们注入到我们的处理程序方法中的参数中，我们通过利用SpEL和@CurrentSecurityContext注解来做到这一点。
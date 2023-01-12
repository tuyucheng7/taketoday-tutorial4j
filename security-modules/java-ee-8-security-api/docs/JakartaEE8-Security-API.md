## 1. 概述

Jakarta EE 8 Security API 是处理Java容器中安全问题的新标准和可移植方式。

在本文中，我们将了解 API 的三个核心功能：

1.  HTTP认证机制
2.  身份存储
3.  安全上下文

我们将首先了解如何配置提供的实现，然后了解如何实现自定义实现。

## 2.Maven依赖

要设置 Jakarta EE 8 Security API，我们需要服务器提供的实现或显式实现。

### 2.1. 使用服务器实现

Jakarta EE 8 兼容服务器已经为 Jakarta EE 8 Security API 提供了一个实现，因此我们只需要[Jakarta EE Web Profile API](https://search.maven.org/classic/#search|gav|1|g%3A"javax" AND a%3A"javaee-web-api") Maven 工件：

```xml
<dependencies>
    <dependency>
        <groupId>javax</groupId>
        <artifactId>javaee-web-api</artifactId>
        <version>8.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 2.2. 使用显式实现

[首先，我们为 Jakarta EE 8 Security API](https://search.maven.org/classic/#search|ga|1|a%3A"javax.security.enterprise-api")指定 Maven 工件：

```xml
<dependencies>
    <dependency>
        <groupId>javax.security.enterprise</groupId>
        <artifactId>javax.security.enterprise-api</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

然后，我们将添加一个实现，例如 [Soteria——](https://search.maven.org/classic/#search|ga|1|a%3A"javax.security.enterprise") 参考实现：

```xml
<dependencies>
    <dependency>
        <groupId>org.glassfish.soteria</groupId>
        <artifactId>javax.security.enterprise</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

## 3. HTTP认证机制

在 Jakarta EE 8 之前，我们通过web.xml文件以声明方式配置身份验证机制。

在这个版本中，Jakarta EE 8 Security API 设计了新的HttpAuthenticationMechanism接口作为替代。因此，Web 应用程序现在可以通过提供此接口的实现来配置身份验证机制。

幸运的是，容器已经为 Servlet 规范定义的三种身份验证方法中的每一种提供了实现：基本 HTTP 身份验证、基于表单的身份验证和自定义的基于表单的身份验证。

它还提供了一个注解来触发每个实现：

1.  @BasicAuthenticationMechanismDefinition
2.  @FormAuthenticationMechanismDefinition
3.  @CustomFormAuthenrticationMechanismDefinition

### 3.1. 基本 HTTP 身份验证

如上所述，Web 应用程序只需 在 CDI bean 上使用 @BasicAuthenticationMechanismDefinition 注解即可配置基本 HTTP 身份验证：

```java
@BasicAuthenticationMechanismDefinition(
  realmName = "userRealm")
@ApplicationScoped
public class AppConfig{}
```

此时，Servlet 容器搜索并实例化提供的HttpAuthenticationMechanism 接口实现。

收到未经授权的请求后，容器会通过WWW-Authenticate响应标头质询客户端以提供合适的身份验证信息。

```bash
WWW-Authenticate: Basic realm="userRealm"
```

然后，客户端通过授权请求标头发送用户名和密码，以冒号“:”分隔并以 Base64 编码：

```bash
//user=baeldung, password=baeldung
Authorization: Basic YmFlbGR1bmc6YmFlbGR1bmc=

```

请注意，为提供凭据而显示的对话框来自浏览器，而不是来自服务器。

### 3.2. 基于表单的 HTTP 身份验证

@FormAuthenticationMechanismDefinition 注解触发了由 Servlet 规范定义的基于表单的身份验证。

然后我们可以选择指定登录和错误页面或使用默认的合理页面/login和/login-error：

```java
@FormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(
    loginPage = "/login.html",
    errorPage = "/login-error.html"))
@ApplicationScoped
public class AppConfig{}
```

作为调用loginPage 的结果，服务器应该将表单发送给客户端：

```html
<form action="j_security_check" method="post">
    <input name="j_username" type="text"/>
    <input name="j_password" type="password"/>
    <input type="submit">
</form>
```

然后，客户端应将表单发送到容器提供的预定义支持身份验证过程。

### 3.3. 自定义基于表单的 HTTP 身份验证

Web 应用程序可以通过使用注解@CustomFormAuthenticationMechanismDefinition 来触发自定义的基于表单的身份验证实现：

```java
@CustomFormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(loginPage = "/login.xhtml"))
@ApplicationScoped
public class AppConfig {
}
```

但与默认的基于表单的身份验证不同，我们正在配置自定义登录页面并调用 SecurityContext.authenticate ()方法作为后备身份验证过程。

让我们也看看支持的LoginBean，它包含登录逻辑：

```java
@Named
@RequestScoped
public class LoginBean {

    @Inject
    private SecurityContext securityContext;

    @NotNull private String username;

    @NotNull private String password;

    public void login() {
        Credential credential = new UsernamePasswordCredential(
          username, new Password(password));
        AuthenticationStatus status = securityContext
          .authenticate(
            getHttpRequestFromFacesContext(),
            getHttpResponseFromFacesContext(),
            withParams().credential(credential));
        // ...
    }
     
    // ...
}
```

作为调用自定义login.xhtml页面的结果，客户端将收到的表单提交给LoginBean的login()方法：

```xml
//...
<input type="submit" value="Login" jsf:action="#{loginBean.login}"/>
```

### 3.4. 自定义认证机制

HttpAuthenticationMechanism接口定义了三种方法。最重要的是 我们必须提供实现的validateRequest() 。

其他两种方法( secureResponse()和cleanSubject())的默认行为 在大多数情况下就足够了。

让我们看一个示例实现：

```java
@ApplicationScoped
public class CustomAuthentication 
  implements HttpAuthenticationMechanism {

    @Override
    public AuthenticationStatus validateRequest(
      HttpServletRequest request,
      HttpServletResponse response, 
      HttpMessageContext httpMsgContext) 
      throws AuthenticationException {
 
        String username = request.getParameter("username");
        String password = response.getParameter("password");
        // mocking UserDetail, but in real life, we can obtain it from a database
        UserDetail userDetail = findByUserNameAndPassword(username, password);
        if (userDetail != null) {
            return httpMsgContext.notifyContainerAboutLogin(
              new CustomPrincipal(userDetail),
              new HashSet<>(userDetail.getRoles()));
        }
        return httpMsgContext.responseUnauthorized();
    }
    //...
}
```

在这里，实现提供了验证过程的业务逻辑，但在实践中，建议通过调用validate通过IdentityStoreHandler委托给IdentityStore。

我们还使用 @ApplicationScoped注解对实现进行了注解，因为我们需要使其启用 CDI。

在对凭证进行有效验证并最终检索用户角色后，实施应通知容器：

```java
HttpMessageContext.notifyContainerAboutLogin(Principal principal, Set groups)
```

### 3.5. 加强 Servlet 安全性

Web 应用程序可以通过在 Servlet 实现上使用@ServletSecurity注解来强制执行安全约束：

```java
@WebServlet("/secured")
@ServletSecurity(
  value = @HttpConstraint(rolesAllowed = {"admin_role"}),
  httpMethodConstraints = {
    @HttpMethodConstraint(
      value = "GET", 
      rolesAllowed = {"user_role"}),
    @HttpMethodConstraint(     
      value = "POST", 
      rolesAllowed = {"admin_role"})
  })
public class SecuredServlet extends HttpServlet {
}
```

这个注解有两个属性 ——httpMethodConstraints和value； httpMethodConstraints 用于指定一个或多个约束，每个约束代表允许角色列表对 HTTP 方法的访问控制。

然后，容器将为每个url 模式和 HTTP 方法检查连接的用户是否具有访问资源的合适角色。

## 4.身份存储

此功能由 IdentityStore 接口抽象出来， 用于验证凭据并最终检索组成员资格 。换句话说，它可以提供身份验证、授权或两者的能力。

IdentityStore旨在并鼓励HttpAuthenticationMecanism通过调用的IdentityStoreHandler接口 使用 。IdentityStoreHandler的默认实现由 Servlet 容器提供 。

应用程序可以提供其IdentityStore的实现或使用容器为数据库和 LDAP 提供的两个内置实现之一。

### 4.1. 内置身份存储

Jakarta EE 兼容服务器应该为两个身份存储提供实现：数据库和 LDAP。

通过将配置数据传递给@DataBaseIdentityStoreDefinition注解来初始化数据库IdentityStore实现：

```java
@DatabaseIdentityStoreDefinition(
  dataSourceLookup = "java:comp/env/jdbc/securityDS",
  callerQuery = "select password from users where username = ?",
  groupsQuery = "select GROUPNAME from groups where username = ?",
  priority=30)
@ApplicationScoped
public class AppConfig {
}
```

作为配置数据，我们需要一个外部数据库的 JNDI 数据源，两个用于检查调用者及其组的 JDBC 语句，最后一个用于多个存储的优先级参数被配置。

具有高优先级的IdentityStore稍后由 IdentityStoreHandler 处理。

与数据库一样，LDAP IdentityStore 实现通过传递配置数据通过@LdapIdentityStoreDefinition进行初始化：

```java
@LdapIdentityStoreDefinition(
  url = "ldap://localhost:10389",
  callerBaseDn = "ou=caller,dc=baeldung,dc=com",
  groupSearchBase = "ou=group,dc=baeldung,dc=com",
  groupSearchFilter = "(&(member=%s)(objectClass=groupOfNames))")
@ApplicationScoped
public class AppConfig {
}
```

这里我们需要外部 LDAP 服务器的 URL，如何在 LDAP 目录中搜索调用者，以及如何检索他的组。

### 4.2. 实现自定义IdentityStore

IdentityStore接口定义了四种默认方法：

```java
default CredentialValidationResult validate(
  Credential credential)
default Set<String> getCallerGroups(
  CredentialValidationResult validationResult)
default int priority()
default Set<ValidationType> validationTypes()
```

priority() 方法返回一个值，表示此实现由IdentityStoreHandler 处理的迭代顺序。 首先处理优先级较低 的 IdentityStore 。

默认情况下， IdentityStore处理凭据验证(ValidationType.VALIDATE)和组检索 ( ValidationType.PROVIDE_GROUPS )。我们可以覆盖此行为，以便它只能提供一种功能。

因此，我们可以将IdentityStore配置 为仅用于凭证验证：

```java
@Override
public Set<ValidationType> validationTypes() {
    return EnumSet.of(ValidationType.VALIDATE);
}
```

在这种情况下，我们应该为validate()方法提供一个实现：

```java
@ApplicationScoped
public class InMemoryIdentityStore implements IdentityStore {
    // init from a file or harcoded
    private Map<String, UserDetails> users = new HashMap<>();

    @Override
    public int priority() {
        return 70;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return EnumSet.of(ValidationType.VALIDATE);
    }

    public CredentialValidationResult validate( 
      UsernamePasswordCredential credential) {
 
        UserDetails user = users.get(credential.getCaller());
        if (credential.compareTo(user.getLogin(), user.getPassword())) {
            return new CredentialValidationResult(user.getLogin());
        }
        return INVALID_RESULT;
    }
}
```

或者我们可以选择配置 IdentityStore ，使其只能用于组检索：

```java
@Override
public Set<ValidationType> validationTypes() {
    return EnumSet.of(ValidationType.PROVIDE_GROUPS);
}
```

然后我们应该为getCallerGroups()方法提供一个实现：

```java
@ApplicationScoped
public class InMemoryIdentityStore implements IdentityStore {
    // init from a file or harcoded
    private Map<String, UserDetails> users = new HashMap<>();

    @Override
    public int priority() {
        return 90;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return EnumSet.of(ValidationType.PROVIDE_GROUPS);
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        UserDetails user = users.get(
          validationResult.getCallerPrincipal().getName());
        return new HashSet<>(user.getRoles());
    }
}
```

因为IdentityStoreHandler期望实现是一个 CDI bean，所以我们用ApplicationScoped注解装饰它。

## 5.安全上下文API

Jakarta EE 8 Security API通过SecurityContext接口提供了对编程安全性的访问点。当容器强制执行的声明性安全模型不够时，它是一种替代方法。

SecurityContext接口的默认实现应该在运行时作为 CDI bean 提供，因此我们需要注入它：

```java
@Inject
SecurityContext securityContext;
```

此时，我们可以对用户进行身份验证，检索经过身份验证的用户，检查他的角色成员身份，并通过五种可用方法授予或拒绝对 Web 资源的访问权限。

### 5.1. 检索呼叫者数据

在以前的 Jakarta EE 版本中，我们会检索Principal或在每个容器中以不同方式检查角色成员资格。

当我们在 servlet 容器中使用 HttpServletRequest的getUserPrincipal()和 isUserInRole() 方法时，在 EJB 容器中使用EJBContext的类似方法 getCallerPrincipal() 和 isCallerInRole() 方法 。

新的 Jakarta EE 8 Security API 通过SecurityContext接口提供类似的方法对此进行 了标准化：

```java
Principal getCallerPrincipal();
boolean isCallerInRole(String role);
<T extends Principal> Set<T> getPrincipalsByType(Class<T> type);
```

getCallerPrincipal ()方法返回经过身份验证的调用者的容器特定表示，而 getPrincipalsByType() 方法检索给定类型的所有主体。

如果特定于应用程序的调用者与容器调用者不同，它会很有用。

### 5.2. Web 资源访问测试

首先，我们需要配置一个受保护的资源：

```java
@WebServlet("/protectedServlet")
@ServletSecurity(@HttpConstraint(rolesAllowed = "USER_ROLE"))
public class ProtectedServlet extends HttpServlet {
    //...
}
```

然后，要检查对这个受保护资源的访问，我们应该调用hasAccessToWebResource() 方法：

```java
securityContext.hasAccessToWebResource("/protectedServlet", "GET");
```

在这种情况下，如果用户在角色 USER_ROLE 中，则该方法返回 true。

### 5.3. 以编程方式验证调用者

应用程序可以通过调用authenticate()以编程方式触发身份验证过程：

```java
AuthenticationStatus authenticate(
  HttpServletRequest request, 
  HttpServletResponse response,
  AuthenticationParameters parameters);
```

然后容器会收到通知，并依次调用为应用程序配置的身份验证机制。 AuthenticationParameters参数为HttpAuthenticationMechanism 提供凭证 ：

```java
withParams().credential(credential)
```

AuthenticationStatus的SUCCESS和SEND_FAILURE 值 设计成功和失败的身份验证，而 SEND_CONTINUE表示 身份验证过程的进行中状态。

## 6. 运行示例

为了突出显示这些示例，我们使用了 支持 Jakarta EE 8 的[Open Liberty](https://openliberty.io/) Server 的最新开发版本。这得益于[liberty-maven-plugin 的](https://search.maven.org/classic/#search|ga|1|a%3A"liberty-maven-plugin")下载和安装 ，它还可以部署应用程序并启动服务器。

要运行示例，只需访问相应的模块并调用此命令：

```bash
mvn clean package liberty:run
```

结果，Maven 将下载服务器、构建、部署和运行应用程序。

## 七. 总结

在本文中，我们介绍了新 Jakarta EE 8 Security API 主要功能的配置和实现。

首先，我们首先展示了如何配置默认的内置身份验证机制以及如何实现自定义机制。后来，我们看到了如何配置内置身份存储以及如何实现自定义身份存储。最后，我们看到了如何调用 SecurityContext 的方法。
## 1. 概述

在本文中，我们将了解[Apache Shiro](https://shiro.apache.org/)，一个多功能的Java安全框架。

该框架是高度可定制和模块化的，因为它提供身份验证、授权、加密和会话管理。

## 2. 依赖

Apache Shiro 有很多[模块](https://shiro.apache.org/download.html)。但是，在本教程中，我们仅使用shiro-core工件。

让我们将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.4.0</version>
</dependency>
```

可以在 Maven Central 上找到最新版本的 Apache Shiro 模块[。](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.shiro")

## 3.配置安全管理器

SecurityManager是Apache Shiro 框架的核心部分。应用程序通常会运行一个实例。

在本教程中，我们将探索桌面环境中的框架。要配置框架，我们需要在资源文件夹中创建一个shiro.ini文件，内容如下：

```plaintext
[users]
user = password, admin
user2 = password2, editor
user3 = password3, author

[roles]
admin = 
editor = articles:
author = articles:compose,articles:save
```

shiro.ini配置文件的[users]部分定义了SecurityManager识别的用户凭证。格式为：principal (username) = password, role1, role2, …, role。

角色及其关联的权限在[roles]部分中声明。管理员角色被授予访问应用程序每个部分的权限和访问权限。这由通配符()符号表示。

editor角色拥有与文章相关的所有权限，而author角色只能撰写和保存文章。

SecurityManager用于配置SecurityUtils类。从SecurityUtils中我们可以获取到当前与系统交互的用户，并进行认证和授权操作。

让我们使用IniRealm从shiro.ini文件加载我们的用户和角色定义，然后使用它来配置DefaultSecurityManager对象：

```java
IniRealm iniRealm = new IniRealm("classpath:shiro.ini");
SecurityManager securityManager = new DefaultSecurityManager(iniRealm);

SecurityUtils.setSecurityManager(securityManager);
Subject currentUser = SecurityUtils.getSubject();
```

现在我们有了一个SecurityManager，它知道shiro.ini文件中定义的用户凭据和角色，让我们继续进行用户身份验证和授权。

## 4.认证

在 Apache Shiro 的术语中，Subject是与系统交互的任何实体。它可以是人、脚本或 REST 客户端。

调用SecurityUtils.getSubject()返回当前Subject的实例，即currentUser。

现在我们有了currentUser对象，我们可以对提供的凭据执行身份验证：

```java
if (!currentUser.isAuthenticated()) {               
  UsernamePasswordToken token                       
    = new UsernamePasswordToken("user", "password");
  token.setRememberMe(true);                        
  try {                                             
      currentUser.login(token);                       
  } catch (UnknownAccountException uae) {           
      log.error("Username Not Found!", uae);        
  } catch (IncorrectCredentialsException ice) {     
      log.error("Invalid Credentials!", ice);       
  } catch (LockedAccountException lae) {            
      log.error("Your Account is Locked!", lae);    
  } catch (AuthenticationException ae) {            
      log.error("Unexpected Error!", ae);           
  }                                                 
}
```

首先，我们检查当前用户是否尚未通过身份验证。然后我们使用用户的主体(用户名)和凭据(密码)创建一个身份验证令牌。

接下来，我们尝试使用令牌登录。如果提供的凭据是正确的，那么一切都会顺利进行。

不同的情况有不同的例外情况。也可以抛出更适合应用程序要求的自定义异常。这可以通过子类化AccountException类来完成。

## 5.授权

身份验证试图验证用户的身份，而授权试图控制对系统中某些资源的访问。

回想一下，我们为在shiro.ini文件中创建的每个用户分配了一个或多个角色。此外，在角色部分，我们为每个角色定义不同的权限或访问级别。

现在让我们看看如何在我们的应用程序中使用它来实施用户访问控制。

在shiro.ini文件中，我们授予管理员对系统每个部分的完全访问权限。

编辑可以完全访问与文章有关的所有资源/操作，而作者仅限于撰写和保存文章。

让我们根据角色欢迎当前用户：

```java
if (currentUser.hasRole("admin")) {       
    log.info("Welcome Admin");              
} else if(currentUser.hasRole("editor")) {
    log.info("Welcome, Editor!");           
} else if(currentUser.hasRole("author")) {
    log.info("Welcome, Author");            
} else {                                  
    log.info("Welcome, Guest");             
}
```

现在，让我们看看当前用户在系统中被允许做什么：

```java
if(currentUser.isPermitted("articles:compose")) {            
    log.info("You can compose an article");                    
} else {                                                     
    log.info("You are not permitted to compose an article!");
}                                                            
                                                             
if(currentUser.isPermitted("articles:save")) {               
    log.info("You can save articles");                         
} else {                                                     
    log.info("You can not save articles");                   
}                                                            
                                                             
if(currentUser.isPermitted("articles:publish")) {            
    log.info("You can publish articles");                      
} else {                                                     
    log.info("You can not publish articles");                
}
```

## 6. 领域配置

在实际应用程序中，我们需要一种从数据库而不是shiro.ini文件中获取用户凭据的方法。这就是 Realm 的概念发挥作用的地方。

在 Apache Shiro 的术语中，[Realm](https://shiro.apache.org/realm.html)是一个 DAO，它指向身份验证和授权所需的用户凭据存储。

要创建一个领域，我们只需要实现Realm接口。那可能很乏味；然而，该框架带有默认实现，我们可以从中继承。这些实现之一是JdbcRealm。

我们创建一个扩展JdbcRealm类并覆盖以下方法的自定义领域实现：doGetAuthenticationInfo()、doGetAuthorizationInfo()、getRoleNamesForUser()和getPermissions()。

让我们通过子类化JdbcRealm类来创建一个领域：

```java
public class MyCustomRealm extends JdbcRealm {
    //...
}
```

为了简单起见，我们使用java.util.Map来模拟一个数据库：

```java
private Map<String, String> credentials = new HashMap<>();
private Map<String, Set<String>> roles = new HashMap<>();
private Map<String, Set<String>> perm = new HashMap<>();

{
    credentials.put("user", "password");
    credentials.put("user2", "password2");
    credentials.put("user3", "password3");
                                          
    roles.put("user", new HashSet<>(Arrays.asList("admin")));
    roles.put("user2", new HashSet<>(Arrays.asList("editor")));
    roles.put("user3", new HashSet<>(Arrays.asList("author")));
                                                             
    perm.put("admin", new HashSet<>(Arrays.asList("")));
    perm.put("editor", new HashSet<>(Arrays.asList("articles:")));
    perm.put("author", 
      new HashSet<>(Arrays.asList("articles:compose", 
      "articles:save")));
}
```

让我们继续并覆盖doGetAuthenticationInfo()：

```java
protected AuthenticationInfo 
  doGetAuthenticationInfo(AuthenticationToken token)
  throws AuthenticationException {
                                                                 
    UsernamePasswordToken uToken = (UsernamePasswordToken) token;
                                                                
    if(uToken.getUsername() == null
      || uToken.getUsername().isEmpty()
      || !credentials.containsKey(uToken.getUsername())) {
          throw new UnknownAccountException("username not found!");
    }
                                        
    return new SimpleAuthenticationInfo(
      uToken.getUsername(), 
      credentials.get(uToken.getUsername()), 
      getName()); 
}
```

我们首先将提供的AuthenticationToken转换为UsernamePasswordToken。从uToken中，我们提取用户名 ( uToken.getUsername() ) 并使用它从数据库中获取用户凭证(密码)。

如果没有找到记录——我们抛出一个UnknownAccountException，否则我们使用凭证和用户名来构造一个从该方法返回的SimpleAuthenticatioInfo对象。

如果用户凭据使用盐进行哈希处理，我们需要返回一个带有相关盐的SimpleAuthenticationInfo ：

```java
return new SimpleAuthenticationInfo(
  uToken.getUsername(), 
  credentials.get(uToken.getUsername()), 
  ByteSource.Util.bytes("salt"), 
  getName()
);
```

我们还需要覆盖doGetAuthorizationInfo()以及getRoleNamesForUser()和getPermissions()。

最后，让我们将自定义领域插入到securityManager中。我们需要做的就是将上面的IniRealm替换为我们的自定义领域，并将其传递给DefaultSecurityManager的构造函数：

```java
Realm realm = new MyCustomRealm();
SecurityManager securityManager = new DefaultSecurityManager(realm);
```

代码的每个其他部分都与以前相同。这就是我们使用自定义领域正确配置securityManager所需的全部内容。

现在的问题是——框架如何匹配凭据？

默认情况下，JdbcRealm使用SimpleCredentialsMatcher ，它仅通过比较AuthenticationToken和AuthenticationInfo中的凭据来检查是否相等。

如果我们对密码进行哈希处理，则需要通知框架改用HashedCredentialsMatcher。可以在[此处](https://shiro.apache.org/realm.html#Realm-HashingCredentials)找到具有散列密码的领域的 INI 配置。

## 7. 注销

现在我们已经对用户进行了身份验证，是时候实现注销了。这只需调用一个方法即可完成——该方法会使用户会话无效并将用户注销：

```java
currentUser.logout();
```

## 8.会话管理

该框架自然带有其会话管理系统。如果在 web 环境中使用，它默认为HttpSession实现。

对于独立应用程序，它使用其企业会话管理系统。好处是即使在桌面环境中，你也可以像在典型的 Web 环境中一样使用会话对象。

让我们看一个快速示例并与当前用户的会话进行交互：

```java
Session session = currentUser.getSession();                
session.setAttribute("key", "value");                      
String value = (String) session.getAttribute("key");       
if (value.equals("value")) {                               
    log.info("Retrieved the correct value! [" + value + "]");
}
```

## 9. 用于 Spring Web 应用程序的 Shiro

到目前为止，我们已经概述了 Apache Shiro 的基本结构，并且我们已经在桌面环境中实现了它。让我们继续将框架集成到Spring Boot应用程序中。

请注意，这里的主要焦点是 Shiro，而不是 Spring 应用程序——我们只打算用它来支持一个简单的示例应用程序。

### 9.1. 依赖关系

首先，我们需要将Spring Boot父级依赖项添加到我们的pom.xml中：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
</parent>
```

接下来，我们必须将以下依赖项添加到同一个pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>${apache-shiro-core-version}</version>
</dependency>
```

### 9.2. 配置

将shiro-spring-boot-web-starter依赖项添加到我们的pom.xml将默认配置 Apache Shiro 应用程序的一些功能，例如SecurityManager。

但是，我们仍然需要配置Realm和 Shiro 安全过滤器。我们将使用上面定义的相同自定义领域。

因此，在运行Spring Boot应用程序的主类中，让我们添加以下Bean定义：

```java
@Bean
public Realm realm() {
    return new MyCustomRealm();
}
    
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition filter
      = new DefaultShiroFilterChainDefinition();

    filter.addPathDefinition("/secure", "authc");
    filter.addPathDefinition("/", "anon");

    return filter;
}
```

在ShiroFilterChainDefinition中，我们将authc过滤器应用于/secure路径，并使用 Ant 模式将anon过滤器应用于其他路径。

默认情况下，authc和anon过滤器都会出现在 Web 应用程序中。可以在[此处](https://shiro.apache.org/web.html#Web-DefaultFilters)找到其他默认过滤器。

如果我们没有定义Realm bean，ShiroAutoConfiguration将默认提供一个IniRealm实现，期望在src/main/resources或src/main/resources/META-INF中找到一个shiro.ini文件。

如果我们不定义ShiroFilterChainDefinition bean，框架会保护所有路径并将登录 URL 设置为login.jsp。

我们可以通过将以下条目添加到我们的application.properties来更改此默认登录 URL 和其他默认值：

```plaintext
shiro.loginUrl = /login
shiro.successUrl = /secure
shiro.unauthorizedUrl = /login
```

现在authc过滤器已应用于/secure，所有对该路由的请求都需要表单身份验证。

### 9.3. 身份验证和授权

让我们创建一个具有以下路径映射的ShiroSpringController ： /index、/login、/logout和/secure。

login()方法是我们实现实际用户身份验证的地方，如上所述。如果身份验证成功，用户将被重定向到安全页面：

```java
Subject subject = SecurityUtils.getSubject();

if(!subject.isAuthenticated()) {
    UsernamePasswordToken token = new UsernamePasswordToken(
      cred.getUsername(), cred.getPassword(), cred.isRememberMe());
    try {
        subject.login(token);
    } catch (AuthenticationException ae) {
        ae.printStackTrace();
        attr.addFlashAttribute("error", "Invalid Credentials");
        return "redirect:/login";
    }
}

return "redirect:/secure";
```

现在在secure()实现中，currentUser是通过调用SecurityUtils.getSubject() 获得的。用户的角色和权限以及用户的主体被传递到安全页面：

```java
Subject currentUser = SecurityUtils.getSubject();
String role = "", permission = "";

if(currentUser.hasRole("admin")) {
    role = role  + "You are an Admin";
} else if(currentUser.hasRole("editor")) {
    role = role + "You are an Editor";
} else if(currentUser.hasRole("author")) {
    role = role + "You are an Author";
}

if(currentUser.isPermitted("articles:compose")) {
    permission = permission + "You can compose an article, ";
} else {
    permission = permission + "You are not permitted to compose an article!, ";
}

if(currentUser.isPermitted("articles:save")) {
    permission = permission + "You can save articles, ";
} else {
    permission = permission + "nYou can not save articles, ";
}

if(currentUser.isPermitted("articles:publish")) {
    permission = permission  + "nYou can publish articles";
} else {
    permission = permission + "nYou can not publish articles";
}

modelMap.addAttribute("username", currentUser.getPrincipal());
modelMap.addAttribute("permission", permission);
modelMap.addAttribute("role", role);

return "secure";
```

我们完成了。这就是我们如何将 Apache Shiro 集成到Spring Boot应用程序中。

另外请注意，该框架提供了额外的[注解](https://shiro.apache.org/spring.html)，可以与过滤器链定义一起使用来保护我们的应用程序。

## 10.JEE集成

将 Apache Shiro 集成到 JEE 应用程序中只是配置web.xml文件的问题。像往常一样，配置期望shiro.ini位于类路径中。[此处](https://shiro.apache.org/web.html#Web-{{web.xml}})提供了详细的示例配置。此外，可以在[此处](https://shiro.apache.org/web.html#Web-JSP%2FGSPTagLibrary)找到 JSP 标记。

## 11.总结

在本教程中，我们了解了 Apache Shiro 的身份验证和授权机制。我们还关注了如何定义自定义领域并将其插入到SecurityManager中。
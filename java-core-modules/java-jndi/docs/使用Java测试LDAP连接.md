## 1. 概述

在本教程中，我们将创建一个[CLI](https://baeldung.com/java-run-jar-with-arguments)应用程序来测试与任何[LDAP 身份验证](https://www.baeldung.com/java-ldap-auth)服务器的连接。我们不会使用 LDAP 来保护我们的应用程序，因为这可以使用[Spring Security LDAP](https://www.baeldung.com/spring-security-ldap)来更好地完成，例如。

即使在开发使用它们的应用程序之前，拥有一个工具来快速检查 LDAP 连接的有效性也是很有用的。在开发应用程序之间的某种集成时，它也很有用，尤其是在设置阶段。我们 将使用核心Java类来完成它。所以不需要额外的依赖。

## 2. LDAPJava客户端

让我们从创建我们唯一的类LdapConnectionTool开始。我们将从主要方法开始。为了简单起见，我们所有的逻辑都将放在这里：

```java
public class LdapConnectionTool {
    public static void main(String[] args) {
        // ...
    }
}

```

首先，我们将参数作为[系统属性](https://www.baeldung.com/java-system-get-property-vs-system-getenv)传递。我们将为工厂( LdapCtxFactory)和authType ( simple ) 变量使用默认值。LdapCtxFactory是负责连接到服务器和填充用户属性的整个过程的核心Java类。简单的身份验证类型意味着我们的密码将以明文形式发送。同样，我们会将查询变量默认为用户，因此我们可以指定其中之一或两者。我们稍后会看到使用细节：

```java
String factory = System.getProperty("factory", "com.sun.jndi.ldap.LdapCtxFactory");
String authType = System.getProperty("authType", "simple");
String url = System.getProperty("url");
String user = System.getProperty("user");
String password = System.getProperty("password");
String query = System.getProperty("query", user);
```

接下来，我们将创建我们的环境映射，它包含使用InitialDirContext进行连接所需的所有属性：

```java
Hashtable<String, String> env = new Hashtable<String, String>();
env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
env.put(Context.SECURITY_AUTHENTICATION, authType);
env.put(Context.PROVIDER_URL, url);
```

我们不想要求用户和密码，因为某些服务器允许匿名访问：

```java
if (user != null) {
    env.put(Context.SECURITY_PRINCIPAL, user);
    env.put(Context.SECURITY_CREDENTIALS, password);
}
```

在测试连接时，我们通常会传递错误的 URL 或者服务器根本没有响应。由于默认客户端行为会无限期阻塞，直到收到响应，因此我们将定义超时参数。等待时间以毫秒为单位定义：

```java
env.put("com.sun.jndi.ldap.read.timeout", "5000");
env.put("com.sun.jndi.ldap.connect.timeout", "5000");
```

之后，我们尝试与InitialDirContext的新实例建立连接，并进行基本的异常处理。这是必不可少的，因为我们将使用它来诊断常见问题。同样，由于我们正在开发 CLI 应用程序，因此我们将消息打印到标准输出：

```java
DirContext context = null;
try {
    context = new InitialDirContext(env);
    System.out.println("success");
    // ...
} catch (NamingException e) {
    System.out.println(e.getMessage());
} finally {
    context.close();
}
```

最后，我们使用上下文变量来查询可选查询产生的所有属性：

```java
if (query != null) {
    Attributes attributes = context.getAttributes(query);
    NamingEnumeration<? extends Attribute> all = attributes.getAll();
    while (all.hasMoreElements()) {
        Attribute next = all.next();

        String key = next.getID();
        Object value = next.get();

        System.out.println(key + "=" + value);
    }
}
```

## 3. 常见错误

在本节中，我们将回顾尝试连接到服务器时遇到的一些常见错误和错误消息：

-   错误的基本 DN：如果我们没有正确设置基本 DN，我们将得到“错误代码 49 – 无效凭证”。由于每个服务器都有自己的结构，我们应该始终首先检查它，因为此消息可能会产生误导。
-   没有匿名连接：如果我们不将服务器配置为允许匿名访问，我们将收到错误“ERR_229 无法验证用户”。

## 4.用法

现在我们都设置好了，我们可以使用我们的应用程序了。首先，让我们将[其构建为 jar](https://www.baeldung.com/java-create-jar)，将其重命名为ldap-connection-tool.jar，然后尝试以下示例之一。请注意，这些值完全取决于我们的服务器配置。

使用用户和密码连接：

```bash
java -cp ldap-connection-tool.jar 
-Durl=ldap://localhost:389 
-Duser=uid=gauss,dc=baeldung,dc=com 
-Dpassword=password 
com.baeldung.jndi.ldap.connectionTool.LdapConnectionTool
```

仅指定用于快速连接测试的服务器 URL：

```bash
java -cp ldap-connection-tool.jar 
-Durl=ldap://localhost:389 
com.baeldung.jndi.ldap.connectionTool.LdapConnectionTool
```

此外，指定查询 以及用户和密码，我们可以连接到特定用户但查询另一个用户。如果我们需要以管理员身份连接，例如，在执行查询之前，这很有用。同样，如果我们与具有足够权限的用户连接，我们可以看到受保护的属性，例如密码。

最后，在处理简单参数时，将系统属性作为输入传递是很好的。但是，还有更优雅的方式来开发 CLI 应用程序，例如[Spring Shell](https://www.baeldung.com/spring-shell-cli)。对于更复杂的事情，我们应该使用类似的东西。

## 5.总结

在本文中，我们创建了一个可以连接到 LDAP 服务器并运行连接测试的 CLI 应用程序。此外，单元测试中还有更多应用程序使用示例。
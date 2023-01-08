## 1. 概述

在本教程中，我们将了解默认情况下禁用的Java内置安全基础设施。具体来说，我们将检查其主要组件、扩展点和配置。

## 2. SecurityManager实战

这可能令人惊讶，但默认的SecurityManager设置不允许 许多标准操作：

```java
System.setSecurityManager(new SecurityManager());
new URL("http://www.google.com").openConnection().connect();
```

在这里，我们以编程方式使用默认设置启用安全监督并尝试连接到 google.com。

然后我们得到以下异常：

```bash
java.security.AccessControlException: access denied ("java.net.SocketPermission"
  "www.google.com:80" "connect,resolve")
```

标准库中还有许多其他用例——例如，读取系统属性、读取环境变量、打开文件、反射和更改语言环境，仅举几例。

## 3.用例

这种安全基础设施从Java1.0 开始就可用。这是一个小应用程序——嵌入到浏览器中的Java应用程序——非常普遍的时代。自然地，有必要限制他们对系统资源的访问。

如今，小程序已经过时了。但是，当存在第三方代码在受保护环境中执行的情况时，安全实施仍然是一个实际概念。

例如，假设我们有一个 Tomcat 实例，第三方客户端可以在其中托管他们的 Web 应用程序。我们不想让他们执行像System.exit()这样的操作，因为这会影响其他应用程序，甚至可能影响整个环境。

## 4.设计

### 4.1. 安全经理

内置安全基础结构中的主要组件之一是[java.lang SecurityManager](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/SecurityManager.html)。它有几个checkXxx方法，比如checkConnect，它授权我们在上面的测试中尝试连接到谷歌。它们都委托给checkPermission(java.security.Permission)方法。

### 4.2. 允许

java.security.Permission实例代表授权请求。标准 JDK 类为所有潜在的危险操作(如读/写文件、打开套接字等)创建它们，并将它们交给SecurityManager以获得适当的授权。

### 4.3. 配置

我们以特殊的策略格式定义权限。这些权限采用授予条目的形式：

```plaintext
grant codeBase "file:${{java.ext.dirs}}/" {
    permission java.security.AllPermission;
};
```

上面的codeBase规则是可选的。我们可以在那里完全不指定任何字段或使用signedBy(与密钥库中的相应证书集成)或主体(通过javax.security.auth.Subject附加到当前线程的java.security.Principal)。我们可以使用这些规则的任意组合。

默认情况下，JVM 加载位于 < java.home>/lib/security/java.policy的公共系统策略文件。如果我们在<user.home>/.java.policy中定义了任何用户本地策略 ，JVM 会将其附加到系统策略。

也可以通过命令行指定策略文件： – Djava.security.policy=/my/policy-file。这样我们就可以将策略附加到先前加载的系统和用户策略中。

有一个特殊的语法用于替换所有系统和用户策略(如果有的话)——双等号：—— Djava.security.policy==/my/policy-file

## 5.例子

让我们定义一个自定义权限：

```java
public class CustomPermission extends BasicPermission {
    public CustomPermission(String name) {
        super(name);
    }

    public CustomPermission(String name, String actions) {
        super(name, actions);
    }
}
```

以及应该保护的共享服务：

```java
public class Service {

    public static final String OPERATION = "my-operation";

    public void operation() {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new CustomPermission(OPERATION));
        }
        System.out.println("Operation is executed");
    }
}
```

如果我们尝试在启用安全管理器的情况下运行它，则会抛出异常：

```plaintext
java.security.AccessControlException: access denied
  ("com.baeldung.security.manager.CustomPermission" "my-operation")

    at java.security.AccessControlContext.checkPermission(AccessControlContext.java:472)
    at java.security.AccessController.checkPermission(AccessController.java:884)
    at java.lang.SecurityManager.checkPermission(SecurityManager.java:549)
    at com.baeldung.security.manager.Service.operation(Service.java:10)
```

我们可以创建包含以下内容的<user.home>/.java.policy文件并尝试重新运行应用程序：

```plaintext
grant codeBase "file:<our-code-source>" {
    permission com.baeldung.security.manager.CustomPermission "my-operation";
};
```

它现在工作得很好。

## 六，总结

在本文中，我们检查了内置 JDK 安全系统的组织方式以及我们如何扩展它。尽管目标用例相对较少，但了解它还是有好处的。
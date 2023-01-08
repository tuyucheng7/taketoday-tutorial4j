## 1. 概述

[Java 身份验证和授权服务](https://docs.oracle.com/en/java/javase/11/security/java-authentication-and-authorization-service-jaas-reference-guide.html)(JAAS) 是一个JavaSE 低级安全框架，可将安全模型从基于代码的安全性扩展到基于用户的安全性。我们可以将 JAAS 用于两个目的：

-   身份验证：识别当前运行代码的实体
-   授权：一旦通过身份验证，确保该实体具有执行敏感代码所需的访问控制权限或权限

在本教程中，我们将介绍如何通过实施和配置其各种 API(尤其是LoginModule)在示例应用程序中设置 JAAS 。

## 2. JAAS 的工作原理

在应用程序中使用 JAAS 时，涉及多个 API：

-   CallbackHandler：用于收集用户凭据，并在创建LoginContext时可选择提供
-   Configuration：负责加载LoginModule实现，可以在创建LoginContext时选择性地提供
-   LoginModule : 有效地用于验证用户

我们将使用配置API的默认实现，并为CallbackHandler和 LoginModule API提供我们自己的实现。

## 3.提供CallbackHandler实现

在深入研究LoginModule实现之前，我们首先需要为CallbackHandler接口提供一个实现，该接口用于收集用户凭证。

它只有一个方法handle()，它接受一个Callback数组。此外，JAAS 已经提供了许多回调实现，我们将分别使用NameCallback和PasswordCallback来收集用户名和密码。

让我们看看我们对CallbackHandler接口的实现：

```java
public class ConsoleCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        Console console = System.console();
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) callback;
                nameCallback.setName(console.readLine(nameCallback.getPrompt()));
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword(console.readPassword(passwordCallback.getPrompt()));
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
```

因此，为了提示和读取用户名，我们使用了：

```java
NameCallback nameCallback = (NameCallback) callback;
nameCallback.setName(console.readLine(nameCallback.getPrompt()));
```

同样，提示和读取密码：

```java
PasswordCallback passwordCallback = (PasswordCallback) callback;
passwordCallback.setPassword(console.readPassword(passwordCallback.getPrompt()));
```

稍后，我们将看到如何在实现LoginModule时调用CallbackHandler。

## 4.提供LoginModule实现

为简单起见，我们将提供一个存储硬编码用户的实现。所以，我们称它为InMemoryLoginModule：

```java
public class InMemoryLoginModule implements LoginModule {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
    
    private boolean loginSucceeded = false;
    private Principal userPrincipal;
    //...
}
```

在接下来的小节中，我们将给出更重要的方法的实现：initialize()、login()和commit()。

### 4.1. 初始化()

LoginModule首先被加载，然后用Subject和 CallbackHandler初始化。此外，LoginModule可以使用一个Map在它们之间共享数据，另一个Map用于存储私有配置数据：

```java
public void initialize(
  Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
}
```

### 4.2. 登录()

在login()方法中，我们调用带有NameCallback和PasswordCallback的 CallbackHandler.handle()方法来提示并获取用户名和密码。然后，我们将这些提供的凭据与硬编码的凭据进行比较：

```java
@Override
public boolean login() throws LoginException {
    NameCallback nameCallback = new NameCallback("username: ");
    PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
    try {
        callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        String username = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        if (USERNAME.equals(username) && PASSWORD.equals(password)) {
            loginSucceeded = true;
        }
    } catch (IOException | UnsupportedCallbackException e) {
        //...
    }
    return loginSucceeded;
}
```

login ()方法应该为成功的操作返回true ，为失败的登录返回false。

### 4.3. 犯罪()

如果对LoginModule#login 的所有调用都 成功，我们将使用额外的Principal更新Subject：

```java
@Override
public boolean commit() throws LoginException {
    if (!loginSucceeded) {
        return false;
    }
    userPrincipal = new UserPrincipal(username);
    subject.getPrincipals().add(userPrincipal);
    return true;
}
```

否则，将调用abort()方法。

此时，我们的LoginModule实现已准备就绪，需要对其进行配置，以便使用配置服务提供程序动态加载它。

## 5.登录模块配置

JAAS 使用配置服务提供者在运行时加载LoginModule。默认情况下，它提供并使用ConfigFile实现，其中LoginModule通过登录文件进行配置。例如，这是用于我们的LoginModule 的文件的内容：

```plaintext
jaasApplication {
   loginmodule.jaas.cn.tuyucheng.taketoday.InMemoryLoginModule required debug=true;
};
```

如我们所见，我们提供了LoginModule实现的完全限定类名、一个必需的标志和一个调试选项。

最后请注意，我们还可以通过java.security.auth.login.config系统属性指定登录文件：

```bash
$ java -Djava.security.auth.login.config=src/main/resources/jaas/jaas.login.config
```

我们还可以通过Java 安全文件${java.home}/jre/lib/security/java.security中的属性login.config.url指定一个或多个登录文件：

```plaintext
login.config.url.1=file:${user.home}/.java.login.config
```

## 6.认证

首先，应用程序通过创建[LoginContext](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/security/auth/login/LoginContext.html)实例来初始化身份验证过程。为此，我们可以查看完整的构造函数以了解我们需要什么作为参数：

```java
LoginContext(String name, Subject subject, CallbackHandler callbackHandler, Configuration config)
```

-   name ：用作仅加载相应LoginModule的索引
-   subject：代表想要登录的用户或服务
-   callbackHandler：负责将用户凭据从应用程序传递到LoginModule
-   config : 负责加载name参数对应的LoginModule

在这里，我们将使用重载的构造函数，我们将在其中提供我们的CallbackHandler实现：

```java
LoginContext(String name, CallbackHandler callbackHandler)
```

现在我们有了一个CallbackHandler和一个已配置的LoginModule，我们可以通过初始化一个LoginContext对象来启动身份验证过程：

```java
LoginContext loginContext = new LoginContext("jaasApplication", new ConsoleCallbackHandler());
```

此时，我们可以调用login()方法来验证用户：

```java
loginContext.login();
```

login ()方法依次创建我们的LoginModule的新实例并调用其login()方法。并且，在身份验证成功后，我们可以检索经过身份验证的Subject：

```java
Subject subject = loginContext.getSubject();
```

现在，让我们运行一个连接了LoginModule 的示例应用程序：

```bash
$ mvn clean package
$ java -Djava.security.auth.login.config=src/main/resources/jaas/jaas.login.config 
    -classpath target/core-java-security-2-0.1.0-SNAPSHOT.jar jaas.cn.tuyucheng.taketoday.JaasAuthentication
```

当系统提示我们提供用户名和密码时，我们将使用testuser和testpassword作为凭据。

## 七、授权

当用户首次连接并与 AccessControlContext 关联时，授权开始发挥作用。使用Java安全策略，我们可以向Principal授予一个或多个访问控制权限。然后我们可以通过调用SecurityManager#checkPermission 方法来阻止对敏感代码的访问：

```java
SecurityManager.checkPermission(Permission perm)
```

### 7.1. 定义权限

访问控制权或许可是对资源执行操作的能力。我们可以通过继承Permission抽象类来实现权限。为此，我们需要提供资源名称和一组可能的操作。例如，我们可以使用FilePermission 来配置对文件的访问控制权限。可能的操作有read、write、execute等等。对于不需要操作的场景，我们可以简单地使用BasicPermision。

接下来，我们将通过ResourcePermission类提供权限的实现，用户可能有权访问资源：

```java
public final class ResourcePermission extends BasicPermission {
    public ResourcePermission(String name) {
        super(name);
    }
}
```

稍后，我们将通过Java安全策略为该权限配置一个条目。

### 7.2. 授予权限

通常，我们不需要知道策略文件的语法，因为我们总是可以使用[策略工具](https://docs.oracle.com/javase/8/docs/technotes/guides/security/PolicyGuide.html)来创建一个。让我们看一下我们的策略文件：

```plaintext
grant principal com.sun.security.auth.UserPrincipal testuser {
    permission jaas.cn.tuyucheng.taketoday.ResourcePermission "test_resource"
};
```

在此示例中，我们已将test_resource权限授予testuser用户。

### 7.3. 检查权限

一旦对Subject进行了身份验证并配置了权限，我们就可以通过调用Subject#doAs 或Subject#doAsPrivileged 静态方法来检查访问权限。为此，我们将提供一个PrivilegedAction，我们可以在其中保护对敏感代码的访问。在run()方法中，我们调用SecurityManager#checkPermission 方法来确保通过身份验证的用户具有test_resource权限：

```java
public class ResourceAction implements PrivilegedAction {
    @Override
    public Object run() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ResourcePermission("test_resource"));
        }
        System.out.println("I have access to test_resource !");
        return null;
    }
}
```

最后一件事是调用Subject#doAsPrivileged 方法：

```java
Subject subject = loginContext.getSubject();
PrivilegedAction privilegedAction = new ResourceAction();
Subject.doAsPrivileged(subject, privilegedAction, null);
```

与身份验证一样，我们将运行一个简单的授权应用程序，除了LoginModule之外，我们还提供一个权限配置文件：

```bash
$ mvn clean package
$ java -Djava.security.manager -Djava.security.policy=src/main/resources/jaas/jaas.policy 
    -Djava.security.auth.login.config=src/main/resources/jaas/jaas.login.config 
    -classpath target/core-java-security-2-0.1.0-SNAPSHOT.jar jaas.cn.tuyucheng.taketoday.JaasAuthorization
```

## 八、总结

在本文中，我们通过探索主要类和接口并展示如何配置它们，展示了如何实现 JAAS。特别是，我们实现了服务提供者LoginModule。
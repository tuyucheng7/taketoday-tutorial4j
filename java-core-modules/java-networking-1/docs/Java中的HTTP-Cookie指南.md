## 1. 概述

在本文中，我们将探索Java网络编程的低级操作。我们将更深入地了解 Cookie。

Java 平台附带内置网络支持，捆绑在java.net包中：

```java
import java.net.;
```

## 2. HTTP Cookies

每当客户端向服务器发送 HTTP 请求并收到响应时，服务器就会忘记该客户端。下次客户端再次请求时，将被视为一个全新的客户端。

然而，正如我们所知，cookie 可以在客户端和服务器之间建立会话，以便服务器可以跨多个请求响应对记住客户端。

从本节开始，我们将学习如何使用 cookie 来增强Java网络编程中的客户端-服务器通信。

java.net包中用于处理 cookie 的主要类是CookieHandler。还有其他辅助类和接口，例如CookieManager、CookiePolicy、CookieStore和HttpCookie。

## 3. CookieHandler类

考虑这种情况；我们正在与http://baeldung.com或任何其他使用 HTTP 协议的 URL 的服务器通信，URL 对象将使用称为 HTTP 协议处理程序的引擎。

此 HTTP 协议处理程序检查系统中是否有默认的CookieHandler实例。如果有，则调用它来负责状态管理。

所以CookieHandler类的目的是为了 HTTP 协议处理程序的好处提供回调机制。

CookieHandler是一个抽象类。它有一个静态的getDefault()方法，可以调用它来检索当前的
CookieHandler安装，或者我们可以调用setDefault(CookieHandler)来设置我们自己的。请注意，调用setDefault会在系统范围内安装一个CookieHandler对象。

它还具有put(uri, responseHeaders)用于将任何 cookie 保存到 cookie 存储。这些 cookie 是从来自给定 URI 的 HTTP 响应的响应标头中检索的。每次收到响应时都会调用它。

相关的 API 方法 - get(uri,requestHeaders)检索保存在给定 URI 下的 cookie，并将它们添加到requetHeaders中。它在发出请求之前被调用。

这些方法都必须在具体的CookieHandler类中实现。此时，CookieManager类就值得我们注意了。此类为大多数常见用例提供了CookieHandler类的完整实现。

在接下来的两节中，我们将探讨CookieManager类；首先是默认模式，然后是自定义模式。

## 4.默认的CookieManager

要拥有完整的 cookie 管理框架，我们需要实现CookiePolicy和CookieStore。

CookiePolicy建立了接受和拒绝 cookie 的规则。我们当然可以更改这些规则以满足我们的需要。

Next – CookieStore 的作用正如其名称所暗示的那样，它具有保存和检索 cookie 的方法。当然，如果需要，我们也可以在这里调整存储机制。

让我们首先看一下默认值。创建默认的CookieHandler并将其设置为系统范围的默认值：

```java
CookieManager cm = new CookieManager();
CookieHandler.setDefault(cm);
```

我们应该注意，默认的CookieStore将具有易失性内存，即它只在 JVM 的生命周期内存在。为了更持久地存储 cookie，我们必须对其进行自定义。

当谈到CookiePolicy时，默认实现是CookiePolicy.ACCEPT_ORIGINAL_SERVER。这意味着如果通过代理服务器收到响应，则 cookie 将被拒绝。

## 5. 自定义CookieManager

现在让我们通过提供我们自己的CookiePolicy或CookieStore(或两者)实例来自定义默认的CookieManager 。

### 5.1. Cookie政策

为了方便， CookiePolicy提供了一些预定义的实现：

-   CookiePolicy.ACCEPT_ORIGINAL_SERVER – 只能保存来自原始服务器的 cookie(默认实现)
-   CookiePolicy.ACCEPT_ALL – 所有的 cookie 都可以保存，不管它们的来源
-   CookiePolicy.ACCEPT_NONE – 不能保存cookie

要简单地更改当前的CookiePolicy而无需实现我们自己的，我们在CookieManager实例上调用setCookiePolicy：

```java
CookieManager cm=new CookieManager();
cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
```

但我们可以做比这更多的定制。知道CookiePolicy.ACCEPT_ORIGINAL_SERVER的行为后，假设我们信任一个特定的代理服务器，并希望在原始服务器之上接受来自它的 cookie。

我们必须实现CookiePolicy接口并实现shouldAccept方法；我们将在这里通过添加所选代理服务器的域名来更改接受规则。

我们将新策略称为 – ProxyAcceptCookiePolicy。除了给定的代理地址之外，它基本上会拒绝其shouldAccept实现中的任何其他代理服务器，然后调用CookiePolicy.ACCEPT_ORIGINAL_SERVER的shouldAccept方法来完成实现：

```java
public class ProxyAcceptCookiePolicy implements CookiePolicy {
    private String acceptedProxy;

    public boolean shouldAccept(URI uri, HttpCookie cookie) {
        String host = InetAddress.getByName(uri.getHost())
          .getCanonicalHostName();
        if (HttpCookie.domainMatches(acceptedProxy, host)) {
            return true;
        }

        return CookiePolicy.ACCEPT_ORIGINAL_SERVER
          .shouldAccept(uri, cookie);
    }

    // standard constructors
}
```

当我们创建ProxyAcceptCookiePolicy的实例时，除了原始服务器之外，我们还传入了一个域地址字符串，我们希望从中接受 cookie。

然后我们将此实例设置为CookieManager实例的 cookie 策略，然后再将其设置为默认的 CookieHandler：

```java
CookieManager cm = new CookieManager();
cm.setCookiePolicy(new ProxyAcceptCookiePolicy("baeldung.com"));
CookieHandler.setDefault(cm);
```

这样，cookie 处理程序将接受来自原始服务器的所有 cookie 以及来自http://www.baeldung.com的所有 cookie 。

### 5.2. 饼干店

CookieManager为每个 HTTP 响应将 cookie 添加到CookieStore ，并为每个 HTTP 请求从CookieStore检索 cookie 。

默认的CookieStore实现没有持久性，它会在 JVM 重新启动时丢失所有数据。更像是计算机中的 RAM。

因此，如果我们希望我们的CookieStore实现像硬盘一样运行并在 JVM 重新启动时保留 cookie，我们必须自定义它的存储和检索机制。

需要注意的一件事是我们无法在创建后将CookieStore实例传递给CookieManager。我们唯一的选择是在创建CookieManager期间传递它，或者通过调用 new CookieManager().getCookieStore()并补充其行为来获取对默认实例的引用。

这是PersistentCookieStore的实现：

```java
public class PersistentCookieStore implements CookieStore, Runnable {
    private CookieStore store;

    public PersistentCookieStore() {
        store = new CookieManager().getCookieStore();
        // deserialize cookies into store
        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    @Override
    public void run() {
        // serialize cookies to persistent storage
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        store.add(uri, cookie);

    }
    
    // delegate all implementations to store object like above
}
```

请注意，我们在构造函数中检索了对默认实现的引用。

我们实现runnable以便我们可以添加一个在 JVM 关闭时运行的关闭钩子。在run方法中，我们将所有 cookie 保存到内存中。

我们可以将数据序列化到文件或任何合适的存储中。另请注意，在构造函数内部，我们首先将所有 cookie 从持久内存读取到 CookieStore中。这两个简单的特性使默认的CookieStore本质上是持久的(以一种简单的方式)。

## 六，总结

在本教程中，我们介绍了 HTTP cookie 并展示了如何以编程方式访问和操作它们。
## 1. 概述

在本教程中，我们将了解[通用安全服务 API (GSS API)](https://www.ietf.org/rfc/rfc2743.txt)以及我们如何在Java中实现它。我们将了解如何使用Java中的 GSS API 保护网络通信。

在此过程中，我们将创建简单的客户端和服务器组件，并使用 GSS API 保护它们。

## 2. 什么是GSS API？

那么，什么是通用安全服务 API？GSS API 为应用程序提供了一个通用框架，以可插入的方式使用不同的安全机制，如 Kerberos 、NTLM 和 SPNEGO。因此，它有助于应用程序将自身直接与安全机制分离。

需要澄清的是，此处的安全性涵盖身份验证、数据完整性和机密性。

### 2.1. 为什么我们需要GSS API？

Kerberos、NTLM 和 Digest-MD5 等安全机制在功能和实现方面大不相同。通常，支持其中一种机制的应用程序会发现切换到另一种机制非常困难。

这就是像 GSS API 这样的通用框架为应用程序提供抽象的地方。因此，使用 GSS API 的应用程序可以协商合适的安全机制并将其用于通信。所有这一切都无需实际实施任何特定于机制的细节。

### 2.2. GSS API如何工作？

GSS API 是一种基于令牌的机制。它通过在对等点之间交换安全令牌来工作。这种交换通常发生在网络上，但 GSS API 对这些细节是不可知的。

这些令牌由 GSS API 的特定实现生成和处理。这些令牌的语法和语义特定于对等点之间协商的安全机制：

[![截图-2019-08-12-at-12.08.43](https://www.baeldung.com/wp-content/uploads/2019/08/Screenshot-2019-08-12-at-12.08.43.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screenshot-2019-08-12-at-12.08.43.png)

GSS API 的中心主题围绕着安全上下文。我们可以通过令牌交换在对等点之间建立这种上下文。我们可能需要在对等点之间多次交换令牌来建立上下文。

一旦在两端成功建立，我们就可以使用安全上下文来安全地交换数据。这可能包括数据完整性检查和数据加密，具体取决于底层安全机制。

## 3.Java中的 GSS API 支持

Java 支持 GSS API 作为包“org.ietf.jgss”的一部分。包名称可能看起来很奇怪。这是因为GSS API 的Java绑定是在[IETF 规范](https://www.ietf.org/rfc/rfc2853.txt)中定义的。规范本身独立于安全机制。

Java GSS 的一种流行安全机制是 Kerberos v5。

### 3.1.JavaGSS API

让我们尝试了解一些构建JavaGSS 的核心 API：

-   GSSContext封装了GSS API安全上下文，并提供上下文下可用的服务
-   GSSCredential封装了建立安全上下文所必需的实体的 GSS API 凭据
-   GSSName封装 GSS API 主体实体，它为底层机制使用的不同名称空间提供抽象

除了上述接口外，还有几个重要的类需要注意：

-   GSSManager 充当其他重要 GSS API 类(如GSSName、GSSCredential和GSSContext )的工厂类
-   Oid表示通用对象标识符 (OID)，它们是 GSS API 中用于识别机制和名称格式的分层标识符
-   MessageProp包装属性以在诸如保护质量 (QoP) 和数据交换机密性等方面指示 GSSContext
-   ChannelBinding封装可选的通道绑定信息，用于加强提供对等实体身份验证的质量

### 3.2.JavaGSS 安全提供程序

虽然JavaGSS 定义了用于在Java中实现 GSS API 的核心框架，但它并未提供实现。Java 采用基于Provider的可插拔实现来实现包括JavaGSS 在内的安全服务。

可以有一个或多个在Java加密体系结构(JCA) 中注册的此类安全提供程序。每个安全提供者都可以实现一个或多个安全服务，例如JavaGSSAPI 和底层的安全机制。

JDK 附带一个默认的 GSS 提供程序。但是，我们可以使用具有不同安全机制的其他特定于供应商的 GSS 提供程序。[IBMJavaGSS](https://www.ibm.com/support/knowledgecenter/en/ssw_ibm_i_74/rzaha/rzahajgssover.htm)就是这样的提供者之一。我们必须向 JCA 注册此类安全提供程序才能使用它们。

此外，如果需要，我们可以使用可能的自定义安全机制实施我们自己的安全提供程序。然而，这在实践中几乎不需要。

## 4. GSS API 实例

现在，我们将通过示例了解JavaGSS 的实际应用。我们将创建一个简单的客户端和服务器应用程序。在 GSS 中，客户端通常被称为发起者，服务器被称为接受者。我们将在下面使用JavaGSS 和 Kerberos v5 进行身份验证。

### 4.1. 客户端和服务器的 GSS 上下文

首先，我们必须在应用程序的服务器端和客户端建立一个GSSContext 。

让我们首先看看我们如何在客户端做到这一点：

```java
GSSManager manager = GSSManager.getInstance();
String serverPrinciple = "HTTP/localhost@EXAMPLE.COM";
GSSName serverName = manager.createName(serverPrinciple, null);
Oid krb5Oid = new Oid("1.2.840.113554.1.2.2");
GSSContext clientContext = manager.createContext(
  serverName, krb5Oid, (GSSCredential)null, GSSContext.DEFAULT_LIFETIME);
clientContext.requestMutualAuth(true);
clientContext.requestConf(true);
clientContext.requestInteg(true);
```

这里发生了很多事情，让我们分解一下：

-   我们首先创建GSSManager的实例

-   然后我们使用这个实例来创建

    GSSContext

    ，并传递：

    -   表示服务器主体的GSSName，请在此处记下Kerberos 特定的主体名称
    -   要使用的机制的Oid ，这里是Kerberos v5
    -   发起者的凭据，此处为空表示将使用默认凭据
    -   既定上下文的生命周期

-   最后，我们为相互认证、机密性和数据完整性准备上下文

同样，我们必须定义服务器端上下文：

```java
GSSManager manager = GSSManager.getInstance();
GSSContext serverContext = manager.createContext((GSSCredential) null);
```

如我们所见，这比客户端上下文简单得多。这里唯一的区别是我们需要我们用作null的接受者的凭证。和以前一样，null表示将使用默认凭据。

### 4.2. GSS API 认证

虽然我们已经创建了服务器端和客户端GSSContext，但请注意，它们在此阶段尚未建立。

要建立这些上下文，我们需要交换特定于指定安全机制的令牌，即 Kerberos v5：

```java
// On the client-side
clientToken = clientContext.initSecContext(new byte[0], 0, 0);
sendToServer(clientToken); // This is supposed to be send over the network
		
// On the server-side
serverToken = serverContext.acceptSecContext(clientToken, 0, clientToken.length);
sendToClient(serverToken); // This is supposed to be send over the network
		
// Back on the client side
clientContext.initSecContext(serverToken, 0, serverToken.length);
```

这最终使上下文在两端建立：

```java
assertTrue(serverContext.isEstablished());
assertTrue(clientContext.isEstablished());
```

### 4.3. GSS API 安全通信

现在，我们已经在两端建立了上下文，我们可以开始发送具有完整性和机密性的数据：

```java
// On the client-side
byte[] messageBytes = "Baeldung".getBytes();
MessageProp clientProp = new MessageProp(0, true);
byte[] clientToken = clientContext.wrap(messageBytes, 0, messageBytes.length, clientProp);
sendToClient(serverToken); // This is supposed to be send over the network
       
// On the server-side 
MessageProp serverProp = new MessageProp(0, false);
byte[] bytes = serverContext.unwrap(clientToken, 0, clientToken.length, serverProp);
String string = new String(bytes);
assertEquals("Baeldung", string);
```

这里发生了一些事情，让我们分析一下：

-   MessageProp被客户端用来设置wrap方法和生成token
-   方法wrap还添加了数据的加密 MIC，MIC 被捆绑为令牌的一部分
-   该令牌被发送到服务器(可能通过网络调用)
-   服务器再次利用MessageProp设置解包方法并取回数据
-   此外，方法unwrap验证接收数据的 MIC，确保数据完整性

因此，客户端和服务器能够以完整性和机密性交换数据。

### 4.4. 示例的 Kerberos 设置

现在，通常期望像 Kerberos这样的 GSS 机制从现有的Subject获取凭证。这里的类Subject是一个 JAAS 抽象，表示像人或服务这样的实体。这通常在基于 JAAS 的身份验证期间填充。

但是，对于我们的示例，我们不会直接使用基于 JAAS 的身份验证。我们将让 Kerberos 直接获取凭据，在我们的例子中使用密钥表文件。有一个 JVM 系统参数可以实现：

```powershell
-Djavax.security.auth.useSubjectCredsOnly=false
```

但是，Sun Microsystem 提供的默认 Kerberos 实现依赖于 JAAS 来提供身份验证。

这听起来可能与我们刚才讨论的相反。请注意，我们可以在我们的应用程序中显式使用 JAAS 来填充Subject。或者将其留给底层机制直接进行身份验证，无论如何它都使用 JAAS。因此，我们需要为底层机制提供一个 JAAS 配置文件：

```powershell
com.sun.security.jgss.initiate  {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  keyTab=example.keytab
  principal="client/localhost"
  storeKey=true;
};
com.sun.security.jgss.accept  {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  keyTab=example.keytab
  storeKey=true
  principal="HTTP/localhost";
};
```

此配置非常简单，我们已将 Kerberos 定义为发起者和接受者所需的登录模块。此外，我们已配置为使用密钥表文件中的相应主体。我们可以将此 JAAS 配置作为系统参数传递给 JVM：

```powershell
-Djava.security.auth.login.config=login.conf
```

在这里，假设我们可以访问 Kerberos KDC。在 KDC 中，我们已经设置了所需的主体并获得了要使用的密钥表文件，假设为“example.keytab”。

此外，我们需要指向正确 KDC 的 Kerberos 配置文件：

```powershell
[libdefaults]
default_realm = EXAMPLE.COM
udp_preference_limit = 1
[realms]
EXAMPLE.COM = {
    kdc = localhost:52135
}
```

这个简单的配置定义了一个在端口 52135 上运行的 KDC，默认领域为 EXAMPLE.COM。我们可以将其作为系统参数传递给 JVM：

```powershell
-Djava.security.krb5.conf=krb5.conf
```

### 4.5. 运行示例

要运行该示例，我们必须使用上一节中讨论的 Kerberos 工件。

此外，我们需要传递所需的 JVM 参数：

```powershell
java -Djava.security.krb5.conf=krb5.conf 
  -Djavax.security.auth.useSubjectCredsOnly=false 
  -Djava.security.auth.login.config=login.conf 
  com.baeldung.jgss.JgssUnitTest
```

这足以让 Kerberos 使用来自 keytab 和 GSS 的凭据执行身份验证以建立上下文。

## 5. 现实世界中的 GSS API

虽然 GSS API 承诺通过可插入机制解决大量安全问题，但很少有用例得到更广泛的采用：

-   它在 SASL 中被广泛用作安全机制，尤其是在 Kerberos 是选择的底层机制的情况下。Kerberos 是一种广泛使用的身份验证机制，尤其是在企业网络中。利用基于 Kerberos 的基础架构对新应用程序进行身份验证非常有用。因此，GSS API 很好地弥补了这一差距。
-   它还与 SPNEGO 结合使用，以在事先不知道安全机制的情况下协商安全机制。就此而言，SPNEGO 在某种意义上是 GSS API 的一种伪机制。这在所有现代浏览器中得到广泛支持，使它们能够利用基于 Kerberos 的身份验证。

## 6. GSS API 比较

GSS API 在以可插入方式为应用程序提供安全服务方面非常有效。然而，这并不是在Java中实现这一目标的唯一选择。

让我们了解Java还必须提供什么以及它们与 GSS API 相比如何：

-   [Java 安全套接字扩展](https://docs.oracle.com/javase/9/security/java-secure-socket-extension-jsse-reference-guide.htm)(JSSE)：JSSE 是Java中的一组包，它实现了Java的安全套接字层 (SSL)。它提供数据加密、客户端和服务器身份验证以及消息完整性。与 GSS API 不同，JSSE 依赖于公钥基础设施 (PKI) 来工作。因此，GSS API 比 JSSE 更灵活、更轻量。
-   [Java 简单身份验证和安全层](https://docs.oracle.com/javase/9/security/introduction-java-sasl-api.htm)(SASL)：SASL 是用于 Internet 协议的身份验证和数据安全框架，可将它们与特定的身份验证机制分离。这在范围上类似于 GSS API。但是，Java GSS 通过可用的安全提供程序对底层安全机制的支持有限。

总的来说，GSS API 在以机制不可知的方式提供安全服务方面非常强大。但是，Java 中对更多安全机制的支持将进一步采用它。

## 七、总结

总而言之，在本教程中，我们了解了 GSS API 作为安全框架的基础知识。我们了解了 GSS 的JavaAPI 并了解了如何利用它们。在此过程中，我们创建了简单的客户端和服务器组件，它们执行相互身份验证并安全地交换数据。

此外，我们还了解了 GSS API 的实际应用以及Java中可用的替代方案。
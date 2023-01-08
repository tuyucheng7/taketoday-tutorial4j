## 1. 概述

在本教程中，我们将介绍[简单身份验证和安全层](https://tools.ietf.org/html/rfc4422)(SASL) 的基础知识。我们将了解Java如何支持采用 SASL 来保护通信。

在此过程中，我们将使用简单的客户端和服务器通信，并使用 SASL 对其进行保护。

## 2.什么是SASL？

SASL 是Internet 协议中用于身份验证和数据安全的框架。它旨在将 Internet 协议与特定的身份验证机制分离。随着我们的进行，我们将更好地理解这个定义的部分内容。

通信安全的需要是隐含的。让我们尝试在客户端和服务器通信的上下文中理解这一点。通常，客户端和服务器通过网络交换数据。双方必须相互信任并安全地发送数据。

### 2.1. SASL适用于哪些领域？

在应用程序中，我们可能会使用 SMTP 发送电子邮件并使用 LDAP 访问目录服务。但是这些协议中的每一个都可能支持另一种身份验证机制，例如 Digest-MD5 或 Kerberos。

如果有一种方法可以让协议更明确地交换身份验证机制呢？这正是 SASL 发挥作用的地方。支持 SASL 的协议可以始终支持任何 SASL 机制。

因此，应用程序可以协商合适的机制并采用该机制进行身份验证和安全通信。

### 2.2. SASL是如何工作的？

现在，我们已经了解了 SASL 在整体安全方案中的位置，让我们了解它是如何工作的。

SASL是一个挑战-响应框架。在这里，服务器向客户端发出质询，客户端根据质询发送响应。挑战和响应是任意长度的字节数组，因此可以携带任何特定于机制的数据。

[![SASL交易所](https://www.baeldung.com/wp-content/uploads/2019/09/SASL-Exchange.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/SASL-Exchange.jpg)

这种交换可以持续多次迭代，并最终在服务器不再发出进一步挑战时结束。

此外，客户端和服务器可以协商安全层后验证。所有后续通信都可以利用此安全层。但是，请注意，某些机制可能仅支持身份验证。

重要的是要了解 SASL仅提供用于交换质询和响应数据的框架。它没有提及有关数据本身或它们如何交换的任何信息。这些细节留给采用 SASL 的应用程序。

## 3.Java中的 SASL 支持

Java 中有一些 API支持使用 SASL开发客户端和服务器端应用程序。API 不依赖于实际机制本身。使用JavaSASL API 的应用程序可以根据所需的安全特性选择一种机制。

### 3.1.JavaSASL API

作为包“javax.security.sasl”的一部分，需要注意的关键接口是SaslServer和SaslClient。

SaslServer代表SASL的服务器端机制。

让我们看看如何实例化一个SaslServer：

```java
SaslServer ss = Sasl.createSaslServer(
  mechanism, 
  protocol, 
  serverName, 
  props, 
  callbackHandler);
```

我们使用工厂类Sasl来实例化SaslServer。createSaslServer方法接受几个参数：

-   机制——SASL 支持机制的 IANA 注册名称
-   protocol – 正在执行身份验证的协议的名称
-   serverName – 服务器的完全限定主机名
-   道具——一组用于配置身份验证交换的属性
-   callbackHandler – 所选机制使用的回调处理程序以获取更多信息

在上面，只有前两个是强制的，其余的都是可以为空的。

SaslClient 表示 SASL 的客户端机制。让我们看看如何实例化一个SaslClient：

```java
SaslClient sc = Sasl.createSaslClient(
  mechanisms, 
  authorizationId, 
  protocol, 
  serverName, 
  props,
  callbackHandler);
```

在这里，我们再次使用工厂类Sasl来实例化我们的SaslClient。createSaslClient接受的参数列表与以前几乎相同。

但是，存在一些细微差别：

-   机制——在这里，这是一个可以尝试的机制列表
-   authorizationId – 这是用于授权的依赖于协议的标识

其余参数的含义和可选性相似。

### 3.2.JavaSASL 安全提供程序

在JavaSASL API 之下是提供安全功能的实际机制。这些机制的实现由在[Java 加密体系结构](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm)(JCA)中注册的安全提供者提供。

可以有多个向 JCA 注册的安全提供程序。其中每一个都可以支持一个或多个 SASL 机制。

Java 附带 SunSASL 作为安全提供程序，它默认注册为 JCA 提供程序。但是，这可能会被删除或与任何其他可用的提供商重新订购。

此外，始终可以提供自定义安全提供程序。这将需要我们实现接口SaslClient和SaslServer。这样做，我们也可以实施我们的自定义安全机制！

## 4. SASL实例

现在我们已经了解了如何创建SaslServer和SaslClient，是时候了解如何使用它们了。我们将开发客户端和服务器组件。这些将迭代地交换挑战和响应以实现身份验证。我们将在此处的简单示例中使用 DIGEST-MD5 机制。

### 4.1. 客户端和服务器CallbackHandler

正如我们之前看到的，我们需要为SaslServer和SaslClient提供CallbackHandler的实现。现在，CallbackHandler是一个简单的接口，它定义了一个方法—— handle。此方法接受Callback数组。

在这里，回调为安全机制提供了一种从调用应用程序收集身份验证数据的方法。例如，安全机制可能需要用户名和密码。有相当多的回调实现可供使用，例如NameCallback和PasswordCallback 。

让我们看看如何为服务器定义一个CallbackHandler，首先：

```java
public class ServerCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] cbs) throws IOException, UnsupportedCallbackException {
        for (Callback cb : cbs) {
            if (cb instanceof AuthorizeCallback) {
                AuthorizeCallback ac = (AuthorizeCallback) cb;
                //Perform application-specific authorization action
                ac.setAuthorized(true);
            } else if (cb instanceof NameCallback) {
                NameCallback nc = (NameCallback) cb;
                //Collect username in application-specific manner
                nc.setName("username");
            } else if (cb instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) cb;
                //Collect password in application-specific manner
                pc.setPassword("password".toCharArray());
            } else if (cb instanceof RealmCallback) { 
                RealmCallback rc = (RealmCallback) cb; 
                //Collect realm data in application-specific manner 
                rc.setText("myServer"); 
            }
        }
    }
}
```

现在，让我们看看Callbackhandler的客户端：

```java
public class ClientCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] cbs) throws IOException, UnsupportedCallbackException {
        for (Callback cb : cbs) {
            if (cb instanceof NameCallback) {
                NameCallback nc = (NameCallback) cb;
                //Collect username in application-specific manner
                nc.setName("username");
            } else if (cb instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) cb;
                //Collect password in application-specific manner
                pc.setPassword("password".toCharArray());
            } else if (cb instanceof RealmCallback) { 
                RealmCallback rc = (RealmCallback) cb; 
                //Collect realm data in application-specific manner 
                rc.setText("myServer"); 
            }
        }
    }
}
```

为澄清起见，我们循环遍历回调数组并仅处理特定的回调。我们必须处理的是特定于使用的机制，这里是 DIGEST-MD5。

### 4.2. SASL 认证

所以，我们已经编写了我们的客户端和服务器CallbackHandler。我们还为 DIGEST-MD5 机制实例化了SaslClient和SaslServer。

现在是时候看看他们的行动了：

```java
@Test
public void givenHandlers_whenStarted_thenAutenticationWorks() throws SaslException {
    byte[] challenge;
    byte[] response;
 
    challenge = saslServer.evaluateResponse(new byte[0]);
    response = saslClient.evaluateChallenge(challenge);
 
    challenge = saslServer.evaluateResponse(response);
    response = saslClient.evaluateChallenge(challenge);
 
    assertTrue(saslServer.isComplete());
    assertTrue(saslClient.isComplete());
}
```

让我们试着了解这里发生了什么：

-   首先，我们的客户端从服务器获取默认挑战
-   然后客户评估挑战并准备回应
-   这种挑战-响应交换持续了一个周期
-   在此过程中，客户端和服务器使用回调处理程序来收集该机制所需的任何其他数据
-   这结束了我们的身份验证，但实际上，它可以迭代多个周期

挑战和响应字节数组的典型交换发生在网络上。但是，这里为了简单起见，我们假设本地通信。

### 4.3. SASL 安全通信

正如我们之前讨论的那样，SASL 是一个能够支持安全通信而不仅仅是身份验证的框架。然而，这只有在底层机制支持的情况下才有可能。

首先，让我们先检查一下我们是否能够协商安全通信：

```java
String qop = (String) saslClient.getNegotiatedProperty(Sasl.QOP);
 
assertEquals("auth-conf", qop);
```

这里，QOP代表保护质量。这是客户端和服务器在身份验证期间协商的内容。“auth-int”值表示身份验证和完整性。而“auth-conf”的值表示身份验证、完整性和机密性。

一旦我们有了安全层，我们就可以利用它来保护我们的通信。

让我们看看如何保护客户端中的传出通信：

```java
byte[] outgoing = "Baeldung".getBytes();
byte[] secureOutgoing = saslClient.wrap(outgoing, 0, outgoing.length);
 
// Send secureOutgoing to the server over the network
```

同样，服务器可以处理传入的通信：

```java
// Receive secureIncoming from the client over the network
byte[] incoming = saslServer.unwrap(secureIncoming, 0, netIn.length);
 
assertEquals("Baeldung", new String(incoming, StandardCharsets.UTF_8));
```

## 5. 现实世界中的 SASL

因此，我们现在对什么是 SASL 以及如何在Java中使用它有了一个清晰的了解。但是，通常情况下，这不是我们最终使用 SASL 的目的，至少在我们的日常工作中是这样。

正如我们之前看到的，SASL 主要用于 LDAP 和 SMTP 等协议。尽管如此，越来越多的应用程序开始使用 SASL——例如 Kafka。那么，我们如何使用 SASL 对此类服务进行身份验证呢？

假设我们已经为 SASL 配置了 Kafka Broker，并将 PLAIN 作为选择的机制。PLAIN 只是意味着它使用明文形式的用户名和密码组合进行身份验证。

现在让我们看看如何配置Java客户端以使用 SASL/PLAIN 对 Kafka Broker 进行身份验证。

我们首先提供一个简单的 JAAS 配置“kafka_jaas.conf”：

```powershell
KafkaClient {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="username"
  password="password";
};
```

我们在启动 JVM 时使用这个 JAAS 配置：

```powershell
-Djava.security.auth.login.config=kafka_jaas.conf
```

最后，我们必须添加一些属性以传递给我们的生产者和消费者实例：

```powershell
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
```

这里的所有都是它的。不过，这只是[Kafka 客户端配置](https://www.baeldung.com/kafka-connectors-guide)的一小部分。除了 PLAIN，Kafka 还支持 GSSAPI/Kerberos 进行身份验证。

## 6. 比较SASL

尽管 SASL 在提供一种机制中立的方式来验证和保护客户端与服务器通信方面非常有效。然而，SASL 并不是这方面唯一可用的解决方案。

Java 本身提供了其他机制来实现这个目标。我们将简要讨论它们并了解它们如何对抗 SASL：

-   [Java 安全套接字扩展](https://docs.oracle.com/javase/9/security/java-secure-socket-extension-jsse-reference-guide.htm)(JSSE)：JSSE 是Java中的一组包，它实现了Java的安全套接字层 (SSL)。它提供数据加密、客户端和服务器身份验证以及消息完整性。与 SASL 不同，JSSE 依赖于公钥基础设施 (PKI) 来工作。因此，SASL 比 JSSE 更灵活、更轻量。
-   [Java GSS API](https://docs.oracle.com/javase/9/security/java-generic-security-services-java-gss-api1.htm) (JGSS)：JGGS 是通用安全服务应用程序编程接口 (GSS-API) 的Java语言绑定。GSS-API 是应用程序访问安全服务的 IETF 标准。在Java中，在 GSS-API 下，Kerberos 是唯一受支持的机制。Kerberos 再次需要 Kerberised 基础设施才能工作。与 SASL 相比，这里的选择有限且重量级。

总的来说，SASL 是一个非常轻量级的框架，并通过可插入机制提供了广泛的安全特性。采用 SASL 的应用程序在实现正确的安全功能集方面有很多选择，具体取决于需要。

## 七、总结

总而言之，在本教程中，我们了解了提供身份验证和安全通信的 SASL 框架的基础知识。我们还讨论了Java中可用于实现 SASL 客户端和服务器端的 API。

我们看到了如何通过 JCA 提供程序使用安全机制。最后，我们还讨论了 SASL 在处理不同协议和应用程序时的用法。
## 一、概述

在本教程中，我们将了解 Kerberos 身份验证协议的基础知识。我们还将讨论与 Kerberos 相关的[SPNEGO需求。](https://tools.ietf.org/html/rfc4178)

最后，我们将了解如何使用[Spring Security Kerberos](https://spring.io/projects/spring-security-kerberos)扩展来创建支持 Kerberos 和 SPNEGO 的应用程序。

在我们继续之前，值得注意的是本教程将为那些不熟悉该领域的人介绍许多新术语。因此，我们将先花一些时间来了解场地。

## 2. 了解 Kerberos

**[Kerberos](https://web.mit.edu/kerberos/)**是麻省理工学院 (MIT) 在 80 年代初期开发的**一种网络身份验证协议。**正如您可能意识到的那样，它相对较旧并且经受住了时间的考验。Windows Server 广泛支持 Kerberos 作为身份验证机制，甚至将其设为默认身份验证选项。

从技术上讲，**Kerberos 是一种基于票证的身份验证协议**，它允许计算机网络中的节点相互识别自己。

### 2.1. Kerberos 的简单用例

让我们拟定一个假设情况来证明这一点。

假设用户通过他机器上的邮件客户端，需要从同一网络上另一台机器上的邮件服务器提取电子邮件。这里显然需要进行身份验证。邮件客户端和邮件服务器必须能够识别并相互信任才能安全通信。

Kerberos 在这方面如何帮助我们？**Kerberos 引入了一个称为密钥分发中心 (KDC) 的第三方**，它与网络中的每个节点都具有相互信任。让我们看看这在我们的案例中是如何工作的：

[![Kerberos 协议](https://www.baeldung.com/wp-content/uploads/2019/04/Kerberos-Protocol.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/Kerberos-Protocol.jpg)

### 2.2. Kerberos 协议的关键方面

虽然这听起来很深奥，但在不安全的网络上保护通信安全方面是非常简单和有创意的。这里提出的一些问题在 TLS 无处不在的时代是相当理所当然的！

虽然这里不可能详细讨论 Kerberos 协议，但让我们来看看一些重要的方面：

-   假定节点（客户端和服务器）与 KDC 之间的信任存在于同一领域中
-   **永远不会通过网络交换密码**
-   基于客户端和服务器可以使用仅与 KDC 共享的密钥解密消息这一事实，隐含了客户端和服务器之间的信任
-   **客户端和服务器之间的信任是相互的**
-   客户端可以缓存票证，重复使用直到过期，提供单点登录体验
-   Authenticator Messages 基于时间戳，因此只适合一次性使用
-   这里三方的时间肯定是比较同步的

虽然这只是这个漂亮的身份验证协议的冰山一角，但足以让我们继续学习教程。

## 3. 了解 SPNEGO

SPNEGO 代表[简单且受保护的 GSS-API 协商机制](https://tools.ietf.org/html/rfc4178)。好一个名字！让我们先看看 GSS-API 代表什么。[通用安全服务应用程序接口](https://tools.ietf.org/html/rfc2743)(GSS-API) 只是客户端和服务器以安全且与供应商无关的方式进行通信的 IETF 标准。

**SPNEGO 是 GSS-API 的一部分，供客户端和服务器协商选择**要使用的安全机制，例如 Kerberos 或 NTLM。

## 4. 为什么我们需要带有 Kerberos*的 SPNEGO ？*

正如我们在上一节中看到的，Kerberos 是一种主要在传输层 (TCP/UDP) 中运行的纯网络身份验证协议。虽然这对许多用例都有好处，但这不符合现代网络的要求。如果我们有一个运行在更高抽象之上的应用程序，比如 HTTP，就不可能直接使用 Kerberos。

这就是 SPNEGO 为我们提供帮助的地方。对于 Web 应用程序，通信主要发生在 Chrome 等 Web 浏览器和通过 HTTP 托管 Web 应用程序的 Tomcat 等 Web 服务器之间。如果启用，他们可以**通过 SPNEGO 将 Kerberos 作为一种安全机制进行协商，并通过 HTTP 将票证作为 SPNEGO 令牌进行交换**。

那么这如何改变我们前面提到的场景呢？让我们用 Web 浏览器替换简单的邮件客户端，用 Web 应用程序替换邮件服务器：

[![带有 SPNEGO 的 Kerberos](https://www.baeldung.com/wp-content/uploads/2019/04/Kerberos-with-SPNEGO.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/Kerberos-with-SPNEGO.jpg)

因此，与我们之前的图表相比，除了客户端和服务器之间的通信现在通过 HTTP 显式发生之外，这方面没有太大变化。让我们更好地理解这一点：

-   客户端机器根据 KDC 进行身份验证并缓存 TGT
-   客户机上的 Web 浏览器配置为使用 SPNEGO 和 Kerberos
-   Web 应用程序还配置为支持 SPNEGO 和 Kerberos
-   Web 应用程序向试图访问受保护资源的 Web 浏览器发起“协商”挑战
-   **服务票证包装为 SPNEGO 令牌并作为 HTTP 标头交换**

## 五、要求

在我们继续开发支持 Kerberos 身份验证模式的 Web 应用程序之前，我们必须收集一些基本设置。让我们快速完成这些任务。

### 5.1. 设置 KDC

为生产环境设置 Kerberos 环境超出了本教程的范围。不幸的是，这不是一项微不足道的任务，也很脆弱。有几个选项可用于获取 Kerberos 的实现，包括开源版本和商业版本：

-   MIT 使[Kerberos v5 的实现](http://web.mit.edu/Kerberos/dist/)可用于多个操作系统
-   [Apache Kerby](https://directory.apache.org/kerby/)是 Apache Directory 的扩展，它提供了 Java Kerberos 绑定
-   Microsoft 的 Windows Server 支持由 Active Directory 本机支持的 Kerberos v5
-   Heimdel 有一个[Kerberos v5 的实现](https://www.h5l.org/)

KDC 和相关基础设施的实际设置取决于提供商，应遵循其各自的文档。但是，[Apache Kerby 可以在 Docker 容器中运行](https://coheigea.blogspot.com/2018/06/running-apache-kerby-kdc-in-docker.html)，这使得它与平台无关。

### 5.2. 在 KDC 中设置用户

我们需要在 KDC 中设置两个用户——或者，他们称之为委托人。为此，我们可以使用“kadmin”命令行工具。假设我们在 KDC 数据库中创建了一个名为“baeldung.com”的领域，并以具有管理员权限的用户身份登录到“kadmin”。

我们将创建我们的第一个用户，我们希望通过网络浏览器对其进行身份验证，其中：

```powershell
$ kadmin: addprinc -randkey kchandrakant -pw password
Principal "kchandrakant@baeldung.com" created.复制
```

我们还需要向 KDC 注册我们的 Web 应用程序：

```powershell
$ kadmin: addprinc -randkey HTTP/demo.kerberos.baeldung.com@baeldung.com -pw password
Principal "HTTP/demo.kerberos.baeldung.com@baeldung.com" created.复制
```

请注意此处命名主体的约定，因为这必须与可从 Web 浏览器访问应用程序的域相匹配。当出现“协商”挑战时，Web**浏览器会自动尝试使用此约定创建服务主体名称 (SPN) 。**

我们还需要将其导出为 keytab 文件，以供 Web 应用程序使用：

```powershell
$ kadmin: ktadd -k baeldung.keytab HTTP/demo.kerberos.baeldung.com@baeldung.com复制
```

这应该给我们一个名为“baeldung.keytab”的文件。

### 5.3. 浏览器配置

我们需要启用用于访问“协商”身份验证方案的 Web 应用程序上受保护资源的 Web 浏览器。幸运的是，大多数现代网络浏览器（如 Chrome）默认支持“协商”作为身份验证方案。

此外，我们可以配置浏览器以提供“集成身份验证”。在这种模式下，当出现“协商”挑战时，浏览器会尝试使用主机中缓存的凭据，该凭据已经登录到 KDC 主体。但是，我们不会在这里使用这种模式来保持明确。

### 5.4. 域配置

可以理解的是，我们可能没有实际的域来测试我们的 Web 应用程序。但遗憾的是，我们不能使用 localhost 或 127.0.0.1 或任何其他具有 Kerberos 身份验证的 IP 地址。然而，有一个简单的解决方案，包括在“hosts”文件中设置条目，例如：

```powershell
demo.kerberos.bealdung.com 127.0.0.1复制
```

## 6. 春天来拯救我们！

最后，由于我们已经弄清楚了基础知识，现在是检验理论的时候了。但是，创建支持 SPNEGO 和 Kerberos 的 Web 应用程序不会很麻烦吗？如果我们使用 Spring，则不会。**Spring 有一个 Kerberos 扩展作为 Spring Security 的一部分，它支持 SPNEGO 与 Kerberos 的**无缝连接。

几乎我们所要做的只是在 Spring Security 中进行配置，以使用 Kerberos 启用 SPNEGO。我们将在此处使用 Java 样式的配置，但可以轻松设置 XML 配置。

### 6.1. Maven 依赖项

我们必须设置的第一件事是依赖项：

```xml
<dependency>
    <groupId>org.springframework.security.kerberos</groupId>
    <artifactId>spring-security-kerberos-web</artifactId>
    <version>${kerberos.extension.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.security.kerberos</groupId>
    <artifactId>spring-security-kerberos-client</artifactId>
    <version>${kerberos.extension.version}</version>
</dependency>复制
```

这些依赖项可从[Maven Central](https://search.maven.org/search?q=g:org.springframework.security.kerberos)下载。

### 6.2. SPNEGO 配置

*首先，SPNEGO 作为HTTPSecurity中的**过滤器*集成到 Spring Security中：

```java
 @Override
 public void configure(HttpSecurity http) throws Exception {
     AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
     http.addFilterBefore(spnegoAuthenticationProcessingFilter(authenticationManager),
         BasicAuthenticationFilter.class);
}复制
```

*这仅显示了配置 SPNEGO Filter*所需的部分，并不是完整的*HTTPSecurity*配置，应根据应用程序安全要求进行配置。

接下来，我们需要提供 SPNEGO *Filter*作为*Bean*：

```html
@Bean
public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
  AuthenticationManager authenticationManager) {
    SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
    filter.setAuthenticationManager(authenticationManager);
    return filter;
}复制
```

### 6.3. Kerberos 配置

另外，我们可以通过在 Spring Security中的*AuthenticationManagerBuilder中添加**AuthenticationProvider*来配置 Kerberos：

```java
 @Bean
 public AuthenticationManager authManager(HttpSecurity http) throws Exception {
     return http.getSharedObject(AuthenticationManagerBuilder.class)
         .authenticationProvider(kerberosAuthenticationProvider())
         .authenticationProvider(kerberosServiceAuthenticationProvider())
         .build();
 }复制
```

我们必须提供的第一件事是*KerberosAuthenticationProvider*作为*Bean*。这是*AuthenticationProvider*的实现，这是我们将*SunJaasKerberosClient*设置为*KerberosClient*的地方：

```java
@Bean
public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
    KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
    SunJaasKerberosClient client = new SunJaasKerberosClient();
    provider.setKerberosClient(client);
    provider.setUserDetailsService(userDetailsService());
    return provider;
}复制
```

接下来，我们还必须提供*KerberosServiceAuthenticationProvider*作为*Bean*。这是验证 Kerberos 服务票证或 SPNEGO 令牌的类：

```java
@Bean
public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
    KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
    provider.setTicketValidator(sunJaasKerberosTicketValidator());
    provider.setUserDetailsService(userDetailsService());
    return provider;
}复制
```

最后，我们需要提供一个*SunJaasKerberosTicketValidator*作为*Bean*。这是*KerberosTicketValidator*的实现并使用 SUN JAAS 登录模块：

```java
@Bean
public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
    SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
    ticketValidator.setServicePrincipal("HTTP/demo.kerberos.bealdung.com@baeldung.com");
    ticketValidator.setKeyTabLocation(new FileSystemResource("baeldung.keytab"));
    return ticketValidator;
}复制
```

### 6.4. 用户详情

我们之前 在*AuthenticationProvider中看到了对**UserDetailsService*的引用，那么我们为什么需要它呢？好吧，正如我们对 Kerberos 的了解，它纯粹是一种基于票证的身份验证机制。

因此，虽然它能够识别用户，但它不提供与用户相关的其他详细信息，例如他们的授权。我们需要向*AuthenticationProvider提供有效的**UserDetailsService*来填补这个空白。

### 6.5. 运行应用程序

这几乎是我们设置 Web 应用程序所需的，该应用程序为带有 Kerberos 的 SPNEGO 启用了 Spring Security。当我们启动 Web 应用程序并访问其中的任何页面时，Web 浏览器应提示输入用户名和密码，准备一个带有服务票证的 SPNEGO 令牌，并将其发送到应用程序。

应用程序应该能够使用 keytab 文件中的凭据处理它，并以成功的身份验证作为响应。

然而，正如我们之前看到的，设置一个工作的 Kerberos 环境是复杂且非常脆弱的。如果事情没有按预期进行，值得再次检查所有步骤。域名不匹配等简单错误可能会导致失败，并显示不是特别有用的错误消息。

## 7. SPNEGO 和 Kerberos 的实际使用

既然我们已经了解了 Kerberos 身份验证的工作原理以及我们如何在 Web 应用程序中将 SPNEGO 与 Kerberos 结合使用，我们可能会质疑它的必要性。虽然将其用作企业网络中的 SSO 机制是完全有意义的，但我们为什么要在 Web 应用程序中使用它呢？

好吧，一方面，即使经过这么多年，Kerberos 仍然在企业应用程序中非常活跃地使用，尤其是基于 Windows 的应用程序。**如果一个组织有多个内部和外部 Web 应用程序，那么扩展相同的 SSO 基础架构以覆盖所有**应用程序确实有意义。这使得组织的管理员和用户更容易通过不同的应用程序获得无缝体验。

## 八、结论

总而言之，在本教程中，我们了解了 Kerberos 身份验证协议的基础知识。我们还讨论了 SPNEGO 作为 GSS-API 的一部分，以及我们如何使用它来促进基于 HTTP 的 Web 应用程序中基于 Kerberos 的身份验证。此外，我们尝试构建一个小型 Web 应用程序，利用 Spring Security 对 SPNEGO 和 Kerberos 的内置支持。

本教程只是提供了一个功能强大且经过时间考验的身份验证机制的快速预览。有相当丰富的信息可供我们了解更多，甚至可能更多地欣赏！
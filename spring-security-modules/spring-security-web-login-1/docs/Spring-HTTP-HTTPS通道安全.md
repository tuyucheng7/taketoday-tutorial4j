## 1. 概述

本教程演示如何使用HTTPS使用Spring的Channel Security特性来保护应用程序的登录页面。

使用HTTPS进行身份验证对于在传输过程中保护敏感数据的完整性至关重要。

本文在[Spring Security表单登录](./SpringSecurity表单登录.md)教程的基础上添加了一个额外的安全层。
我们强调通过编码的HTTPS通道提供登录页面来保护身份验证数据所需的步骤。

## 2. 无通道安全的初始设置

web-app允许用户访问:

+ 未经身份验证的/anonymous.jsp
+ /login.jsp
+ 成功登录后的其他页面(/homepage.jsp)

该规则由以下配置控制：

```text
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/anonymous*")
            .anonymous();
            
    http.authorizeRequests()
            .antMatchers("/login*")
            .permitAll();
            
    http.authorizeRequests()
            .anyRequest()
            .authenticated();
```

或通过XML：

```xml

<http use-expressions="true">
    <intercept-url pattern="/anonymous*" access="isAnonymous()"/>
    <intercept-url pattern="/login*" access="permitAll"/>
    <intercept-url pattern="/**" access="isAuthenticated()"/>
</http>
```

此时，登录页面位于：

```text
http://localhost:8080/login.html
```

用户可以通过HTTP进行身份验证，但这是不安全的，因为密码将以明文形式发送。

## 3. HTTPS服务器配置

要仅通过HTTPS提供登录页面，**你的Web服务器必须能够提供HTTPS页面**，这需要启用SSL/TLS支持。

请注意，你可以使用有效的证书，或者出于测试目的，你可以生成自己的证书。

假设我们使用Tomcat并使用我们自己的证书，我们首先需要创建一个带有自签名证书的密钥库。

可以在终端中使用以下命令来生成密钥库：

```shell
keytool -genkey -alias tuyucheng -keyalg RSA -storepass changeit -keypass changeit -dname 'CN=tomcat'
```

这会在你的主文件夹中(Windows中为用户目录)为你的用户配置文件的默认密钥库中创建一个私有密钥和一个自签名证书。

下一步是编辑conf/server.xml：

```text

<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"/>

<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
           clientAuth="false" sslProtocol="TLS"
           keystoreFile="${user.home}/.keystore" keystorePass="changeit"/>
```

第二个SSL/TLS<Connector\>标签通常在配置文件中被注释掉，因此只需要取消注释并添加密钥库信息。

有了HTTPS配置，登录页面现在也可以通过以下URL访问：

```text
https://localhost:8443/spring-security-login/login.html
```

## 4. 配置通道安全

此时，我们可以在HTTP和HTTPS下提供登录页面，本节说明如何强制使用HTTPS。

要强制登录页面使用HTTPS，通过添加以下内容修改你的安全配置：

```text
http.requiresChannel()
  .antMatchers("/login*").requiresSecure();
```

或者将requires-channel="https"属性添加到你的XML配置中：

```text
<intercept-url pattern="/login*" access="permitAll" requires-channel="https"/>
```

在此之后，用户只能通过HTTPS登录。所有相关链接，例如转发到/homepage.jsp将继承原始请求的协议，并将在HTTPS下提供服务。

在单个Web应用程序中混合HTTP和HTTPS请求时，还有其他方面需要注意并且需要进一步配置。

## 5. 混合HTTP和HTTPS

从安全角度来看，通过HTTPS提供一切服务是一种良好的实践。

但是，如果不能单独使用HTTPS，我们可以通过将以下内容添加到配置中来配置Spring以使用HTTP：

```text
http.requiresChannel()
  .anyRequest().requiresInsecure();
```

或者在XML中添加requires-channel="http"属性：

```text
<intercept‐url pattern="/**" access="isAuthenticated()" requires‐channel="http"/>
```

这指定Spring对所有未明确配置为使用HTTPS的请求使用HTTP，但同时它破坏了原始登录机制。

### 5.1 HTTPS上的自定义登录处理URL

原始的安全配置包含以下内容：

```text
<form-login login-processing-url="/perform_login"/>
```

**在不强制/perform_login使用HTTPS的情况下，重定向会发生在它的HTTP变体上，从而丢失与原始请求一起发送的登录信息**。

为了克服这个问题，我们需要将Spring配置为使用HTTPS来处理URL：

```text
http.requiresChannel()
  .antMatchers("/login*", "/perform_login");
```

注意传递给antMatchers方法的额外参数/perform_login。

XML配置中的等效项需要在配置中添加一个新的<intercept-url\>元素：

```text
<intercept-url pattern="/perform_login" requires-channel="https"/>
```

如果你自己的应用程序使用默认的login-processing-url(即/login)，则无需显式配置它，因为/login*模式已经涵盖了这一点。

配置完成后，用户可以登录，但不能访问经过身份验证的页面，例如/homepage.jsp。在HTTP协议下，因为Spring的会话固定保护功能。

### 5.2 禁用会话固定保护

在HTTP和HTTPS之间切换时，会话固定是一个无法避免的问题。

默认情况下，Spring会在成功登录后创建一个新的session-id。
当用户加载HTTPS登录页面时，用户的session-id cookie将被标记为安全的。
登录后，上下文将切换到HTTP，由于HTTP不安全，cookie将丢失。

**为了避免此设置，需要将session-fixation-protection设置为none**。

```text
http.sessionManagement()
  .sessionFixation()
  .none();
```

或通过XML：

```text
<session-management session-fixation-protection="none"/>
```

禁用会话固定保护可能会带来安全隐患，因此如果你担心基于会话固定的攻击，则需要权衡利弊。

## 6. 测试

应用所有这些配置后，在不登录(使用http://或https://)的情况下访问/anonymous.jsp会通过HTTP将你转发到该页面。

直接打开/homepage.jsp等其他页面会让你通过HTTPS转发到登录页面，登录成功后会通过使用HTTP将你转发回/homepage.jsp。

## 7. 总结

在本教程中，我们了解了如何配置通过HTTP进行通信的Spring Web应用程序，但登录机制除外。
然而，现代Web应用程序几乎总是应该只使用HTTPS作为它们的通信协议，降低安全级别或关闭安全功能(如会话固定保护)绝不是我们提倡的。
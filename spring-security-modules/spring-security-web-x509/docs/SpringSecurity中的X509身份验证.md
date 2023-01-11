## 1. 概述

在本文中，我们将重点介绍 X.509 证书身份验证的主要用例——在使用 HTTPS(HTTP over SSL)协议时验证通信对等方的身份。

简而言之——在建立安全连接时，客户端会根据其证书(由受信任的证书颁发机构颁发)验证服务器。

但除此之外，Spring Security 中的 X.509 可用于在连接时由服务器验证客户端的身份。这称为“相互身份验证”，我们也将在这里了解它是如何完成的。

最后，我们将讨论何时使用这种身份验证有意义。

为了演示服务器验证，我们将创建一个简单的 Web 应用程序并在浏览器中安装自定义证书颁发机构。

此外，对于相互身份验证，我们将创建一个客户端证书并修改我们的服务器以仅允许经过验证的客户端。

强烈建议按照本教程一步一步地创建证书，以及根据以下部分中提供的说明自行创建密钥库和信任库。但是，所有准备使用的文件都可以在我们的[GitHub 存储库](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-x509/store)中找到。

## 2. 自签名根 CA

为了能够签署我们的服务器端和客户端证书，我们需要首先创建我们自己的自签名根 CA 证书。这样，我们将充当我们自己的证书颁发机构。

为此，我们将使用[openssl](https://wiki.openssl.org/index.php/Binaries)库，因此我们需要在执行下一步之前安装它。

现在让我们创建 CA 证书：

```bash
openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt
```

当我们执行上述命令时，我们需要提供我们私钥的密码。出于本教程的目的，我们使用changeit作为密码。

此外，我们需要输入形成所谓专有名称的信息。在这里，我们只提供 CN(通用名称)- Baeldung.com - 其他部分为空。

[![根CA](https://www.baeldung.com/wp-content/uploads/2016/08/rootCA.jpg)](https://www.baeldung.com/wp-content/uploads/2016/08/rootCA.jpg)

## 3. 密钥库

可选要求：要使用加密强密钥以及加密和解密功能，我们需要在我们的 JVM 中安装“JavaCryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files ” 。

例如，这些可以从[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)下载(遵循下载中包含的安装说明)。一些 Linux 发行版还通过其包管理器提供可安装的包。

密钥库是我们的Spring Boot应用程序将用来保存我们服务器的私钥和证书的存储库。换句话说，我们的应用程序将在 SSL 握手期间使用密钥库向客户端提供证书。

在本教程中，我们使用Java Key-Store (JKS) 格式和[keytool](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)命令行工具。

### 3.1。服务器端证书

要在我们的Spring Boot应用程序中实现服务器端 X.509 身份验证，我们首先需要创建一个服务器端证书。


让我们从创建一个所谓的证书签名请求 (CSR) 开始：

```bash
openssl req -new -newkey rsa:4096 -keyout localhost.key –out localhost.csr
```

同样，对于 CA 证书，我们必须提供私钥的密码。此外，让我们使用localhost作为通用名称 (CN)。

在我们继续之前，我们需要创建一个配置文件—— localhost.ext。它将存储签署证书期间所需的一些附加参数。

```bash
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```

[此处](https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-web-x509/store/localhost.ext)还提供了一个即用型文件。

现在，是时候使用我们的rootCA.crt证书及其私钥签署请求了：

```bash
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial -extfile localhost.ext
```

请注意，我们必须提供创建 CA 证书时使用的相同密码。

在这个阶段，我们终于有了一个可以使用的由我们自己的证书颁发机构签名的localhost.crt证书。

要以人类可读的形式打印我们的证书详细信息，我们可以使用以下命令：

```bash
openssl x509 -in localhost.crt -text
```

### 3.2. 导入密钥库

在本节中，我们将了解如何将签名证书和相应的私钥导入到keystore.jks文件中。

我们将使用[PKCS 12 存档](https://en.wikipedia.org/wiki/PKCS_12)，将我们服务器的私钥与签名证书打包在一起。然后我们将它导入到新创建的keystore.jks 中。

我们可以使用以下命令创建一个.p12文件：

```bash
openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey localhost.key -in localhost.crt
```

因此，我们现在将localhost.key和localhost.crt捆绑在单个localhost.p12文件中。

现在让我们使用 keytool创建一个keystore.jks存储库并使用一个命令导入localhost.p12文件：

```bash
keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS
```

在这个阶段，我们已经为服务器身份验证部分准备好了一切。让我们继续我们的Spring Boot应用程序配置。

## 4. 示例应用

我们的 SSL 安全服务器项目由一个[@SpringBootApplication](https://www.baeldung.com/spring-boot-application-configuration)注解的应用程序类(它是一种[@Configuration](https://www.baeldung.com/bootstraping-a-web-application-with-spring-and-java-based-configuration))、一个application.properties配置文件和一个非常简单的 MVC 风格的前端组成。

应用程序所要做的就是呈现一个带有“Hello {User}！”的 HTML 页面。信息。通过这种方式，我们可以在浏览器中检查服务器证书，以确保连接得到验证和保护。

### 4.1。Maven 依赖项

首先，我们创建一个新的 Maven 项目，其中包含三个Spring BootStarter 包：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

供参考：我们可以在 Maven Central 上找到捆绑包([security](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-security")、[web](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-web")、[thymeleaf](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-thymeleaf"))。

### 4.2. 春季启动应用程序

下一步，我们创建主应用程序类和用户控制器：

```java
@SpringBootApplication
public class X509AuthenticationServer {
    public static void main(String[] args) {
        SpringApplication.run(X509AuthenticationServer.class, args);
    }
}

@Controller
public class UserController {
    @RequestMapping(value = "/user")
    public String user(Model model, Principal principal) {
        
        UserDetails currentUser 
          = (UserDetails) ((Authentication) principal).getPrincipal();
        model.addAttribute("username", currentUser.getUsername());
        return "user";
    }
}
```

现在，我们告诉应用程序在哪里可以找到我们的keystore.jks以及如何访问它。我们将 SSL 设置为“启用”状态并更改标准侦听端口以指示安全连接。

此外，我们配置一些用户详细信息以通过基本身份验证访问我们的服务器：

```plaintext
server.ssl.key-store=../store/keystore.jks
server.ssl.key-store-password=${PASSWORD}
server.ssl.key-alias=localhost
server.ssl.key-password=${PASSWORD}
server.ssl.enabled=true
server.port=8443
spring.security.user.name=Admin
spring.security.user.password=admin
```

这将是 HTML 模板，位于resources/templates文件夹中：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>X.509 Authentication Demo</title>
</head>
<body>
    <h2>Hello <span th:text="${username}"/>!</h2>
</body>
</html>
```

### 4.3. 根 CA 安装

在我们完成本节并查看该站点之前，我们需要将生成的根证书颁发机构安装为浏览器中的受信任证书。

我们的Mozilla Firefox证书颁发机构的示例性安装如下所示：

1.  在地址栏中输入about :preferences
2.  打开高级 -> 证书 -> 查看证书 -> 权限
3.  点击导入
4.  找到Baeldung 教程文件夹及其子文件夹spring-security-x509/keystore
5.  选择rootCA.crt文件并单击确定
6.  选择“信任此 CA 以识别网站”，然后单击确定

注意：如果不想将我们的证书颁发机构添加到受信任的颁发机构列表中，稍后可以选择例外并显示网站强硬，即使它被提及为不安全也是如此。但随后会在地址栏中看到一个“黄色感叹号”符号，表示连接不安全！

之后，我们将导航到spring-security-x509-basic-auth模块并运行：

```bash
mvn spring-boot:run
```

最后，我们点击https://localhost:8443/user ，从application.properties输入我们的用户凭据，应该会看到“Hello Admin！” 信息。现在我们可以通过单击地址栏中的“绿色锁”符号来检查连接状态，它应该是安全连接。

[![截图_20160822_205015](https://www.baeldung.com/wp-content/uploads/2016/08/Screenshot_20160822_205015.png)](https://www.baeldung.com/wp-content/uploads/2016/08/Screenshot_20160822_205015.png)

 

## 5.相互认证

在上一节中，我们介绍了如何实现最常见的 SSL 身份验证模式——服务器端身份验证。这意味着，只有服务器向客户端验证自己。

在本节中，我们将描述如何添加身份验证的另一部分——客户端身份验证。这样，只有拥有我们服务器信任的权威机构签署的有效证书的客户端才能访问我们的安全网站。

但在我们继续之前，让我们看看使用相互 SSL 身份验证的优缺点是什么。

优点：

-   X.509 客户端证书的私钥比任何用户定义的密码都强。但必须保密！
-   有了证书，客户的身份就众所周知并且易于验证。
-   不再忘记密码！

缺点：

-   我们需要为每个新客户创建一个证书。
-   客户端的证书必须安装在客户端应用程序中。事实上：X.509 客户端身份验证是设备相关的，这使得无法在公共场所使用这种身份验证，例如在网吧中。
-   必须有一种机制来撤销受损的客户端证书。
-   我们必须维护客户的证书。这很容易变得昂贵。

### 5.1。信任库

在某种程度上，信任库与密钥库相反。它持有我们信任的外部实体的证书。

在我们的例子中，将根 CA 证书保存在信任库中就足够了。

让我们看看如何创建一个truststore.jks文件并使用 keytool导入rootCA.crt ：

```bash
keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file rootCA.crt -keystore truststore.jks
```

注意，我们需要为新创建的trusstore.jks提供密码。在这里，我们再次使用了changeit密码。

就是这样，我们已经导入了我们自己的 CA 证书，并且可以使用信任库了。

### 5.2. Spring 安全配置

为了继续，我们正在修改我们的X509AuthenticationServer以通过创建[SecurityFilterChain](https://www.baeldung.com/spring-security-authentication-provider) Bean来配置HttpSecurity 。这里我们配置 x.509 机制来解析证书的通用名称 (CN)字段以提取用户名。

使用这个提取的用户名，Spring Security 在提供的UserDetailsService中查找匹配的用户。所以我们也实现了这个包含一个演示用户的服务接口。

提示：在生产环境中，此UserDetailsService可以加载其用户，例如从[JDBC Datasource加载](https://www.baeldung.com/spring-jdbc-jdbctemplate)。

必须注意，我们使用@EnableWebSecurity和@EnableGlobalMethodSecurity注解我们的类并启用了预授权/授权后。

使用后者，我们可以使用@PreAuthorize和@PostAuthorize注解我们的资源，以实现细粒度的访问控制：

```java
@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class X509AuthenticationServer {
    ...

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .x509()
            .subjectPrincipalRegex("CN=(.?)(?:,|$)")
            .userDetailsService(userDetailsService());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (username.equals("Bob")) {
                    return new User(username, "", 
                     AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
                }
                throw new UsernameNotFoundException("User not found!");
            }
        };
    }
}
```

如前所述，我们现在可以在控制器中使用基于表达式的访问控制。更具体地说，由于我们的@Configuration中的@EnableGlobalMethodSecurity注解，我们的授权注解受到尊重：


```java
@Controller
public class UserController {
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping(value = "/user")
    public String user(Model model, Principal principal) {
        ...
    }
}
```

可以在[官方文档](https://docs.spring.io/spring-security/reference/6.0/servlet/authorization/method-security.html)中找到所有可能的授权选项的概述。

作为最后的修改步骤，我们必须告诉应用程序我们的信任库所在的位置以及SSL 客户端身份验证是必要的(server.ssl.client-auth=need)。

所以我们将以下内容放入我们的application.properties：

```plaintext
server.ssl.trust-store=store/truststore.jks
server.ssl.trust-store-password=${PASSWORD}
server.ssl.client-auth=need
```

现在，如果我们运行应用程序并将浏览器指向https://localhost:8443/user，我们会收到通知，无法验证对等方并拒绝打开我们的网站。

### 5.3. 客户端证书

现在是时候创建客户端证书了。我们需要采取的步骤与我们已经创建的服务器端证书几乎相同。

首先，我们必须创建一个证书签名请求：

```bash
openssl req -new -newkey rsa:4096 -nodes -keyout clientBob.key -out clientBob.csr
```

我们必须提供将纳入证书的信息。对于本练习，我们只输入通用名 (CN) – Bob。这很重要，因为我们在授权期间使用此条目，并且我们的示例应用程序只识别 Bob。

接下来，我们需要使用我们的 CA 签署请求：

```bash
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in clientBob.csr -out clientBob.crt -days 365 -CAcreateserial
```

我们需要采取的最后一步是将签名证书和私钥打包到 PKCS 文件中：

```bash
openssl pkcs12 -export -out clientBob.p12 -name "clientBob" -inkey clientBob.key -in clientBob.crt
```

最后，我们准备在浏览器中安装客户端证书。

同样，我们将使用 Firefox：

1.  在地址栏中输入about :preferences
2.  打开高级 -> 查看证书 -> 的证书
3.  点击导入
4.  找到Baeldung 教程文件夹及其子文件夹spring-security-x509/store
5.  选择clientBob.p12文件并点击OK
6.  输入的证书密码，然后单击确定

现在，当我们刷新网站时，系统会提示我们选择要使用的客户端证书：

[![客户证书](https://www.baeldung.com/wp-content/uploads/2016/08/clientCert.jpg)](https://www.baeldung.com/wp-content/uploads/2016/08/clientCert.jpg)

如果我们看到像“Hello Bob！”这样的欢迎消息 ，这意味着一切都按预期工作！

[![鲍勃](https://www.baeldung.com/wp-content/uploads/2016/08/bob.jpg)](https://www.baeldung.com/wp-content/uploads/2016/08/bob.jpg)

## 6. 使用 XML 进行相互认证

也可以将 X.509 客户端身份验证添加到[XML中的](https://www.baeldung.com/spring-security-digest-authentication)[http安全配置](https://www.baeldung.com/spring-security-digest-authentication)：

```xml
<http>
    ...
    <x509 subject-principal-regex="CN=(.?)(?:,|$)" 
      user-service-ref="userService"/>

    <authentication-manager>
        <authentication-provider>
            <user-service id="userService">
                <user name="Bob" password="" authorities="ROLE_USER"/>
            </user-service>
        </authentication-provider>
    </authentication-manager>
    ...
</http>
```

要配置底层 Tomcat，我们必须将我们的密钥库和信任库放入其conf文件夹并编辑server.xml：

```xml
<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true" scheme="https" secure="true"
    clientAuth="true" sslProtocol="TLS"
    keystoreFile="${catalina.home}/conf/keystore.jks"
    keystoreType="JKS" keystorePass="changeit"
    truststoreFile="${catalina.home}/conf/truststore.jks"
    truststoreType="JKS" truststorePass="changeit"
/>
```

提示：当clientAuth设置为“want”时，SSL仍然启用，即使客户端没有提供有效的证书。但是在这种情况下，我们必须使用第二种身份验证机制，例如登录表单来访问受保护的资源。

## 7. 总结

总之，我们已经学习了如何创建自签名 CA 证书以及如何使用它来签署其他证书。

此外，我们还创建了服务器端和客户端证书。然后我们介绍了如何将它们相应地导入到密钥库和信任库中。

此外，现在应该能够将证书连同其私钥一起打包成 PKCS12 格式。

我们还讨论了何时使用 Spring Security X.509 客户端身份验证有意义，因此由决定是否将其实现到的 Web 应用程序中。
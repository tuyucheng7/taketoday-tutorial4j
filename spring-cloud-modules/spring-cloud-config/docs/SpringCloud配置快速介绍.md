## 1. 概述

**Spring Cloud Config**是Spring的客户端/服务器方法，用于跨多个应用程序和环境存储和提供分布式配置。

理想情况下，此配置存储在Git版本控制下进行版本控制，并且可以在应用程序运行时进行修改。虽然它非常适合使用所有受支持的配置文件格式以及Environment、[PropertySource或@Value](https://www.baeldung.com/properties-with-spring)等构造的Spring应用程序，但它可以在运行任何编程语言的任何环境中使用。

在本教程中，我们将重点介绍如何设置Git支持的配置服务器，在简单的REST应用程序服务器中使用它，以及如何设置包含加密属性值的安全环境。

## 2. 项目设置和依赖

首先，我们将创建两个新的Maven项目。服务器项目依赖于[spring-cloud-config-server](https://search.maven.org/search?q=a:spring-cloud-config-server)模块，以及[spring-boot-starter-security](https://search.maven.org/search?q=a:spring-boot-starter-security)和[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)启动器依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

但是，对于客户端项目，我们只需要[spring-cloud-starter-config](https://search.maven.org/search?q=a:spring-cloud-starter-config)和[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)模块：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. 配置服务器实现

应用程序的主要部分是一个配置类，更具体地说是一个[@SpringBootApplication](https://www.baeldung.com/spring-boot-application-configuration)，它通过自动配置注解@EnableConfigServer引入所有必需的设置：

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {

    public static void main(String[] arguments) {
        SpringApplication.run(ConfigServer.class, arguments);
    }
}
```

现在我们需要配置我们的服务器正在监听的服务器端口和一个Git-url，它提供了我们版本控制的配置内容。后者可以与http、ssh或本地文件系统上的简单文件等协议一起使用。

>   **提示**：如果我们计划使用指向同一个配置仓库的多个配置服务器实例，则我们可以配置服务器以将我们的仓库克隆到本地临时文件夹中。但要注意具有双因素身份验证的私有仓库；他们很难处理！在这种情况下，将它们克隆到我们的本地文件系统并使用副本会更容易。

还有一些占位符变量和搜索模式用于配置可用的仓库url；但是，这超出了我们本文的范围。如果你有兴趣了解更多信息，官方文档是一个不错的起点。

我们还需要在application.properties中为基本身份验证(Basic-Authentication)设置用户名和密码，以避免在每次应用程序重新启动时自动生成密码：

```properties
server.port=8888
spring.cloud.config.server.git.uri=ssh://localhost/config-repo
spring.cloud.config.server.git.clone-on-start=true
spring.security.user.name=root
spring.security.user.password=s3cr3t
```

## 4. 作为配置存储的Git仓库

为了完成我们的服务器，我们必须在配置的url下初始化一个Git仓库，创建一些新的属性文件，并用一些值填充它们。

配置文件的名称像普通的Spring application.properties一样组成，但是使用客户端的配置名称(例如属性“spring.application.name”的值)代替“application”一词，后跟破折号和激活的Profile。例如：

```shell
$> git init
$> echo 'user.role=Developer' > config-client-development.properties
$> echo 'user.role=User'      > config-client-production.properties
$> git add .
$> git commit -m 'Initial config-client properties'
```

> **故障排除**：如果我们遇到与ssh相关的身份验证问题，我们可以仔细检查ssh服务器上的~/.ssh/known_hosts和~/.ssh/authorized_keys。

## 5. 查询配置

现在我们可以启动我们的服务器了。可以使用以下路径查询我们服务器提供的Git支持的配置API：

```shell
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

{label}占位符是指Git分支，{application}是指客户端的应用程序名称，而{profile}是指客户端当前激活的应用程序Profile。

因此，我们可以通过以下方式检索在分支master的development Profile下运行的计划配置客户端的配置：

```bash
$> curl http://root:s3cr3t@localhost:8888/config-client/development/master
```

## 6. 客户端实现

接下来，让我们来开发客户端。这将是一个非常简单的客户端应用程序，由一个带有一个GET方法的REST控制器组成。

要获取我们的服务器，配置必须放在application.properties文件中。Spring Boot 2.4引入了一种使用**spring.config.import**属性加载配置数据的新方法，这是现在绑定到配置服务器的默认方法：

```java
@SpringBootApplication
@RestController
public class ConfigClient {

    @Value("${user.role}")
    private String role;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClient.class, args);
    }

    @GetMapping(value = "/whoami/{username}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String whoami(@PathVariable("username") String username) {
        return String.format("Hello! You're %s and you'll become a(n) %s...\n", username, role);
    }
}
```

除了应用程序名称之外，我们还将激活的Profile和连接详细信息放在我们的application.properties中：

```properties
spring.application.name=config-client
spring.profiles.active=development
spring.config.import=optional:configserver:http://root:s3cr3t@localhost:8888
```

这将连接到位于http://localhost:8888的配置服务器，并且在启动连接时还将使用HTTP基本安全性。我们也可以使用`spring.cloud.config.username`和`spring.cloud.config.password`属性分别设置用户名和密码。

在某些情况下，如果服务无法连接到配置服务器，我们可能希望服务启动失败。如果这是所需的行为，我们可以删除**optional:**前缀以使客户端因异常而停止。

为了测试配置是否从我们的服务器正确接收，以及role值是否被注入到我们的控制器方法中，我们只需在启动客户端后使用curl测试它：

```shell
$> curl http://localhost:8080/whoami/Mr_Pink
```

如果响应如下，则我们的Spring Cloud Config Server和它的客户端现在工作正常：

```shell
Hello! You're Mr_Pink and you'll become a(n) Developer...
```

## 7. 加密与解密

要求：要将加密强密钥与Spring加密和解密功能一起使用，我们需要在JVM中安装“Java加密扩展(JCE)无限强度管辖策略文件”。例如，可以从[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)下载这些文件。要安装，请按照下载中包含的说明进行操作。一些Linux发行版还通过其包管理器提供可安装的包。

由于配置服务器支持属性值的加密和解密，因此我们可以使用公共仓库来存储敏感数据，如用户名和密码。加密值以字符串{cipher}为前缀，如果服务器配置为使用对称密钥或密钥对，则可以通过对路径'/encrypt'的REST调用生成。

还可以使用要解密的端点。两个端点都接收包含应用程序名称及其当前Profile的占位符的路径：'/*/{name}/{profile}'。这对于控制每个客户端的加密特别有用。然而，在它们发挥作用之前，我们必须配置一个加密密钥，我们将在下一节中进行配置。

> **提示**：如果我们使用curl来调用en-/decryption API，最好使用–data-urlencode选项(而不是–data/-d)，或者将“Content-Type”标头显式设置为“text/plain”。这可确保正确处理加密值中的特殊字符，如“+”。

如果在通过客户端获取时无法自动解密值，则其密钥将使用名称本身重命名，并以“invalid”为前缀。这应该可以防止使用加密值作为密码。

> **提示**：在设置包含YAML文件的仓库时，我们必须用单引号将加密值和前缀值括起来。但是，Properties文件不是这种情况。

### 7.1 CSRF

默认情况下，Spring Security为发送到我们应用程序的所有请求启用[CSRF](https://www.baeldung.com/spring-security-csrf)保护。

因此，为了能够使用/encrypt和/decrypt端点，让我们为它们禁用CSRF：

```java
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
              .ignoringAntMatchers("/encrypt/**")
              .ignoringAntMatchers("/decrypt/**");

        //...
    }
}
```

### 7.2 密钥管理

默认情况下，配置服务器能够以对称或非对称方式加密属性值。

**要使用对称加密**，我们只需将application.properties中的属性“encrypt.key”设置为我们选择的密钥。或者，我们可以传入环境变量ENCRYPT_KEY。

**对于非对称加密**，我们可以将“encrypt.key”设置为PEM编码的字符串值或配置要使用的密钥库。

由于我们的演示服务器需要一个高度安全的环境，我们将选择后一个选项，同时生成一个新的密钥库，包括一个RSA密钥对，首先使用Java keytool工具：

```shell
$> keytool -genkeypair -alias config-server-key \
       -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
       -dname 'CN=Config Server,OU=Spring Cloud,O=Tuyucheng' \
       -keypass my-k34-s3cr3t -keystore config-server.jks \
       -storepass my-s70r3-s3cr3t
```

然后我们将创建的密钥库添加到我们服务器的application.properties中并重新运行它：

```properties
encrypt.keyStore.location=classpath:/config-server.jks
encrypt.keyStore.password=my-s70r3-s3cr3t
encrypt.keyStore.alias=config-server-key
encrypt.keyStore.secret=my-k34-s3cr3t
```

接下来，我们将查询加密端点，并将响应作为值添加到我们仓库中的配置中：

```shell
$> export PASSWORD=$(curl -X POST --data-urlencode d3v3L \
       http://root:s3cr3t@localhost:8888/encrypt)
$> echo "user.password={cipher}$PASSWORD" >> config-client-development.properties
$> git commit -am 'Added encrypted password'
$> curl -X POST http://root:s3cr3t@localhost:8888/refresh
```

为了测试我们的设置是否正常工作，我们将修改ConfigClient类并重新启动我们的客户端：

```java
@SpringBootApplication
@RestController
public class ConfigClient {

    // ...

    @Value("${user.password}")
    private String password;

    // ...
    public String whoami(@PathVariable("username") String username) {
        return String.format("Hello! You're %s and you'll become a(n) %s, " + "but only if your password is '%s'!\n", username, role, password);
    }
}
```

最后，如果我们的配置值被正确解密，对我们客户端的查询将告诉我们：

```shell
$> curl http://localhost:8080/whoami/Mr_Pink
Hello! You're Mr_Pink and you'll become a(n) Developer, \
  but only if your password is 'd3v3L'!
```

### 7.3 使用多个键

如果我们想使用多个密钥进行加密和解密，例如为每个服务的应用程序使用一个专用密钥，我们可以在{cipher}前缀和BASE64编码的属性值之间添加另一个{name:value}形式的前缀。

配置服务器几乎开箱即用地理解像{secret:my-crypto-secret}或{key:my-key-alias}这样的前缀。后一个选项需要在我们的application.properties中配置密钥库。搜索此密钥库以查找匹配的密钥别名。例如：

```properties
user.password={cipher}{secret:my-499-s3cr3t}AgAMirj1DkQC0WjRv...
user.password={cipher}{key:config-client-key}AgAMirj1DkQC0WjRv...
```

对于没有密钥库的场景，我们必须实现一个TextEncryptorLocator类型的@Bean，它处理查找并为每个密钥返回一个TextEncryptor-Object。

### 7.4 提供加密属性

如果我们想禁用服务器端加密并在本地处理属性值的解密，我们可以将以下内容放入服务器的application.properties中：

```properties
spring.cloud.config.server.encrypt.enabled=false
```

此外，我们可以删除所有其他“encrypt.*”属性以禁用REST端点。

## 8. 总结

现在我们可以创建一个配置服务器来从Git仓库向客户端应用程序提供一组配置文件。我们还可以使用这样的服务器做一些其他的事情。

例如：

-   以YAML或Properties格式而不是JSON提供配置，同时解析占位符。这在非Spring环境中使用时非常有用，在非Spring环境中配置未直接映射到PropertySource。
-   依次提供纯文本配置文件，可以选择使用已解析的占位符。例如，这对于提供依赖于环境的日志记录配置很有用。
-   将配置服务器嵌入到应用程序中，它在其中从Git仓库配置自身，而不是作为服务于客户端的独立应用程序运行。因此，我们必须设置一些属性和/或我们必须删除@EnableConfigServer注解，这取决于用例。
-   使配置服务器在Spring Netflix Eureka服务发现中可用，并在配置客户端中启用自动服务器发现。如果服务器没有固定位置或在其位置移动，这就变得很重要。
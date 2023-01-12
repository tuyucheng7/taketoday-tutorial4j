## 1. 概述

在本教程中，我们将研究 Apereo 中央身份验证服务 (CAS)，并了解Spring Boot服务如何使用它进行身份验证。[CAS](https://apereo.github.io/cas/6.1.x/index.html)是一种企业[单点登录 (SSO)](https://www.baeldung.com/cs/sso-guide)解决方案，也是开源的。

什么是单点登录？当你使用相同的凭据登录 YouTube、Gmail 和地图时，这就是单点登录。我们将通过设置 CAS 服务器和Spring Boot应用程序来演示这一点。Spring Boot 应用程序将使用 CAS 进行身份验证。

## 2. CAS 服务器设置

### 2.1. CAS 安装和依赖

服务器使用 Maven (Gradle) War Overlay 样式来简化设置和部署：

```powershell
git clone https://github.com/apereo/cas-overlay-template.git cas-server
```

此命令会将cas-overlay-template克隆到cas-server目录中。

我们将涉及的一些方面包括 JSON 服务注册和 JDBC 数据库连接。因此，我们将它们的模块添加到build.gradle文件的依赖项部分：

```groovy
compile "org.apereo.cas:cas-server-support-json-service-registry:${casServerVersion}"
compile "org.apereo.cas:cas-server-support-jdbc:${casServerVersion}"
```

让我们确保检查最新版本的[casServer](https://github.com/apereo/cas/releases)。

### 2.2. CAS 服务器配置

在启动 CAS 服务器之前，我们需要添加一些基本配置。让我们从创建一个cas-server/src/main/resources文件夹开始，并在这个文件夹中。随后还将 在文件夹中创建application.properties ：

```plaintext
server.port=8443
spring.main.allow-bean-definition-overriding=true
server.ssl.key-store=classpath:/etc/cas/thekeystore
server.ssl.key-store-password=changeit
```

让我们继续创建上面配置中引用的密钥库文件。首先，我们需要在cas-server/src/main/resources中创建文件夹/etc/cas和 /etc/cas/config。

然后，我们需要将目录更改为cas-server/src/main/resources/etc/cas并运行命令生成密钥库：

```bash
keytool -genkey -keyalg RSA -alias thekeystore -keystore thekeystore -storepass changeit -validity 360 -keysize 2048
```

为了不出现 SSL 握手错误，我们应该使用localhost作为名字和姓氏的值。我们也应该对组织名称和单位使用相同的名称。此外，我们需要将密钥库导入到我们将用来运行客户端应用程序的 JDK/JRE 中：

```bash
keytool -importkeystore -srckeystore thekeystore -destkeystore $JAVA11_HOME/jre/lib/security/cacerts
```

源密钥库和目标密钥库的密码是changeit。在 Unix 系统上，我们可能必须以管理员 ( sudo ) 权限运行此命令。导入后，我们应该重新启动所有正在运行的Java实例或重新启动系统。

我们正在使用 JDK11，因为它是 CAS 版本 6.1.x 所必需的。此外，我们定义了指向其主目录的环境变量 $JAVA11_HOME。我们现在可以启动 CAS 服务器：

```bash
./gradlew run -Dorg.gradle.java.home=$JAVA11_HOME
```

当应用程序启动时，我们将在终端上看到“READY”字样，服务器将在[https://localhost:8443](https://localhost:8443/)上可用。

### 2.3. CAS 服务器用户配置

我们还不能登录，因为我们还没有配置任何用户。CAS 有不同的[管理配置](https://apereo.github.io/cas/6.1.x/configuration/Configuration-Server-Management.html)的方法，包括独立模式。让我们创建一个配置文件夹cas-server/src/main/resources/etc/cas/config我们将在其中创建一个属性文件cas.properties。现在，我们可以在属性文件中定义一个静态用户：

```plaintext
cas.authn.accept.users=casuser::Mellon
```

我们必须将配置文件夹的位置传达给 CAS 服务器，以使设置生效。让我们更新tasks.gradle以便我们可以从命令行将位置作为 JVM 参数传递：

```groovy
task run(group: "build", description: "Run the CAS web application in embedded container mode") {
    dependsOn 'build'
    doLast {
        def casRunArgs = new ArrayList<>(Arrays.asList(
          "-server -noverify -Xmx2048M -XX:+TieredCompilation -XX:TieredStopAtLevel=1".split(" ")))
        if (project.hasProperty('args')) {
            casRunArgs.addAll(project.args.split('s+'))
        }
        javaexec {
            main = "-jar"
            jvmArgs = casRunArgs
            args = ["build/libs/${casWebApplicationBinaryName}"]
            logger.info "Started ${commandLine}"
        }
    }
}
```

然后我们保存文件并运行：

```bash
./gradlew run
  -Dorg.gradle.java.home=$JAVA11_HOME
  -Pargs="-Dcas.standalone.configurationDirectory=/cas-server/src/main/resources/etc/cas/config"
```

请注意cas.standalone.configurationDirectory的值是一个绝对路径。我们现在可以转到https://localhost:8443并使用用户名casuser和密码Mellon登录。

## 3. CAS客户端设置

我们将使用[Spring Initializr](https://start.spring.io/)生成Spring Boot客户端应用程序。它将具有Web、Security、Freemarker和DevTools依赖项。[此外，我们还将Spring Security CAS](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.security" a%3A"spring-security-cas")模块的依赖添加到它的pom.xml中：

```java
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-cas</artifactId>
    <versionId>5.3.0.RELEASE</versionId>
</dependency>
```

最后，让我们添加以下Spring Boot属性来配置应用程序：

```plaintext
server.port=8900
spring.freemarker.suffix=.ftl
```

## 4、CAS服务器服务注册

客户端应用程序必须在任何身份验证之前向 CAS 服务器注册。CAS 服务器支持使用 YAML、JSON、MongoDB 和 LDAP 客户端注册表。

在本教程中，我们将使用 JSON 服务注册表方法。让我们创建另一个文件夹cas-server/src/main/resources/etc/cas/services。此文件夹将存放服务注册表 JSON 文件。

我们将创建一个包含客户端应用程序定义的 JSON 文件。文件名称casSecuredApp-8900.json 遵循模式 s erviceName-Id.json：

```javascript
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "http://localhost:8900/login/cas",
  "name" : "casSecuredApp",
  "id" : 8900,
  "logoutType" : "BACK_CHANNEL",
  "logoutUrl" : "http://localhost:8900/exit/cas"
}
```

serviceId属性定义客户端应用程序的正则表达式 URL 模式。该模式应与客户端应用程序的 URL 相匹配。

id属性应该是唯一的。换句话说，不应该有两个或多个具有相同 id 的服务注册到同一个 CAS 服务器。具有重复的id将导致冲突和覆盖配置。

我们还将注销类型配置为BACK_CHANNEL并将 URL 配置为[http://localhost:8900/exit/cas](http://localhost:8900/exit/cas)以便我们稍后可以进行单点注销。

在 CAS 服务器可以使用我们的 JSON 配置文件之前，我们必须在我们的cas.properties中启用 JSON 注册表：

```plaintext
cas.serviceRegistry.initFromJson=true
cas.serviceRegistry.json.location=classpath:/etc/cas/services
```

## 5. CAS客户端单点登录配置

我们的下一步是配置 Spring Security 以与 CAS 服务器一起工作。我们还应该检查[交互的完整流程](https://docs.spring.io/spring-security/site/docs/5.2.2.RELEASE/reference/html5/#cas-sequence)，称为 CAS 序列。

让我们将以下 bean 配置添加到Spring Boot 应用程序的CasSecuredApplication类中：

```java
@Bean
public CasAuthenticationFilter casAuthenticationFilter(
  AuthenticationManager authenticationManager,
  ServiceProperties serviceProperties) throws Exception {
    CasAuthenticationFilter filter = new CasAuthenticationFilter();
    filter.setAuthenticationManager(authenticationManager);
    filter.setServiceProperties(serviceProperties);
    return filter;
}

@Bean
public ServiceProperties serviceProperties() {
    logger.info("service properties");
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setService("http://cas-client:8900/login/cas");
    serviceProperties.setSendRenew(false);
    return serviceProperties;
}

@Bean
public TicketValidator ticketValidator() {
    return new Cas30ServiceTicketValidator("https://localhost:8443");
}

@Bean
public CasAuthenticationProvider casAuthenticationProvider(
  TicketValidator ticketValidator,
  ServiceProperties serviceProperties) {
    CasAuthenticationProvider provider = new CasAuthenticationProvider();
    provider.setServiceProperties(serviceProperties);
    provider.setTicketValidator(ticketValidator);
    provider.setUserDetailsService(
      s -> new User("test@test.com", "Mellon", true, true, true, true,
      AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
    provider.setKey("CAS_PROVIDER_LOCALHOST_8900");
    return provider;
}
```

ServiceProperties bean的URL 与casSecuredApp-8900.json中的serviceId相同。这很重要，因为它向 CAS 服务器标识此客户端。

ServiceProperties的sendRenew属性设置为false。这意味着用户只需向服务器提供一次登录凭据。

AuthenticationEntryPoint bean将处理身份验证异常。因此，它将用户重定向到 CAS 服务器的登录 URL 以进行身份验证。

总之，身份验证流程如下：

1.  用户尝试访问安全页面，这会触发身份验证异常
2.  异常触发AuthenticationEntryPoint。作为响应，AuthenticationEntryPoint会将用户带到 CAS 服务器登录页面 – [https://localhost:8443/login](https://localhost:8443/login)
3.  身份验证成功后，服务器会使用票证重定向回客户端
4.  CasAuthenticationFilter将获取重定向并调用CasAuthenticationProvider
5.  CasAuthenticationProvider将使用TicketValidator在 CAS 服务器上确认提交的票据
6.  如果票证有效，用户将重定向到请求的安全 URL

最后，让我们配置HttpSecurity以保护 WebSecurityConfig 中的一些路由。在此过程中，我们还将添加用于异常处理的身份验证入口点：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers( "/secured", "/login") 
      .authenticated() 
      .and().exceptionHandling() 
      .authenticationEntryPoint(authenticationEntryPoint());
}
```

## 6. CAS客户端单点退出配置

到目前为止，我们已经处理了单点登录；现在让我们考虑 CAS 单点注销 (SLO)。

使用 CAS 管理用户身份验证的应用程序可以从两个地方注销用户：

-   客户端应用程序可以在本地注销用户——这不会影响用户在使用同一 CAS 服务器的其他应用程序中的登录状态
-   客户端应用程序还可以从 CAS 服务器注销用户——这将导致用户从连接到同一 CAS 服务器的所有其他客户端应用程序注销。

我们将首先在客户端应用程序上设置注销，然后将其扩展为 CAS 服务器上的单点注销。

为了使幕后发生的事情一目了然，我们将创建一个logout()方法来处理本地注销。成功后，它会将我们重定向到一个页面，其中包含用于单点注销的链接：

```java
@GetMapping("/logout")
public String logout(
  HttpServletRequest request, 
  HttpServletResponse response, 
  SecurityContextLogoutHandler logoutHandler) {
    Authentication auth = SecurityContextHolder
      .getContext().getAuthentication();
    logoutHandler.logout(request, response, auth );
    new CookieClearingLogoutHandler(
      AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
      .logout(request, response, auth);
    return "auth/logout";
}
```

在单点注销过程中，CAS 服务器会先使用户的票证过期，然后向所有已注册的客户端应用程序发送异步请求。每个收到此信号的客户端应用程序都将执行本地注销。从而达到一次登出的目的，就会造成处处登出。

话虽如此，让我们向客户端应用程序添加一些 bean 配置。具体来说，在 CasSecuredApplicaiton中：

```java
@Bean
public SecurityContextLogoutHandler securityContextLogoutHandler() {
    return new SecurityContextLogoutHandler();
}

@Bean
public LogoutFilter logoutFilter() {
    LogoutFilter logoutFilter = new LogoutFilter("https://localhost:8443/logout",
      securityContextLogoutHandler());
    logoutFilter.setFilterProcessesUrl("/logout/cas");
    return logoutFilter;
}

@Bean
public SingleSignOutFilter singleSignOutFilter() {
    SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
    singleSignOutFilter.setCasServerUrlPrefix("https://localhost:8443");
    singleSignOutFilter.setLogoutCallbackPath("/exit/cas");
    singleSignOutFilter.setIgnoreInitConfiguration(true);
    return singleSignOutFilter;
}
```

logoutFilter将拦截对/logout/cas的请求并将应用程序重定向到 CAS 服务器。SingleSignOutFilter将拦截来自 CAS 服务器的请求并执行本地注销。

## 7. 将 CAS 服务器连接到数据库

我们可以将 CAS 服务器配置为从 MySQL 数据库读取凭据。我们将使用在本地计算机上运行的 MySQL 服务器的测试数据库。让我们更新cas-server/src/main/resources/etc/cas/config/cas.properties：

```plaintext
cas.authn.accept.users=

cas.authn.jdbc.query[0].sql=SELECT  FROM users WHERE email = ?
cas.authn.jdbc.query[0].url=
  jdbc:mysql://127.0.0.1:3306/test?
  useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
cas.authn.jdbc.query[0].dialect=org.hibernate.dialect.MySQLDialect
cas.authn.jdbc.query[0].user=root
cas.authn.jdbc.query[0].password=root
cas.authn.jdbc.query[0].ddlAuto=none
cas.authn.jdbc.query[0].driverClass=com.mysql.cj.jdbc.Driver
cas.authn.jdbc.query[0].fieldPassword=password
cas.authn.jdbc.query[0].passwordEncoder.type=NONE
```

我们将cas.authn.accept.users设置为空白。这将停用 CAS 服务器对静态用户存储库的使用。

根据上面的 SQL，用户的凭据存储在users表中。电子邮件列代表用户的主体(用户名)。

请确保检查[支持的数据库、可用的驱动程序和方言的列表](https://apereo.github.io/cas/6.1.x/installation/JDBC-Drivers.html)。我们还将密码编码器类型设置为NONE。还可以使用其他[加密机制](https://apereo.github.io/cas/6.1.x/configuration/Configuration-Properties-Common.html#password-encoding)及其特殊属性。

需要注意的是CAS服务器数据库中的principal必须和客户端应用的principal一致。

让我们将CasAuthenticationProvider更新为与 CAS 服务器具有相同的用户名：

```java
@Bean
public CasAuthenticationProvider casAuthenticationProvider() {
    CasAuthenticationProvider provider = new CasAuthenticationProvider();
    provider.setServiceProperties(serviceProperties());
    provider.setTicketValidator(ticketValidator());
    provider.setUserDetailsService(
      s -> new User("test@test.com", "Mellon", true, true, true, true,
      AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
    provider.setKey("CAS_PROVIDER_LOCALHOST_8900");
    return provider;
}
```

CasAuthenticationProvider不使用密码进行身份验证。尽管如此，它的用户名必须与 CAS 服务器的用户名匹配才能使身份验证成功。CAS 服务器要求 MySQL 服务器在本地主机的3306端口运行。用户名和密码应该是root。

再次重启 CAS 服务器和Spring Boot应用程序。然后使用新凭据进行身份验证。

## 八. 总结

我们已经了解了如何将 CAS SSO 与 Spring Security 以及涉及的许多配置文件一起使用。CAS SSO 的许多其他方面都是可配置的。从主题和协议类型到身份验证策略。
## 1. 概述

在本教程中，我们介绍使用[Okta作为身份提供者(IdP)的](https://www.baeldung.com/spring-security-okta)[Spring Security SAML](https://spring.io/projects/spring-security-saml)。

## 2. 什么是SAML？

安全断言标记语言 ( [SAML](https://www.baeldung.com/cs/saml-introduction) ) 是一种开放标准，允许 IdP 安全地将用户的身份验证和授权详细信息发送给服务提供商 (SP)。它使用基于 XML 的消息在 IdP 和 SP 之间进行通信。

换句话说，当用户尝试访问服务时，他需要使用 IdP 登录。登录后，IdP 以 XML 格式向 SP 发送带有授权和身份验证详细信息的 SAML 属性。

除了提供安全的身份验证传输机制外，SAML 还提倡单点登录 (SSO)，允许用户登录一次并重复使用相同的凭据登录其他服务提供商。

## 3. Okta SAML 设置

首先，作为先决条件，我们应该[设置一个 Okta 开发者帐户](https://www.baeldung.com/spring-security-okta#developerAccountSignUp)。

### 3.1。创建新应用程序

然后，我们将创建一个支持 SAML 2.0 的新 Web 应用程序集成：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.00.09-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.00.09-PM.png)

接下来，我们将填写应用名称和应用徽标等一般信息：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.02.25-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.02.25-PM.png)

### 3.2. 编辑 SAML 集成

在此步骤中，我们将提供 SAML 设置，例如 SSO URL 和 Audience URI：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.10-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.10-PM.png)

最后，我们可以提供有关我们集成的反馈：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.29-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.29-PM.png)

### 3.3. 查看设置说明

完成后，我们可以查看Spring BootApp 的设置说明：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.56-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.04.56-PM.png)

注意：我们应该 Spring Security 配置中进一步需要的 IdP Issuer URL 和 IdP 元数据 XML 等说明：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.06.33-PM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-19-at-4.06.33-PM.png)

## 4.Spring Boot设置

除了[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web)和[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-security)等常见的 Maven 依赖项之外，我们还需要[spring-security-saml2-core](https://search.maven.org/search?q=g:org.springframework.security.extensions a:spring-security-saml2-core)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.security.extensions</groupId>
    <artifactId>spring-security-saml2-core</artifactId>
    <version>1.0.10.RELEASE</version>
</dependency>
```

此外，确保添加[Shibboleth存储库](https://build.shibboleth.net/nexus/content/repositories/releases/)以下载spring-security-saml2-core依赖项所需的最新opensaml jar ：

```xml
<repository>
    <id>Shibboleth</id>
    <name>Shibboleth</name>
    <url>https://build.shibboleth.net/nexus/content/repositories/releases/</url>
</repository>
```

或者，我们可以在 Gradle 项目中设置依赖项：

```groovy
compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: "<code class="language-xml">2.7.2" 编译组：'org.springframework.boot'，名称：'spring-boot-starter-security'，版本：" 2.7.2" 编译组：'org.springframework.security.extensions'，名称：'spring-security-saml2-核心'，版本：“1.0.10.RELEASE”
```

## 5.Spring安全配置

现在我们已经准备好 Okta SAML 设置和Spring Boot项目，让我们从 SAML 2.0 与 Okta 集成所需的 Spring Security 配置开始。

### 5.1。SAML 入口点

首先，我们将创建一个[SAMLEntryPoint](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/saml/SAMLEntryPoint.html)类的 bean，它将作为 SAML 身份验证的入口点：

```java
@Bean
public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    return webSSOProfileOptions;
}

@Bean
public SAMLEntryPoint samlEntryPoint() {
    SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
    return samlEntryPoint;
}
```

在这里，WebSSOProfileOptions bean 允许我们设置从 SP 发送到 IdP 要求用户身份验证的请求的参数。

### 5.2. 登录和注销

接下来，让我们为我们的 SAML URI 创建一些过滤器，例如 / discovery、 / login和 / logout：

```java
@Bean
public FilterChainProxy samlFilter() throws Exception {
    List<SecurityFilterChain> chains = new ArrayList<>();
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/"),
        samlWebSSOProcessingFilter()));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/"),
        samlDiscovery()));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/"),
        samlEntryPoint));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/"),
        samlLogoutFilter));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/"),
        samlLogoutProcessingFilter));
    return new FilterChainProxy(chains);
}
```

然后，我们将添加一些相应的过滤器和处理程序：

```java
@Bean
public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
    SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
    samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
    samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
    samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    return samlWebSSOProcessingFilter;
}

@Bean
public SAMLDiscovery samlDiscovery() {
    SAMLDiscovery idpDiscovery = new SAMLDiscovery();
    return idpDiscovery;
}

@Bean
public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
    SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    successRedirectHandler.setDefaultTargetUrl("/home");
    return successRedirectHandler;
}

@Bean
public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    failureHandler.setUseForward(true);
    failureHandler.setDefaultFailureUrl("/error");
    return failureHandler;
}
```

到目前为止，我们已经配置了身份验证的入口点 ( samlEntryPoint ) 和一些过滤器链。因此，让我们深入了解他们的详细信息。

当用户第一次尝试登录时，samlEntryPoint将处理登录请求。然后，samlDiscovery bean(如果启用)将发现要联系以进行身份验证的 IdP。

接下来，当用户登录时，IdP 将 SAML 响应重定向到/saml/sso URI 进行处理，相应的samlWebSSOProcessingFilter将验证关联的身份验证令牌。

成功后， successRedirectHandler会将用户重定向到默认目标 URL ( /home )。否则，authenticationFailureHandler会将用户重定向到/error URL。

最后，让我们为单个和全局注销添加注销处理程序：

```java
@Bean
public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
    SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
    successLogoutHandler.setDefaultTargetUrl("/");
    return successLogoutHandler;
}

@Bean
public SecurityContextLogoutHandler logoutHandler() {
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    logoutHandler.setInvalidateHttpSession(true);
    logoutHandler.setClearAuthentication(true);
    return logoutHandler;
}

@Bean
public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
    return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
}

@Bean
public SAMLLogoutFilter samlLogoutFilter() {
    return new SAMLLogoutFilter(successLogoutHandler(),
        new LogoutHandler[] { logoutHandler() },
        new LogoutHandler[] { logoutHandler() });
}
```

### 5.3. 元数据处理

现在，我们将向 SP 提供 IdP 元数据 XML。这将有助于让我们的 IdP 知道一旦用户登录它应该重定向到哪个 SP 端点。

因此，我们将配置[MetadataGenerator](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/saml/metadata/MetadataGenerator.html) bean 以启用 Spring SAML 来处理元数据：

```java
public MetadataGenerator metadataGenerator() {
    MetadataGenerator metadataGenerator = new MetadataGenerator();
    metadataGenerator.setEntityId(samlAudience);
    metadataGenerator.setExtendedMetadata(extendedMetadata());
    metadataGenerator.setIncludeDiscoveryExtension(false);
    metadataGenerator.setKeyManager(keyManager());
    return metadataGenerator;
}

@Bean
public MetadataGeneratorFilter metadataGeneratorFilter() {
    return new MetadataGeneratorFilter(metadataGenerator());
}

@Bean
public ExtendedMetadata extendedMetadata() {
    ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    extendedMetadata.setIdpDiscoveryEnabled(false);
    return extendedMetadata;
}
```

MetadataGenerator bean 需要一个[KeyManager](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/saml/key/KeyManager.html)实例来加密 SP 和 IdP 之间的交换：

```java
@Bean
public KeyManager keyManager() {
    DefaultResourceLoader loader = new DefaultResourceLoader();
    Resource storeFile = loader.getResource(samlKeystoreLocation);
    Map<String, String> passwords = new HashMap<>();
    passwords.put(samlKeystoreAlias, samlKeystorePassword);
    return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlKeystoreAlias);
}
```

在这里，我们必须为KeyManager bean 创建并提供一个 Keystore。我们可以使用 JRE 命令创建自签名密钥和密钥库：

```shell
keytool -genkeypair -alias baeldungspringsaml -keypass baeldungsamlokta -keystore saml-keystore.jks
```

### 5.4. 元数据管理器

[然后，我们将使用ExtendedMetadataDelegate](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/saml/metadata/ExtendedMetadataDelegate.html)实例将 IdP 元数据配置到我们的Spring Boot应用程序中：

```java
@Bean
@Qualifier("okta")
public ExtendedMetadataDelegate oktaExtendedMetadataProvider() throws MetadataProviderException {
    org.opensaml.util.resource.Resource resource = null
    try {
        resource = new ClasspathResource("/saml/metadata/sso.xml");
    } catch (ResourceException e) {
        e.printStackTrace();
    }
    Timer timer = new Timer("saml-metadata")
    ResourceBackedMetadataProvider provider = new ResourceBackedMetadataProvider(timer,resource);
    provider.setParserPool(parserPool());
    return new ExtendedMetadataDelegate(provider, extendedMetadata());
}

@Bean
@Qualifier("metadata")
public CachingMetadataManager metadata() throws MetadataProviderException, ResourceException {
    List<MetadataProvider> providers = new ArrayList<>(); 
    providers.add(oktaExtendedMetadataProvider());
    CachingMetadataManager metadataManager = new CachingMetadataManager(providers);
    metadataManager.setDefaultIDP(defaultIdp);
    return metadataManager;
}
```

在这里，我们解析了sso.xml文件中的元数据，该文件包含 IdP 元数据 XML，在查看设置说明时从 Okta 开发人员帐户。

同样，defaultIdp变量包含从 Okta 开发人员帐户的 IdP 颁发者 URL。

### 5.5. XML 解析

对于 XML 解析，我们可以使用[StaticBasicParserPool](https://javadoc.io/doc/org.opensaml/xmltooling/latest/org/opensaml/xml/parse/StaticBasicParserPool.html)类的实例：

```java
@Bean(initMethod = "initialize")
public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
}

@Bean(name = "parserPoolHolder")
public ParserPoolHolder parserPoolHolder() {
    return new ParserPoolHolder();
}
```

### 5.6. SAML 处理器

然后，我们需要一个处理器来解析来自 HTTP 请求的 SAML 消息：

```java
@Bean
public HTTPPostBinding httpPostBinding() {
    return new HTTPPostBinding(parserPool(), VelocityFactory.getEngine());
}

@Bean
public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
}

@Bean
public SAMLProcessorImpl processor() {
    ArrayList<SAMLBinding> bindings = new ArrayList<>();
    bindings.add(httpRedirectDeflateBinding());
    bindings.add(httpPostBinding());
    return new SAMLProcessorImpl(bindings);
}
```

在这里，我们针对 Okta 开发人员帐户中的配置使用了 POST 和 Redirect 绑定。

### 5.7. SAMLAuthenticationProvider实现

[最后，我们需要SAMLAuthenticationProvider](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/saml/SAMLAuthenticationProvider.html)类的自定义实现来检查[ExpiringUsernameAuthenticationToken](https://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/providers/ExpiringUsernameAuthenticationToken.html)类的实例并设置获得的权限：

```java
public class CustomSAMLAuthenticationProvider extends SAMLAuthenticationProvider {
    @Override
    public Collection<? extends GrantedAuthority> getEntitlements(SAMLCredential credential, Object userDetail) {
        if (userDetail instanceof ExpiringUsernameAuthenticationToken) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.addAll(((ExpiringUsernameAuthenticationToken) userDetail).getAuthorities());
            return authorities;
        } else {
            return Collections.emptyList();
        }
    }
}

```

此外，我们应该将CustomSAMLAuthenticationProvider配置为SecurityConfig类中的 bean ：

```java
@Bean
public SAMLAuthenticationProvider samlAuthenticationProvider() {
    return new CustomSAMLAuthenticationProvider();
}
```

### 5.8. 安全配置

最后，我们将使用已经讨论过的samlEntryPoint和samlFilter配置基本的 HTTP 安全性：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.httpBasic().authenticationEntryPoint(samlEntryPoint);

    http
      .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
      .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
      .addFilterBefore(samlFilter(), CsrfFilter.class);

    http
      .authorizeRequests()
      .antMatchers("/").permitAll()
      .anyRequest().authenticated();

    http
      .logout()
      .addLogoutHandler((request, response, authentication) -> {
          response.sendRedirect("/saml/logout");
      });
}
```

瞧！我们完成了 Spring Security SAML 配置，允许用户登录到 IdP，然后从 IdP 接收 XML 格式的用户身份验证详细信息。最后，它验证用户令牌以允许访问我们的 Web 应用程序。

## 6.家庭控制器

现在我们的 Spring Security SAML 配置已经与 Okta 开发者帐户设置一起准备好了，我们可以设置[一个简单的控制器来提供登录页面和主页](https://www.baeldung.com/spring-mvc)。

### 6.1。索引和授权映射

首先，让我们添加到默认目标 URI (/)和 / auth URI 的映射：

```java
@RequestMapping("/")
public String index() {
    return "index";
}

@GetMapping(value = "/auth")
public String handleSamlAuth() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
        return "redirect:/home";
    } else {
        return "/";
    }
}
```

然后，我们将添加一个简单的index.html，允许用户使用登录链接重定向 Okta SAML 身份验证：

```html
<!doctype html>
<html>
<head>
    <title>Tuyucheng Spring Security SAML</title>
</head>
<body>
<h3><Strong>Welcome to Tuyucheng Spring Security SAML</strong></h3>
<a th:href="@{/auth}">Login</a>
</body>
</html>
```

现在，我们已经准备好运行我们的Spring Boot应用程序并通过http://localhost:8080/访问它：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.20.44-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.20.44-AM.png)
单击登录链接时，应打开 Okta 登录页面：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.21.07-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.21.07-AM.png)

### 6.2. 主页

接下来，让我们将映射添加到/home URI，以在成功通过身份验证时重定向用户：

```java
@RequestMapping("/home")
public String home(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    model.addAttribute("username", authentication.getPrincipal());
    return "home";
}
```

此外，我们将添加home.html以显示登录用户和注销链接：

```html
<!doctype html>
<html>
<head>
    <title>Tuyucheng Spring Security SAML: Home</title>
</head>
<body>
<h3><Strong>Welcome!</strong><br/>You are successfully logged in!</h3>
<p>You are logged as <span th:text="${username}">null</span>.</p>
<small>
    <a th:href="@{/logout}">Logout</a>
</small>
</body>
</html>
```

成功登录后，我们应该看到主页：

[![img](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.22.17-AM.png)](https://www.baeldung.com/wp-content/uploads/2021/03/Screen-Shot-2021-02-24-at-7.22.17-AM.png)

## 7. 总结

在本教程中，我们讨论了 Spring Security SAML 与 Okta 的集成。

首先，我们使用 SAML 2.0 Web 集成设置了一个 Okta 开发人员帐户。然后，我们创建了一个带有所需 Maven 依赖项的Spring Boot项目。

接下来，我们为 Spring Security SAML 进行了所有必需的设置，例如samlEntryPoint、samlFilter、元数据处理和 SAML 处理器。

最后，我们创建了一个控制器和几个页面，例如index和home来测试我们与 Okta 的 SAML 集成。
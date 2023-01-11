## 一、概述

在本教程中，我们将概述 Spring Security Kerberos。

我们将用 Java 编写一个 Kerberos 客户端，它授权自己访问我们的 Kerberos 服务。我们将运行我们自己的嵌入式密钥分发中心来执行完整的端到端 Kerberos 身份验证。由于[Spring Security Kerberos](https://docs.spring.io/spring-security-kerberos/docs/1.0.1.RELEASE/reference/htmlsingle/) ，所有这些都不需要任何外部基础设施。

## 2. Kerberos 及其好处

Kerberos 是麻省理工学院在 1980 年代创建的一种网络身份验证协议，特别适用于在网络上集中进行身份验证。

1987 年，麻省理工学院将其发布到[开源](https://github.com/krb5/krb5)社区，目前仍在积极开发中。2005 年，它被推崇为[RFC 4120](https://tools.ietf.org/html/rfc4120)下的 IETF 标准。 

通常，Kerberos**用于企业环境**。在那里，它以**用户不必分别对每个服务进行身份验证**的方式保护环境。这种架构解决方案称为***单点登录\***。

简单地说，Kerberos 是一个票务系统。用户**验证一次** 并**收到票证授予票证**(TGT)。 **然后，网络基础设施将该 TGT 交换为服务票证。**这些服务票证允许用户与基础设施服务交互，只要 TGT 有效，通常持续几个小时。

因此，用户只需登录一次就很好了。但也有安全方面的好处：在这样的环境中，**用户的密码永远不会通过网络发送**。相反，Kerberos 使用它作为一个因素来生成另一个密钥，该密钥将用于消息加密和解密。

另一个好处是**我们可以从一个中心位置管理用户，**比如说一个由 LDAP 支持的地方。因此，如果我们为给定用户禁用集中式数据库中的帐户，那么我们将撤销他在我们基础设施中的访问权限。因此，管理员不必在每个服务中分别撤销访问权限。

[Spring 中的 SPNEGO/Kerberos 身份验证简介](https://www.baeldung.com/spring-security-kerberos)提供了对该技术的深入概述。

## 3. Kerberos 环境

因此，让我们创建一个使用 Kerberos 协议进行身份验证的环境。该环境将由三个同时运行的独立应用程序组成。

首先，**我们将有一个密钥分发中心**作为身份验证点。接下来，我们将编写一个客户端和一个我们将配置为使用 Kerberos 协议的服务应用程序。

现在，运行 Kerberos 需要一些安装和配置。但是，我们将利用[Spring Security Kerberos](https://docs.spring.io/spring-security-kerberos/docs/1.0.1.RELEASE/reference/htmlsingle/)，因此我们将在嵌入式模式下以编程方式运行密钥分发中心。此外，下面显示的*MiniKdc*在使用 Kerberized 基础设施进行集成测试时很有用。

### 3.1. 运行密钥分发中心

首先，我们将启动我们的密钥分发中心，它将为我们发行 TGT：

```java
String[] config = MiniKdcConfigBuilder.builder()
  .workDir(prepareWorkDir())
  .principals("client/localhost", "HTTP/localhost")
  .confDir("minikdc-krb5.conf")
  .keytabName("example.keytab")
  .build();

MiniKdc.main(config);复制
```

基本上，我们已经为*MiniKdc*提供了一组主体和一个配置文件；此外，我们已经告诉 *MiniKdc*如何调用它生成的*[密钥表。](https://web.mit.edu/kerberos/krb5-devel/doc/basic/keytab_def.html)*

*MiniKdc*将生成一个[*krb5.conf*](https://web.mit.edu/kerberos/krb5-1.12/doc/admin/conf_files/krb5_conf.html)文件，我们将其提供给我们的客户端和服务应用程序。该文件包含在哪里可以找到我们的 KDC 的信息——给定领域的主机和端口。

*MiniKdc.main*启动 KDC 并应输出如下内容：

```plaintext
Standalone MiniKdc Running
---------------------------------------------------
  Realm           : EXAMPLE.COM
  Running at      : localhost:localhost
  krb5conf        : .\spring-security-sso\spring-security-sso-kerberos\krb-test-workdir\krb5.conf

  created keytab  : .\spring-security-sso\spring-security-sso-kerberos\krb-test-workdir\example.keytab
  with principals : [client/localhost, HTTP/localhost]复制
```

### 3.2. 客户申请

我们的客户端将是一个 Spring Boot 应用程序，它使用*RestTemplate* 来调用外部 REST API。

**但是**，我们将**改用\*KerberosRestTemplate\***。它需要密钥表和客户的委托人：

```java
@Configuration
public class KerberosConfig {

    @Value("${app.user-principal:client/localhost}")
    private String principal;

    @Value("${app.keytab-location}")
    private String keytabLocation;

    @Bean
    public RestTemplate restTemplate() {
        return new KerberosRestTemplate(keytabLocation, principal);
    }
}复制
```

就是这样！*KerberosRestTemplate* 为我们协商 Kerberos 协议的客户端。

因此，让我们创建一个快速类，它将从托管在端点*app.access-url*的 Kerberized 服务中查询一些数据：

```java
@Service
class SampleClient {

    @Value("${app.access-url}")
    private String endpoint;

    private RestTemplate restTemplate;

    // constructor, getter, setter

    String getData() {
        return restTemplate.getForObject(endpoint, String.class);
    }
}复制
```

那么，让我们现在创建我们的服务应用程序，这样这个类就有东西可以调用了！

### 3.3. 服务申请

我们将使用 Spring Security，并使用适当的特定于 Kerberos 的 bean 对其进行配置。

另外，请注意该服务也将有其主体并使用密钥表：

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends AbstractHttpConfigurer<WebSecurityConfig, HttpSecurity> {

    @Value("${app.service-principal}")
    private String servicePrincipal;

    @Value("${app.keytab-location}")
    private String keytabLocation;

    public static WebSecurityConfig securityConfig() {
        return new WebSecurityConfig();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = 
        http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(spnegoAuthenticationProcessingFilter(authenticationManager),
           BasicAuthenticationFilter.class);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .authenticationEntryPoint(spnegoEntryPoint())
            .and()
            .authorizeRequests()
            .antMatchers("/", "/home")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll()
            .and()
            .apply(securityConfig());
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(kerberosAuthenticationProvider())
            .authenticationProvider(kerberosServiceAuthenticationProvider())
            .build();
    }

    @Bean
    public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
	KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
	// provider configuration
	return provider;
    }

    @Bean
    public SpnegoEntryPoint spnegoEntryPoint() {
	return new SpnegoEntryPoint("/login");
    }

    @Bean
    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
		AuthenticationManager authenticationManager) {
	SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
	// filter configuration
	return filter;
    }

    @Bean
    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
	KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
	// auth provider configuration 
	return provider;
    }

    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
	SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
	// validator configuration
	return ticketValidator;
    }

}复制
```

[介绍文章](https://www.baeldung.com/spring-security-kerberos)包含上面的所有实现，因此为了简洁起见，我们在这里省略了完整的方法。

请注意，我们已经为[SPNEGO 身份验证](https://tools.ietf.org/html/rfc4559)配置了 Spring Security 。这样，我们将能够通过 HTTP 协议进行身份验证，尽管我们也可以[使用核心 Java 实现 SPNEGO 身份验证](https://docs.oracle.com/en/java/javase/11/security/part-vi-http-spnego-authentication.html)。

## 4.测试

现在，我们将运行一个集成测试，以显示**我们的客户端成功地通过 Kerberos 协议从外部服务器检索数据**。要运行此测试，我们需要运行基础设施，因此必须启动*MiniKdc和我们的服务应用程序。*

基本上，我们将使用客户端应用程序中的*SampleClient*向我们的服务应用程序发出请求。让我们测试一下：

```java
@Autowired
private SampleClient sampleClient;

@Test
public void givenKerberizedRestTemplate_whenServiceCall_thenSuccess() {
    assertEquals("data from kerberized server", sampleClient.getData());
}复制
```

请注意，我们还可以通过在没有 KerberizedRestTemplate 的情况下访问服务来证明*KerberizedRestTemplate*很重要：

```java
@Test
public void givenRestTemplate_whenServiceCall_thenFail() {
    sampleClient.setRestTemplate(new RestTemplate());
    assertThrows(RestClientException.class, sampleClient::getData);
}复制
```

作为旁注，**我们的第二个测试有可能重新使用已存储在[凭证缓存](https://web.mit.edu/kerberos/krb5-1.12/doc/basic/ccache_def.html)**中的票证。这是由于*HttpUrlConnection*中使用的自动 SPNEGO 协商而发生的。

结果，**数据可能实际上返回，使我们的测试无效。**然后，根据我们的需要，我们可以通过系统属性*http.use.global.creds=false 禁用票证缓存使用。*

## 5.结论

在本教程中， **我们探讨了用于集中式用户管理**的 Kerberos，以及 Spring Security 如何支持 Kerberos 协议和 SPNEGO 身份验证机制。

我们使用 *MiniKdc*建立嵌入式 KDC，还创建了一个非常简单的 Kerberized 客户端和服务器。这个设置对于探索来说很方便，尤其是当我们创建一个集成测试来测试的时候。

现在，我们只是触及了表面。要深入了解，请查看 Kerberos [wiki 页面](https://en.wikipedia.org/wiki/Kerberos_(protocol))或其[RFC](https://tools.ietf.org/html/rfc4120)。此外，[官方文档页面](https://web.mit.edu/kerberos/krb5-latest/doc/)也会很有用。除此之外，要了解如何在核心 java 中完成这些事情，[以下 Oracle 的教程](https://docs.oracle.com/en/java/javase/11/security/advanced-security-programming-java-se-authentication-secure-communication-and-single-sign1.html)将详细说明。
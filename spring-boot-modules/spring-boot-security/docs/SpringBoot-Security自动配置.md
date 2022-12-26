## 1. 概述

在本教程中，我们将了解Spring Boot的自以为是的安全方法。

简而言之，我们将重点关注默认安全配置以及如何在需要时禁用或自定义它。

## 2. 默认安全设置

为了向我们的Spring Boot应用程序添加安全性，我们需要添加security starter依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

这还将包括包含初始/默认安全配置的SecurityAutoConfiguration类。

请注意我们在这里没有指定版本，假设项目已经使用Boot作为父项目。

**默认情况下，为应用程序启用身份验证。此外，内容协商用于确定是否应使用basic或formLogin**。

有一些预定义的属性：

```plaintext
spring.security.user.name
spring.security.user.password
```

如果我们没有使用预定义的属性spring.security.user.password配置密码并启动应用程序，则会随机生成一个默认密码并打印在控制台日志中：

```shell
Using default security password: c8be15de-4488-4490-9dc6-fab3f91435c6
```

有关更多默认值，请参阅[Spring Boot常用应用属性](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)参考页面的安全属性部分。

## 3. 禁用自动配置

要放弃安全自动配置并添加我们自己的配置，我们需要排除SecurityAutoConfiguration类。

我们可以通过简单的指定@SpringBootApplication注解的exclude属性来做到这一点：

```java
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SpringBootSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityApplication.class, args);
	}
}
```

或者我们可以在application.properties文件中添加一些配置：

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
```

但是，在某些特殊情况下，此设置还不够。

例如，几乎每个Spring Boot应用程序都是通过类路径中的Actuator启动的，**这会导致问题，因为另一个自动配置类需要我们刚刚排除的那个**。因此，应用程序将无法启动。

为了解决这个问题，我们需要排除那个类；并且，具体到Actuator的情况，我们还需要排除ManagementWebSecurityAutoConfiguration。

### 3.1 禁用与超越安全自动配置

禁用自动配置和超越自动配置之间存在显著差异。

禁用它就像添加Spring Security依赖项和从头开始整个设置一样，这在以下几种情况下很有用：

1.  将应用程序安全性与自定义安全提供程序集成
2.  将具有现有安全设置的遗留Spring应用程序迁移到Spring Boot

**但大多数情况下，我们不需要完全禁用安全自动配置**。

这是因为Spring Boot配置为允许通过添加我们的新/自定义配置类来超越自动配置的安全性。这通常更容易，因为我们只是自定义现有的安全设置来满足我们的需求。

## 4. 配置Spring Boot Security

如果我们选择了禁用安全自动配置的路径，我们自然需要提供我们自己的配置。

正如我们之前所讨论的，这是默认的安全配置，然后我们通过修改属性文件来自定义它。

例如，我们可以通过添加自己的密码来覆盖默认密码：

```properties
spring.security.user.password=password
```

如果我们想要更灵活的配置，例如有多个用户和角色，我们需要使用一个完整的@Configuration类：

```java
@Configuration
@EnableWebSecurity
public class BasicConfiguration {

	@Bean
	public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("user")
			.password(passwordEncoder.encode("password"))
			.roles("USER")
			.build();

		UserDetails admin = User.withUsername("admin")
			.password(passwordEncoder.encode("admin"))
			.roles("USER", "ADMIN")
			.build();

		return new InMemoryUserDetailsManager(user, admin);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.anyRequest()
			.authenticated()
			.and()
			.httpBasic();
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder;
	}
}
```

**如果我们禁用默认安全配置，则@EnableWebSecurity注解是至关重要的**。如果缺少该注解，应用程序将无法启动。

另外，请注意，**在使用Spring Boot 2时，我们需要使用PasswordEncoder来设置密码**。有关更多详细信息，请参阅我们关于[Spring Security 5中默认密码编码器]()的指南。

现在我们应该通过几个快速实时测试来验证我们的安全配置是否正确应用：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class BasicConfigurationIntegrationTest {

	TestRestTemplate restTemplate;
	URL base;
	@LocalServerPort int port;

	@Before
	public void setUp() throws MalformedURLException {
		restTemplate = new TestRestTemplate("user", "password");
		base = new URL("http://localhost:" + port);
	}

	@Test
	public void whenLoggedUserRequestsHomePage_ThenSuccess() throws IllegalStateException, IOException {
		ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().contains("Tuyucheng"));
	}

	@Test
	public void whenUserWithWrongCredentials_thenUnauthorizedPage() throws Exception {
		restTemplate = new TestRestTemplate("user", "wrongpassword");
		ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertTrue(response.getBody().contains("Unauthorized"));
	}
}
```

Spring Security实际上是Spring Boot Security的幕后推手，因此可以使用此工具完成的任何安全配置或此工具支持的任何集成也可以在Spring Boot中实现。

## 5. Spring Boot OAuth2自动配置(使用遗留堆栈)

Spring Boot为OAuth2提供了专门的自动配置支持。

Spring Boot 1.x附带的[Spring Security OAuth](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html5/)支持在以后的Boot版本中被删除，取而代之的是[Spring Security 5](https://docs.spring.io/spring-security/site/docs/5.2.12.RELEASE/reference/html/oauth2.html)附带的一流OAuth支持，我们将在下一节中看到如何使用它。

对于遗留堆栈(使用Spring Security OAuth)，我们首先需要添加一个Maven依赖项来开始设置我们的应用程序：

```xml
<dependency>
   <groupId>org.springframework.security.oauth</groupId>
   <artifactId>spring-security-oauth2</artifactId>
</dependency>
```

此依赖项包括一组能够触发OAuth2AutoConfiguration类中定义的自动配置机制的类。

现在，根据应用程序的范围，我们有多种选择可以继续。

### 5.1 OAuth2授权服务器自动配置

如果我们希望我们的应用程序成为OAuth2提供程序，我们可以使用@EnableAuthorizationServer。

在启动时，我们会在日志中注意到自动配置类将为我们的授权服务器生成一个客户端ID和一个客户端密钥，当然还有一个用于基本身份验证的随机密码：

```shell
Using default security password: a81cb256-f243-40c0-a585-81ce1b952a98
security.oauth2.client.client-id = 39d2835b-1f87-4a77-9798-e2975f36972e
security.oauth2.client.client-secret = f1463f8b-0791-46fe-9269-521b86c55b71
```

这些凭据可用于获取访问令牌：

```shell
curl -X POST -u 39d2835b-1f87-4a77-9798-e2975f36972e:f1463f8b-0791-46fe-9269-521b86c55b71 \
 -d grant_type=client_credentials 
 -d username=user 
 -d password=a81cb256-f243-40c0-a585-81ce1b952a98 \
 -d scope=write  http://localhost:8080/oauth/token
```

我们的[另一篇文章]()提供了有关该主题的更多详细信息。

### 5.2 其他Spring Boot OAuth2自动配置设置

Spring Boot OAuth2还涵盖了一些其他用例：

1.  [资源服务器]()：@EnableResourceServer
2.  [客户端应用程序]()：@EnableOAuth2Sso或@EnableOAuth2Client

如果我们需要我们的应用程序成为这些类型之一，我们只需要向应用程序属性添加一些配置，如链接所详述。

所有OAuth2特定属性都可以在[Spring Boot常用应用属性](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)中找到。

## 6. Spring Boot OAuth2自动配置(使用新堆栈)

要使用新堆栈，我们需要根据我们要配置的内容(授权服务器、资源服务器或客户端应用程序)添加依赖项。

### 6.1 OAuth2授权服务器支持

正如我们所看到的，Spring Security OAuth堆栈提供了将授权服务器设置为Spring应用程序的可能性，但是该项目已被弃用，目前Spring不支持自己的授权服务器。相反，建议使用现有的成熟供应商，例如Okta、Keycloak和ForgeRock。

但是，Spring Boot使我们可以轻松配置此类提供程序。对于Keycloak配置示例，我们可以参考[在Spring Boot中使用Keycloak的快速指南]()或[嵌入在Spring Boot应用程序中的Keycloak]()。

### 6.2 OAuth2资源服务器支持

要包含对资源服务器的支持，我们需要添加此依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>    
</dependency>
```

有关最新版本信息，请前往[Maven Central](https://search.maven.org/search?q=a:spring-boot-starter-oauth2-resource-server)。

此外，在我们的安全配置中，我们需要包含[oauth2ResourceServer()](https://docs.spring.io/spring-security-oauth2-boot/docs/2.2.x-SNAPSHOT/reference/html/boot-features-security-oauth2-resource-server.html) DSL：

```java
@Configuration
public class JWTSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
          ...
          .oauth2ResourceServer(oauth2 -> oauth2.jwt());
          ...
	}
}
```

我们的[OAuth 2.0资源服务器与Spring Security 5]()给出了这个主题的深入介绍。

### 6.3 OAuth2客户端支持

与我们配置资源服务器的方式类似，客户端应用程序也需要自己的依赖项和DSL。

这是OAuth2客户端支持的特定依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

```

最新版本可以在[Maven Central](https://search.maven.org/search?q=a:spring-boot-starter-oauth2-client)找到。

Spring Security 5还通过其[oath2Login()]() DSL提供一流的登录支持。

有关新堆栈中SSO支持的详细信息，请参阅我们的文章[使用Spring Security OAuth2进行简单单点登录](https://www.baeldung.com/sso-spring-security-oauth2)。

## 7. 总结

在本文中，我们重点介绍了Spring Boot提供的默认安全配置，我们看到了如何禁用或覆盖安全自动配置机制，然后我们研究了如何应用新的安全配置。

OAuth2的源代码可以在我们的OAuth2 GitHub仓库中找到，适用于[旧](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-legacy)堆栈和[新](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-rest)堆栈。
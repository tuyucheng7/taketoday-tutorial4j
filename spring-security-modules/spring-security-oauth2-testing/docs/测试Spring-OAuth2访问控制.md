## 1. 概述

在本教程中，我们将探索使用模拟身份测试Spring OAuth2访问控制规则的选项。

我们将使用来自spring-security-test和[spring-addons](https://github.com/ch4mpy/spring-addons)的MockMvc请求后处理器、WebTestClient突变器和测试注解。

## 2. 为什么要使用Spring-Addons？

在OAuth2领域，spring-security-test仅提供请求后处理器和突变器，它们分别需要MockMvc或WebTestClient请求的上下文。这对于@Controller来说可能很好，但是在@Service或@Repository上测试使用方法安全性(@PreAuthorize、@PostFilter等)定义的访问控制规则是一个问题。

**使用@WithMockJwtAuth或@WithOidcLogin等注解，我们可以在Servlet和响应式应用程序中对任何类型的@Component进行单元测试时mock安全上下文**。这就是为什么我们将在一些测试中使用[spring-addons-oauth2-test](https://central.sonatype.com/artifact/com.c4-soft.springaddons/spring-addons-oauth2-test/6.1.0)的原因：它为我们提供了大多数Spring OAuth2身份验证实现的此类注解。

## 3. 我们将测试什么？

配套的[GitHub仓库](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-security-modules/spring-security-oauth2-testing)包含两个共享以下功能的[资源服务器]：(https://www.baeldung.com/spring-security-oauth-resource-server)

- **使用JWT解码器保护**(而不是不透明的令牌内省)
- **需要ROLE_AUTHORIZED_PERSONNEL权限才能访问/secured-route和/secured-method**
- **如果身份验证丢失或无效(过期、颁发者错误等)，则返回401；如果访问被拒绝(缺少角色)，则返回403**
- 公开任何经过身份验证的用户都可以访问的/greet端点
- 使用配置来保护/secured-route(分别使用requestMatcher和pathMatcher用于Servlet和响应式应用程序)
- 使用方法注解来保护/secured-method
- 将消息生成委托给MessageService(我们将在@Controller单元测试期间对其进行Mock)
- 使用@PreAuthorize保护MessageService的方法
- 在@Service中，从安全上下文中的JwtAuthenticationToken中提取数据

为了说明Servlet和响应式测试API之间的细微差别，一个是Servlet([浏览代码](https://github.com/tu-yucheng/taketoday-tutorial4j/blob/master/spring-security-modules/spring-security-oauth2-testing/servlet-resource-server/src/main/java/cn/tuyucheng/taketoday/ServletResourceServerApplication.java))，第二个是响应式应用程序([浏览代码](https://github.com/tu-yucheng/taketoday-tutorial4j/blob/master/spring-security-modules/spring-security-oauth2-testing/reactive-resource-server/src/main/java/cn/tuyucheng/taketoday/ReactiveResourceServerApplication.java))。

在本文中，我们将重点介绍如何在单元测试和集成测试中测试上述规范中定义的访问控制规则，并**断言响应的HTTP状态与模拟用户身份的预期匹配**(或者在对@Controller以外的其他@Component进行单元测试时引发异常，例如使用@PreAuthorize保护@Service或@Repository，@PostFilter和类似)。

**所有测试都在没有任何授权服务器的情况下通过**，但如果我们想启动被测资源服务器并使用[Postman](https://www.baeldung.com/postman-testing-collections)等工具查询它，我们将需要一个启动并运行的服务器。

## 4. Mock授权的单元测试

“单元测试”是指在隔离任何其他依赖项(我们将mock)的情况下对单个@Component进行测试。被测试的@Component可以是@WebMvcTest或@WebFluxTest中的@Controller，也可以是普通JUnit测试中的任何其他安全@Service、@Repository等。

**MockMvc或WebTestClient会忽略Authorization标头**，并且无需提供有效的访问令牌。当然，我们可以实例化或mock任何身份验证实现，并在每次测试开始时手动创建安全上下文，但这太乏味了。相反，**我们将使用spring-security-test MockMvc请求后处理器、WebTestClient突变器或[spring-addons](https://github.com/ch4mpy/spring-addons)注解来使用我们选择的mock Authentication实例填充测试安全上下文**。

我们将使用@WithMockUser只是为了查看它构建了一个UsernamePasswordAuthenticationToken实例，这通常是一个问题，因为OAuth2运行时配置将其他类型的Authentication放在安全上下文中：

-   带有JWT解码器的资源服务器的JwtAuthenticationToken
-   具有访问令牌内省的资源服务器的BearerTokenAuthentication(opaqueToken)
-   使用oauth2Login的客户端的OAuth2AuthenticationToken
-   如果我们决定在自定义身份验证转换器中返回另一个Authentication实例而不是Spring默认实例，那么绝对可以。因此，从技术上讲，OAuth2身份验证转换器可以返回UsernamePasswordAuthenticationToken实例并在测试中使用@WithMockUser，但这是一个非常不自然的选择，我们不会在这里使用它。

### 4.1 测试设置

**对于@Controller单元测试，我们应该用@WebMvcTest修饰测试类(用于Servlet应用程序)，用@WebFluxTest修饰响应式应用程序**。

Spring为我们自动装配MockMvc或WebTestClient，当我们编写控制器单元测试时，我们将mock MessageService。

这是在Servlet应用程序中一个空的@Controller单元测试的样子：

```java
@WebMvcTest(controllers = GreetingController.class)
class GreetingControllerTest {

    @MockBean
    MessageService messageService;

    @Autowired
    MockMvc mockMvc;

    // ...
}
```

这是在响应式应用程序中空的@Controller单元测试的样子：

```java
@WebFluxTest(controllers = GreetingController.class)
class GreetingControllerTest {
    private static final AnonymousAuthenticationToken ANONYMOUS = new AnonymousAuthenticationToken(
          "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @MockBean
    MessageService messageService;

    @Autowired
    WebTestClient webTestClient;

    // ...
}
```

现在，让我们看看如何断言HTTP状态代码符合我们之前设置的规范。

### 4.2 使用MockMvc后处理器进行单元测试

**要使用JwtAuthenticationToken(这是使用JWT解码器的资源服务器的默认Authentication类型)填充测试安全上下文，我们将使用jwt后处理器处理MockMvc请求**。

首先，我们为MockMvc声明jwt请求后处理器的静态导入：

```java
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
```

然后我们通过在MockHttpServletRequestBuilder上调用with(jwt())来激活和自定义它，并断言结果状态码符合我们的规范，具体取决于使用jwt()后处理器配置的用户身份。

让我们首先看看当我们mock未经授权的请求时如何断言MockMvc返回401，以及如何使用jwt()请求后处理器mock OAuth2授权以使请求成功：

```java
@Test
void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
    mockMvc.perform(get("/greet").with(anonymous()))
        .andExpect(status().isUnauthorized());
}

@Test
void givenUserIsAuthenticated_whenGetGreet_thenOk() throws Exception {
    var greeting = "Whatever the service returns";
    when(messageService.greet()).thenReturn(greeting);

    mockMvc.perform(get("/greet").with(jwt().authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
        .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
        .andExpect(status().isOk())
        .andExpect(content().string(greeting));

    verify(messageService, times(1)).greet();
}
```

然后我们可以检查，在实现基于角色的访问控制的端点上，MockMvc请求在未授权时实际上返回401，在配置了预期权限时返回200，在授权但缺少预期权限时返回403：

```java
@Test
void givenRequestIsAnonymous_whenGetSecuredRoute_thenUnauthorized() throws Exception {
    mockMvc.perform(get("/secured-route").with(anonymous()))
        .andExpect(status().isUnauthorized());
}

@Test
void givenUserIsGrantedWithRoleAuthorizedPersonnel_whenGetSecuredRoute_thenOk() throws Exception {
    var secret = "Secret!";
    when(messageService.getSecret()).thenReturn(secret);

    mockMvc.perform(get("/secured-route").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL"))))
        .andExpect(status().isOk())
        .andExpect(content().string(secret));
}

@Test
void givenUserIsNotGrantedWithRoleAuthorizedPersonnel_whenGetSecuredRoute_thenForbidden() throws Exception {
    mockMvc.perform(get("/secured-route").with(jwt().authorities(new SimpleGrantedAuthority("admin"))))
        .andExpect(status().isForbidden());
}
```

### 4.3 使用WebTestClient突变器进行单元测试

在响应式资源服务器中，安全上下文中的Authentication类型与Servlet中的相同：JwtAuthenticationToken。因此，我们将为WebTestClient使用mockJwt突变器。

首先，为WebTestClient声明一个mockJwt()突变器的静态导入：

```java
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
```

然后通过在WebTestClient上调用mutateWith(mockJwt())来激活它，并断言结果码状态符合我们的规范。

让我们首先看看如何断言WebTestClient在匿名请求时返回401以及如何使用mockJwt()突变器mock OAuth2身份验证：

```java
@Test
void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
    webTestClient.mutateWith(mockAuthentication(ANONYMOUS))
        .get()
        .uri("/greet")
        .exchange()
        .expectStatus()
        .isUnauthorized();
}

@Test
void givenUserIsAuthenticated_whenGetGreet_thenOk() throws Exception {
    var greeting = "Whatever the service returns";
    when(messageService.greet()).thenReturn(Mono.just(greeting));

    webTestClient.mutateWith(mockJwt().authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
        .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy")))
        .get()
        .uri("/greet")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo(greeting);

    verify(messageService, times(1)).greet();
}
```

然后我们可以检查，在实现基于角色的访问控制的端点上，WebTestClient请求在未授权时实际上返回401，在配置了预期权限时返回200，在授权但缺少预期权限时返回403：

```java
@Test
void givenRequestIsAnonymous_whenGetSecuredRoute_thenUnauthorized() throws Exception {
    webTestClient.mutateWith(mockAuthentication(ANONYMOUS))
        .get()
        .uri("/secured-route")
        .exchange()
        .expectStatus()
        .isUnauthorized();
}

@Test
void givenUserIsGrantedWithRoleAuthorizedPersonnel_whenGetSecuredRoute_thenOk() throws Exception {
    var secret = "Secret!";
    when(messageService.getSecret()).thenReturn(Mono.just(secret));

    webTestClient.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
        .get()
        .uri("/secured-route")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo(secret);
}

@Test
void givenUserIsNotGrantedWithRoleAuthorizedPersonnel_whenGetSecuredRoute_thenForbidden() throws Exception {
    webTestClient.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("admin")))
        .get()
        .uri("/secured-route")
        .exchange()
        .expectStatus()
        .isForbidden();
}
```

### 4.4 使用Spring-Addons的注解对控制器进行单元测试

我们可以在Servlet和响应式应用程序中以完全相同的方式使用测试注解。

我们只需要添加对[spring-addons-oauth2-test](https://central.sonatype.com/artifact/com.c4-soft.springaddons/spring-addons-oauth2-test/6.1.2)的依赖：

```xml
<dependency>
    <groupId>com.c4-soft.springaddons</groupId>
    <artifactId>spring-addons-oauth2-test</artifactId>
    <version>6.1.0</version>
    <scope>test</scope>
</dependency>
```

现在，我们可以从测试主体中删除身份mock，使用注解修饰测试方法来代替：

```java
@Test
@WithAnonymousUser
void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
    mockMvc.perform(get("/greet"))
        .andExpect(status().isUnauthorized());
}

@Test
@WithMockJwtAuth(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, claims = @OpenIdClaims(preferredUsername = "ch4mpy"))
void givenUserIsAuthenticated_whenGetGreet_thenOk() throws Exception {
    var greeting = "Whatever the service returns";
    when(messageService.greet()).thenReturn(greeting);

    mockMvc.perform(get("/greet"))
        .andExpect(status().isOk())
        .andExpect(content().string(greeting));

    verify(messageService, times(1)).greet();
}
```

WebTestClient的身份mock是完全一样的：

```java
@Test
@WithAnonymousUser
void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
    webTestClient.get()
        .uri("/greet")
        .exchange()
        .expectStatus()
        .isUnauthorized();
}

@Test
@WithMockJwtAuth(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, claims = @OpenIdClaims(preferredUsername = "ch4mpy"))
void givenUserIsAuthenticated_whenGetGreet_thenOk() throws Exception {
    var greeting = "Whatever the service returns";
    when(messageService.greet()).thenReturn(Mono.just(greeting));

    webTestClient.get()
        .uri("/greet")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo(greeting);

    verify(messageService, times(1)).greet();
}
```

**注解绝对非常适合BDD范例**：

-   先决条件(Given)在文本上下文中(修饰测试的注解)
-   只有被测试的代码执行(When)和结果断言(Then)在测试体中

### 4.5 单元测试@Service或@Repository安全方法

在测试@Controller时，请求MockMvc后处理器(或WebTestClient突变器)和注解之间的选择主要是团队偏好的问题，但**要对MessageService::getSecret访问控制进行单元测试，spring-security-test不再是一个选项**，我们需要spring-addons注解。

这是JUnit设置：

-   使用@ExtendWith(SpringExtension.class)激活Spring自动装配
-   @Import({MessageService.class})并@Autowire它以获取检测的实例
-   在Servlet应用程序中使用@EnableMethodSecurity或在响应式应用程序中使用@EnableReactiveMethodSecurity修饰测试类

我们将断言，每当用户缺少ROLE_AUTHORIZED_PERSONNEL权限时，MessageService都会抛出异常。

这是Servlet应用程序中@Service的完整单元测试：

```java
@Import({ MessageService.class })
@ExtendWith(SpringExtension.class)
@EnableMethodSecurity
class MessageServiceUnitTest {
    @Autowired
    MessageService messageService;

    @Test
    void givenSecurityContextIsNotSet_whenGreet_thenThrowsAuthenticationCredentialsNotFoundException() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> messageService.getSecret());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymous_whenGreet_thenThrowsAccessDeniedException() {
        assertThrows(AccessDeniedException.class, () -> messageService.getSecret());
    }

    @Test
    @WithMockJwtAuth(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, claims = @OpenIdClaims(preferredUsername = "ch4mpy"))
    void givenSecurityContextIsPopulatedWithJwtAuthenticationToken_whenGreet_thenReturnGreetingWithPreferredUsernameAndAuthorities() {
        assertEquals("Hello ch4mpy! You are granted with [admin, ROLE_AUTHORIZED_PERSONNEL].",
              messageService.greet());
    }

    @Test
    @WithMockUser(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, username = "ch4mpy")
    void givenSecurityContextIsPopulatedWithUsernamePasswordAuthenticationToken_whenGreet_thenThrowsClassCastException() {
        assertThrows(ClassCastException.class, () -> messageService.greet());
    }
}
```

响应式应用程序中@Service的单元测试没有太大区别：

```java
@Import({ MessageService.class })
@ExtendWith(SpringExtension.class)
@EnableReactiveMethodSecurity
class MessageServiceUnitTest {
    @Autowired
    MessageService messageService;

    @Test
    void givenSecurityContextIsEmpty_whenGreet_thenThrowsAuthenticationCredentialsNotFoundException() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> messageService.greet()
              .block());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymous_whenGreet_thenThrowsClassCastException() {
        assertThrows(ClassCastException.class, () -> messageService.greet()
              .block());
    }

    @Test
    @WithMockJwtAuth(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, claims = @OpenIdClaims(preferredUsername = "ch4mpy"))
    void givenSecurityContextIsPopulatedWithJwtAuthenticationToken_whenGreet_thenReturnGreetingWithPreferredUsernameAndAuthorities() {
        assertEquals("Hello ch4mpy! You are granted with [admin, ROLE_AUTHORIZED_PERSONNEL].",
              messageService.greet().block());
    }

    @Test
    @WithMockUser(authorities = { "admin", "ROLE_AUTHORIZED_PERSONNEL" }, username = "ch4mpy")
    void givenSecurityContextIsPopulatedWithUsernamePasswordAuthenticationToken_whenGreet_thenThrowsClassCastException() {
        assertThrows(ClassCastException.class, () -> messageService.greet().block());
    }
}
```

## 5. 模拟授权的集成测试

**我们将使用@SpringBootTest编写Spring Boot集成测试**，以便Spring将实际组件连接在一起。为了继续使用模拟身份，我们将它与MockMvc或WebTestClient一起使用。测试本身和使用模拟身份填充测试安全上下文的选项与单元测试相同。仅测试设置更改：

- 不再有组件mock，也没有参数匹配器
- 我们将使用@SpringBootTest(webEnvironment=WebEnvironment.MOCK)而不是@WebMvcTest或@WebFluxTest。MOCK环境最适合使用MockMvc或WebTestClient模拟授权
- 使用@AutoConfigureMockMvc或@AutoConfigureWebTestClient显式修饰测试类以进行MockMvc或WebTestClient注入

这是Spring Boot Servlet集成测试的框架：

```java
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ServletResourceServerApplicationTests {
    @Autowired
    MockMvc api;

    // Test structure and mocked identities options are the same as seen before in unit tests
}
```

这是它在响应式应用程序中的等效项：

```java
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class ReactiveResourceServerApplicationTests {
    @Autowired
    WebTestClient api;

    // Test structure and mocked identities options are the same as seen before in unit tests
}
```

**当然，这种集成测试节省了mocks、argument captors等配置，但它也比单元测试更慢、更脆弱**。我们应该谨慎使用它，可能覆盖率低于@WebMvcTest或@WebFluxTest，只是为了断言自动装配和组件间通信有效。

## 6. 走得更远

到目前为止，我们测试了[使用JWT解码器保护的资源服务器](https://www.baeldung.com/spring-security-oauth-resource-server#jwt-server)，这些服务器在安全上下文中具有JwtAuthenticationToken实例。我们只使用模拟的HTTP请求运行自动化测试，过程中没有涉及任何授权服务器。

### 6.1 使用任何类型的OAuth2 Authentication进行测试

如前所述，**Spring OAuth2安全上下文可以包含其他类型的Authentication，在这种情况下，我们应该在测试中使用其他注解、请求后处理器或突变器**：

-   默认情况下，[具有令牌内省](https://www.baeldung.com/spring-security-oauth-resource-server#opaque-server)的资源服务器在其安全上下文中具有BearerTokenAuthentication实例，并且测试应使用@WithMockBearerTokenAuthentication、opaqueToken()或mockOpaqueToken()
-   [使用oauth2Login()的客户端](https://www.baeldung.com/spring-security-5-oauth2-login)通常在其安全上下文中有一个OAuth2AuthenticationToken，我们将使用@WithOAuth2Login、@WithOidcLogin、oauth2Login()、oidcLogin()、mockOAuth2Login()或mockOidcLogin()
-   假设我们使用http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(...)或其他任何方式显式配置自定义身份验证类型。在那种情况下，我们将不得不提供我们自己的单元测试工具，这在[使用spring-addons实现作为示例](https://github.com/ch4mpy/spring-addons/blob/master/spring-addons-oauth2-test/src/main/java/com/c4_soft/springaddons/security/oauth2/test/annotations/OpenId.java)时并不那么复杂。同一个Github仓库还包含带有[自定义Authentication](https://github.com/ch4mpy/spring-addons/tree/master/samples/tutorials)和专用测试注解的示例

### 6.2 运行示例应用程序

示例项目包含在[https://localhost:8443](https://localhost:8443)运行的Keycloak实例的master realm的属性。使用任何其他OIDC授权服务器只需要在Java配置中调整issuer-uri属性和权限映射器：更改realmRoles2AuthoritiesConverter bean以将权限映射到新授权服务器将角色放入的私有claim中。

有关Keycloak设置的更多详细信息，请参阅[官方入门指南](https://www.keycloak.org/getting-started)。用于[独立zip分发](https://www.keycloak.org/getting-started/getting-started-zip)的可能是最容易开始的。

要使用自签名证书通过TLS设置本地[Keycloak实例](https://www.keycloak.org/server/enabletls)，[这个](https://github.com/ch4mpy/self-signed-certificate-generation)GitHub仓库可能非常有用。

授权服务器应至少具有：

-   两个声明的用户，一个被授予ROLE_AUTHORIZED_PERSONNEL而另一个没有
-   已声明的客户端，为Postman等工具启用了授权代码流，以代表这些用户获取访问令牌

## 7. 总结

在本文中，我们探讨了在Servlet和响应式应用程序中使用模拟身份对Spring OAuth2访问控制规则进行单元和集成测试的两个选项：

-   来自spring-security-test的MockMvc请求后处理器和WebTestClient突变器
-   来自[spring-addons-oauth2-test](https://central.sonatype.com/artifact/com.c4-soft.springaddons/spring-addons-oauth2-test/6.1.0)的OAuth2测试注解

我们还看到我们可以使用MockMvc请求后处理器、WebTestClient突变器或注解来测试@Controllers。但是，只有后者使我们能够在测试其他类型的组件时设置安全上下文。
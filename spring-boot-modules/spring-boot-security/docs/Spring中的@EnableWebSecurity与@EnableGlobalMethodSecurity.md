## 1. 概述

我们可能希望在Spring Boot应用程序的不同路径中应用多个安全过滤器。

在本教程中，我们将介绍两种自定义安全性的方法-通过使用[@EnableWebSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/EnableWebSecurity.html)和[@EnableGlobalMethodSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/method/configuration/GlobalMethodSecurityConfiguration.html)。

为了说明差异，我们将使用一个简单的应用程序，该应用程序具有一些管理资源、即经过身份验证的用户资源。我们还将为其提供一个包含公共资源的部分，因此任何人都可以下载这些资源。

## 2. Spring Boot Security

### 2.1 Maven依赖项

无论我们采用哪种方法，我们首先需要添加spring-security的starter依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2.2 Spring Boot自动配置

通过类路径上的Spring Security，[Spring Boot Security自动配置](SpringBoot-Security自动配置.md)的WebSecurityEnablerConfiguration为我们激活了@EnableWebSecurity，这会将Spring的默认安全配置应用于我们的应用程序。

**默认安全性同时激活HTTP安全过滤器和安全过滤器链，并将基本身份验证应用于我们的端点**。

## 3. 保护我们的端点

对于我们的第一种方法，让我们从创建一个MySecurityConfigurer类开始，确保我们使用@EnableWebSecurity注解对其进行标注。

```java
@EnableWebSecurity
public class MySecurityConfigurer {
}
```

### 3.1 快速浏览SecurityFilterChain bean

首先，让我们看一下如何注册一个SecurityFilterChain bean：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests((requests -> requests.anyRequest().authenticated());
    http.formLogin();
    http.httpBasic();
    return http.build();
}
```

在这里我们看到我们收到的任何请求都需要经过身份验证，并且我们有一个基本的表单登录来提示输入凭据。

当我们想要使用HttpSecurity DSL时，我们将其编写为：

```java
http.authorizeRequests().anyRequest().authenticated()
    .and().formLogin()
    .and().httpBasic()
```

### 3.2 要求用户具有适当的角色

现在让我们配置我们的安全性，以仅允许具有ADMIN角色的用户访问我们的/admin端点，并只允许具有USER角色的用户访问我们的/protected端点。

我们通过创建一个SecurityFilterChain bean来实现这一点：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/admin/**")
        .hasRole("ADMIN")
        .antMatchers("/protected/**")
        .hasRole("USER");
    return http.build();
}
```

### 3.3 放宽公共资源安全

我们不需要对我们的公共/hello资源进行身份验证，因此我们将WebSecurity配置为不对它们执行任何操作。

就像以前一样，让我们注册一个WebSecurityCustomizer bean：

```java
@Bean
public WebSecurityCustomizer ignoreResources() {
    return webSecurity -> webSecurity
        .ignoring()
        .antMatchers("/hello/**");
}
```

## 4. 使用注解保护我们的端点

要使用注解驱动的方法应用安全性，我们可以使用@EnableGlobalMethodSecurity。

### 4.1 使用安全注解要求用户具有适当的角色

现在让我们使用方法注解来配置我们的安全性，以仅允许ADMIN用户访问我们的/admin端点，并且只有我们的USER用户可以访问我们的/protected端点。

让我们通过在EnableGlobalMethodSecurity注解中设置jsr250Enabled=true来启用[JSR-250](https://jcp.org/aboutJava/communityprocess/final/jsr250/index.html)注解：

```java
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@Controller
public class AnnotationSecuredController {
	@RolesAllowed("ADMIN")
	@RequestMapping("/admin")
	public String adminHello() {
		return "Hello Admin";
	}

	@RolesAllowed("USER")
	@RequestMapping("/protected")
	public String jsr250Hello() {
		return "Hello Jsr250";
	}
}
```

### 4.2 强制所有公共方法具有安全性

当我们使用注解作为实现安全性的方式时，我们可能会忘记对方法使用注解进行标注，这会无意中造成安全漏洞。

为了防止这种情况，**我们应该**[拒绝访问所有没有授权注解的方法](https://www.baeldung.com/spring-deny-access)。

### 4.3 允许访问公共资源

Spring的默认安全性强制对我们所有的端点进行身份验证，无论我们是否添加基于角色的安全性。

尽管我们之前的示例将安全性应用于我们的/admin和/protected端点，但我们仍然希望允许访问/hello中基于文件的资源。

虽然我们可以再次扩展WebSecurityAdapter，但Spring为我们提供了一个更简单的替代方案。

使用注解保护我们的方法后，我们现在可以添加WebSecurityCustomizer来公开/hello/*资源：

```java
public class MyPublicPermitter implements WebSecurityCustomizer {
	public void customize(WebSecurity webSecurity) {
		webSecurity.ignoring()
			.antMatchers("/hello/*");
	}
}
```

或者，我们可以简单地创建一个bean在我们的配置类中实现它：

```java
@Configuration
public class MyWebConfig {
	@Bean
	public WebSecurityCustomizer ignoreResources() {
		return webSecurity -> webSecurity
			.ignoring()
			.antMatchers("/hello/*");
	}
}
```

**当Spring Security初始化时，它会调用它找到的任何WebSecurityCustomizer，包括我们的**。

## 5. 测试我们的安全配置

现在我们已经配置了我们的安全性，我们应该检查它的行为是否符合我们的预期。

根据我们为安全选择的方法，我们有一种或两种自动化测试选项。我们可以向我们的应用程序发送Web请求，也可以直接调用我们的控制器方法。

### 5.1 通过Web请求进行测试

对于第一个选项，我们将创建一个带有TestRestTemplate注解的@SpringBootTest测试类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WebSecuritySpringBootIntegrationTest {
	@Autowired
	private TestRestTemplate template;
}
```

现在，让我们添加一个测试来确保我们的公共资源可用：

```java
@Test
public void givenPublicResource_whenGetViaWeb_thenOk() {
    ResponseEntity<String> result = template.getForEntity("/hello/tuyucheng.txt", String.class);
    assertEquals("Hello From Tuyucheng", result.getBody());
}
```

我们还可以看到当我们尝试访问我们受保护的资源之一时会发生什么：

```java
@Test
public void whenGetProtectedViaWeb_thenForbidden() {
    ResponseEntity<String> result = template.getForEntity("/protected", String.class);
    assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
}
```

在这里我们得到一个FORBIDDEN响应，因为我们的匿名请求没有所需的角色。

因此，无论我们选择哪种安全方法，我们都可以使用此方法来测试我们的安全应用程序。

### 5.2 通过自动装配和注解进行测试

现在让我们看看我们的第二个选择，让我们设置一个@SpringBootTest并自动装配我们的AnnotationSecuredController：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GlobalMethodSpringBootIntegrationTest {
	@Autowired
	private AnnotationSecuredController api;
}
```

首先我们使用@WithAnonymousUser测试我们可公开访问的方法：

```java
@Test
@WithAnonymousUser
public void givenAnonymousUser_whenPublic_thenOk() {
    assertThat(api.publicHello()).isEqualTo(HELLO_PUBLIC);
}
```

现在我们已经访问了我们的公共资源，让我们使用@WithMockUser注解来访问我们的受保护方法。

首先，让我们使用具有“USER”角色的用户测试我们的JSR-250保护方法：

```java
@WithMockUser(username="tuyucheng", roles = "USER")
@Test
public void givenUserWithRole_whenJsr250_thenOk() {
    assertThat(api.jsr250Hello()).isEqualTo("Hello Jsr250");
}
```

现在，当我们的用户没有正确的角色时，让我们尝试访问相同的方法：

```java
@WithMockUser(username = "tuyucheng", roles = "NOT-USER")
@Test
public void givenWrongRole_whenJsr250_thenAccessDenied() {
	assertThrows(AccessDeniedException.class, () -> api.jsr250Hello());
}
```

我们的请求被Spring Security拦截，并抛出AccessDeniedException。

当我们选择基于注解的安全时，我们只能使用这种方法。

## 6. 注解注意事项

当我们选择基于注解的方法时，有一些要点需要注意。

**只有当我们通过公共方法进入类时，才会应用带注解的安全性**。

### 6.1 间接方法调用

早些时候，当我们调用带注解的方法时，我们看到我们的安全性已成功应用。但是，现在让我们在同一个类中创建一个没有安全注解的公共方法，我们将让它调用我们带注解的jsr250Hello()方法：

```java
@GetMapping("/indirect")
public String indirectHello() {
    return jsr250Hello();
}
```

现在让我们仅使用匿名访问来调用我们的“/indirect”端点：

```java
@Test
@WithAnonymousUser
public void givenAnonymousUser_whenIndirectCall_thenNoSecurity() {
    assertThat(api.indirectHello()).isEqualTo(HELLO_JSR_250);
}
```

我们的测试通过了，因为我们的“安全”方法在没有触发任何安全性的情况下被调用。换句话说，不会对同一类中的内部调用应用安全性。

### 6.2 对不同类的间接方法调用

现在让我们看看当我们未受保护的方法调用不同类上的注解方法时会发生什么。

首先，让我们创建一个带有注解方法differentJsr250Hello()的DifferentClass类：

```java
@Component
public class DifferentClass {
	@RolesAllowed("USER")
	public String differentJsr250Hello() {
		return "Hello Jsr250";
	}
}
```

现在，让我们将DifferentClass自动注入到我们的控制器中，并添加一个不受保护的differentClassHello()公共方法来调用它。

```java
@Autowired
DifferentClass differentClass;

@GetMapping("/differentclass")
public String differentClassHello() {
    return differentClass.differentJsr250Hello();
}
```

最后，让我们测试调用并查看我们的安全性是否得到执行：

```java
@Test
@WithAnonymousUser
public void givenAnonymousUser_whenIndirectToDifferentClass_thenAccessDenied() {
	assertThrows(AccessDeniedException.class, () -> api.differentClassHello());
}
```

因此，我们看到，虽然当我们在同一个类中调用了内部包含安全配置的方法的方法，它不会为我们启用安全性。但是**当我们从不同类间接调用一个在内部调用包含安全注解的方法时，它们仍然会得到应用**。

### 6.3 最后的注意事项

我们应该确保正确配置我们的@EnableGlobalMethodSecurity，如果我们不这样做，那么尽管我们进行了所有的安全注解配置，但它们可能根本不起作用。

例如，如果我们使用JSR-250注解但我们指定prePostEnabled=true而不是jsr250Enabled=true，那么我们的JSR-250注解将什么都不做！

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
```

当然，我们可以通过将它们都添加到我们的@EnableGlobalMethodSecurity注解来声明我们将使用多种注解类型：

```java
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
```

## 7. 当我们需要更多时

相比JSR-250，我们还可以使用[Spring方法安全](https://www.baeldung.com/spring-security-method-security)，这包括为更高级的授权场景使用更强大的[Spring Security表达式语言(SpEL)](https://www.baeldung.com/spring-security-expressions)，我们可以通过设置prePostEnabled=true在我们的EnableGlobalMethodSecurity注解上启用SpEL：

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
```

此外，当我们想要根据域对象是否由用户拥有来强制实施安全性时，我们可以使用[Spring Security访问控制列表](https://www.baeldung.com/spring-security-acl)。

我们还应该注意，当我们编写响应式应用程序时，我们使用@EnableWebFluxSecurity和@EnableReactiveMethodSecurity代替。

## 8. 总结

在本教程中，我们首先介绍了如何使用带有@EnableWebSecurity的集中式安全规则方法来保护我们的应用程序。

然后，我们通过配置我们的安全性将这些规则置于更接近它们影响的代码的位置，以此为基础。我们通过使用@EnableGlobalMethodSecurity并标注我们想要保护的方法来做到这一点。

最后，我们引入了一种为不需要它的公共资源放松安全性的替代方法。
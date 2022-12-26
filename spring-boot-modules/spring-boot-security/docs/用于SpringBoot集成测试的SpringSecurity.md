## 1. 简介

无需独立集成环境即可执行集成测试的能力对于任何软件堆栈都是一项宝贵的功能，Spring Boot与Spring Security的无缝集成使得测试与安全层交互的组件变得简单。

在本快速教程中，我们将探讨如何使用@MockMvcTest和@SpringBootTest来执行支持安全的集成测试。

## 2. 依赖关系

首先我们引入示例所需的依赖项：

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
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web&core=gav)、[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-security&core=gav)和[spring-boot-starter-test](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-test&core=gav) starter为我们提供了对Spring MVC、Spring Security和Spring Boot测试实用程序的访问。

此外，我们将引入[spring-security-test](https://search.maven.org/search?q=g:org.springframework.security AND a:spring-security-test&core=gav)以便访问我们将使用的@WithMockUser注解。

## 3. Web安全配置

我们的Web安全配置将很简单，只有经过身份验证的用户才能访问匹配/private/**的路径，而与/public/**匹配的路径将对任何用户可用：

```java
@Configuration
public class WebSecurityConfigurer {

	@Bean
	public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("spring")
			.password(passwordEncoder.encode("secret"))
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/private/**")
			.hasRole("USER")
			.antMatchers("/public/**")
			.permitAll()
			.and()
			.httpBasic();
		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
```

## 4. 方法安全配置

除了我们在WebSecurityConfigurer中定义的基于URL路径的安全性之外，我们还可以通过提供额外的配置文件来配置基于方法的安全性：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfigurer extends GlobalMethodSecurityConfiguration {
}
```

此配置启用对Spring Security的pre/post注解的支持，如果需要额外的支持，其他属性也可用。有关Spring方法安全的更多信息，请查看我们关于[该主题的文章](https://www.baeldung.com/spring-security-method-security)。

## 5. 使用@WebMvcTest测试控制器

在Spring Security中使用@WebMvcTest注解时，**MockMvc会自动配置测试我们的安全配置所需的必要过滤器链**。

由于MockMvc是为我们配置的，因此我们可以在测试中使用@WithMockUser，而无需任何其他配置：

```java
@RunWith(SpringRunner.class)
@WebMvcTest(SecuredController.class)
public class SecuredControllerWebMvcIntegrationTest {

	@Autowired
	private MockMvc mvc;

	// ... other methods

	@WithMockUser(value = "spring")
	@Test
	public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/private/hello").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}
```

请注意，使用@WebMvcTest将告诉Spring Boot仅实例化Web层而不是整个上下文。因此，**使用@WebMvcTest的控制器测试将比使用其他方法运行得更快**。

## 6. 使用@SpringBootTest测试控制器

**当使用@SpringBootTest注解通过Spring Security测试控制器时，有必要在设置MockMvc时显式配置过滤器链**。

使用SecurityMockMvcConfigurer提供的静态springSecurity方法是执行此操作的首选方法：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecuredControllerSpringBootIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	// ... other methods

	@WithMockUser("spring")
	@Test
	public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/private/hello").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}
```

## 7. 使用@SpringBootTest测试安全方法

@SpringBootTest不需要任何额外的配置来测试安全方法，我们可以**简单地直接调用方法并根据需要使用@WithMockUser**：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SecuredMethodSpringBootIntegrationTest {

	@Autowired
	private SecuredService service;

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void givenUnauthenticated_whenCallService_thenThrowsException() {
		service.sayHelloSecured();
	}

	@WithMockUser(username="spring")
	@Test
	public void givenAuthenticated_whenCallServiceWithSecured_thenOk() {
		assertThat(service.sayHelloSecured()).isNotBlank();
	}
}
```

## 8. 使用@SpringBootTest和TestRestTemplate进行测试

在为受保护的REST端点编写集成测试时，TestRestTemplate是一个方便的选项。

我们可以**在请求安全端点之前简单地自动装配TestRestTemplate并设置凭据**：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecuredControllerRestTemplateIntegrationTest {

	@Autowired
	private TestRestTemplate template;

	// ... other methods

	@Test
	public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
		ResponseEntity<String> result = template.withBasicAuth("spring", "secret")
			.getForEntity("/private/hello", String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
}
```

TestRestTemplate非常灵活，并提供了许多有用的安全相关选项。有关TestRestTemplate的更多详细信息，请查看我们[关于该主题的文章](https://www.baeldung.com/spring-boot-testresttemplate)。

## 9. 总结

在本文中，我们介绍了几种执行支持安全性的集成测试的方法，并研究了如何使用MVC控制器和REST端点以及安全方法。
## 1. 概述

本教程说明如何使用Spring设置、配置和自定义基本身份验证。我们在简单的Spring MVC案例之上构建，并使用Spring Security提供的Basic Auth机制保护MVC应用程序的UI。

## 2. Spring Security配置

我们可以使用Java类配置Spring Security：

```java
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter {

	@Autowired
	private RestAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("user1")
				.password(passwordEncoder().encode("user1Pass"))
				.authorities("ROLE_USER");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/securityNone")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.httpBasic()
				.authenticationEntryPoint(authenticationEntryPoint);
		http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
```

在这里，我们使用httpBasic()元素在SecurityFilterChain bean中定义基本身份验证。

我们也可以使用XML实现相同的效果：

```xml
<http pattern="/securityNone" security="none"/>
<http use-expressions="true">
    <intercept-url pattern="/" access="isAuthenticated()" />
    <http-basic />
</http>

<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user1" password="{noop}user1Pass" authorities="ROLE_USER" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

这里相关的是配置的主<http\>元素中的<http-basic\>元素，这足以为整个应用程序启用基本身份验证。由于我们在本教程中不关注身份验证管理器，因此我们将使用内存管理器，其中用户和密码以纯文本形式定义。

启用 Spring Security 的 Web 应用程序的web.xml已经在[Spring Logout教程]()中讨论过。

## 3. 使用安全应用程序

curl命令是我们使用安全应用程序的首选工具。首先，让我们尝试在不提供任何安全凭证的情况下请求/homepage.html：

```bash
curl -i http://localhost:8080/spring-security-rest-basic-auth/api/foos/1
```

我们得到了预期的401 Unauthorized和[Authentication Challenge](https://datatracker.ietf.org/doc/html/rfc1945#section-10.16)：

```bash
HTTP/1.1 401 Unauthorized
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=E5A8D3C16B65A0A007CFAACAEEE6916B; Path=/spring-security-mvc-basic-auth/; HttpOnly
WWW-Authenticate: Basic realm="Spring Security Application"
Content-Type: text/html;charset=utf-8
Content-Length: 1061
Date: Wed, 29 May 2013 15:14:08 GMT
```

通常浏览器会解释这个问题并通过一个简单的对话框提示我们输入凭据，但由于我们使用的是curl，所以情况并非如此。

现在让我们请求相同的资源，即主页，但这次提供访问它的凭据：

```bash
curl -i --user user1:user1Pass http://localhost:8080/spring-security-rest-basic-auth/api/foos/1
```

结果，来自服务器的响应是200 OK以及Cookie：

```java
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=301225C7AE7C74B0892887389996785D; Path=/spring-security-mvc-basic-auth/; HttpOnly
Content-Type: text/html;charset=ISO-8859-1
Content-Language: en-US
Content-Length: 90
Date: Wed, 29 May 2013 15:19:38 GMT
```

从浏览器，我们可以正常使用应用程序；唯一的区别是登录页面不再是硬性要求，因为所有浏览器都支持基本身份验证，并使用对话框提示用户输入凭据。

## 4. 进一步配置-入口点

默认情况下，Spring Security提供的BasicAuthenticationEntryPoint会向客户端返回401 Unauthorized响应的完整页面。该错误的这种HTML表示形式在浏览器中呈现良好。相反，它不太适合其他场景，例如可能首选json表示的REST API。

命名空间对于这个新需求也足够灵活。为了解决这个问题，可以覆盖入口点：

```xml
<http-basic entry-point-ref="myBasicAuthenticationEntryPoint" />
```

新的入口点被定义为标准bean：

```java
@Component
public class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		final PrintWriter writer = response.getWriter();
		writer.println("HTTP Status " + HttpServletResponse.SC_UNAUTHORIZED + " - " + authException.getMessage());
	}

	@Override
	public void afterPropertiesSet() {
		setRealmName("Tuyucheng");
		super.afterPropertiesSet();
	}
}
```

通过直接写入HTTP响应，我们现在可以完全控制响应正文的格式。

## 5. Maven依赖

Spring Security的Maven依赖项之前已经在[Spring Security与Maven文章]()中讨论过，我们需要在运行时同时使用spring-security-web和spring-security-config。

## 6. 总结

在本文中，我们使用Spring Security和Basic Authentication保护了一个MVC应用程序。我们讨论了XML配置，并使用简单的curl命令来使用应用程序。最后，我们控制了确切的错误消息格式，从标准的HTML错误页面转换为自定义文本或JSON格式。

当项目在本地运行时，可以在以下位置访问示例HTML：

[http://localhost:8082/spring-security-web-rest-basic-auth/api/foos/1](http://localhost:8082/spring-security-web-rest-basic-auth/homepage.html)。
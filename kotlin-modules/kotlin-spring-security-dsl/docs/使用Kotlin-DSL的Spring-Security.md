## 1. 概述

Spring Security 5.3 引入了 Kotlin 版本的[DSL](https://www.baeldung.com/java-config-spring-security)(领域特定语言)。

在本教程中，我们将讨论新引入的 Kotlin DSL 以及它如何减少样板代码并让我们简洁地配置安全性。

## 2. Spring Security 科特林 DSL

### 2.1. 配置安全

在 Spring 安全应用程序中，我们创建SecurityFilterChain bean来自定义默认安全配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(authz -> authz
            .antMatchers("/greetings/").hasAuthority("ROLE_ADMIN")
            .antMatchers("/").permitAll()
        )
        .httpBasic(basic -> {});
    return http.build();
}

```

在上面，我们通过仅允许将角色配置为 ADMIN 的用户来保护/greetings路径及其任何子路径。所有其他端点都可供所有用户访问。

等效的 Kotlin DSL 配置为：

```java
@Bean
fun filterChain(http: HttpSecurity): SecurityFilterChain {
  http {
    authorizeRequests {
      authorize("/greetings/", hasAuthority("ROLE_ADMIN"))
      authorize("/", permitAll)
    }
    httpBasic {}
  return http.build();
 }
}

```

有时，我们可能希望为不同的端点使用不同的安全配置。这是通过为端点提供多个SecurityFilterChain beans 来实现的：

```java
@Order(1)
@Configuration
class AdminSecurityConfiguration {
  @Bean
  fun filterChainAdmin(http: HttpSecurity): SecurityFilterChain {
    http {
      securityMatcher("/greetings/")
      authorizeRequests {
          authorize("/greetings/", hasAuthority("ROLE_ADMIN"))
      }
      httpBasic {}
    }
    return http.build()
  }
}

@Configuration
class BasicSecurityConfiguration {
  @Bean
  fun filterChainBasic(http: HttpSecurity): SecurityFilterChain {
    http {
      authorizeRequests {
          authorize("/", permitAll)
      }
      httpBasic {}
    }
    return http.build()
  }
}
```

第一个保护 / greetings端点及其子端点，而另一个覆盖所有其他端点。

为此，我们使用值为 1 的@Order 让 Spring 知道AdminSecurityConfiguration需要在BasicSecurityConfiguration之前应用。

### 2.2. 配置用户

为了验证配置，我们需要两个用户，一个普通用户具有 USER 角色，另一个用户具有 ADMIN 角色：

```java
@Bean
public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
  UserDetails user = User.withDefaultPasswordEncoder()
    .username("user").password("password").roles("USER").build();
  UserDetails admin = User.withDefaultPasswordEncoder()
    .username("admin").password("password").roles("USER", "ADMIN").build();
	
  InMemoryUserDetailsManager inMemoryUserDetailsManager 
    = new InMemoryUserDetailsManager();
  inMemoryUserDetailsManager.createUser(user);
  inMemoryUserDetailsManager.createUser(admin);
  return inMemoryUserDetailsManager;
}
```

我们可以使用 Kotlin beans DSL 编写相同的配置：

```java
beans {
  bean {
    fun user(user: String, password: String, vararg  roles: String) =
      User.withDefaultPasswordEncoder().username(user).password(password)
        .roles(roles).build()
    InMemoryUserDetailsManager(user("user", "password", "USER"), 
	  user("admin", "password", "USER", "ADMIN"))
  }
}
```

### 2.3. 配置端点

让我们创建另一个 bean 来定义 REST 端点：

```java
bean {
  router {
    GET("/greetings") {
      request -> request.principal().map { it.name }
        .map { ServerResponse.ok().body(mapOf("greeting" to "Hello $it")) }
        .orElseGet { ServerResponse.badRequest().build() }
    }
  }
}
```

在上面的 bean 定义中，我们使用 HTTP GET 方法定义/greetings路由器。此外，我们从请求中检索委托人并从中获取用户名。然后我们通过问候登录用户来返回响应。

## 3. 使用受保护的应用程序

[在与我们的应用程序交互时， cURL](https://www.baeldung.com/curl-rest) 命令是我们的首选工具。 

首先，让我们尝试向 普通用户请求/greetings端点：

```shell
$ curl -v -u user:password http://localhost:8080/greetings
```

我们得到了预期的 403 Forbidden：

```shell
HTTP/1.1 403
Set-Cookie: JSESSIONID=F0CBE263219CDCEDD28DE2F0C8DE3A75; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 11 Jun 2020 09:43:44 GMT
```

浏览器会解释这个挑战，并通过一个简单的对话框提示我们输入凭据。

现在，让我们请求相同的资源—— /greetings端点——但这次使用管理员用户来访问它：

```shell
$ curl -v -u admin:password localhost:8080/greetings
```

现在，来自服务器的响应是 200 OK 以及一个 Cookie：

```shell
HTTP/1.1 200
Set-Cookie: JSESSIONID=D1E537C2424467AF426E494610BF950F; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 11 Jun 2020 09:49:38 GMT
```

从浏览器中，可以正常使用应用程序。事实上，关键区别在于登录页面不再是硬性要求，因为所有浏览器都支持[HTTP 基本身份验证](https://www.baeldung.com/spring-security-basic-authentication)并使用对话框提示用户输入凭据。

## 4。总结

在本教程中，我们了解了如何使用 Spring Security Kotlin DSL 来声明 Spring 应用程序的安全方面。此外，我们随后使用cURL 测试了我们的应用程序。
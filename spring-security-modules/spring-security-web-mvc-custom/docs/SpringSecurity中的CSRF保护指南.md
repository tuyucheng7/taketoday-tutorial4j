## 1. 概述

在本教程中，我们将讨论跨站点请求伪造(CSRF)攻击以及如何使用 Spring Security 来防止它们。

## 进一步阅读：

## [使用 Spring MVC 和 Thymeleaf 进行 CSRF 保护](https://www.baeldung.com/csrf-thymeleaf-with-spring-security)

使用 Spring Security、Spring MVC 和 Thymeleaf 防止 CSRF 攻击的快速实用指南。

[阅读更多](https://www.baeldung.com/csrf-thymeleaf-with-spring-security)→

## [Spring Boot 安全自动配置](https://www.baeldung.com/spring-boot-security-autoconfiguration)

Spring Boot 默认 Spring Security 配置的快速实用指南。

[阅读更多](https://www.baeldung.com/spring-boot-security-autoconfiguration)→

## [Spring方法安全介绍](https://www.baeldung.com/spring-security-method-security)

使用 Spring Security 框架的方法级安全性指南。

[阅读更多](https://www.baeldung.com/spring-security-method-security)→

## 2. 两个简单的 CSRF 攻击

CSRF 攻击有多种形式。让我们讨论一些最常见的。

### 2.1。获取示例

让我们考虑登录用户使用以下GET请求将钱转帐到特定银行帐户1234：

```bash
GET http://bank.com/transfer?accountNo=1234&amount=100
```

如果攻击者想将钱从受害者的账户转移到他自己的账户——5678—— 他需要让受害者触发请求：

```bash
GET http://bank.com/transfer?accountNo=5678&amount=1000
```

有多种方法可以做到这一点：

-   链接——攻击者可以说服受害者点击这个链接，例如，执行传输：

```html
<a href="http://bank.com/transfer?accountNo=5678&amount=1000">
Show Kittens Pictures
</a>
```

-   图片– 攻击者可能使用带有目标 URL的<img/>标签作为图片源。换句话说，甚至不需要点击。该请求将在页面加载时自动执行：

```html
<img src="http://bank.com/transfer?accountNo=5678&amount=1000"/>
```

### 2.2. 发布示例

假设主请求需要是一个 POST 请求：

```bash
POST http://bank.com/transfer
accountNo=1234&amount=100
```

在这种情况下，攻击者需要让受害者运行类似的请求：

```bash
POST http://bank.com/transfer
accountNo=5678&amount=1000
```

在这种情况下，<a>和<img/>标签都不起作用。

攻击者需要一个<form>：

```html
<form action="http://bank.com/transfer" method="POST">
    <input type="hidden" name="accountNo" value="5678"/>
    <input type="hidden" name="amount" value="1000"/>
    <input type="submit" value="Show Kittens Pictures"/>
</form>
```

但是，可以使用 JavaScript 自动提交表单：

```html
<body onload="document.forms[0].submit()">
<form>
...
```

### 2.3. 实际模拟

现在我们了解了 CSRF 攻击的样子，让我们在 Spring 应用程序中模拟这些示例。

我们将从一个简单的控制器实现开始—— BankController：

```java
@Controller
public class BankController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    @ResponseBody
    public String transfer(@RequestParam("accountNo") int accountNo, 
      @RequestParam("amount") final int amount) {
        logger.info("Transfer to {}", accountNo);
        ...
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void transfer2(@RequestParam("accountNo") int accountNo, 
      @RequestParam("amount") final int amount) {
        logger.info("Transfer to {}", accountNo);
        ...
    }
}
```

让我们也有一个触发银行转账操作的基本 HTML 页面：

```html
<html>
<body>
    <h1>CSRF test on Origin</h1>
    <a href="transfer?accountNo=1234&amount=100">Transfer Money to John</a>
	
    <form action="transfer" method="POST">
        <label>Account Number</label> 
        <input name="accountNo" type="number"/>

        <label>Amount</label>         
        <input name="amount" type="number"/>

        <input type="submit">
    </form>
</body>
</html>
```

这是主应用程序的页面，在源域上运行。

我们应该注意到，我们已经通过一个简单的链接实现了一个GET并通过一个简单的<form>实现了一个POST。

现在让我们看看攻击者页面会是什么样子：

```html
<html>
<body>
    <a href="http://localhost:8080/transfer?accountNo=5678&amount=1000">Show Kittens Pictures</a>
    
    <img src="http://localhost:8080/transfer?accountNo=5678&amount=1000"/>
	
    <form action="http://localhost:8080/transfer" method="POST">
        <input name="accountNo" type="hidden" value="5678"/>
        <input name="amount" type="hidden" value="1000"/>
        <input type="submit" value="Show Kittens Picture">
    </form>
</body>
</html>
```

该页面将在不同的域上运行——攻击者域。

最后，让我们在本地运行原始应用程序和攻击者应用程序。

为了使攻击起作用，用户需要通过会话 cookie 对原始应用程序进行身份验证。

让我们首先访问原始应用程序页面：

```bash
http://localhost:8081/spring-rest-full/csrfHome.html
```

它将在我们的浏览器上设置JSESSIONID cookie。

然后让我们访问攻击者页面：

```bash
http://localhost:8081/spring-security-rest/api/csrfAttacker.html
```

如果我们跟踪来自这个攻击者页面的请求，我们将能够发现那些攻击原始应用程序的请求。由于JSESSIONID cookie 与这些请求一起自动提交，因此 Spring 对它们进行身份验证，就好像它们来自原始域一样。

## 3. Spring MVC 应用

为了保护 MVC 应用程序，Spring 为每个生成的视图添加了一个 CSRF 令牌。这个令牌必须在每个修改状态的 HTTP 请求(PATCH、POST、PUT 和 DELETE—— 不是 GET)上提交给服务器。这可以保护我们的应用程序免受 CSRF 攻击，因为攻击者无法从他们自己的页面获取此令牌。

接下来，我们将看到如何配置我们的应用程序安全性以及如何使我们的客户端符合它。

### 3.1。Spring 安全配置

在旧的 XML 配置中(Spring Security 4 之前)，CSRF 保护默认是禁用的，我们可以根据需要启用它：

```java
<http>
    ...
    <csrf />
</http>
```

从 Spring Security 4.x 开始，默认启用 CSRF 保护。

此默认配置将 CSRF 令牌添加到名为_csrf的HttpServletRequest属性中。

如果需要，我们可以禁用此配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf().disable();
    return http.build();
}
```

### 3.2. 客户端配置

现在我们需要在请求中包含 CSRF 令牌。

_csrf属性包含以下信息：

-   令牌 – CSRF 令牌值
-   parameterName – HTML 表单参数的名称，必须包含令牌值
-   headerName – HTTP 标头的名称，必须包含令牌值

如果我们的视图使用 HTML 表单，我们将使用parameterName和token值来添加隐藏输入：

```html
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```

如果我们的视图使用 JSON，我们需要使用headerName和token值来添加 HTTP 标头。

我们首先需要在元标记中包含令牌值和标头名称：

```html
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
```

然后让我们用 JQuery 检索元标记值：

```javascript
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

```

最后，让我们使用这些值来设置我们的 XHR 标头：

```javascript
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```

## 4. 无状态 Spring API

让我们回顾一下前端使用的无状态 Spring API 的案例。

正如[我们在专门文章](https://www.baeldung.com/csrf-stateless-rest-api)中所解释的，我们需要了解我们的无状态 API 是否需要 CSRF 保护。

如果我们的无状态 API 使用基于令牌的身份验证，例如 JWT，我们不需要 CSRF 保护，我们必须像之前看到的那样禁用它。

但是，如果我们的无状态 API 使用会话 cookie 身份验证，我们需要启用 CSRF 保护 ，如下所示。


### 4.1。后端配置

我们的无状态 API 无法像 MVC 配置那样添加 CSRF 令牌，因为它不会生成任何 HTML 视图。

在这种情况下，我们可以使用CookieCsrfTokenRepository在 cookie 中发送 CSRF 令牌：

```java
@Configuration
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
}
```

此配置会将XSRF-TOKEN cookie 设置到前端。因为我们将HTTP-only标志设置为false，所以前端将能够使用 JavaScript 检索此 cookie。

### 4.2. 前端配置

使用 JavaScript，我们需要从document.cookie列表中搜索XSRF-TOKEN cookie 值。

由于此列表存储为字符串，我们可以使用此正则表达式检索它：

```javascript
const csrfToken = document.cookie.replace(/(?:(?:^|.;s)XSRF-TOKENs=s([^;]).$)|^.$/, '$1');
```

然后我们必须将令牌发送到修改 API 状态的每个 REST 请求：POST、PUT、DELETE 和 PATCH。

Spring 期望在X-XSRF-TOKEN标头中接收它。

我们可以使用 JavaScript Fetch API 简单地设置它：

```javascript
fetch(url, {
  method: 'POST',
  body: / data to send /,
  headers: { 'X-XSRF-TOKEN': csrfToken },
})
```

## 5. CSRF 禁用测试

有了所有这些，让我们进行一些测试。

我们先尝试在禁用 CSRF 时提交一个简单的 POST 请求：

```java
@ContextConfiguration(classes = { SecurityWithoutCsrfConfig.class, ...})
public class CsrfDisabledIntegrationTest extends CsrfAbstractIntegrationTest {

    @Test
    public void givenNotAuth_whenAddFoo_thenUnauthorized() throws Exception {
        mvc.perform(
          post("/foos").contentType(MediaType.APPLICATION_JSON)
            .content(createFoo())
          ).andExpect(status().isUnauthorized());
    }

    @Test 
    public void givenAuth_whenAddFoo_thenCreated() throws Exception {
        mvc.perform(
          post("/foos").contentType(MediaType.APPLICATION_JSON)
            .content(createFoo())
            .with(testUser())
        ).andExpect(status().isCreated()); 
    } 
}
```

在这里，我们使用一个基类来保存常见的测试辅助逻辑—— CsrfAbstractIntegrationTest：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CsrfAbstractIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    protected MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
          .addFilters(springSecurityFilterChain)
          .build();
    }

    protected RequestPostProcessor testUser() {
        return user("user").password("userPass").roles("USER");
    }

    protected String createFoo() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new Foo(randomAlphabetic(6)));
    }
}
```

我们应该注意到，当用户拥有正确的安全凭证时，请求已成功执行——不需要额外的信息。

这意味着攻击者可以简单地使用前面讨论的任何攻击向量来破坏系统。

## 6. CSRF 启用测试

现在让我们启用 CSRF 保护，看看有什么区别：

```java
@ContextConfiguration(classes = { SecurityWithCsrfConfig.class, ...})
public class CsrfEnabledIntegrationTest extends CsrfAbstractIntegrationTest {

    @Test
    public void givenNoCsrf_whenAddFoo_thenForbidden() throws Exception {
        mvc.perform(
          post("/foos").contentType(MediaType.APPLICATION_JSON)
            .content(createFoo())
            .with(testUser())
          ).andExpect(status().isForbidden());
    }

    @Test
    public void givenCsrf_whenAddFoo_thenCreated() throws Exception {
        mvc.perform(
          post("/foos").contentType(MediaType.APPLICATION_JSON)
            .content(createFoo())
            .with(testUser()).with(csrf())
          ).andExpect(status().isCreated());
    }
}
```

我们可以看到该测试如何使用不同的安全配置——启用了 CSRF 保护的安全配置。

现在，如果不包含 CSRF 令牌，POST 请求将简单地失败，这当然意味着更早的攻击不再是一种选择。

此外，测试中的[csrf()](https://docs.spring.io/spring-security/site/docs/4.0.2.RELEASE/apidocs/org/springframework/security/test/web/servlet/request/SecurityMockMvcRequestPostProcessors.html#csrf())方法会创建一个RequestPostProcessor，它会自动在请求中填充有效的 CSRF 令牌以进行测试。

## 7. 总结

在本文中，我们讨论了几个 CSRF 攻击以及如何使用 Spring Security 来防止它们。
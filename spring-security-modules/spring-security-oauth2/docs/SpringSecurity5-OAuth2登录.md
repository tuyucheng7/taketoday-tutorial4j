## 1. 概述

Spring Security 5引入了一个新的OAuth2LoginConfigurer类，我们可以使用它来配置外部授权服务器。

在本教程中，我们介绍一些可用于oauth2Login()元素的各种配置选项。

## 2. Maven依赖

在Spring Boot项目中，我们只需要添加spring-boot-starter-oauth2-client：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-oauth2-client</artifactId>
    <version>2.6.1</version>
</dependency>
```

在非Spring Boot项目中，除了标准的Spring和Spring Security依赖项外，
我们还需要显式添加spring-security-oauth2-client和spring-security-oauth2-jose依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-client</artifactId>
    <version>5.6.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
    <version>5.6.0</version>
</dependency>
```

## 3. 客户端设置

在Spring Boot项目中，我们需要做的就是为要配置的每个客户端添加一些标准属性。

我们将项目设置为使用在Google和Facebook注册为身份验证提供者的客户登录。

### 3.1 获取客户端凭据

要获取用于Google OAuth2身份验证的客户端凭据，请转到[Google API控制台]
(https://console.cloud.google.com/apis/dashboard)的“凭据”部分。

在这里，我们将为Web应用程序创建类型为“OAuth2 Client ID”的凭据，Google会为我们设置客户端ID和密码。

我们还必须在Google Console中配置一个授权的重定向URI，这是用户成功登录Google后将被重定向到的路径。

默认情况下，Spring Boot将此重定向URI配置为/login/oauth2/code/{registrationId}。

因此，对于Google，我们将添加以下URI：

```text
http://localhost:8081/login/oauth2/code/google
```

要获取用于Facebook身份验证的客户端凭据，我们需要在Facebook for Developers网站上注册一个应用程序，
并将相应的URI设置为“Valid OAuth redirect URI”：

```text
http://localhost:8081/login/oauth2/code/facebook
```

### 3.2 Security配置

接下来，我们需要将客户端凭据添加到application.properties文件中。

Spring Security属性以spring.security.oauth2.client.registration为前缀，后跟客户端名称，然后是客户端属性的名称：

```properties
spring.security.oauth2.client.registration.google.client-id=<your client id>
spring.security.oauth2.client.registration.google.client-secret=<your client secret>

spring.security.oauth2.client.registration.facebook.client-id=<your client id>
spring.security.oauth2.client.registration.facebook.client-secret=<your client secret>
```

为至少一个客户端添加这些属性将启用Oauth2ClientAutoConfiguration类，该类会配置所有必要的bean。

自动Web安全配置相当于定义一个简单的oauth2Login()元素：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login();
    }
}
```

在这里，我们可以看到oauth2Login()的使用方式与我们熟悉的httpBasic()和formLogin()方法类似。

现在，当我们尝试访问受保护的URL时，应用程序将显示一个自动生成的登录页面，其中包含两个客户端：

<img src="../assets/img.png">

### 3.3 其他客户端

请注意，除了Google和Facebook之外，Spring Security项目还包含GitHub和Okta的默认配置。
这些默认配置为身份验证提供了所有必要的信息，这使我们只能输入客户端凭据。

如果我们想使用没有在Spring Security中配置的不同身份验证提供程序，我们需要定义完整配置，包括授权URI和令牌URI等信息。
下面看一下Spring Security中的默认配置，以了解所需的属性。

## 4. 非Spring Boot项目的配置

### 4.1 创建ClientRegistrationRepository Bean

如果我们不使用Spring Boot应用程序，我们需要定义一个ClientRegistrationRepository bean，其中包含授权服务器拥有的客户端信息的内部表示：

```java
@Configuration
@PropertySource("classpath:application-oauth2.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
```

这里，我们使用ClientRegistration对象列表创建InMemoryClientRegistrationRepository。

### 4.2 构建ClientRegistration对象

让我们看看构建这些对象的getRegistration()方法：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    @Autowired
    private Environment env;

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
        return null;
    }
}
```

这里我们从一个类似的application.properties文件中读取客户端凭据。
然后，我们使用Spring Security中已经定义的CommonOauth2Provider枚举，用于Google和Facebook客户端的其余客户端属性。

每个ClientRegistration实例对应一个客户端。

### 4.3 注册ClientRegistrationRepository

最后，我们必须基于ClientRegistrationRepository bean创建一个 OAuth2AuthorizedClientService bean，
并使用oauth2Login()注册这两个bean：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2Login()
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
}
```

可以看到，我们可以使用oauth2Login()的clientRegistrationRepository()方法来注册一个自定义的RegistrationRepository。

我们还必须定义一个自定义登录页面，因为它不会再自动生成。

## 5. 自定义oauth2Login()

OAuth 2流程使用了几个元素，我们可以使用oauth2Login()方法对其进行自定义。

注意，所有这些元素在Spring Boot中都有默认配置，不需要显式配置。

### 5.1 自定义登录页面

尽管Spring Boot为我们生成了默认登录页面，但我们通常希望定义自己的自定义页面。

让我们开始使用loginPage()方法为oauth2Login()元素配置一个新的登录URL：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/oauth_login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/oauth_login");
    }
}
```

这里，我们将登录URL设置为/oauth_login。

接下来，我们需要将此URL映射到一个控制器方法。
此方法必须将可用客户端及其授权端点的Map传递给Model，我们将从ClientRegistrationRepository bean中获取：

```java
@Controller
public class LoginController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";

    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "oauth_login";
    }
}
```

最后，我们需要定义oauth_login.html页面：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Oauth2 Login</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css"/>
</head>

<body>
<div class="container">
    <div class="col-sm-3 well">
        <h3>Login with:</h3>
        <div class="list-group">
            <p th:each="url : ${urls}">
                <a th:text="${url.key}" th:href="${url.value}" class="list-group-item active">Client</a>
            </p>
        </div>
    </div>
</div>
</body>
</html>
```

这是一个简单的HTML页面，其中包含了与每个客户端进行身份验证的链接。

添加一些样式后，我们可以更改登录页面的外观：

<img src="../assets/img_1.png">

### 5.2 自定义身份验证成功和失败行为

我们可以使用不同的方法控制认证后的行为：

+ defaultSuccessUrl()和failureUrl()将用户重定向到给定的URL。
+ successHandler()和failureHandler()在身份验证过程之后运行自定义逻辑。

让我们看看如何设置自定义URL以将用户重定向到：

```text
.oauth2Login()
  .defaultSuccessUrl("/loginSuccess")
  .failureUrl("/loginFailure");
```

如果用户在身份验证之前访问了安全页面，则登录后会重定向到该页面。否则，他们将被重定向到/loginSuccess。

如果我们希望用户始终被重定向到/loginSuccess URL，无论他们之前是否位于安全页面，
我们可以使用方法defaultSuccessUrl(“/loginSuccess”, true)。

要使用自定义处理程序，我们必须创建一个实现AuthenticationSuccessHandler或AuthenticationFailureHandler接口的类，
重写继承的方法，然后使用successHandler()和failureHandler()方法设置bean。

### 5.3 自定义授权端点

授权端点是Spring Security用来向外部服务器触发授权请求的端点。

首先，让我们为授权端点设置新的属性：

```text
.oauth2Login() 
  .authorizationEndpoint()
  .baseUri("/oauth2/authorize-client")
  .authorizationRequestRepository(authorizationRequestRepository());
```

这里，我们将baseUri修改为/oauth2/authorize-client，而不是默认的/oauth2/authorization。

我们还显式设置了一个必须定义的authorizationRequestRepository() bean：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
```

我们已经为我们的bean使用了Spring提供的实现，但我们也可以提供一个自定义的实现。

### 5.4 自定义Token端点

token端点处理访问token。

让我们使用默认响应客户端实现显式配置tokenEndpoint()：

```text
.oauth2Login()
  .tokenEndpoint()
  .accessTokenResponseClient(accessTokenResponseClient());
```

下面是响应客户端bean：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }
}
```

此配置与默认配置相同，它使用Spring实现，该实现基于与提供者交换授权码。

当然，我们也可以替换自定义响应客户端。

### 5.5 自定义重定向端点

这是在与外部提供者进行身份验证后要重定向到的端点。

让我们看看如何更改重定向端点的baseUri：

```text
.oauth2Login()
  .redirectionEndpoint()
  .baseUri("/oauth2/redirect")
```

默认URI是login/oauth2/code。

请注意，如果我们更改它，我们还必须更新每个ClientRegistration的redirectUriTemplate属性，并将新的URI添加为每个客户端的授权重定向URI。

### 5.6 自定义用户信息端点

用户信息端点是我们可以用来获取用户信息的位置。

我们可以使用userInfoEndpoint()方法自定义这个端点。
为此，我们可以使用userService()和customUserType()等方法来修改检索用户信息的方式。

## 6. 访问用户信息

我们可能想要实现的一个常见任务是获取有关登录用户的信息。为此，我们可以向用户信息端点发出请求。

首先，我们必须获取与当前用户令牌对应的客户端：

```java
public class LoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        //...
        return "loginSuccess";
    }
}
```

接下来，我们向客户端的用户信息端点发送请求并检索userAttributes Map：

```java
String userInfoEndpointUri = client.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUri();
if (StringUtils.hasLength(userInfoEndpointUri)) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
    Map userAttributes = response.getBody();
    model.addAttribute("name", userAttributes.get("name"));
}
```

通过将name属性添加为Model属性，我们可以在loginSuccess视图中将其作为欢迎消息显示给用户：

除了名称之外，userAttributes Map还包含诸如email、family_name、picture和locale等属性。

## 7. 总结

在本文中，我们介绍了如何使用Spring Security中的oauth2Login()方法向不同的提供商(如Google和Facebook)进行身份验证。

我们还演示了一些自定义此过程的常见场景。
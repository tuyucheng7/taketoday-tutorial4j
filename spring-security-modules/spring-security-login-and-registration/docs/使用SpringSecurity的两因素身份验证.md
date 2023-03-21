## 1. 概述

在本教程中，我们将使用软令牌和Spring Security实现[双因素身份验证功能](https://en.wikipedia.org/wiki/Multi-factor_authentication)。

我们将把新功能添加到[现有的简单登录流程](https://github.com/Baeldung/spring-security-registration)中，并使用[Google Authenticator应用程序](https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en)生成令牌。

简单地说，双因素身份验证是一个验证过程，它遵循众所周知的“用户知道什么，用户就拥有什么”的原则。

因此，用户在身份验证时提供了一个额外的“验证令牌”-基于时间一次性密码[TOTP](https://tools.ietf.org/html/rfc6238)算法的一次性密码验证码。

## 2. Maven配置

首先，为了在我们的应用程序中使用Google Authenticator，我们需要：

-   生成秘钥
-   通过二维码向用户提供密钥
-   使用此密钥验证用户输入的令牌

我们将使用一个简单的[服务器端库](https://github.com/aerogear/aerogear-otp-java)通过将以下依赖项添加到我们的pom.xml来生成/验证一次性密码：

```xml
<dependency>
    <groupId>org.jboss.aerogear</groupId>
    <artifactId>aerogear-otp-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 3. 用户实体

接下来，我们将修改我们的用户实体以保存额外的信息-如下所示：

```java
@Entity
public class User {
    ...
    private boolean isUsing2FA;
    private String secret;

    public User() {
        super();
        this.secret = Base32.random();
        // ...
    }
}
```

注意：

-   我们为每个用户保存一个随机密码，以供以后生成验证码时使用
-   我们的两步验证是可选的

## 4. 额外的登录参数

首先，我们需要调整我们的安全配置以接收额外的参数-验证令牌。我们可以通过使用自定义AuthenticationDetailsSource来实现：

这是我们的CustomWebAuthenticationDetailsSource：

```java
@Component
public class CustomWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new CustomWebAuthenticationDetails(context);
    }
}
```

这是CustomWebAuthenticationDetails：

```java
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private String verificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("code");
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
```

以及我们的安全配置：

```java
@Configuration
@EnableWebSecurity
public class LssSecurityConfig {

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
              .authenticationDetailsSource(authenticationDetailsSource)
        // ...
    }
}
```

最后将额外参数添加到我们的登录表单：

```html
<labelth:text="#{label.form.login2fa}">
Google Authenticator Verification Code
</label>
<input type='text' name='code'/>
```

> 注意：我们需要在安全配置中设置我们的自定义AuthenticationDetailsSource。

## 5. 自定义身份验证提供程序

接下来，我们需要一个自定义的AuthenticationProvider来处理额外的参数验证：

```java
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails())
              .getVerificationCode();
        User user = userRepository.findByEmail(auth.getName());
        if ((user == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (user.isUsing2FA()) {
            Totp totp = new Totp(user.getSecret());
            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                throw new BadCredentialsException("Invalid verfication code");
            }
        }

        Authentication result = super.authenticate(auth);
        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

请注意，在我们验证了一次性密码验证码之后，我们只是将身份验证委托给下游。

这是我们的身份验证提供程序bean

```java
@Bean
public DaoAuthenticationProvider authProvider() {
    CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
}
```

## 6. 注册流程

现在，为了让用户能够使用该应用程序生成令牌，他们需要在注册时正确设置。

因此，我们需要对注册过程做一些简单的修改-允许选择使用两步验证的用户**扫描他们稍后需要登录的二维码**。

首先，我们将这个简单的输入添加到我们的注册表单中：

```html
Use Two step verification <input type="checkbox" name="using2FA" value="true"/>
```

然后，在我们的RegistrationController中-我们在确认注册后根据用户的选择重定向用户：

```java
@GetMapping("/registrationConfirm")
public String confirmRegistration(@RequestParam("token") String token, ...) {
    String result = userService.validateVerificationToken(token);
    if(result.equals("valid")) {
        User user = userService.getUser(token);
        if (user.isUsing2FA()) {
            model.addAttribute("qr", userService.generateQRUrl(user));
            return "redirect:/qrcode.html?lang=" + locale.getLanguage();
        }
        
        model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login?lang=" + locale.getLanguage();
    }
    // ...
}
```

这是我们的方法generateQRUrl()：

```java
public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

@Override
public String generateQRUrl(User user) {
    return QR_PREFIX + URLEncoder.encode(String.format(
        "otpauth://totp/%s:%s?secret=%s&issuer=%s", 
        APP_NAME, user.getEmail(), user.getSecret(), APP_NAME),
        "UTF-8");
}
```

这是我们的qrcode.html：

```html
<html>
<body>
<div id="qr">
    <p>
        Scan this Barcode using Google Authenticator app on your phone
        to use it later in login
    </p>
    <img th:src="${param.qr[0]}"/>
</div>
<a href="/login" class="btn btn-primary">Go to login page</a>
</body>
</html>
```

注意：

-   generateQRUrl()方法用于生成二维码URL
-   此二维码将由用户手机使用Google Authenticator应用程序扫描
-   该应用程序将生成一个6位代码，有效期仅为30秒，这是所需的验证码
-   此验证码将在使用我们的自定义AuthenticationProvider登录时进行验证

## 7. 启用两步验证

接下来，我们将确保用户可以随时更改他们的登录首选项-如下所示：

```java
@PostMapping("/user/update/2fa")
public GenericResponse modifyUser2FA(@RequestParam("use2FA") boolean use2FA) throws UnsupportedEncodingException {
    User user = userService.updateUser2FA(use2FA);
    if (use2FA) {
        return new GenericResponse(userService.generateQRUrl(user));
    }
    return null;
}
```

这是updateUser2FA()：

```java
@Override
public User updateUser2FA(boolean use2FA) {
    Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = (User) curAuth.getPrincipal();
    currentUser.setUsing2FA(use2FA);
    currentUser = repository.save(currentUser);
    
    Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), curAuth.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
    return currentUser;
}
```

这是前端：

```html
<div th:if="${#authentication.principal.using2FA}">
    You are using Two-step authentication
    <a href="#" onclick="disable2FA()">Disable 2FA</a>
</div>
<div th:if="${! #authentication.principal.using2FA}">
    You are not using Two-step authentication
    <a href="#" onclick="enable2FA()">Enable 2FA</a>
</div>
<br/>
<div id="qr" style="display:none;">
    <p>Scan this Barcode using Google Authenticator app on your phone </p>
</div>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
function enable2FA(){
    set2FA(true);
}
function disable2FA(){
    set2FA(false);
}
function set2FA(use2FA){
    $.post( "/user/update/2fa", { use2FA: use2FA } , function( data ) {
        if(use2FA){
        	$("#qr").append('<img src="'+data.message+'" />').show();
        }else{
            window.location.reload();
        }
    });
}
</script>
```

## 8. 总结

在这个快速教程中，我们说明了如何使用带有Spring Security的软令牌来执行双因素身份验证实现。
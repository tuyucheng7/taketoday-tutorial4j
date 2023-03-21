## 1. 概述

在本教程中，我们将通过将Google [reCAPTCHA](https://www.baeldung.com/cs/captcha-intro)添加到注册过程来继续[Spring Security注册](https://www.baeldung.com/spring-security-registration)系列，以区分人类和机器人。

## 2. 集成谷歌的reCAPTCHA

要集成Google的reCAPTCHA网络服务，我们首先需要向该服务注册我们的网站，将他们的库添加到我们的页面，然后使用该网络服务验证用户的验证码响应。

让我们在[https://www.google.com/recaptcha/admin](https://www.google.com/recaptcha/admin)注册我们的网站。注册过程会生成用于访问Web服务的**site-key和secret-key**。

### 2.1 存储API密钥对

我们将密钥存储在application.properties中：

```properties
google.recaptcha.key.site=6LfaHiITAAAA...
google.recaptcha.key.secret=6LfaHiITAAAA...
```

并使用带有@ConfigurationProperties注解的bean将它们暴露给Spring：

```java
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {

    private String site;
    private String secret;

    // standard getters and setters
}
```

### 2.2 显示小部件

在该系列教程的基础上，我们现在将修改registration.html以包含Google的库。

在我们的注册表单中，我们添加了reCAPTCHA小部件，它期望属性data-sitekey包含site-key。

**小部件将在提交时附加请求参数g-recaptcha-response**：

```html
<!DOCTYPE html>
<html>
<head>

    ...

    <script src='https://www.google.com/recaptcha/api.js'></script>
</head>
<body>

...

<form method="POST" enctype="utf8">
    ...

    <div class="g-recaptcha col-sm-5"
         th:attr="data-sitekey=${@captchaSettings.getSite()}"></div>
    <span id="captchaError" class="alert alert-danger col-sm-4"
          style="display:none"></span>
```

## 3. 服务器端验证

新的请求参数对我们的站点密钥和标识用户成功完成挑战的唯一字符串进行编码。

然而，由于我们自己无法辨别，因此我们无法相信用户提交的内容是合法的。发出服务器端请求以使用Web服务API验证验证码响应。

端点接受URL [https://www.google.com/recaptcha/api/siteverify](https://www.google.com/recaptcha/api/siteverify)上的HTTP请求，其中包含查询参数**secret、response和remoteip**。它返回具有以下模式的JSON响应：

```json
{
    "success": true|false,
    "challenge_ts": timestamp,
    "hostname": string,
    "error-codes": [ ... ]
}
```

### 3.1 检索用户的响应

使用HttpServletRequest从请求参数g-recaptcha-response中检索用户对reCAPTCHA质询的响应，并使用我们的CaptchaService进行验证。处理响应时抛出的任何异常都将中止其余的注册逻辑：

```java
public class RegistrationController {

    @Autowired
    private ICaptchaService captchaService;

    // ...

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid UserDto accountDto, HttpServletRequest request) {
        String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);

        // Rest of implementation
    }

    // ...
}
```

### 3.2 验证服务

应首先对获得的验证码响应进行清理。使用了一个简单的正则表达式。

如果响应看起来是合法的，我们就会使用密钥、验证码响应和客户端的IP地址向Web服务发出请求：

```java
public class CaptchaService implements ICaptchaService {

    @Autowired
    private CaptchaSettings captchaSettings;

    @Autowired
    private RestOperations restTemplate;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Override
    public void processResponse(String response) {
        if(!responseSanityCheck(response)) {
            throw new InvalidReCaptchaException("Response contains invalid characters");
        }

        URI verifyUri = URI.create(String.format(
              "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
              getReCaptchaSecret(), response, getClientIP()));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(!googleResponse.isSuccess()) {
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
```

### 3.3 客观化验证

用Jackson注解修饰的Java bean封装了验证响应：

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
      "success",
      "challenge_ts",
      "hostname",
      "error-codes"
})
public class GoogleResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    @JsonProperty("hostname")
    private String hostname;

    @JsonProperty("error-codes")
    private ErrorCode[] errorCodes;

    @JsonIgnore
    public boolean hasClientError() {
        ErrorCode[] errors = getErrorCodes();
        if(errors == null) {
            return false;
        }
        for(ErrorCode error : errors) {
            switch(error) {
                case InvalidResponse:
                case MissingResponse: return true;
            }
        }
        return false;
    }

    static enum ErrorCode {
        MissingSecret,     InvalidSecret,
        MissingResponse,   InvalidResponse;

        private static Map<String, ErrorCode> errorsMap = new HashMap<String, ErrorCode>(4);

        static {
            errorsMap.put("missing-input-secret",   MissingSecret);
            errorsMap.put("invalid-input-secret",   InvalidSecret);
            errorsMap.put("missing-input-response", MissingResponse);
            errorsMap.put("invalid-input-response", InvalidResponse);
        }

        @JsonCreator
        public static ErrorCode forValue(String value) {
            return errorsMap.get(value.toLowerCase());
        }
    }

    // standard getters and setters
}
```

正如所暗示的那样，success属性中的true值表示用户已通过验证。否则，errorCodes属性将填充原因。

hostname是指将用户重定向到reCAPTCHA的服务器。如果你管理多个域并希望它们都共享相同的密钥对，可以选择自己验证主机名属性。

### 3.4 验证失败

如果验证失败，则会抛出异常。reCAPTCHA库需要指示客户端创建新的挑战。

我们在客户端的注册错误处理程序中执行此操作，方法是在库的grecaptcha小部件上调用reset：

```javascript
register(event){
    event.preventDefault();

    var formData= $('form').serialize();
    $.post(serverContext + "user/registration", formData, function(data){
        if(data.message == "success") {
            // success handler
        }
    })
    .fail(function(data) {
        grecaptcha.reset();
        ...
        
        if(data.responseJSON.error == "InvalidReCaptcha"){ 
            $("#captchaError").show().html(data.responseJSON.message);
        }
        ...
    }
}
```

## 4. 保护服务器资源

恶意客户端不需要遵守浏览器沙箱的规则。因此，我们的安全心态应该关注暴露的资源以及它们可能如何被滥用。

### 4.1 尝试缓存

重要的是要了解，通过集成reCAPTCHA，发出的每个请求都会导致服务器创建一个套接字来验证请求。

虽然我们需要一种更加分层的方法来真正缓解DoS，但我们可以实现一个基本缓存，将客户端限制为4个失败的验证码响应：

```java
public class ReCaptchaAttemptService {
    private int MAX_ATTEMPT = 4;
    private LoadingCache<String, Integer> attemptsCache;

    public ReCaptchaAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder()
              .expireAfterWrite(4, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
                  @Override
                  public Integer load(String key) {
                      return 0;
                  }
              });
    }

    public void reCaptchaSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void reCaptchaFailed(String key) {
        int attempts = attemptsCache.getUnchecked(key);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        return attemptsCache.getUnchecked(key) >= MAX_ATTEMPT;
    }
}
```

### 4.2 重构验证服务

如果客户端超过尝试限制，则首先通过中止合并缓存。否则，在处理不成功的GoogleResponse时，我们会记录包含客户端响应错误的尝试。成功验证会清除尝试缓存：

```java
public class CaptchaService implements ICaptchaService {

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;

    // ...

    @Override
    public void processResponse(String response) {

        // ...

        if(reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new InvalidReCaptchaException("Client exceeded maximum number of failed attempts");
        }

        // ...

        GoogleResponse googleResponse = ...

        if(!googleResponse.isSuccess()) {
            if(googleResponse.hasClientError()) {
                reCaptchaAttemptService.reCaptchaFailed(getClientIP());
            }
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
}
```

## 5. 集成谷歌的reCAPTCHA v3

Google的reCAPTCHA v3与以前的版本不同，因为它不需要任何用户交互。它只是为我们发送的每个请求给出一个分数，并让我们决定对我们的网络应用程序采取什么最终行动。

同样，要集成Google的reCAPTCHA 3，我们首先需要使用该服务注册我们的站点，将他们的库添加到我们的页面，然后使用Web服务验证令牌响应。

因此，让我们在[https://www.google.com/recaptcha/admin/create](https://www.google.com/recaptcha/admin/create)上注册我们的站点，并在选择reCAPTCHA v3后，我们将获得新的site-key和secret-key。

### 5.1 更新application.properties和CaptchaSettings

注册后，我们需要使用新键和我们选择的分数阈值更新application.properties：

```properties
google.recaptcha.key.site=6LefKOAUAAAAAE...
google.recaptcha.key.secret=6LefKOAUAAAA...
google.recaptcha.key.threshold=0.5
```

请务必注意，设置为0.5的阈值是默认值，可以通过分析[Google管理控制台](https://www.google.com/recaptcha/admin)中的实际阈值随着时间的推移进行调整。

接下来，让我们更新我们的CaptchaSettings类：

```java
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {
    // ... other properties
    private float threshold;

    // standard getters and setters
}
```

### 5.2 前端集成

我们现在将修改registration.html以将Google的库与我们的站点密钥一起包含在内。

在我们的注册表单中，我们添加了一个隐藏字段，用于存储从调用grecaptcha.execute函数时收到的响应令牌：

```html
<!DOCTYPE html>
<html>
<head>

    ...

    <script th:src='|https://www.google.com/recaptcha/api.js?render=${@captchaService.getReCaptchaSite()}'></script>
</head>
<body>

...

<form method="POST" enctype="utf8">
    ...

    <input type="hidden" id="response" name="response" value="" />
    ...
</form>

...

<script th:inline="javascript">
   ...
   var siteKey = /*[[${@captchaService.getReCaptchaSite()}]]*/;
   grecaptcha.execute(siteKey, {action: /*[[${T(cn.tuyucheng.taketoday.captcha.CaptchaService).REGISTER_ACTION}]]*/}).then(function(response) {
	$('#response').val(response);    
    var formData= $('form').serialize();
```

### 5.3 服务器端验证

我们必须发出与[reCAPTCHA服务器端验证](https://www.baeldung.com/spring-security-registration-captcha#Server)中相同的服务器端请求，以使用Web服务API验证响应令牌。

响应JSON对象将包含两个附加属性：

```json
{
    ...
    "score": number,
    "action": string
}
```

该score基于用户的交互，是一个介于0(很可能是机器人)和1.0(很可能是人类)之间的值。

action是Google引入的一个新概念，以便我们可以在同一个网页上执行多个reCAPTCHA请求。

每次执行reCAPTCHA v3时都必须指定一个操作。并且，我们必须验证响应中的action属性的值是否与预期的名称相对应。

### 5.4 检索响应令牌

使用HttpServletRequest从响应请求参数中检索reCAPTCHA v3响应令牌，并使用我们的CaptchaService进行验证。该机制与[上面](https://www.baeldung.com/spring-security-registration-captcha#Retrieve)在reCAPTCHA中看到的机制相同：

```java
public class RegistrationController {

    @Autowired
    private ICaptchaService captchaService;

    // ...

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid UserDto accountDto, HttpServletRequest request) {
        String response = request.getParameter("response");
        captchaService.processResponse(response, CaptchaService.REGISTER_ACTION);

        // rest of implementation
    }

    // ...
}
```

### 5.5 使用v3重构验证服务

重构后的CaptchaService验证服务类包含一个类似于之前版本的[processResponse方法](https://www.baeldung.com/spring-security-registration-captcha#Validation)的processResponse方法，但它会注意检查GoogleResponse的action和score参数：

```java
public class CaptchaService implements ICaptchaService {

    public static final String REGISTER_ACTION = "register";
    // ...

    @Override
    public void processResponse(String response, String action) {
        // ...

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
        if(!googleResponse.isSuccess() || !googleResponse.getAction().equals(action)
              || googleResponse.getScore() < captchaSettings.getThreshold()) {
            // ...
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
}
```

如果验证失败，我们将抛出异常，但请注意，对于v3，JavaScript客户端中没有可调用的reset方法。

我们仍将使用[上面](https://www.baeldung.com/spring-security-registration-captcha#Protecting)看到的相同实现来保护服务器资源。

### 5.6 更新GoogleResponse类

我们需要将新的属性score和action添加到[GoogleResponse](https://www.baeldung.com/spring-security-registration-captcha#Objectifying) Java bean：

```java
@JsonPropertyOrder({
      "success",
      "score",
      "action",
      "challenge_ts",
      "hostname",
      "error-codes"
})
public class GoogleResponse {
    // ... other properties
    @JsonProperty("score")
    private float score;
    @JsonProperty("action")
    private String action;

    // standard getters and setters
}
```

## 6. 总结

在本文中，我们将Google的reCAPTCHA库集成到我们的注册页面中，并实现了一个服务来通过服务器端请求验证验证码响应。

后来，我们用Google的reCAPTCHA v3库升级了注册页面，发现注册表单变得更精简了，因为用户不再需要采取任何操作。
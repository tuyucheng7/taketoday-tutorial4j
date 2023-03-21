## 1. 概述

在本教程中，我们将继续进行中的**Spring Security注册系列**，着眼于向用户重新发送验证链接，以防验证链接在用户有机会激活其帐户之前过期。

## 2. 重新发送验证链接

首先，让我们看看当用户**请求另一个验证链接**时会发生什么，以防前一个验证链接过期。

首先-我们将使用新的expireDate重置现有令牌。然后，我们将向用户发送一封新电子邮件，其中包含新链接/令牌：

```java
@GetMapping("/user/resendRegistrationToken")
public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken) {
    VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
    
    User user = userService.getUser(newToken.getToken());
    String appUrl = 
        "http://" + request.getServerName() + 
        ":" + request.getServerPort() + 
        request.getContextPath();
    SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
    mailSender.send(email);

    return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
}
```

以及实际构建用户收到的电子邮件消息的实用程序–constructResendVerificationTokenEmail()：

```java
private SimpleMailMessage constructResendVerificationTokenEmail (String contextPath, Locale locale, VerificationToken newToken, User user) {
    String confirmationUrl = contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
    String message = messages.getMessage("message.resendToken", null, locale);
    SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject("Resend Registration Token");
    email.setText(message + " rn" + confirmationUrl);
    email.setFrom(env.getProperty("support.email"));
    email.setTo(user.getEmail());
    return email;
}
```

我们还需要修改现有的注册功能-通过在模型上添加一些**关于令牌过期的新信息**：

```java
@GetMapping("/registrationConfirm")
public String confirmRegistration(Locale locale, Model model, @RequestParam("token") String token) {
    VerificationToken verificationToken = userService.getVerificationToken(token);
    if (verificationToken == null) {
        String message = messages.getMessage("auth.message.invalidToken", null, locale);
        model.addAttribute("message", message);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
        model.addAttribute("expired", true);
        model.addAttribute("token", token);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    user.setEnabled(true);
    userService.saveRegisteredUser(user);
    model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
    return "redirect:/login.html?lang=" + locale.getLanguage();
}
```

## 3. 异常处理器

之前的功能是，在特定条件下抛出异常；这些异常需要处理，我们将使用**自定义异常处理程序**来执行此操作：

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messages;

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ MailAuthenticationException.class })
    public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
              messages.getMessage("message.email.config.error", null, request.getLocale()), "MailError");

        return handleExceptionInternal(
              ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
              messages.getMessage("message.error", null, request.getLocale()), "InternalError");

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
```

注意：

-   我们使用@ControllerAdvice注解来处理整个应用程序中的异常
-   我们使用了一个简单的对象GenericResponse来发送响应：

```java
public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(String message) {
        super();
        this.message = message;
    }

    public GenericResponse(String message, String error) {
        super();
        this.message = message;
        this.error = error;
    }
}
```

## 4. 修改badUser.html

我们现在将修改badUser.html，使用户仅在其令牌过期时才能获得新的VerificationToken：

```html
<html>
<head>
    <title th:text="#{label.badUser.title}">bad user</title>
</head>
<body>
<h1 th:text="${param.message[0]}">error</h1>
<br>
<a th:href="@{/user/registration}" th:text="#{label.form.loginSignUp}">
    signup</a>

<div th:if="${param.expired[0]}">
    <h1 th:text="#{label.form.resendRegistrationToken}">resend</h1>
    <button onclick="resendToken()"
            th:text="#{label.form.resendRegistrationToken}">resend
    </button>

    <script src="jquery.min.js"></script>
    <script type="text/javascript">

var serverContext = [[@{/}]];

function resendToken(){
    $.get(serverContext + "user/resendRegistrationToken?token=" + token, 
      function(data){
            window.location.href = 
              serverContext +"login.html?message=" + data.message;
    })
    .fail(function(data) {
        if(data.responseJSON.error.indexOf("MailError") > -1) {
            window.location.href = serverContext + "emailError.html";
        }
        else {
            window.location.href = 
              serverContext + "login.html?message=" + data.responseJSON.message;
        }
    });
}
    </script>
</div>
</body>
</html>
```

请注意，我们在这里使用了一些非常基本的JavaScript和JQuery来处理“/user/resendRegistrationToken”的响应并基于它重定向用户。

## 5. 总结

在这篇快速文章中，我们允许用户请求一个新的验证链接来激活他们的帐户，以防旧的过期。
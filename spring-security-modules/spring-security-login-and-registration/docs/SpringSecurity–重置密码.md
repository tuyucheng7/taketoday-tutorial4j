## 1. 概述

在本教程中-我们将继续进行中的**Spring Security注册系列**，看看**基本的“忘记密码”功能**-以便用户可以在需要时安全地重置自己的密码。

## 2. 请求重置密码

密码重置流程通常在用户单击登录页面上的某种“重置”按钮时启动。然后，我们可以要求用户提供他们的电子邮件地址或其他身份信息。确认后，我们可以生成令牌并向用户发送电子邮件。

下图可视化了我们将在本文中实现的流程：

<img src="../assets/img.png">

## 3. 密码重置令牌

让我们首先创建一个PasswordResetToken实体，用它来重置用户密码：

```java
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;
}
```

当触发密码重置时-将创建一个令牌，并将**包含该令牌的特殊链接通过电子邮件发送给用户**。

令牌和链接仅在设定的时间段内有效(在此示例中为24小时)。

## 4. forgotPassword.html

该过程的**第一步是“忘记密码”页面**-系统会提示用户输入他们的电子邮件地址，以便开始实际的重置过程。

所以，让我们创建一个简单的forgotPassword.html要求用户提供电子邮件地址：

```html
<html>
<body>
<h1 th:text="#{message.resetPassword}">reset</h1>

<label th:text="#{label.user.email}">email</label>
<input id="email" name="email" type="email" value="" />
<button type="submit" onclick="resetPass()"
        th:text="#{message.resetPassword}">reset</button>

<a th:href="@{/registration.html}" th:text="#{label.form.loginSignUp}">
    registration
</a>
<a th:href="@{/login}" th:text="#{label.form.loginLink}">login</a>

<script src="jquery.min.js"></script>
<script th:inline="javascript">
var serverContext = [[@{/}]];
function resetPass(){
    var email = $("#email").val();
    $.post(serverContext + "user/resetPassword",{email: email} ,
      function(data){
          window.location.href = 
           serverContext + "login?message=" + data.message;
    })
    .fail(function(data) {
    	if(data.responseJSON.error.indexOf("MailError") > -1)
        {
            window.location.href = serverContext + "emailError.html";
        }
        else{
            window.location.href = 
              serverContext + "login?message=" + data.responseJSON.message;
        }
    });
}
</script>
</body>
</html>
```

我们现在需要从登录页面链接到这个新的“重置密码”页面：

```html
<a th:href="@{/forgetPassword.html}"
   th:text="#{message.resetPassword}">reset</a>
```

## 5. 创建PasswordResetToken

让我们从创建新的PasswordResetToken开始，并通过电子邮件将其发送给用户：

```java
@PostMapping("/user/resetPassword")
public GenericResponse resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
    User user = userService.findUserByEmail(userEmail);
    if (user == null) {
        throw new UserNotFoundException();
    }
    String token = UUID.randomUUID().toString();
    userService.createPasswordResetTokenForUser(user, token);
    mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
    return new GenericResponse(
        messages.getMessage("message.resetPasswordEmail", null, 
        request.getLocale()));
}
```

这是createPasswordResetTokenForUser()方法：

```java
public void createPasswordResetTokenForUser(User user, String token) {
    PasswordResetToken myToken = new PasswordResetToken(token, user);
    passwordTokenRepository.save(myToken);
}
```

这是方法constructResetTokenEmail()–用于发送带有重置令牌的电子邮件：

```java
private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user) {
    String url = contextPath + "/user/changePassword?token=" + token;
    String message = messages.getMessage("message.resetPassword", null, locale);
    return constructEmail("Reset Password", message + " \r\n" + url, user);
}

private SimpleMailMessage constructEmail(String subject, String body, User user) {
    SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(user.getEmail());
    email.setFrom(env.getProperty("support.email"));
    return email;
}
```

请注意我们如何使用一个简单的对象GenericResponse来表示我们对客户端的响应：

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

## 6. 检查PasswordResetToken

一旦用户点击他们电子邮件中的链接，user/changePassword端点：

-   验证令牌是否有效并且
-   向用户显示updatePassword页面，用户可以在其中输入新密码

然后将新密码和令牌传递给user/savePassword端点：

<img src="../assets/img_1.png">

用户收到带有用于重置密码的唯一链接的电子邮件，然后单击该链接：

```java
@GetMapping("/user/changePassword")
public String showChangePasswordPage(Locale locale, Model model, @RequestParam("token") String token) {
    String result = securityService.validatePasswordResetToken(token);
    if(result != null) {
        String message = messages.getMessage("auth.message." + result, null, locale);
        return "redirect:/login.html?lang=" + locale.getLanguage() + "&message=" + message;
    } else {
        model.addAttribute("token", token);
        return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }
}
```

这是validatePasswordResetToken()方法：

```java
public String validatePasswordResetToken(String token) {
    final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

    return !isTokenFound(passToken) ? "invalidToken"
        : isTokenExpired(passToken) ? "expired"
        : null;
}

private boolean isTokenFound(PasswordResetToken passToken) {
    return passToken != null;
}

private boolean isTokenExpired(PasswordResetToken passToken) {
    final Calendar cal = Calendar.getInstance();
    return passToken.getExpiryDate().before(cal.getTime());
}
```

## 7. 修改密码

此时，用户会看到简单的密码重置页面-其中唯一可能的选项是**提供新密码**：

### 7.1 updatePassword.html

```html
<html>
<body>
<div sec:authorize="hasAuthority('CHANGE_PASSWORD_PRIVILEGE')">
    <h1 th:text="#{message.resetYourPassword}">reset</h1>
    <form>
        <label th:text="#{label.user.password}">password</label>
        <input id="password" name="newPassword" type="password" value="" />

        <label th:text="#{label.user.confirmPass}">confirm</label>
        <input id="matchPassword" type="password" value="" />

        <label th:text="#{token.message}">token</label>
        <input id="token" name="token" value="" />

        <div id="globalError" style="display:none"
             th:text="#{PasswordMatches.user}">error</div>
        <button type="submit" onclick="savePass()"
                th:text="#{message.updatePassword}">submit</button>
    </form>

    <script th:inline="javascript">
var serverContext = [[@{/}]];
$(document).ready(function () {
    $('form').submit(function(event) {
        savePass(event);
    });
    
    $(":password").keyup(function(){
        if($("#password").val() != $("#matchPassword").val()){
            $("#globalError").show().html(/*[[#{PasswordMatches.user}]]*/);
        }else{
            $("#globalError").html("").hide();
        }
    });
});

function savePass(event){
    event.preventDefault();
    if($("#password").val() != $("#matchPassword").val()){
        $("#globalError").show().html(/*[[#{PasswordMatches.user}]]*/);
        return;
    }
    var formData= $('form').serialize();
    $.post(serverContext + "user/savePassword",formData ,function(data){
        window.location.href = serverContext + "login?message="+data.message;
    })
    .fail(function(data) {
        if(data.responseJSON.error.indexOf("InternalError") > -1){
            window.location.href = serverContext + "login?message=" + data.responseJSON.message;
        }
        else{
            var errors = $.parseJSON(data.responseJSON.message);
            $.each( errors, function( index,item ){
                $("#globalError").show().html(item.defaultMessage);
            });
            errors = $.parseJSON(data.responseJSON.error);
            $.each( errors, function( index,item ){
                $("#globalError").show().append(item.defaultMessage+"<br/>");
            });
        }
    });
}
</script>
</div>
</body>
</html>
```

请注意，我们在以下调用中显示重置令牌并将其作为POST参数传递以保存密码。

### 7.2 保存密码

最后，在提交之前的post请求时-保存新的用户密码：

```java
@PostMapping("/user/savePassword")
public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
    String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

    if(result != null) {
        return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
    }

    Optional user = userService.getUserByPasswordResetToken(passwordDto.getToken());
    if(user.isPresent()) {
        userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    } else {
        return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
    }
}
```

这是changeUserPassword()方法：

```java
public void changeUserPassword(User user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    repository.save(user);
}
```

和PasswordDto：

```java
public class PasswordDto {

    private String oldPassword;

    private  String token;

    @ValidPassword
    private String newPassword;
}
```

## 8. 总结

在本文中，我们为成熟的身份验证过程实现了一个简单但非常有用的功能-作为系统用户重置你自己的密码的选项。
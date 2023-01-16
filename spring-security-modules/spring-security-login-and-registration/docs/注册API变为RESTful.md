## 1. 概述

[在Baeldung 上注册系列](https://www.baeldung.com/spring-security-registration)的最后几篇文章中，我们以 MVC 方式构建了我们需要的大部分功能。

我们现在要将其中一些 API 转换为更加 RESTful 的方法。

## 2.注册操作

让我们从主要的注册操作开始：

```java
@PostMapping("/user/registration")
public GenericResponse registerUserAccount(
      @Valid UserDto accountDto, HttpServletRequest request) {
    logger.debug("Registering user account with information: {}", accountDto);
    User registered = createUserAccount(accountDto);
    if (registered == null) {
        throw new UserAlreadyExistException();
    }
    String appUrl = "http://" + request.getServerName() + ":" + 
      request.getServerPort() + request.getContextPath();
   
    eventPublisher.publishEvent(
      new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));

    return new GenericResponse("success");
}
```

那么，这与最初的 MVC 实现有何不同？

开始：

-   该请求现在已正确映射到 HTTP POST
-   我们现在返回一个适当的 DTO 并将其直接编组到响应的主体中
-   我们根本不再处理方法中的错误处理

我们还删除了旧的showRegistrationPage() – 因为不需要它来简单地显示注册页面。

## 3.注册.html

此外，我们需要将registration.html修改为：

-   使用Ajax提交注册表单
-   以 JSON 格式接收操作结果

开始：

```html
<html>
<head>
<title th:text="#{label.form.title}">form</title>
</head>
<body>
<form method="POST" enctype="utf8">
    <input  name="firstName" value="" />
    <span id="firstNameError" style="display:none"></span>
 
    <input  name="lastName" value="" />
    <span id="lastNameError" style="display:none"></span>
                     
    <input  name="email" value="" />           
    <span id="emailError" style="display:none"></span>
     
    <input name="password" value="" type="password" />
    <span id="passwordError" style="display:none"></span>
                 
    <input name="matchingPassword" value="" type="password" />
    <span id="globalError" style="display:none"></span>
 
    <a href="#" onclick="register()" th:text="#{label.form.submit}>submit</a>
</form>
             
 
<script src="jquery.min.js"></script>
<script type="text/javascript">
var serverContext = [[@{/}]];

function register(){
    $(".alert").html("").hide();
    var formData= $('form').serialize();
    $.post(serverContext + "/user/registration",formData ,function(data){
        if(data.message == "success"){
            window.location.href = serverContext +"/successRegister.html";
        }
    })
    .fail(function(data) {
        if(data.responseJSON.error.indexOf("MailError") > -1)
        {
            window.location.href = serverContext + "/emailError.html";
        }
        else if(data.responseJSON.error.indexOf("InternalError") > -1){
            window.location.href = serverContext + 
              "/login.html?message=" + data.responseJSON.message;
        }
        else if(data.responseJSON.error == "UserAlreadyExist"){
            $("#emailError").show().html(data.responseJSON.message);
        }
        else{
            var errors = $.parseJSON(data.responseJSON.message);
            $.each( errors, function( index,item ){
                $("#"+item.field+"Error").show().html(item.defaultMessage);
            });
            errors = $.parseJSON(data.responseJSON.error);
            $.each( errors, function( index,item ){
                $("#globalError").show().append(item.defaultMessage+"<br>");
            });
 }
}
</script>
</body>
</html>
```

## 4. 异常处理

通常，实施良好的异常处理策略可以使 REST API 更健壮且更容易出错。

我们使用相同的@ControllerAdvice机制来干净地处理不同的异常——现在我们需要一种新的异常类型。

这是BindException——当UserDto被验证时抛出(如果无效)。我们将覆盖默认的ResponseEntityExceptionHandler方法handleBindException()以在响应正文中添加错误：

```java
@Override
protected ResponseEntity<Object> handleBindException
  (BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("400 Status Code", ex);
    BindingResult result = ex.getBindingResult();
    GenericResponse bodyOfResponse = 
      new GenericResponse(result.getFieldErrors(), result.getGlobalErrors());
    
    return handleExceptionInternal(
      ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
}
```

接下来，我们还需要处理我们的自定义异常 UserAlreadyExistException——当用户使用已经存在的电子邮件注册时抛出该异常：

```java
@ExceptionHandler({ UserAlreadyExistException.class })
public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException ex, WebRequest request) {
    logger.error("409 Status Code", ex);
    GenericResponse bodyOfResponse = new GenericResponse(
      messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");
    
    return handleExceptionInternal(
      ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
}
```

## 5.通用响应

我们还需要改进GenericResponse实现以保留这些验证错误：

```java
public class GenericResponse {

    public GenericResponse(List<FieldError> fieldErrors, List<ObjectError> globalErrors) {
        super();
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.message = mapper.writeValueAsString(fieldErrors);
            this.error = mapper.writeValueAsString(globalErrors);
        } catch (JsonProcessingException e) {
            this.message = "";
            this.error = "";
        }
    }
}
```

## 6. UI——字段和全局错误

最后，让我们看看如何使用 jQuery 处理字段错误和全局错误：

```javascript
var serverContext = [[@{/}]];

function register(){
    $(".alert").html("").hide();
    var formData= $('form').serialize();
    $.post(serverContext + "/user/registration",formData ,function(data){
        if(data.message == "success"){
            window.location.href = serverContext +"/successRegister.html";
        }
    })
    .fail(function(data) {
        if(data.responseJSON.error.indexOf("MailError") > -1)
        {
            window.location.href = serverContext + "/emailError.html";
        }
        else if(data.responseJSON.error.indexOf("InternalError") > -1){
            window.location.href = serverContext + 
              "/login.html?message=" + data.responseJSON.message;
        }
        else if(data.responseJSON.error == "UserAlreadyExist"){
            $("#emailError").show().html(data.responseJSON.message);
        }
        else{
            var errors = $.parseJSON(data.responseJSON.message);
            $.each( errors, function( index,item ){
                $("#"+item.field+"Error").show().html(item.defaultMessage);
            });
            errors = $.parseJSON(data.responseJSON.error);
            $.each( errors, function( index,item ){
                $("#globalError").show().append(item.defaultMessage+"<br>");
            });
 }
}
```

注意：

-   如果存在验证错误，则消息对象包含字段错误，错误对象包含全局错误
-   我们在其字段旁边显示每个字段错误
-   我们在表单末尾的一个地方显示所有全局错误

## 七. 总结

这篇快速文章的重点是将 API 带入更 RESTful 的方向，并展示在前端处理该 API 的简单方法。

jQuery 前端本身不是重点——只是一个基本的潜在客户端，可以在任意数量的 JS 框架中实现，而 API 保持完全相同。

[GitHub 上](https://github.com/Baeldung/spring-security-registration)提供了本教程的完整实现。
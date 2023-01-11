## 1. 概述

在本教程中，我们将通过一个简单的场景来说明如何在 Spring Security 中使用 Run-As 身份验证。

关于 Run-As 的高级解释如下：用户可以作为具有不同权限的另一个主体执行某些逻辑。

## 2. RunAsManager

我们需要做的第一件事是设置我们的GlobalMethodSecurity并注入一个RunAsManager。

这负责为临时Authentication对象提供额外的权限：

```java
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Override
    protected RunAsManager runAsManager() {
        RunAsManagerImpl runAsManager = new RunAsManagerImpl();
        runAsManager.setKey("MyRunAsKey");
        return runAsManager;
    }
}
```

通过覆盖runAsManager，我们替换了基类中的默认实现——它只返回一个null。

还要注意关键属性——框架使用它来保护/验证临时身份验证对象(通过此管理器创建)。

最后——生成的Authentication对象是RunAsUserToken。


## 3.安全配置

为了验证我们的临时Authentication对象，我们将设置一个RunAsImplAuthenticationProvider：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    ...
    auth.authenticationProvider(runAsAuthenticationProvider());
}

@Bean
public AuthenticationProvider runAsAuthenticationProvider() {
    RunAsImplAuthenticationProvider authProvider = new RunAsImplAuthenticationProvider();
    authProvider.setKey("MyRunAsKey");
    return authProvider;
}
```

我们当然会使用我们在管理器中使用的相同密钥进行设置 - 以便提供者可以检查RunAsUserToken身份验证对象是否是使用相同的密钥创建的。

## 4. 带有@Secured的控制器

现在 - 让我们看看如何使用 Run-As Authentication 替换：

```java
@Controller
@RequestMapping("/runas")
class RunAsController {

    @Secured({ "ROLE_USER", "RUN_AS_REPORTER" })
    @RequestMapping
    @ResponseBody
    public String tryRunAs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current User Authorities inside this RunAS method only " + 
          auth.getAuthorities().toString();
    }

}
```

这里的核心是新角色——RUN_AS_REPORTER。这是 Run-As 功能的触发器——因为框架会因为前缀而以不同的方式处理它。

当请求通过此逻辑执行时，我们将拥有：

-   tryRunAs()方法之前的当前用户权限是 [ ROLE_USER ]
-   tryRunAs()方法中的当前用户权限是 [ ROLE_USER, ROLE_RUN_AS_REPORTER ]
-   临时Authentication对象仅在tryRunAS()方法调用期间替换现有的 Authentication 对象

## 5. 服务

最后，让我们实现实际的逻辑——一个简单的服务层，它也是安全的：

```java
@Service
public class RunAsService {

    @Secured({ "ROLE_RUN_AS_REPORTER" })
    public Authentication getCurrentUser() {
        Authentication authentication = 
          SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }
}
```

注意：

-   要访问getCurrentUser()方法，我们需要ROLE_RUN_AS_REPORTER
-   所以我们只能在tryRunAs()控制器方法中调用getCurrentUser()方法

## 6. 前端

接下来，我们将使用一个简单的前端来测试我们的 Run-As 功能：

```html
<html>
<body>
Current user authorities: 
    <span sec:authentication="principal.authorities">user</span>
<br/>
<span id="temp"></span>
<a href="#" onclick="tryRunAs()">Generate Report As Super User</a>
             
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
function tryRunAs(){
    $.get( "/runas" , function( data ) {
         $("#temp").html(data);
    });
}
</script>
</body>
</html>
```

所以现在，当用户触发“ Generate Report As Super User ”动作时——他们将获得临时的ROLE_RUN_AS_REPORTER权限。

## 7. 总结

在这个快速教程中，我们探索了一个使用 Spring Security [Run-As 身份验证替换](https://docs.spring.io/spring-security/site/docs/4.0.3.RELEASE/reference/htmlsingle/#runas)功能的简单示例。
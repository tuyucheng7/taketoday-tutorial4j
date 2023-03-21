## 1. 概述

在这篇简短的文章中，我们将探讨Spring Boot Actuator模块以及与Spring Security对发布身份验证和授权事件的支持。

## 2. Maven依赖

首先，我们需要将spring-boot-starter-actuator添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.7.2</version>
</dependency>
```

最新版本在[Maven中央仓库](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-actuator/3.0.3)中可用。

## 3. 监听身份验证和授权事件

要在Spring Boot应用程序中记录所有身份验证和授权尝试，我们可以只定义一个带有@EventListener方法的bean：

```java
@Component
public class LoginAttemptsLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptsLogger.class);

    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        LOGGER.info("Principal " + auditEvent.getPrincipal() + " - " + auditEvent.getType());

        WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");
        LOGGER.info("  Remote IP address: " + details.getRemoteAddress());
        LOGGER.info("  Session Id: " + details.getSessionId());
    }
}
```

请注意，我们只是输出AuditApplicationEvent中可用的一些内容，以显示可用的信息。在实际应用程序中，你可能希望将该信息存储在数据存储或缓存中以进一步处理它。

请注意，任何Spring bean都可以工作；新的Spring事件机制的使用非常简单：

+ 使用@EventListener标注方法
+ 添加AuditApplicationEvent作为方法的唯一参数

运行应用程序的输出如下所示：

```shell
21:06:49.686 [http-nio-8080-exec-3] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>> Principal anonymousUser - AUTHORIZATION_FAILURE 
21:06:49.686 [http-nio-8080-exec-3] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Remote IP address: 0:0:0:0:0:0:0:1 
21:06:49.686 [http-nio-8080-exec-3] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Session Id: null 

21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>> Principal anonymousUser - AUTHORIZATION_FAILURE 
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Remote IP address: 0:0:0:0:0:0:0:1 
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Session Id: 59B00322DF239941AFCD3C527FBA8B5C 

21:07:02.134 [http-nio-8080-exec-5] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>> Principal anonymousUser - AUTHORIZATION_SUCCESS 
21:07:02.134 [http-nio-8080-exec-5] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Remote IP address: 0:0:0:0:0:0:0:1 
21:07:02.134 [http-nio-8080-exec-5] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Session Id: 59B00322DF239941AFCD3C527FBA8B5C 
```

在此示例中，监听器接收到三个AuditApplicationEvent事件：

1. 未登录，已请求访问受限页面
2. 登录时使用了错误的密码
3. 第二次使用了正确的密码

## 4. Authentication Audit Listener

如果Spring Boot的AuthorizationAuditListener公开的信息还不够，你可以**创建自己的bean来公开更多的信息**。

让我们来看一个例子，其中我们还公开了授权失败时访问的请求URL：

```java
@Component
public class ExposeAttemptedPathAuthorizationAuditListener extends AbstractAuthorizationAuditListener {
    public static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthorizationFailureEvent)
            onAuthorizationFailureEvent((AuthorizationFailureEvent) event);
    }

    private void onAuthorizationFailureEvent(AuthorizationFailureEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", event.getAccessDeniedException().getClass().getName());
        data.put("message", event.getAccessDeniedException().getMessage());
        data.put("requestUrl", ((FilterInvocation) event.getSource()).getRequestUrl());
        if (event.getAuthentication().getDetails() != null)
            data.put("details", event.getAuthentication().getDetails());

        publish(new AuditEvent(event.getAuthentication().getName(), AUTHORIZATION_FAILURE, data));
    }
}
```

现在，我们可以在监听器中记录请求URL：

```java
@Component
public class LoginAttemptsLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptsLogger.class);

    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        LOGGER.info("Principal " + auditEvent.getPrincipal() + " - " + auditEvent.getType());

        WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");
        LOGGER.info("  Remote IP address: " + details.getRemoteAddress());
        LOGGER.info("  Session Id: " + details.getSessionId());
        LOGGER.info("  Request URL: " + auditEvent.getData().get("requestUrl"));
    }
}
```

因此，输出现在包含请求的URL：

```shell
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>> Principal anonymousUser - AUTHORIZATION_FAILURE 
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Remote IP address: 0:0:0:0:0:0:0:1 
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Session Id: null 
21:06:58.150 [http-nio-8080-exec-4] INFO  [c.t.t.a.auditing.LoginAttemptsLogger] >>>   Request URL: /
```

请注意，在此示例中，我们从抽象的AbstractAuthorizationAuditListener进行了扩展，因此我们可以在实现中使用该基类的publish方法。

如果要对其进行测试，请运行：

```shell
mvn clean spring-boot:run
```

然后，你可以使用浏览器访问[http://localhost:8080/](http://localhost:8080/)。

## 5. 保存审计事件

默认情况下，Spring Boot将审计事件存储在AuditEventRepository中。如果你不使用自己的实现创建bean，那么将为你注入InMemoryAuditEventRepository。

InMemoryAuditEventRepository是一种循环缓冲区，用于在内存中存储最后4000个审计事件。然后可以通过管理端点[http://localhost:8080/auditevents](http://localhost:8080/auditevents)访问这些事件。

这将返回审计事件的JSON表示：

```json
{
    "events": [
        {
            "timestamp": "2022-06-13T16:22:00+0000",
            "principal": "anonymousUser",
            "type": "AUTHORIZATION_FAILURE",
            "data": {
                "requestUrl": "/auditevents",
                "details": {
                    "remoteAddress": "0:0:0:0:0:0:0:1",
                    "sessionId": null
                },
                "type": "org.springframework.security.access.AccessDeniedException",
                "message": "Access is denied"
            }
        },
        {
            "timestamp": "2022-06-13T16:22:00+0000",
            "principal": "anonymousUser",
            "type": "AUTHORIZATION_FAILURE",
            "data": {
                "requestUrl": "/favicon.ico",
                "details": {
                    "remoteAddress": "0:0:0:0:0:0:0:1",
                    "sessionId": "18FA15865F80760521BBB736D3036901"
                },
                "type": "org.springframework.security.access.AccessDeniedException",
                "message": "Access is denied"
            }
        }
    ]
}
```

## 6. 总结

借助Spring Boot中的Actuator支持，记录用户的身份验证和授权尝试变得微不足道。读者还可以参考[生产就绪审计](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-auditing.html)以获得一些额外信息。
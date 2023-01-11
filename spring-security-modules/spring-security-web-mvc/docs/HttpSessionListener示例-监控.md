## 1. 概述

本教程将演示如何注册javax.servlet.http.HttpSessionListener并使用metrics跟踪Web应用程序中的活动会话数。

## 2. 定义监听器

我们可以在web.xml中注册HTTP Session监听器：

```text
<web-app ...>
    <listener>
        <listener-class>cn.tuyucheng.web.SessionListenerWithMetrics</listener-class>
    </listener>
</web-app>
```

或者，在Servlet 3环境中，我们也可以使用@WebListener来注册监听器。
在这种情况下，我们需要使用@ServletComponentScan标注SpringBootApplication主类。

最后，我们还可以通过声明一个ServletListenerRegistrationBean bean来使用Java配置注册监听器：

```text
@Bean
public ServletListenerRegistrationBean<SessionListenerWithMetrics> sessionListenerWithMetrics() {
    ServletListenerRegistrationBean<SessionListenerWithMetrics> listenerRegBean =
       new ServletListenerRegistrationBean<>();
    
    listenerRegBean.setListener(new SessionListenerWithMetrics());
    return listenerRegBean;
}
```

## 3. 监听器


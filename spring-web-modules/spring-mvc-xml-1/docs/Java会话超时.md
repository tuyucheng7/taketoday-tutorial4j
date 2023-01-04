## 1. 概述

本教程将展示如何在基于Servlet的Web应用程序中设置会话超时。

## 2. web.xml中的全局会话超时

所有Http Session的超时时间都可以在web应用的web.xml中配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app ...>

    ...
    <session-config>
        <session-timeout>10</session-timeout>
    </session-config>

</web-app>
```

请注意，超时值是以分钟为单位设置的，而不是以秒为单位。

一个有趣的注意点是，在可以使用注解而不是XML部署描述符的Servlet 3.0环境中，无法以编程方式设置全局会话超时。会话超时的编程配置在Servlet规范JIRA上确实有一个未解决的问题，但该问题尚未安排。

## 3. 每个会话的编程超时

当前会话的超时只能通过javax.servlet.http.HttpSession的API以编程方式指定：

```java
HttpSession session = request.getSession();
session.setMaxInactiveInterval(10 * 60);
```

与具有以分钟为单位的值的<session-timeout\>元素相反，setMaxInactiveInterval方法接受以秒为单位的值。

## 4. Tomcat会话超时

所有Tomcat服务器都提供[一个默认的web.xml文件](https://tomcat.apache.org/tomcat-7.0-doc/default-servlet.html)，可以为整个Web服务器进行全局配置，它位于：

```bash
$tomcat_home/conf/web.xml
```

此默认部署描述符确实将<session-timeout\>配置为30分钟的值。

在自己的web.xml描述符中提供自己的超时值的各个已部署应用程序将优先于并覆盖此全局web.xml配置。

请注意，[在Jetty中也是可能的](https://www.eclipse.org/jetty/documentation/current/webdefault-xml.html)：该文件位于：

```bash
$jetty_home/etc/webdefault.xml
```

## 5. 总结

本教程讨论了如何在Servlet Java应用程序中配置HTTP会话超时的实际方面。我们还说明了如何在Tomcat和Jetty中的Web服务器级别设置它。

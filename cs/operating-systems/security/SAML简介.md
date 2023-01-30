## 1. 概述

安全断言标记语言 (SAML) 是一种开放的联合身份标准，用于在提供商之间交换[授权和身份验证数据。](https://www.baeldung.com/cs/authentication-vs-authorization)数据可以在许多支持 SAML 的应用程序和[安全](https://www.baeldung.com/security-spring)域之间共享。因此，SAML 主要用于[SSO](https://www.baeldung.com/cs/sso-guide)目的。在本文中，我们将深入探讨 SAML 基本概念。我们将涉及最新版本的 SAML 2.0。

## 2.什么是SAML？

SAML 是一种基于[XML](https://www.baeldung.com/java-xml)的标记语言，用于在身份提供者和服务提供者之间共享安全数据。身份提供者是对用户进行身份验证并将用户身份及其授权级别数据发送给服务提供者的一方。

另一方面，服务提供者信任身份提供者，并允许用户根据其授权级别访问资源。因此，SAML 提供了一种在提供商之间交换安全数据的标准化方式。

SAML 包含供应商用来做出访问控制决策的安全断言。此外，SAML 可以提供协议消息、协议消息绑定和配置文件。我们将在后面的部分详细介绍它们。

## 3.工作流程

正如我们已经指出的，SAML 的主要用例是 SSO。因此，用户可以登录一次并访问许多独立的服务，而无需单独登录。让我们看一个 SAML 工作流的例子：

1.  用户向服务提供商请求资源。服务提供商执行安全检查(例如，检查[会话](https://www.baeldung.com/cs/tokens-vs-sessions)cookie 是否存在)。如果安全检查通过，请转到第 8 点。如果没有通过，请转到下一点。

2.  服务提供商将请求重定向到身份提供商的 SSO 服务。它使用查询参数通知身份提供者这是一个SAMLRequest 。该参数是一个解码和缩小的<samlp:AuthnRequest>元素。

3.  用户代理对重定向的 URL 执行 GET 请求。SSO 服务处理AuthnRequest 并识别用户，

4.  接下来，SSO 服务使用 XHTML 表单进行响应，如下所示：

    ```xml
    <form method="post" action="some-action-url" ...>
        <input type="hidden" name="SAMLResponse" value="some-response-value" />
        ...
        <input type="submit" value="Submit" />
      </form>
    ```

    SAMLResponse的值是编码的<samlp:Response>元素。

5.  用户以 XHTML 形式请求服务提供者的断言消费者服务。

6.  断言消费者服务处理 XHTML 表单，为用户创建安全上下文，并重定向到步骤 1 中请求的资源。

7.  用户再次请求资源。现在，存在有效的安全上下文，因此可以返回资源。

8.  服务提供者以请求的资源作为响应。

现在，让我们看一下工作流程的可视化表示：

[![工作流程](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/workflow.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/workflow.svg)

总而言之，服务提供者需要来自身份提供者的安全上下文，以查看用户是否可以访问特定资源。如果上下文存在，资源可以立即返回。否则，SAML 指定的请求将发送到身份提供者以创建安全上下文。

通过安全上下文，用户无需单独登录即可访问信任该特定身份提供者的多个服务。

## 4.建筑

在本节中，我们将简要介绍 SAML 元素。完整的规范可以在[官方 SAML 规范页面上](http://saml.xml.org/saml-specifications)找到。在这里，我们将关注四个元素——断言、协议、绑定和配置文件。这些是 SAML 可以使用的核心元素。

### 4.1. 断言

首先，正如我们在官方规范中所读到的：

>   断言是提供零个或多个由 SAML 机构做出的声明的信息包。

断言数据放置在以下标签之间：

```xml
<saml:Assertion ...>
   ..
 </saml:Assertion>
```

SAML 2.0 断言语句分为三种类型：

1.  身份验证——通知服务提供商特定用户在特定时间使用特定身份验证方法进行了身份验证。
2.  属性——将用户的相关属性传递给服务提供商。属性是包含与用户相关的一些信息的名称-值数据对。
3.  授权——通知服务用户用户是否可以访问所需的资源。

一个断言可以包含一个或多个上述元素。基于这些元素，提供者可以执行控制访问决策。

### 4.2. 协议

第二种类型的 SAML 元素是协议。它们描述了特定元素应该如何在请求和响应中打包和使用。SAML 2.0 提供了多种协议：

-   断言查询和请求协议
-   工件解析协议
-   单点注销协议
-   身份验证请求协议
-   名称标识符管理协议
-   名称标识符映射协议

每个协议都在规范中进行了详细说明。因此，我们不会在这里详细介绍。

### 4.3. 绑定

第三种重要的元素类型是绑定。它们告知 SAML 消息的机制和映射。例如，SAML 消息可以封装在[SOAP](https://www.baeldung.com/spring-boot-soap-web-service)信封中。SAML 2.0 提供了几种绑定：

-   SAML SOAP 绑定
-   反向肥皂 (PAOS)
-   绑定[HTTP](https://www.baeldung.com/cs/rest-vs-http)重定向 (GET) 绑定
-   HTTP POST 绑定
-   HTTP 工件绑定
-   SAML URI 绑定

### 4.4. 简介

最后但同样重要的是，还有 SAML 配置文件。配置文件表示断言、绑定和协议如何协作处理特定用例。我们可以在规范中读到：

>   通常，SAML 配置文件定义了约束和/或扩展，以支持特定应用程序使用 SAML——目标是通过消除通用标准中不可避免的一些灵活性来增强互操作性。

有多种 SAML 配置文件，但主要的是 Web 浏览器 SSO。

## 5.总结

在本文中，我们讨论了 SAML 2.0 标准。我们描述了它的工作流程和核心架构。综上所述，SAML 标准最重要的用例是 SSO 机制。
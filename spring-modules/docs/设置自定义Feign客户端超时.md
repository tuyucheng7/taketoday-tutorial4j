## 一、简介

[Spring Cloud Feign Client](https://www.baeldung.com/spring-cloud-openfeign)是一个方便的声明式 REST 客户端，我们用它来实现微服务之间的通信。

在这个简短的教程中，我们将展示如何在全局和每个客户端设置自定义 Feign 客户端连接超时。

## 2. 默认值

Feign Client 是非常可配置的。

在超时方面，它允许我们配置读取和连接超时。连接超时是 TCP 握手所需的时间，而读取超时是从套接字读取数据所需的时间。

连接超时和读取超时默认分别为 10 秒和 60 秒。

## 3. 全球范围内

我们可以通过 feign.client.config 设置适用于应用程序中每个 Feign Client 的连接和读取超时*。**在我们的application.yml文件中设置的**默认*属性：

```java
feign:
  client:
    config:
      default:
        connectTimeout: 60000
        readTimeout: 10000复制
```

**这些值表示超时发生前的毫秒数。**

## 4.每位顾客

也可以**通过命名客户端为每个特定客户端设置这些超时：**

```java
feign:
  client:
    config:
      FooClient:
        connectTimeout: 10000
        readTimeout: 20000复制
```

而且，我们当然可以毫无问题地同时列出全局设置和每个客户端覆盖。

## 5.结论

在本教程中，我们解释了如何调整 Feign Client 的超时以及如何通过*application.yml*文件设置自定义值。[请按照我们主要的 Feign 介绍](https://www.baeldung.com/spring-cloud-openfeign#properties)随意尝试这些。
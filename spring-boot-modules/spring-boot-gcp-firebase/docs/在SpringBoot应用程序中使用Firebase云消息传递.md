## 1. 简介

在本快速教程中，我们将展示如何使用Google的Firebase Cloud Messaging向Web和移动应用程序发送推送通知。

## 2. 什么是FCM？

Firebase Cloud Messaging(简称FCM)，是一种基于云的消息传递服务，提供以下功能：

-   可靠地将消息发送到移动或Web应用程序，这里称为“客户端”
-   使用主题或基于订阅的寻址向所有或特定客户端发送消息
-   在服务器应用程序中接收来自客户端的消息

以下是该技术的一些实际应用示例：

-   发送包含特定产品独家优惠的有针对性的消息
-   通知给定应用程序的所有用户新功能可用
-   即时消息/聊天应用程序
-   直接通知给定客户

## 3. 应用架构

基于FCM的应用程序的典型架构由服务器、客户端和FCM本身组成：

<img src="../assets/img.png">

**在本教程中，我们将重点关注此类应用程序的服务器端**。在我们的例子中，这个服务器将是一个基于Spring Boot的服务，它公开了一个REST API。这个API允许我们探索向我们(希望如此)庞大的用户群发送通知的不同方式：

-   向主题发布通知
-   向特定客户端发布通知
-   向多个主题发布通知

这种应用程序的核心是客户端、主题和订阅的概念。

### 3.1 主题

**主题是一个命名实体，充当共享某些属性的通知的中心**。例如，金融应用程序可以为其交易的每种资产使用一个主题。同样，体育应用程序可以为每支球队甚至特定比赛使用一个主题。

### 3.2 客户端

**客户端是我们安装在给定移动设备上或在浏览器上运行的应用程序的实例**。要接收通知，客户端使用适当的SDK API调用将自己注册到我们的Firebase项目。

成功注册后，客户端会从Firebase获得一个唯一的注册令牌。通常，此令牌会发送到服务器端，因此可用于直接通知。

### 3.3 订阅

**订阅表示客户端和主题之间的关联**。服务器应用程序使用采用一个或多个客户端注册令牌和主题名称的API调用来创建新订阅。

## 4. Firebase项目设置

Firebase项目充当我们将在应用程序中使用的云资源的容器。Google提供了一个免费的初始层，允许开发人员试验可用的服务，并且只需为超出可用配额的部分付费。

因此，要使用FCM，我们的第一步是使用[Firebase的控制台](https://console.firebase.google.com/)创建一个新项目。登录后，我们将获得Firebase的主页：

<img src="../assets/img_1.png">

在这里，我们可以选择添加新项目或选择现有项目。让我们选择前者。这将启动一个向导，该向导将收集创建新项目所需的信息。

首先，我们必须命名我们的项目：

<img src="../assets/img_2.png">

请注意，在通知的名称下，有一个带有为此项目生成的内部ID的按钮。通常，不需要更改它，但如果我们出于某种原因不喜欢它，我们可以单击它并使用不同的。**另请注意，虽然项目ID是唯一的，但项目名称不是唯一的，这可能有点令人困惑**。

在下一步中，我们可以选择将分析添加到我们的项目中。我们将禁用此选项，因为本教程不需要它。如果需要，我们可以稍后启用它。

<img src="../assets/img_3.png">

单击“创建项目”后，Firebase会将我们重定向到新创建的项目管理页面。

## 5. 生成服务账号

**为了让服务器端应用程序对Firebase服务进行API调用，我们需要生成一个新的服务帐户并获取其凭据**。我们可以通过访问项目设置页面并选择“服务账号”选项卡来做到这一点：

<img src="../assets/img_4.png">

任何新项目都以管理服务帐户开始，该帐户基本上可以在该项目中执行任何操作。**在这里，我们将使用它进行测试，但实际应用程序应该创建一个有限权限的专用服务帐户**。这样做需要一些IAM(Google的身份和访问管理)知识，并且超出了本教程的范围。

我们现在必须单击“生成新的私钥”按钮来下载包含调用Firebase API所需数据的JSON文件。不用说，我们必须将此文件存储在安全位置。

## 6. Maven依赖

现在我们的Firebase项目已经准备就绪，是时候编写发送通知的服务器组件了。除了MVC应用程序的常规Spring Boot启动器之外，我们还必须添加firebase-admin依赖项：

```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.1.1</version>
</dependency>
```

此依赖项的最新版本可在[Maven Central](https://central.sonatype.com/artifact/com.google.firebase/firebase-admin/9.1.1)上获得。

## 7. Firebase消息配置

**FirebaseMessaging类是我们使用FCM发送消息的主要门面**。由于此类是线程安全的，我们将在@Configuration类中使用@Bean方法来创建它的单个实例并使其可用于我们的控制器：

```java
@Bean
FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
    return FirebaseMessaging.getInstance(firebaseApp);
}
```

很简单，但现在我们必须提供一个FirebaseApp。或者，我们可以使用getInstance()的无参数变体，它可以完成工作但不允许我们更改任何默认参数。

为了解决这个问题，让我们创建另一个@Bean方法来创建一个自定义的FirebaseApp实例：

```java
@Bean
FirebaseApp firebaseApp(GoogleCredentials credentials) {
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build();

    return FirebaseApp.initializeApp(options);
}
```

在这里，唯一的自定义是使用特定的GoogleCredentials对象。FirebaseOptions的构建器允许我们调整Firebase客户端的其他方面：

-   超时
-   HTTP请求工厂
-   特定服务的自定义端点
-   线程工厂

最后一项配置是凭据本身。我们将创建另一个@Bean，它使用通过[配置属性](https://www.baeldung.com/configuration-properties-in-spring-boot)提供的服务帐户或使用默认凭证链创建GoogleCredentials实例：

```java
@Bean
GoogleCredentials googleCredentials() {
    if (firebaseProperties.getServiceAccount() != null) {
        try (InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
            return GoogleCredentials.fromStream(is);
        }
    } 
    else {
        // Use standard credentials chain. Useful when running inside GKE
        return GoogleCredentials.getApplicationDefault();
    }
}
```

**这种方法简化了在我们可能有多个服务帐户文件的本地机器上的测试**。我们可以使用标准的GOOGLE_APPLICATION_CREDENTIALS环境变量来指向正确的文件，但更改它有点麻烦。

> 注意：运行应用程序会抛出异常Factory method ‘googleCredentials' threw exception; nested exception is java.lang.RuntimeException: java.io.IOException: Invalid PKCS#8 data，直到你在文件**firebase-service-account.json**中为以下键输入有效值：

```json
"project_id": "REPLACE WITH VALID PROJECT ID",
"private_key_id": "REPLACE WITH VALID PRIVATE KEY ID",
"private_key": "REPLACE WITH VALID PRIVATE KEY",
"client_email": "REPLACE WITH VALID CLIENT EMAIL",
"client_id": "REPLACE WITH VALID CLIENT ID",
```

## 8. 向主题发送消息

向主题发送消息需要两个步骤：构建Message对象并使用FirebaseMessaging的方法之一发送它。**Message实例是使用熟悉的[构建器模式](https://www.baeldung.com/java-builder-pattern-freebuilder)创建的**：

```java
Message msg = Message.builder()
    .setTopic(topic)
    .putData("body", "some data")
    .build();
```

一旦我们有了Message实例，我们就可以使用send()来请求它的传递：

```java
String id = fcm.send(msg);
```

在这里，fcm是我们注入到控制器类中的FirebaseMessaging实例。send()返回一个值，该值是FCM生成的消息标识符。我们可以将此标识符用于跟踪或记录目的。

send()也有一个异步版本sendAsync()，它返回一个ApiFuture对象。此类扩展了Java的Future，因此我们可以轻松地将其与[Project Reactor](https://www.baeldung.com/reactor-core)等响应式框架一起使用。

## 9. 向特定客户端发送消息

正如我们之前提到的，每个客户端都有一个与之关联的唯一订阅令牌。我们在构建Message时使用此令牌作为其“地址”，而不是主题名称：

```java
Message msg = Message.builder()
    .setToken(registrationToken)
    .putData("body", "some data")
    .build();
```

对于我们想要向多个客户端发送相同消息的用例，我们可以使用MulticastMessage和sendMulticast()。它们的工作方式与单播版本相同，但允许我们在一次调用中将消息发送给多达500个客户端：

```java
MulticastMessage msg = MulticastMessage.builder()
    .addAllTokens(message.getRegistrationTokens())
    .putData("body", "some data")
    .build();

BatchResponse response = fcm.sendMulticast(msg);
```

返回的BatchResponse包含生成的消息标识符以及与给定客户端的传递相关的任何错误。

## 10. 向多个主题发送消息

FCM允许我们指定条件来定义消息的目标受众。**条件是一种逻辑表达式，它根据客户端订阅(或未订阅)的主题来选择客户端**。例如，给定三个主题(T1、T2和T3)，此表达式的目标设备是T1或T2但不是T3的订阅者：

```java
('T1' in topics || 'T2' in topics) && !('T3' in topics)
```

在这里，topics变量表示给定客户端已订阅的所有主题。现在我们可以使用构建器中可用的setCondition()方法构建一条发送给满足此条件的客户端的消息：

```java
Message msg = Message.builder()
    .setCondition("('T1' in topics || 'T2' in topics) && !('T3' in topics)")
    .putData("body", "some data")
    .build();

String id = fcm.send(msg);
```

## 11. 为客户端订阅主题

我们使用subscribeToTopic()或其异步变体subscribeToTopicAsync()方法来创建将客户端与主题相关联的订阅。该方法接收客户端注册令牌列表和主题名称作为参数：

```java
fcm.subscribeToTopic(registrationTokens, topic);
```

请注意，与其他消息传递系统相比，返回值没有创建订阅的标识符。**如果我们想跟踪给定客户端订阅了哪些主题，我们必须自己做**。

要取消订阅客户端，我们使用unsubscribeFromTopic()：

```java
fcm.subscribeToTopic(Arrays.asList(registrationToken), topic);
```

## 12. 总结

在本教程中，我们展示了如何使用Google的Firebase云消息服务向Web和移动应用程序发送通知消息。
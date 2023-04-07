## 1. 概述

在这个简短的教程中，我们将**介绍@ConditionalOnProperty注解的主要用途**。

首先，我们将从一些关于什么是@ConditionalOnProperty的背景知识开始。然后我们看一些实际的例子来帮助理解它是如何工作的以及它带来的功能。

## 2. @ConditionalOnProperty的用途

通常，在开发基于Spring的应用程序时，**我们需要根据配置属性的存在和值有条件地[创建一些bean](https://www.baeldung.com/spring-bean)**。

例如，我们可能希望注册一个DataSource bean以指向生产或测试数据库，具体取决于我们是将属性值设置为“prod”还是“test”。

幸运的是，实现这一点并不像乍看起来那么难。Spring框架正是为此目的提供了[@ConditionalOnProperty注解](https://www.baeldung.com/spring-boot-custom-auto-configuration#3-property-conditions)。

简而言之，@ConditionalOnProperty仅在环境属性存在且具有特定值时才启用bean注册。默认情况下，必须定义指定的属性并且不等于false。

现在我们已经熟悉了@ConditionalOnProperty注解的用途，让我们更深入地了解它是如何工作的。

## 3. 实践中的@ConditionalOnProperty注解

为了举例说明@ConditionalOnProperty的使用，我们将开发一个基本的通知系统。现在为了简单起见，假设我们要发送电子邮件通知。

首先，我们需要创建一个简单的服务来发送通知消息。例如，考虑NotificationSender接口：

```java
public interface NotificationSender {
    String send(String message);
}
```

接下来，让我们提供NotificationSender接口的实现来发送电子邮件：

```java
public class EmailNotification implements NotificationSender {
    @Override
    public String send(String message) {
        return "Email Notification: " + message;
    }
}
```

现在让我们看看如何使用@ConditionalOnProperty注解。**我们以这样一种方式配置NotificationSender bean，使其仅在定义了属性notification.service时才加载**：

```java
@Bean(name = "emailNotification")
@ConditionalOnProperty(prefix = "notification", name = "service")
public NotificationSender notificationSender() {
    return new EmailNotification();
}
```

正如我们所见，**prefix和name属性用于表示应该检查的配置属性**。

最后，我们需要在[application.properties文件](https://www.baeldung.com/properties-with-spring#1-applicationproperties---the-default-property-file)中定义我们的自定义属性：

```properties
notification.service=email
```

## 4. 高级配置

正如我们已经了解到的，@ConditionalOnProperty注解允许我们根据配置属性的存在有条件地注册bean。

但是，我们可以使用这个注解做更多的事情。因此，让我们探索一下！

假设我们要添加另一个通知服务，例如允许我们发送SMS通知的服务。

为此，我们需要创建另一个NotificationSender实现：

```java
public class SmsNotification implements NotificationSender {
    @Override
    public String send(String message) {
        return "SMS Notification: " + message;
    }
}
```

由于我们有两个实现，让我们看看如何使用@ConditionalOnProperty有条件地加载正确的NotificationSender bean。

为此，注解提供了havingValue属性。非常有趣的是，**它定义了属性必须具有的值，以便将特定的bean添加到[Spring容器](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring#the-spring-ioc-container)中**。

现在，让我们指定要在上下文中注册SmsNotification实现的条件：

```java
@Bean(name = "smsNotification")
@ConditionalOnProperty(prefix = "notification", name = "service", havingValue = "sms")
public NotificationSender notificationSender2() {
    return new SmsNotification();
}
```

在havingValue属性的帮助下，我们明确表示我们只希望在notification.service设置为sms时加载SmsNotification。

值得一提的是，@ConditionalOnProperty还有另一个名为[matchIfMissing](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/ConditionalOnProperty.html#matchIfMissing--)的属性。**此属性指定条件是否应匹配，以防属性不可用**。

现在让我们将所有部分放在一起，并编写一个简单的测试用例来确认一切都按预期工作：

```java
@Test
void whenValueSetToEmail_thenCreateEmailNotification() {
	this.contextRunner.withPropertyValues("notification.service=email")
		.withUserConfiguration(NotificationConfig.class)
		.run(context -> {
			assertThat(context).hasBean("emailNotification");
			NotificationSender notificationSender = context.getBean(EmailNotification.class);
			assertThat(notificationSender.send("Hello From Tuyucheng!")).isEqualTo("Email Notification: Hello From Tuyucheng!");
			assertThat(context).doesNotHaveBean("smsNotification");
		});
}
```

## 5. 总结

在这篇简短的文章中，我们强调了使用@ConditionalOnProperty注解的目的。然后我们通过一个实际的例子学习了如何使用它来有条件地加载Spring beans。
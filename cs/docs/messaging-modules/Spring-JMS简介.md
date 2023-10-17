## 1. 概述

Spring提供了一个JMS集成框架，简化了JMS API的使用。本文主要介绍这种集成的基本概念。

## 2. Maven依赖

为了在我们的应用程序中使用Spring JMS，我们需要在pom.xml中添加必要的工件：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jms</artifactId>
    <version>4.3.3.RELEASE</version>
</dependency>
```

## 3. JmsTemplate

**JmsTemplate类在发送或同步接收消息时处理资源的创建和释放**。因此，使用这个JmsTemplate的类只需要实现方法定义中指定的回调接口。

从Spring 4.1开始，JmsMessagingTemplate建立在JmsTemplate之上，JmsTemplate提供了与消息传递抽象的集成，即org.springframework.messaging.Message。反过来，这使我们能够创建以通用方式发送的消息。

## 4. 连接管理

为了连接并能够发送/接收消息，我们需要配置一个ConnectionFactory。**ConnectionFactory是JMS管理的对象之一，由管理员预先配置**。借助配置的客户端将与JMS提供程序建立连接。

Spring提供了两种类型的ConnectionFactory：

-   **SingleConnectionFactory**：是ConnectionFactory接口的实现，它将在所有createConnection()调用上返回相同的连接并忽略对close()的调用
-   **CachingConnectionFactory**：扩展了SingleConnectionFactory的功能，并通过缓存Sessions、MessageProducers和MessageConsumers(消息消费者)来增强它

## 5. 目的地管理

如上所述，与ConnectionFactory一起，目的地也是JMS管理的对象，可以从JNDI存储和检索。

Spring提供了通用解析器，如DynamicDestinationResolver和特定解析器，如JndiDestinationResolver。JmsTemplate将根据我们的选择将目标名称的解析委托给其中一个实现。

它还提供一个名为defaultDestination的属性，该属性将用于不指向特定目的地的发送和接收操作。

## 6. 消息转换

如果没有消息转换器的支持，Spring JMS将是不完整的。

JmsTemplate用于ConvertAndSend()和ReceiveAndConvert()操作的默认转换策略是SimpleMessageConverter类。**SimpleMessageConverter能够处理TextMessages、BytesMessages、MapMessages和ObjectMessages。此类实现MessageConverter接口**。

除了SimpleMessageConverter之外，Spring JMS还提供了一些其他开箱即用的MessageConverter类，如MappingJackson2MessageConverter、MarshallingMessageConverter、MessagingMessageConverter。此外，我们可以简单地通过实现MessageConverter接口的toMessage()和FromMessage()方法来创建自定义消息转换功能。

让我们看一个关于实现自定义MessageConverter的示例代码片段：

```java
public class SampleMessageConverter implements MessageConverter {
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        // ...
    }

    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException { 
        // ...
    }
}
```

## 7. Spring JMS示例

在本节中，我们演示如何使用JmsTemplate发送和接收消息。

发送消息的默认方法是JmsTemplate.send()。它有两个关键参数，第一个参数是JMS目标，第二个参数是MessageCreator的实现。JmsTemplate使用MessageCreator的回调方法createMessage()来构造消息。

**JmsTemplate.send()适用于发送纯文本消息，但为了发送自定义消息，JmsTemplate还有另一个叫做convertAndSend()的方法**。

我们可以在下面看到这些方法的实现：

```java
public class SampleJmsMessageSender {

    private JmsTemplate jmsTemplate;
    private Queue queue;

    // setters for jmsTemplate & queue

    public void simpleSend() {
        jmsTemplate.send(queue, s -> s.createTextMessage("hello queue world"));
    }

    public void sendMessage(Employee employee) {
        System.out.println("Jms Message Sender : " + employee);
        Map<String, Object> map = new HashMap<>();
        map.put("name", employee.getName());
        map.put("age", employee.getAge());
        jmsTemplate.convertAndSend(map);
    }
}
```

下面是消息接收器类，我们称之为消息驱动的POJO(MDP)。**我们可以看到SampleListener类实现了MessageListener接口，并为接口方法onMessage()提供特定于文本的实现**。

除了onMessage()方法之外，我们的SampleListener类还调用了一个方法receiveAndConvert()来接收自定义消息：

```java
public class SampleListener implements MessageListener {

    public JmsTemplate getJmsTemplate() {
        return getJmsTemplate();
    }

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String msg = ((TextMessage) message).getText();
                System.out.println("Message has been consumed : " + msg);
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Message Error");
        }
    }

    public Employee receiveMessage() throws JMSException {
        Map map = (Map) getJmsTemplate().receiveAndConvert();
        return new Employee((String) map.get("name"), (Integer) map.get("age"));
    }
}
```

下面是在Spring应用程序上下文中的配置：

```xml
<bean id="messageListener" class="cn.tuyucheng.taketoday.spring.jms.SampleListener" /> 

<bean id="jmsContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer"> 
    <property name="connectionFactory" ref="connectionFactory"/> 
    <property name="destinationName" ref="IN_QUEUE"/> 
    <property name="messageListener" ref="messageListener" /> 
</bean>
```

**DefaultMessageListenerContainer是Spring提供的默认消息监听器容器以及许多其他专用容器**。

## 8. Java注解的基本配置

**@JmsListener是将普通bean的方法转换为JMS监听器端点所需的唯一注解**，Spring JMS提供了更多的注解来简化JMS实现。

例如：

```java
@JmsListener(destination = "myDestination")
public void SampleJmsListenerMethod(Message<Order> order) { ... }
```

为了向单个方法添加多个监听器，我们只需要添加多个@JmsListener注解。

**我们需要将@EnableJms注解添加到我们的一个配置类上，以支持@JmsListener注解方法**：

```java
@Configuration
@EnableJms
public class AppConfig {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }
}
```

## 9. 错误处理程序

我们还可以为我们的消息监听器容器配置自定义错误处理程序，首先我们实现org.springframework.util.ErrorHandler接口：

```java
@Service
public class SampleJmsErrorHandler implements ErrorHandler {

    // ... logger

    @Override
    public void handleError(Throwable t) {
        LOG.warn("In default JMS error handler...");
        LOG.error("Error Message : {}", t.getMessage());
    }
}
```

请注意，我们已经重写了handleError()方法，它只是记录错误消息。然后，我们需要使用setErrorHandler()方法在DefaultJmsListenerConnectionFactory中引用我们的错误处理程序服务：

```java
@Bean
public DefaultJmsListenerContainerFactorybjmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setErrorHandler(sampleJmsErrorHandler);
    return factory;
}
```

**这样，我们配置的错误处理程序现在将捕获任何未经处理的异常并记录消息**。 

或者，我们还可以通过更新我们的appContext.xml使用普通的旧XML配置来配置错误处理程序：

```xml
<bean id="sampleJmsErrorHandler" class="cn.tuyucheng.taketoday.spring.jms.SampleJmsErrorHandler" />

<bean id="jmsContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
    <property name="connectionFactory" ref="connectionFactory" />
    <property name="destinationName" value="IN_QUEUE" />
    <property name="messageListener" ref="messageListener" />
    <property name="errorHandler" ref="sampleJmsErrorHandler" />
</bean>
```

## 10. 总结

在本教程中，我们介绍了Spring JMS的配置和基本概念，并简要了解了用于发送和接收消息的特定于Spring的JmsTemplate类。
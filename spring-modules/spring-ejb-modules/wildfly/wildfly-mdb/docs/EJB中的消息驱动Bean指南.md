## 1. 概述

简而言之，Enterprise JavaBean (EJB) 是运行在应用程序服务器上的 JEE 组件。

在本教程中，我们将讨论消息驱动的 Bean (MDB)，它负责处理异步上下文中的消息处理。

自 EJB 2.0 规范以来，MDB 是 JEE 的一部分；EJB 3.0 引入了注解的使用，使得创建这些对象变得更加容易。在这里，我们将重点关注注解。

## 2. 一些背景

在深入了解消息驱动 Bean 的详细信息之前，让我们回顾一下与消息传递相关的一些概念。

### 2.1. 讯息

消息传递是一种通信机制。通过使用消息传递，程序可以交换数据，即使它们是用不同的程序语言编写的或位于不同的操作系统中。

它提供了一个松耦合的解决方案；信息的生产者或消费者都不需要了解彼此的详细信息。

因此，它们甚至不必同时连接到消息系统(异步通信)。

### 2.2. 同步和异步通信

在同步通信期间，请求者等待响应返回。同时，请求者进程保持阻塞状态。

另一方面，在异步通信中，请求者发起操作但不会被它阻塞；请求者可以继续执行其他任务并稍后收到响应。

### 2.3. ETC

Java 消息服务(“JMS”)是一种支持消息传递的 Java API。

JMS 提供点对点和发布/订阅消息模型。

## 3.消息驱动的Bean

MDB 是每次消息到达消息传递系统时由容器调用的组件。结果，这个事件触发了这个 bean 中的代码。

我们可以在 MDB onMessage()方法中执行很多任务，因为在浏览器上显示接收到的数据或解析并将其保存到数据库中。

另一个例子是经过一些处理后将数据提交到另一个队列。这一切都取决于我们的业务规则。

### 3.1. 消息驱动的 Bean 生命周期

MDB 只有两种状态：

1.  它不存在于容器中
2.  创建并准备接收消息

依赖项(如果存在)会在创建 MDB 后立即注入。

要在接收消息之前执行指令，我们需要用@javax.ejb 注解一个方法。后构造。

依赖注入和@javax.ejb。PostConstruct执行只发生一次。

之后，MDB 就可以接收消息了。

### 3.2. 交易

可以将消息传递到事务上下文中的 MDB。

这意味着onMessage()方法中的所有操作都是单个事务的一部分。

因此，如果发生回滚，消息系统会重新传递数据。

## 4. 使用消息驱动 Bean

### 4.1. 创建消费者

要创建消息驱动 Bean，我们在类名声明之前使用@javax.ejb.MessageDriven注解。

要处理传入消息，我们必须实现MessageListener接口的onMessage()方法：

```java
@MessageDriven(activationConfig = { 
    @ActivationConfigProperty(
      propertyName = "destination", 
      propertyValue = "tutorialQueue"), 
    @ActivationConfigProperty(
      propertyName = "destinationType", 
      propertyValue = "javax.jms.Queue")
})
public class ReadMessageMDB implements MessageListener {

    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("Message received: " + textMessage.getText());
        } catch (JMSException e) {
            System.out.println(
              "Error while trying to consume messages: " + e.getMessage());
        }
    }
}
```

由于本文关注注解而不是 .xml 描述符，我们将使用@ActivationConfigProperty而不是 <activation-config-property> 。

@ActivationConfigProperty是表示该配置的键值属性。我们将在activationConfig中使用两个属性，设置队列和 MDB 将使用的对象类型。

在onMessage()方法中，我们可以将消息参数转换为TextMessage、BytesMessage、MapMessage StreamMessage或ObjectMessage。

但是，对于本文，我们只会查看标准输出上的消息内容。

### 4.2. 创建生产者

正如 2.1 节所述，生产者和消费者服务是完全独立的，甚至可以用不同的编程语言编写！

我们将使用 Java Servlet 生成消息：

```java
@Override
protected void doGet(
  HttpServletRequest req, 
  HttpServletResponse res) 
  throws ServletException, IOException {
 
    String text = req.getParameter("text") != null ? req.getParameter("text") : "Hello World";

    try (
        Context ic = new InitialContext();
 
        ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
        Queue queue = (Queue) ic.lookup("queue/tutorialQueue");
 
        Connection connection = cf.createConnection();
    ) {
        Session session = connection.createSession(
          false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer publisher = session
          .createProducer(queue);
 
        connection.start();

        TextMessage message = session.createTextMessage(text);
        publisher.send(message);
 
    } catch (NamingException | JMSException e) {
        res.getWriter()
          .println("Error while trying to send <" + text + "> message: " + e.getMessage());
    } 

    res.getWriter()
      .println("Message sent: " + text);
}
```

获得ConnectionFactory和Queue实例后，我们必须创建一个Connection和Session。

要创建会话，我们调用createSession方法。

createSession中的第一个参数是一个布尔值，它定义会话是否是事务的一部分。

第二个参数仅在第一个参数为false时使用。它允许我们描述适用于传入消息的确认方法，并采用Session.AUTO_ACKNOWLEDGE、Session.CLIENT_ACKNOWLEDGE和Session.DUPS_OK_ACKNOWLEDGE的值。

我们现在可以启动连接，在会话对象上创建文本消息并发送我们的消息。

绑定到同一队列的消费者将接收消息并执行其异步任务。

此外，除了查找JNDI对象之外，我们的 try-with-resources 块中的所有操作都会确保在JMSException遇到错误时关闭连接，例如尝试连接到不存在的队列或指定错误的端口号进行连接.

## 5. 测试消息驱动 Bean

通过SendMessageServlet上的GET方法发送消息，如下所示：

http://127.0.0.1:8080/producer/SendMessageServlet?text=要发送的文本

此外，如果我们不发送任何参数，servlet 会将“Hello World”发送到队列，如http://127.0.0.1:8080/producer/SendMessageServlet。

## 六，总结

消息驱动 Bean 允许简单地创建基于队列的应用程序。

因此，MDB 允许我们将应用程序解耦为具有本地化职责的较小服务，从而允许更加模块化和增量的系统，可以从系统故障中恢复。
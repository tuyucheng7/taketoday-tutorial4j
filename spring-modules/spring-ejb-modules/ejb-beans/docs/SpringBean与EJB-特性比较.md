## 1. 概述

多年来，Java 生态系统已经发展壮大。在此期间，Enterprise Java Beans 和 Spring 是两种既相互竞争又相互学习共生的技术。

在本教程中，我们将了解它们的历史和差异。当然，我们会在 Spring 世界中看到一些 EJB 代码示例和它们的等价物。

## 2. 技术简史

首先，让我们快速浏览一下这两种技术的历史以及它们多年来是如何稳步发展的。

### 2.1. 企业 Java Bean

EJB 规范是 Java EE(或[J2EE，现在称为 Jakarta EE](https://www.baeldung.com/java-enterprise-evolution))规范的子集。它的第一个版本于 1999 年问世，是旨在简化 Java 服务器端企业应用程序开发的首批技术之一。

它肩负着 Java 开发人员在并发性、安全性、持久性、事务处理等方面的重担。该规范将这些和其他常见的企业问题移交给了实施应用程序服务器的容器，这些容器可以无缝地处理它们。然而，由于需要大量的配置，按原样使用 EJB 有点麻烦。此外，它被证明是一个性能瓶颈。

但是现在，随着注解的发明以及来自 Spring 的激烈竞争，最新 3.2 版本中的 EJB 比最初的版本更易于使用。今天的 Enterprise Java Bean 大量借鉴了 Spring 的依赖注入和 POJO 的使用。

### 2.2. 春天

当 EJB(和一般的 Java EE)努力满足 Java 社区时，Spring Framework 的出现就像一股清新的空气。它的第一个里程碑版本于 2004 年发布，提供了 EJB 模型及其重量级容器的替代方案。

感谢 Spring，Java 企业应用程序现在可以在更轻量级的[IOC 容器](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring#the-spring-ioc-container)上运行。此外，它还提供依赖倒置、AOP 和 Hibernate 支持以及无数其他有用的功能。在 Java 社区的大力支持下，Spring 现在呈指数级增长，可以称为完整的 Java/JEE 应用程序框架。

在其最新的化身中，Spring 5.0 甚至支持响应式编程模型。另一个分支[Spring Boot](https://www.baeldung.com/spring-boot-start)凭借其嵌入式服务器和自动配置彻底改变了游戏规则。

## 3. 特征对比前奏

在跳转到与代码示例进行功能比较之前，让我们建立一些基础知识。

### 3.1. 两者的基本区别

首先，根本的和明显的区别是EJB 是一个规范，而 Spring 是一个完整的框架。

该规范由许多应用程序服务器实现，例如 GlassFish、IBM WebSphere 和 JBoss/WildFly。这意味着我们选择使用 EJB 模型进行应用程序的后端开发是不够的。我们还需要选择要使用的应用程序服务器。

从理论上讲，Enterprise Java Beans 可以跨应用程序服务器移植，但如果要保留互操作性作为一个选项，那么我们不应该使用任何特定于供应商的扩展总是有一个先决条件。

其次，就其广泛的产品组合而言，Spring 作为技术比 EJB 更接近 Java EE。虽然 EJB 仅指定后端操作，但与 Java EE 一样，Spring 也支持 UI 开发、RESTful API 和响应式编程等等。

### 3.2. 有用的信息

在接下来的部分中，我们将通过一些实际示例来比较这两种技术。由于 EJB 特性是更大的 Spring 生态系统的一个子集，我们将按照它们的类型来查看它们对应的 Spring 等价物。

为了更好地理解这些示例，请考虑先阅读[Java EE 会话 Bean](https://www.baeldung.com/ejb-session-beans)、[消息驱动 Bean](https://www.baeldung.com/ejb-message-driven-beans)、[Spring Bean](https://www.baeldung.com/spring-bean)和[Spring Bean 注解](https://www.baeldung.com/spring-bean-annotations)。

我们将使用[OpenJB](https://www.baeldung.com/java-ee-singleton-session-bean#maven)作为我们的嵌入式容器来运行 EJB 示例。对于运行大多数 Spring 示例，它的 IOC 容器就足够了；对于 Spring JMS，我们需要一个嵌入式 ApacheMQ 代理。

为了测试我们所有的示例，我们将使用 JUnit。

## 4.单例EJB == Spring组件

有时我们需要容器只创建一个 bean 实例。例如，假设我们需要一个 bean 来计算 Web 应用程序的访问者数量。这个 bean 只需要在应用程序启动期间创建一次。

让我们看看如何使用[Singleton Session EJB](https://www.baeldung.com/java-ee-singleton-session-bean)和[Spring Component](https://www.baeldung.com/spring-bean-annotations#component)来实现这一点。

### 4.1. 单例 EJB 示例

我们首先需要一个接口来指定我们的 EJB 具有远程处理的能力：

```java
@Remote
public interface CounterEJBRemote {    
    int count();
    String getName();
    void setName(String name);
}
```

下一步是用注解javax.ejb.Singleton定义一个实现类，中提琴！我们的单身人士准备好了：

```java
@Singleton
public class CounterEJB implements CounterEJBRemote {
    private int count = 1;
    private String name;

    public int count() {
        return count++;
    }
    
    // getter and setter for name
}

```

但在我们可以测试单例(或任何其他 EJB 代码示例)之前，我们需要初始化ejbContainer并获取上下文：

```java
@BeforeClass
public void initializeContext() throws NamingException {
    ejbContainer = EJBContainer.createEJBContainer();
    context = ejbContainer.getContext();
    context.bind("inject", this);
}

```

现在让我们看一下测试：

```java
@Test
public void givenSingletonBean_whenCounterInvoked_thenCountIsIncremented() throws NamingException {

    int count = 0;
    CounterEJBRemote firstCounter = (CounterEJBRemote) context.lookup("java:global/ejb-beans/CounterEJB");
    firstCounter.setName("first");
        
    for (int i = 0; i < 10; i++) {
        count = firstCounter.count();
    }
        
    assertEquals(10, count);
    assertEquals("first", firstCounter.getName());

    CounterEJBRemote secondCounter = (CounterEJBRemote) context.lookup("java:global/ejb-beans/CounterEJB");

    int count2 = 0;
    for (int i = 0; i < 10; i++) {
        count2 = secondCounter.count();
    }

    assertEquals(20, count2);
    assertEquals("first", secondCounter.getName());
}

```

上例中需要注意的几点：

-   我们正在使用[JNDI](https://www.baeldung.com/jndi)查找从容器中获取counterEJB
-   count2从离开单例的点计数中提取，加起来为20
-   secondCounter保留我们为firstCounter设置的名称

最后两点证明了单例的重要性。由于每次查找时都使用同一个 bean 实例，因此总计数为 20，并且为一个实例设置的值对另一个实例保持不变。

### 4.2. 单例 Spring Bean 示例

使用 Spring 组件可以获得相同的功能。

我们不需要在这里实现任何接口。相反，我们将添加@Component注解：

```java
@Component
public class CounterBean {
    // same content as in the EJB
}
```

事实上，组件在 Spring 中默认是单例的。

我们还需要[配置 Spring 来扫描组件](https://www.baeldung.com/spring-component-scanning#1-using-componentscan-in-aspring-application)：

```java
@Configuration
@ComponentScan(basePackages = "com.baeldung.ejbspringcomparison.spring")
public class ApplicationConfig {}

```

与我们初始化 EJB 上下文的方式类似，我们现在将设置 Spring 上下文：

```java
@BeforeClass
public static void init() {
    context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
}

```

现在让我们看看我们的组件在运行：

```java
@Test
public void whenCounterInvoked_thenCountIsIncremented() throws NamingException {    
    CounterBean firstCounter = context.getBean(CounterBean.class);
    firstCounter.setName("first");
    int count = 0;
    for (int i = 0; i < 10; i++) {
        count = firstCounter.count();
    }

    assertEquals(10, count);
    assertEquals("first", firstCounter.getName());

    CounterBean secondCounter = context.getBean(CounterBean.class);
    int count2 = 0;
    for (int i = 0; i < 10; i++) {
        count2 = secondCounter.count();
    }

    assertEquals(20, count2);
    assertEquals("first", secondCounter.getName());
}

```

如我们所见，与 EJB 的唯一区别是我们如何从 Spring 容器的上下文中获取 bean，而不是 JNDI 查找。

## 5. Stateful EJB == Spring Component with prototype Scope

有时，比如说当我们构建一个购物车时，我们需要我们的 bean 在方法调用之间来回移动时记住它的状态。

在这种情况下，我们需要我们的容器为每次调用生成一个单独的 bean 并保存状态。让我们看看如何使用我们的相关技术来实现这一目标。

### 5.1. 有状态 EJB 示例

与我们的单例 EJB 示例类似，我们需要一个javax.ejb.Remote接口及其实现。只有这一次，它用javax.ejb.Stateful注解：

```java
@Stateful
public class ShoppingCartEJB implements ShoppingCartEJBRemote {
    private String name;
    private List<String> shoppingCart;

    public void addItem(String item) {
        shoppingCart.add(item);
    }
    // constructor, getters and setters
}
```

让我们编写一个简单的测试来设置名称并将项目添加到bathingCart。我们将检查其大小并验证名称：

```java
@Test
public void givenStatefulBean_whenBathingCartWithThreeItemsAdded_thenItemsSizeIsThree()
  throws NamingException {
    ShoppingCartEJBRemote bathingCart = (ShoppingCartEJBRemote) context.lookup(
      "java:global/ejb-beans/ShoppingCartEJB");

    bathingCart.setName("bathingCart");
    bathingCart.addItem("soap");
    bathingCart.addItem("shampoo");
    bathingCart.addItem("oil");

    assertEquals(3, bathingCart.getItems().size());
    assertEquals("bathingCart", bathingCart.getName());
}

```

现在，为了证明该 bean 确实跨实例维护状态，让我们将另一个 shoppingCartEJB 添加到该测试：

```java
ShoppingCartEJBRemote fruitCart = 
  (ShoppingCartEJBRemote) context.lookup("java:global/ejb-beans/ShoppingCartEJB");

fruitCart.addItem("apples");
fruitCart.addItem("oranges");

assertEquals(2, fruitCart.getItems().size());
assertNull(fruitCart.getName());

```

这里我们没有设置名称，因此它的值为空。回想一下单例测试，在一个实例中设置的名称在另一个实例中保留。这表明我们从具有不同实例状态的 bean 池中获得了单独的ShoppingCartEJB实例。

### 5.2. 有状态的 Spring Bean 示例

为了使用 Spring 获得相同的效果，我们需要一个具有[原型作用域的](https://www.baeldung.com/spring-bean-scopes#prototype)组件：

```java
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShoppingCartBean {
   // same contents as in the EJB
}

```

就是这样，只是注解不同——其余代码保持不变。

要测试我们的有状态 bean，我们可以使用与针对 EJB 描述的相同的测试。唯一的区别还是我们如何从容器中获取 bean：

```java
ShoppingCartBean bathingCart = context.getBean(ShoppingCartBean.class);

```

## 6. 无状态 EJB != Spring 中的任何东西

有时，例如在搜索 API 中，我们既不关心 bean 的实例状态，也不关心它是否是单例。我们只需要我们的搜索结果，这些结果可能来自我们所关心的任何 bean 实例。

### 6.1. 无状态 EJB 示例

对于这种情况，EJB 有一个无状态变体。容器维护一个 beans 实例池，其中任何一个都返回给调用方法。

我们定义它的方式和其他EJB类型一样，有远程接口，有javax.ejb.Stateless注解实现：

```java
@Stateless
public class FinderEJB implements FinderEJBRemote {

    private Map<String, String> alphabet;

    public FinderEJB() {
        alphabet = new HashMap<String, String>();
        alphabet.put("A", "Apple");
        // add more values in map here
    }

    public String search(String keyword) {
        return alphabet.get(keyword);
    }
}

```

让我们添加另一个简单的测试来查看实际效果：

```java
@Test
public void givenStatelessBean_whenSearchForA_thenApple() throws NamingException {
    assertEquals("Apple", alphabetFinder.search("A"));        
}

```

在上面的示例中，alphabetFinder使用注解javax.ejb.EJB作为字段注入到测试类中：

```java
@EJB
private FinderEJBRemote alphabetFinder;

```

无状态 EJB 背后的中心思想是通过拥有类似 bean 的实例池来提高性能。

然而，Spring 并不认同这种理念，它只提供无状态的单例。

## 7. 消息驱动的 Bean == Spring JMS

到目前为止讨论的所有 EJB 都是会话 bean。另一种是消息驱动的。顾名思义，它们通常用于两个系统之间的异步通信。

### 7.1. 多数据库实例

要创建消息驱动的 Enterprise Java Bean，我们需要实现定义其onMessage方法的javax.jms.MessageListener接口，并将类注解为javax.ejb.MessageDriven：

```java
@MessageDriven(activationConfig = { 
  @ActivationConfigProperty(propertyName = "destination", propertyValue = "myQueue"), 
  @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
})
public class RecieverMDB implements MessageListener {

    @Resource
    private ConnectionFactory connectionFactory;

    @Resource(name = "ackQueue")
    private Queue ackQueue;

    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String producerPing = textMessage.getText();

            if (producerPing.equals("marco")) {
                acknowledge("polo");
            }
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }
}

```

请注意，我们还为 MDB 提供了一些配置：

-   -   -   destinationType作为队列
        -   myQueue作为我们的 bean 正在侦听的目标队列名称

在这个例子中，我们的接收者也产生了一个确认，从这个意义上说，它本身就是一个发送者。它向另一个名为ackQueue的队列发送一条消息。

现在让我们通过测试看看它的实际效果：

```java
@Test
public void givenMDB_whenMessageSent_thenAcknowledgementReceived()
  throws InterruptedException, JMSException, NamingException {
    Connection connection = connectionFactory.createConnection();
    connection.start();
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    MessageProducer producer = session.createProducer(myQueue);
    producer.send(session.createTextMessage("marco"));
    MessageConsumer response = session.createConsumer(ackQueue);

    assertEquals("polo", ((TextMessage) response.receive(1000)).getText());
}

```

在这里，我们向myQueue发送了一条消息，该消息由我们的@MessageDriven注解的 POJO接收。这个 POJO 然后发送了一个确认，我们的测试收到了作为MessageConsumer的响应。

### 7.2. Spring JMS 示例

那么，现在是时候使用 Spring 做同样的事情了！

首先，我们需要为此添加一些配置。我们需要用@EnableJms注解之前的ApplicationConfig类，并添加一些 bean 来设置JmsListenerContainerFactory和JmsTemplate：

```java
@EnableJms
public class ApplicationConfig {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate(connectionFactory());
        template.setConnectionFactory(connectionFactory());
        return template;
    }
}


```

接下来，我们需要一个生产者——一个简单的 Spring组件——它将向myQueue发送消息并从ackQueue接收确认：

```java
@Component
public class Producer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessageToDefaultDestination(final String message) {
        jmsTemplate.convertAndSend("myQueue", message);
    }

    public String receiveAck() {
        return (String) jmsTemplate.receiveAndConvert("ackQueue");
    }
}

```

然后，我们有一个Receiver Component，它带有一个注解为@JmsListener的方法，用于从myQueue异步接收消息：

```java
@Component
public class Receiver {
    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "myQueue")
    public void receiveMessage(String msg) {
        sendAck();
    }

    private void sendAck() {
        jmsTemplate.convertAndSend("ackQueue", "polo");
    }
}

```

它还充当在ackQueue确认消息接收的发送方。

按照我们的惯例，让我们通过测试来验证这一点：

```java
@Test
public void givenJMSBean_whenMessageSent_thenAcknowledgementReceived() throws NamingException {
    Producer producer = context.getBean(Producer.class);
    producer.sendMessageToDefaultDestination("marco");

    assertEquals("polo", producer.receiveAck());
}

```

在此测试中，我们将marco发送到myQueue并从ackQueue收到polo作为确认，这与我们对 EJB 所做的相同。

这里需要注意的一件事是Spring JMS 可以同步和异步地发送/接收消息。

## 八、总结

在本教程中，我们看到了Spring 和 Enterprise Java Beans 的一对一比较。我们了解他们的历史和基本差异。

然后我们用简单的例子来演示Spring Beans和EJBs的比较。不用说，这只是触及技术能力的皮毛，还有更多的东西有待进一步探索。

此外，这些可能是相互竞争的技术，但这并不意味着它们不能共存。我们可以轻松地[将 EJB 集成到 Spring 框架](https://www.baeldung.com/spring-ejb)中。
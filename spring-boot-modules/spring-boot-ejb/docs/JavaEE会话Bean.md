## 1. 概述

企业会话Bean大致可分为：

1.  无状态会话Bean
2.  有状态会话Bean

在这篇简短的文章中，我们将讨论这两种主要类型的会话bean。

## 2. 设置

要使用Enterprise Beans 3.2，请确保将最新版本添加到pom.xml文件的dependencies部分：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
```

最新的依赖可以在[Maven Repository](https://central.sonatype.com/artifact/javax/javaee-api/8.0.1)中找到。此依赖确保所有Java EE 7 API在编译期间都可用。provided范围确保一旦部署；依赖项将由部署它的容器提供。

## 3. 无状态Bean

无状态会话bean是一种企业bean，通常用于执行独立操作。它没有任何关联的客户端状态，但它可以保留其实例状态。

让我们看一个示例来演示无状态bean的工作原理。

### 3.1 创建无状态Bean

首先，让我们创建无状态EJB bean。我们使用@Stateless注解将bean标记为无状态：

```java
@Stateless
public class StatelessEJB {

    public String name;
}
```

然后我们创建上述无状态bean的第一个客户端，称为EJBClient1：

```java
public class EJBClient1 {

    @EJB
    public StatelessEJB statelessEJB;
}
```

然后我们声明另一个客户端，名为EJBClient2，它访问相同的无状态bean：

```java
public class EJBClient2 {

    @EJB
    public StatelessEJB statelessEJB;
}
```

### 3.2 测试无状态Bean

为了测试EJB的无状态性，我们可以按以下方式使用上面声明的两个客户端：

```java
@RunWith(Arquillian.class)
public class StatelessEJBTest {

    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatelessBean_whenStateIsSetInOneBean_secondBeanShouldHaveSameState() {
        ejbClient1.statelessEJB.name = "Client 1";
        assertEquals("Client 1", ejbClient1.statelessEJB.name);
        assertEquals("Client 1", ejbClient2.statelessEJB.name);
    }

    @Test
    public void givenOneStatelessBean_whenStateIsSetInBothBeans_secondBeanShouldHaveSecondBeanState() {
        ejbClient1.statelessEJB.name = "Client 1";
        ejbClient2.statelessEJB.name = "Client 2";
        assertEquals("Client 2", ejbClient2.statelessEJB.name);
    }

    // Arquillian setup code removed for brevity
}
```

我们首先将两个EJB客户端注入到单元测试中。

然后，在第一个测试方法中，我们将注入到EJBClient1中的EJB中的name变量设置为值Client 1。现在，当我们比较两个客户端中的name变量的值时，我们应该看到该值是相等的。**这表明状态在无状态bean中不被保留**。

让我们以不同的方式证明这是正确的。在第二个测试方法中，我们看到，一旦我们在第二个客户端中设置了name变量，它就会“覆盖”通过ejbClient1赋予它的任何值。

## 4. 有状态Bean

有状态会话bean在事务内部和事务之间维护状态。这就是为什么每个有状态会话bean都与特定客户端相关联的原因。容器可以在管理有状态会话bean的实例池时自动保存和检索bean的状态。

### 4.1 创建有状态Bean

有状态会话bean用@Stateful注解进行标记。有状态bean的代码如下：

```java
@Stateful
public class StatefulEJB {

    public String name;
}
```

我们有状态bean的第一个本地客户端编写如下：

```java
public class EJBClient1 {

    @EJB
    public StatefulEJB statefulEJB;
}
```

与EJBClient1一样，也创建了名为EJBClient2的第二个客户端：

```java
public class EJBClient2 {

    @EJB
    public StatefulEJB statefulEJB;
}
```

### 4.2 测试有状态Bean

有状态bean的功能在EJBStatefulBeanTest单元测试中按以下方式进行测试：

```java
@RunWith(Arquillian.class)
public class StatefulEJBTest {

    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatefulBean_whenTwoClientsSetValueOnBean_thenClientStateIsMaintained() {
        ejbClient1.statefulEJB.name = "Client 1";
        ejbClient2.statefulEJB.name = "Client 2";
        assertNotEquals(ejbClient1.statefulEJB.name, ejbClient2.statefulEJB.name);
        assertEquals("Client 1", ejbClient1.statefulEJB.name);
        assertEquals("Client 2", ejbClient2.statefulEJB.name);
    }

    // Arquillian setup code removed for brevity
}
```

与之前一样，将两个EJB客户端注入到单元测试中。在测试方法中，我们可以看到name变量的值是通过ejbClient1客户端设置的，并且即使通过ejbClient2设置的name值不同，它也会保持不变。**这表明EJB的状态得到了维护**。

## 5. 无状态会话Bean与有状态会话Bean

现在让我们看一下这两种会话bean之间的主要区别。

### 5.1 无状态Bean

-   无状态会话bean不与客户端保持任何状态。因此，它们可用于创建与多个客户端交互的对象池
-   由于无状态bean没有每个客户端的任何状态，因此它们在性能方面更好
-   它们可以并行处理来自多个客户端的多个请求，并且
-   可用于从数据库中检索对象

### 5.2 有状态Bean

-   有状态会话bean可以维护与多个客户端的状态，任务不在客户端之间共享
-   该状态在会话期间持续。会话销毁后，状态不保留
-   容器可以将状态序列化并存储为过时状态以供将来使用。这样做是为了节省应用程序服务器资源并支持bean故障，并且是钝化
-   可用于解决生产者-消费者类型的问题

## 6. 总结

因此，我们创建了两种类型的会话bean和相应的客户端来调用bean中的方法。该项目演示了两种主要类型的会话bean的行为。
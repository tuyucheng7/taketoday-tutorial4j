## 1. 概述

企业会话 Bean 大致可分为：

1.  无状态会话 Bean
2.  有状态会话 Bean

在这篇简短的文章中，我们将讨论这两种主要类型的会话 bean。

## 2.设置

要使用 Enterprise Beans 3.2 ，请确保将最新版本添加到pom.xml文件的依赖项部分：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
```

最新的依赖可以在[Maven Repository](https://search.maven.org/classic/#search|gav|1|g%3A"javax" AND a%3A"javaee-api")中找到。这种依赖性确保所有 Java EE 7 API 在编译期间都可用。提供的范围确保一旦部署；依赖项将由部署它的容器提供。

## 3.无状态Bean

无状态会话 bean 是一种企业 bean，通常用于执行独立操作。它没有任何关联的客户端状态，但它可以保留其实例状态。

让我们看一个示例来演示无状态 bean 是如何工作的。

### 3.1. 创建无状态 Bean

首先，让我们创建StatelessEJB bean。我们使用@Stateless注解将 bean 标记为无状态：

```java
@Stateless
public class StatelessEJB {

    public String name;

}
```

然后我们创建上述无状态 bean 的第一个客户端，称为EJBClient1：

```java
public class EJBClient1 {

    @EJB
    public StatelessEJB statelessEJB;

}
```

然后我们声明另一个客户端，名为EJBClient2，它访问相同的无状态 bean：

```java
public class EJBClient2 {

    @EJB
    public StatelessEJB statelessEJB;

}
```

### 3.2. 测试无状态 Bean

为了测试 EJB 的无状态性，我们可以按以下方式使用上面声明的两个客户端：

```java
@RunWith(Arquillian.class)
public class StatelessEJBTest {

    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatelessBean_whenStateIsSetInOneBean
      _secondBeanShouldHaveSameState() {

        ejbClient1.statelessEJB.name = "Client 1";
        assertEquals("Client 1", ejbClient1.statelessEJB.name);
        assertEquals("Client 1", ejbClient2.statelessEJB.name);
    }

    @Test
    public void givenOneStatelessBean_whenStateIsSetInBothBeans
      _secondBeanShouldHaveSecondBeanState() {

        ejbClient1.statelessEJB.name = "Client 1";
        ejbClient2.statelessEJB.name = "Client 2";
        assertEquals("Client 2", ejbClient2.statelessEJB.name);
    }

    // Arquillian setup code removed for brevity

}
```

我们首先将两个 EBJ 客户端注入到单元测试中。

然后，在第一个测试方法中，我们将注入到EJBClient1中的 EJB 中的name变量设置为值Client 1。现在，当我们比较两个客户端中的name变量的值时，我们应该看到该值是相等的. 这表明状态在无状态 bean 中不被保留。

让我们以不同的方式证明这是真的。在第二种测试方法中，我们看到，一旦我们在第二个客户端中设置了name变量，它就会“覆盖”通过ejbClient1赋予它的任何值。

## 4.有状态的Bean

有状态会话 bean 在事务内部和事务之间维护状态。这就是为什么每个有状态会话 bean 都与特定客户端相关联的原因。容器可以在管理有状态会话 bean 的实例池时自动保存和检索 bean 的状态。

### 4.1. 创建有状态 Bean

有状态会话 bean 标有@Stateful注解。有状态bean的代码如下：

```java
@Stateful
public class StatefulEJB {

    public String name;

}
```

我们有状态 bean 的第一个本地客户端编写如下：

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

### 4.2. 测试有状态 Bean

有状态 bean 的功能在EJBStatefulBeanTest单元测试中按以下方式进行测试：

```java
@RunWith(Arquillian.class)
public class StatefulEJBTest {

    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatefulBean_whenTwoClientsSetValueOnBean
      _thenClientStateIsMaintained() {

        ejbClient1.statefulEJB.name = "Client 1";
        ejbClient2.statefulEJB.name = "Client 2";
        assertNotEquals(ejbClient1.statefulEJB.name, ejbClient2.statefulEJB.name);
        assertEquals("Client 1", ejbClient1.statefulEJB.name);
        assertEquals("Client 2", ejbClient2.statefulEJB.name);
    }

    // Arquillian setup code removed for brevity

}
```

与之前一样，将两个 EJB 客户端注入到单元测试中。在测试方法中，我们可以看到name变量的值是通过ejbClient1客户端设置的，并且即使通过ejbClient2设置的name值不同，它也会保持不变。这表明 EJB 的状态得到了维护。

## 5. 无状态会话 Bean 与有状态会话 Bean

现在让我们看一下这两种会话 bean 之间的主要区别。

### 5.1. 无状态 Bean

-   无状态会话 bean 不与客户端保持任何状态。因此，它们可用于创建与多个客户端交互的对象池
-   由于无状态 bean 没有每个客户端的任何状态，因此它们在性能方面更好
-   他们可以并行处理来自多个客户端的多个请求，并且
-   可用于从数据库中检索对象

### 5.2. 有状态的 Bean

-   有状态会话 bean 可以与多个客户端保持状态，任务不在客户端之间共享
-   该状态在会话期间持续。session销毁后，状态不保留
-   容器可以将状态序列化并存储为陈旧状态以供将来使用。这样做是为了节省应用程序服务器资源并支持 bean 故障并且是钝化
-   可用于解决生产者-消费者类型的问题

## 六，总结

所以我们创建了两种类型的会话 bean 和相应的客户端来调用 bean 中的方法。该项目演示了两种主要类型的会话 bean 的行为。
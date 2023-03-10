## 1. 概述

使用ORM时，数据获取/加载可以分为两种类型：急切和惰性。

在这个快速教程中，我们将指出它们的差异并展示我们如何在Hibernate中使用它们。

## 2. Maven依赖

为了使用Hibernate，让首先在我们的pom.xml中定义主要依赖：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>   
    <version>5.2.2.Final</version>
</dependency>
```

可以在[此处](https://mvnrepository.com/artifact/org.hibernate/hibernate-core)找到最新版本的Hibernate。

## 3. 急切和延迟加载

我们在这里首先要讨论的是什么是延迟加载和急切加载：

-   **急切加载**是一种设计模式，其中数据初始化发生在现场。
-   **延迟加载**是一种设计模式，我们使用它来尽可能推迟对象的初始化。

让我们看看这是如何工作的。

首先，我们来看看UserLazy类：

```java
@Entity
@Table(name = "USER")
public class UserLazy implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<OrderDetail> orderDetail = new HashSet();

    // standard setters and getters
    // also override equals and hashcode
}
```

接下来，下面是OrderDetail类：

```java
@Entity
@Table (name = "USER_ORDER")
public class OrderDetail implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="ORDER_ID")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private UserLazy user;

    // standard setters and getters
    // also override equals and hashcode
}
```

一个用户可以有多个OrderDetails。**在急切加载策略中，如果我们加载用户数据，它也会加载与其关联的所有订单并将其存储在内存中**。

但是当我们启用延迟加载时，如果我们获取UserLazy，OrderDetail数据将不会被初始化并加载到内存中，直到我们对其进行显式调用。

在下一节中，我们将看到如何在Hibernate中实现该示例。

## 4. 加载配置

让我们看看如何在Hibernate中配置获取策略。

我们可以使用这个注解参数启用延迟加载：

```java
fetch = FetchType.LAZY
```

对于急切加载，我们使用这个参数：

```java
fetch = FetchType.EAGER
```

为了设置急切加载，我们使用了UserLazy的孪生类UserEager。

在下一节中，我们将研究两种获取类型之间的区别。

## 5. 差异

正如我们所提到的，两种类型的获取之间的主要区别在于数据加载到内存中的时间：

```java
List<UserLazy> users = sessionLazy.createQuery("From UserLazy").list();
UserLazy userLazyLoaded = users.get(3);
return (userLazyLoaded.getOrderDetail());
```

使用延迟初始化方法，orderDetailSet只有在我们使用getter或其他方法显式调用它时才会被初始化：

```java
UserLazy userLazyLoaded = users.get(3);
```

但是使用UserEager中的急切方法，它将在第一行立即初始化：

```java
List<UserEager> user = sessionEager.createQuery("From UserEager").list();
```

对于延迟加载，我们使用代理对象并触发单独的SQL查询来加载orderDetailSet。

禁用代理或延迟加载的想法在Hibernate中被认为是一种不好的做法。这可能会导致获取和存储大量数据，而不管是否需要这些数据。

我们可以使用以下方法来测试功能：

```java
Hibernate.isInitialized(orderDetailSet);
```

现在让我们看看在这两种情况下生成的查询：

```xml
<property name="show_sql">true</property>
```

fetching.hbm.xml中的上述设置用于在控制台显示生成的SQL语句。如果我们查看控制台输出，可以看到生成的查询。

对于延迟加载，下面是为加载用户数据而生成的查询：

```sql
select user0_.USER_ID as USER_ID1_0_,  ... from USER user0_
```

但是，在急切加载中，我们看到了使用USER_ORDER进行的连接：

```sql
select orderdetai0_.USER_ID as USER_ID4_0_0_, orderdetai0_.ORDER_ID as ORDER_ID1_1_0_, orderdetai0_ ... 
    from USER_ORDER orderdetai0_ where orderdetai0_.USER_ID=?
```

上面的查询是为所有Users生成的，这会导致比其他方法更多的内存使用。

## 6. 优缺点

### 6.1 延迟加载

优点：

-   与其他方法相比，初始加载时间短得多
-   与其他方法相比，内存消耗更少

缺点：

-   延迟的初始化可能会在不需要的时刻影响性能
-   在某些情况下，我们需要特别小心地处理延迟初始化的对象，否则我们最终可能会得到异常

### 6.2 急切加载

优点：

-   没有延迟初始化相关的性能影响

缺点：

-   初始加载时间长
-   加载太多不必要的数据可能会影响性能

## 7. Hibernate中的延迟加载

**Hibernate通过提供类的代理实现对实体和关联应用延迟加载方法**。

Hibernate通过用派生自实体类的代理替换它来拦截对实体的调用。在我们的示例中，在将控制权交给User类实现之前，将从数据库加载缺少的请求信息。

我们还应该注意，当关联表示为集合类时(在上面的示例中，它表示为Set<OrderDetail\> orderDetailSet)，将创建一个包装器并替换原始集合。

要了解有关代理设计模式的更多信息，请参阅[此处](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html)。

## 8. 总结

在本文中，我们展示了Hibernate中使用的两种主要类型数据获取的示例。

有关高级专业知识，请查看Hibernate的官方网站。
## 1. 简介

在本教程中，我们将讨论Session接口的几种方法之间的区别： save、persist、update、merge和saveOrUpdate。

这不是对 Hibernate 的介绍，我们应该已经了解配置、对象关系映射和使用实体实例的基础知识。有关 Hibernate 的介绍性文章，请访问我们的[Hibernate 4 with Spring](https://www.baeldung.com/hibernate-4-spring)教程。

## 延伸阅读：

## [使用 Hibernate 删除对象](https://www.baeldung.com/delete-with-hibernate)

在 Hibernate 中删除实体的快速指南。

[阅读更多](https://www.baeldung.com/delete-with-hibernate)→

## [Hibernate 的存储过程](https://www.baeldung.com/stored-procedures-with-hibernate-tutorial)

本文简要讨论了如何从 Hibernate 调用存储过程。

[阅读更多](https://www.baeldung.com/stored-procedures-with-hibernate-tutorial)→

## [Hibernate/JPA 标识符概述](https://www.baeldung.com/hibernate-identifiers)

了解如何使用 Hibernate 映射实体标识符。

[阅读更多](https://www.baeldung.com/hibernate-identifiers)→

## 2. Session作为持久化上下文实现

[Session](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html)接口有几个最终导致将数据保存到数据库的方法：[persist](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html#persist-java.lang.Object-)、[save](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html#save-java.lang.Object-)、[update](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html#update-java.lang.Object-)、[merge](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html#merge-java.lang.Object-)和[saveOrUpdate](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html#saveOrUpdate-java.lang.Object-)。要理解这些方法之间的区别，我们必须首先讨论Session作为持久性上下文的目的，以及与 Session 相关的实体实例状态之间的区别。

我们还应该了解 Hibernate 的发展历史导致了一些部分重复的 API 方法。

### 2.1. 管理实体实例

除了对象关系映射本身，Hibernate 解决的问题之一是在运行时管理实体的问题。“持久性上下文”的概念是 Hibernate 对这个问题的解决方案。我们可以将持久性上下文视为我们在会话期间加载或保存到数据库的所有对象的容器或一级缓存。

会话是一个逻辑事务，其边界由应用程序的业务逻辑定义。当我们通过持久性上下文使用数据库，并且我们所有的实体实例都附加到这个上下文时，我们应该始终为我们在会话期间与之交互的每个数据库记录提供一个实体实例。

在 Hibernate 中，持久性上下文由[org.hibernate.Session](http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html)实例表示。对于 JPA，它是[javax.persistence.EntityManager](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)。当我们使用 Hibernate 作为 JPA 提供者，并通过EntityManager接口进行操作时，这个接口的实现基本上包装了底层的Session对象。但是，Hibernate Session提供了更丰富的接口和更多的可能性，所以有时直接使用Session是有用的。

### 2.2. 实体实例的状态

我们应用程序中的任何实体实例都出现在与会话持久性上下文相关的三个主要状态之一：

-   transient — 此实例未且从未附加到Session。该实例在数据库中没有相应的行；它通常只是我们创建的一个新对象，用于保存到数据库中。
-   persistent——这个实例与一个唯一的Session对象相关联。将Session刷新到数据库后，保证该实体在数据库中具有相应的一致记录。
-   detached — 此实例曾经附加到Session(处于持久状态)，但现在不是。如果我们将实例从上下文中逐出，清除或关闭会话，或者将实例置于序列化/反序列化过程中，实例就会进入此状态。

这是一个简化的状态图，其中包含对使状态转换发生的Session方法的注解：

[![2016-07-11_13-38-11](https://www.baeldung.com/wp-content/uploads/2016/07/2016-07-11_13-38-11-1024x551.png)](https://www.baeldung.com/wp-content/uploads/2016/07/2016-07-11_13-38-11-1024x551.png)

当实体实例处于持久状态时，我们对该实例的映射字段所做的所有更改都将在刷新Session时应用于相应的数据库记录和字段。持久实例 是“在线的”，而分离的实例是“离线的”并且不受更改监视。

这意味着当我们更改持久对象的字段时，我们不必调用save、update或任何这些方法来将这些更改获取到数据库。我们需要做的就是提交事务、刷新会话或关闭会话。

### 2.3. 符合 JPA 规范

Hibernate 是最成功的JavaORM 实现。因此，Hibernate API 严重影响了Java持久性 API (JPA) 的规范。不幸的是，也存在许多差异，有些差异较大，有些差异较小。

为了作为 JPA 标准的实现，必须修改 Hibernate API。为了匹配EntityManager接口，在Session接口中添加了几个方法。这些方法与原方法的目的相同，但符合规范，因此存在一些差异。

## 3.操作的区别

从一开始就了解所有方法(persist、save、update、merge、saveOrUpdate)不会立即导致相应的 SQL UPDATE或INSERT语句，这一点很重要。将数据实际保存到数据库是在提交事务或刷新Session时发生的。

提到的方法基本上通过在生命周期中的不同状态之间转换它们来管理实体实例的状态。

作为示例，我们将使用一个简单的注解映射实体Person：

```java
@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // ... getters and setters

}
```

### 3.1. 坚持

persist方法旨在将新的实体实例添加到持久性上下文中，即将实例从瞬态状态转换为持久状态。

我们通常在要向数据库中添加一条记录(持久化一个实体实例)时调用它：

```java
Person person = new Person();
person.setName("John");
session.persist(person);
```

调用persist方法后会发生什么？person对象已从瞬态状态转变为持久状态。该对象现在位于持久性上下文中，但尚未保存到数据库中。INSERT语句的生成只会在提交事务、刷新或关闭会话时发生。

请注意，persist方法具有void返回类型。它“就地”对传递的对象进行操作，改变其状态。person变量引用实际的持久化对象。

此方法是后来添加到Session接口的。这种方法的主要区别在于它符合 JSR-220 规范(EJB 持久性)。我们在规范中严格定义了这个方法的语义，它基本上声明了一个瞬态实例变为持久化(并且操作级联到它与cascade=PERSIST或cascade=ALL的所有关系)：

-   如果一个实例已经是持久的，那么这个调用对这个特定实例没有影响(但它仍然级联到它与cascade=PERSIST或cascade=ALL的关系)。
-   如果一个实例是分离的，我们会得到一个异常，无论是在调用这个方法时，还是在提交或刷新会话时。

请注意，这里没有任何内容涉及实例的标识符。规范没有说明 id 会立即生成，不管 id 生成策略如何。persist方法的规范允许实现发出语句以在提交或刷新时生成 id。调用此方法后，id 不一定非空，因此我们不应该依赖它。

我们可以在一个已经持久化的实例上调用这个方法，但什么也没有发生。但是如果我们试图持久化一个分离的实例，实现就会抛出异常。在下面的示例中，我们将持久化实体，将其从上下文中逐出，使其变为detached，然后再次尝试持久化。对session.persist()的第二次调用导致异常，因此以下代码将不起作用：

```java
Person person = new Person();
person.setName("John");
session.persist(person);

session.evict(person);

session.persist(person); // PersistenceException!
```

### 3.2. 节省

save方法是不符合 JPA 规范的“原始”Hibernate 方法。

它的目的与persist基本相同，但有不同的实现细节。此方法的文档严格声明它会持久化实例，“首先分配一个生成的标识符”。该方法将返回此标识符的可序列化值：

```java
Person person = new Person();
person.setName("John");
Long id = (Long) session.save(person);
```

保存已持久化实例的效果与persist相同。当我们尝试保存一个分离的实例时，区别就来了：

```java
Person person = new Person();
person.setName("John");
Long id1 = (Long) session.save(person);

session.evict(person);
Long id2 = (Long) session.save(person);
```

id2变量将不同于id1。对分离实例的保存调用创建一个新的持久实例并为其分配一个新标识符，这会导致在提交或刷新时数据库中出现重复记录。

### 3.3. 合并

merge方法的主要目的是使用分离实体实例中的新字段值更新持久实体实例。

例如，假设我们有一个 RESTful 接口，其中有一个方法可以通过其 id 检索 JSON 序列化的对象给调用者，还有一个方法可以从调用者那里接收该对象的更新版本。通过这种序列化/反序列化的实体将以分离状态出现。

反序列化此实体实例后，我们需要从持久化上下文中获取持久化实体实例，并使用来自此分离实例的新值更新其字段。所以merge方法正是这样做的：

-   通过从传递的对象中获取的 id 查找实体实例(检索持久性上下文中的现有实体实例，或者从数据库加载新实例)
-   将传递的对象中的字段到此实例
-   返回一个新更新的实例

在下面的示例中，我们从上下文中逐出(分离)保存的实体，更改名称字段，然后合并分离的实体：

```java
Person person = new Person(); 
person.setName("John"); 
session.save(person);

session.evict(person);
person.setName("Mary");

Person mergedPerson = (Person) session.merge(person);
```

请注意，merge方法返回一个对象。它是我们加载到持久性上下文中并更新的mergedPerson对象，而不是我们作为参数传递的person对象。它们是两个不同的对象，我们通常需要丢弃person对象。

与persist方法一样， jsr-220 指定merge方法具有我们可以依赖的某些语义：

-   如果实体是分离的，它会现有的持久实体。
-   如果实体是transient，它会一个新创建的持久实体。
-   此操作级联所有具有cascade=MERGE或cascade=ALL映射的关系。
-   如果实体是持久的，那么这个方法调用不会对它产生影响(但级联仍然发生)。

### 3.4. 更新

与 persist 和 save一样， update方法是一个“原始的”Hibernate 方法。它的语义在几个关键点上有所不同：

-   它作用于传递的对象(其返回类型为void)。update方法将传递的对象从分离状态转换为持久状态。
-   如果我们向它传递一个瞬态实体，这个方法会抛出一个异常。

在下面的示例中，我们保存对象，将其从上下文中逐出(分离)，然后更改其名称并调用update。请注意，我们没有将更新操作的结果放在单独的变量中，因为更新发生在person对象本身上。基本上，我们将现有的实体实例重新附加到持久性上下文，这是 JPA 规范不允许我们做的事情：

```java
Person person = new Person();
person.setName("John");
session.save(person);
session.evict(person);

person.setName("Mary");
session.update(person);
```

尝试在瞬态实例上调用更新将导致异常。以下将不起作用：

```java
Person person = new Person();
person.setName("John");
session.update(person); // PersistenceException!
```

### 3.5. 保存或更新

此方法仅出现在 Hibernate API 中，没有对应的标准化方法。类似于update，我们也可以用它来重新附加实例。

实际上，处理更新方法的内部DefaultUpdateEventListener类是DefaultSaveOrUpdateListener的子类，只是覆盖了一些功能。saveOrUpdate方法的主要区别在于它在应用于瞬态实例时不会抛出异常，而是使该瞬态实例持久化。以下代码将保留一个新创建的Person实例：

```java
Person person = new Person();
person.setName("John");
session.saveOrUpdate(person);
```

我们可以将此方法视为使对象持久化的通用工具，无论其状态如何，无论是transient还是detached。

## 4.使用什么？

如果我们没有任何特殊要求，我们应该坚持使用persist和merge方法，因为它们是标准化的并且符合 JPA 规范。

它们也是可移植的，以防我们决定切换到另一个持久性提供者；然而，它们有时看起来不如“原始的”Hibernate 方法save、update和saveOrUpdate有用。

## 5.总结

在本文中，我们讨论了与在运行时管理持久实体相关的不同 Hibernate Session 方法的目的。我们了解了这些方法如何在其生命周期中传输实体实例，以及为什么其中一些方法具有重复的功能。
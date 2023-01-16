## 1. 概述

在本文中，我们将讨论如何在 Hibernate中使实体、集合或属性[不可变。](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/annotations/Immutable.html)

默认情况下，字段是可变的，这意味着我们能够对它们执行更改其状态的操作。

## 2.专家

为了让我们的项目启动并运行，我们首先需要将必要的依赖项添加到我们的pom.xml中。当我们使用 Hibernate 时，我们将添加相应的[依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-core")：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.7.Final</version>
</dependency>
```

而且，因为我们正在使用[HSQLDB](https://search.maven.org/classic/#search|gav|1|g%3A"org.hsqldb" AND a%3A"hsqldb")，我们还需要：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.3.4</version>
</dependency>
```

## 3. 实体注解

首先，让我们定义一个简单的实体类：

```java
@Entity
@Immutable
@Table(name = "events_generated")
public class EventGeneratedId {

    @Id
    @Column(name = "event_generated_id")
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    // standard setters and getters
}
```

正如你所注意到的，我们已经将@Immutable注解添加到我们的实体中，因此如果我们尝试保存一个Event：

```java
@Test
public void addEvent() {
    Event event = new Event();
    event.setId(2L);
    event.setTitle("Public Event");
    session.save(event);
    session.getTransaction().commit();
    session.close();
}
```

然后我们应该得到输出：

```shell
Hibernate: insert into events (title, event_id) values (?, ?)
```

即使我们删除注解，输出也应该是相同的，这意味着无论注解如何，当我们尝试添加实体时都没有效果。

同样重要的是要注意，在我们的EventGeneratedId实体中，我们添加了GeneratedValue注解，但这只会在我们创建实体时产生影响。这是因为它指定了 id 的生成策略——由于Immutable注解，任何其他操作都不会影响Id字段。

### 3.1. 更新实体

现在，我们保存实体没有问题，让我们尝试更新它：

```java
@Test
public void updateEvent() {
    Event event = (Event) session.createQuery(
      "FROM Event WHERE title='My Event'").list().get(0);
    event.setTitle("Public Event");
    session.saveOrUpdate(event);
    session.getTransaction().commit();
}
```

Hibernate 将简单地忽略更新操作而不抛出异常。但是，如果我们删除@Immutable注解，我们会得到不同的结果：

```shell
Hibernate: select ... from events where title='My Event'
Hibernate: update events set title=? where event_id=?
```

这告诉我们，我们的对象现在是可变的(如果我们不包含注解，可变是默认值)并且将允许更新完成它的工作。

### 3.2. 删除实体

在删除实体时：

```java
@Test
public void deleteEvent() {
    Event event = (Event) session.createQuery(
      "FROM Event WHERE title='My Event'").list().get(0);
    session.delete(event);
    session.getTransaction().commit();
}
```

我们将能够执行删除，无论它是否可变：

```shell
Hibernate: select ... from events where title='My Event'
Hibernate: delete from events where event_id=?
```

## 4. 藏品注解

到目前为止，我们已经了解了注解对实体的作用，但正如我们在开头提到的，它也可以应用于集合。

首先，让我们向Event类添加一个集合：

```java
@Immutable
public Set<String> getGuestList() {
    return guestList;
}
```

和以前一样，我们已经预先添加了注解，所以如果我们继续尝试向我们的集合中添加一个元素：

```shell
org.hibernate.HibernateException: 
  changed an immutable collection instance: [com.baeldung.entities.Event.guestList#1]
```

这次我们得到一个例外，因为对于集合，我们不允许添加或删除它们。

### 4.1. 删除集合

另一种情况下，Collection是不可变的，当我们尝试删除并设置了@Cascade注解时会抛出异常。

因此，每当出现@Immutable并且我们尝试删除时：

```java
@Test
public void deleteCascade() {
    Event event = (Event) session.createQuery(
      "FROM Event WHERE title='Public Event'").list().get(0);
    String guest = event.getGuestList().iterator().next();
    event.getGuestList().remove(guest);
    session.saveOrUpdate(event);
    session.getTransaction().commit();
}
```

输出：

```shell
org.hibernate.HibernateException: 
  changed an immutable collection instance:
  [com.baeldung.entities.Event.guestList#1]
```

## 5. XML注解

最后，还可以通过mutable=false属性使用 XML 完成配置：

```xml
<hibernate-mapping>
    <class name="com.baeldung.entities.Event" mutable="false">
        <id name="id" column="event_id">
            <generator class="increment"/>
        </id>
        <property name="title"/>
    </class>
</hibernate-mapping>
```

但是，由于我们基本上使用注解方法实现示例，因此我们不会使用 XML 进行详细介绍。

## 六. 总结

在这篇简短的文章中，我们探讨了 Hibernate 中有用的@Immutable注解，以及它如何帮助我们定义更好的数据语义和约束。
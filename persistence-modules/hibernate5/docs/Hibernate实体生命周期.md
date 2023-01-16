## 1. 概述

每个 Hibernate 实体在框架内自然都有一个生命周期——它处于瞬态、托管、分离或删除状态。

在概念和技术层面上理解这些状态对于正确使用 Hibernate 至关重要。

要了解处理实体的各种 Hibernate 方法，请查看[我们之前的教程之一](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)。

## 2. 辅助方法

在本教程中，我们将始终使用几种辅助方法：

-   HibernateLifecycleUtil.getManagedEntities(session) –我们将使用它从 Session 的 内部存储中获取所有托管实体

-   DirtyDataInspector.getDirtyEntities() –我们将使用此方法获取所有标记为“脏”的实体的列表

-   HibernateLifecycleUtil.queryCount(query) – 一种对嵌入式数据库进行 count()查询的便捷方法

为了更好的可读性，上面所有的辅助方法都是静态导入的。你可以在本文末尾链接的 GitHub 项目中找到它们的实现。

## 3. 一切都与持久性上下文有关

在进入实体生命周期的话题之前，首先，我们需要了解 持久化上下文。

简而言之，持久性上下文 位于客户端代码和数据存储之间。它是一个暂存区，持久性数据在其中转换为实体，准备好由客户端代码读取和更改。

从理论上讲，持久化上下文 是[工作单元](https://martinfowler.com/eaaCatalog/unitOfWork.html)模式的一种实现 。它跟踪所有加载的数据，跟踪该数据的更改，并负责在业务事务结束时最终将任何更改同步回数据库。

JPA EntityManager和 Hibernate 的Session 是持久性上下文 概念的实现。在整篇文章中，我们将使用 Hibernate Session 来表示 持久性上下文。

Hibernate 实体生命周期状态解释了实体如何与持久化上下文相关，我们将在接下来看到。

## 4. 管理实体

托管实体是数据库表行的表示(尽管该行还不一定存在于数据库中)。

这是由当前运行的Session管理的，对其所做的每个更改都将被自动跟踪并传播到数据库。

Session 要么从数据库加载实体，要么重新附加一个分离的实体。 我们将在第 5 节中讨论分离实体。

让我们观察一些代码以获得澄清。

我们的示例应用程序定义了一个实体，即FootballPlayer类。在启动时，我们将使用一些示例数据初始化数据存储：

```plaintext
+-------------------+-------+
| Name              |  ID   |
+-------------------+-------+
| Cristiano Ronaldo | 1     |
| Lionel Messi      | 2     |
| Gianluigi Buffon  | 3     |
+-------------------+-------+
```

假设我们想要更改布冯的名字 - 我们想输入他的全名 Gianluigi Buffon 而不是 Gigi Buffon。

首先，我们需要通过获取 Session 来启动我们的工作单元：

```java
Session session = sessionFactory.openSession();
```

在服务器环境中，我们可以 通过上下文感知代理向我们的代码中注入会话。原则保持不变：我们需要一个Session 来封装我们工作单元的业务事务。

接下来，我们将指示我们的 Session从持久存储中加载数据：

```java
assertThat(getManagedEntities(session)).isEmpty();

List<FootballPlayer> players = s.createQuery("from FootballPlayer").getResultList();

assertThat(getManagedEntities(session)).size().isEqualTo(3);

```

当我们第一次获得 Session时，它的持久上下文存储是空的，如我们的第一个断言语句所示 。

接下来，我们执行一个查询，从数据库中检索数据，创建数据的实体表示，最后返回实体供我们使用。

在内部， Session 跟踪它在持久性上下文存储中加载的所有实体。在我们的例子中，会话的 内部存储将在查询后包含 3 个实体。

现在让我们更改 Gigi 的名字：

```java
Transaction transaction = session.getTransaction();
transaction.begin();

FootballPlayer gigiBuffon = players.stream()
  .filter(p -> p.getId() == 3)
  .findFirst()
  .get();

gigiBuffon.setName("Gianluigi Buffon");
transaction.commit();

assertThat(getDirtyEntities()).size().isEqualTo(1);
assertThat(getDirtyEntities().get(0).getName()).isEqualTo("Gianluigi Buffon");
```

### 4.1. 它是如何工作的？

在调用事务commit()或 flush()时， 会话将从其跟踪列表中找到任何脏实体并将状态同步到数据库。

请注意，我们不需要调用任何方法来通知 Session我们更改了实体中的某些内容——因为它是一个托管实体，所有更改都会自动传播到数据库。

受管实体始终是持久实体——它必须具有数据库标识符，即使尚未创建数据库行表示，即 INSERT 语句正在等待工作单元结束。

请参阅下面有关瞬态实体的章节。

## 5.分离实体

分离的实体 只是 一个普通实体 POJO ，其标识值对应于数据库行。与托管实体的区别在于它不再被任何 持久性上下文跟踪。

当用于加载它的Session 关闭时，或者当我们调用 Session.evict(entity) 或Session.clear()时，实体可能会分离。

让我们在代码中看到它：

```java
FootballPlayer cr7 = session.get(FootballPlayer.class, 1L);

assertThat(getManagedEntities(session)).size().isEqualTo(1);
assertThat(getManagedEntities(session).get(0).getId()).isEqualTo(cr7.getId());

session.evict(cr7);

assertThat(getManagedEntities(session)).size().isEqualTo(0);
```

我们的持久性上下文不会跟踪分离实体中的变化：

```java
cr7.setName("CR7");
transaction.commit();

assertThat(getDirtyEntities()).isEmpty();
```

Session.merge(entity)/Session.update(entity) 可以 (重新)附加会话：

```java
FootballPlayer messi = session.get(FootballPlayer.class, 2L);

session.evict(messi);
messi.setName("Leo Messi");
transaction.commit();

assertThat(getDirtyEntities()).isEmpty();

transaction = startTransaction(session);
session.update(messi);
transaction.commit();

assertThat(getDirtyEntities()).size().isEqualTo(1);
assertThat(getDirtyEntities().get(0).getName()).isEqualTo("Leo Messi");
```

有关Session.merge()和Session.update()的参考，请参见[此处](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)。

### 5.1. 身份字段是最重要的

我们来看看下面的逻辑：

```java
FootballPlayer gigi = new FootballPlayer();
gigi.setId(3);
gigi.setName("Gigi the Legend");
session.update(gigi);
```

在上面的示例中，我们通过其构造函数以通常的方式实例化了一个实体。我们用值填充了字段，并将身份设置为 3，这对应于属于 Gigi Buffon 的持久数据的身份。调用 update()与我们从另一个持久性上下文 加载实体具有完全相同的效果 。

事实上，Session 不区分重新附加的实体来自何处。

从 HTML 表单值构造分离的实体是 Web 应用程序中相当常见的场景。

就会 话而言，分离的实体只是一个普通实体，其标识值对应于持久数据。

请注意，上面的示例仅用于演示目的。我们需要确切地知道我们在做什么。否则，如果我们只是在要更新的字段上设置值，而其余部分保持不变(因此，实际上是空值)，我们最终可能会在整个实体中得到空值。

## 6. 瞬态实体

瞬态实体只是一个实体对象，在持久存储中没有表示，并且不受任何 Session管理。

瞬态实体的一个典型示例是通过其构造函数实例化一个新实体。

要使瞬态实体 持久化，我们需要调用 Session.save(entity) 或 Session.saveOrUpdate(entity)：

```java
FootballPlayer neymar = new FootballPlayer();
neymar.setName("Neymar");
session.save(neymar);

assertThat(getManagedEntities(session)).size().isEqualTo(1);
assertThat(neymar.getId()).isNotNull();

int count = queryCount("select count() from Football_Player where name='Neymar'");

assertThat(count).isEqualTo(0);

transaction.commit();
count = queryCount("select count() from Football_Player where name='Neymar'");

assertThat(count).isEqualTo(1);
```

一旦我们执行 Session.save(entity)，实体就会被分配一个标识值并由 Session管理。但是，它可能在数据库中尚不可用，因为 INSERT 操作可能会延迟到工作单元结束。

## 7.删除的实体

如果Session.delete(entity)已被调用，则实体处于删除(删除)状态，并且 会话 已将实体标记为删除。DELETE 命令本身可能会在工作单元结束时发出。

让我们在下面的代码中看到它：

```java
session.delete(neymar);

assertThat(getManagedEntities(session).get(0).getStatus()).isEqualTo(Status.DELETED);
```

但是，请注意实体一直保留在持久性上下文存储中，直到工作单元结束。

## 八. 总结

持久性上下文的概念对于理解 Hibernate 实体的生命周期至关重要。我们通过查看演示每个状态的代码示例阐明了生命周期。
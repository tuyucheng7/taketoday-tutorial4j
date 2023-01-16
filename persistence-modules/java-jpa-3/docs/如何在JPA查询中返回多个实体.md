## 1. 概述

在这个简短的教程中，我们将了解如何在 JPA 查询中返回多个不同的实体。 

首先，我们将创建一个包含几个不同实体的简单代码示例。然后，我们将解释如何创建返回多个不同实体的 JPA 查询。最后，我们将在 Hibernate 的 JPA 实现中展示一个工作示例。

## 2. 示例配置

在我们解释如何在单个查询中返回多个实体之前，让我们构建一个我们将要处理的示例。

我们将创建一个应用程序，允许其用户购买特定电视频道的订阅。它由 3 个表组成：Channel、Subscription和User。

首先，让我们看一下Channel 实体：

```java
@Entity
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private Long subscriptionId;

   // getters, setters, etc.
}

```

它由映射到相应列的 3 个字段组成。第一个也是最重要的一个是id， 它也是主键。在代码字段中，我们将存储Channel的代码。

最后但同样重要的是，还有一个subscriptionId列。它将用于在频道和它所属的订阅之间创建关系。一个频道可以属于不同的订阅。

现在，让我们看看订阅实体：

```java
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

   // getters, setters, etc.
}
```

它甚至比第一个更简单。它由作为主键的id字段和订阅的 代码字段组成。

让我们再看看User实体：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private Long subscriptionId;

    // getters, setters, etc.
}
```

除了主键id字段外，它还包含 email和subscriptionId字段。后者用于在用户和他们选择的订阅之间创建关系。

## 3. 查询返回多个实体

### 3.1. 创建查询

为了创建返回多个不同实体的查询，我们需要做两件事。

首先，我们需要在 SQL 查询的SELECT 部分列出我们想要返回的实体，以逗号分隔。

其次，我们需要通过它们的主键和对应的外键将它们相互连接起来。

让我们看看我们的例子。想象一下，我们想要获取分配给用户使用给定电子邮件购买的订阅 的所有频道。 JPA 查询看起来像这样：

```sql
SELECT c, s, u
  FROM Channel c, Subscription s, User u
  WHERE c.subscriptionId = s.id AND s.id = u.subscriptionId AND u.email=:email
```

### 3.2. 提取结果

选择多个不同实体的 JPA 查询在对象数组中返回它们。值得指出的是，数组保持实体的顺序。这是至关重要的信息，因为我们需要手动将返回的对象转换为特定的实体类。

让我们看看实际效果。我们创建了一个专用的存储库类来创建查询并获取结果：

```java
public class ReportRepository {
    private final EntityManagerFactory emf;

    public ReportRepository() {
        // create an instance of entity manager factory
    }

    public List<Object[]> find(String email) {
        EntityManager entityManager = emf.createEntityManager();
        Query query = entityManager
          .createQuery("SELECT c, s, u FROM  Channel c, Subscription s, User u" 
          + " WHERE c.subscriptionId = s.id AND s.id = u.subscriptionId AND u.email=:email");
        query.setParameter("email", eamil);

        return query.getResultList();
    }
}

```

我们正在使用上一节中的精确查询。然后，我们设置一个电子邮件参数来缩小结果范围。最后，我们获取结果列表。

让我们看看如何从获取的列表中提取单个实体：

```java
List<Object[]> reportDetails = reportRepository.find("user1@gmail.com");

for (Object[] reportDetail : reportDetails) {
    Channel channel = (Channel) reportDetail[0];
    Subscription subscription = (Subscription) reportDetail[1];
    User user = (User) reportDetail[2];
    
    // do something with entities
}
```

我们遍历获取的列表并从给定的对象数组中提取实体。考虑到我们的 JPA 查询及其SELECT部分中实体的顺序，我们将Channel实体作为第一个元素，将Subscription实体作为第二个元素，将User实体作为数组的最后一个元素。

## 4. 总结

在本文中，w 讨论了如何在 JPA 查询中返回多个实体。首先，我们创建了一个我们将在本文后面处理的示例。然后，我们解释了如何编写 JPA 查询以返回多个不同的实体。最后，我们展示了如何从结果列表中提取它们。
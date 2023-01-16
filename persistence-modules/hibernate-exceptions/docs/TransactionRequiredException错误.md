## 1. 概述

在本教程中，我们将检查TransactionRequiredException错误的原因以及解决方法。

## 2. TransactionRequiredException 异常

当我们试图在没有事务的情况下执行修改数据库的数据库操作时，通常会发生此错误。

例如，尝试在没有事务的情况下更新记录：

```java
Query updateQuery
  = session.createQuery("UPDATE Post p SET p.title = ?1, p.body = ?2 WHERE p.id = ?3");
updateQuery.setParameter(1, title);
updateQuery.setParameter(2, body);
updateQuery.setParameter(3, id);
updateQuery.executeUpdate();
```

将通过以下几行消息引发异常：

```bash
...
javax.persistence.TransactionRequiredException: Executing an update/delete query
  at org.hibernate.query.internal.AbstractProducedQuery.executeUpdate(AbstractProducedQuery.java:1586)
...
```

## 3. 提供交易

显而易见的解决方案是将任何数据库修改操作包装在事务中：

```java
Transaction txn = session.beginTransaction();
Query updateQuery
  = session.createQuery("UPDATE Post p SET p.title = ?1, p.body = ?2 WHERE p.id = ?3");
updateQuery.setParameter(1, title);
updateQuery.setParameter(2, body);
updateQuery.setParameter(3, id);
updateQuery.executeUpdate();
txn.commit();
```

在上面的代码片段中，我们手动发起并提交事务。尽管在Spring Boot环境中，我们可以通过使用@Transactional 注解来实现这一点。

## 4. Spring的事务支持

如果我们想要更细粒度的控制，我们可以使用 Spring 的TransactionTemplate。因为这允许程序员在继续执行方法的代码之前立即触发对象的持久化。

例如，假设我们想在发送电子邮件通知之前更新帖子：

```java
public void update() {
    entityManager.createQuery("UPDATE Post p SET p.title = ?2, p.body = ?3 WHERE p.id = ?1")
      // parameters
      .executeUpdate();
    sendEmail();
}
```

将@Transactional 应用于上述方法可能会导致尽管更新过程中存在异常，但仍会发送电子邮件。这是因为事务只有在方法退出并即将返回给调用者时才会提交。

因此，在TransactionTemplate中更新帖子将防止这种情况，因为它会立即提交操作：

```java
public void update() {
    transactionTemplate.execute(transactionStatus -> {
        entityManager.createQuery("UPDATE Post p SET p.title = ?2, p.body = ?3 WHERE p.id = ?1")
          // parameters
          .executeUpdate();
        transactionStatus.flush();
        return null;
    });
    sendEmail();
}
```

## 5.总结

总之，将数据库操作包装在事务中通常是一种很好的做法。它有助于防止数据损坏
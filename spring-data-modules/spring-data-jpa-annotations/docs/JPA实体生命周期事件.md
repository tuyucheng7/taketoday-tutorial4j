## 1. 简介

在使用JPA时，我们可以在实体的生命周期中收到多个事件的通知。**在本教程中，我们介绍JPA实体生命周期事件，以及如何在发生这些事件时使用注解来处理回调和执行代码**。

我们首先通过在实体本身上使用注解标注方法开始，然后使用实体监听器。

## 2. JPA实体生命周期事件

JPA指定了七个可选的生命周期事件，它们分别为：

-   在为新实体调用persist之前：@PrePersist
-   在为新实体调用persist之后：@PostPersist
-   在删除实体之前：@PreRemove
-   在删除实体之后：@PostRemove
-   在更新实体之前：@PreUpdate
-   在更新实体之后：@PostUpdate
-   加载实体后：@PostLoad

**有两种使用生命周期事件注解的方法**：在实体中标注方法和创建带有注解回调方法的EntityListener，我们也可以同时使用两者。**但无论回调方法位于哪里，它们都必须具有void返回类型**。

因此，如果我们创建一个新实体并调用Repository的save方法，则首先会调用带有@PrePersist注解的方法，然后将记录插入数据库，最后调用我们的@PostPersist方法。**如果我们使用@GeneratedValue来自动生成我们的主键，我们可以期望该主键在@PostPersist方法中可用**。

对于@PostPersist、@PostRemove和@PostUpdate操作，文档中提到这些事件可以在操作发生后、刷新后或事务结束时发生。

**我们应该注意，@PreUpdate回调仅在数据实际更改时才会被调用**：也就是说，如果有一个实际的SQL更新语句要运行，无论是否有任何实际更改，都会调用@PostUpdate回调。

**如果我们用于保存或删除实体的任何回调抛出异常，事务将被回滚**。

## 3. 标注实体

首先我们从最简单的开始，直接在我们的实体中使用回调注解。在我们的示例中，当用户记录发生更改时，我们通过日志记录下来，因此在我们的回调方法中添加简单的日志记录语句。

此外，我们要确保在从数据库中加载用户后设置用户的全名，这可以通过使用@PostLoad标注一个方法来实现。

下面是用户实体类：

```java
@Entity
public class User {
    private static Log log = LogFactory.getLog(User.class);

    @Id
    @GeneratedValue
    private int id;
    
    private String userName;
    private String firstName;
    private String lastName;
    @Transient
    private String fullName;

    // Standard getters/setters
}
```

接下来，我们需要一个UserRepository接口：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserName(String userName);
}
```

现在，回到我们的User类并添加我们的回调方法：

```java
@PrePersist
public void logNewUserAttempt() {
    log.info("Attempting to add new user with username: " + userName);
}
    
@PostPersist
public void logNewUserAdded() {
    log.info("Added user '" + userName + "' with ID: " + id);
}
    
@PreRemove
public void logUserRemovalAttempt() {
    log.info("Attempting to delete user: " + userName);
}
    
@PostRemove
public void logUserRemoval() {
    log.info("Deleted user: " + userName);
}

@PreUpdate
public void logUserUpdateAttempt() {
    log.info("Attempting to update user: " + userName);
}

@PostUpdate
public void logUserUpdate() {
    log.info("Updated user: " + userName);
}

@PostLoad
public void logUserLoad() {
    fullName = firstName + " " + lastName;
}
```

当运行我们的测试时，我们会看到一系列来自我们注解方法的日志记录语句。此外，当我们从数据库加载用户时，我们可以可靠地期望用户的fullName属性已经正确的设置。

## 4. 标注EntityListener

现在扩展我们的示例，并使用单独的EntityListener来处理我们的更新回调。**如果我们有一些要应用于所有实体的操作，我们可能会倾向于这种方法用于统一管理，而不是将方法放在单独的实体中**。

下面我们创建AuditTrailListener来记录User表上的所有活动：

```java
public class AuditTrailListener {
    private static Log log = LogFactory.getLog(AuditTrailListener.class);
    
    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(User user) {
        if (user.getId() == 0) {
            log.info("[USER AUDIT] About to add a user");
        } else {
            log.info("[USER AUDIT] About to update/delete user: " + user.getId());
        }
    }
    
    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(User user) {
        log.info("[USER AUDIT] add/update/delete complete for user: " + user.getId());
    }
    
    @PostLoad
    private void afterLoad(User user) {
        log.info("[USER AUDIT] user loaded from database: " + user.getId());
    }
}
```

从代码中可以看出，**我们可以将多个注解应用于一个方法**。现在，回到我们的User实体并将@EntityListener注解添加到类上，同时指定AuditTrailListener作为注解的参数：

```java
@EntityListeners(AuditTrailListener.class)
@Entity
public class User {
    // ...
}
```

此时，如果运行我们的测试，我们会得到两组日志消息的每个更新操作和一个用户从数据库加载后的日志消息。

## 5. 总结

在本文中，我们介绍了JPA实体生命周期回调是什么以及何时调用它们。
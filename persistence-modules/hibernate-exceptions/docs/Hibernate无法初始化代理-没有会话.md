## 1. 概述

使用 Hibernate，我们可能会遇到这样的错误：org.hibernate.LazyInitializationException : could not initialize proxy – no Session 。

在本快速教程中，我们将仔细研究错误的根本原因并学习如何避免它。

## 2.理解错误

在打开的 Hibernate 会话上下文之外访问延迟加载的对象将导致此异常。

了解Session、Lazy Initialisation 和Proxy Object是什么，以及它们如何在Hibernate框架中结合在一起是很重要的：

-   会话是表示应用程序和数据库之间对话的持久性上下文。
-   延迟加载意味着在代码中访问该对象之前，该对象不会被加载到会话上下文中。
-   Hibernate 创建一个动态的代理对象子类，只有当我们第一次使用该对象时，它才会访问数据库。

当我们尝试使用代理对象从数据库中获取延迟加载的对象，但 Hibernate 会话已经关闭时，会发生此错误。

## 3. LazyInitializationException示例

让我们看看具体场景中的异常。

我们想要创建一个具有关联角色的简单用户对象。我们将使用 JUnit 来演示LazyInitializationException错误。

### 3.1. 休眠工具类

首先，让我们定义一个HibernateUtil类来创建一个带有配置的SessionFactory。

我们将使用内存中的HSQLDB数据库。

### 3.2. 实体

这是我们的用户实体：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @OneToMany
    private Set<Role> roles;
    
}

```

以及关联的角色实体：

```java
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "role_name")
    private String roleName;
}
```

如我们所见， User和Role之间存在一对多关系。

### 3.3. 创建具有角色的用户

然后我们将创建两个Role对象：

```java
Role admin = new Role("Admin");
Role dba = new Role("DBA");
```

我们还将创建一个具有以下角色的用户：

```java
User user = new User("Bob", "Smith");
user.addRole(admin);
user.addRole(dba);
```

最后，我们可以打开一个会话并持久化对象：

```java
Session session = sessionFactory.openSession();
session.beginTransaction();
user.getRoles().forEach(role -> session.save(role));
session.save(user);
session.getTransaction().commit();
session.close();
```

### 3.4. 获取角色

在第一个场景中，我们将看到如何以正确的方式获取用户角色：

```java
@Test
public void whenAccessUserRolesInsideSession_thenSuccess() {

    User detachedUser = createUserWithRoles();

    Session session = sessionFactory.openSession();
    session.beginTransaction();
		
    User persistentUser = session.find(User.class, detachedUser.getId());
		
    Assert.assertEquals(2, persistentUser.getRoles().size());
		
    session.getTransaction().commit();
    session.close();
}
```

这里我们访问会话中的对象，因此没有错误。

### 3.5. 获取角色失败

在第二种情况下，我们将在会话外调用getRoles方法：

```java
@Test
public void whenAccessUserRolesOutsideSession_thenThrownException() {
		
    User detachedUser = createUserWithRoles();

    Session session = sessionFactory.openSession();
    session.beginTransaction();
		
    User persistentUser = session.find(User.class, detachedUser.getId());
		
    session.getTransaction().commit();
    session.close();

    thrown.expect(LazyInitializationException.class);
    System.out.println(persistentUser.getRoles().size());
}
```

在这种情况下，我们尝试在会话关闭后访问角色，结果，代码抛出LazyInitializationException 。

## 4. 如何避免错误

现在让我们来看看克服错误的四种不同解决方案。

### 4.1. 在上层打开会话

最佳实践是在持久层中打开一个会话，例如使用[DAO 模式。](https://www.baeldung.com/java-dao-pattern)

我们可以在上层打开会话，以安全的方式访问关联的对象。比如我们可以在View层打开session。

结果，我们会看到响应时间增加，这将影响应用程序的性能。

就关注点分离原则而言，此解决方案是一种反模式。此外，它还可能导致违反数据完整性和长时间运行的事务。

### 4.2. 开启enable_lazy_load_no_trans属性

此 Hibernate 属性用于声明延迟加载对象获取的全局策略。

默认情况下，此属性为false。打开它意味着对关联的延迟加载实体的每次访问都将包装在新事务中运行的新会话中：

```xml
<property name="hibernate.enable_lazy_load_no_trans" value="true"/>
```

不建议使用此属性来避免LazyInitializationException错误，因为它会降低我们应用程序的性能。这是因为我们 最终会遇到[n+1 问题](https://www.baeldung.com/hibernate-common-performance-problems-in-logs)。简单的说就是一个SELECT给User，N个额外的SELECT去获取每个用户的角色。

这种方法效率不高，也被认为是一种反模式。

### 4.3. 使用FetchType.EAGER策略 

我们可以将此策略与@OneToMany注解一起使用：

```java
@OneToMany(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id")
private Set<Role> roles;
```

当我们需要为大多数用例获取关联的集合时，这是一种针对特定用途的妥协解决方案。

[声明EAGER fetch type](https://www.baeldung.com/hibernate-fetchmode)比显式获取大多数不同业务流的集合要容易得多。

### 4.4. 使用连接获取

我们还可以在JPQL中使用JOIN FETCH指令来按需获取关联的集合：

```sql
SELECT u FROM User u JOIN FETCH u.roles
```

或者我们可以使用 Hibernate Criteria API：

```java
Criteria criteria = session.createCriteria(User.class);
criteria.setFetchMode("roles", FetchMode.EAGER);
```

在这里，我们指定应从数据库中获取的关联集合，以及同一往返中的User对象。使用此查询可提高迭代效率，因为它消除了单独检索关联对象的需要。

这是避免LazyInitializationException错误的最有效和细粒度的解决方案。

## 5.总结

在本文中，我们学习了如何处理org.hibernate.LazyInitializationException : could not initialize proxy – no Session错误。

我们探索了不同的方法以及性能问题。我们还讨论了使用简单高效的解决方案以避免影响性能的重要性。

最后，我们演示了连接获取方法是如何避免错误的好方法。
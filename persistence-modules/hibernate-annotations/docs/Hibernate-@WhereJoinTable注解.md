## 1. 概述

使用对象关系映射工具(如 Hibernate)可以轻松地将我们的数据读入对象，但可能会使我们难以使用复杂的数据模型进行查询。

多对多关系总是具有挑战性，但当我们希望 根据关系本身的某些属性获取相关实体时，它可能更具挑战性。

在本教程中，我们将看看如何使用 Hibernate 的@WhereJoinTable注解来解决这个问题。

## 2. 基本的@ManyToMany关系

让我们从一个简单的[@ManyToMany关系](https://www.baeldung.com/jpa-many-to-many)开始。我们需要领域模型实体、关系实体和一些样本测试数据。

### 2.1. 领域模型

假设我们有两个简单的实体，User和Group，它们关联为@ManyToMany：

```java
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany
    private List<Group> groups = new ArrayList<>();

    // standard getters and setters

}

@Entity
public class Group {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "groups")
    private List<User> users = new ArrayList<>();

    // standard getters and setters

}

```

如我们所见，我们的User实体可以是多个Group实体的成员。同样，一个组实体可以包含多个用户实体。

### 2.2. 关系实体

对于@ManyToMany关联，我们需要一个单独的数据库表，称为关系表。关系表至少需要包含两列：相关用户和组实体的主键。

只有两个主键列，[我们的 Hibernate 映射可以表示这个关系表](https://www.baeldung.com/hibernate-many-to-many)。

但是，如果我们需要在关系表中放置额外的数据，我们还应该为多对多关系本身定义一个关系实体。

让我们创建UserGroupRelation类来执行此操作：

```java
@Entity(name = "r_user_group")
public class UserGroupRelation implements Serializable {

    @Id
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Id
    @Column(name = "group_id", insertable = false, updatable = false)
    private Long groupId;

}

```

在这里，我们将实体命名为r_user_group，以便稍后引用它。

对于我们的额外数据，假设我们要存储每个User的每个Group的角色。因此，我们将创建UserGroupRole枚举：

```java
public enum UserGroupRole {
    MEMBER, MODERATOR
}

```

接下来，我们将向UserGroupRelation添加角色属性：

```java
@Enumerated(EnumType.STRING)
private UserGroupRole role;

```

最后，要正确配置它，我们需要在User的组集合上添加@JoinTable注解。这里我们将使用r_user_group指定连接表名称， UserGroupRelation的实体名称：

```java
@ManyToMany
@JoinTable(
    name = "r_user_group",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id")
)
private List<Group> groups = new ArrayList<>();

```

### 2.3. 样本数据

对于我们的集成测试，让我们定义一些示例数据：

```java
public void setUp() {
    session = sessionFactory.openSession();
    session.beginTransaction();
    
    user1 = new User("user1");
    user2 = new User("user2");
    user3 = new User("user3");

    group1 = new Group("group1");
    group2 = new Group("group2");

    session.save(group1);
    session.save(group2);

    session.save(user1);
    session.save(user2);
    session.save(user3);

    saveRelation(user1, group1, UserGroupRole.MODERATOR);
    saveRelation(user2, group1, UserGroupRole.MODERATOR);
    saveRelation(user3, group1, UserGroupRole.MEMBER);

    saveRelation(user1, group2, UserGroupRole.MEMBER);
    saveRelation(user2, group2, UserGroupRole.MODERATOR);
}

private void saveRelation(User user, Group group, UserGroupRole role) {

    UserGroupRelation relation = new UserGroupRelation(user.getId(), group.getId(), role);
    
    session.save(relation);
    session.flush();
    session.refresh(user);
    session.refresh(group);
}
```

正如我们所见，user1和user2在两个组中。另外，我们应该注意到，虽然user1在group1上是MODERATOR，但同时它在group2上具有MEMBER角色。

## 3.获取@ManyToMany关系

现在我们已经正确配置了我们的实体，让我们获取用户实体的组。

### 3.1. 简单获取

为了获取组，我们可以在活动的 Hibernate 会话中简单地调用User的getGroups()方法：

```java
List<Group> groups = user1.getGroups();

```

我们对小组的输出将是：

```java
[Group [name=group1], Group [name=group2]]    

```

但是，我们如何才能获得组角色仅为MODERATOR 的用户的组呢？

### 3.2. 关系实体上的自定义过滤器

我们可以使用@WhereJoinTable注解直接获取过滤后的分组。

让我们将一个新属性定义为moderatorGroups并在其上放置@WhereJoinTable注解。当我们通过此属性访问相关实体时，它将仅包含我们的用户是MODERATOR 的组。

我们需要添加一个 SQL where 子句以按MODERATOR角色过滤组：

```java
@WhereJoinTable(clause = "role='MODERATOR'")
@ManyToMany
@JoinTable(
    name = "r_user_group",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id")
)
private List<Group> moderatorGroups = new ArrayList<>();

```

因此，我们可以很容易地获得应用了指定 SQL where 子句的组：

```java
List<Group> groups = user1.getModeratorGroups();

```

我们的输出将是用户仅具有MODERATOR 角色的组：

```java
[Group [name=group1]]

```

## 4. 总结

在本教程中，我们学习了如何使用 Hibernate 的@WhereJoinTable注解根据关系表的属性过滤@ManyToMany集合。
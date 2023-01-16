## 1. 简介

在本教程中，我们将讨论javax.persistence包中的EntityNotFoundException 。我们将介绍可能发生此异常的情况，然后，我们将为这些情况编写测试。

## 2. EntityNotFoundException什么时候抛出？

此异常的 Oracle 文档定义了持久性提供程序可以抛出[EntityNotFoundException](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityNotFoundException.html)的三种情况：

-   EntityManager.getReference对不存在的实体
-   数据库中不存在的对象上的EntityManager.refresh
-   EntityManager.lock对数据库中不存在的实体进行悲观锁定

除了这三种用例之外，还有一种情况有点模棱两可。使用@ManyToOne关系和延迟加载时也会发生此异常 。

当我们使用@ManyToOne注解时，引用的实体必须存在。这通常通过使用外键的数据库完整性来确保。如果我们在我们的关系模型中不使用外键或者我们的数据库不一致，我们可以在获取实体时看到EntityNotFoundException 。我们将在下一节中举例说明这一点。

## 3. EntityNotFoundException实践

首先，让我们介绍一个更简单的用例。在上一节中，我们提到了[getReference方法](https://www.baeldung.com/jpa-entity-manager-get-reference)。我们使用此方法来获取特定实体的代理。该代理仅初始化了主键字段。当我们在此代理实体上调用 getter 时，持久性提供程序会初始化其余字段。如果实体在数据库中不存在，那么我们得到EntityNotFoundException：

```java
@Test(expected = EntityNotFoundException.class)
public void givenNonExistingUserId_whenGetReferenceIsUsed_thenExceptionIsThrown() {
    User user = entityManager.getReference(User.class, 1L);
    user.getName();
}

```

用户实体是基本的。它只有两个字段，没有关系。我们在测试的第一行创建一个主键值为 1L 的代理实体。之后，我们在该代理实体上调用 getter。持久性提供程序尝试通过主键获取实体，并且由于记录不存在，因此抛出EntityNotFoundException 。

对于下一个示例，我们将使用不同的域实体。我们将创建Item和Category实体，它们之间具有双向关系：

```java
@Entity
public class Item implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    private long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;
    // getters and setters
}
@Entity
public class Category implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    private long id;
    private String name;
    @OneToMany
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<Item> items = new ArrayList<>();
    // getters and setters
}
```

请注意，我们在@ManyToOne注解上使用延迟获取。此外，我们使用@ForeignKey从数据库中删除约束：

```java
@Test(expected = EntityNotFoundException.class)
public void givenItem_whenManyToOneEntityIsMissing_thenExceptionIsThrown() {
    entityManager.createNativeQuery("Insert into Item (category_id, name, id) values (1, 'test', 1)").executeUpdate();
    entityManager.flush();
    Item item = entityManager.find(Item.class, 1L);
    item.getCategory().getName();
}
```

在这个测试中，我们通过 id 获取 Item 实体。由于我们使用FetchType.LAZY(仅设置了id，类似于前面的示例)，方法find 将返回一个没有完全初始化Category 的Item对象。当我们在Category对象上调用 getter 时，持久性提供程序将尝试从数据库中获取对象，并且由于记录不存在，我们将得到一个异常。

@ManyToOne关系假定引用的实体存在。外键和数据库完整性确保这些实体存在。如果不是这种情况，则有一种解决方法可以忽略丢失的实体。

将@NotFound(action = NotFoundAction.IGNORE)与@ManyToOne注解结合使用将阻止持久性提供程序抛出EntityNotFoundException，但我们必须手动处理丢失的实体以避免NullPointerException。

## 4. 总结

在本文中，我们介绍了在哪些情况下会发生EntityNotFoundException以及我们如何处理它。首先，我们查看了官方文档并涵盖了常见的用例。之后，我们将介绍更复杂的案例以及如何解决此问题。
## 1. 概述

当我们将 Spring Data JPA 与 Hibernate 一起使用时，我们也可以使用 Hibernate 的附加功能。@DynamicUpdate就是这样一个功能。

@DynamicUpdate是可以应用于 JPA 实体的类级注解。它确保 Hibernate 只使用它为更新实体而生成的 SQL 语句中修改过的列。

在本文中，我们将借助[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)示例了解@DynamicUpdate注解。

## 2.JPA @实体

当应用程序启动时，Hibernate 为所有实体的 CRUD 操作生成 SQL 语句。这些 SQL 语句生成一次并缓存在内存中以提高性能。

生成的 SQL 更新语句包括实体的所有列。如果我们更新实体，修改后的列的值将传递给 SQL 更新语句。对于未更新的列，Hibernate 使用它们现有的值进行更新。

让我们试着用一个例子来理解这一点。首先，让我们考虑一个名为Account的 JPA 实体：

```java
@Entity
public class Account {

    @Id
    private int id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private boolean active;

    // Getters and Setters
}
```

接下来，让我们为Account实体编写一个 JPA 存储库：

```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
```

现在，我们将使用 AccountRepository来更新Account对象的名称字段：

```java
Account account = accountRepository.findOne(ACCOUNT_ID);
account.setName("Test Account");
accountRepository.save(account);
```

执行完这个更新后，我们就可以验证生成的SQL语句了。生成的 SQL 语句将包括Account的所有列：

```sql
update Account set active=?, name=?, type=? where id=?
```

## 3. JPA @Entity和@DynamicUpdate

我们已经看到，尽管我们只修改了名称字段，但 Hibernate 已将所有列包含在 SQL 语句中。

现在，让我们将@DynamicUpdate注解添加到Account实体：

```java
@Entity
@DynamicUpdate
public class Account {
    // Existing data and methods
}
```

接下来，让我们运行上一节中使用的相同更新代码。我们可以看到 Hibernate 生成的 SQL，在这种情况下，只包含name列：

```sql
update Account set name=? where id=?
```

那么，当我们在实体上使用@DynamicUpdate时会发生什么？

实际上，当我们在实体上使用@DynamicUpdate时，Hibernate 不会使用缓存的 SQL 语句进行更新。相反，它会在我们每次更新实体时生成一条 SQL 语句。此生成的 SQL 仅包含更改的列。

为了找出更改的列，Hibernate 需要跟踪当前实体的状态。因此，当我们更改实体的任何字段时，它会比较实体的当前状态和修改后的状态。

这意味着@DynamicUpdate具有与之关联的性能开销。因此，我们应该只在实际需要时才使用它。

当然，有一些场景我们应该使用这个注解——例如，如果一个实体代表一个有大量列的表，而这些列中只有少数列需要经常更新。另外，当我们使用无版本乐观锁定时，我们需要使用@DynamicUpdate。

## 4. 总结

在本教程中，我们研究了Hibernate 的@DynamicUpdate注解。我们使用了一个 Spring Data JPA 示例来查看@DynamicUpdate 的实际应用。此外，我们还讨论了何时应该使用此功能以及何时不应该使用此功能。
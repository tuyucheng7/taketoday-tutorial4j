## 1. 概述

从我们的应用程序管理 SQL 语句是我们需要处理的最重要的事情之一，因为它对性能有巨大的影响。在处理对象之间的关系时，有两种主要的抓取设计模式。第一种是懒惰的方法，而另一种是急切的方法。

在本文中，我们将对它们进行概述。此外，我们还将讨论Hibernate 中的@LazyCollection注解。

## 2.延迟获取

当我们想推迟数据初始化直到我们需要它时，我们使用[延迟获取](https://www.baeldung.com/hibernate-lazy-eager-loading)。让我们看一个例子来更好地理解这个想法。

假设我们有一家公司，在这个城市有多个分支机构。每个分支机构都有自己的员工。从数据库的角度来看，这意味着我们在分支机构和它的员工之间有一个一对多的关系。

在延迟获取方法中，一旦获取了分支对象，我们就不会再获取员工了。我们只获取分支对象的数据，我们推迟加载员工列表，直到我们调用 getEmployees ()方法。此时，将执行另一个数据库查询以获取员工。

这种方法的好处是我们减少了最初加载的数据量。原因是我们可能不需要分支机构的员工，加载他们没有意义，因为我们不打算立即使用他们。

## 3. 急切获取

当需要立即加载数据时，我们使用预取。让我们同样以公司、分支机构和员工为例来解释这个想法。一旦我们从数据库中加载了一些分支对象，我们将立即使用相同的数据库查询加载其员工列表。

使用预先获取时的主要问题是我们加载了大量可能不需要的数据。因此，只有当我们确定一旦我们加载了它的对象，急切获取的数据将始终被使用时，我们才应该使用它。

## 4. @LazyCollection注解

当我们需要关注应用程序的性能时，我们使用@LazyCollection注解。从 Hibernate 3.0 开始，@LazyCollection默认启用。使用@LazyCollection的主要思想是控制数据的获取是应该使用懒惰的方法还是急切的方法。

使用@LazyCollection时，我们为LazyCollectionOption设置提供了三个配置选项：TRUE、FALSE和EXTRA。让我们独立讨论它们中的每一个。

### 4.1. 使用LazyCollectionOption.TRUE

此选项为指定字段启用延迟获取方法，并且是从 Hibernate version 3.0 开始的默认设置。因此，我们不需要显式设置此选项。但是，为了更好地解释这个想法，我们将举一个设置此选项的示例。

在此示例中，我们有一个Branch实体，它由一个id、name和一个与Employee实体的@OneToMany关系组成。我们可以注意到，在此示例中，我们将@LazyCollection选项显式设置为true ：

```java
@Entity
public class Branch {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "branch")
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Employee> employees;
    
    // getters and setters
}
```

现在，让我们看一下由id、name、address以及与Branch实体的@ManyToOne关系组成的Employee实体：

```java
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    private String address;
    
    @ManyToOne
    @JoinColumn(name = "BRANCH_ID") 
    private Branch branch; 

    // getters and setters 
}
```

在上面的例子中，当我们得到一个分支对象时，我们不会立即加载员工列表。相反，这个操作将被推迟到我们调用getEmployees()方法时。

### 4.2. 使用LazyCollectionOption.FALSE

当我们将此选项设置为FALSE时，我们启用了预先获取方法。在这种情况下，我们需要明确指定此选项，因为我们将覆盖 Hibernate 的默认值。让我们看另一个例子。

在这种情况下，我们有Branch实体，其中包含id、name和与Employee实体的@OneToMany关系。请注意，我们将@LazyCollection的选项设置为FALSE：

```java
@Entity
public class Branch {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "branch")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Employee> employees;
    
    // getters and setters
}
```

在上面的例子中，当我们得到一个分支对象时，我们会立即加载员工列表的分支。

### 4.3. 使用LazyCollectionOption.EXTRA

有时，我们只关心集合的属性，并不马上需要其中的对象。

例如，回到Branch和Employee的示例，我们可能只需要分支机构中的员工数量，而不关心实际员工的实体。在这种情况下，我们考虑使用EXTRA选项。让我们更新我们的示例来处理这种情况。

与之前的情况类似，Branch实体与Employee实体有一个id、name和一个@OneToMany关系。但是，我们将@LazyCollection的选项设置为EXTRA：

```java
@Entity
public class Branch {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "branch")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OrderColumn(name = "order_id")
    private List<Employee> employees;

    // getters and setters
    
    public Branch addEmployee(Employee employee) {
        employees.add(employee);
        employee.setBranch(this);
        return this;
    }
}
```

我们注意到在这种情况下我们使用了@OrderColumn注解。原因是EXTRA选项只被考虑用于索引列表集合。这意味着如果我们不使用@OrderColumn 注解该字段，则 EXTRA选项将为我们提供与惰性相同的行为，并且将在第一次访问时获取集合。

此外，我们还定义了addEmployee()方法，因为我们需要Branch和Employee从两侧同步。如果我们添加一个新的Employee并为他设置一个分支，我们也需要更新Branch实体中的员工列表。

现在，当持久化一个具有三个关联员工的Branch实体时，我们需要将代码编写为：

```java
entityManager.persist(
  new Branch().setId(1L).setName("Branch-1")

    .addEmployee(
      new Employee()
        .setId(1L)
        .setName("Employee-1")
        .setAddress("Employee-1 address"))
  
    .addEmployee(
      new Employee()
        .setId(2L)
        .setName("Employee-2")
        .setAddress("Employee-2 address"))
  
    .addEmployee(
      new Employee()
        .setId(3L)
        .setName("Employee-3")
        .setAddress("Employee-3 address"))
);

```

如果我们看一下执行的查询，我们会注意到 Hibernate 将首先为 Branch-1插入一个新的Branch 。然后它将插入 Employee-1、Employee-2，然后是 Employee-3。

我们可以看到这是一种自然行为。然而， EXTRA选项中的不良行为是，在刷新上述查询后，它将执行三个额外的查询——一个用于我们添加的每个Employee：

```sql
UPDATE EMPLOYEES
SET
    order_id = 0
WHERE
    id = 1
     
UPDATE EMPLOYEES
SET
    order_id = 1
WHERE
    id = 2
 
UPDATE EMPLOYEES
SET
    order_id = 2
WHERE
    id = 3
```

执行更新语句以设置列表条目索引。这是所谓的[N +1 查询问题](https://www.baeldung.com/hibernate-common-performance-problems-in-logs)的示例，这意味着我们执行N个额外的 SQL 语句来更新我们创建的相同数据。

正如我们从示例中注意到的那样，在使用EXTRA选项时我们可能会遇到N +1 查询问题。

另一方面，使用这个选项的好处是当我们需要获取每个分支的员工列表的大小时：

```java
int employeesCount = branch.getEmployees().size();
```

当我们调用这条语句时，它只会执行这条 SQL 语句：

```sql
SELECT
    COUNT(ID)
FROM
    EMPLOYEES
WHERE
    BRANCH_ID = :ID
```

如我们所见，我们不需要将员工列表存储在内存中来获取其大小。尽管如此，我们还是建议避免使用EXTRA选项，因为它会执行额外的查询。

此处还值得注意的是，其他数据访问技术可能会遇到N +1 查询问题，因为它不仅限于 JPA 和 Hibernate。

## 5.总结

在本文中，我们讨论了使用 Hibernate 从数据库中获取对象属性的不同方法。

首先，我们通过一个例子讨论了惰性抓取。然后，我们更新了示例以使用预先获取并讨论了差异。

最后，我们展示了一种获取数据的额外方法并解释了它的优缺点。
## 1. 概述

在本教程中，我们将了解在 JPA 中创建一对一映射的不同方法。

我们需要对 Hibernate 框架有基本的了解，因此请查看我们[的 Hibernate 5 with Spring 指南以](https://www.baeldung.com/hibernate-5-spring) 了解更多背景知识。

## 延伸阅读：

## [JPA/Hibernate 级联类型概述](https://www.baeldung.com/jpa-cascade-types)

JPA/Hibernate 级联类型的快速实用概述。

[阅读更多](https://www.baeldung.com/jpa-cascade-types)→

## [Hibernate 一对多注解教程](https://www.baeldung.com/hibernate-one-to-many)

在本教程中，我们将通过一个实际示例了解使用 JPA 注解的一对多映射。

[阅读更多](https://www.baeldung.com/hibernate-one-to-many)→

## 2.说明

假设我们正在构建一个用户管理系统，我们的老板要求我们为每个用户存储一个邮寄地址。一个用户将有一个邮寄地址，一个邮寄地址将只有一个用户与之绑定。

这是一对一关系的示例，在本例中是 用户 和地址 实体之间的关系。

让我们看看我们如何在接下来的部分中实现它。

## 3.使用外键

### 3.1. 使用外键建模

让我们看一下下面的[ER 图](https://en.wikipedia.org/wiki/Entity–relationship_model)，它表示一个基于外键的一对一映射：

[![通过 address_id 外键将用户映射到地址的 ER 图](https://www.baeldung.com/wp-content/uploads/2018/12/1-1_FK.png)](https://www.baeldung.com/wp-content/uploads/2018/12/1-1_FK.png)

在此示例中， users中的address_id 列 是 address 的[外键](https://en.wikipedia.org/wiki/Foreign_key)。

### 3.2. 在 JPA 中使用外键实现

首先，让我们创建 User 类并对其进行适当的注解：

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //... 

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    // ... getters and setters
}

```

请注意，我们将 @OneToOne 注解放在相关的实体字段 Address上。

另外，我们需要放置[@JoinColumn ](https://www.baeldung.com/jpa-join-column)[注解](https://www.baeldung.com/jpa-join-column)来配置 users表中映射到 address表中主键的列[的 名称。](https://www.baeldung.com/jpa-join-column)如果我们不提供名称，Hibernate 将[遵循一些规则](http://docs.jboss.org/hibernate/jpa/2.2/api/javax/persistence/JoinColumn.html)来选择默认名称。

最后，请注意在下一个实体中我们不会在那里使用@JoinColumn 注解。这是因为我们只在外键关系的拥有方需要它。简单地说，谁拥有外键列，谁就获得 @JoinColumn注解。

Address 实体变得有点简单： 

```java
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //...

    @OneToOne(mappedBy = "address")
    private User user;

    //... getters and setters
}
```

我们还需要在这里放置 @OneToOne 注解。那是因为这是一个[双向关系](https://docs.oracle.com/cd/E19798-01/821-1841/bnbqj/index.html)。关系的地址方称为非拥有方。 

## 4.使用共享主键

### 4.1. 使用共享主键建模

在此策略中，我们不会创建新列 address_id，而是将address 表的主键列 ( user_id ) 标记为 users 表的外键：

[![用户绑定到地址的 ER 图，他们共享相同的主键值](https://www.baeldung.com/wp-content/uploads/2018/12/1-1-SK.png)](https://www.baeldung.com/wp-content/uploads/2018/12/1-1-SK.png)

我们利用这些实体之间具有一对一关系这一事实优化了存储空间。

### 4.2. 在 JPA 中使用共享主键实现

请注意，我们的定义仅略有变化：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    //...

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Address address;

    //... getters and setters
}
@Entity
@Table(name = "address")
public class Address {

    @Id
    @Column(name = "user_id")
    private Long id;

    //...

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
   
    //... getters and setters
}

```

由于外键现在存在于 地址 表中，因此mappedBy属性 现在已移至 User 类。我们还添加了@PrimaryKeyJoinColumn注解，它指示User实体的主键用作关联Address实体的外键值。

我们仍然需要在Address类中定义一个@Id字段。但请注意，这引用了user_id列，并且不再使用@GeneratedValue 注解。此外，在引用User 的字段上，我们添加了@MapsId注解，指示将从User实体主键值。

## 5.使用连接表

一对一映射可以有两种类型：可选的和强制的。到目前为止，我们只看到了强制关系。

现在让我们假设我们的员工与工作站相关联。它是一对一的，但有时员工可能没有工作站，反之亦然。

### 5.1. 使用连接表建模

到目前为止我们讨论的策略 迫使我们在列中放置空值以处理可选关系。

通常，我们在考虑连接表时会想到[多对多关系，](https://www.baeldung.com/jpa-many-to-many)但在这种情况下使用连接表可以帮助我们消除这些空值：

[![通过连接表将员工与工作站相关联的 ER 图](https://www.baeldung.com/wp-content/uploads/2018/12/1-1-JT.png)](https://www.baeldung.com/wp-content/uploads/2018/12/1-1-JT.png)

现在，每当我们建立关系时，我们都会在 emp_workstation 表中创建一个条目并完全避免空值 。

### 5.2. 在 JPA 中使用连接表实现

我们的第一个示例使用了 @JoinColumn。这一次，我们将使用@JoinTable：

```java
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    //...

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "emp_workstation", 
      joinColumns = 
        { @JoinColumn(name = "employee_id", referencedColumnName = "id") },
      inverseJoinColumns = 
        { @JoinColumn(name = "workstation_id", referencedColumnName = "id") })
    private WorkStation workStation;

    //... getters and setters
}
@Entity
@Table(name = "workstation")
public class WorkStation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    //...

    @OneToOne(mappedBy = "workStation")
    private Employee employee;

    //... getters and setters
}
```

[@JoinTable](http://docs.jboss.org/hibernate/jpa/2.2/api/javax/persistence/JoinTable.html) 指示 Hibernate 在维护关系的同时采用连接表策略[。](http://docs.jboss.org/hibernate/jpa/2.2/api/javax/persistence/JoinTable.html)

此外， Employee 是此关系的所有者，因为我们选择在其上使用连接表注解。

## 六. 总结

在本文中，我们了解了在 JPA 和 Hibernate 中维护一对一关联的不同方法，以及何时使用每种方法。
## 1. 概述

JPA 关系可以是单向的或双向的。这仅仅意味着我们可以将它们建模为一个关联实体或两者的属性。

定义实体间关系的方向对数据库映射没有影响。它仅定义我们在领域模型中使用该关系的方向。

对于双向关系，我们通常定义

-   拥有方
-   反向或参考侧

@JoinColumn注解帮助我们指定我们将用于加入实体关联或元素[集合](https://www.baeldung.com/jpa-join-column)的列。另一方面，mappedBy属性用于定义关系的引用端(非拥有端)。

在本快速教程中，我们将了解JPA 中@JoinColumn和mappedBy之间的区别。 我们还将介绍如何在一对多关联中使用它们。

## 2.初始设置

为了学习本教程，假设我们有两个实体：Employee和Email。

显然，一个员工可以有多个电子邮件地址。但是，给定的电子邮件地址可以完全属于单个员工。

这意味着它们共享一个一对多的关联：

[![员工 - 电子邮件](https://www.baeldung.com/wp-content/uploads/2018/11/12345789.png)](https://www.baeldung.com/wp-content/uploads/2018/11/12345789.png)

同样在我们的 RDBMS 模型中，我们将在我们的Email实体中有一个外键employee_id引用Employee的id属性。

## 3. @JoinColumn注解

在一对多/多对一关系中，拥有方通常定义在 关系的多方。通常是拥有外键的一方。

@JoinColumn注解定义了拥有方的实际物理映射：

```java
@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // ...

}
```

它只是意味着我们的Email实体将有一个名为employee_id的外键列，引用我们的Employee实体的主要属性id。

## 4.mappedBy属性_

一旦我们定义了关系的拥有方，Hibernate 就已经拥有了在我们的数据库中映射该关系所需的所有信息。

要使这种关联成为双向的，我们所要做的就是定义引用端。反向或引用端简单地映射到拥有端。

我们可以很容易地使用@OneToMany注解的mappedBy属性来做到这一点。

那么，让我们定义我们的Employee实体：

```java
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private List<Email> emails;
    
    // ...
}
```

在这里，mappedBy的值是所属方关联映射属性的名称。这样，我们现在已经在Employee和Email实体之间建立了双向关联。

## 5.总结

在本文中，我们了解了@JoinColumn和mappedBy之间的区别，以及如何在一对多双向关系中使用它们。

@JoinColumn注解定义了拥有方的实际物理映射。另一方面，引用端是使用@OneToMany注解的mappedBy属性定义的。
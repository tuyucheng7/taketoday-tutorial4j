## 1. 概述

在本快速教程中，我们将探索 JPA @Basic注解。我们还将讨论@Basic和@Column JPA 注解之间的区别。

## 2. 基本类型

JPA 支持各种Java数据类型作为实体的持久字段，通常称为基本类型。

基本类型直接映射到数据库中的列。这些包括Java基元及其包装类、String、java.math.BigInteger 和 java.math.BigDecimal、各种可用的日期时间类、枚举以及实现java.io.Serializable的任何其他类型。

与任何其他 ORM 供应商一样，Hibernate 维护基本类型的注册表并使用它来解析列的特定org.hibernate.type.Type。

## 3. @Basic注解

我们可以使用@Basic注解来标记一个基本类型的属性：

```java
@Entity
public class Course {

    @Basic
    @Id
    private int id;

    @Basic
    private String name;
    ...
}
```

换句话说，字段或属性上的@Basic注解表示它是基本类型，Hibernate 应该使用标准映射来持久化。

请注意，这是一个可选注解。因此，我们可以将我们的Course实体重写为：

```java
@Entity
public class Course {

    @Id
    private int id;

    private String name;
    ...
}
```

当我们没有为基本类型属性指定@Basic注解时，它是隐式假定的，并且应用此注解的默认值。

## 4. 为什么要使用@Basic注解？

@Basic注解有两个属性，optional和fetch 。让我们仔细看看每一个。

可选属性是一个布尔参数，用于定义标记的字段或属性是否允许null。它默认为true。因此，如果该字段不是原始类型，则默认情况下假定基础列可为空。

fetch属性接受枚举Fetch的成员，它指定标记的字段或属性应该延迟加载还是急切获取。它默认为FetchType.EAGER，但我们可以通过将其设置为 FetchType.LAZY 来允许延迟加载。

仅当我们将大型可序列化对象映射为基本类型时，延迟加载才有意义，因为在这种情况下，字段访问成本可能会很高。

我们有一个详细的教程，涵盖[了 Hibernate](https://www.baeldung.com/hibernate-lazy-eager-loading)中的预热/延迟加载，可以更深入地探讨该主题。

现在，假设不想让我们的Course名称为空值，并且还想延迟加载该属性。然后，我们将Course实体定义为：

```java
@Entity
public class Course {
    
    @Id
    private int id;
    
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String name;
    ...
}
```

当我们愿意偏离可选参数和获取 参数的默认值时，我们应该明确地使用@Basic注解。我们可以根据需要指定其中一个或两个属性。

## 5. JPA @Basic与@Column

我们来看看@Basic和@Column注解的区别：

-   @Basic注解的属性应用于 JPA 实体，而@Column的属性 应用于数据库列
-   @Basic注解的可选属性定义实体字段是否可以为null；另一方面，@Column注解的nullable属性指定对应的数据库列是否可以为null
-   我们可以使用@Basic来指示应该延迟加载一个字段
-   @Column注解允许我们指定映射数据库列的名称

## 六. 总结

在本文中，我们了解了何时以及如何使用 JPA 的@Basic 注解。我们还讨论了它与@Column注解的不同之处。
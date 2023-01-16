## 1. 概述

[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)使我们的Java应用程序处理关系数据库模型变得不那么痛苦。当我们将每个表映射到单个实体类时，事情就简单了。

但有时我们有理由对我们的[实体](https://www.baeldung.com/jpa-entities)和表进行不同的建模：

-   当我们想要创建字段的逻辑组时，我们可以将[多个类映射到单个表](https://www.baeldung.com/jpa-embedded-embeddable)。
-   如果涉及继承，我们可以将[类层次结构映射到表结构](https://www.baeldung.com/hibernate-inheritance)。
-   在相关字段分散在多个表之间并且我们希望使用单个类对这些表进行建模的情况下

在这个简短的教程中，我们将了解如何处理最后一个场景。

## 2. 数据模型

假设我们经营一家餐厅，我们想要存储我们提供的每顿饭的数据：

-   姓名
-   描述
-   价格
-   它含有什么样的过敏原

由于有许多可能的过敏原，我们将把这个数据集分组在一起。

此外，我们还将使用以下表定义对此进行建模：

[![膳食](https://www.baeldung.com/wp-content/uploads/2019/10/meals.png)](https://www.baeldung.com/wp-content/uploads/2019/10/meals.png)

现在让我们看看如何使用标准 JPA 注解将这些表映射到实体。

## 3.创建多个实体

最明显的解决方案是为这两个类创建一个实体。

让我们从定义Meal实体开始：

```java
@Entity
@Table(name = "meal")
class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    BigDecimal price;

    @OneToOne(mappedBy = "meal")
    Allergens allergens;

    // standard getters and setters
}
```

接下来，我们将添加Allergens实体：

```java
@Entity
@Table(name = "allergens")
class Allergens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    Long mealId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "meal_id")
    Meal meal;

    @Column(name = "peanuts")
    boolean peanuts;

    @Column(name = "celery")
    boolean celery;

    @Column(name = "sesame_seeds")
    boolean sesameSeeds;

    // standard getters and setters
}
```

我们可以看到meal_id既是主键也是外键。这意味着我们需要使用@PrimaryKeyJoinColumn定义一对一关系列。

但是，这个解决方案有两个问题：

-   我们总是想为一顿饭储存过敏原，而这个解决方案并不强制执行这个规则。
-   膳食和过敏原数据在逻辑上属于一起。因此，我们可能希望将这些信息存储在同一个Java类中，即使我们为它们创建了多个表。

第一个问题的一个可能解决方案是将@NotNull注解添加到Meal实体的allergens字段中。如果我们有一个null Allergens，JPA 不会让我们保留这顿饭。

然而，这不是一个理想的解决方案。我们想要一个更严格的限制，我们甚至没有机会尝试在没有过敏原的情况下坚持用餐。

## 4. 使用@SecondaryTable创建单个实体

我们可以使用@SecondaryTable注解创建一个实体，指定我们在不同表中有列：

```java
@Entity
@Table(name = "meal")
@SecondaryTable(name = "allergens", pkJoinColumns = @PrimaryKeyJoinColumn(name = "meal_id"))
class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "peanuts", table = "allergens")
    boolean peanuts;

    @Column(name = "celery", table = "allergens")
    boolean celery;

    @Column(name = "sesame_seeds", table = "allergens")
    boolean sesameSeeds;

    // standard getters and setters

}
```

在幕后，JPA 将主表与辅助表连接起来并填充字段。此解决方案类似于@OneToOne关系，但通过这种方式，我们可以在同一个类中拥有所有属性。

重要的是要注意， 如果我们在辅助表中有一个列，我们必须使用@Column注解的表参数来指定它。如果列在主表中，我们可以省略表参数，因为 JPA 默认在主表中查找列。

另请注意，如果我们将它们嵌入@SecondaryTables中，我们可以有多个辅助表。或者，从Java8 开始，我们可以使用多个@SecondaryTable注解来标记实体，因为它是[可重复的注解](https://www.baeldung.com/java-default-annotations)。

## 5.结合@SecondaryTable和@Embedded

正如我们所见，@SecondaryTable将多个表映射到同一个实体。我们也知道@Embedded和@Embeddable做相反的事情，[将单个表映射到多个类](https://www.baeldung.com/jpa-embedded-embeddable)。

让我们看看将@SecondaryTable与@Embedded和@Embeddable结合使用会得到什么：

```java
@Entity
@Table(name = "meal")
@SecondaryTable(name = "allergens", pkJoinColumns = @PrimaryKeyJoinColumn(name = "meal_id"))
class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    BigDecimal price;

    @Embedded
    Allergens allergens;

    // standard getters and setters

}

@Embeddable
class Allergens {

    @Column(name = "peanuts", table = "allergens")
    boolean peanuts;

    @Column(name = "celery", table = "allergens")
    boolean celery;

    @Column(name = "sesame_seeds", table = "allergens")
    boolean sesameSeeds;

    // standard getters and setters

}
```

这与我们使用@OneToOne看到的方法类似。但是，它有几个优点：

-   JPA帮我们把两张桌子统一管理起来，所以我们可以确定两张桌子每顿饭都会有一行。
-   此外，代码更简单一些，因为我们需要的配置更少。

然而，这种一对一的解决方案仅在两个表具有匹配的 id 时才有效。

值得一提的是，如果我们要复用Allergens类，最好在Meal类中用@AttributeOverride定义副表的列。

## 六. 总结

在这个简短的教程中，我们了解了如何使用@SecondaryTable JPA 注解将多个表映射到同一实体。

我们还看到了将@SecondaryTable与@Embedded和@Embeddable结合使用以获得类似于一对一关系的优势。
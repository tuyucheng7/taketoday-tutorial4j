## 1. 概述

在许多以数据为中心的应用程序中，可能会出现需要检查特定对象是否已经存在的情况。

在本教程中，我们将讨论使用Spring Data和JPA实现此目的的几种方法。

## 2. 实体类

为了为我们的示例做准备，我们将创建一个具有两个属性model和power的实体Car：

```java
@Entity
public class Car {

    @Id
    @GeneratedValue
    private int id;
    private Integer power;
    private String model;
    // constructors, getters, setters ...
}
```

## 3. 通过ID查找

JpaRepository接口公开了existsById方法，该方法检查数据库中是否存在具有给定id的实体：

```java
int searchId = 2; // ID of the Car
boolean exists = repository.existsById(searchId)
```

假设searchId是我们在测试设置期间创建的Car的id。为了测试的可重复性，我们永远不应该使用硬编码的数字(例如“2”)，因为Car的id属性很可能是自动生成的，并且可能会随着时间的推移而变化。**existsById查询是检查对象是否存在的最简单但最不灵活的方法**。

## 4. 使用派生查询方法

我们还可以使用Spring的派生查询方法特性来自定义我们的查询。在我们的示例中，我们要检查是否存在具有给定型号名称的Car，因此我们设计了以下查询方法：

```java
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    boolean existsCarByModel(String model);
}
```

**需要注意的是，方法的命名不是任意的，它必须遵循一定的规则**。然后Spring将为Repository生成代理，以便它可以从方法名称派生SQL查询。像IntelliJ IDEA这样的现代化IDE提供自动语法补全提示。

当查询变得更复杂时，例如合并排序、limit以及包括多个查询条件，**这些方法名称可能会变得很长，直至难以辨认**。此外，派生查询方法也可能看起来很神奇，因为它们具有隐含和“约定俗成”的性质。

尽管如此，当干净整洁的代码很重要以及开发人员想要依赖经过良好测试的框架时，它们可以派上用场。

## 5. 使用Example API

Example是一种非常强大的检查存在性的方法，因为它使用ExampleMatcher来动态构建查询。因此，每当我们需要动态性时，这是一个很好的方法。关于Spring ExampleMatcher的全面解释以及如何使用它们可以在我们的[Spring Data Query](https://www.baeldung.com/spring-data-query-by-example)文章中找到。

### 5.1 Matcher

假设我们要以不区分大小写的方式搜索model名称。让我们从创建ExampleMatcher开始：

```java
ExampleMatcher modelMatcher = ExampleMatcher.matching()
    .withIgnorePaths("id")
    .withMatcher("model", ignoreCase());
```

请注意，我们必须明确忽略id路径，因为id是主键，默认情况下会自动获取这些路径。

### 5.2 probe

接下来，我们需要定义一个所谓的“探头”，它是我们要查找的类的一个实例。它具有所有与搜索相关的属性集。然后我们将它连接到我们的nameMatcher并执行查询：

```java
Car probe = new Car();
probe.setModel("bmw");
Example<Car> example = Example.of(probe, modelMatcher);
assertThat(repository.exists(example)).isTrue();
```

极大的灵活性带来了极大的复杂性，尽管ExampleMatcher API可能非常强大，但使用它会产生相当多的额外代码。**我们建议在动态查询中使用它，或者如果没有其他方法满足我们的需求**。

## 6. 使用Exists语义编写自定义JPQL查询

我们将研究的最后一种方法使用JPQL(Java持久性查询语言)来实现具有存在性语义的自定义查询：

```java
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("select case when count(c)> 0 then true else false end from Car c where c.model = :model")
    boolean existsCarExactCustomQuery(@Param("model") String model);
}
```

**这个想法是基于model属性执行不区分大小写的计数查询，计算返回值，并将结果映射到Java布尔值**。同样，大多数IDE对JPQL语句都有很好的支持。

[自定义JPQL查询](https://www.baeldung.com/spring-data-jpa-query)可以被视为派生方法的替代方案，当我们熟悉类似SQL的语句时，它通常是一个不错的选择。

## 7. 总结

在本文中，我们学习了如何使用Spring Data和JPA检查数据库中是否存在对象。何时使用哪种方法并没有硬性规定，因为它在很大程度上取决于手头上的用例和个人喜好。

不过，作为一个好的经验法则，如果可以选择，出于稳健性、性能和代码清晰度的原因，开发人员应该始终倾向于更直接的方法。此外，一旦决定使用派生查询或自定义JPQL查询，最好尽可能长时间地坚持这种选择以确保一致的编码风格。
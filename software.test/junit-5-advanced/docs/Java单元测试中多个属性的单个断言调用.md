## 1. 概述

作为程序员，我们经常编写测试以确保我们的代码按预期工作。测试中的标准做法之一是使用断言。

当我们想要验证一个对象的多个属性时，我们可以编写一堆断言来完成工作。

但是，在本教程中，我们将探讨如何在单个断言调用中验证多个属性。

## 2. 问题简介

在很多情况下，我们需要检查一个对象的多个属性。传统上，这意味着为每个属性编写单独的断言语句，这会使代码冗长且难以阅读。

但是，更好的方法是对多个属性使用单个断言调用。那么接下来，让我们看看它是如何完成的。

为了更直接的演示，首先，我们以一个[POJO](https://www.baeldung.com/java-pojo-class)类为例：

```java
class Product {
    private Long id;
    private String name;
    private String description;
    private boolean onSale;
    private BigDecimal price;
    private int stockQuantity;

    // constructor with all properties is omitted

    // getters and setters are omitted
}
```

Product类有6个属性。假设我们已经实现了一个程序来生成Product实例。通常，我们将生成的Product实例与预期对象进行比较，以断言程序是否有效，例如assertEquals(EXPECTED_PRODUCT, myProgram.createProduct())。

但是，在我们的程序中，id和description是不可预测的。换句话说，**如果我们可以验证其余4个字段(name、onSale、price和stockQuantity)包含预期值，我们就认为程序正确地完成了工作**。

接下来，让我们创建一个Product对象作为预期结果：

```java
Product EXPECTED = new Product(42L, "LG Monitor", "32 inches, 4K Resolution, Ideal for programmers", true, new BigDecimal("429.99"), 77);
```

为简单起见，我们不会真正实现创建Product对象的方法。相反，让我们简单地创建一个Product实例来保存所需的值，因为我们的重点是如何在一个语句中断言这4个属性：

```java
Product TO_BE_TESTED = new Product(-1L, "LG Monitor", "dummy value: whatever", true, new BigDecimal("429.99"), 77);
```

那么接下来，让我们看看如何组织断言。

## 3. 使用JUnit 5的assertAll()

[JUnit](https://www.baeldung.com/junit-5)是最流行的单元测试框架之一。最新版本JUnit 5带来了许多新特性。例如，[assertAll()](https://www.baeldung.com/junit-assertions#junit5-assertAll)就是其中之一。

**JUnit 5的assertAll()方法接收一个断言列表，所有断言都将在一次调用中执行**。此外，如果任何断言失败，则测试将失败，并且将报告所有失败。

接下来，让我们将属性断言组合到一个assertAll()调用中：

```java
assertAll("Verify Product properties",
    () -> assertEquals(EXPECTED.getName(), TO_BE_TESTED.getName()),
    () -> assertEquals(EXPECTED.isOnSale(), TO_BE_TESTED.isOnSale()),
    () -> assertEquals(EXPECTED.getStockQuantity(), TO_BE_TESTED.getStockQuantity()),
    () -> assertEquals(EXPECTED.getPrice(), TO_BE_TESTED.getPrice()));
```

正如我们所见，assertAll()方法在一次调用中组合了4个断言。值得一提的是，price字段的类型是BigDecimal。**我们使用assertEquals()来验证BigDecimal对象的值和小数位数**。

我们已经实现了我们的目标。但是，如果我们仔细查看代码，在assertAll()体内，我们仍然有4个断言，即使它们是[lambda表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)格式。因此，代码还是有点冗长。

接下来，让我们看看在一次调用中断言这4个属性的其他方法。

## 4. 使用AssertJ的extracting()和containsExactly()

[AssertJ](https://www.baeldung.com/introduction-to-assertj)是一个功能强大的Java库，它提供了流式且直观的API，用于在测试中编写断言。它提供了extracting()方法，**允许我们从一个对象中提取我们需要的属性值**。提取的值存储在列表中。然后，AssertJ提供了其他方法来验证列表。例如，**我们可以使用containsExactly()来验证实际组是否按顺序完全包含给定的值，而不是其他任何值**。

接下来，让我们组装extracting()和containsExactly()：

```java
assertThat(TO_BE_TESTED)
    .extracting("name", "onSale", "stockQuantity", "price")
    .containsExactly(EXPECTED.getName(), EXPECTED.isOnSale(), EXPECTED.getStockQuantity(), EXPECTED.getPrice());
```

正如我们所看到的，AssertJ的extracting()和containsExactly()允许我们编写更简洁和更具表现力的断言。

如上面的代码所示，将属性名称作为字符串传递给extracting()方法非常简单。但是，由于名称是纯字符串，因此它们可能包含拼写错误。此外，如果我们重命名属性，测试方法仍然可以毫无问题地编译。在运行测试之前，我们不会看到问题。另外，最终找到命名问题可能需要一些时间。

因此，AssertJ支持将getter[方法引用](https://www.baeldung.com/java-method-references)而不是属性名称传递给extracting()：

```java
assertThat(TO_BE_TESTED)
    .extracting(Product::getName, Product::isOnSale, Product::getStockQuantity,Product::getPrice)
    .containsExactly(EXPECTED.getName(), EXPECTED.isOnSale(), EXPECTED.getStockQuantity(), EXPECTED.getPrice());
```

## 5. 使用AssertJ的returns()和from()

AssertJ为各种需求提供了一组丰富的断言。我们已经学会了使用extracting()和containsExactly()在一次断言调用中验证多个属性。在我们的示例中，我们将检查4个属性。然而，我们可能想要验证现实世界中的10个属性。随着待检查属性数量的增加，断言行变得难以阅读。另外，编写如此长的断言行容易出错。

接下来，让我们看看使用AssertJ的returns()和from()方法的替代方法。用法非常简单：assertThat(ToBeTestedObject).returns(Expected, from(FunctionToGetTheValue))。

**因此，returns()方法验证被测对象是否从给定函数FunctionToGetTheValue返回预期值**。

接下来，让我们应用这种方法来验证Product对象：

```java
assertThat(TO_BE_TESTED)
    .returns(EXPECTED.getName(), from(Product::getName))
    .returns(EXPECTED.isOnSale(), from(Product::isOnSale))
    .returns(EXPECTED.getStockQuantity(), from(Product::getStockQuantity))
    .returns(EXPECTED.getPrice(), from(Product::getPrice));
```

正如我们所见，代码流式且易于阅读。此外，即使我们需要验证许多属性，我们也不会迷路。

值得一提的是，AssertJ提供了doesNotReturn()方法来验证from()结果是否与预期值匹配。此外，**我们可以在同一个断言中使用doesNotReturn()和returns()**。

最后，让我们编写一个混合了returns()和doesNotReturn()方法的单行断言：

```java
assertThat(TO_BE_TESTED)
    .returns(EXPECTED.getName(), from(Product::getName))
    .returns(EXPECTED.isOnSale(), from(Product::isOnSale))
    .returns(EXPECTED.getStockQuantity(), from(Product::getStockQuantity))
    .returns(EXPECTED.getPrice(), from(Product::getPrice))
    .doesNotReturn(EXPECTED.getId(), from(Product::getId))
    .doesNotReturn(EXPECTED.getDescription(), from(Product::getDescription));
```

## 6. 总结

使用单个断言调用来测试多个属性提供了许多好处，例如提高可读性、不易出错、更好的可维护性等。

在本文中，我们通过示例学习了三种在单次断言调用中验证多个属性的方法：

- JUnit 5：assertAll()
- AssertJ：extracting()和containsExactly()
- AssertJ：returns()、doesNotReturn()和from()
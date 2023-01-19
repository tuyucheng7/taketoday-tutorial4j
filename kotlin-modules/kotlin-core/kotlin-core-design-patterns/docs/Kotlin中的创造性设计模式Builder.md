## 一、简介

在这篇简短的文章中，我们将了解如何在 Kotlin 中实现构建器设计模式。

## 2.建造者模式

Builder 模式是人们经常使用但很少自己创建的模式。

处理可能包含大量参数的对象的构建以及当我们希望在完成构建后使对象不可变时，这非常好。

要了解更多信息，请在[此处](https://www.baeldung.com/creational-design-patterns)查看我们关于创建设计模式的教程。

## 3.实施

Kotlin 提供了许多有用的特性，例如命名参数和默认参数、apply()和数据类，它们避免了使用经典的构建器模式实现。

出于这个原因，我们将首先看到一个经典的 Java 风格的实现，然后是一个更 Kotlin 风格的简短形式。

### 3.1. Java 风格的实现

让我们开始创建一个类——FoodOrder——它包含只读字段，因为我们不希望外部对象直接访问它们：

```java
class FoodOrder private constructor(builder: FoodOrder.Builder) {

    val bread: String?
    val condiments: String?
    val meat: String?
    val fish: String?

    init {
        this.bread = builder.bread
        this.condiments = builder.condiments
        this.meat = builder.meat
        this.fish = builder.fish
    }

    class Builder {
        // builder code
    }
}
```

请注意，构造函数是私有的，因此只有嵌套的Builder类才能访问它。

现在让我们继续创建将用于构建对象的嵌套类：

```java
class Builder {

    var bread: String? = null
      private set
    var condiments: String? = null
      private set
    var meat: String? = null
      private set
    var fish: String? = null
      private set

    fun bread(bread: String) = apply { this.bread = bread }
    fun condiments(condiments: String) = apply { this.condiments = condiments }
    fun meat(meat: String) = apply { this.meat = meat }
    fun fish(fish: String) = apply { this.fish = fish }
    fun build() = FoodOrder(this)
}

```

如我们所见，我们的Builder具有与外部类相同的字段。对于每个外部字段，我们都有一个匹配的设置方法。

如果我们有一个或多个必填字段，而不是使用 setter 方法，让我们创建一个构造函数来设置它们。

请注意，我们使用apply函数是为了支持[流畅的设计](https://en.wikipedia.org/wiki/Fluent_interface)方法。

最后，通过构建方法，我们调用FoodOrder构造函数。

### 3.2. Kotlin 风格的实现

为了充分利用 Kotlin，我们必须重新审视我们在 Java 中习惯的一些最佳实践。其中许多可以用更好的替代品代替。

让我们看看如何编写惯用的 Kotlin 代码：

```java
class FoodOrder private constructor(
  val bread: String?,
  val condiments: String?,
  val meat: String?,
  val fish: String?) {

    data class Builder(
      var bread: String? = null,
      var condiments: String? = null,
      var meat: String? = null,
      var fish: String? = null) {

        fun bread(bread: String) = apply { this.bread = bread }
        fun condiments(condiments: String) = apply { this.condiments = condiments }
        fun meat(meat: String) = apply { this.meat = meat }
        fun fish(fish: String) = apply { this.fish = fish }
        fun build() = FoodOrder(bread, condiments, meat, fish)
    }
}
```

Kotlin 带有命名参数和默认参数，有助于最大程度地减少重载次数并提高函数调用的可读性。

我们还可以利用 Kotlin 的数据类结构，我们将在[此处](https://www.baeldung.com/kotlin-data-classes)的另一个教程中进行更多探索。

最后，与 Java 风格的实现一样，apply()对于实现流畅的 setter 很有用。

## 4. 用法举例

简单地说，让我们看一下如何使用这些构建器模式实现来构建FoodOrder对象：

```java
val foodOrder = FoodOrder.Builder()
  .bread("white bread")
  .meat("bacon")
  .condiments("olive oil")
  .build()

```

## 5.总结

Builder Pattern 解决了面向对象编程中一个非常普遍的问题，即如何在不编写许多构造函数的情况下灵活地创建一个不可变对象。

在考虑建筑商时，我们应该关注建筑是否复杂。如果我们的构建模式太简单，那么创建灵活的构建器对象的工作量可能会远远超过收益。
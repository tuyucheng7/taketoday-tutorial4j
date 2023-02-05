## 1. 概述

在本文中，我们将了解如何在Java 8中实现策略设计模式。

首先，我们简要概述该模式的定义，并解释在旧版本的Java中如何以传统的方式实现该模式。

接下来，我们试着使用Java 8的lambda实现该模式，进行重构。

## 2. 策略模式

**从本质上讲，策略模式允许我们在运行时改变算法的行为**。

通常，我们会从一个用于应用算法的接口开始，然后对每个可能的算法多次实现该接口。

假设我们需要根据当天是双11、国庆节还是春节，对商品应用不同类型的折扣。首先，让我们创建一个Discounter接口，它将由我们的每个节日折扣策略实现：

```java
public interface Discounter {
    BigDecimal applyDiscount(BigDecimal amount);
}
```

然后假设我们想在国庆节应用50%的折扣，在春节应用10%的折扣。让我们为这些策略中的每一个实现我们的接口：

```java
public class NationalDiscounter implements Discounter {

	@Override
	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(0.5));
	}
}
```

```java
public class SpringFestivalDiscounter implements Discounter {

	@Override
	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(0.9));
	}
}
```

最后，让我们在测试中尝试一种策略：

```java
Discounter nationalDiscounter = new EasterDiscounter();

BigDecimal discountedValue = nationalDiscounter.applyDiscount(BigDecimal.valueOf(100));

assertThat(discountedValue).isEqualByComparingTo(BigDecimal.valueOf(50));
```

上面的代码很有效，但问题是必须为每个策略创建一个具体的类，这可能有点痛苦。另一种方法是使用匿名内部类，但这仍然非常冗长，而且并不比以前的解决方案方便多少：

```java
Discounter nationalDiscounter = new Discounter() {
    @Override
    public BigDecimal applyDiscount(final BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.5));
    }
};
```

## 3. 利用Java 8

自从Java 8发布以来，lambdas的引入使匿名内部类或多或少变得多余。这意味着创建策略接口的实现现在变得更加简洁和容易。

此外，函数式编程的声明式风格允许我们能够实现以前不可能实现的模式。

### 3.1 减少代码冗长

让我们尝试创建一个内联NationalDiscounter，只是这次我们使用的是lambda表达式：

```java
Discounter nationalDiscounter = amount -> amount.multiply(BigDecimal.valueOf(0.5));
```

正如我们所看到的，我们的代码现在变得更简洁、更易于维护，同时实现了与以前相同的效果，但只需一行代码。**本质上，lambda可以被视为匿名内部类型的替代品**。

当我们想要在行中声明更多的Discounter时，这种优势变得更加明显：

```java
List<Discounter> discounters = newArrayList(
    amount -> amount.multiply(BigDecimal.valueOf(0.9)),
    amount -> amount.multiply(BigDecimal.valueOf(0.8)),
    amount -> amount.multiply(BigDecimal.valueOf(0.5))
);
```

当我们想要定义很多Discounter时，我们可以在一个地方静态地声明它们。如果我们愿意，Java 8甚至允许我们在接口中定义静态方法。

因此，与其在具体类或匿名内部类之间进行选择，不如尝试在单个类中创建所有lambda：

```java
public interface Discounter {

	BigDecimal applyDiscount(BigDecimal amount);

	static Discounter springFestival() {
		return (amount) -> amount.multiply(BigDecimal.valueOf(0.9));
	}

	static Discounter newYear() {
		return (amount) -> amount.multiply(BigDecimal.valueOf(0.8));
	}

	static Discounter national() {
		return (amount) -> amount.multiply(BigDecimal.valueOf(0.5));
	}
}
```

正如我们所看到的，我们在极少的代码中实现了很多功能。

### 3.2 利用函数组合

让我们修改Discounter接口，使其扩展UnaryOperator接口，然后添加一个combine()方法：

```java
public interface Discounter extends UnaryOperator<BigDecimal> {
    default Discounter combine(Discounter after) {
        return value -> after.apply(this.apply(value));
    }
}
```

本质上，我们正在重构我们的Discounter并利用一个事实，即应用折扣是一个将BigDecimal实例转换为另一个BigDecimal实例的函数，允许我们访问预定义的方法。**由于UnaryOperator附带有一个apply()方法，我们可以用它替换applyDiscount**。

combine()方法只是将一个Discounter应用于this的结果的一种抽象。它使用内置函数apply()来实现这一点。

现在，让我们尝试将多个折扣累积应用于一个金额。我们将通过使用函数reduce()和combine()来做到这一点：

```java
Discounter combinedDiscounter = discounters
    .stream()
    .reduce(v -> v, Discounter::combine);

combinedDiscounter.apply(...);
```

请特别注意reduce方法的第一个参数，当没有提供折扣时，我们需要返回不变的值。这可以通过提供一个identity函数作为默认折扣来实现。

这是执行标准迭代的有用且不那么冗长的替代方法。如果我们考虑开箱即用的函数式组合方法，它还免费为我们提供了更多功能。

## 4. 总结

在本文中，我们解释了策略模式，并演示了如何通过lambda表达式以不那么冗长的方式实现它。
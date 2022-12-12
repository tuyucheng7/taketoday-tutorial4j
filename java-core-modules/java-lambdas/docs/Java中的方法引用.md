## 1. 概述

Java 8中最受欢迎的变化之一是引入了[lambda表达式]()，因为它们允许我们放弃匿名类，大大减少了样板代码并提高了可读性。

**方法引用是一种特殊类型的lambda表达式**，它们通常用于通过引用现有方法来创建简单的lambda表达式。

有四种类型的方法引用：

-   静态方法
-   特定对象的实例方法
-   特定类型的任意对象的实例方法
-   构造器

在本教程中，我们将探索Java中的方法引用。

## 2. 引用静态方法

我们从一个非常简单的示例开始，将字符串集合的元素转化为大写并打印：

```java
List<String> messages = Arrays.asList("hello", "tuyucheng", "readers!");
```

我们可以通过利用一个简单的lambda表达式直接调用[StringUtils.capitalize()](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#capitalize-java.lang.String-)方法来实现这一点：

```java
messages.forEach(word -> StringUtils.capitalize(word));
```

或者，我们可以使用方法引用来简单地引用capitalize静态方法：

```java
messages.forEach(StringUtils::capitalize);
```

**请注意，方法引用始终使用::运算符**。

## 3. 引用特定对象的实例方法

为了演示这种类型的方法引用，让我们考虑两个类：

```java
public class Bicycle {

    private String brand;
    private Integer frameSize;
    // standard constructor, getters and setters
}

public class BicycleComparator implements Comparator {

    @Override
    public int compare(Bicycle a, Bicycle b) {
        return a.getFrameSize().compareTo(b.getFrameSize());
    }
}
```

然后，我们创建一个BicycleComparator对象来比较自行车的frameSize：

```java
BicycleComparator bikeFrameSizeComparator = new BicycleComparator();
```

我们可以使用lambda表达式按frameSize对自行车进行排序，但我们需要指定两个自行车进行比较：

```java
createBicyclesList().stream()
	.sorted((a, b) -> bikeFrameSizeComparator.compare(a, b));
```

相反，我们可以使用方法引用让编译器为我们处理参数传递：

```java
createBicyclesList().stream()
	.sorted(bikeFrameSizeComparator::compare);
```

方法引用更清晰、更易读，因为代码清楚地表明了我们的意图。

## 4. 引用特定类型的任意对象的实例方法

这种类型的方法引用类似于前面的示例，但无需创建自定义对象来执行比较。

让我们创建一个要排序的整数集合：

```java
List<Integer> numbers = Arrays.asList(5, 3, 50, 24, 40, 2, 9, 18);
```

如果我们使用典型的lambda表达式，则需要显式传递这两个参数，而使用方法引用则更为直接：

```java
numbers.stream()
	.sorted((a, b) -> a.compareTo(b));
numbers.stream()
	.sorted(Integer::compareTo);
```

尽管它仍然是单行代码，但方法引用更易于阅读和理解。

## 5. 引用构造函数

我们可以像在第一个示例中引用静态方法一样引用构造函数，唯一的区别是在这里我们使用new关键字。

让我们用不同品牌的字符串集合创建一个Bicycle数组：

```java
List<String> bikeBrands = Arrays.asList("Giant", "Scott", "Trek", "GT");
```

首先，我们向Bicycle类添加一个新的构造函数：

```java
public Bicycle(String brand) {
    this.brand = brand;
    this.frameSize = 0;
}
```

接下来，我们将使用方法引用中的新构造函数，并从原始String集合创建一个Bicycle数组：

```java
bikeBrands.stream()
	.map(Bicycle::new)
	.toArray(Bicycle[]::new);
```

请注意我们如何使用方法引用同时调用Bicycle和Array构造函数，从而使我们的代码看起来更加简洁明了。

## 6. 其他示例和限制

正如我们到目前为止所看到的，方法引用是使我们的代码和意图非常清晰和可读的好方法。但是，我们不能使用它们来代替各种lambda表达式，因为它们有一些局限性。

其中的主要限制是它们最大优势的结果：前一个表达式的输出需要与引用方法签名的输入参数相匹配。

让我们看一下此限制的示例：

```java
createBicyclesList().forEach(b -> System.out.printf(
	"Bike brand is '%s' and frame size is '%d'%n",
	b.getBrand(),
	b.getFrameSize()));
```

这种简单的情况不能用方法引用来表达，因为在我们的例子中printf方法需要3个参数，而使用createBicyclesList().forEach()将只允许方法引用推断一个参数(Bicycle对象)。

最后，让我们探讨如何创建一个可以从lambda表达式引用的无操作函数。

在这种情况下，我们希望使用lambda表达式而不使用它的参数。

首先，我们创建doNothingAtAll方法：

```java
private static <T> void doNothingAtAll(Object... o) {
}
```

由于它是[可变参数]()方法，因此它适用于任何lambda表达式，无论引用的对象或推断的参数数量如何。

现在，让我们看看它的实际效果：

```java
createBicyclesList()
	.forEach((o) -> MethodReferenceExamples.doNothingAtAll(o));
```

## 7. 总结

在本快速教程中，我们了解了Java中的方法引用是什么以及如何使用它们来替换lambda表达式，从而提高可读性并阐明程序员的意图。
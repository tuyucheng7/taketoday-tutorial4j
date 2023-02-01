## 1. 概述

Java SE 17的发布引入了对随机数生成API的更新-[JEP 356](https://openjdk.java.net/jeps/356)。

通过这次API更新，**引入了新的接口类型，以及轻松列出、查找和实例化生成器工厂的方法**。此外，现在可以使用一组新的随机数生成器实现。

在本教程中，我们将新的RandomGenerator API和旧的Random API进行比较，我们将查看列出所有可用的生成器工厂并根据其名称或属性选择生成器。

我们还将探讨新API的线程安全性和性能。

## 2. 旧的Random API

首先，让我们看一下Java基于Random类生成随机数的旧API。

### 2.1 API设计

原始API由四个不包含接口的类组成：

<img src="../assets/img.png">

### 2.2 Random

最常用的随机数生成器是java.util包中的Random。

要生成随机数流，我们需要**创建一个随机数生成器类的实例-Random**：

```java
Random random = new Random();
int number = random.nextInt(10);
assertThat(number).isPositive().isLessThan(10);
```

在这里，默认构造函数将随机数生成器的种子设置为一个很可能不同于任何其他调用的值。

### 2.3 备选方案

除了java.util.Random之外，**还可以使用三个替代生成器来解决线程安全和安全问题**。

默认情况下，Random的所有实例都是线程安全的，但是，跨线程并发使用同一个实例可能会导致性能不佳。因此，java.util.concurrent包中的ThreadLocalRandom类是多线程系统的首选。

由于Random实例不是加密安全的，因此SecureRandom类使我们能够创建用于安全敏感上下文的生成器。

最后，java.util包中的SplittableRandom类针对并行流和fork/join样式的计算进行了优化。

## 3. 新的随机生成器API

现在，让我们看一下基于RandomGenerator接口的新API。

### 3.1 API设计

新的API通过**新的接口类型和生成器实现**提供了更好的整体设计：

<img src="../assets/img_1.png">

在上图中，我们可以看到旧的API类是如何适应新设计的。在这些类型之上，添加了几个随机数生成器实现类：

-   Xoroshiro group
    -   Xoroshiro128PlusPlus
-   Xoshiro group
    -   Xoshiro256PlusPlus
-   LXM group
    -   L128X1024MixRandom
    -   L128X128MixRandom
    -   L128X256MixRandom
    -   L32X64MixRandom
    -   L64X1024MixRandom
    -   L64X128MixRandom
    -   L64X128StarStarRandom
    -   L64X256MixRandom

### 3.2 改进领域

**旧的API中缺少接口**，这使得在不同的生成器实现之间切换变得更加困难。因此，第三方很难提供自己的实现。

例如，SplittableRandom完全脱离了API的其余部分，尽管它的某些代码片段与Random完全相同。

因此，新的RandomGenerator API的主要目标是：

-   确保不同算法更容易互换
-   更好地支持基于流的编程
-   消除现有类中的代码重复
-   保留旧Random API的现有行为

### 3.3 新接口

新的顶层接口**RandomGenerator为所有现有的和新的生成器提供了统一的API**。

它定义了返回随机选择的不同类型值以及随机选择值的流的方法。

新的API提供了额外的四个新的专用生成器接口：

-   SplitableGenerator允许创建一个新的生成器作为当前生成器的后代
-   JumpableGenerator允许向前跳跃适度数量的平局
-   LeapableGenerator允许跳转大量绘制
-   ArbitrarilyJumpableGenerator为LeapableGenerator添加跳跃距离

## 4. 随机生成器工厂

新API中提供了用于生成特定算法的多个随机数生成器的工厂类。

### 4.1 获取所有

RandomGeneratorFactory的all方法生成所有可用生成器工厂的非空流。

**我们可以使用它来打印所有已注册的生成器工厂并检查其算法的属性**：

```java
RandomGeneratorFactory.all()
    .sorted(Comparator.comparing(RandomGeneratorFactory::name))
    .forEach(factory -> System.out.println(String.format("%s\t%s\t%s\t%s",
        factory.group(),
        factory.name(),
        factory.isJumpable(),
        factory.isSplittable())));
```

工厂的可用性取决于通过服务提供者API定位RandomGenerator接口的实现。

### 4.2 按属性查找

我们还可以使用all方法**通过随机数生成器算法的属性查询工厂**：

```java
RandomGeneratorFactory.all()
    .filter(RandomGeneratorFactory::isJumpable)
    .findAny()
    .map(RandomGeneratorFactory::create)
    .orElseThrow(() -> new RuntimeException("Error creating a generator"));
```

因此，使用Stream API，我们可以找到满足我们需求的工厂，然后使用它来创建生成器。

## 5. RandomGenerator选择

除了更新的API设计外，还实现了一些新算法，并且未来可能会添加更多算法。

### 5.1 选择默认的

在大多数情况下，我们没有特定的生成器要求。因此，**我们可以直接从RandomGenerator接口获取默认生成器**。

这是Java 17中推荐的新方法，作为创建Random实例的替代方法：

```java
RandomGenerator generator = RandomGenerator.getDefault();
```

getDefault方法当前选择L32X64MixRandom生成器。

但是，算法可能会随着时间而改变。因此，无法保证此方法会在未来的版本中继续返回此算法。

### 5.2 选择具体的

另一方面，当我们确实有特定的生成器需求时，**我们可以使用of方法检索特定的生成器**：

```java
RandomGenerator generator = RandomGenerator.of("L128X256MixRandom");
```

此方法需要将随机数生成器的名称作为参数传递。

如果找不到指定的算法，它将抛出IllegalArgumentException。

## 6. 线程安全

**大多数新的生成器实现都不是线程安全的**，但是Random和SecureRandom仍然是。

因此，在多线程环境中，我们可以选择：

-   共享线程安全生成器的实例
-   在启动新线程之前从局部源拆分一个新实例

我们可以使用SplittableGenerator实现第二种情况：

```java
List<Integer> numbers = Collections.synchronizedList(new ArrayList<>());
ExecutorService executorService = Executors.newCachedThreadPool();

RandomGenerator.SplittableGenerator sourceGenerator = RandomGeneratorFactory
    .<RandomGenerator.SplittableGenerator>of("L128X256MixRandom")
    .create();

sourceGenerator.splits(20).forEach((splitGenerator) -> {
    executorService.submit(() -> {
        numbers.add(splitGenerator.nextInt(10));
    });
})
```

通过这种方式，我们确保我们的生成器实例的初始化方式不会导致相同的数字流。

## 7. 性能

让我们对Java 17中所有可用的生成器实现运行一个简单的性能测试。

我们将使用相同的方法测试生成器，生成四种不同类型的随机数：

```java
private static void generateRandomNumbers(RandomGenerator generator) {
    generator.nextLong();
    generator.nextInt();
    generator.nextFloat();
    generator.nextDouble();
}
```

让我们看一下基准测试结果：

|           算法           |  模式  |     分数      |    错误     |  单位   |
|:----------------------:|:----:|:-----------:|:---------:|:-----:|
|   L128X1024MixRandom   | avgt |   95,637    |  ±3,274   | ns/op |
|   L128X128MixRandom    | avgt |   57,899    |  ±2,162   | ns/op |
|   L128X256MixRandom    | avgt |   66,095    |  ±3,260   | ns/op |
|    L32X64MixRandom     | avgt |   35,717    |  ±1,737   | ns/op |
|   L64X1024MixRandom    | avgt |   73,690    |  ±4,967   | ns/op |
|    L64X128MixRandom    | avgt |   35,261    |  ±1,985   | ns/op |
| L64X128StarStarRandom  | avgt |   34,054    |  ±0,314   | ns/op |
|    L64X256MixRandom    | avgt |   36,238    |  ±0,090   | ns/op |
|         Random         | avgt |   111,369   |  ±0,329   | ns/op |
|      SecureRandom      | avgt |  9,457,881  |  ±45,574  | ns/op |
|    SplittableRandom    | avgt |   27,753    |  ±0,526   | ns/op |
|  Xoroshiro128PlusPlus  | avgt |   31,825    |  ±1,863   | ns/op |
|  Xoroshiro128PlusPlus  | avgt |   33,327    |  ±0,555   | ns/op |

 

SecureRandom是最慢的生成器，这是因为它是唯一的加密功能强大的生成器。

由于它们不必是线程安全的，**因此与Random相比，新的生成器实现执行得更快**。

## 8. 总结

在本文中，我们探讨了随机数生成器API的更新，这是Java SE 17中的一项新功能。

我们了解了新旧API之间的区别，包括引入的新API设计、接口和实现。

在示例中，我们看到了如何使用RandomGeneratorFactory找到合适的生成器算法，并介绍了如何根据算法的名称或属性来选择算法。

最后，我们研究了新旧生成器实现的线程安全性和性能参考。
## 1. 简介

在这篇简短的文章中，我们将讨论在纯Java中实现单例的两种最流行的方法。

## 2. 基于类的单例

最常见的方法是通过创建一个常规类并确保它具有以下内容来实现单例：

-   私有构造函数
-   包含其唯一实例的静态字段
-   获取实例的静态工厂方法

我们还添加一个info属性，仅供以后使用。因此，我们的实现如下所示：

```java
public final class ClassSingleton {

    private static ClassSingleton INSTANCE;
    private String info = "Initial info class";

    private ClassSingleton() {
    }

    public static ClassSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassSingleton();
        }

        return INSTANCE;
    }

    // getters and setters
}
```

虽然这是一种常见的方法，但重要的是要注意它在多线程场景中可能会出现问题，这是使用单例的主要原因。

简而言之，它可能导致不止一个实例，从而打破了模式的核心原则。尽管有针对此问题的锁定解决方案，但我们的下一个方法是从根本上解决这些问题。

## 3. 枚举单例

接下来，让我们讨论另一种有趣的方法-使用枚举：

```java
public enum EnumSingleton {

    INSTANCE("Initial class info");

    private String info;

    private EnumSingleton(String info) {
        this.info = info;
    }

    public EnumSingleton getInstance() {
        return INSTANCE;
    }

    // getters and setters
}
```

这种方法具有由枚举实现本身保证的序列化和线程安全性，这在内部确保只有单个实例可用，从而纠正了基于类的实现中指出的问题。

## 4. 用法

要使用我们的ClassSingleton，我们只需要静态地获取实例：

```java
ClassSingleton classSingleton1 = ClassSingleton.getInstance();

System.out.println(classSingleton1.getInfo()); // Initial class info

ClassSingleton classSingleton2 = ClassSingleton.getInstance();
classSingleton2.setInfo("New class info");

System.out.println(classSingleton1.getInfo()); // New class info
System.out.println(classSingleton2.getInfo()); // New class info
```

至于EnumSingleton，我们可以像使用任何其他Java Enum一样使用它：

```java
EnumSingleton enumSingleton1 = EnumSingleton.INSTANCE.getInstance();

System.out.println(enumSingleton1.getInfo()); // Initial enum info

EnumSingleton enumSingleton2 = EnumSingleton.INSTANCE.getInstance();
enumSingleton2.setInfo("New enum info");

System.out.println(enumSingleton1.getInfo()); // New enum info
System.out.println(enumSingleton2.getInfo()); // New enum info
```

## 5. 常见陷阱

单例是一种看似简单的设计模式，程序员在创建单例时可能犯的常见错误很少。

我们区分两种类型的单例问题：

-   存在主义(我们需要单例吗？)
-   实现(我们是否正确实现它？)

### 5.1 存在问题

从概念上讲，单例是一种全局变量。一般来说，我们知道应该避免使用全局变量-尤其是当它们的状态是可变的时候。

**我们并不是说我们永远不应该使用单例。但是，我们说可能有更有效的方法来组织我们的代码**。

如果方法的实现依赖于单例对象，为什么不将其作为参数传递呢？在这种情况下，我们明确显示该方法所依赖的内容。因此，我们可以在执行测试时很容易地Mock这些依赖项(如有必要)。

例如，单例通常用于包含应用程序的配置数据(即与存储库的连接)。如果将它们用作全局对象，则很难为测试环境选择配置。

因此，当我们运行测试时，生产数据库被测试数据破坏了，这是几乎不可接受的。

如果我们需要一个单例，我们可以考虑将其实例化委托给另一个类(一种工厂)的可能性，该类应该负责确保只有一个单例实例在起作用。

### 5.2 实现问题

尽管单例看起来很简单，但它们的实现可能会遇到各种问题。所有这些都导致我们最终可能拥有不止一个类实例的事实。

我们上面介绍的私有构造函数的实现不是线程安全的：它在单线程环境中运行良好，但在多线程环境中，我们应该使用同步技术来保证操作的原子性：

```java
public synchronized static ClassSingleton getInstance() {
    if (INSTANCE == null) {
        INSTANCE = new ClassSingleton();
    }
    return INSTANCE;
}
```

注意方法声明中的synchronized关键字，该方法的主体有几个操作(比较、实例化和返回)。

在没有同步的情况下，两个线程可能会以这样一种方式交错执行，即表达式INSTANCE == null对两个线程的计算结果都为真，因此会创建两个ClassSingleton实例。

同步可能会显著影响性能。如果此代码经常被调用，我们应该使用各种技术加快它的速度，例如延迟初始化或双重检查锁定(请注意，由于编译器优化，这可能无法按预期工作)。

与JVM本身相关的单例还有其他几个问题可能导致我们最终得到一个单例的多个实例。这些问题非常微妙，我们对每个问题进行简要说明：

1.  每个JVM的单例应该是唯一的。对于分布式系统或内部结构基于分布式技术的系统来说，这可能是个问题。
2.  每个类加载器都可能加载它的单例版本。
3.  一旦没有人持有对它的引用，一个单例可能会被垃圾收集。此问题不会导致同时存在多个单例实例，但在重新创建时，实例可能与之前的版本不同。

## 6. 总结

在本快速教程中，我们重点介绍了如何仅使用核心Java来实现单例模式，如何确保它的一致性以及如何使用这些实现。
## 一、简介

在本教程中，我们将讨论 Valhalla 项目为 Java 生态系统带来的一个非常有趣的功能，即基于值的类。基于值的类在 Java 8 中引入，并在后续版本中进行了重大重构和增强。

## 2. 基于价值的课程

### 2.1. 瓦尔哈拉计划

[Valhalla 项目](https://www.baeldung.com/java-valhalla-project)是 OpenJDK 的一个实验项目，旨在向 Java 添加新特性和功能。该计划的主要目标是增加对值类型、通用专业化和性能改进的改进支持，同时保持完全的向后兼容性。

基于值的类是 Valhalla 项目引入的功能之一，旨在向 Java 语言引入原始的、不可变的值，而不会增加传统面向对象类带来的额外开销。

### 2.2. 原语和值类型

在我们正式定义基于值的类之前，让我们先看一下 Java 中的两个重要语义——基元和值类型。

**[Java 中的原始](https://www.baeldung.com/java-primitives)数据类型或原语是表示单个值的简单数据类型，而不是对象。Java 提供了八种这样的基本数据类型：\*byte\*、\*Short\*、\*int\*、\*long\*、\*float\*、\*double\*、\*char\*和\*boolean\*。虽然这些都是简单类型，但 Java为每个类型提供了[包装类，以便我们以面向对象的方式与它们进行交互。](https://www.baeldung.com/java-wrapper-classes)**

同样重要的是要记住，Java 会自动执行自动装箱和拆箱，以有效地在对象类型和基本类型之间进行转换：

```java
int primitive_a = 125;
Integer obj_a = 125; // this is autoboxed
Assert.assertSame(primitive_a, obj_a);复制
```

**原始类型位于堆栈内存中，而我们在代码中使用的对象则位于堆内存中。**

Valhalla 项目在 Java 生态系统中引入了一种介于对象和基元之间的新类型，称为值类型。值类型**是不可变类型，并且它们没有任何标识。**这些值类型也不支持继承。

值类型不是通过其引用而是通过其值来寻址，就像基元一样。

### 2.3. 基于值的类

基于值的类是被设计为行为类似于Java 中的值类型并封装*值类型的类。*JVM 可以在值类型和基于值的类之间自由切换，就像自动装箱和拆箱一样。因此，出于同样的原因，基于价值的类是与身份无关的。

## 3. 基于值的类的属性

基于值的类是表示简单不可变值的类。基于值的类具有多个属性，可以将这些属性分类为一些常规主题。

### 3.1. 不变性

基于值的类旨在表示不可变数据，类似于*int 等基元，*并具有以下特征：

- 基于价值的课程始终是*最终的*
- 它仅包含*最终*字段
- 该类可以扩展*Object*类或不声明实例字段的抽象类的层次结构

### 3.2. 对象创建

让我们了解创建基于值的类的新对象是如何工作的：

- 该类没有声明任何可访问的构造函数
- 如果存在可访问的构造函数，则应将它们标记为已弃用以便删除
- 该类只能通过工厂方法实例化。从工厂接收的实例可能是也可能不是新实例，并且调用代码不应对其身份做出任何假设

### 3.3. Identity 和*equals()*、*hashCode() 、* *toString()*方法

基于价值的类是与身份无关的。由于它们仍然是 Java 中的类，因此我们需要了解从*Object* 类继承的方法是如何发生的：

- *equals()*、*hashCode()*和*toString()*的实现仅根据其实例成员的值定义，而不是根据它们的身份或任何其他实例的状态
- *我们认为两个对象仅在对象的equals()*检查上相等，而不是基于引用的相等，即 ==
- 我们可以互换使用两个相等的对象，并且它们应该在任何计算或方法调用上产生相同的结果。

### 3.4. 一些额外的注意事项

在使用基于值的类时，我们应该考虑一些额外的限制：

- *根据equals()*方法相等的两个对象可能是 JVM 中的不同对象，也可能是同一个对象
- [我们无法确保监视器](https://www.baeldung.com/cs/monitor)的独占所有权，使得实例不适合[同步。](https://www.baeldung.com/java-synchronized)

## 4. 基于值的类的示例

### 4.1. JDK 中基于值的类

JDK 中有几个类遵循基于值的类规范。

首次引入时，[*java.util.Optional*](https://www.baeldung.com/java-optional)和[DateTime](https://www.baeldung.com/java-8-date-time-intro) API ( *java.time.LocalDateTime* ) 是基于值的类。*从 Java 16 及更高版本开始，Java 已将Integer*和*Long*等基本类型的所有包装类定义为基于值的。

这些类具有*jdk.internal包中的**@ValueBased*注释：

```java
@jdk.internal.ValueBased
public final class Integer extends Number implements Comparable<Integer>, Constable, ConstantDesc {
    // Integer class in the JDK
}复制
```

### 4.2. 自定义基于值的类

让我们创建遵循上面定义的基于值的类规范的自定义类。对于我们的示例，我们采用一个*Point* 类来标识 3D 空间中的一个点。该类有 3 个整数字段*x*、*y*和*z*。

我们可以认为*点* 定义是基于值的类的良好候选者，因为空间中的特定点是唯一的并且只能通过其值来引用。它是恒定且明确的，很像整数 302。

我们首先将类定义为*final*，并将其属性*x*、*y*和*z*定义为final。我们还将构造函数设为私有：

```java
public final class Point {
    private final int x;
    private final int y;
    private final int z;复制
    // inaccessible constructor
    private Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // ...
}复制
```

现在，让我们预先创建该类的*origin(0, 0, 0)*实例，每次调用创建*x = 0*、*y = 0*和*z = 0 的点时，我们都会返回相同的实例：*

```java
private static Point ORIGIN = new Point(0, 0, 0);复制
```

我们现在需要以工厂方法的形式提供对象创建机制：

```java
public static Point valueOfPoint(int x, int y, int z) {
    // returns a cached instance if it is the origin, or a new instance
    if (isOrigin(x, y, z)) {
        return ORIGIN;
    }
    return new Point(x, y, z);
}

// checking if a point is the origin
private static boolean isOrigin(int x, int y, int z) {
    return x == 0 && y == 0 && z == 0;
}复制
```

工厂方法*valueOfPoint()*可以根据参数返回一个新实例或缓存实例。**这迫使调用代码不对对象的状态做出任何假设或比较两个实例的引用。** 

最后，我们应该仅根据实例字段的值定义*equals()方法：*

```java
@Override
public boolean equals(Object other) {
    if (other == null || getClass() != other.getClass()) {
        return false;
    }
    Point point = (Point) other;
    return x == point.x && y == point.y && z == point.z;
}

@Override
public int hashCode() {
    return Objects.hash(x, y, z);
}复制
```

我们现在有一个*Point*类，它可以充当基于值的类。*从jdk.internal*包导入后，我们可以将*@ValueBased*注解添加到该类中。然而，这对于我们的案例来说不是强制性的。

现在让我们测试 (1,2,3) 表示的空间中同一点的两个实例是否相等：

```java
@Test
public void givenValueBasedPoint_whenCompared_thenReturnEquals() {
    Point p1 = Point.valueOfPoint(1,2,3);
    Point p2 = Point.valueOfPoint(1,2,3);

    Assert.assertEquals(p1, p2);
}复制
```

此外，为了进行本练习，我们还可以看到，如果通过引用进行比较，则在创建两个*原点时，两个实例是相同的：*

```java
@Test
public void givenValueBasedPoint_whenOrigin_thenReturnCachedInstance() {
    Point p1 = Point.valueOfPoint(0, 0, 0);
    Point p2 = Point.valueOfPoint(0, 0, 0);

    // the following should not be assumed for value-based classes
    Assert.assertTrue(p1 == p2);
}复制
```

## 5. 基于值的类的优点

现在我们知道什么是基于值的类以及如何定义基于值的类，让我们了解为什么我们可能需要基于值的类。

基于值的类是 Valhalla 规范的一部分，仍处于实验阶段并继续发展。因此，此类课程的好处可能会随着时间的推移而改变。

到目前为止，使用基于值的类带来的最重要的好处是内存利用率。基于值的类具有更高的内存效率，因为它们没有基于引用的标识。**此外，JVM 可以重用现有实例或根据需求创建新实例，从而减少内存占用。**

**此外，它们不需要同步，从而提高了整体性能，尤其是在多线程应用程序中。**

## 6. 基于值的类与其他类型的区别

### 6.1. 不可变类

[Java 中的不可变](https://www.baeldung.com/java-immutable-object)类与基于值的类有很多共同点。因此，了解它们之间的差异非常重要。

虽然基于值的类是新的并且是正在进行的实验功能的一部分，但不可变类长期以来一直是 Java 生态系统的核心和组成部分。**Java 中的String类、\*Enums\*和包装类（例如\*Integer\*类）都是不可变类的示例\*。\***

**不可变类不像基于值的类那样是无身份的。**具有相同状态的 Immutable 类的实例是不同的，我们可以根据引用相等性来比较它们。**基于值的类的实例没有基于引用的相等概念：**

**不可变类可以自由地提供可访问的构造函数，并且可以具有多个属性和复杂的行为。**然而，基于值的类表示简单的值，并且不定义具有依赖属性的复杂行为。

**最后，我们应该注意到，根据定义，基于值的类是不可变的，但反之则不然。**

### 6.2. 记录

[*Java 在 Java 14 中引入了记录*](https://www.baeldung.com/java-record-keyword)的概念，作为传递不可变数据对象的简单方法。记录和基于值的类实现不同的目的，即使它们在行为和语义上看起来相似。

**记录和基于值的类之间最明显的区别是记录具有公共构造函数，而基于值的类则缺少公共构造函数。**

## 七、结论

在本文中，我们讨论了 Java 中基于值的类和值类型的概念。我们谈到了基于价值的阶级必须遵守的重要属性及其带来的好处。我们还讨论了基于值的类和类似的 Java 概念（例如不可变类和记录）之间的差异。
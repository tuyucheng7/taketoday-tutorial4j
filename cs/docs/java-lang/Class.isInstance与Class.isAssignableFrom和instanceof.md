## 1. 概述

在本快速教程中，我们将了解[instanceof](https://www.baeldung.com/java-instanceof)、Class.isInstance和Class.isAssignableFrom之间的区别。我们将学习如何使用每种方法以及它们之间的区别。

## 2.设置

让我们在探索instanceof、Class.isInstance和Class.isAssignableFrom功能时设置一个接口和几个要使用的类。

首先，让我们定义一个接口：

```java
public interface Shape {
}
```

接下来，让我们定义一个实现Shape的类：

```java
public class Triangle implements Shape {
}
```

现在，我们将创建一个扩展Triangle的类：

```java
public class IsoscelesTriangle extends Triangle {
}
```

## 3.实例化

instanceof关键字是一个二元运算符，我们可以用它来验证某个对象是否是给定类型的实例。因此，操作的结果要么是true要么是false。此外，instanceof关键字是检查对象是否子类型为另一种类型的最常见和最直接的方法。

让我们将我们的类与instanceof运算符一起使用：

```java
Shape shape = new Triangle();
Triangle triangle = new Triangle();
IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
Shape nonspecificShape = null;

assertTrue(shape instanceof Shape);
assertTrue(triangle instanceof Shape);
assertTrue(isoscelesTriangle instanceof Shape);
assertFalse(nonspecificShape instanceof Shape);

assertTrue(shape instanceof Triangle);
assertTrue(triangle instanceof Triangle);
assertTrue(isoscelesTriangle instanceof Triangle);
assertFalse(nonspecificShape instanceof Triangle);

assertFalse(shape instanceof IsoscelesTriangle);
assertFalse(triangle instanceof IsoscelesTriangle);
assertTrue(isoscelesTriangle instanceof IsoscelesTriangle);
assertFalse(nonspecificShape instanceof IsoscelesTriangle);
```

通过上面的代码片段，我们可以看到右侧的类型比左侧的对象更通用。更具体地说，instanceof运算符会将null值处理为false。

## 4.类.isInstance

Class类上的isInstance方法相当于instanceof运算符。isInstance方法是在Java1.1 中引入的，因为它可以动态使用。通常，如果参数不为null并且可以在不引发ClassCastException的情况下成功转换为引用类型，则此方法将返回true。

让我们看看如何将isInstance方法与我们定义的接口和类一起使用：

```java
Shape shape = new Triangle();
Triangle triangle = new Triangle();
IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
Triangle isoscelesTriangle2 = new IsoscelesTriangle();
Shape nonspecificShape = null;

assertTrue(Shape.class.isInstance(shape));
assertTrue(Shape.class.isInstance(triangle));
assertTrue(Shape.class.isInstance(isoscelesTriangle));
assertTrue(Shape.class.isInstance(isoscelesTriangle2));
assertFalse(Shape.class.isInstance(nonspecificShape));

assertTrue(Triangle.class.isInstance(shape));
assertTrue(Triangle.class.isInstance(triangle));
assertTrue(Triangle.class.isInstance(isoscelesTriangle));
assertTrue(Triangle.class.isInstance(isoscelesTriangle2));

assertFalse(IsoscelesTriangle.class.isInstance(shape));
assertFalse(IsoscelesTriangle.class.isInstance(triangle));
assertTrue(IsoscelesTriangle.class.isInstance(isoscelesTriangle));
assertTrue(IsoscelesTriangle.class.isInstance(isoscelesTriangle2));
```

正如我们所见，右侧可以等同于或比左侧更具体。特别是，向isInstance方法提供null会返回false。

## 5.类.isAssignableFrom

如果语句左侧的 Class 与提供的 Class 参数相同或者是超类或超接口，则Class.isAssignableFrom方法将返回true 。

让我们使用带有isAssignableFrom方法的类：

```java
Shape shape = new Triangle();
Triangle triangle = new Triangle();
IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
Triangle isoscelesTriangle2 = new IsoscelesTriangle();

assertFalse(shape.getClass().isAssignableFrom(Shape.class));
assertTrue(shape.getClass().isAssignableFrom(shape.getClass()));
assertTrue(shape.getClass().isAssignableFrom(triangle.getClass()));
assertTrue(shape.getClass().isAssignableFrom(isoscelesTriangle.getClass()));
assertTrue(shape.getClass().isAssignableFrom(isoscelesTriangle2.getClass()));

assertFalse(triangle.getClass().isAssignableFrom(Shape.class));
assertTrue(triangle.getClass().isAssignableFrom(shape.getClass()));
assertTrue(triangle.getClass().isAssignableFrom(triangle.getClass()));
assertTrue(triangle.getClass().isAssignableFrom(isoscelesTriangle.getClass()));
assertTrue(triangle.getClass().isAssignableFrom(isoscelesTriangle2.getClass()));

assertFalse(isoscelesTriangle.getClass().isAssignableFrom(Shape.class));
assertFalse(isoscelesTriangle.getClass().isAssignableFrom(shape.getClass()));
assertFalse(isoscelesTriangle.getClass().isAssignableFrom(triangle.getClass()));
assertTrue(isoscelesTriangle.getClass().isAssignableFrom(isoscelesTriangle.getClass()));
assertTrue(isoscelesTriangle.getClass().isAssignableFrom(isoscelesTriangle2.getClass()));

assertFalse(isoscelesTriangle2.getClass().isAssignableFrom(Shape.class));
assertFalse(isoscelesTriangle2.getClass().isAssignableFrom(shape.getClass()));
assertFalse(isoscelesTriangle2.getClass().isAssignableFrom(triangle.getClass()));
assertTrue(isoscelesTriangle2.getClass().isAssignableFrom(isoscelesTriangle.getClass()));
assertTrue(isoscelesTriangle2.getClass().isAssignableFrom(isoscelesTriangle2.getClass()));
```

与isInstance示例一样，我们可以清楚地看到右侧必须与左侧相同或更具体。我们还可以看到，我们永远无法分配我们的Shape接口。

## 6. 差异

现在我们已经列出了一些详细的示例，让我们回顾一下其中的一些差异。

### 6.1. 语义差异

从表面上看，instanceof是一个Java语言关键字。相反，isInstance和isAssignableFrom都是Class类型的本地方法。

在语义上，我们使用它们来验证两个编程元素之间的不同关系：

-   两个对象：我们可以测试两个对象是否相同或相等。
-   对象和类型：我们可以检查对象是否是类型的实例。显然，instanceof关键字和isInstance方法都属于这一类。
-   两种类型：我们可以检查一种类型是否与另一种类型兼容，例如isAssignableFrom方法。

### 6.2. 使用极端情况差异

首先，它们与空值不同：

```java
assertFalse(null instanceof Shape);
assertFalse(Shape.class.isInstance(null));
assertFalse(Shape.class.isAssignableFrom(null)); // NullPointerException
```

从上面的代码片段来看，instanceof和isInstance都返回false；但是，isAssignableFrom方法抛出NullPointerException。

其次，它们与原始类型不同：

```java
assertFalse(10 instanceof int); // illegal
assertFalse(int.class.isInstance(10));
assertTrue(Integer.class.isInstance(10));
assertTrue(int.class.isAssignableFrom(int.class));
assertFalse(float.class.isAssignableFrom(int.class));
```

如我们所见，instanceof关键字不支持原始类型。如果我们使用带有int值的isInstance方法，Java 编译器会将int值转换为Integer对象。因此，isInstance方法支持原始类型但始终返回false。当我们使用isAssignableFrom方法时，结果取决于确切的类型值。

第三，它们与类实例变量不同：

```java
Shape shape = new Triangle();
Triangle triangle = new Triangle();
Class<?> clazz = shape.getClass();

assertFalse(triangle instanceof clazz); // illegal
assertTrue(clazz.isInstance(triangle));
assertTrue(clazz.isAssignableFrom(triangle.getClass()));
```

从上面的代码片段中，我们了解到isInstance和isAssignableFrom方法都支持类实例变量，但instanceof关键字不支持。

### 6.3. 字节码差异

在编译的类文件中，它们使用不同的操作码：

-   instanceof关键字对应instanceof操作码
-   isInstance和isAssignableFrom方法都将使用invokevirtual操作码

在[JVM Instruction Set](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.instanceof)中，instanceof操作码的值为 193，它有一个两字节的操作数：

```plaintext
instanceof
indexbyte1
indexbyte2
```

然后，JVM 将计算(indexbyte1 << 8) | indexbyte2变成一个索引。而这个索引指向当前类的运行时常量池。在索引处，运行时常量池包含对[CONSTANT_Class_info](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.1)常量的符号引用。并且，这个常量正是instanceof关键字右侧需要的值。

此外，它还解释了为什么instanceof关键字不能使用类实例变量。这是因为instanceof操作码需要运行时常量池中的常量类型，而我们不能用类实例变量替换常量类型。

以及， instanceof关键字的左侧对象信息存储在哪里？它位于操作数栈的顶部。因此，instanceof关键字需要操作数栈上的对象和运行时常量池中的常量类型。

在[JVM Instruction Set](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.invokevirtual)中，invokevirtual opcode 的值为 182，它还有一个两字节的操作数：

```plaintext
invokevirtual
indexbyte1
indexbyte2
```

同样，JVM 会计算(indexbyte1 << 8) | indexbyte2变成一个索引。在索引处，运行时常量池保存对[CONSTANT_Methodref_info](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2)常量的符号引用。该常量包含目标方法信息，如类名、方法名和方法描述符。

isInstance方法需要操作数栈上的两个元素：第一个元素是类型；第二个元素是对象。但是，isAssignableFrom方法需要操作数堆栈上的两个类型元素。

### 6.4. 加起来

综上所述，我们用一个表格来说明它们的区别：

| 财产         | 实例                       | 类.isInstance                                              | 类.isAssignableFrom                        |
| ------------ | ---------------------------- | ------------------------------------------------------------ | -------------------------------------------- |
| 种类         | 关键词                       | 本机方法                                                     | 本机方法                                     |
| 操作数       | 对象和类型                   | 类型和对象                                                   | 一种类型和另一种类型                         |
| 空值处理     | 错误的                     | 错误的                                                     | 空指针异常                                 |
| 原始类型     | 不支持                       | 支持，但总是false                                          | 是的                                         |
| 类实例变量   | 不                           | 是的                                                         | 是的                                         |
| 字节码       | 实例                       | 调用虚拟                                                   | 调用虚拟                                   |
| 最合适的时候 | 对象已给定，类型在编译时已知 | 对象已给定，目标类型在编译类型时未知                         | 没有给出对象，只有类型是已知的并且仅在运行时 |
| 用例         | 日常使用，适合大多数情况     | 复杂且不典型的案例，例如使用 Reflection API 实现库或实用程序 |                                              |

## 七、总结

在本教程中，我们查看了instanceof、Class.isInstance和Class.isAssignableFrom方法并探索了它们的用法和区别。
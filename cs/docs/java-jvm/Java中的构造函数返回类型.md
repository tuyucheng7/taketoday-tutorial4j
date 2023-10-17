## 一、概述

在本快速教程中，我们将重点关注 Java 中构造函数的返回类型。

首先，我们将熟悉 Java 和 JVM 中对象初始化的工作方式。然后，我们将更深入地了解对象初始化和分配是如何工作的。

## 2.实例初始化

让我们从一个空类开始：

```java
public class Color {}复制
```

在这里，我们将从此类创建一个实例并将其分配给某个变量：

```java
Color color = new Color();复制
```

*编译完这个简单的 Java 代码片段后，让我们通过javap -c*命令看一下它的字节码：

```bash
0: new           #7                  // class Color
3: dup
4: invokespecial #9                  // Method Color."<init>":()V
7: astore_1复制
```

当我们在 Java 中实例化一个对象时，JVM 会执行以下操作：

1.  首先，它在其进程空间中为新对象[找到一个位置。](https://alidg.me/blog/2019/6/21/tlab-jvm)
2.  然后，JVM 执行系统初始化过程。在此步骤中，它以默认状态创建对象。*字节码中的新* 操作码实际上负责这一步。
3.  最后，它使用构造函数和其他初始化块初始化对象。在这种情况下， *invokespecial* 操作码调用构造函数。

如上所示，默认构造函数的方法签名为：

```bash
Method Color."<init>":()V复制
```

**<init> \*是\* JVM 中[实例初始化方法](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html#jvms-2.9)的名称**。在这种情况下， *<init>* 是一个函数：

-   不接受任何输入（方法名称后的空括号）
-   什么都不返回*（V* 代表*void*）

**因此，Java 和 JVM 中构造函数的返回类型都是 \*void。\***

再看看我们的简单作业：

```java
Color color = new Color();复制
```

现在我们知道构造函数返回 *void*，让我们看看赋值是如何工作的。

## 3. 作业如何运作

JVM 是一个基于堆栈的虚拟机。每个堆栈由[堆栈帧](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html#jvms-2.6)组成。简单来说，每个栈帧对应一个方法调用。事实上，JVM 使用新的方法调用创建框架，并在完成工作后销毁它们：

[![简单的ol](https://www.baeldung.com/wp-content/uploads/2020/06/simple-ol.svg)](https://www.baeldung.com/wp-content/uploads/2020/06/simple-ol.svg)

**每个堆栈帧使用一个数组来存储局部变量和一个操作数堆栈来存储部分结果**。鉴于此，让我们再看一下字节码：

```bash
0: new           #7                // class Color
3: dup
4: invokespecial #9               // Method Color."<init>":()V
7: astore_1复制
```

以下是作业的工作原理：

-   新 指令创建*Color* 实例 并将其*引用* 压入操作数堆栈
-   dup 操作码*复制* 操作数栈中的最后一项
-   invokespecial 获取重复的引用并将其用于初始化*。* 在此之后，只有原始引用保留在操作数堆栈中
-   astore_1 存储对局部*变量* 数组索引 1 的原始引用。前缀“a”表示要存储的项是对象引用，“1”是数组索引

从现在开始，**局部变量数组中的第二项（索引 1）是对新创建对象的引用**。因此，我们不会丢失引用，并且赋值确实有效——即使构造函数不返回任何内容！

## 4。结论

在这个快速教程中，我们学习了 JVM 如何创建和初始化我们的类实例。此外，我们还看到了实例初始化是如何在后台工作的。

为了更详细地了解 JVM，查看其[规范](https://docs.oracle.com/javase/specs/jvms/se14/html/index.html)总是一个好主意。
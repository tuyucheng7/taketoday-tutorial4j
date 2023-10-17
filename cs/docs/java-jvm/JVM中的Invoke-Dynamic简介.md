## 1. 概述

Invoke Dynamic(也称为 Indy)是[JSR 292](https://jcp.org/en/jsr/detail?id=292)的一部分，旨在增强 JVM 对动态类型语言的支持。在 Java 7 中首次发布后，invokedynamic 操作码被 JRuby 等基于 JVM 的动态语言甚至 Java 等静态类型语言广泛使用。

在本教程中，我们将揭开invokedynamic的神秘面纱，看看它如何 帮助库和语言设计者实现多种形式的动态性。

## 2.认识调用动态

让我们从一个简单的[Stream API](https://www.baeldung.com/java-streams)调用链开始：

```java
public class Main { 

    public static void main(String[] args) {
        long lengthyColors = List.of("Red", "Green", "Blue")
          .stream().filter(c -> c.length() > 3).count();
    }
}
```

起初，我们可能认为 Java 创建了一个派生自 Predicate 的匿名内部类，然后将该实例传递给 filter 方法。 但是，我们错了。

### 2.1. 字节码

为了验证这个假设，我们可以看一下生成的字节码：

```bash
javap -c -p Main
// truncated
// class names are simplified for the sake of brevity 
// for instance, Stream is actually java/util/stream/Stream
0: ldc               #7             // String Red
2: ldc               #9             // String Green
4: ldc               #11            // String Blue
6: invokestatic      #13            // InterfaceMethod List.of:(LObject;LObject;)LList;
9: invokeinterface   #19,  1        // InterfaceMethod List.stream:()LStream;
14: invokedynamic    #23,  0        // InvokeDynamic #0:test:()LPredicate;
19: invokeinterface  #27,  2        // InterfaceMethod Stream.filter:(LPredicate;)LStream;
24: invokeinterface  #33,  1        // InterfaceMethod Stream.count:()J
29: lstore_1
30: return
```

不管我们怎么想，没有匿名内部类，当然，没有人将此类的实例传递给过滤器 方法。 

令人惊讶的是，invokedynamic 指令以某种方式负责创建 Predicate 实例。

### 2.2. Lambda 特定方法

此外，Java 编译器还生成了以下看起来很有趣的静态方法：

```bash
private static boolean lambda$main$0(java.lang.String);
    Code:
       0: aload_0
       1: invokevirtual #37                 // Method java/lang/String.length:()I
       4: iconst_3
       5: if_icmple     12
       8: iconst_1
       9: goto          13
      12: iconst_0
      13: ireturn
```

此方法将一个 字符串 作为输入，然后执行以下步骤：

-   计算输入长度(在length 上调用虚拟)
-   将长度与常量 3(if_icmple 和 iconst_3)进行比较
-   如果长度小于或等于 3，则返回 false 

有趣的是，这实际上等同于我们传递给过滤器 方法的 lambda：

```java
c -> c.length() > 3
```

因此，Java 没有创建匿名内部类，而是创建了一个特殊的静态方法，并以某种方式通过 invokedynamic 调用该方法。 

在本文中，我们将了解此调用在内部是如何工作的。但是，首先，让我们定义invokedynamic 试图解决的问题。

### 2.3. 问题

在Java 7之前，JVM只有四种方法调用类型：调用普通类方法的 invokevirtual ，调用静态方法的 invokestatic ，调用接口方法的 invokeinterface ，调用构造函数或私有方法的invokespecial 。

尽管存在差异，但所有这些调用都有一个简单的特征：它们有几个预定义的步骤来完成每个方法调用，我们无法用自定义行为来丰富这些步骤。

此限制有两种主要的解决方法：一种在编译时，另一种在运行时。前者通常由[Scala](https://alidg.me/blog/2020/2/9/java14-records-in-depth#scalas-case-class)或[Koltin](https://alidg.me/blog/2020/2/9/java14-records-in-depth#kotlins-data-class) 等语言使用，后者是 JRuby 等基于 JVM 的动态语言的首选解决方案。

运行时方法通常是基于反射的，因此效率低下。

另一方面，编译时解决方案通常依赖于编译时的代码生成。这种方法在运行时更有效。但是，它有点脆弱，并且还可能导致启动时间变慢，因为要处理的字节码更多。

现在我们已经对问题有了更好的理解，让我们看看解决方案在内部是如何工作的。

## 3.引擎盖下

invokedynamic让我们以任何我们想要的方式引导方法调用过程。也就是说，当 JVM 第一次看到 invokedynamic 操作码时，它会调用一种称为引导方法的特殊方法来初始化调用过程：

[![无标题图](https://www.baeldung.com/wp-content/uploads/2020/05/Untitled-Diagram.svg)](https://www.baeldung.com/wp-content/uploads/2020/05/Untitled-Diagram.svg)

bootstrap 方法是我们为设置调用过程而编写的一段普通 Java 代码。因此，它可以包含任何逻辑。

一旦引导方法正常完成，它应该返回一个 [CallSite](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/CallSite.html)的实例。 这个 CallSite 封装了以下信息：

-   指向 JVM 应执行的实际逻辑的指针。这应该表示为 [MethodHandle](https://www.baeldung.com/java-method-handles)。 
-   表示返回的CallSite有效性的条件 。

从现在开始，每次 JVM 再次看到这个特定的操作码时，它都会跳过慢速路径，直接调用底层可执行文件。此外，JVM 将继续跳过慢速路径，直到CallSite 中的条件发生变化。

与 Reflection API 不同，JVM 可以完全看穿MethodHandle并尝试优化它们，因此性能更好。

### 3.1. Bootstrap 方法表

我们再看一下生成的 invokedynamic 字节码：

```bash
14: invokedynamic #23,  0  // InvokeDynamic #0:test:()Ljava/util/function/Predicate;
```

这意味着该特定指令应从引导方法表中调用第一个引导方法(#0 部分)。 此外，它还提到了一些要传递给引导方法的参数：

-   test 是 Predicate中唯一的抽象方法
-   ( )Ljava/util/function/Predicate 表示 JVM 中的方法签名——该方法不接受任何输入并返回Predicate 接口的实例

为了查看 lambda 示例的引导方法表，我们应该将 -v 选项传递给 javap：

```bash
javap -c -p -v Main
// truncated
// added new lines for brevity
BootstrapMethods:
  0: #55 REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory:
    (Ljava/lang/invoke/MethodHandles$Lookup;
     Ljava/lang/String;
     Ljava/lang/invoke/MethodType;
     Ljava/lang/invoke/MethodType;
     Ljava/lang/invoke/MethodHandle;
     Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #62 (Ljava/lang/Object;)Z
      #64 REF_invokeStatic Main.lambda$main$0:(Ljava/lang/String;)Z
      #67 (Ljava/lang/String;)Z
```

所有 lambda 的引导方法是 [LambdaMetafactory](https://github.com/openjdk/jdk/blob/a445b66e58a30577dee29cacb636d4c14f0574a2/src/java.base/share/classes/java/lang/invoke/LambdaMetafactory.java#L315) 类中的[元](https://github.com/openjdk/jdk/blob/a445b66e58a30577dee29cacb636d4c14f0574a2/src/java.base/share/classes/java/lang/invoke/LambdaMetafactory.java#L227) 工厂静态方法 。

与所有其他引导方法类似，这个方法至少需要三个参数，如下所示：

-   Ljava/lang/invoke/MethodHandles$Lookup 参数表示 invokedynamic的查找上下文
-   Ljava/lang/String 表示调用站点中的 方法名称——在这个例子中，方法名称是test
-   Ljava/lang/invoke/MethodType 是调用站点的动态方法签名——在本例中，它是( )Ljava/util/function/Predicate

除了这三个参数之外，引导方法还可以选择性地接受一个或多个额外参数。在这个例子中，这些是额外的：

-   ( Ljava/lang/Object;)Z 是一个已[擦除](https://www.baeldung.com/java-type-erasure)的方法签名，它接受一个 Object 实例并返回一个 布尔值。
-   REF_invokeStatic Main.lambda$main$0:(Ljava/lang/String;)Z 是 指向实际 lambda 逻辑的MethodHandle 。
-   ( Ljava/lang/String;)Z 是一个未擦除的方法签名，它接受一个 字符串 并返回一个布尔值。

简而言之，JVM 会将所有需要的信息传递给引导程序方法。Bootstrap 方法将依次使用该信息创建适当的Predicate 实例。 然后，JVM 将该实例传递给过滤器 方法。

### 3.2. 不同类型 的CallSite

一旦 JVM 第一次看到 此示例中的invokedynamic ，它就会调用 bootstrap 方法。在撰写本文时，lambda 引导程序方法将使用 [InnerClassLambdaMetafactory](https://github.com/openjdk/jdk/blob/a445b66e58a30577dee29cacb636d4c14f0574a2/src/java.base/share/classes/java/lang/invoke/InnerClassLambdaMetafactory.java#L194)在运行时为 lambda 生成内部类。 

然后引导方法将生成的内部类封装在一种 称为 [ConstantCallSite](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/ConstantCallSite.html)的特殊类型的CallSite 中。 这种类型的 CallSite 在设置后永远不会改变。因此，在为每个 lambda 进行第一次设置后，JVM 将始终使用快速路径直接调用 lambda 逻辑。

尽管这是最有效的 invokedynamic 类型， 但它肯定不是唯一可用的选项。事实上，Java 提供了 [MutableCallSite](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/MutableCallSite.html) 和 [VolatileCallSite](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/VolatileCallSite.html) 以适应更动态的需求。

### 3.3. 好处

因此，为了实现 lambda 表达式，Java 不是在编译时创建匿名内部类，而是在运行时通过 invokedynamic 创建它们。

有人可能会反对将内部类的生成推迟到运行时。然而， 与简单的编译时解决方案相比， invokedynamic 方法有一些优势。

首先，JVM 在第一次使用 lambda 之前不会生成内部类。因此，我们不会在第一次执行 lambda 之前为与内部类相关的额外占用空间付费。

此外，许多链接逻辑已从字节码移出到引导程序方法中。因此，invokedynamic 字节 码通常比替代解决方案小得多。较小的字节码可以提高启动速度。

假设更新版本的 Java 带有更高效的引导方法实现。那么我们的 invokedynamic 字节码就可以利用这一改进而无需重新编译。这样我们就可以实现某种转发二进制兼容性。基本上，我们可以在不同的策略之间切换而无需重新编译。

最后，用 Java 编写引导程序和链接逻辑通常比遍历 AST 生成一段复杂的字节码更容易。因此，invokedynamic 可以(主观上)不那么脆弱。

## 4.更多例子

Lambda 表达式不是唯一的特性，Java 当然也不是唯一使用invokedynamic 的语言。 在本节中，我们将熟悉一些动态调用的其他示例。

### 4.1. Java 14：记录

[记录](https://www.baeldung.com/java-record-keyword)是[Java 14](https://openjdk.java.net/projects/jdk/14/)中的一项新预览功能，它 提供了一种简洁的语法来声明应该是哑数据持有者的类。

这是一个简单的记录示例：

```java
public record Color(String name, int code) {}
```

鉴于这一简单的单行代码，Java 编译器会为访问器方法、 toString、equals 和 hashcode 生成适当的实现。 

为了实现 toString、equals 或 hashcode， Java 使用 invokedynamic。 例如， equals 的字节码如下：

```bash
public final boolean equals(java.lang.Object);
    Code:
       0: aload_0
       1: aload_1
       2: invokedynamic #27,  0  // InvokeDynamic #0:equals:(LColor;Ljava/lang/Object;)Z
       7: ireturn
```

替代解决方案是查找所有记录字段并 在编译时基于这些字段生成等于 逻辑。字段越多，字节码就越长。

相反，Java 在运行时调用引导方法来链接适当的实现。因此，无论字段的数量如何，字节码长度都将保持不变。

更仔细地查看字节码表明引导方法是[ObjectMethods#bootstrap](https://github.com/openjdk/jdk/blob/827e5e32264666639d36990edd5e7d0b7e7c78a9/src/java.base/share/classes/java/lang/runtime/ObjectMethods.java#L338)：

```bash
BootstrapMethods:
  0: #42 REF_invokeStatic java/lang/runtime/ObjectMethods.bootstrap:
    (Ljava/lang/invoke/MethodHandles$Lookup;
     Ljava/lang/String;
     Ljava/lang/invoke/TypeDescriptor;
     Ljava/lang/Class;
     Ljava/lang/String;
     [Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;
    Method arguments:
      #8 Color
      #49 name;code
      #51 REF_getField Color.name:Ljava/lang/String;
      #52 REF_getField Color.code:I
```

### 4.2. Java 9：字符串连接

在 Java 9 之前，重要的字符串连接是使用 StringBuilder 实现的。 作为 JEP [280](https://openjdk.java.net/jeps/280)的一部分，字符串连接现在使用 invokedynamic。 例如，让我们连接一个常量字符串和一个随机变量：

```java
"random-" + ThreadLocalRandom.current().nextInt();
```

以下是此示例的字节码：

```bash
0: invokestatic  #7          // Method ThreadLocalRandom.current:()LThreadLocalRandom;
3: invokevirtual #13         // Method ThreadLocalRandom.nextInt:()I
6: invokedynamic #17,  0     // InvokeDynamic #0:makeConcatWithConstants:(I)LString;
```

此外，字符串连接的引导方法位于 [StringConcatFactory](https://github.com/openjdk/jdk/blob/827e5e32264666639d36990edd5e7d0b7e7c78a9/src/java.base/share/classes/java/lang/invoke/StringConcatFactory.java#L593)类中：

```bash
BootstrapMethods:
  0: #30 REF_invokeStatic java/lang/invoke/StringConcatFactory.makeConcatWithConstants:
    (Ljava/lang/invoke/MethodHandles$Lookup;
     Ljava/lang/String;
     Ljava/lang/invoke/MethodType;
     Ljava/lang/String;
     [Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #36 random-u0001
```

## 5.总结

在本文中，我们首先熟悉了 Indy 试图解决的问题。

然后，通过一个简单的 lambda 表达式示例，我们了解了invokedynamic 的内部工作原理。

最后，我们列举了最近几个 Java 版本中 indy 的其他几个例子。
## 一、概述

编译器和运行时倾向于优化一切，即使是最小的和看似不那么重要的部分。当谈到这些优化时，JVM 和 Java 可以提供很多。

在本文中，我们将评估这些相对较新的优化之一：使用*invokedynamic 的*[字符串连接](https://www.baeldung.com/java-strings-concatenation)。

## 2.Java 9之前

在 Java 9 之前，重要的字符串连接是使用 *[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)*实现的。例如，让我们考虑以下方法：

```java
String concat(String s, int i) {
    return s + i;
}复制
```

这个简单代码的字节码如下（使用*javap -c*）：

```java
java.lang.String concat(java.lang.String, int);
  Code:
     0: new           #2      // class StringBuilder
     3: dup
     4: invokespecial #3      // Method StringBuilder."<init>":()V
     7: aload_0
     8: invokevirtual #4      // Method StringBuilder.append:(LString;)LStringBuilder;
    11: iload_1
    12: invokevirtual #5      // Method StringBuilder.append:(I)LStringBuilder;
    15: invokevirtual #6      // Method StringBuilder.toString:()LString;复制
```

在这里，Java 8 编译器使用 *StringBuilder* 连接方法输入，*即使*我们没有 在代码中使用*StringBuilder 。*

公平地说，使用***StringBuilder\*****连接字符串 非常高效且经过精心设计。**

让我们看看 Java 9 如何改变这个实现以及这种改变的动机是什么。

## 3.调用动态

从 Java 9 开始，作为[JEP 280](https://openjdk.java.net/jeps/280)的一部分，字符串连接现在使用 *[invokedynamic](https://www.baeldung.com/java-invoke-dynamic)*。

**更改背后的主要动机是要有一个更动态的实现**。也就是说，可以在不更改字节码的情况下更改连接策略。这样，客户无需重新编译即可从新的优化策略中获益。

还有其他优点。例如， *invokedynamic*的字节码 更优雅、更不脆弱且更小。

### 3.1. 大图

在深入了解这种新方法的工作原理之前，让我们从更广泛的角度来看它。

例如，假设我们要通过 将另一个*String* 与 *int连接来创建一个新的**String* 。**我们可以将其视为接受*****String\*** **和** ***int\*** **然后返回连接后的** ***String 的\*****函数。**

以下是新方法如何适用于此示例：

-   准备描述串联的函数签名。例如， *(String, int) -> 字符串*
-   为串联准备实际参数。例如，如果我们要加入 *“答案是”* 和 42，那么这些值将成为参数
-   调用引导程序方法并将函数签名、参数和一些其他参数传递给它
-   为该函数签名生成实际实现并将其封装在 MethodHandle*中*
-   调用生成的函数来创建最终的连接字符串

[![印地连续](https://www.baeldung.com/wp-content/uploads/2020/06/Untitled-2020-05-22-0432-2-1024x409.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Untitled-2020-05-22-0432-2.png)

简而言之，**字节码在编译时定义了规范。然后引导程序方法在运行时将实现链接到该规范。** 这反过来又可以在不触及字节码的情况下更改实现。

在本文中，我们将揭示与每个步骤相关的详细信息。

首先，让我们看看与 bootstrap 方法的链接是如何工作的。

## 4. 联系

让我们看看 Java 9+ 编译器如何为相同的方法生成字节码：

```java
java.lang.String concat(java.lang.String, int);
  Code:
     0: aload_0
     1: iload_1
     2: invokedynamic #7,  0   // InvokeDynamic #0:makeConcatWithConstants:(LString;I)LString;
     7: areturn复制
```

***与朴素的StringBuilder\*方法相反 ，这种方法使用的指令数量要少得多**。

在此字节码中，*(LString;I)LString* 签名非常有趣。它需要一个 *String* 和一个 *int*（*I* 代表 *int*）并返回连接的字符串。这是因为该方法将一个 *String* 和一个 *int* 连接在一起。

**与其他调用动态实现类似，大部分逻辑从编译时移到运行时。**

要查看运行时逻辑，让我们检查引导方法表（使用*javap -c -v*）：

```bash
BootstrapMethods:
  0: #25 REF_invokeStatic java/lang/invoke/StringConcatFactory.makeConcatWithConstants:
    (Ljava/lang/invoke/MethodHandles$Lookup;
     Ljava/lang/String;
     Ljava/lang/invoke/MethodType;
     Ljava/lang/String;
     [Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #31 \u0001\u0001
复制
```

在这种情况下，当 JVM第一次 看到*invokedynamic指令时，它会调用**[makeConcatWithConstants](https://github.com/openjdk/jdk14u/blob/8c9ab998b758a18e65e2a1cebcc608860ae43931/src/java.base/share/classes/java/lang/invoke/StringConcatFactory.java#L593)* 引导程序方法。bootstrap 方法将依次返回一个 *[ConstantCallSite](https://github.com/openjdk/jdk14u/blob/master/src/java.base/share/classes/java/lang/invoke/ConstantCallSite.java)*，它指向连接逻辑。

[![印地](https://www.baeldung.com/wp-content/uploads/2020/06/Untitled-2020-05-22-0432-1-1024x143.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Untitled-2020-05-22-0432-1.png)

在传递给 bootstrap 方法的参数中，有两个很突出：

-   *Ljava/lang/invoke/MethodType*表示字符串连接签名。在这种情况下，它是 *(LString;I)LString* 因为我们将整数与字符串 *组合*
-   *\u0001\u0001* 是构造字符串的秘诀（稍后会详细介绍）

## 5.食谱

为了更好地理解食谱的作用，让我们考虑一个简单的数据类：

```java
public class Person {

    private String firstName;
    private String lastName;

    // constructor

    @Override
    public String toString() {
        return "Person{" +
          "firstName='" + firstName + '\'' +
          ", lastName='" + lastName + '\'' +
          '}';
    }
}复制
```

为了生成 *字符串* 表示，JVM 将*firstName* 和 *lastName* 字段 作为参数传递给*invokedynamic指令：*

```bash
 0: aload_0
 1: getfield      #7        // Field firstName:LString;
 4: aload_0
 5: getfield      #13       // Field lastName:LString;
 8: invokedynamic #16,  0   // InvokeDynamic #0:makeConcatWithConstants:(LString;LString;)L/String;
 13: areturn复制
```

这一次，引导方法表看起来有点不同：

```bash
BootstrapMethods:
  0: #28 REF_invokeStatic StringConcatFactory.makeConcatWithConstants // truncated
    Method arguments:
      #34 Person{firstName=\'\u0001\', lastName=\'\u0001\'} // The recipe复制
```

如上所示，**配方表示串联** ***字符串***的基本结构。例如，前面的配方包括：

-   常量字符串，例如“ *Person* ” *。*这些文字值将按原样出现在连接的字符串中
-   两个 *\u0001* 标签来表示普通参数。它们将被实际参数替换，例如 *firstName*

我们可以将食谱视为 包含静态部分和变量占位符的模板化*字符串。*

**使用配方可以显着减少传递给引导程序方法的参数数量，因为我们只需要传递所有动态参数和一个配方。**

## 6. 字节码风格

新的串联方法有两种字节码风格。到目前为止，我们熟悉一种风格：调用*makeConcatWithConstants* 引导程序方法并传递配方。**这种风格被称为带有常量的 indy，是 Java 9 的默认风格。**

**第二种风格不使用食谱，而是将所有内容作为参数传递**。也就是说，它不区分常量部分和动态部分，并将它们全部作为参数传递。

**要使用第二种风格，我们应该将 \*-XDstringConcat=indy\*选项传递给 Java 编译器**。例如，如果我们用这个标志编译同一个 *Person* 类，那么编译器会生成以下字节码：

```bash
public java.lang.String toString();
    Code:
       0: ldc           #16      // String Person{firstName=\'
       2: aload_0
       3: getfield      #7       // Field firstName:LString;
       6: bipush        39
       8: ldc           #18      // String , lastName=\'
      10: aload_0
      11: getfield      #13      // Field lastName:LString;
      14: bipush        39
      16: bipush        125
      18: invokedynamic #20,  0  // InvokeDynamic #0:makeConcat:(LString;LString;CLString;LString;CC)LString;
      23: areturn复制
```

这一次，引导方法是*[makeConcat](https://github.com/openjdk/jdk14u/blob/8c9ab998b758a18e65e2a1cebcc608860ae43931/src/java.base/share/classes/java/lang/invoke/StringConcatFactory.java#L472)*。此外，串联签名有七个参数。*每个参数代表toString*的一部分 ：

-   *第一个参数表示firstName*变量之前的部分—— *“Person{firstName=\'”* 字面量 
-   *第二个参数是firstName* 字段的值
-   第三个参数是单引号字符
-   第四个参数是下一个变量之前的部分—— *“, lastName=\'”*
-   第五个参数是 *lastName* 字段
-   第六个参数是单引号字符
-   最后一个参数是右大括号

这样，引导程序方法就有足够的信息来链接适当的串联逻辑。

非常有趣的是**，还可以回到 Java 9 之前的世界并使用 带有 \*-XDstringConcat=inline编译器选项的\**StringBuilder\* 。**

## 7.策略

**bootstrap 方法最终提供了 指向实际串联逻辑的\*MethodHandle\***。在撰写本文时，有六种不同的[策略](https://github.com/openjdk/jdk14u/blob/8c9ab998b758a18e65e2a1cebcc608860ae43931/src/java.base/share/classes/java/lang/invoke/StringConcatFactory.java#L136)可以生成此逻辑：

-   *BC_SB*或“字节码*StringBuilder* ”策略在运行时生成相同的 *StringBuilder*字节码。*然后它通过Unsafe.defineAnonymousClass* 方法加载生成的字节码
-   *BC_SB_SIZED*策略将尝试猜测 *StringBuilder*的必要容量。除此之外，它与以前的方法相同。猜测容量可能有助于 *StringBuilder在不调整底层**byte[]* 大小的情况下执行串联 
-   *BC_SB_SIZED_EXACT*是一个基于*StringBuilder 的*字节码生成器 ，可以精确计算所需的存储空间。要计算确切的大小，首先，它将所有参数转换为 *String*
-   *MH_SB_SIZED*基于 *MethodHandle*，最终调用 *StringBuilder* API 进行拼接。该策略还对所需容量进行了有根据的猜测
-   *MH_SB_SIZED_EXACT*与前一个类似，除了它计算所需的容量完全准确
-   *MH_INLINE_SIZE_EXACT*预先计算所需的存储空间并直接维护其 *byte[]* 以存储连接结果*。* 这个策略是内联的，因为它复制了 *StringBuilder* 在内部所做的事情

**默认策略是\*MH_INLINE_SIZE_EXACT\*。\*但是，我们可以使用-Djava.lang.invoke.stringConcat=<strategyName>\*系统属性更改此策略 。** 

## 八、结论

在这篇详细的文章中，我们了解了新的*字符串* 连接是如何实现的，以及使用这种方法的优势。

要进行更详细的讨论，最好查看[实验说](https://cr.openjdk.java.net/~shade/8085796/notes.txt)明甚至[源代码](https://github.com/openjdk/jdk14u/blob/master/src/java.base/share/classes/java/lang/invoke/StringConcatFactory.java)。
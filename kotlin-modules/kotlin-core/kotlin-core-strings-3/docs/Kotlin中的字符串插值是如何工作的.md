## 1. 概述

Kotlin中的[字符串插值](https://www.baeldung.com/kotlin/string-templates)允许我们轻松地拼接常量字符串和变量以优雅地构建另一个字符串。

在本文中，我们将通过查看生成的字节码来了解这种插值是如何工作的。此外，我们还将研究当前实现的未来可能的优化。

## 2. 字符串插值

让我们从一个简单而熟悉的例子开始：

```kotlin
class Person(val firstName: String, val lastName: String, val age: Int) {
    override fun toString(): String {
        return "$firstName $lastName is $age years old"
    }
}
```

如上所示，我们使用字符串插值来实现toString()方法，为了查看Kotlin编译器如何实现此功能，我们应该首先通过kotlinc编译此类：

```bash
>> kotlinc interpolation.kt
```

然后我们可以使用[javap](https://www.baeldung.com/java-class-view-bytecode)工具查看生成的字节码：

```bash
>> javap -c -p cn.tuyucheng.taketoday.interpolation.Person
// truncated
public java.lang.String toString();
    Code:
       0: new           #9   // class StringBuilder
       3: dup
       4: invokespecial #13  // Method StringBuilder."<init>":()V
       7: aload_0
       8: getfield      #17  // Field firstName:LString;
      11: invokevirtual #21  // Method StringBuilder.append:(LString;)LStringBuilder;
      14: bipush        32
      16: invokevirtual #24  // Method StringBuilder.append:(C)LStringBuilder;
      19: aload_0
      20: getfield      #27  // Field lastName:LString;
      23: invokevirtual #21  // Method StringBuilder.append:(LString;)LStringBuilder;
      26: ldc           #29  // String  is
      28: invokevirtual #21  // Method StringBuilder.append:(LString;)LStringBuilder;
      31: aload_0
      32: getfield      #33  // Field age:I
      35: invokevirtual #36  // Method StringBuilder.append:(I)LStringBuilder;
      38: ldc           #38  // String  years old
      40: invokevirtual #21  // Method StringBuilder.append:(LString;)LStringBuilder;
      43: invokevirtual #40  // Method StringBuilder.toString:()LString;
      46: areturn
```

**这组字节码指令创建一个**[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)**实例并使用append()方法追加字符串的每个部分**。基本上，这个字节码等价于下面的Java代码：

```java
new StringBuilder()
    .append(firstName)
    .append(' ') // ascii code 32 or space
    .append(lastName)
    .append(" is ")
    .append(age)
    .append(" years old")
    .toString()
```

**因此，字符串插值功能在底层使用StringBuilder类**。

### 2.1 优点和缺点

从好的方面来说，StringBuilder实现对于许多Java和Kotlin开发人员来说非常简单易懂。

但是，**随着模板变量数量的增加，字节码会变长**，这反过来会影响JVM的启动时间，因为有更多的字节码需要处理和验证。

此外，拼接策略在编译时是固定的。因此，如果更新的编译器版本使用更高效的方法，**我们应该重新编译旧代码以利用这种高效的实现**。

让我们看看Java 9和Kotlin 1.4.20如何解决这些问题。

## 3. Invoke Dynamic

[Invoke Dynamic(也称为Indy)](http://baeldung.com/java-invoke-dynamic)是[JSR 292](https://jcp.org/en/jsr/detail?id=292)的一部分，旨在增强JVM对动态类型语言的支持。从Java 9开始，作为[JEP 280](https://openjdk.java.net/jeps/280)的一部分，Java中的字符串拼接使用invoke dynamic。

**这背后的主要动机是要有一个更动态的实现**，也就是说，可以在不更改字节码的情况下更改拼接策略，这样，客户无需重新编译即可从新的优化策略中获益。生成的字节码也变得更小，有助于更快的启动时间。

[从Kotlin 1.4.20开始](https://blog.jetbrains.com/kotlin/2020/11/kotlin-1-4-20-released/)，Kotlin编译器可以利用indy进行字符串拼接。为此，我们必须做两件事：

-   使用9或更高版本作为JVM目标，因为使用indy的字符串拼接仅在Java 9+上可用
-   使用-Xstring-concat编译器标志启用invoke dynamic字符串拼接

因此，如果我们使用这些标志重新编译相同的代码：

```bash
>> kotlinc -jvm-target 9 -Xstring-concat=indy-with-constants interpolation.kt
```

那么字节码将是：

```bash
>> javap -c -p cn.tuyucheng.taketoday.interpolation.Person
public java.lang.String toString();
    Code:
       0: aload_0
       1: getfield      #11       // Field firstName:LString;
       4: aload_0
       5: getfield      #14       // Field lastName:LString;
       8: aload_0
       9: getfield      #18       // Field age:I
      12: invokedynamic #30,  0   // InvokeDynamic #0:makeConcatWithConstants:(LString;LString;I)LString;
      17: areturn

```

如上所示，**无论模板变量的数量如何，字节码都是相同的**-更简单、更可靠、更不冗长！

请注意，这仅适用于具有9+ JVM目标的Java 9+和Kotlin 1.4.20+。此外，还有一个计划使Indy成为Kotlin 1.5中的默认实现。

## 4. 总结

在这篇简短的文章中，我们看到了Kotlin中字符串插值的两种不同实现：一种使用StringBuilder，另一种使用invoke dynamic。
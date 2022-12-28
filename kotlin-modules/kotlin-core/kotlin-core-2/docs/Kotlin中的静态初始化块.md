## 1. 概述

在这个快速教程中，我们将了解如何使用伴生对象来静态初始化类。

首先，我们将快速浏览一下Java中的静态初始化，然后我们将在Kotlin中实现相同的目标。在此过程中，我们将了解Kotlin解决方案的字节码表示形式。

## 2. 静态初始化

在Java中，要初始化类的静态组件，我们可以使用[静态初始化块](https://www.baeldung.com/java-static#a-static-block)：

```java
static {
    // put static initializers here
}
```

Kotlin中没有静态成员和静态初始化器，至少与Java类似。但是，**我们可以使用伴生对象在Kotlin中实现相同的目的**：

```kotlin
class Static {

    companion object {
        lateinit var answer: String

        init {
            answer = "42"
            println("Initialized")
        }
    }
}
```

**我们所要做的就是在类的伴生对象中声明一个init块**，这样，我们得到与Java的静态块相同的行为。

简而言之，当JVM将要初始化封闭类时，它将执行伴随对象内的init块。

## 3. 字节码表示

为了看一下生成的字节码，首先，我们应该使用kotlinc编译Kotlin类：

```bash
$ kotlinc Static.kt
```

现在，让我们使用javap工具查看字节码：

```bash
$ javap -c -p -v Static 
Compiled from "Static.kt"
public final class cn.tuyucheng.taketoday.staticinit.Static {
    public static java.lang.String answer;
  
    // truncated
}
```

如上所示，伴生对象中的变量在封闭类中被声明为静态字段。此外，伴生对象本身是同一个封闭类中的静态内部类：

```shell
InnerClasses:
  public static final #15= #37 of #2; // Companion=class Static$Companion of class Static
```

每个伴生对象都是一个单例，因为编译器创建它的单个实例：

```bash
public final class cn.tuyucheng.taketoday.staticinit.Static {

    public static final Static$Companion Companion;
    // truncated 
}
```

最后，**init块本身被编译为底层的静态初始化块**：

```bash
static {};
    Code:
       0: new           #37     // class Static$Companion
       3: dup
       4: aconst_null
       5: invokespecial #40     // Method Static$Companion."<init>":(LDefaultConstructorMarker;)V
       8: putstatic     #42     // Field Companion:LStatic$Companion;
      11: ldc           #44     // String 42
      13: putstatic     #20     // Field answer:LString;
      16: ldc           #46     // String Initialized
      18: astore_0
      19: iconst_0
      20: istore_1
      21: getstatic     #52     // Field System.out:LPrintStream;
      24: aload_0
      25: invokevirtual #58     // Method PrintStream.println:(LObject;)V
      28: return
```

在执行init块中的逻辑之前，JVM创建伴生类的实例(索引0到5)，并将其作为静态成员存储在封闭类(索引8)中。只有在那之后，JVM才会执行初始化逻辑。

**最重要的是，尽管它们在语言级别存在差异，但静态初始化程序块和伴生对象初始化程序在字节码级别是相同的**。

## 4. 总结

在这个简短的教程中，首先，我们快速回顾了Java静态初始化器的外观，然后我们看到我们可以使用伴生对象和init块在Kotlin中实现同样的事情。最后，通过查看生成的字节码，我们了解到static和init块在Kotlin和Java中的底层表示相同。
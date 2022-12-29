## 1. 概述

在本教程中，我们将介绍如何编译Kotlin类。之后，我们将展示如何从命令行运行该类。最后，我们将演示如何操作主类名。

## 2. 编译类

为了演示一个类的编译和执行，我们先定义一个简单的类：

```kotlin
package cn.tuyucheng.taketoday.run

class RunClass {
    fun printInsideClass() {
        println("Running inside the RunClass")
    }
}

fun main(args: Array<String>) {
    println("Running the main function")
    RunClass().printInsideClass()
}
```

它由main函数组成，该函数是运行RunClass时的入口点，**main函数是运行类所必需的，args参数是可选的**。此外，它还包含一个函数printInsideClass，以表明它也将被触发。

现在让我们编译它，对于编译，我们将使用[kotlinc](https://www.baeldung.com/kotlin/multiplatform-programming#1-kotlinjvm-compiler)命令。首先，我们可以使用依赖项来编译它：

```bash
$ kotlinc RunClass.kt -include-runtime -d example.jar
```

它生成一个可运行的JAR(example.jar)，其中包含Kotlin的运行时依赖项。

此外，我们可以在不使用-include-runtime选项的情况下运行相同的命令，它将生成一个没有运行时的库JAR，如果我们想运行它，我们必须将运行时添加到类路径中。

最后，我们可以只编译类本身：

```bash
$ kotlinc RunClass.kt
```

它生成两个类，RunClass.class和RunClassKt.class。 

## 3. 从命令行执行一个类

现在让我们执行这个类，**如果JAR包含Kotlin的运行时，我们可以简单地运行它**：

```bash
$ java -jar example.jar
Running the main function
Running inside the RunClass
```

正如我们所见，它将两条语句打印到控制台。

现在让我们**运行本身不包含Kotlin依赖项的JAR**：

```bash
$ java -cp "kotlin-stdlib.jar;example.jar" cn.tuyucheng.taketoday.run.RunClassKt
Running the main function
Running inside the RunClass
```

首先，kotlin-stdlib.jar在类路径中，它提供了Kotlin的所有依赖项。出于本练习的目的，我们将运行时放在与example.jar相同的目录中，默认情况下，它在Kotlin编译器的lib目录中提供。

此外，example.jar包含我们的类，最后提供的参数是我们的类名。

现在，**让我们运行这个没有归档到JAR中的类**。上面的编译产生了两个类，以“Kt”结尾的包含main函数。

让我们执行RunClassKt类：

```bash
$ java -cp ".;kotlin-stdlib.jar" cn.tuyucheng.taketoday.run.RunClassKt
Running the main function
Running inside the RunClass
```

我们将编译RunClass的目录添加到类路径中，此外，与之前的案例一样，我们添加了Kotlin的运行时。最后，类名作为最后一个参数。

## 4. 更改主类的名称

现在让我们看看如何操作可运行类的名称。首先，我们可以将main函数放在[伴生](https://www.baeldung.com/kotlin/companion-objects-in-java#declaring-a-companion-object)对象中：

```kotlin
class RunClass {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Running main function")
        }
    }
}
```

使用kotlinc RunClass.kt编译后，我们可以使用RunClass名称运行该类：

```bash
$ java -cp ".;kotlin-stdlib.jar" cn.tuyucheng.taketoday.run.RunClass
Running main function
```

之后，让**我们使用**[JvmName](https://www.baeldung.com/kotlin/jvm-annotations#jvm-name)**注解来设置类名**，该值表示类名，**将在字节码中生成**：

```kotlin
@file:JvmName("CustomName")

package cn.tuyucheng.taketoday.run

class RunClass {
    fun printInsideClass() {
        println("Running inside the RunClass")
    }
}

fun main(args: Array<String>) {
    println("Running the main function")
    RunClass().printInsideClass()
}
```

让我们使用kotlinc RunClass.kt编译这个类并执行它：

```bash
$ java -cp ".;kotlin-stdlib.jar" cn.tuyucheng.taketoday.run.CustomName
Running the main function
Running inside the RunClass
```

也就是说，我们能够运行CustomName类。

## 5. 总结

在这篇简短的文章中，我们展示了如何从命令行编译和运行Kotlin类，此外，我们介绍了如何更改类名。
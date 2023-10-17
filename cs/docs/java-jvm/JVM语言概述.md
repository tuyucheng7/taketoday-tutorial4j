## **一、简介**

除了 Java，其他语言也可以在 Java 虚拟机上运行，如 Scala、Kotlin、Groovy、Clojure。

在接下来的部分中，我们将深入了解最流行的 JVM 语言。

当然，我们将从 JVM 语言的先驱 – Java 开始。

## **2.Java**

### **2.1. 概述**

[Java](https://java.com/en/)是一种包含面向对象范式的通用编程语言。

**该语言的一个核心特性是跨平台可移植性**，这意味着在一个平台上编写的程序可以在具有足够运行时支持的任何软件和硬件组合上执行。这是通过首先将代码编译成字节码而不是直接编译成特定于平台的机器码来实现的。

Java 字节码指令类似于机器码，但它们由特定于主机操作系统和硬件组合的 Java 虚拟机 (JVM) 解释。

**尽管最初是一种面向对象的语言，但 Java 已经开始采用其他编程范式（如[函数式编程）](https://www.baeldung.com/cs/functional-programming)的概念。**

让我们快速浏览一下 Java 的一些主要特性：

-   面向对象
-   强[静态类型](https://www.baeldung.com/cs/statically-vs-dynamically-typed-languages)
-   平台无关
-   垃圾回收
-   多线程

### **2.2. 例子**

让我们看看一个简单的“你好，世界！” 例子看起来像：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}复制
```

在此示例中，我们创建了一个名为*HelloWorld*的类，并定义了在控制台上打印一条消息的主要方法。

接下来，**我们将使用\*javac\* 命令生成**可以在 JVM 上执行的字节码：

```bash
javac HelloWorld.java复制
```

最后***java\*****命令在JVM上执行生成的字节码：**

```bash
java HelloWorld复制
```

有关更多 Java 示例，请查看我们的[教程列表](https://www.baeldung.com/java-tutorial)。

## **3.斯卡拉**

### **3.1. 概述**

[Scala](https://www.scala-lang.org/)代表“可扩展语言”。**Scala 是一种静态类型语言，它结合了两种重要的编程范式，即面向对象和函数式编程。** 

该语言起源于 2004 年，但近年来变得更加流行。

Scala 是一种纯面向对象的语言，因为**它不支持原语**。Scala 提供了定义类、对象、方法以及函数式编程特性（如特征、代数数据类型或类型类）的能力。

Scala 的几个重要特性是：

-   函数式，面向对象
-   强静态类型
-   代数数据类型
-   模式匹配
-   增强的不变性支持
-   惰性计算
-   多线程

### **3.2. 例子**

首先，让我们看一下同样的“Hello, World!” 示例如前，这次是在 Scala 中：

```scala
object HelloWorld {
    def main(args: Array[String]): Unit = println("Hello, world!")
}复制
```

在这个例子中，我们创建了一个名为*HelloWorld*的单例对象和*main*方法。

接下来，要编译它，我们可以使用 *scalac*：

```bash
scalac HelloWorld.scala复制
```

*scala*命令在 JVM 上执行生成的字节码：

```bash
scala HelloWorld复制
```

## 4.**科特林**

### **4.1. 概述**

**[Kotlin是由](https://kotlinlang.org/)[JetBrains](https://en.wikipedia.org/wiki/JetBrains) 团队开发的一种静态类型、通用、开源语言** ，它汇集了面向对象和功能范式。

开发 Kotlin 的主要重点是 Java 互操作性、安全性（异常处理）、简洁性和更好的工具支持。

自 Android Studio 3.0 发布以来，Kotlin 是谷歌在 Android 平台上完全支持的编程语言。它也作为标准 Java 编译器的替代品包含在 Android Studio IDE 包中。

一些重要的 Kotlin 特性：

-   面向对象+函数式
-   强静态类型
-   简洁的
-   可与 Java 互操作

我们[对 Kotlin 的介绍](https://www.baeldung.com/kotlin)还包含有关这些功能的更多细节。

### **4.2. 例子**

让我们看看“你好，世界！” 科特林中的例子：

```java
fun main(args: Array<String>) { println("Hello, World!") }复制
```

我们可以将上面的代码写在一个名为 *helloWorld.kt 的新文件中。*

然后，**我们将使用 \*kotlinc\* 命令编译它**并生成可以在 JVM 上执行的字节码：

```bash
kotlinc helloWorld.kt -include-runtime -d helloWorld.jar复制
```

*-d*选项用于指示*类*文件或*.jar*文件名的输出文件。**-include \*-runtime\*选项通过在其中包含 Kotlin 运行时库，使生成的\*.jar\*文件独立且可运行。**

然后，*java* 命令在JVM上执行生成的字节码：

```bash
java -jar helloWorld.jar复制
```

*让我们再看看另一个使用for*循环打印项目列表的例子：

```java
fun main(args: Array<String>) {
    val items = listOf(1, 2, 3, 4)
    for (i in items) println(i)
}复制
```

## **5. 时髦**

### **5.1. 概述**

[**Groovy**](http://groovy-lang.org/)**是一种面向对象的、可选类型的动态领域特定语言 (DSL)**，支持静态类型和静态编译功能。它旨在通过易于学习的语法提高开发人员的工作效率。

**Groovy 可以轻松地与任何 Java 程序集成**，并立即添加强大的功能，例如脚本功能、运行时和编译时元编程以及函数式编程功能。

让我们重点介绍几个重要的功能：

-   面向对象，具有高阶函数、柯里化、闭包等功能特性
-   打字 - 动态，静态，强，鸭
-   领域特定语言
-   与 Java 的互操作性
-   简洁的生产力
-   运算符重载

### **5.2. 例子**

首先，让我们看看我们的“Hello, World!” Groovy 中的示例：

```groovy
println("Hello world")复制
```

我们将上面的代码写在一个名为*HelloWorld.groovy*的新文件中。现在**我们可以通过两种方式运行这段代码：先编译再执行，或者只运行未编译的代码。**

我们可以 使用*groovyc命令编译一个**.groovy*文件，如下所示：

```bash
groovyc HelloWorld.groovy复制
```

然后，我们将使用 *java* 命令来执行 groovy 代码：

```bash
java -cp <GROOVY_HOME>\embeddable\groovy-all-<VERSION>.jar;. HelloWorld复制
```

例如，上面的命令可能如下所示：

```bash
java -cp C:\utils\groovy-1.8.1\embeddable\groovy-all-1.8.1.jar;. HelloWorld复制
```

我们还看看如何使用 *groovy*命令在不编译的情况下执行*.groovy*文件：

```bash
groovy HelloWorld.groovy复制
```

最后，这是另一个打印带有索引的项目列表的示例：

```groovy
list = [1, 2, 3, 4, 5, 6, 7, 8, 9]
list.eachWithIndex { it, i -> println "$i: $it"}复制
```

[在我们的介绍文章](https://www.baeldung.com/groovy-language)中查看更多 Groovy 示例。

## **6. 克罗贾尔**

### **6.1. 概述**

[**Clojure**](https://clojure.org/)**是一种通用的函数式编程语言。**该语言在 JVM 以及 Microsoft 的 [Common Language Runtime](https://en.wikipedia.org/wiki/Common_Language_Runtime)上运行。Clojure 仍然是一种编译语言，它仍然是动态的，因为它的特性在运行时得到支持。

Clojure 设计者想要设计 可以在 JVM 上运行的现代[Lisp 。](https://en.wikipedia.org/wiki/Lisp_(programming_language))这就是为什么它也被称为 Lisp 编程语言的一种方言。 **与 Lisps 类似，Clojure 将代码视为数据，也有一个宏系统。**

一些重要的 Clojure 特性：

-   功能性
-   Typing – Dynamic, Strong，最近开始支持[渐进式打字](https://en.wikipedia.org/wiki/Gradual_typing)
-   为并发而设计
-   运行时多态性

### **6.2. 例子**

与其他 JVM 语言不同，创建简单的“Hello, World!”并不是那么简单。Clojure 中的程序。

我们将使用 [Leiningen](https://leiningen.org/) 工具来运行我们的示例。

首先，我们将使用以下命令创建一个带有默认模板的简单项目：

```bash
lein new hello-world复制
```

该项目将使用以下文件结构创建：

```plaintext
./project.clj
./src
./src/hello-world
./src/hello-world/core.clj复制
```

现在我们需要 使用以下内容更新*./project.ctj文件以设置主源文件：*

```plaintext
(defproject hello-world "0.1.0-SNAPSHOT"
  :main hello-world.core
  :dependencies [[org.clojure/clojure "1.5.1"]])复制
```

现在我们准备更新代码以打印“Hello, World!” 在 *./src/hello-world/core.* .clj 文件：

```plaintext
(ns hello-world.core)

(defn -main [& args]
    (println "Hello, World!"))复制
```

最后，在移动到项目的根目录后，我们将使用 *lein*命令执行上面的代码：

```bash
cd hello-world
lein run复制
```

## 7. 其他 JVM 语言

### **7.1. 杰通**

[**Jython**](http://www.jython.org/)是 在 JVM 上运行的**[Python](https://www.python.org/)****的 Java 平台实现。**

该语言最初设计用于在不牺牲交互性的情况下编写高性能应用程序。**Jython 是面向对象的、多线程的，并使用 Java 的垃圾收集器来有效地清理内存。**

Jython 包括大多数属于 Python 语言的模块。它还可以导入和使用 Java 库中的任何类。

让我们快速看一下“Hello, World!” 例子：

```python
print "Hello, world!"复制
```

### **7.2. JRuby**

**[JRuby](http://www.jruby.org/)是在 Java 虚拟机上运行的[Ruby](http://www.ruby-lang.org/en/)编程语言的实现 。**

JRuby 语言是高性能和多线程的，具有来自 Java 和 Ruby 的大量可用库。此外，它结合了两种语言的特性，如面向对象编程和鸭子类型。

让我们打印“Hello, World!” 在 JRuby 中：

```ruby
require "java"

stringHello= "Hello World"
puts "#{stringHello.to_s}"复制
```

## **八、结论**

在本文中，我们研究了许多流行的 JVM 语言以及基本的代码示例。这些语言实现了各种编程范式，如面向对象、函数式、静态类型、动态类型。

到目前为止，它表明即使 JVM 的历史可以追溯到 1995 年，它仍然是现代编程语言高度相关且引人注目的平台。
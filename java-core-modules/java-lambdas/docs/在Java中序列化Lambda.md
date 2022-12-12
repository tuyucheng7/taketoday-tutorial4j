## 1. 概述

一般来说，[Java文档](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#serialization)强烈建议我们不要序列化[lambda表达式]()，这是因为lambda表达式将生成合成结构。而且，这些合成构造存在几个潜在问题：源代码中没有相应的构造、不同Java编译器实现之间的差异以及与不同JRE实现的兼容性问题。但是，有时序列化lambda是必要的。

在本教程中，我们将解释如何序列化lambda表达式及其底层机制。

## 2. Lambda和序列化

当我们使用[Java Serialization]()来序列化或反序列化一个对象时，它的类和非静态字段都必须是可序列化的。否则将导致NotSerializableException。同样，在序列化lambda表达式时，我们必须确保其目标类型和捕获参数是可序列化的。

### 2.1 失败的Lambda序列化

在源文件中，让我们使用Runnable接口构造一个lambda表达式：

```java
public class NotSerializableLambdaExpression {
	public static Object getLambdaExpressionObject() {
		Runnable r = () -> System.out.println("please serialize this message");
		return r;
	}
}
```

当尝试序列化Runnable对象时，我们会得到一个NotSerializableException。在继续之前，让我们稍微解释一下。

当JVM遇到lambda表达式时，它会使用内置的ASM构建一个内部类。那么，这个内部类是什么样子的呢？我们可以通过在命令行上指定jdk.internal.lambda.dumpProxyClasses属性来转储这个生成的内部类：

```shell
-Djdk.internal.lambda.dumpProxyClasses=<dump directory>
```

这里要注意：当我们将<dump directory\>替换为我们的目标目录时，这个目标目录最好是空的，因为如果我们的项目依赖第三方库，JVM可能会转储相当多的意外生成的内部类。

转储后，我们可以使用适当的Java反编译器检查这个生成的内部类：

<img src="../assets/img.png">

在上图中，生成的内部类只实现了Runnable接口，即lambda表达式的目标类型。此外，在run方法中，代码将调用NotSerializableLambdaExpression.lambda$getLambdaExpressionObject$0方法，该方法由Java编译器生成，表示我们的lambda表达式实现。

因为这个生成的内部类是我们的lambda表达式的实际类，并且它没有实现Serializable接口，所以lambda表达式不适合序列化。

### 2.2 如何序列化Lambda

至此，问题就落到了重点：如何给生成的内部类添加Serializable接口呢？答案是使用结合了[函数式接口]()和Serializable接口的[交集类型](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.16)来转换lambda表达式。

例如，让我们将Runnable和Serializable组合成一个交集类型：

```java
Runnable r = (Runnable & Serializable) () -> System.out.println("please serialize this message");
```

现在，如果我们尝试序列化上面的Runnable对象，它将成功。

但是，如果我们经常这样做，就会引入很多样板代码。为了使代码更干净，我们可以定义一个同时实现Runnable和Serializable的新接口：

```java
interface SerializableRunnable extends Runnable, Serializable {
}
```

然后我们可以使用它：

```java
SerializableRunnable obj = () -> System.out.println("please serialize this message");
```

但我们也应该注意不要捕获任何不可序列化的参数。例如，让我们定义另一个接口：

```java
interface SerializableConsumer<T> extends Consumer<T>, Serializable {
}
```

然后我们可以选择System.out::println作为它的实现：

```java
SerializableConsumer<String> obj = System.out::println;
```

结果，它将导致NotSerializableException。这是因为此实现将捕获System.out变量作为其参数，该变量的类是PrintStream，它是不可序列化的。

## 3. 底层机制

说到这里，我们可能会想：引入交集类型后背后到底发生了什么？

为了有讨论的基础，我们再准备一段代码：

```java
public class SerializableLambdaExpression {
	public static Object getLambdaExpressionObject() {
		Runnable r = (Runnable & Serializable) () -> System.out.println("please serialize this message");
		return r;
	}
}
```

### 3.1 编译类文件

编译后，我们可以使用javap检查编译后的类：

```shell
javap -v -p SerializableLambdaExpression.class
```

-v选项将打印详细消息，-p选项将显示私有方法。

而且，我们可能会发现Java编译器提供了一个$deserializeLambda$方法，该方法接收一个[SerializedLambda](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/SerializedLambda.html)参数：

<img src="../assets/img_1.png">

为了可读性，让我们将上面的字节码反编译成Java代码：

<img src="../assets/img_2.png">

上面的$deserializeLambda$方法的主要职责是构造一个对象。首先，它使用lambda表达式详细信息的不同部分检查SerializedLambda的getXXX方法。然后，如果满足所有条件，它将调用SerializableLambdaExpression::lambda$getLambdaExpressionObject$36ab28bd$1方法引用来创建实例。否则，它将抛出IllegalArgumentException。

### 3.2 生成的内部类

除了检查编译后的类文件，我们还需要检查新生成的内部类。因此，我们使用jdk.internal.lambda.dumpProxyClasses属性来转储生成的内部类：

<img src="../assets/img_3.png">

在上面的代码中，新生成的内部类同时实现了Runnable和Serializable接口，这意味着它适用于序列化。并且，它还提供了一个额外的[writeReplace](https://docs.oracle.com/en/java/javase/11/docs/specs/serialization/output.html#the-writereplace-method)方法。从内部看，此方法返回一个描述lambda表达式实现细节的SerializedLambda实例。

为了形成一个闭环，还缺少一件事：序列化的lambda文件。

### 3.3 序列化的Lambda文件

由于序列化的lambda文件是以二进制格式存储的，因此我们可以使用十六进制工具来检查其内容：

<img src="../assets/img_4.png">

在[序列化流](https://www.infoworld.com/article/2072752/the-java-serialization-algorithm-revealed.html)中，十六进制“AC ED”(Base64中的“rO0”)是流幻数，十六进制“00 05”是流版本。但是，剩余的数据不是人类可读的。

根据[对象序列化流协议](https://docs.oracle.com/en/java/javase/17/docs/specs/serialization/protocol.html)，剩下的数据可以解释为：

<img src="../assets/img_5.png">

从上图中，我们可能会注意到序列化的lambda文件实际上包含了SerializedLambda类的数据。具体来说，它包含10个字段和对应的值。并且，SerializedLambda类的这些字段和值是编译类文件中的$deserializeLambda$方法和生成的内部类中的writeReplace方法之间的桥梁。

### 3.4 把所有东西放在一起

现在，是时候将不同的部分组合在一起了：

<img src="../assets/img_6.png">

当我们使用ObjectOutputStream序列化一个lambda表达式时，ObjectOutputStream会发现生成的内部类包含一个返回SerializedLambda实例的writeReplace方法。然后，ObjectOutputStream将序列化此SerializedLambda实例而不是原始对象。

接下来，当我们使用ObjectInputStream反序列化序列化的lambda文件时，会创建一个SerializedLambda实例。然后，ObjectInputStream将使用此实例来调用SerializedLambda类中定义的[readResolve](https://docs.oracle.com/en/java/javase/11/docs/specs/serialization/input.html#the-readresolve-method)。并且，readResolve方法将调用捕获类中定义的$deserializeLambda$方法。最后，我们得到了反序列化的lambda表达式。

总而言之，SerializedLambda类是lambda序列化过程的关键。

## 4. 总结

在本文中，我们首先查看了一个失败的lambda序列化示例，并解释了它失败的原因。然后，我们介绍了如何使lambda表达式可序列化。最后，我们探索了lambda序列化的底层机制。
## 一、概述

Java 8 引入了方法引用的概念。我们经常将它们视为类似于 lambda 表达式。

然而，方法引用和 lambda 表达式并不完全是一回事。在本文中，我们将展示它们为何不同以及以错误方式使用它们的风险是什么。

## 2. Lambdas 和方法引用语法

首先，让我们看几个 lambda 表达式的例子：

```java
Runnable r1 = () -> "some string".toUpperCase();
Consumer<String> c1 = x -> x.toUpperCase();
复制
```

以及一些方法参考示例：

```java
Function<String, String> f1 = String::toUpperCase;
Runnable r2 = "some string"::toUpperCase;
Runnable r3 = String::new;复制
```

这些示例可以让我们将方法引用视为 lambda 的缩写符号。

不过还是先看看[Oracle官方文档](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.13)吧。我们可以在那里找到一个有趣的例子：

```java
(test ? list.replaceAll(String::trim) : list) :: iterator复制
```

正如我们所看到的，Java 语言规范允许我们在双冒号运算符之前有不同种类的表达式。**::**之前的部分称为**目标** **引用**。

接下来，我们将讨论方法参考评估的过程。

## 三、方法参考评价

当我们运行以下代码时会发生什么？

```java
public static void main(String[] args) {
    Runnable runnable = (f("some") + f("string"))::toUpperCase;
}

private static String f(String string) {
    System.out.println(string);
    return string;
}复制
```

我们刚刚创建了一个*Runnable*对象。仅此而已。但是，输出是：

```bash
some
string
复制
```

发生这种情况是因为**在首次发现声明时对目标引用进行了评估。**因此，我们失去了理想的惰性。**目标引用也只被评估一次。**因此，如果我们将这一行添加到上面的示例中：

```java
runnable.run()复制
```

我们不会看到任何输出。下一个案子呢？

```java
SomeWorker worker = null;
Runnable workLambda = () -> worker.work() // ok
Runnable workMethodReference = worker::work; // boom! NullPointerException复制
```

[前面提到的文档](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.13)提供的解释：

*“如果目标引用为空，则调用实例方法的方法调用表达式 (§15.12) 将抛出 NullPointerException。”*

防止意外情况的最好方法可能是**永远不要使用变量访问和复杂表达式作为目标引用**。

一个好主意可能是将方法引用仅用作其 lambda 等价物的简洁、简短的表示法。**在::**运算符之前只有一个类名可以保证安全。

## 4。结论

在本文中，我们了解了方法引用的评估过程。

我们知道我们应该遵循的风险和规则，以免突然对我们应用程序的行为感到惊讶。
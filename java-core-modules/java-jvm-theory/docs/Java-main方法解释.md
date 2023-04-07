## **一、概述**

每个程序都需要一个开始执行的地方；谈到 Java 程序，那就是*主要*方法。

我们习惯于在代码会话期间编写*main*方法，以至于我们甚至不注意它的细节。在这篇简短的文章中，我们将分析此方法并展示其他一些编写方法。

## **2. 共同签名**

最常见的main方法模板是：

```java
public static void main(String[] args) { }复制
```

这就是我们学习它的方式，这就是 IDE 为我们自动完成代码的方式。但这不是该方法可以采用的唯一形式，**我们可以使用一些有效的变体**，但并不是每个开发人员都会注意这个事实。

在我们深入研究这些方法签名之前，让我们回顾一下公共签名的每个关键字的含义：

-   *public——*访问修饰符，表示全局可见
-   *static——*方法可以直接从类中访问，我们不必实例化一个对象来获得引用并使用它
-   *void* – 表示此方法不返回值
-   *main* – 方法的名称，这是 JVM 在执行 Java 程序时查找的标识符

至于*args*参数，它表示方法接收到的值。这就是我们在第一次启动程序时将参数传递给程序的方式。

参数*args是一个**String*数组。在以下示例中：

```java
java CommonMainMethodSignature foo bar复制
```

我们正在执行一个名为*CommonMainMethodSignature*的 Java 程序并传递 2 个参数：*foo*和*bar*。*这些值可以在main*方法内部作为*args[0]*（以*foo*为值）和*args[1]*（以*bar*为值）访问。

在下一个示例中，我们将检查 args 以决定是加载测试参数还是生产参数：

```java
public static void main(String[] args) {
    if (args.length > 0) {
        if (args[0].equals("test")) {
            // load test parameters
        } else if (args[0].equals("production")) {
            // load production parameters
        }
    }
}复制
```

记住 IDE 也可以向程序传递参数总是好的。

## **3. 编写\*main()\*方法的不同方式**

让我们检查一些不同的方法来编写*main*方法。尽管它们不是很常见，但它们是有效的签名。

请注意，这些都不是特定于*main*方法的，它们可以与任何 Java 方法一起使用，但它们也是*main*方法的有效部分。

方括号可以放在*String*附近，就像在通用模板中一样，或者放在两边的*args附近：*

```java
public static void main(String []args) { }
复制
public static void main(String args[]) { }复制
```

参数可以表示为可变参数：

```java
public static void main(String...args) { }复制
```

我们甚至可以为*main()*方法添加*strictfp*，它用于处理浮点值时处理器之间的兼容性：

```java
public strictfp static void main(String[] args) { }复制
```

*synchronized*和*final*也是*main*方法的有效关键字，但它们在这里不起作用。

另一方面，可以将*final*应用于*args*以防止修改数组：

```java
public static void main(final String[] args) { }复制
```

为了结束这些示例，我们还可以使用上述所有关键字编写*main方法（当然，您可能永远不会在实际应用程序中使用）：*

```java
final static synchronized strictfp void main(final String[] args) { }复制
```

## 4. 有多个*main()*方法

我们还可以在我们的应用程序中定义**多个\*主要\*方法。**

事实上，有些人将它用作验证单个类的原始测试技术（尽管像*JUnit*这样的测试框架更适用于此活动）。

为了指定JVM 应该执行哪个*主要方法作为我们应用程序的入口点，我们使用**MANIFEST.MF*文件。在清单中，我们可以指明主类：

```plaintext
Main-Class: mypackage.ClassWithMainMethod复制
```

这主要在创建可执行*.jar*文件时使用。*我们通过位于META-INF/MANIFEST.MF*（以 UTF-8 编码）的清单文件指示哪个类具有启动执行的*主要方法。*

## **5.结论**

*本教程描述了main*方法的细节和它可以采用的一些其他形式，甚至是大多数开发人员不太常见的形式。

请记住，**尽管我们展示的所有示例在语法方面都是有效的，但它们只是用于教育目的**，大多数时候我们将坚持使用通用签名来完成我们的工作。
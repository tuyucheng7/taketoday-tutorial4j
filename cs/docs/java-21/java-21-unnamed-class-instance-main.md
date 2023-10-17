## **一、简介**

[Java 21 已经到来，在新功能中，我们可以看到 Java 如何通过未命名类和实例 main 方法](https://openjdk.org/jeps/445)变得越来越适合初学者。这些内容的引入是使 Java 成为一种更适合初学者的编程语言的关键一步。

在本教程中，我们将探索这些新功能并了解它们如何使学生的学习曲线更加平滑。

## **2. 编写基本的 Java 程序**

传统上，对于初学者来说，编写第一个 Java 程序比其他编程语言稍微复杂一些。一个基本的 Java 程序需要声明一个*公共*类。此类包含一个*public static void main(String[] args)*方法，作为程序的入口点。

所有这一切只是为了在控制台中写一个*“Hello world” ：*

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}复制
```

Java 21 极大地简化了我们编写简单程序的方式：

```java
void main() {
    System.out.println("Hello, World!");
}复制
```

我们将更详细地介绍如何使用新功能实现语法简化。

## **3. 实例主要方法**

*实例main()*方法的引入允许开发人员利用更动态的方法来初始化他们的应用程序。

### 3.1. 了解实例主要方法

这改变了 Java 程序声明其入口点的方式。事实上，Java 早期要求*公共*类中存在带有*String[]参数的*[*静态* *main()*方法](https://www.baeldung.com/java-main-method)，正如我们在上一节中看到的那样。

这个新协议更加宽松。它允许使用具有不同[访问级别的](https://www.baeldung.com/java-access-modifiers)*main()*方法：*public*、*protected*或 default （包）。

**此外，它不要求方法是\*静态的\*或具有\*String[]\*参数：**

```java
class HelloWorld {
    void main() {
        System.out.println("Hello, World!");
    }
}复制
```

### 3.2. 选择启动协议

完善的启动协议会自动选择我们程序的起点，同时考虑可用性和访问级别。

**实例\*main()\*方法应始终具有非\*私有\*访问级别**。此外，启动协议遵循特定的顺序来决定使用哪种方法：

1. 在启动类中声明的 static *void main(String[] args)方法*
2. 在启动类中声明的 static *void main()方法*
3. 在启动的类中声明或从超类继承的 void *main(String[] args)实例方法*
4.  void *main()*实例方法

**当类声明实例\*main()\*方法并继承[标准\*静态\* \*main()\*方法](https://www.baeldung.com/java-hello-world)时，系统将调用实例\*main()\*方法**。在这种情况下，[JVM](https://www.baeldung.com/jvm-vs-jre-vs-jdk#jvm)在运行时发出警告。

例如，假设我们有一个超类*HelloWorldSuper，*它实现了一个长期建立的*main()*方法：

```java
public class HelloWorldSuper {
    public static void main(String[] args) {
        System.out.println("Hello from the superclass");
    }
}复制
```

*这个超类由HelloWorldChild*类扩展：

```java
public class HelloWorldChild extends HelloWorldSuper {
    void main() {
        System.out.println("Hello, World!");
    }
}
复制
```

让我们编译超类并使用*--source 21*和*--enable-preview*标志运行子类：

```java
javac --source 21 --enable-preview HelloWorldSuper.java
java --source 21 --enable-preview HelloWorldChild复制
```

我们将在控制台中得到以下输出：

```vhdl
WARNING: "void HelloWorldChild.main()" chosen over "public static void HelloWorldSuper.main(java.lang.String[])"
Hello, World!复制
```

我们可以看到 JVM 如何警告我们程序中有两个可能的入口点。

## **4. 未命名类**

未命名课程是一项重要功能，旨在简化初学者的学习曲线。**它允许方法、字段和类在没有显式类声明的情况下存在。**

通常，在 Java 中，每个类都存在于包中，每个包也存在于模块中。然而，未命名的类存在于未命名的包和未命名的模块中。它们是*最终的*，只能扩展*Object*类，而不实现任何接口。

鉴于这一切，我们可以声明*main()*方法，而无需在代码中显式声明该类：

```java
void main() { 
    System.out.println("Hello, World!");
}复制
```

利用这两个新功能，我们成功地将程序变得非常简单，任何开始使用 Java 编程的人都可以更容易地理解。

未命名的类几乎与显式声明的类完全相同。其他方法或变量被解释为未命名类的成员，因此我们可以将它们添加到我们的类中：

```java
private String getMessage() {
    return "Hello, World!";
}
void main() {
    System.out.println(getMessage());
}
复制
```

尽管无名类简单且灵活，但它们具有固有的局限性。

**直接构造或按名称引用是不可能的，并且它们不定义任何可从其他类访问的 API**。这种不可访问性还会导致[Javadoc](https://www.baeldung.com/javadoc)工具在为此类类生成 API 文档时出现问题。然而，未来的 Java 版本可能会调整和增强这些行为。

## **5. 结论**

在本文中，我们了解到 Java 21 通过引入未命名类和实例 main() 方法，在增强用户体验方面取得了重大进展，特别是对于那些刚刚开始编程之旅的用户来说。

通过简化编程的结构，这些功能使新手能够更快地专注于逻辑思维和解决问题。
## 1. 概述

Java 语言规范没有定义甚至没有使用术语编译时常量。然而，开发人员经常使用这个术语来描述一个在编译后不会改变的值。

在本教程中，我们将探讨类常量和编译时常量之间的区别。我们将查看常量表达式，看看哪些数据类型和运算符可用于定义编译时常量。最后，我们将看几个编译时常量常用的例子。

## 2.类常量

当我们在Java中使用术语[常量](https://www.baeldung.com/java-constants-good-practices)时，我们大多数时候指的是[静态](https://www.baeldung.com/java-static) 和[最终](https://www.baeldung.com/java-final) 类变量。我们无法在编译后更改类常量的值。因此，原始类型或String的所有类常量也是编译时常量：

```java
public static final int MAXIMUM_NUMBER_OF_USERS = 10;
public static final String DEFAULT_USERNAME = "unknown";

```

可以创建不是static的常量。但是，Java 将在类的每个对象中为该常量分配内存。因此，如果常量确实只有一个值，则应将其声明为static。

Oracle 为类常量定义了命名约定。我们将它们命名为大写，单词之间用下划线分隔。但是，并非所有静态变量和最终变量都是常量。如果对象的状态可以改变，则它不是常量：

```java
public static final Logger log = LoggerFactory.getLogger(ClassConstants.class);
public static final List<String> contributorGroups = Arrays.asList("contributor", "author");
```

尽管这些是常量引用，但它们指的是可变对象。

## 3.常量表达式

Java 编译器能够在代码编译期间计算包含常量变量和某些运算符的表达式：

```java
public static final int MAXIMUM_NUMBER_OF_GUESTS = MAXIMUM_NUMBER_OF_USERS  10;
public String errorMessage = ClassConstants.DEFAULT_USERNAME + " not allowed here.";
```

像这样的表达式称为常量表达式，因为编译器会计算它们并生成一个编译时常量。如Java语言规范中所定义，以下运算符和表达式可用于常量表达式：

-   一元运算符：+、-、~、!
-   乘法运算符：、/、%
-   加法运算符：+、-
-   移位运算符：<<、>>、>>>
-   关系运算符：<、<=、>、>=
-   相等运算符：==、!=
-   按位和逻辑运算符：&、^、|
-   条件与和条件或运算符：&&、||
-   三元条件运算符：？：
-   括号内的表达式，其包含的表达式是常量表达式
-   引用常量变量的简单名称

## 4. 编译与运行时常量

如果变量的值是在编译时计算的，那么它就是[编译](https://www.baeldung.com/cs/runtime-vs-compile-time)时常量。另一方面，将在执行期间计算运行时常量值。

### 4.1. 编译时常量

如果Java变量是原始类型或String，声明为final，在其声明中初始化，并带有常量表达式，则它是编译时常量。

字符串是原始类型之上的一种特殊情况，因为它们是不可变的并且存在于[String池中](https://www.baeldung.com/java-string-pool)。因此，应用程序中运行的所有类都可以共享String值。

术语编译时常量包括类常量，但也包括使用常量表达式定义的实例和局部变量：

```java
public final int maximumLoginAttempts = 5;

public static void main(String[] args) {
    PrintWriter printWriter = System.console().writer();
    printWriter.println(ClassConstants.DEFAULT_USERNAME);

    CompileTimeVariables instance = new CompileTimeVariables();
    printWriter.println(instance.maximumLoginAttempts);

    final String username = "baeldung" + "-" + "user";
    printWriter.println(username);
}
```

只有第一个打印的变量是类常量。但是，所有三个打印变量都是编译时常量。

### 4.2. 运行时常量

运行时常量值在程序运行时不能更改。但是，每次我们运行应用程序时，它都可以有不同的值：

```java
public static void main(String[] args) {
    Console console = System.console();

    final String input = console.readLine();
    console.writer().println(input);

    final double random = Math.random();
    console.writer().println("Number: " + random);
}
```

在我们的示例中打印了两个运行时常量，一个是用户定义的值，一个是随机生成的值。

## 5.静态代码优化

Java 编译器在编译过程中静态[优化](https://www.baeldung.com/java-final-performance)所有编译时常量。因此，编译器将所有编译时常量引用替换为其实际值。编译器对使用编译时常量的任何类执行此优化。

让我们看一个例子，其中引用了另一个类的常量：

```java
PrintWriter printWriter = System.console().writer();
printWriter.write(ClassConstants.DEFAULT_USERNAME);
```

接下来，我们将编译该类并观察上面两行代码生成的字节码：

```bash
LINENUMBER 11 L1
ALOAD 1
LDC "unknown"
INVOKEVIRTUAL java/io/PrintWriter.write (Ljava/lang/String;)V
```

请注意，编译器将变量引用替换为其实际值。因此，为了更改编译时常量，我们需要重新编译所有使用它的类。否则，将继续使用旧值。

## 6.用例

让我们看一下Java中编译时常量的两个常见用例。

### 6.1. 开关语句

在为 switch 语句定义 case 时，我们需要遵守Java语言规范中定义的规则：

-   switch 语句的 case 标签需要的值要么是常量表达式，要么是枚举常量
-   与 switch 语句关联的两个 case 常量表达式不能具有相同的值

这背后的原因是编译器将 switch 语句编译成字节码tableswitch或lookupswitch。它们要求case 语句中使用的值既是编译时常量又是唯一的：

```java
private static final String VALUE_ONE = "value-one"

public static void main(String[] args) {
    final String valueTwo = "value" + "-" + "two";
    switch (args[0]) {
        case VALUE_ONE:
            break;
        case valueTwo:
            break;
        }
}
```

如果我们不在 switch 语句中使用常量值，编译器将抛出错误。但是，它将接受最终的 String或任何其他编译时常量。

### 6.2. 注解

Java 中的注解处理发生在[编译时](https://www.baeldung.com/cs/compile-load-execution-time)。实际上，这意味着注解参数只能使用编译时常量来定义：

```java
private final String deprecatedDate = "20-02-14";
private final String deprecatedTime = "22:00";

@Deprecated(since = deprecatedDate + " " + deprecatedTime)
public void deprecatedMethod() {}
```

虽然在这种情况下使用类常量更为常见，但编译器允许这种实现，因为它将值识别为不可变常量。

## 七、总结

在本文中，我们探讨了Java中的术语编译时常量。我们看到该术语包括原始类型或String的类、实例和局部变量，声明为final，在其声明中初始化，并使用常量表达式定义。

在示例中，我们看到了编译时常量和运行时常量之间的区别。我们还看到编译器使用编译时常量来执行静态代码优化。

最后，我们查看了编译时常量在 switch 语句和Java注解中的用法。
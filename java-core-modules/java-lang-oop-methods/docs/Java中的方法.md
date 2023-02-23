## 1. 概述

在Java中，[方法](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)是我们定义应用程序业务逻辑的地方。它们定义对象中包含的数据之间的交互。

在本教程中，**我们将介绍Java方法的语法、方法签名的定义以及如何调用和重载方法**。

## 2. 方法语法

首先，一个方法由六个部分组成：

-   访问修饰符：可选地，我们可以指定可以从中访问该方法的代码
-   返回类型：方法返回值的类型，如果有的话
-   方法标识符：我们给方法的名字
-   参数列表：可选的逗号分隔的方法输入列表
-   异常列表：方法可以抛出的可选异常列表
-   方法体：逻辑的定义(可以为空)

让我们看一个例子：

<img src="../assets/img.png">

让我们仔细看看Java方法的这六个部分中的每一个。

### 2.1 访问修饰符

[访问修饰符](https://www.baeldung.com/java-access-modifiers)允许我们指定哪些对象可以访问该方法。有四种可能的访问修饰符：public、protected、private和default(也称为package-private)。

**方法还可以在访问修饰符之前或之后包含static关键字**。这意味着方法属于类而不属于实例，因此，我们可以在不创建类实例的情况下调用方法。没有static关键字的方法称为实例方法，只能在类的实例上调用。

关于性能，静态方法只会在类加载期间加载到内存中一次，因此内存效率更高。

### 2.2 返回类型

方法可以将数据返回到调用它们的代码。**方法可以返回原始值或对象引用，或者如果我们使用void关键字作为返回类型，它可以不返回任何内容**。

让我们看一个void方法的例子：

```java
public void printFullName(String firstName, String lastName) {
    System.out.println(firstName + " " + lastName);
}
```

**如果我们声明一个返回类型，那么我们必须在方法体中指定一个return语句**。执行return语句后，方法体执行完毕，如果有更多语句，则不再处理。

另一方面，void方法不返回任何值，因此没有return语句。

### 2.3 方法标识符

方法标识符是我们分配给方法规范的名称。使用信息丰富的描述性名称是一种很好的做法。值得一提的是，一个方法标识符最多可以有65536个字符(虽然名称很长)。

### 2.4 参数列表

我们可以在方法的参数列表中**指定方法的输入值**，该参数列表用括号括起来。一个方法可以有0到255个以逗号分隔的参数。参数可以是[对象](https://www.baeldung.com/java-classes-objects)、[原始类型](https://www.baeldung.com/java-primitives-vs-objects)或[枚举](https://www.baeldung.com/a-guide-to-java-enums)。我们可以在方法参数级别使用Java注解(例如[Spring注解@RequestParam](https://www.baeldung.com/spring-request-param))。

### 2.5 异常列表

我们可以使用throws子句指定方法抛出哪些异常。在[受检异常](https://www.baeldung.com/java-checked-unchecked-exceptions)的情况下，我们要么必须将代码包含在[try-catch](https://www.baeldung.com/java-exceptions)子句中，要么必须在方法签名中提供throws子句。

因此，让我们看一下我们之前方法的一个更复杂的变体，它会抛出一个受检的异常：

```java
public void writeName(String name) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter("OutFile.txt"));
    out.println("Name: " + name);
    out.close();
}
```

### 2.6 方法体

**Java方法的最后一部分是方法体，它包含我们要执行的逻辑**。在方法体中，我们可以根据需要编写任意数量的代码行-或者在静态方法的情况下根本不写代码。如果我们的方法声明了返回类型，那么方法体必须包含一个return语句。

## 3. 方法签名

根据其定义，方法签名仅由两个部分组成-**方法的名称和参数列表**。

因此，让我们编写一个简单的方法：

```java
public String getName(String firstName, String lastName) {
  return firstName + " " + middleName + " " + lastName;
}
```

此方法的签名是getName(String firstName, String lastName)。

方法标识符可以是任何标识符。但是，如果我们遵循常见的Java编码约定，则方法标识符应该是一个小写的动词，后面可以跟形容词和/或名词。

## 4. 调用方法

现在，让我们探讨**如何在Java中调用方法**。按照前面的示例，我们假设这些方法包含在一个名为PersonName的[Java类](https://www.baeldung.com/java-classes-objects)中：

```java
public class PersonName {
    public String getName(String firstName, String lastName) {
        return firstName + " " + middleName + " " + lastName;
    }
}
```

由于我们的getName方法是一个实例方法而不是静态方法，为了调用方法getName，我们**需要创建类PersonName的实例**：

```java
PersonName personName = new PersonName();
String fullName = personName.getName("Alan", "Turing");
```

正如我们所看到的，我们使用创建的对象来调用getName方法。

最后，让我们看看如何调用静态方法。在[静态方法](https://www.baeldung.com/java-static)的情况下，我们不需要类实例来进行调用。相反，我们使用以类名为前缀的名称调用该方法。

让我们使用上一个示例的变体进行演示：

```java
public class PersonName {
    public static String getName(String firstName, String lastName) {
        return firstName + " " + middleName + " " + lastName;
    }
}
```

在这种情况下，方法调用为：

```java
String fullName = PersonName.getName("Alan", "Turing");
```

## 5. 方法重载

**Java允许我们拥有两个或多个具有相同标识符但参数列表不同的方法-不同的方法签名**。在这种情况下，我们说**该方法是重载的**。让我们来看一个例子：

```java
public String getName(String firstName, String lastName) {
    return getName(firstName, "", lastName);
}

public String getName(String firstName, String middleName, String lastName) {
    if (!middleName.isEqualsTo("")) {
        return firstName + " " + lastName;
    }
    return firstName + " " + middleName + " " + lastName;
}
```

方法重载对于示例中的情况很有用，在这种情况下，我们可以使用一种方法来实现相同功能的简化版本。

最后，一个好的设计习惯是确保[重载方法](https://www.baeldung.com/java-method-overload-override)以相似的方式运行。否则，如果具有相同标识符的方法以不同的方式运行，代码将变得混乱。

## 6. 总结

在本教程中，我们探讨了在Java中指定方法时涉及的Java语法部分。

特别是，我们检查了访问修饰符、返回类型、方法标识符、参数列表、异常列表和方法主体。然后我们看到了方法签名的定义，如何调用一个方法，如何重载一个方法。
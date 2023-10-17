## 1. 概述

在本教程中，**我们将学习Java语言中的变量和方法隐藏**。

首先，我们将了解每个场景的概念和目的。之后，我们将深入研究用例并检查不同的示例。

## 2. 变量隐藏

**当我们在局部作用域中声明一个属性与我们在外部作用域中已有的名称相同时，就会发生变量隐藏**。

在进入到示例之前，让我们简要回顾一下Java中可能的变量作用域。我们可以使用以下类别来定义它们：

-   局部变量：在一段代码中声明，例如方法、构造函数、任何带花括号的代码块中
-   实例变量：在类内部定义并属于对象的实例
-   类或静态变量：在类中使用static关键字声明，它们具有类级别作用域

现在，让我们针对每个单独的变量类别，通过示例来描述隐藏。

### 2.1 局部的力量

让我们看一下HideVariable类：

```java
public class HideVariable {

    private String message = "this is instance variable";

    HideVariable() {
        String message = "constructor local variable";
        System.out.println(message);
    }

    public void printLocalVariable() {
        String message = "method local variable";
        System.out.println(message);
    }

    public void printInstanceVariable() {
        String message = "method local variable";
        System.out.println(this.message);
    }
}
```

在这里，我们在4个不同的地方声明了message变量。在构造函数内部声明的局部变量和两个方法正在隐藏实例变量。

让我们测试一个对象的初始化并调用方法：

```java
HideVariable variable = new HideVariable();
variable.printLocalVariable();

variable.printInstanceVariable();
```

上面代码的输出是：

```shell
constructor local variable
method local variable
this is instance variable
```

在这里，前2个调用是检索局部变量。

要从局部作用域访问实例变量，我们可以使用this关键字，就像在printInstanceVariable()方法中显示的那样。

### 2.2 隐藏和层次结构

类似地，当子类和父类都具有同名变量时，子类的变量会向父类隐藏该变量。

假设我们有父类：

```java
public class ParentVariable {

    String instanceVariable = "parent variable";

    public void printInstanceVariable() {
        System.out.println(instanceVariable);
    }
}
```

之后我们定义一个子类：

```java
public class ChildVariable extends ParentVariable {

    String instanceVariable = "child variable";

    public void printInstanceVariable() {
        System.out.println(instanceVariable);
    }
}
```

为了测试它，让我们初始化两个实例。一个使用父类，另一个使用子类，然后对它们中的每一个调用printInstanceVariable()方法：

```java
ParentVariable parentVariable = new ParentVariable();
ParentVariable childVariable = new ChildVariable();

parentVariable.printInstanceVariable();
childVariable.printInstanceVariable();
```

输出显示属性隐藏：

```shell
parent variable
child variable
```

**在大多数情况下，我们应该避免在父类和子类中创建同名的变量**。相反，我们应该使用适当的访问修饰符，如private，并为此目的提供getter/setter方法。

## 3. 方法隐藏

方法隐藏可能发生在java中的任何层次结构中。当子类定义了一个与父类中的静态方法具有相同签名的静态方法时，子类的方法就会隐藏父类中的静态方法。要了解有关static关键字的更多信息，[这篇](https://www.baeldung.com/spring-bean-scopes)文章是一个很好的起点。

涉及实例方法的相同行为称为方法覆盖。要了解有关方法覆盖的更多信息，请在[此处](https://www.baeldung.com/java-method-overload-override)查看我们的指南。

现在，让我们看一下这个实际示例：

```java
public class BaseMethodClass {

    public static void printMessage() {
        System.out.println("base static method");
    }
}
```

BaseMethodClass有一个printMessage()静态方法。

接下来，让我们创建一个与基类具有相同签名的子类：

```java
public class ChildMethodClass extends BaseMethodClass {

    public static void printMessage() {
        System.out.println("child static method");
    }
}
```

以下是它的工作原理：

```java
ChildMethodClass.printMessage();
```

调用printMessage()方法后的输出：

```shell
child static method
```

ChildMethodClass.printMessage()隐藏了BaseMethodClass中的方法。

### 3.1 方法隐藏与覆盖

隐藏不像覆盖那样工作，因为静态方法不是多态的。覆盖仅发生在实例方法中。它支持后期绑定，因此将调用哪个方法是在运行时确定的。

**另一方面，方法隐藏适用于静态方法。因此它是在编译时确定的**。

## 4. 总结

在本文中，我们回顾了Java中隐藏方法和变量的概念。我们展示了变量隐藏和阴影的不同场景。这篇文章的一个重要亮点也是方法覆盖和隐藏的比较。
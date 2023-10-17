## 1. 概述

在Java编程语言中，字段、构造函数、方法和类都可以使用访问修饰符进行标记。**在本教程中，我们将介绍protected访问修饰符**。

## 2. protected关键字

[声明为private的元素只能由声明它们的类访问](https://www.baeldung.com/java-private-keyword#private-modifier)，而protected关键字允许从子类和同一包的成员访问。

通过使用protected关键字，我们可以决定哪些方法和字段应该被视为包或类层次结构的内部，哪些暴露给外部代码。

## 3. 声明受保护的字段、方法和构造函数

首先，让我们创建一个名为FirstClass的类，其中包含受保护的字段、方法和构造函数：

```java
public class FirstClass {

    protected String name;

    protected FirstClass(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
    }
}
```

在此示例中，通过使用protected关键字，我们已将对这些字段的访问权限授予与FirstClass相同的包中的类以及FirstClass的子类。

## 4. 访问受保护的字段、方法和构造函数

### 4.1 从同一个包

现在，让我们看看如何通过创建一个在与FirstClass相同的包中声明的新GenericClass来访问受保护的字段：

```java
public class GenericClass {

    public static void main(String[] args) {
        FirstClass first = new FirstClass("random name");
        System.out.println("FirstClass name is " + first.getName());
        first.name = "new name";
    }
}
```

由于此调用类与FirstClass在同一个包中，因此允许它访问所有受保护的字段、方法和构造函数并与之交互。

### 4.2 从不同的包

现在让我们尝试与FirstClass不同包中声明的类中的这些字段进行交互：

```java
public class SecondGenericClass {

    public static void main(String[] args) {
        FirstClass first = new FirstClass("random name");
        System.out.println("FirstClass name is " + first.getName());
        first.name = "new name";
    }
}
```

如我们所见，**我们得到了编译错误**：

```shell
The constructor FirstClass(String) is not visible
The method getName() from the type FirstClass is not visible
The field FirstClass.name is not visible
```

这正是我们使用protected关键字所期望的。这是因为SecondGenericClass与FirstClass不在同一个包中，也不是它的子类。

### 4.3 从子类

现在让我们看看当我们**声明一个扩展FirstClass但声明在不同包中的类时会发生什么**：

```java
public class SecondClass extends FirstClass {

    public SecondClass(String name) {
        super(name);
        System.out.println("SecondClass name is " + this.getName());
        this.name = "new name";
    }
}
```

正如预期的那样，我们可以访问所有受保护的字段、方法和构造函数。这是因为SecondClass是FirstClass的子类。

## 5. 受保护的内部类

在前面的示例中，我们看到了受保护的字段、方法和构造函数的作用。还有一种特殊情况-受保护的内部类。

让我们在FirstClass中创建这个空的内部类：

```java
package cn.tuyucheng.taketoday.core.modifiers;

public class FirstClass {

    // ...

    protected static class InnerClass {

    }
}
```

正如我们所看到的，这是一个静态内部类，因此可以从FirstClass实例的外部构造。但是，由于它是受保护的，**我们只能从与FirstClass相同的包中的代码实例化它**。

### 5.1 从同一个包

为了测试这一点，让我们编辑我们的GenericClass：

```java
public class GenericClass {

    public static void main(String[] args) {
        // ...
        FirstClass.InnerClass innerClass = new FirstClass.InnerClass();
    }
}
```

如我们所见，我们可以毫无问题地实例化InnerClass，因为GenericClass与FirstClass在同一个包中。

### 5.2 从不同的包

让我们尝试从我们的SecondGenericClass实例化一个InnerClass，我们知道它在FirstClass包之外：

```java
public class SecondGenericClass {

    public static void main(String[] args) {
        // ...

        FirstClass.InnerClass innerClass = new FirstClass.InnerClass();
    }
}
```

正如预期的那样，我们得到一个编译错误：

```shell
The type FirstClass.InnerClass is not visible
```

### 5.3 从子类

让我们尝试从我们的SecondClass做同样的事情：

```java
public class SecondClass extends FirstClass {

    public SecondClass(String name) {
        // ...

        FirstClass.InnerClass innerClass = new FirstClass.InnerClass();
    }
}
```

**我们期望轻松地实例化我们的InnerClass。但是，我们在这里也遇到编译错误**：

```shell
The constructor FirstClass.InnerClass() is not visible
```

让我们看一下我们的InnerClass声明：

```java
protected static class InnerClass {
}
```

我们收到此错误的主要原因是**受保护类的默认构造函数是隐式保护的**。此外，**SecondClass是FirstClass的子类，但不是InnerClass的子类**。最后，**我们还在FirstClass的包外声明了SecondClass**。

由于所有这些原因，SecondClass无法访问受保护的InnerClass构造函数。

如果我们想解决这个问题并允许我们的SecondClass实例化一个InnerClass对象，**我们可以显式声明一个公共构造函数**：

```java
protected static class InnerClass {
    public InnerClass() {
    }
}
```

通过这样做，我们不再遇到编译错误，我们现在可以从SecondClass实例化一个InnerClass。

## 6. 总结

在本快速教程中，我们讨论了Java中的protected访问修饰符。有了它，我们可以确保只向同一包中的子类和类公开所需的数据和方法。
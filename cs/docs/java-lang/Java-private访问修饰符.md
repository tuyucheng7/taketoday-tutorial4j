## 1. 概述

在Java编程语言中，字段、构造函数、方法和类都可以用[访问修饰符](https://www.baeldung.com/java-access-modifiers)来标记。在本教程中，我们将讨论Java中的私有访问修饰符。

## 2. 关键字

private访问修饰符很重要，因为它允许封装和信息隐藏，这是面向对象编程的核心原则。封装负责捆绑方法和数据，而信息隐藏是封装的结果-它隐藏了对象的内部表示。

首先要记住的是，**声明为私有的元素只能由声明它们的类访问**。

## 3. 字段

现在，我们将看到一些简单的代码示例以更好地理解该主题。

首先，让我们创建一个包含几个私有实例变量的Employee类：

```java
public class Employee {
    private String privateId;
    private boolean manager;
    // ...
}
```

在此示例中，我们将privateId变量标记为私有，因为我们想向id生成添加一些逻辑。而且，正如我们所看到的，我们对manager属性做了同样的事情，因为我们不想允许直接修改这个字段。

## 4. 构造函数

现在让我们创建一个私有构造函数：

```java
private Employee(String id, String name, boolean managerAttribute) {
    this.name = name;
    this.privateId = id + "_ID-MANAGER";
}
```

通过将我们的构造函数标记为private，我们只能在我们的类内部使用它。

**让我们添加一个静态方法，这将是我们从Employee类外部使用此私有构造函数的唯一方法**：

```java
public static Employee buildManager(String id, String name) {
    return new Employee(id, name, true);
}
```

现在，我们可以通过简单地编写以下代码来获取Employee类的经理实例：

```java
Employee manager = Employee.buildManager("123MAN","Bob");
```

当然，在幕后，buildManager方法会调用我们的私有构造函数。

## 5. 方法

现在让我们向我们的类添加一个私有方法：

```java
private void setManager(boolean manager) {
    this.manager = manager;
}
```

让我们假设出于某种原因，我们公司有一个武断的规则，其中只有名为“Carl”的员工可以晋升为经理，尽管其他类并不知道这一点。我们将创建一个具有一些逻辑的公共方法来处理调用我们的私有方法的规则：

```java
public void elevateToManager() {
    if ("Carl".equals(this.name)) {
        setManager(true);
    }
}
```

## 6. 实践

让我们看一个如何从外部使用我们的Employee类的示例：

```java
public class ExampleClass {

    public static void main(String[] args) {
        Employee employee = new Employee("Bob", "ABC123");
        employee.setPrivateId("BCD234");
        System.out.println(employee.getPrivateId());
    }
}
```

执行ExampleClass后，我们将在控制台上看到它的输出：

```shell
BCD234_ID
```

在此示例中，我们使用了公共构造函数和公共方法changeId(customId)，因为我们无法直接访问私有变量privateId。

让我们看看**如果我们尝试从Employee类外部访问私有方法、构造函数或变量会发生什么**：

```java
public class ExampleClass {

    public static void main(String[] args) {
        Employee employee = new Employee("Bob", "ABC123", true);
        employee.setManager(true);
        employee.privateId = "ABC234";
    }
}
```

**对于每个非法语句，我们都会得到编译错误**：

```shell
The constructor Employee(String, String, boolean) is not visible
The method setManager(boolean) from the type Employee is not visible
The field Employee.privateId is not visible
```

## 7. 类

**有一种特殊情况，我们可以创建一个私有类-作为其他类的内部类**。否则，如果我们要将一个外部类声明为private，我们将禁止其他类访问它，从而使其无用：

```java
public class PublicOuterClass {

    public PrivateInnerClass getInnerClassInstance() {
        PrivateInnerClass myPrivateClassInstance = this.new PrivateInnerClass();
        myPrivateClassInstance.id = "ID1";
        myPrivateClassInstance.name = "Bob";
        return myPrivateClassInstance;
    }

    private class PrivateInnerClass {
        public String name;
        public String id;
    }
}
```

在此示例中，我们通过指定私有访问修饰符在PublicOuterClass中创建了一个私有内部类。

因为我们使用了private关键字，**如果我们出于某种原因尝试从PublicOuterClass外部实例化我们的PrivateInnerClass，代码将无法编译**，我们将看到错误：

```shell
PrivateInnerClass cannot be resolved to a type
```

## 8. 总结

在本快速教程中，我们讨论了Java中的private访问修饰符。这是实现封装的好方法，这会导致信息隐藏。因此，我们可以确保只向其他类公开我们想要的数据和行为。
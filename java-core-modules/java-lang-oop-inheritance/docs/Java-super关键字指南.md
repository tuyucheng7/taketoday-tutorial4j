## 1. 概述

在这个快速教程中，**我们将了解super Java关键字**。

**简单的说，我们可以使用super关键字来访问父类**。 

下面我们就来探讨一下super关键字在语言中的应用。

## 2. 构造函数与super关键字

**我们可以使用super()来调用父默认构造函数**，它应该是构造函数中的第一条语句。

在我们的示例中，我们将super(message)与String参数一起使用：

```java
public class SuperSub extends SuperBase {

    public SuperSub(String message) {
        super(message);
    }
}
```

让我们创建一个子类实例，看看背后发生了什么：

```java
SuperSub child = new SuperSub("message from the child class");
```

new关键字调用SuperSub的构造函数，该构造函数本身首先调用父构造函数并将String参数传递给它。

## 3. 访问父类变量

让我们创建一个包含message实例变量的父类：

```java
public class SuperBase {
    String message = "super class";

    // default constructor

    public SuperBase(String message) {
        this.message = message;
    }
}
```

现在，我们创建一个具有相同名称变量的子类：

```java
public class SuperSub extends SuperBase {

    String message = "child class";

    public void getParentMessage() {
        System.out.println(super.message);
    }
}
```

我们可以使用super关键字从子类访问父变量。

## 4. 方法覆盖与super关键字

在继续之前，我们建议你查看我们的[方法覆盖](https://www.baeldung.com/java-method-overload-override)指南。

让我们向父类添加一个实例方法：

```java
public class SuperBase {

    String message = "super class";

    public void printMessage() {
        System.out.println(message);
    }
}
```

并在子类中覆盖printMessage()方法：

```java
public class SuperSub extends SuperBase {

    String message = "child class";

    public SuperSub() {
        super.printMessage();
        printMessage();
    }

    public void printMessage() {
        System.out.println(message);
    }
}
```

**我们可以使用super从子类访问父类被覆盖的方法**。构造函数中的super.printMessage()调用来自SuperBase的父方法。

## 5. 总结

在本文中，我们探讨了super关键字。
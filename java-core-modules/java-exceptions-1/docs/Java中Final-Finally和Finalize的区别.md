## 1. 概述

在本教程中，我们将概述三个Java关键字： final、finally 和 finalize。

虽然这些关键字彼此相似，但在Java中每个关键字都有非常不同的含义。我们将了解它们每个的用途，并通过一些代码查看一些示例。

## 2.final关键字_

让我们首先看一下 final关键字，在哪里使用它以及为什么使用它。我们可以将 final关键字应用于类、方法、字段、变量和方法参数声明。

但是，它对它们中的每一个都没有相同的影响：

-   将一个类设置为 final意味着无法扩展该类
-   将final添加到方法意味着无法重写该方法
-   最后，将 final放在字段、变量或参数的前面意味着一旦引用被赋值就不能更改(但是，如果引用是指向可变对象的，它的内部状态可能会更改，尽管它是 final 的)

[可以在此处](https://www.baeldung.com/java-final)找到有关final关键字的详细文章。

让我们 通过一些例子看看final关键字是如何工作的。

### 2.1. final字段、参数和变量

让我们创建一个 包含两个int 字段、一个final字段和一个常规非 final 字段的Parent类：

```java
public class Parent {

    int field1 = 1;
    final int field2 = 2;

    Parent() {
        field1 = 2; // OK
        field2 = 3; // Compilation error
    }

}
```

如我们所见，编译器禁止我们为 field2分配新值。

现在让我们添加一个带有常规参数和最终参数的方法：

```java
    void method1(int arg1, final int arg2) {
        arg1 = 2; // OK
        arg2 = 3; // Compilation error
    }
```

与字段类似，不可能将某些内容分配给arg2，因为它被声明为 final。

我们现在可以添加第二种方法来说明它如何处理局部变量：

```java
    void method2() {
        final int localVar = 2; // OK
        localVar = 3; // Compilation error
    }
```

没什么奇怪的，编译器不允许我们在第一次赋值后给localVar赋新值。

### 2.2. 最终方法

现在假设我们将method2设置为 final 并创建Parent的子类，比方说Child，我们在其中尝试重写它的两个超类方法：

```java
public class Child extends Parent {

    @Override
    void method1(int arg1, int arg2) {
        // OK
    }
    
    @Override
    final void method2() {
        // Compilation error
    }

}
```

如我们所见，重写method1()没有问题，但在尝试重写method2()时出现编译错误。

### 2.3. 最后一堂课

最后，让我们将Child类设置为 final 并尝试创建它的子类GrandChild：

```java
public final class Child extends Parent { 
    // ... 
}
public class GrandChild extends Child {
    // Compilation error
}
```

编译器再次抱怨。Child 类是最终类，因此无法扩展。

## 3.最后阻止

finally块 是一个可选块，用于 try/catch语句。在这个块中，我们包括在try/catch 结构之后执行的代码 ，无论是否抛出异常。

如果我们包含一个finally块，甚至可以在 没有任何 catch块的情况下将它与try块一起使用。代码将在尝试后或抛出异常后执行 。

[我们在这里](https://www.baeldung.com/java-exceptions)有一篇关于Java异常处理的深入文章。

现在让我们在一个简短的示例中演示 finally块。我们将创建一个具有try/catch/finally结构的虚拟 main()方法：

```java
public static void main(String args[]) {
    try {
        System.out.println("Execute try block");
        throw new Exception();
    } catch (Exception e) {
        System.out.println("Execute catch block");
    } finally {
        System.out.println("Execute finally block");
    }
}
```

如果我们运行这段代码，它会输出以下内容：

```plaintext
Execute try block
Execute catch block
Execute finally block
```

现在让我们通过删除 catch 块来修改该方法(并向 签名添加throws Exception )：

```java
public static void main(String args[]) throws Exception {
    try {
        System.out.println("Execute try block");
        throw new Exception();
    } finally {
        System.out.println("Execute finally block");
    }
}
```

现在的输出是：

```plaintext
Execute try block
Execute finally block
```

如果我们现在删除 throw new Exception()指令，我们可以观察到输出保持不变。我们的 finally块执行每次都会发生。

## 4.finalize 方法_

最后， finalize方法是一个受保护的方法，在[Object 类](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#finalize())中定义 。它由 垃圾收集器在不再引用的对象上调用，并且已被选择用于垃圾收集。

与任何其他非 final 方法一样，我们可以覆盖此方法来定义对象在被垃圾收集器收集时必须具有的行为。

[同样，可以在此处](https://www.baeldung.com/java-finalize)找到有关finalize方法的详细文章。

让我们看一个它如何工作的例子。我们将使用 System.gc()来建议JVM 触发 垃圾回收：

```java
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Execute finalize method");
        super.finalize();
    }
    public static void main(String[] args) throws Exception {
        FinalizeObject object = new FinalizeObject();
        object = null;
        System.gc();
        Thread.sleep(1000);
    }
```

在此示例中，我们重写 对象中的finalize()方法并创建一个main()方法，该方法实例化我们的对象并通过将创建的变量设置为null来立即删除引用。

之后，我们调用 System.gc()来运行 垃圾收集器 (至少我们希望它运行)并等待一秒钟(只是为了确保 JVM在垃圾收集器有机会调用finalize之前不会关闭 ()方法)。

此代码执行的输出应为：

```plaintext
Execute finalize method
```

请注意，覆盖 finalize()方法被认为是不好的做法，因为它的执行取决于JVM手中的 垃圾收集。另外，此方法自Java9 以来已被 弃用。

## 5.总结

在本文中，我们简要讨论了三个Java相似关键字之间的区别： final、finally和 finalize。
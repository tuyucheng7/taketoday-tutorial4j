## 1. 概述

在这个简短的教程中，我们将展示如何在调用超类型构造函数之前得到错误Cannot reference “X”，以及如何避免它。

## 2. 构造函数链

一个构造函数可以调用另一个构造函数，此调用必须位于其函数主体的第一行。

我们可以用关键字this调用同一个类的构造函数，也可以用关键字super调用超类的构造函数。

**当构造函数不调用另一个构造函数时，编译器会添加对超类的无参数构造函数的调用**。

## 3. 我们的编译错误

这个错误归结为**在我们调用构造函数链之前试图访问实例级成员**。

让我们看看我们可能会遇到这种情况的几种方式。

### 3.1 引用实例方法

在下一个示例中，我们将在第5行看到**编译错误Cannot reference “X” before supertype constructor has been called**。请注意，构造函数过早地尝试使用实例方法getErrorCode()：

```java
public class MyException extends RuntimeException {
    private int errorCode = 0;

    public MyException(String message) {
        super(message + getErrorCode()); // compilation error
    }

    public int getErrorCode() {
        return errorCode;
    }
}
```

这个错误是因为，**在super()完成之前，没有类MyException的实例**。因此，我们还不能调用实例方法getErrorCode()。

### 3.2 引用实例字段

在下一个示例中，我们将看到带有实例字段而不是实例方法的异常。让我们看一下**第一个构造函数如何在实例本身准备就绪之前尝试使用实例成员**：

```java
public class MyClass {

    private int myField1 = 10;
    private int myField2;

    public MyClass() {
        this(myField1); // compilation error
    }

    public MyClass(int i) {
        myField2 = i;
    }
}
```

对实例字段的引用只能在其类初始化后进行，这意味着在调用this()或super()之后。

那么，为什么同样使用实例字段的第二个构造函数没有出现编译错误呢？

请记住，**所有类都是从类Object隐式派生的**，因此编译器添加了一个隐式super()调用：

```java
public MyClass(int i) {
    super(); // added by compiler
    myField2 = i;
}
```

在这里，Object的构造函数在我们访问myField2之前被调用，这意味着我们没问题。

## 4. 解决方案

这个问题的第一个可能的解决方案很简单：**我们不调用第二个构造函数**。我们在第一个构造函数中显式执行我们想在第二个构造函数中执行的操作。

在这种情况下，我们会将myField1的值复制到myField2中：

```java
public class MyClass {

    private int myField1 = 10;
    private int myField2;

    public MyClass() {
        myField2 = myField1;
    }

    public MyClass(int i) {
        myField2 = i;
    }
}
```

不过，总的来说，**我们可能需要重新考虑我们正在构建的结构**。

但是，如果我们出于充分的理由调用第二个构造函数，例如，为了避免重复代码，**我们可以将代码移到一个方法中**：

```java
public class MyClass {

    private int myField1 = 10;
    private int myField2;

    public MyClass() {
        setupMyFields(myField1);
    }

    public MyClass(int i) {
        setupMyFields(i);
    }

    private void setupMyFields(int i) {
        myField2 = i;
    }
}
```

同样，这是有效的，因为编译器在调用方法之前隐式调用了构造函数链。

第三种解决方案可能是我们使用**静态字段或方法**。如果我们将myField1更改为静态常量，那么编译器也会很高兴：

```java
public class MyClass {

    private static final int SOME_CONSTANT = 10;
    private int myField2;

    public MyClass() {
        this(SOME_CONSTANT);
    }

    public MyClass(int i) {
        myField2 = i;
    }
}
```

我们应该注意，将字段设置为静态意味着它会与该对象的所有实例共享，因此不能轻易做出更改。

要使静态成为正确答案，我们需要一个强有力的理由。例如，也许这个值实际上不是一个字段，而是一个常量，因此将它设为静态和最终是有意义的。也许我们要调用的构造方法不需要访问类的实例成员，这意味着它应该是静态的。

## 5. 总结

我们在本文中看到在super()或this()调用之前引用实例成员如何导致编译错误。我们看到这种情况发生在显式声明的基类和隐式Object基类中。

我们还证明了这是构造函数设计的一个问题，并展示了如何通过在构造函数中重复代码、委托构造后设置方法或使用常量值或静态方法来帮助构造来解决这个问题。
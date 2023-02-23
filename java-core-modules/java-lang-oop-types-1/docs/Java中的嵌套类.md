## 1. 概述

本教程快速而切题地介绍了Java语言中的嵌套类。

简单地说，Java允许我们在其他类中定义类。嵌套类使我们能够对仅在一个地方使用的类进行逻辑分组，编写更具可读性和可维护性的代码并增加封装性。

在我们开始之前，让我们看一下该语言中可用的几种嵌套类类型：

-   静态嵌套类
-   非静态嵌套类
-   本地课程
-   匿名类

在接下来的部分中，我们将详细讨论其中的每一个。

## 2. 静态嵌套类

以下是有关静态嵌套类的几点注意事项：

-   与静态成员一样，它们属于它们的封闭类，而不属于该类的实例
-   他们可以在声明中使用所有类型的访问修饰符
-   他们只能访问封闭类中的静态成员
-   他们可以定义静态和非静态成员

让我们看看如何声明一个静态嵌套类：

```java
public class Enclosing {

    private static int x = 1;

    public static class StaticNested {

        private void run() {
            // method implementation
        }
    }

    @Test
    public void test() {
        Enclosing.StaticNested nested = new Enclosing.StaticNested();
        nested.run();
    }
}
```

## 3. 非静态嵌套类

接下来，这里有一些关于非静态嵌套类的快速要点：

-   它们也被称为内部类
-   他们可以在声明中使用所有类型的访问修饰符
-   就像实例变量和方法一样，内部类与封闭类的实例相关联
-   它们可以访问封闭类的所有成员，无论它们是静态的还是非静态的
-   他们只能定义非静态成员

下面是我们如何声明一个内部类：

```java
public class Outer {

    public class Inner {
        // ...
    }
}
```

如果我们用修饰符static声明一个嵌套类，那么它就是一个静态成员。否则，它就是一个内部类。尽管在句法上的区别只是一个关键字(即static)，但在语义上这些嵌套类之间存在巨大差异。内部类实例绑定到封闭类实例，因此它们可以访问其成员。我们在选择是否使嵌套类成为内部类时应该注意这个问题。

要实例化一个内部类，我们必须首先实例化它的外围类。

让我们看看我们如何做到这一点：

```java
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
```

在接下来的小节中，我们将展示一些特殊类型的内部类。

### 3.1 局部类

局部类是一种特殊类型的内部类-其中类在方法或作用域块内定义。

让我们看看关于这种类型的课程要记住的几点：

-   他们的声明中不能有访问修饰符
-   他们可以访问封闭上下文中的静态和非静态成员
-   他们只能定义实例成员

这是一个简单的例子：

```java
public class NewEnclosing {

    void run() {
        class Local {

            void run() {
                // method implementation
            }
        }
        Local local = new Local();
        local.run();
    }

    @Test
    public void test() {
        NewEnclosing newEnclosing = new NewEnclosing();
        newEnclosing.run();
    }
}
```

### 3.2 匿名类

匿名类可用于定义接口或抽象类的实现，而无需创建可重用的实现。

让我们列出几点关于匿名类需要记住的几点：

-   他们的声明中不能有访问修饰符
-   他们可以访问封闭上下文中的静态和非静态成员
-   他们只能定义实例成员
-   它们是唯一一种不能定义构造函数或扩展/实现其他类或接口的嵌套类

要定义匿名类，我们先定义一个简单的抽象类：

```java
abstract class SimpleAbstractClass {
    abstract void run();
}
```

现在让我们看看如何定义匿名类：

```java
public class AnonymousInnerUnitTest {

    @Test
    public void whenRunAnonymousClass_thenCorrect() {
        SimpleAbstractClass simpleAbstractClass = new SimpleAbstractClass() {
            void run() {
                // method implementation
            }
        };
        simpleAbstractClass.run();
    }
}
```

有关更多详细信息，我们可能会发现我们关于[Java中的匿名类](https://www.baeldung.com/java-anonymous-classes)的教程很有用。

## 4. 阴影

如果内部类的成员具有相同的名称，则它们的声明会影响封闭类的成员。

在这种情况下，this关键字引用嵌套类的实例，并且可以使用外部类的名称来引用外部类的成员。

让我们看一个简单的例子：

```java
public class NewOuter {

    int a = 1;
    static int b = 2;

    public class InnerClass {
        int a = 3;
        static final int b = 4;

        public void run() {
            System.out.println("a = " + a);
            System.out.println("b = " + b);
            System.out.println("NewOuterTest.this.a = " + NewOuter.this.a);
            System.out.println("NewOuterTest.b = " + NewOuter.b);
            System.out.println("NewOuterTest.this.b = " + NewOuter.this.b);
        }
    }

    @Test
    public void test() {
        NewOuter outer = new NewOuter();
        NewOuter.InnerClass inner = outer.new InnerClass();
        inner.run();

    }
}
```

## 5. 连载

为了避免在尝试序列化嵌套类时出现java.io.NotSerializableException，我们应该：

-   将嵌套类声明为静态
-   使嵌套类和封闭类都实现Serializable

## 6. 总结

在本文中，我们了解了嵌套类是什么以及它们的不同类型。我们还研究了这些不同类型的字段可见性和访问修饰符有何不同。
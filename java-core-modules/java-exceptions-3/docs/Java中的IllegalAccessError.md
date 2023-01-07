## 1. 概述

在本快速教程中，我们将讨论java.lang.IllegalAccessError。

我们将检查一些有关何时抛出它以及如何避免它的示例。

## 2. IllegalAccessError简介

当应用程序试图访问不可访问的字段或调用不可访问的方法时，将抛出IllegalAccessError 。

编译器会捕获此类非法调用，但我们仍可能在运行时遇到IllegalAccessError 。

首先我们观察一下IllegalAccessError的类层次结构：

```plaintext
java.lang.Object
  |_java.lang.Throwable
    |_java.lang.Error
      |_java.lang.LinkageError
        |_java.lang.IncompatibleClassChangeError
          |_java.lang.IllegalAccessError
```

它的父类是IncompatibleClassChangeError。因此，此错误的原因是应用程序中一个或多个类定义的不兼容更改。

简而言之，运行时类的版本与编译时所针对的版本不同。

## 3. 这个错误是怎么产生的？

让我们用一个简单的程序来理解这一点：

```java
public class Class1 {
    public void bar() {
        System.out.println("SUCCESS");
    }
}

public class Class2 {
    public void foo() {
        Class1 c1 = new Class1();
        c1.bar();
    }
}
```

在运行时，上述代码调用 Class1中的方法bar() 。到目前为止，一切都很好。

现在，让我们将bar()的访问修饰符更新为private并独立编译它。

接下来，将Class1(.class文件)的先前定义替换为新编译的版本并重新运行程序：

```java
java.lang.IllegalAccessError: 
  class Class2 tried to access private method Class1.bar()
```

上面的异常是不言自明的。方法bar()现在在Class1 中是私有的。显然，访问是非法的。

## 4. IllegalAccessError实战

### 4.1. 图书馆更新

考虑一个在编译时使用库的应用程序，并且在运行时在类路径中也可以使用该库。

库所有者将公开可用的方法更新为私有方法，重建它，但忘记将此更改更新给其他方。

此外，在执行时，当应用程序调用此方法(假设公共访问)时，它会遇到 IllegalAccessError 。

### 4.2. 接口默认方法

在接口中滥用[默认方法](https://www.baeldung.com/java-static-default-methods)是导致此错误的另一个原因。

考虑以下接口和类定义：

```java
interface Baeldung {
    public default void foobar() {
        System.out.println("This is a default method.");
    }
}

class Super {
    private void foobar() {
        System.out.println("Super class method foobar");
    }
}
```

另外，让我们扩展Super 并实现Baeldung：

```java
class MySubClass extends Super implements Baeldung {}
```

最后，让我们通过实例化MySubClass 来调用foobar() ：

```java
new MySubClass().foobar();
```

方法foobar()在Super中是私有的，在Baeldung 中是默认的。因此，它可以在MySubClass 的层次结构中访问。

因此，编译器不会报错，但在运行时，我们会得到一个错误：

```java
java.lang.IllegalAccessError:
  class IllegalAccessErrorExample tried to access private method 'void Super.foobar()'
```

在执行期间，超类方法声明始终优先于接口默认方法。

从技术上讲，应该调用Super的foobar ，但它是私有的。毫无疑问，会抛出IllegalAccessError 。

## 5. 如何避免？

准确地说，如果我们遇到 IllegalAccessError ，我们应该主要查找类定义中关于访问修饰符的更改。

其次，我们应该验证用私有访问修饰符覆盖的接口默认方法。

公开类级别的方法就可以了。

## 六，总结

总之，编译器将解决大部分非法方法调用。如果我们仍然遇到IllegalAccessError，我们需要查看类定义更改。
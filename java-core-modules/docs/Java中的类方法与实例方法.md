## 一、简介

在本教程中，我们将探讨 Java 中类方法和实例方法之间的区别。

在面向对象编程中，方法等同于函数。这意味着它是对象可以执行的操作。当它们对成员变量进行操作时，我们使用实例方法；当方法执行不需要类的实例时，我们使用静态方法。接下来让我们更详细地了解这一点。

## 2.实例与静态方法

与大多数面向对象的语言一样，我们在 Java 中创建类定义并将它们实例化为对象。

这些对象具有与其关联的属性（成员变量）和通常引用这些成员变量的方法。**当方法引用非静态成员变量时，我们必须将它们定义为实例方法**。

我们有时会定义一个不引用成员变量或只引用*静态*变量的方法。当我们这样做时，我们可以使该方法[成为*静态*方法](https://www.baeldung.com/java-static-methods-use-cases)。**这意味着我们不需要类的实例来调用方法**。

类方法和实例方法在行为上存在差异，所以让我们从一个示例开始。

要将方法定义为[*静态*](https://www.baeldung.com/java-static)方法，我们只需要使用 *static*关键字。*下面是一个包含静态*方法和实例方法的类的示例：

```java
public class MyClass {
  public static boolean isAllLowerCaseStatic(String word) {
    return word.toLowerCase().equals(word);
  }
  public boolean isAllLowerCaseInstance(String word) {
    return word.toLowerCase().equals(word);
  }
}复制
```

当我们调用这些方法时，需要记住一个重要的区别。**要使用实例方法，我们必须首先实例化包含方法定义的类**：

```java
MyClass obj = new MyClass();
obj.isAllLowerCaseInstance("test");复制
```

*在调用静态*方法的情况下，我们可以直接引用类：

```java
MyClass.isAllLowerCaseStatic("test");复制
```

我们的 *isAllLowerCaseStatic()*方法很好地使用了*静态*方法，因为它不引用属于对象实例的任何成员变量。

重要的是要记住，虽然*静态方法似乎是一个不错的选择，但由于没有可*[模拟](https://www.baeldung.com/mockito-mock-static-methods)的对象，因此很难对其进行单元测试。

**如果静态方法对\*静态\*成员变量**进行操作，则静态方法可能会引入并发问题。在这种情况下，我们可以在方法定义中使用[*synchronized关键字。*](https://www.baeldung.com/java-synchronized)

## 3.结论

在本文中，我们了解了 Java 中类或静态方法与实例方法之间的区别。我们讨论了如何定义*静态*方法和实例方法以及如何调用它们中的每一个。

我们应该记住，关键区别在于我们必须通过实例化对象调用实例方法，而我们可以直接通过类访问*静态方法。*
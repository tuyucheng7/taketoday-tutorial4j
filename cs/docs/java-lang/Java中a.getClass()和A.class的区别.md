## 1. 概述

在Java中，类[java.lang.Class](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html)是所有反射操作的入口。一旦我们有了java.lang.Class的对象，我们就可以调用相应的方法来获取反射类的对象。

在本教程中，我们将讨论两种获取java.lang.Class对象的不同方法之间的区别：

-   调用[Object.getClass()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#getClass())方法
-   使用.class语法

## 2. 两种方法的简短介绍

Object.getClass()方法是 Object类的 实例方法。如果我们有一个对象，我们可以调用object.getClass()来获取其类型的Class对象。

同样，我们可以使用ClassName.class语法来获取该类型的Class对象。一个例子可以解释清楚：

```java
@Test
public void givenObjectAndType_whenGettingClassObject_thenTwoMethodsHaveTheSameResult() {
    String str = "I am an object of the String class";

    Class fromStrObject = str.getClass();
    Class clazz = String.class;

    assertSame(fromStrObject, clazz);
}

```

在上面的测试方法中，我们尝试 使用我们提到的两种方式来获取String类的Class 对象 。最后，断言方法告诉我们这两个Class对象是同一个实例。

但是，这两种方法之间存在差异。让我们仔细看看它们。

## 3.运行时类型与静态类型

让我们快速回顾一下前面的例子。当我们调用str.getClass()方法时，我们得到了str对象的运行时类型。另一方面，String.class静态评估 String类。在此示例中， str的运行时类型与 String.class相同。

但是，如果类继承加入派对，它们可能会有所不同。让我们看两个简单的类：

```java
public class Animal {
    protected int numberOfEyes;
}

public class Monkey extends Animal {
    // monkey stuff
}
```

现在让我们实例化一个Animal类的对象并做另一个测试：

```java
@Test
public void givenClassInheritance_whenGettingRuntimeTypeAndStaticType_thenGetDifferentResult() {
    Animal animal = new Monkey();

    Class runtimeType = animal.getClass();
    Class staticType = Animal.class;

    assertSame(staticType, runtimeType);
}

```

如果我们运行上面的测试，我们将得到一个测试失败：

```bash
java.lang.AssertionError: ....
Expected :class getclassobject.cn.tuyucheng.taketoday.Animal
Actual   :class getclassobject.cn.tuyucheng.taketoday.Monkey
```

在测试方法中，即使我们 通过Animal animal = new Monkey();实例化动物对象。而不是Monkey animal = new Monkey(); ， 动物对象的运行时类型仍然是 Monkey。 这是因为 动物对象 在运行时是Monkey的一个实例。

但是，当我们获取 Animal类的静态类型时，类型始终是 Animal。

## 4. 处理原始类型

当我们编写Java代码时，我们经常使用原始类型。让我们尝试使用object.getClass()方法获取原始类型的Class对象 ：

```java
int number = 7;
Class numberClass = number.getClass();
```

如果我们尝试编译上面的代码，我们会得到一个编译错误：

```bash
Error: java: int cannot be dereferenced
```

编译器不能取消引用number变量，因为它是一个原始变量。所以object.getClass()方法并不能帮助我们得到原始类型的Class对象。

让我们看看是否可以使用.class语法获取原始类型：

```java
@Test
public void givenPrimitiveType_whenGettingClassObject_thenOnlyStaticTypeWorks() {
    Class intType = int.class;
    assertNotNull(intType);
    assertEquals("int", intType.getName());
    assertTrue(intType.isPrimitive());
}

```

所以，我们可以通过int.class获取到int原始类型 的Class对象。在Java版本 9 及更高版本中，原始类型的Class对象属于java.base模块。

如测试所示，.class语法是获取原始类型的Class对象的简单方法。

## 5. 获取没有实例的类

我们已经了解到object.getClass()方法可以为我们提供其运行时类型的Class对象。

现在，让我们考虑这样一种情况，我们想要获取某个类型的Class对象，但是我们无法获取目标类型的实例，因为它是抽象类、接口 或某些类不允许实例化：

```java
public abstract class SomeAbstractClass {
    // ...
}

interface SomeInterface {
   // some methods ...
}

public class SomeUtils {
    private SomeUtils() {
        throw new RuntimeException("This Util class is not allowed to be instantiated!");
    }
    // some public static methods...
}

```

在这些情况下，我们无法使用object.getClass()方法获取这些类型 的Class对象，但我们仍然可以使用.class语法获取它们的 Class对象：

```java
@Test
public void givenTypeCannotInstantiate_whenGetTypeStatically_thenGetTypesSuccefully() {
    Class interfaceType = SomeInterface.class;
    Class abstractClassType = SomeAbstractClass.class;
    Class utilClassType = SomeUtils.class;

    assertNotNull(interfaceType);
    assertTrue(interfaceType.isInterface());
    assertEquals("SomeInterface", interfaceType.getSimpleName());

    assertNotNull(abstractClassType);
    assertEquals("SomeAbstractClass", abstractClassType.getSimpleName());

    assertNotNull(utilClassType);
    assertEquals("SomeUtils", utilClassType.getSimpleName());
}

```

如上面的测试所示， .class语法可以获得这些类型的Class对象。

因此，当我们想要拥有 Class对象，但无法获得该类型的实例时，.class语法是可行的方法。

## 六，总结

在本文中，我们学习了两种获取类型的Class对象的不同方法：object.getClass()方法和.class语法。

后来，我们讨论了这两种方法之间的区别。下表可以给我们一个清晰的概览：

|                                  | 对象.getClass()  | SomeClass类         |
| -------------------------------- | ------------------ | --------------------- |
| 类对象                       | 对象的运行时类型 | SomeClass的静态类型 |
| 原始类型                     | —                  | 直接工作              |
| 接口、抽象类或无法实例化的类 | —                  | 直接工作              |


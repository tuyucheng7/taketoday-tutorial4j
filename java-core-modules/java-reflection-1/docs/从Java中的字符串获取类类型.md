## 1. 概述

[Class](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html)类在[Java反射](https://www.baeldung.com/java-reflection)中起着重要作用，因为它是所有反射操作的入口点。

在本快速教程中，我们将探讨如何从字符串中的类名获取Class对象。

## 2. 问题简介

首先，让我们创建一个简单的类作为示例：

```java
package cn.tuyucheng.taketoday.getclassfromstr;

public class MyNiceClass {
    public String greeting() {
        return "Hi there, I wish you all the best!";
    }
}
```

如上面的代码所示，**MyNiceClass类是在包cn.tuyucheng.taketoday.getclassfromstr中创建的**。此外，该类只有一个方法greeting()，它返回一个String。

我们的目标是从其类名称中获取MyNiceClass类的Class对象。此外，我们想从Class对象创建一个MyNiceClass的实例，以验证Class对象是否是我们想要的对象。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

接下来，让我们看看它的实际效果。

## 3. 使用forName()方法获取Class对象

Class类提供了[forName()](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#forName-java.lang.String-)方法，用于从类名中以字符串形式获取Class对象。接下来，我们看看如何调用该方法来获取MyNiceClass的Class对象：

```java
Class cls = Class.forName("cn.tuyucheng.taketoday.getclassfromstr.MyNiceClass");
assertNotNull(cls);
```

接下来，让我们从Class对象cls创建一个MyNiceClass实例。如果我们的Java版本早于9，我们可以使用cls.newInstance()方法获取一个实例。但是，**此方法自**[Java 9起已被弃用](https://docs.oracle.com/javase/9/docs/api/java/lang/Class.html#newInstance--)。对于较新的Java版本，我们可以使用cls.getDeclaredConstructor().newInstance()从Class对象中获取一个新的实例：

```java
MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
assertNotNull(myNiceObject);
assertEquals("Hi there, I wish you all the best!", myNiceObject.greeting());
```

当我们运行它时，测试通过了。因此，我们从类名中得到了所需的Class对象。

值得一提的是，**要获取Class对象，我们必须向forName()方法传递一个全限定类名，即包名+类名的形式，而不是简单的名称**。例如，我们应该将字符串“cn.tuyucheng.taketoday.getclassfromstr.MyNiceClass”传递给forName()方法。否则，forName()方法会抛出ClassNotFoundException：

```java
assertThrows(ClassNotFoundException.class, () -> Class.forName("MyNiceClass"));
```

## 4. 关于异常处理的几句话

我们已经了解了如何从类名中获取MyNiceClass的Class对象。为简单起见，我们在测试中省略了[异常处理](https://www.baeldung.com/java-exceptions)。现在，让我们看看在使用Class.forName()和cls.getDeclaredConstructor().newInstance()方法时应该处理哪些异常。

首先，Class.forName()抛出ClassNotFoundException。上面提到我们在将MyNiceClass的简单名称传递给它时会抛出此异常。**ClassNotFoundException是一个**[受检异常](https://www.baeldung.com/java-checked-unchecked-exceptions#checked)，因此，我们必须在调用Class.forName()方法时进行处理。

接下来，让我们看看cls.getDeclaredConstructor()和newInstance()。**getDeclaredConstructor()方法抛出NoSuchMethodException**。**此外，newInstance()会抛出InstantiationException、IllegalAccessException、IllegalArgumentException和InvocationTargetException**。所有这五个异常都是受检异常。所以，如果我们使用这两种方法，我们需要处理它们。

值得一提的是，我们在本节中讨论的所有异常都是ReflectiveOperationException的子类型。也就是说，**如果我们不想单独处理这些异常，我们可以处理ReflectiveOperationException**，例如：

```java
void someNiceMethod() throws ReflectiveOperationException {
    Class cls = Class.forName("cn.tuyucheng.taketoday.getclassfromstr.MyNiceClass");
    MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
    // ...
}
```

或者，我们可以使用try-catch块：

```java
try {
    Class cls = Class.forName("cn.tuyucheng.taketoday.getclassfromstr.MyNiceClass");
    MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
    // ...
} catch (ReflectiveOperationException exception) {
    // handle the exception
}
```

## 5. 总结

在这篇简短的文章中，我们学习了使用Class.forName()方法从给定的类名字符串中获取Class对象。我们应该注意，我们应该将全限定名称传递给Class.forName()方法。
## 一、概述

[*Class*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html)[类在Java 反射](https://www.baeldung.com/java-reflection)中起着重要作用，因为它是所有反射操作的入口点。

在本快速教程中，我们将探索如何从字符串中的类名获取*Class对象。*

## 二、问题简介

首先，让我们创建一个简单的类作为示例：

```java
package com.baeldung.getclassfromstr;

public class MyNiceClass {
    public String greeting(){
        return "Hi there, I wish you all the best!";
    }
}复制
```

如上面的代码所示，**MyNiceClass\*类\*是在包\*com.baeldung.getclassfromstr\***中创建的。此外，该类只有一个方法*greeting()，*它返回一个*String*。

我们的目标是从其名称中获取*MyNiceClass类的**Class*对象。此外，我们想从*Class对象创建一个**MyNiceClass*的实例，以验证*Class*对象是否是我们想要的对象。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

接下来，让我们看看它的实际效果。

## 3.使用*forName()*方法获取*类*对象

*Class*类提供了[*forName()*](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#forName-java.lang.String-)方法来从类名中获取 *Class*对象作为字符串。接下来，我们看看如何调用方法来获取*MyNiceClass的**Class*对象：

```java
Class cls = Class.forName("com.baeldung.getclassfromstr.MyNiceClass");
assertNotNull(cls);复制
```

接下来，让我们从*Class*对象 *cls创建一个**MyNiceClass*实例。如果我们的 Java 版本早于 9，我们可以使用*cls.newInstance()*方法获取一个实例。但是，**此方法****[自 Java 9 起已被弃用](https://docs.oracle.com/javase/9/docs/api/java/lang/Class.html#newInstance--)**。对于较新的 Java 版本，我们可以使用*cls.getDeclaredConstructor().newInstance()*从*Class*对象中获取一个新的实例：

```java
MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
assertNotNull(myNiceObject);
assertEquals("Hi there, I wish you all the best!", myNiceObject.greeting());复制
```

当我们运行它时，测试通过了。因此，我们从类名中得到了所需的*Class*对象。

值得一提的是，**要获取\*Class对象，我们必须向\**forName()\*方法 传递一个合格的类名，而不是简单的名称。**例如，我们应该将字符串*“com.baeldung.getclassfromstr.MyNiceClass”*传递给*forName()*方法。否则，*forName()*方法会抛出*ClassNotFoundException*：

```java
assertThrows(ClassNotFoundException.class, () -> Class.forName("MyNiceClass"));复制
```

## 4. 关于异常处理的几句话

我们已经了解了如何从类名中获取*MyNiceClass*的*类*对象。为简单起见，我们在测试中省略了[异常处理](https://www.baeldung.com/java-exceptions)。现在，让我们看看在使用*Class.forName()*和*cls.getDeclaredConstructor().newInstance()*方法时应该处理哪些异常。

首先，*Class.forName()*抛出*ClassNotFoundException。*我们在将*MyNiceClass*的简单名称传递给它时提到了它。***ClassNotFoundException\*是一个已[检查异常](https://www.baeldung.com/java-checked-unchecked-exceptions#checked)**。因此，我们必须在调用*Class.forName()*方法时进行处理。

接下来，让我们看看*cls.getDeclaredConstructor()*和*newInstance()。* ***getDeclaredConstructor()\*****方法抛出*****NoSuchMethodException**。*此外，***newInstance()\*会抛出\*InstantiationException、IllegalAccessException\*、\*IllegalArgumentException\*和\*InvocationTargetException\***。这五个异常都是检查异常。所以，如果我们使用这两种方法，我们需要处理它们。

值得一提的是，我们在本节中讨论的所有异常都是*ReflectiveOperationException*的子类型。也就是说，**如果我们不想单独处理那些异常，我们可以处理\*ReflectiveOperationException\***，例如：

```java
void someNiceMethod() throws ReflectiveOperationException {
    Class cls = Class.forName("com.baeldung.getclassfromstr.MyNiceClass");
    MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
    // ...
}复制
```

或者，我们可以使用*try-catch*块：

```java
try {
    Class cls = Class.forName("com.baeldung.getclassfromstr.MyNiceClass");
    MyNiceClass myNiceObject = (MyNiceClass) cls.getDeclaredConstructor().newInstance();
    // ...
} catch (ReflectiveOperationException exception) {
    // handle the exception
}复制
```

## 5.结论

在这篇简短的文章中，我们学习了使用*Class.forName()*方法从给定的类名字符串中获取*Class对象。*我们应该注意，我们应该将限定名称传递给*Class.forName()*方法。
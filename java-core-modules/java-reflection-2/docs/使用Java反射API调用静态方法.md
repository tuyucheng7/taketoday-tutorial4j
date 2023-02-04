## 1. 概述

在本快速教程中，我们将讨论如何使用[反射API](https://www.baeldung.com/java-reflection)在Java中调用静态方法。

我们将涵盖两种不同的场景：

-   静态方法是公共的。
-   静态方法是私有的。

## 2. 示例类

为了方便演示和解释，我们先创建一个GreetingAndBye类作为示例：

```java
public class GreetingAndBye {

    public static String greeting(String name) {
        return String.format("Hey %s, nice to meet you!", name);
    }

    private static String goodBye(String name) {
        return String.format("Bye %s, see you next time.", name);
    }
}
```

GreetingAndBye类看起来非常简单。它有两种静态方法，一种是公共的，一种是私有的。

这两种方法都接收一个字符串参数并返回一个字符串作为结果。

现在，让我们使用Java反射API调用这两个静态方法。在本教程中，我们将把代码作为单元测试方法来处理。

## 3. 调用公共静态方法

首先，让我们看看如何调用公共静态方法：

```java
@Test
void invokePublicMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<GreetingAndBye> clazz = GreetingAndBye.class;
    Method method = clazz.getMethod("greeting", String.class);

    Object result = method.invoke(null, "Eric");

    Assertions.assertEquals("Hey Eric, nice to meet you!", result);
}
```

我们应该注意，当我们使用反射API时，我们需要处理所需的[受检异常](https://www.baeldung.com/java-checked-unchecked-exceptions#checked)。

在上面的例子中，我们首先获取了我们要测试的类的实例，即GreetingAndBye。

在我们有了类实例之后，我们可以通过调用[getMethod](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html#getMethod(java.lang.String,java.lang.Class...))方法来获取公共静态方法对象。

一旦我们持有方法对象，我们就可以通过调用[invoke](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Method.html#invoke(java.lang.Object,java.lang.Object...))方法来简单地调用它。

值得解释一下invoke方法的第一个参数。如果该方法是实例方法，则第一个参数是调用基础方法的对象。

但是，**当我们调用静态方法时，我们将null作为第一个参数传递**，因为静态方法不需要实例即可被调用。

最后，如果我们运行测试，它就会通过。

## 3. 调用私有静态方法

调用私有静态方法与调用公共静态方法非常相似。我们先看一下代码：

```java
@Test
void invokePrivateMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<GreetingAndBye> clazz = GreetingAndBye.class;
    Method method = clazz.getDeclaredMethod("goodBye", String.class);
    method.setAccessible(true);

    Object result = method.invoke(null, "Eric");

    Assertions.assertEquals("Bye Eric, see you next time.", result);
}
```

正如我们在上面的代码中看到的，**当我们尝试获取私有方法的Method对象时，我们应该使用**[getDeclaredMethod](https://www.baeldung.com/java-method-reflection#2-getdeclaredmethod)**而不是**[getMethod](https://www.baeldung.com/java-method-reflection#1-getmethod)。

此外，**我们需要调用**[method.setAccessible(true)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Method.html#setAccessible(boolean))**来调用私有方法**。这将要求JVM抑制对该方法的访问控制检查。

因此，它允许我们调用私有方法。否则，将引发IllegalAccessException异常。

如果我们执行它，测试将通过。

## 4. 总结

在这篇简短的文章中，我们讨论了如何使用Java反射API调用静态方法。
## 1. 概述

使用[Java Reflection API](https://www.baeldung.com/java-reflection)时，遇到java.lang.reflect.InvocationTargetException是很常见的。

在本教程中，我们将通过一个简单的示例来了解它以及如何处理它。 

## 2. InvocationTargetException的原因

它主要发生在我们使用反射层并尝试调用自身抛出底层异常的方法或构造函数时。

**反射层用InvocationTargetException包装了方法抛出的实际异常**。

让我们尝试通过一个例子来理解它。

我们将编写一个带有故意抛出异常的方法的类：

```java
public class InvocationTargetExample {
    public int divideByZeroExample() {
        return 1 / 0;
    }
}
```

让我们在简单的JUnit 5测试中使用反射来调用上述方法：

```java
InvocationTargetExample targetExample = new InvocationTargetExample(); 
Method method = InvocationTargetExample.class.getMethod("divideByZeroExample");
 
Exception exception = assertThrows(InvocationTargetException.class, () -> method.invoke(targetExample));
```

在上面的代码中，我们断言了在调用该方法时抛出的InvocationTargetException。这里需要注意的一件重要事情是实际的异常(在这种情况下是ArithmeticException)被包装到InvocationTargetException中。

现在，为什么反射不首先抛出实际的异常呢？

原因是可以让我们了解Exception是由于通过反射层调用方法失败导致的，还是方法本身发生的。

## 3. 如何处理InvocationTargetException？

这里实际的底层异常是InvocationTargetException的原因，因此我们可以**使用Throwable.getCause()**来获取有关它的更多信息。

让我们看看如何使用getCause()来获取上面使用的相同示例中的实际异常：

```java
assertEquals(ArithmeticException.class, exception.getCause().getClass());
```

我们对抛出的同一个异常对象使用了getCause()方法，并且我们断言ArithmeticException.class是异常的原因。

因此，一旦我们获得底层异常，我们就可以重新抛出相同的异常，将其包装在某个自定义异常中，或者根据我们的要求简单地记录异常。

## 4. 总结

在这篇简短的文章中，我们了解了反射层如何包装任何底层异常。

我们还看到了如何确定InvocationTargetException的根本原因以及如何通过一个简单的示例来处理这种情况。
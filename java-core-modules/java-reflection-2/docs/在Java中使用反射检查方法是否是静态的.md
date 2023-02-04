## 1. 概述

在本快速教程中，我们将讨论如何使用[反射API](https://www.baeldung.com/java-reflection)检查Java中的方法是否为[静态](https://www.baeldung.com/java-static#the-static-methods-or-class-methods)方法。

## 2. 例子

为了演示这一点，我们将使用一些静态方法创建StaticUtility类：

```java
public class StaticUtility {

    public static String getAuthorName() {
        return "Umang Budhwar";
    }

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public static LocalTime getLocalTime() {
        return LocalTime.now();
    }
}
```

## 3. 检查一个方法是否是静态的

**我们可以使用**[Modifier](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Modifier.html)**.isStatic方法检查一个方法是否是静态的**：

```java
@Test
void whenCheckStaticMethod_ThenSuccess() throws Exception {
    Method method = StaticUtility.class.getMethod("getAuthorName", null);
    Assertions.assertTrue(Modifier.isStatic(method.getModifiers()));
}
```

在上面的示例中，我们首先使用Class.getMethod方法获得了要测试的方法的实例。一旦我们有了方法引用，我们需要做的就是调用Modifier.isStatic方法。

## 4. 获取类的所有静态方法

现在我们已经知道如何检查一个方法是否是静态的，我们可以很容易地列出一个类的所有静态方法：

```java
@Test
void whenCheckAllStaticMethods_thenSuccess() {
    List<Method> methodList = Arrays.asList(StaticUtility.class.getMethods())
        .stream()
        .filter(method -> Modifier.isStatic(method.getModifiers()))
        .collect(Collectors.toList());
    Assertions.assertEquals(3, methodList.size());
}

```

在上面的代码中，我们刚刚验证了类StaticUtility中静态方法的总数。

## 5. 总结

在本教程中，我们了解了如何检查方法是否为静态方法。我们还看到了如何获取一个类的所有静态方法。
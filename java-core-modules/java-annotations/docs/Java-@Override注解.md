## 1. 概述

在本快速教程中，我们将了解如何使用@Override注解。

## 2. @Override注解

在子类中，我们可以[覆盖或重载](https://www.baeldung.com/java-classes-initialization-questions)实例方法。覆盖表示子类正在替换继承的行为。重载是在子类添加新行为时。

有时，当我们真正打算覆盖时，我们会意外地超载。在Java中很容易犯这个错误：

```java
public class Machine {
    public boolean equals(Machine obj) {
        return true;
    }

    @Test
    public void whenTwoDifferentMachines_thenReturnTrue() {
        Object first = new Machine();
        Object second = new Machine();
        assertTrue(first.equals(second));
    }
}
```

令人惊讶的是，上面的测试失败了。这是因为这个equals方法重载了Object#equals，而不是覆盖它。

我们可以在继承的方法上使用@Override注解来保护我们免受这种错误。

在此示例中，我们可以在equals方法上方添加@Override注解：

```java
@Override
public boolean equals(Machine obj) {
    return true;
}
```

此时，编译器会报错，通知我们并没有像我们想的那样重写equals。

然后，我们可以纠正我们的错误：

```java
@Override
public boolean equals(Object obj) {
    return true;
}
```

由于很容易意外重载，因此通常建议在所有继承的方法上使用@Override注解。

## 3. 总结

在本指南中，我们了解了@Override注解在Java中的工作方式。
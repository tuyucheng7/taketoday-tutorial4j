## 1. 概述

在本快速教程中，我们将了解Java中已弃用的API以及如何使用@Deprecated注解。

## 2. @Deprecated注解

随着项目的发展，其API也会发生变化。随着时间的推移，我们不希望人们再使用某些构造函数、字段、类型或方法。

我们可以使用@Deprecated注解标记这些元素，而不是破坏项目API的向后兼容性。

@Deprecated告诉其他开发人员不应再使用标记的元素。通常还会在@Deprecated注解旁边添加一些Javadoc来解释什么是提供正确行为的更好替代方案：

```java
public class Worker {
    /**
     * Calculate period between versions
     * @deprecated
     * This method is no longer acceptable to compute time between versions.
     * <p> Use {@link Utils#calculatePeriod(Machine)} instead.
     *
     * @param machine instance
     * @return computed time
     */
    @Deprecated
    public int calculate(Machine machine) {
        return machine.exportVersions().size() * 10;
    }
}
```

请记住，如果在代码中某处使用了带注解的Java元素，编译器只会显示已弃用的API警告。因此，在这种情况下，它只会显示是否有调用计算方法的代码。

此外，我们还可以使用Javadoc @deprecated标记在文档中传达弃用状态。

## 3. Java 9新增的可选属性

Java 9为@Deprecated注解添加了一些可选属性：since和forRemoval。

since属性需要一个字符串，让我们定义该元素在哪个版本中被弃用。默认值为空字符串。

而forRemoval是一个布尔值，它允许我们指定元素是否会在下一个版本中被删除。它的默认值为false：

```java
public class Worker {
    /**
     * Calculate period between versions
     * @deprecated
     * This method is no longer acceptable to compute time between versions.
     * <p> Use {@link Utils#calculatePeriod(Machine)} instead.
     *
     * @param machine instance
     * @return computed time
     */
    @Deprecated(since = "4.5", forRemoval = true)
    public int calculate(Machine machine) {
        return machine.exportVersions().size() * 10;
    }
}
```

简而言之，上述用法意味着calculate自我们库的4.5版本以来已被弃用，并计划在下一个主要版本中删除。

添加它对我们很有帮助，因为如果编译器发现我们正在使用具有该值的方法，它会给我们一个更强烈的警告。

IDE已经支持检测标记为forRemoval=true的方法的使用。 例如，IntelliJ[用红线](https://www.vojtechruzicka.com/java-9-enhanced-deprecation/)而不是黑线划过代码。

## 4. 总结

在这篇快速文章中，我们了解了如何使用@Deprecated注解及其[可选属性](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Deprecated.html)来标记不应再使用的代码。
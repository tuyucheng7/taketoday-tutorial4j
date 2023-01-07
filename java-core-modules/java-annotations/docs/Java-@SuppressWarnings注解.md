## 1. 概述

在本快速教程中，我们将了解如何使用@SuppressWarnings注解。

## 2. @SuppressWarnings注解

编译器警告消息通常很有帮助。不过，有时警告会变得嘈杂。

特别是当我们不能或不想解决它们时：

```java
public class Machine {
    private List versions;

    public void addVersion(String version) {
        versions.add(version);
    }
}
```

编译器将发出有关此方法的警告。它会警告我们正在使用原始类型的集合。如果我们不想修复警告，那么我们可以使用@SuppressWarnings注解来抑制它。

这个注解允许我们说出要忽略哪种警告。虽然警告类型可能因[编译器供应商](https://stackoverflow.com/questions/1205995/what-is-the-list-of-valid-suppresswarnings-warning-names-in-java)而异，但最常见的两种是弃用和未检查。

deprecation告诉编译器在我们使用已弃用的方法或类型时忽略。

unchecked告诉编译器在我们使用原始类型时忽略。

因此，在我们上面的示例中，我们可以抑制与原始类型使用相关的警告：

```java
public class Machine {
    private List versions;

    @SuppressWarnings("unchecked")
    // or
    @SuppressWarnings({"unchecked"})
    public void addVersion(String version) {
        versions.add(version);
    }
}
```

为了抑制多个警告列表，我们设置了一个包含相应警告列表的字符串数组：

```java
@SuppressWarnings({"unchecked", "deprecated"})
```

## 3. 总结

在本指南中，我们了解了如何在Java中使用@SuppressWarnings注解。
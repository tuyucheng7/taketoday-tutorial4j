## 1. 概述

在本快速教程中，我们将了解@SafeVarargs注解。

## 2. @SafeVarargs注解

Java 5引入了可变参数(可变长度的方法参数)以及参数化类型的概念。

结合这些可能会给我们带来问题：

```java
public static <T> T[] unsafe(T... elements) {
    return elements; // unsafe! don't ever return a parameterized varargs array
}

public static <T> T[] broken(T seed) {
    T[] plant = unsafe(seed, seed, seed); // broken! This will be an Object[] no matter what T is
    return plant;
}

public static void plant() {
   String[] plants = broken("seed"); // ClassCastException
}
```

这些问题对于编译器来说很难确认，因此只要将两者结合在一起，它就会发出警告，例如在不安全的情况下：

```bash
warning: [unchecked] Possible heap pollution from parameterized vararg type T
  public static <T> T[] unsafe(T... elements) {
```

这个方法，如果使用不当，就像在broken的情况下，会将Object[]数组污染到堆中，而不是预期的类型b中。

要消除此警告，我们可以在最终或静态方法和构造函数上添加@SafeVarargs注解。

@SafeVarargs就像@SupressWarnings一样，它允许我们声明特定的编译器警告是误报。一旦我们确保我们的行为是安全的，我们就可以添加这个注解：

```java
public class Machine<T> {
    private List<T> versions = new ArrayList<>();

    @SafeVarargs
    public final void safe(T... toAdd) {
        for (T version : toAdd) {
            versions.add(version);
        }
    }
}
```

安全使用可变参数本身就是一个棘手的概念。有关更多信息，Josh Bloch在他的书[Effective Java]()中有很好的解释。

## 3. 总结

在这篇快速文章中，我们了解了如何在Java中使用@SafeVarargs注解。
## 1. 概述

作为Java开发人员，我们可能在某些场合遇到过Void类型，并且想知道它的用途是什么。

在这个快速教程中，我们将了解这个特殊的类，了解何时以及如何使用它，以及如何尽可能避免使用它。

## 2. 什么是Void类型

从JDK 1.1开始，Java为我们提供了Void类型。**它的目的只是将void返回类型表示为一个类并包含一个Class<Void\>公共值**。它不可实例化，因为它唯一的构造函数是私有的。

因此，我们可以分配给Void变量的唯一值是null。它可能看起来有点无用，但我们现在将看看何时以及如何使用这种类型。

## 3. 用法

在某些情况下，使用Void类型会很有趣。

### 3.1 反射

首先，我们可以在进行反射时使用它。**实际上，任何void方法的返回类型都将匹配保存前面提到的Class<Void\>值的Void.TYPE变量**。

让我们想象一个简单的Calculator类：

```java
public class Calculator {
    private int result = 0;

    public int add(int number) {
        return result += number;
    }

    public int sub(int number) {
        return result -= number;
    }

    public void clear() {
        result = 0;
    }

    public void print() {
        System.out.println(result);
    }
}
```

有些方法返回一个整数，有些方法不返回任何东西。现在，假设我们必须通过反射检索所有不返回任何结果的方法。我们将通过使用Void.TYPE变量来实现这一点：

```java
@Test
void givenCalculator_whenGettingVoidMethodsByReflection_thenOnlyClearAndPrint() {
    Method[] calculatorMethods = Calculator.class.getDeclaredMethods();
    List<Method> calculatorVoidMethods = Arrays.stream(calculatorMethods)
        .filter(method -> method.getReturnType().equals(Void.TYPE))
        .collect(Collectors.toList());

    assertThat(calculatorVoidMethods)
        .allMatch(method -> Arrays.asList("clear", "print").contains(method.getName()));
}
```

正如我们所看到的，只检索了clear()和print()方法。

### 3.2 泛型

Void类型的另一种用法是与泛型类一起使用。假设我们正在调用一个需要Callable参数的方法：

```java
public class Defer {
    public static <V> V defer(Callable<V> callable) throws Exception {
        return callable.call();
    }
}
```

**但是，我们要传递的Callable不必返回任何东西。因此，我们可以传递一个Callable<Void\>**：

```java
@Test
void givenVoidCallable_whenDiffer_thenReturnNull() throws Exception {
    Callable<Void> callable = new Callable<Void>() {
        @Override
        public Void call() {
            System.out.println("Hello!");
            return null;
        }
    };

    assertThat(Defer.defer(callable)).isNull();
}
```

如上所示，**为了从具有Void返回类型的方法返回，我们只需要返回null**。此外，我们可以使用随机类型(例如Callable<Integer\>)并返回null或根本不返回任何类型(Callable)，**但使用Void清楚地表明了我们的意图**。

我们也可以将此方法应用于lambda。事实上，我们的Callable可以写成lambda。让我们想象一个需要Function的方法，但我们想使用一个不返回任何内容的Function。然后我们只需要让它返回Void：

```java
public static <T, R> R defer(Function<T, R> function, T arg) {
    return function.apply(arg);
}
@Test
void givenVoidFunction_whenDiffer_thenReturnNull() {
    Function<String, Void> function = s -> {
        System.out.println("Hello " + s + "!");
        return null;
    };

    assertThat(Defer.defer(function, "World")).isNull();
}
```

## 4. 如何避免使用它？

现在，我们已经看到了Void类型的一些用法。然而，即使第一次使用完全没问题，**我们也可能希望尽可能避免在泛型中使用Void**。确实，遇到表示没有结果且只能包含null的返回类型可能会很麻烦。

我们现在将了解如何避免这些情况。首先，让我们考虑带有Callable参数的方法。**为了避免使用Callable<Void\>，我们可能会提供另一种接收Runnable参数的方法**：

```java
public static void defer(Runnable runnable) {
    runnable.run();
}
```

因此，我们可以向它传递一个不返回任何值的Runnable，从而摆脱无用的return null：

```java
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello!");
    }
};

Defer.defer(runnable);
```

但是，如果Defer类不是我们要修改的怎么办？然后我们可以坚持使用Callable<Void\>选项或**创建另一个接收Runnable并将调用推迟到Defer类的类**：

```java
public class MyOwnDefer {
    public static void defer(Runnable runnable) throws Exception {
        Defer.defer(new Callable<Void>() {
            @Override
            public Void call() {
                runnable.run();
                return null;
            }
        });
    }
}
```

通过这样做，我们将繁琐的部分一劳永逸地封装在我们自己的方法中，允许未来的开发人员使用更简单的API。

当然，对于Function也可以实现同样的效果。在我们的示例中，Function不返回任何内容，因此我们可以提供另一种接收Consumer的方法：

```java
public static <T> void defer(Consumer<T> consumer, T arg) {
    consumer.accept(arg);
}
```

那么，如果我们的函数不接收任何参数呢？我们可以使用Runnable或创建我们自己的函数式接口(如果这样看起来更清楚的话)：

```java
public interface Action {
    void execute();
}
```

然后，我们再次重载defer()方法：

```java
public static void defer(Action action) {
    action.execute();
}
Action action = () -> System.out.println("Hello!");

Defer.defer(action);
```

## 5. 总结

在这篇简短的文章中，我们介绍了Java Void类。我们看到了它的目的是什么以及如何使用它。我们还学习了它的一些替代用法。
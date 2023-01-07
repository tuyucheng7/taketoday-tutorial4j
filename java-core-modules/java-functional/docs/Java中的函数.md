## 一、概述

在本教程中，我们将演示如何在 Java 中创建仿函数。首先，让我们通过一些关于术语“函子”的性质的细节。然后我们将查看一些代码示例，说明如何在 Java 中使用它。

## 2. 什么是函子？

“函子”一词来自数学领域，特别是来自一个称为“范畴论”的子领域。在计算机编程中，仿函数可以被认为是一个实用程序类，它允许我们映射包含在特定上下文中的值。此外，它表示两个类别之间的结构保留映射。

两条定律支配函子：

-   Identity：当一个 Functor 被一个恒等函数映射时，这个函数返回与它传递的参数相同的值，我们需要得到初始的 Functor（容器及其内容保持不变）。
-   Composition/Associativity：当Functor用于映射两个部分的组合时，它应该与一个接一个地映射一个函数相同。

## 3. 函数式编程中的函子

仿函数是一种用于函数式编程的设计模式，其灵感来自范畴论中使用的定义。它使泛型能够在其内部应用函数而不影响泛型的结构。[在像Scala](https://www.baeldung.com/scala/functors-functional-programming)这样的编程语言中，我们可以找到 Functors 的很多用途。

## 4. Java 中的函数

Java 和大多数其他当代编程语言不包含任何被认为是合适的内置函子等价物。但是，从 Java 8 开始，函数式编程元素被引入到该语言中。[函数式编程](https://www.baeldung.com/java-functional-programming)的思想在 Java 编程语言中还是比较新颖的。

Functor可以使用java.util.function包中的Function接口在 Java 中实现。下面是 Java 中Functor类的示例，它采用Function对象并将其应用于值：

```java
public class Functor<T> {
    private final T value;
    public Functor(T value) {
        this.value = value;
    }
    public <R> Functor<R> map(Function<T, R> mapper) {
        return new Functor<>(mapper.apply(value));
    }
    // getter
}
```

正如我们所注意到的，map()方法负责执行操作。对于新类，我们定义了一个最终值属性。该属性是函数将被应用的地方。此外，我们需要一种方法来比较值。让我们把这个函数添加到Functor类中：

```java
public class Functor<T> {
    // Definitions
    boolean eq(T other) {
        return value.equals(other);
    }
    // Getter
}
```

在此示例中，Functor类是通用的，因为它接受一个类型参数T ，该参数指定存储在类中的值的类型。map方法接受一个Function对象，该对象接受类型T的值并返回类型R的值。然后map方法通过将函数应用于原始值并返回它来创建一个新的Functor对象。

下面是一个如何使用这个仿函数类的例子：

```java
@Test
public void whenProvideAValue_ShouldMapTheValue() {
    Functor<Integer> functor = new Functor<>(5);
    Function<Integer, Integer> addThree = (num) -> num + 3;
    Functor<Integer> mappedFunctor = functor.map(addThree);
    assertEquals(8, mappedFunctor.getValue());
}
```

## 5. 法律验证器

因此，我们需要进行测试。在我们的第一种方法之后，让我们使用我们的Functor类来演示 Functor Laws。首先是身份法。在这种情况下，我们的代码片段是：

```java
@Test
public void whenApplyAnIdentityToAFunctor_thenResultIsEqualsToInitialValue() {
    String value = "baeldung";
    //Identity
    Functor<String> identity = new Functor<>(value).map(Function.identity());
    assertTrue(identity.eq(value));
}
```

在刚刚给出的示例中，我们使用了Function类中可用的标识方法。结果Functor返回的值不受影响，并且与作为参数传入的值保持相同。此行为证明身份法得到遵守。

下一步是应用第二定律。在开始实施之前，我们需要定义一些假设。

-   f是将类型T和R相互映射的函数。
-   g是将类型R和U相互映射的函数。

之后，我们准备好实施我们的测试来证明组合/结合律。这是我们实现的一些代码：

```java
@Test
public void whenApplyAFunctionToOtherFunction_thenResultIsEqualsBetweenBoth() {
    int value = 100;
    Function<Integer, String> f = Object::toString;
    Function<String, Long> g = Long::valueOf;
    Functor<Long> left = new Functor<>(value).map(f).map(g);
    Functor<Long> right = new Functor<>(value).map(f.andThen(g));
    assertTrue(left.eq(100L));
    assertTrue(right.eq(100L));
}
```

从我们的代码片段中，我们定义了两个标记为f和g的函数。之后，我们使用两种不同的映射策略构建两个Functors，一个名为left，另一个名为right 。两个Functor最终产生相同的输出。结果，我们对第二定律的实施得到了成功的应用。

## 6. Java 8 之前的仿函数

到目前为止，我们已经看到了使用 Java 8 中引入的java.util.function.Function接口的代码示例。假设我们使用的是早期版本的 Java。在那种情况下，我们可以使用类似的接口或创建我们自己的功能接口来表示一个接受单个参数并返回结果的函数。

另一方面，我们可以利用 Enum 的功能设计一个Functor 。虽然这不是最佳答案，但它确实符合 Functor 法则，也许最重要的是，它完成了工作。让我们定义我们的EnumFunctor类：

```java
public enum EnumFunctor {
    PLUS {
        public int apply(int a, int b) {
            return a + b;
        }
    }, MINUS {
        public int apply(int a, int b) {
            return a - b;
        }
    }, MULTIPLY {
        public int apply(int a, int b) {
            return a  b;
        }
    }, DIVIDE {
        public int apply(int a, int b) {
            return a / b;
        }
    };
    public abstract int apply(int a, int b);
}
```

在此示例中，对每个常量值调用apply方法，使用两个整数作为参数。该方法执行必要的数学运算并返回结果。另外，本例中使用了abstract关键字，表明apply过程不是在Enum本身实现的，而是必须由各个常量值来实现。现在，让我们测试我们的实现：

```java
@Test
public void whenApplyOperationsToEnumFunctors_thenGetTheProperResult() {
    assertEquals(15, EnumFunctor.PLUS.apply(10, 5));
    assertEquals(5, EnumFunctor.MINUS.apply(10, 5));
    assertEquals(50, EnumFunctor.MULTIPLY.apply(10, 5));
    assertEquals(2, EnumFunctor.DIVIDE.apply(10, 5));
}
```

## 七、结论

在本文中，我们首先描述了Functor是什么。然后，我们进入它的法律定义。之后，我们在 Java 8 中实现了一些代码示例来演示Functor的使用。此外，我们通过示例演示了两个 Functor 定律。最后，我们简要说明了如何在 Java 8 之前的 Java 版本中使用 Functors，并提供了一个带有Enum的示例。
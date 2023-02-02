## 1. 概述

[Java 16](https://jdk.java.net/16/release-notes)于2021年3月16日发布，是基于[Java 15](https://www.baeldung.com/java-15-new)构建的最新短期增量版本。此版本带有一些有趣的功能，例如记录和密封类。

在本文中，我们将探索其中的一些新功能。

## 2. 从代理实例调用默认方法(JDK-8159746)

作为对接口中默认方法的增强，随着Java 16的发布，java.lang.reflect.InvocationHandler支持使用反射通过[动态代理](https://www.baeldung.com/java-dynamic-proxies)调用[接口的默认方法](https://bugs.openjdk.java.net/browse/JDK-8159746)。

为了说明这一点，让我们看一个简单的默认方法示例：

```java
interface HelloWorld {
    default String hello() {
        return "world";
    }
}
```

通过此增强功能，我们可以使用反射在该接口的代理上调用默认方法：

```java
@Test
void givenAnInterfaceWithDefaulMethod_whenCreatingProxyInstance_thenCanInvokeDefaultMethod() throws Exception {
	Object proxy = Proxy.newProxyInstance(getSystemClassLoader(), new Class<?>[]{HelloWorld.class},
		(prox, method, args) -> {
			if (method.isDefault()) {
				return InvocationHandler.invokeDefault(prox, method, args);
			}
			return method.invoke(prox, args);
		}
	);
	Method method = proxy.getClass().getMethod("hello");
	assertThat(method.invoke(proxy)).isEqualTo("world");
}
```

## 3. 日期段支持(JDK-8247781)

[DateTimeFormatter新增的元素](https://bugs.openjdk.java.net/browse/JDK-8247781)是一天中的时段符号“B”，它提供了am/pm格式的替代方法：

```java
LocalTime date = LocalTime.parse("15:25:08.690791");
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h B");
assertThat(date.format(formatter)).isEqualTo("3 in the afternoon");
```

我们得到的输出不是“3pm”，而是“3 in the afternoon”。我们还可以使用“B”、“ BBBB ”或“BBBBB” [DateTimeFormatter](https://www.baeldung.com/java-datetimeformatter)模式分别用于短、全和窄风格。

## 4. 添加Stream.toList方法(JDK-8180352)

目的是减少一些常用流收集器的样板代码，例如Collectors.toList和Collectors.toSet：

```java
List<String> integersAsString = Arrays.asList("1", "2", "3");
List<Integer> ints = integersAsString.stream().map(Integer::parseInt).collect(Collectors.toList());
List<Integer> intsEquivalent = integersAsString.stream().map(Integer::parseInt).toList();
```

在上面的代码中，我们通过新旧两种方法将String集合收集成另一个集合元素。

## 5. Vector API孵化器(JEP-338)

[Vector API](https://openjdk.java.net/jeps/338)正处于Java 16的初始孵化阶段。此API的想法是提供一种矢量计算方法，最终能够比传统的标量计算方法更优化地执行(在支持的CPU架构上)。

让我们看看传统上如何将两个数组相乘：

```java
int[] a = {1, 2, 3, 4};
int[] b = {5, 6, 7, 8};

var c = new int[a.length];

for (int i = 0; i < a.length; i++) {
    c[i] = a[i] * b[i];
}
```

对于长度为4的数组，此标量计算示例将在循环内执行4次。现在，让我们来看看等效的基于向量的计算：

```java
int[] a = {1, 2, 3, 4};
int[] b = {5, 6, 7, 8};

var vectorA = IntVector.fromArray(IntVector.SPECIES_128, a, 0);
var vectorB = IntVector.fromArray(IntVector.SPECIES_128, b, 0);
var vectorC = vectorA.mul(vectorB);
vectorC.intoArray(c, 0);
```

在基于向量的代码中，我们做的第一件事是使用IntVector类的静态工厂方法fromArray从我们的输入数组创建两个IntVector，第一个参数是向量的大小，后面是数组和偏移量(这里设置为0)。这里最重要的是我们达到128位的向量大小，在Java中，每个int占用4个字节。

由于我们有一个包含4个整数的输入数组，因此需要128位来存储。我们的单个Vector可以存储整个数组。

在某些架构上，编译器将能够优化字节码，将计算从4个周期减少到只有1个周期。这些优化有利于机器学习和密码学等领域。

我们应该注意到，处于孵化阶段意味着此Vector API可能会随着新版本的发布而发生变化。

## 6. Record(JEP-395)

[Records]()是在Java 14中引入的，Java 16带来了一些[增量变化](https://bugs.openjdk.org/browse/JDK-8262133)。

记录与枚举的相似之处在于它们是一种受限制的类形式，定义记录是定义不可变数据保存对象的一种简洁方式。

### 6.1 不使用记录的例子

首先，让我们定义一个Book类：

```java
public final class Book {
    private final String title;
    private final String author;
    private final String isbn;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public boolean equals(Object o) {
        // ...
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, isbn);
    }
}
```

在Java中创建简单的数据保存类需要大量样板代码，这可能很麻烦，并且可能会导致在开发人员没有提供所有必要方法(例如equals和hashCode)时出现错误。

同样，有时开发人员会跳过创建适当的[不可变类](https://www.baeldung.com/java-immutable-object)的必要步骤。有时我们最终会重用一个通用类，而不是为每个不同的用例定义一个专门的类。

大多数现代IDE都提供了自动生成代码(例如setter、getter、构造函数等)的功能，这有助于缓解这些问题并减少开发人员编写代码的开销。但是，Records提供了一种内置机制来减少样板代码并可以达到相同的效果。

### 6.2 使用记录的例子

下面是使用记录重写的Book对象

```java
public record Book(String title, String author, String isbn) {
}
```

通过使用record关键字，我们可以将Book类的代码减少到只有两行。这使得它更容易，更不容易出错。

### 6.3 Java 16中对记录的新增功能

随着Java 16的发布，我们现在可以将记录定义为内部类的类成员。这是由于放宽了[JEP-384](https://openjdk.java.net/jeps/384)下Java 15增量版本中缺少的限制：

```java
class OuterClass {
    class InnerClass {
        Book book = new Book("Title", "author", "isbn");
    }
}
```

## 7. instanceof的模式匹配(JEP-394)

从Java 16开始，已经添加了[instanceof关键字的模式匹配](https://bugs.openjdk.org/browse/JDK-8250623)。

以前我们可能都写过这样的代码：

```java
Object obj = "TEST";

if (obj instanceof String) {
    String t = (String) obj;
    // do some logic...
}
```

这段代码必须首先检查obj的实例，然后将对象强制转换为String并将其分配给新变量t，而不是纯粹关注应用程序所需的逻辑。

随着模式匹配的引入，我们可以像下面这样重写这段代码：

```java
Object obj = "TEST";

if (obj instanceof String t) {
    // do some logic
}
```

现在，我们可以声明一个变量(在本例中为t)作为instanceof检查的一部分。

## 8. 密封类(JEP-397)

密封类首先在[Java 15](https://openjdk.java.net/jeps/360)中引入，它提供了一种机制来确定允许哪些子类扩展或实现父类或接口。

### 8.1 例子

让我们通过定义一个接口和两个实现类来说明这一点：

```java
public sealed interface JungleAnimal permits Monkey, Snake {
}

public final class Monkey implements JungleAnimal {
}

public non-sealed class Snake implements JungleAnimal {
}
```

sealed关键字与permits关键字结合使用，来准确确定允许哪些类实现JungleAnimal接口。在我们的例子中为Monkey和Snake。 

密封类的所有继承类都必须标记为以下之一：

-   sealed：意味着他们必须使用permits关键字定义允许从它继承的类
-   final：阻止任何进一步的继承子类
-   non-sealed：允许任何类能够从它继承

密封类的一个显著好处是，它们允许进行详尽的模式匹配检查，而无需捕获所有未涵盖的情况。例如，使用我们定义的类，我们可以有逻辑来覆盖JungleAnimal的所有可能子类：

```java
JungleAnimal j = // some JungleAnimal instance

if (j instanceof Monkey m) {
    // do logic
} else if (j instanceof Snake s) {
    // do logic
}
```

我们不需要else块，因为密封类只允许Monkey和Snake这两种可能的子类型。

### 8.2 Java 16中密封类的新增功能

Java 16中对密封类进行了[一些补充](https://bugs.openjdk.java.net/browse/JDK-8246775)，以下是Java 16对密封类引入的更改：

-   Java语言将sealed、non-sealed和permits识别为上下文关键字(类似于abstract和extends)
-   限制创建作为密封类子类的局部类的能力(类似于不能创建密封类的匿名类)。
-   转换密封类和从密封类派生的类时进行更严格的检查

## 9. 其他变化

延续Java 15版本中的JEP-383，[外部链接器API](https://bugs.openjdk.java.net/browse/JDK-8249755)提供了一种灵活的方式来访问主机上的本机代码。最初是为了实现C语言的互操作性，未来可能会适配C++或Fortran等其他语言。此功能的目标是最终取代[Java Native Interface](https://docs.oracle.com/en/java/javase/11/docs/specs/jni/index.html)。

另一个重要的变化是JDK内部结构现在[默认是强封装的](https://bugs.openjdk.java.net/browse/JDK-8256358)，自Java 9以来，这些都是可访问的。但是，现在需要向JVM提供参数–illegal-access=permit。这将影响所有当前直接使用JDK内部结构并忽略警告消息的库和应用程序(尤其是在测试时)。

## 10. 总结

在本文中，我们介绍了作为增量Java 16发行版的一部分引入的一些功能和更改，Java 16中的完整更改列表在[JDK发行说明](https://jdk.java.net/16/release-notes)中可以找到。
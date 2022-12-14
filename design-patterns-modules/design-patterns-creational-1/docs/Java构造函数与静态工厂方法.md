## 1. 概述

Java构造函数是获取完全初始化的类实例的默认机制。毕竟，它们提供了手动或自动注入依赖项所需的所有基础结构。

即便如此，在一些特定的用例中，最好还是使用静态工厂方法来获得相同的结果。

在本教程中，我们将重点介绍使用静态工厂方法与普通旧式Java构造函数的优缺点。

## 2. 静态工厂方法相对于构造函数的优势

在像Java这样的面向对象语言中，构造函数可能有什么问题？总的来说，什么都没有。即便如此，著名的[Joshua Block的Effective Java](https://www.pearson.com/us/higher-education/program/Bloch-Effective-Java-3rd-Edition/PGM1763855.html)第1项明确指出：

>   “考虑静态工厂方法而不是构造函数”

虽然这不是灵丹妙药，但以下是支持这种方法的最令人信服的理由：

1.  **构造函数没有有意义的名称**，因此它们始终受限于语言强加的标准命名约定。**静态工厂方法可以有有意义的名称**，从而明确地传达它们所做的事情
2.  **静态工厂方法可以返回实现方法的相同类型、子类型和原始类型**，因此它们提供了更灵活的返回类型范围
3.  **静态工厂方法可以封装预构造完全初始化实例所需的所有逻辑**，因此它们可用于将这些附加逻辑移出构造函数。这可以防止构造函数[执行进一步的任务，而不仅仅是初始化字段](https://stackoverflow.com/questions/23623516/flaw-constructor-does-real-work)
4.  **静态工厂方法可以是受控实例方法**，[单例模式](https://en.wikipedia.org/wiki/Singleton_pattern)是该特性最明显的例子

## 3. JDK中的静态工厂方法

JDK中有大量静态工厂方法的示例，它们展示了上面概述的许多优点，让我们探讨其中的一些实现。

### 3.1 String类

由于众所周知的[String interning]()，我们不太可能使用[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)类构造函数来创建新的String对象。即便如此，以下语句也是完全合法的：

```java
String value = new String("Tuyucheng");
```

在这种情况下，构造函数将创建一个新的String对象，这是预期的行为。

或者，如果我们想**使用静态工厂方法创建一个新的String对象**，我们可以使用valueOf()方法的以下一些实现：

```java
String value1 = String.valueOf(1);
String value2 = String.valueOf(1.0L);
String value3 = String.valueOf(true);
String value4 = String.valueOf('a');
```

valueOf()有几个重载实现。每个都将返回一个新的String对象，具体取决于传递给方法的参数类型(例如int、long、boolean、char等等)。

方法名称也非常清楚地表达了该方法的作用，它还坚持Java生态系统中用于命名静态工厂方法的公认标准。

### 3.2 Optional类

JDK中静态工厂方法的另一个简洁示例是[Optional](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html)类，这个类实现了一些名称非常有意义的工厂方法，包括[empty()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#empty())、[of()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#of(T))和[ofNullable()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#ofNullable(T))：

```java
Optional<String> value1 = Optional.empty();
Optional<String> value2 = Optional.of("Tuyucheng");
Optional<String> value3 = Optional.ofNullable(null);
```

### 3.3 Collections类

**JDK中静态工厂方法最具代表性的例子很可能是[Collections](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html)类**，这是一个不可实例化的类，仅实现静态方法。

其中许多是工厂方法，在对提供的集合应用某种类型的算法后，它们也会返回集合。

下面是该类工厂方法的一些典型示例：

```java
Collection syncedCollection = Collections.synchronizedCollection(originalCollection);
Set syncedSet = Collections.synchronizedSet(new HashSet());
List<Integer> unmodifiableList = Collections.unmodifiableList(originalList);
Map<String, Integer> unmodifiableMap = Collections.unmodifiableMap(originalMap);
```

JDK中静态工厂方法的数量非常广泛，因此为了简洁起见，我们保持示例代码简单。

尽管如此，上面的例子应该让我们清楚地了解静态工厂方法在Java中是多么普遍。

## 4. 自定义静态工厂方法

当然，我们可以实现自己的静态工厂方法。但是什么时候真正值得这样做，而不是通过简单的构造函数创建类实例？

让我们看一个简单的例子。

考虑下面这个简单的用户类：

```java
public class User {

    private final String name;
    private final String email;
    private final String country;

    public User(String name, String email, String country) {
        this.name = name;
        this.email = email;
        this.country = country;
    }

    // standard getters / toString
}
```

在这种情况下，没有明显的迹象表明静态工厂方法可能比标准构造函数更好。

如果我们希望所有User实例都获得country字段的默认值怎么办？

**如果我们使用默认值初始化字段，我们也必须重构构造函数，从而使设计更加严格**。

我们可以改用静态工厂方法：

```java
public static User createWithDefaultCountry(String name, String email) {
    return new User(name, email, "Argentina");
}
```

以下是我们如何获得一个User实例，并将默认值分配给country字段：

```java
User user = User.createWithDefaultCountry("John", "john@domain.com");
```

## 5. 将逻辑移出构造函数

如果我们决定实现需要向构造函数添加更多逻辑的功能(此时应该敲响警钟)，我们的User类可能会很快变成有缺陷的设计。

**假设我们希望为类提供记录每个用户对象创建时间的功能，如果我们只是把这个逻辑放到构造函数中，我们就违反了[单一职责原则](https://en.wikipedia.org/wiki/Single_responsibility_principle)**。我们最终会得到一个整体式构造函数，它所做的不仅仅是初始化字段。

**我们可以使用静态工厂方法来保持我们的设计整洁**：

```java
public class User {

    private static final Logger LOGGER = Logger.getLogger(User.class.getName());
    private final String name;
    private final String email;
    private final String country;

    // standard constructors / getters

    public static User createWithLoggedInstantiationTime(String name, String email, String country) {
        LOGGER.log(Level.INFO, "Creating User instance at : {0}", LocalTime.now());
        return new User(name, email, country);
    }
}
```

以下是我们如何创建改进的User实例：

```java
User user = User.createWithLoggedInstantiationTime("John", "john@domain.com", "Argentina");
```

## 6. 实例控制的实例化

如上所示，我们可以在返回完全初始化的用户对象之前将逻辑块封装到静态工厂方法中。我们可以做到这一点，而不会因为执行多个不相关的任务而污染构造函数。

**例如，假设我们想使我们的用户类成为单例，我们可以通过实现一个实例控制的静态工厂方法来实现这一点**：

```java
public class User {

    private static volatile User instance = null;

    // other fields / standard constructors / getters

    public static User getSingletonInstance(String name, String email, String country) {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User(name, email, country);
                }
            }
        }
        return instance;
    }
}
```

**getSingletonInstance()方法的实现是线程安全的，由于同步块，性能损失很小**。

在本例中，我们使用延迟初始化来演示实例控制的静态工厂方法的实现。

**然而，值得一提的是，实现单例的最佳方式是使用Java枚举类型，因为它既是序列化安全的又是线程安全的**。有关如何使用不同方法实现单例的完整详细信息，请查看[这篇文章]()。

正如预期的那样，使用此方法获取User对象看起来与前面的示例非常相似：

```java
User user = User.getSingletonInstance("John", "john@domain.com", "Argentina");
```

## 7. 总结

在本文中，我们演示了一些用例，在这些用例中，静态工厂方法可以更好地替代使用普通Java构造函数。

此外，这种重构模式与典型的工作流程紧密相连，以至于大多数IDE都会提醒我们、为我们做这件事。

当然，[Apache NetBeans](https://netbeans.apache.org/)、[IntelliJ IDEA](https://www.jetbrains.com/idea/)和[Eclipse](https://www.eclipse.org/downloads/?)执行重构的方式略有不同，因此请务必先查看你的IDE文档。

与许多其他重构模式一样，我们应该谨慎使用静态工厂方法，并且只有在生成更灵活和干净的设计与必须实现额外方法的成本之间值得权衡时才使用静态工厂方法。
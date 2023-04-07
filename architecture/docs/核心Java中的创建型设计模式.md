## 1. 概述

[设计模式](https://www.baeldung.com/design-patterns-series)是我们在编写软件时使用的常见模式。它们代表了随着时间的推移而发展起来的既定最佳实践。然后，这些可以帮助我们确保我们的代码设计良好且构建良好。

[创建型模式](https://www.baeldung.com/creational-design-patterns)是专注于我们如何获取对象实例的设计模式。通常，这意味着我们如何构造一个类的新实例，但在某些情况下，它意味着获得一个已经构造好的实例供我们使用。

在本文中，我们将重新审视一些常见的创建型设计模式。我们将看到它们的外观以及在 JVM 或其他核心库中的何处可以找到它们。

## 2.工厂方法

工厂方法模式是我们将实例的构造与我们正在构造的类分开的一种方式。这样我们就可以抽象出确切的类型，从而使我们的客户端代码可以根据接口或抽象类来工作：

```java
class SomeImplementation implements SomeInterface {
    // ...
}
复制
public class SomeInterfaceFactory {
    public SomeInterface newInstance() {
        return new SomeImplementation();
    }
}复制
```

在这里，我们的客户端代码永远不需要了解SomeImplementation，而是根据SomeInterface工作。但是，不仅如此，我们还可以更改从工厂返回的类型，而无需更改客户端代码。这甚至可以包括在运行时动态选择类型。

### 2.1. JVM 中的示例

JVM 这种模式最著名的例子可能是Collections类上的集合构建方法，如singleton()、singletonList()和singletonMap() 。这些都返回适当集合的实例——Set 、 List或Map——但具体类型无关紧要。此外，Stream.of()方法和新的Set.of()、List.of()和Map.ofEntries()方法允许我们对更大的集合执行相同的操作。

还有很多其他示例，包括Charset.forName()，它将根据请求的名称返回Charset类的不同实例，以及ResourceBundle.getBundle()，它将加载不同的资源包，具体取决于在提供的名称上。

并非所有这些都需要提供不同的实例。有些只是隐藏内部运作的抽象。例如，Calendar.getInstance()和NumberFormat.getInstance()始终返回相同的实例，但确切的细节与客户端代码无关。

## 3. 抽象工厂

[抽象工厂](https://www.baeldung.com/java-abstract-factory-pattern)模式比这更进一步，其中使用的工厂也有一个抽象基类型。然后我们可以根据这些抽象类型编写代码，并在运行时以某种方式选择具体的工厂实例。

首先，我们有一个接口和一些我们实际想要使用的功能的具体实现：

```java
interface FileSystem {
    // ...
}
复制
class LocalFileSystem implements FileSystem {
    // ...
}
复制
class NetworkFileSystem implements FileSystem {
    // ...
}
复制
```

接下来，我们有一个接口和一些工厂的具体实现来获取以上内容：

```java
interface FileSystemFactory {
    FileSystem newInstance();
}
复制
class LocalFileSystemFactory implements FileSystemFactory {
    // ...
}
复制
class NetworkFileSystemFactory implements FileSystemFactory {
    // ...
}
复制
```

然后我们有另一个工厂方法来获取抽象工厂，通过它我们可以获取实际的实例：

```java
class Example {
    static FileSystemFactory getFactory(String fs) {
        FileSystemFactory factory;
        if ("local".equals(fs)) {
            factory = new LocalFileSystemFactory();
        else if ("network".equals(fs)) {
            factory = new NetworkFileSystemFactory();
        }
        return factory;
    }
}复制
```

在这里，我们有一个具有两个具体实现的FileSystemFactory接口。我们在运行时选择确切的实现，但使用它的代码不需要关心实际使用的是哪个实例。然后它们各自返回一个不同的FileSystem接口的具体实例，但是同样，我们的代码不需要关心我们拥有的是哪个实例。

通常，我们使用另一种工厂方法获取工厂本身，如上所述。在我们这里的例子中，getFactory()方法本身就是一个工厂方法，它返回一个抽象的FileSystemFactory，然后用它来构造一个FileSystem。

### 3.1. JVM 中的示例

在整个 JVM 中有很多这种设计模式的例子。最常见的是 XML 包——例如，DocumentBuilderFactory、TransformerFactory和XPathFactory。这些都有一个特殊的newInstance()工厂方法来让我们的代码获取抽象工厂的实例。

在内部，此方法使用许多不同的机制——系统属性、JVM 中的配置文件和[服务提供者接口](https://www.baeldung.com/java-spi)——来尝试并准确决定使用哪个具体实例。如果我们愿意，这允许我们在我们的应用程序中安装替代的 XML 库，但这对实际使用它们的任何代码都是透明的。

一旦我们的代码调用了newInstance()方法，它就会从适当的 XML 库中获得一个工厂实例。然后这个工厂从同一个库中构建我们想要使用的实际类。

例如，如果我们使用 JVM 默认的 Xerces 实现，我们将获得com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl的实例，但如果我们想使用不同的实现，则调用newInstance()会透明地返回它。

## 4.建造者

当我们想以更灵活的方式构造一个复杂的对象时，Builder 模式很有用。它的工作原理是有一个单独的类，我们用它来构建复杂的对象，并允许客户端使用更简单的接口来创建它：

```java
class CarBuilder {
    private String make = "Ford";
    private String model = "Fiesta";
    private int doors = 4;
    private String color = "White";

    public Car build() {
        return new Car(make, model, doors, color);
    }
}复制
```

这允许我们分别为make、model、doors和color提供值，然后当我们构建Car时，所有构造函数参数都会解析为存储的值。

### 4.1. JVM 中的示例

JVM 中有一些非常关键的这种模式示例。StringBuilder和StringBuffer类是构建器，它们允许我们通过提供许多小部分来构造一个长字符串。最近的Stream.Builder类允许我们做完全相同的事情来构造一个Stream：

```java
Stream.Builder<Integer> builder = Stream.builder<Integer>();
builder.add(1);
builder.add(2);
if (condition) {
    builder.add(3);
    builder.add(4);
}
builder.add(5);
Stream<Integer> stream = builder.build();复制
```

## 5.延迟初始化

我们使用 Lazy Initialization 模式将某些值的计算推迟到需要的时候。有时，这可能涉及个别数据，而其他时候，这可能意味着整个对象。

这在许多情况下都很有用。例如，如果完全构建一个对象需要数据库或网络访问，而我们可能永远不需要使用它，那么执行这些调用可能会导致我们的应用程序性能不佳。或者，如果我们正在计算我们可能永远不需要的大量值，那么这可能会导致不必要的内存使用。

通常，这是通过让一个对象成为我们需要的数据的惰性包装器，并在通过 getter 方法访问时计算数据来实现的：

```java
class LazyPi {
    private Supplier<Double> calculator;
    private Double value;

    public synchronized Double getValue() {
        if (value == null) {
            value = calculator.get();
        }
        return value;
    }
}复制
```

计算 pi 是一项昂贵的操作，我们可能不需要执行。以上将在我们第一次调用getValue()时执行，而不是之前。

### 5.1. JVM 中的示例

JVM 中这样的例子相对较少。然而， Java 8 中引入的[Streams API](https://www.baeldung.com/java-streams)就是一个很好的例子。在流上执行的所有操作都是惰性的，所以我们可以在这里执行昂贵的计算并且知道它们只在需要时调用。

然而，流本身的实际生成也可能是惰性的。Stream.generate()需要一个函数在需要下一个值时调用，并且只在需要时调用。我们可以使用它来加载昂贵的值——例如，通过调用 HTTP API——我们只在实际需要新元素时才支付成本：

```java
Stream.generate(new BaeldungArticlesLoader())
  .filter(article -> article.getTags().contains("java-streams"))
  .map(article -> article.getTitle())
  .findFirst();复制
```

在这里，我们有一个Supplier，它将进行 HTTP 调用以加载文章，根据关联的标签过滤它们，然后返回第一个匹配的标题。如果加载的第一篇文章与此过滤器匹配，则只需要进行一次网络调用，而不管实际存在多少篇文章。

## 6.对象池

在构造对象的新实例时，我们将使用对象池模式，创建该对象的成本可能很高，但重新使用现有实例是一个可接受的替代方案。不是每次都构造一个新实例，我们可以预先构造一组这样的实例，然后根据需要使用它们。

实际对象池的存在是为了管理这些共享对象。它还会跟踪它们，以便每个人同时只在一个地方使用。在某些情况下，整个对象集仅在开始时才构建。在其他情况下，如果有必要，池可能会按需创建新实例

### 6.1. JVM 中的示例

JVM 中此模式的主要示例是线程池的使用。ExecutorService将管理一组线程，并允许我们在任务需要在一个线程上执行时使用它们[。](https://www.baeldung.com/java-executor-service-tutorial)使用这意味着我们不需要创建新线程，只要我们需要产生一个异步任务，就不需要涉及所有的成本：

```java
ExecutorService pool = Executors.newFixedThreadPool(10);

pool.execute(new SomeTask()); // Runs on a thread from the pool
pool.execute(new AnotherTask()); // Runs on a thread from the pool复制
```

这两个任务从线程池中分配了一个线程来运行。它可能是同一个线程，也可能是一个完全不同的线程，使用哪些线程对我们的代码来说并不重要。

## 7.原型

当我们需要创建与原始对象相同的对象的新实例时，我们使用原型模式。原始实例充当我们的原型，并用于构建新实例，这些新实例随后完全独立于原始实例。然后我们可以使用这些但是是必要的。

Java 通过实现Cloneable标记接口然后使用Object.clone()对此有一定程度的支持。这将生成对象的浅表克隆，创建新实例并直接复制字段。

这更便宜，但有一个缺点，即我们的对象内部的任何字段都已经结构化了它们自己将是相同的实例。那么，这意味着对这些字段的更改也会在所有实例中发生。但是，如有必要，我们总是可以自己覆盖它：

```java
public class Prototype implements Cloneable {
    private Map<String, String> contents = new HashMap<>();

    public void setValue(String key, String value) {
        // ...
    }
    public String getValue(String key) {
        // ...
    }

    @Override
    public Prototype clone() {
        Prototype result = new Prototype();
        this.contents.entrySet().forEach(entry -> result.setValue(entry.getKey(), entry.getValue()));
        return result;
    }
}复制
```

### 7.1. JVM 中的示例

JVM 有几个这样的例子。我们可以通过跟踪实现Cloneable接口的类来了解这些。例如，PKIXCertPathBuilderResult、PKIXBuilderParameters、PKIXParameters、PKIXCertPathBuilderResult和PKIXCertPathValidatorResult都是可克隆的。

另一个例子是java.util.Date类。值得注意的是，这会覆盖对象。clone()方法也可以复制一个额外的瞬态字段。

## 8.单例

当我们有一个应该只有一个实例的类时，通常会使用单例模式，并且这个实例应该可以从整个应用程序访问。通常，我们使用通过静态方法访问的静态实例来管理它：

```java
public class Singleton {
    private static Singleton instance = null;

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}复制
```

根据具体需要，对此有多种变体——例如，实例是在启动时创建还是在首次使用时创建，访问它是否需要线程安全，以及每个线程是否需要不同的实例。

### 8.1. JVM 中的示例

JVM 有一些这样的示例，其中的类代表了 JVM 本身的核心部分——Runtime 、Desktop和SecurityManager。这些都有返回相应类的单个实例的访问器方法。

此外，许多 Java 反射 API 都适用于单例实例。相同的实际类总是返回相同的 Class 实例，无论它是使用Class.forName()、String.class还是通过其他反射方法访问的。

以类似的方式，我们可以将表示当前线程的Thread实例视为单例。通常会有很多这样的实例，但根据定义，每个线程只有一个实例。从同一线程中执行的任何地方调用Thread.currentThread()将始终返回相同的实例。

## 9.总结

在本文中，我们了解了用于创建和获取对象实例的各种不同设计模式。我们还查看了在核心 JVM 中使用的这些模式的示例，因此我们可以看到它们以许多应用程序已经从中受益的方式使用。
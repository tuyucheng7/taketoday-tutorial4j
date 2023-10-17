## 1. 概述

在本教程中，我们将重点关注Java语言的一个核心方面——根Object类提供的finalize方法。

简单地说，这是在特定对象的垃圾回收之前调用的。

## 2. 使用终结器

finalize()方法称为终结器。

当JVM发现应该对这个特定实例进行垃圾回收时，终结器就会被调用。这样的终结器可以执行任何操作，包括使对象起死回生。

然而，终结器的主要目的是在对象从内存中删除之前释放对象使用的资源。终结器可以作为清理操作的主要机制，或者在其他方法失败时作为安全网。

要了解终结器的工作原理，让我们看一下类声明：

```java
public class Finalizable {
    private BufferedReader reader;

    public Finalizable() {
        InputStream input = this.getClass()
              .getClassLoader()
              .getResourceAsStream("file.txt");
        this.reader = new BufferedReader(new InputStreamReader(input));
    }

    public String readFirstLine() throws IOException {
        String firstLine = reader.readLine();
        return firstLine;
    }

    // other class members
}
```

Finalizable类有一个字段reader，它引用一个可关闭的资源。当从此类创建对象时，它会构造一个新的BufferedReader实例，从类路径中的文件中读取数据。

这样的实例在readFirstLine方法中用于提取给定文件中的第一行。请注意，阅读器在给定代码中并未关闭。

我们可以使用终结器来做到这一点：

```java
@Override
public void finalize() {
    try {
        reader.close();
        System.out.println("Closed BufferedReader in the finalizer");
    } catch (IOException e) {
        // ...
    }
}
```

很容易看出终结器的声明就像任何普通实例方法一样。

实际上，垃圾收集器调用终结器的时间取决于JVM的实现和系统的条件，这是我们无法控制的。

为了当场进行垃圾收集，我们将利用System.gc方法。在现实世界的系统中，我们永远不应该显式地调用它，原因有很多：

1. 这很昂贵
2. 它不会立即触发垃圾收集——它只是提示JVM启动GC
3. JVM更清楚何时需要调用GC

如果我们需要强制GC，我们可以使用jconsole。

以下是演示终结器操作的测试用例：

```java
@Test
public void whenGC_thenFinalizerExecuted() throws IOException {
    String firstLine = new Finalizable().readFirstLine();
    assertEquals("baeldung.com", firstLine);
    System.gc();
}
```

在第一条语句中，创建了一个Finalizable对象，然后调用了它的readFirstLine方法。此对象未分配给任何变量，因此在调用System.gc方法时它有资格进行垃圾回收。

测试中的断言验证输入文件的内容，仅用于证明我们的自定义类按预期工作。

当我们运行提供的测试时，控制台上将打印一条消息，说明缓冲读取器在终结器中被关闭。这意味着调用了finalize方法并且它已经清理了资源。

到目前为止，终结器看起来像是预销毁操作的好方法。然而，事实并非如此。

在下一节中，我们将了解为什么应避免使用它们。

## 3. 避免终结器

尽管它们带来了好处，但终结器也有许多缺点。

###3.1.终结器的缺点

让我们来看看在使用终结器执行关键操作时我们将面临的几个问题。

第一个值得注意的问题是缺乏及时性。我们无法知道终结器何时运行，因为垃圾收集可能随时发生。

就其本身而言，这不是问题，因为终结器迟早会执行。但是，系统资源不是无限的。因此，我们可能会在清理发生之前耗尽资源，这可能会导致系统崩溃。

终结器也会影响程序的可移植性。由于垃圾收集算法是依赖于JVM实现的，一个程序可能在一个系统上运行良好，而在另一个系统上表现不同。

性能成本是终结器带来的另一个重要问题。具体来说，JVM在构造和销毁包含非空终结器的对象时必须执行更多操作。

我们要讨论的最后一个问题是在终结过程中缺少异常处理。如果终结器抛出异常，则终结过程将停止，使对象处于损坏状态而无需任何通知。

###3.2.终结器效果的演示

是时候抛开理论，看看终结器在实践中的效果了。

让我们定义一个带有非空终结器的新类：

```java
public class CrashedFinalizable {
    public static void main(String[] args) throws ReflectiveOperationException {
        for (int i = 0; ; i++) {
            new CrashedFinalizable();
            // other code
        }
    }

    @Override
    protected void finalize() {
        System.out.print("");
    }
}
```

注意finalize()方法——它只是向控制台打印一个空字符串。如果此方法完全为空，则JVM会将该对象视为没有终结器。因此，我们需要为finalize()提供一个实现，在这种情况下它几乎什么都不做。

在main方法内部，for循环的每次迭代都会创建一个新的CrashedFinalizable实例。此实例未分配给任何变量，因此符合垃圾回收条件。

让我们在标有//其他代码的行中添加几条语句，以查看运行时内存中存在多少对象：

```java
if ((i % 1_000_000) == 0) {
    Class<?> finalizerClass = Class.forName("java.lang.ref.Finalizer");
    Field queueStaticField = finalizerClass.getDeclaredField("queue");
    queueStaticField.setAccessible(true);
    ReferenceQueue<Object> referenceQueue = (ReferenceQueue) queueStaticField.get(null);

    Field queueLengthField = ReferenceQueue.class.getDeclaredField("queueLength");
    queueLengthField.setAccessible(true);
    long queueLength = (long) queueLengthField.get(referenceQueue);
    System.out.format("There are %d references in the queue%n", queueLength);
}
```

给定的语句访问内部JVM类中的某些字段，并在每百万次迭代后打印出对象引用的数量。

让我们通过执行main方法来启动程序。我们可能期望它无限期地运行，但事实并非如此。几分钟后，我们应该会看到系统崩溃并出现类似如下的错误：

```java
...
There are 21914844 references in the queue
There are 22858923 references in the queue
There are 24202629 references in the queue
There are 24621725 references in the queue
There are 25410983 references in the queue
There are 26231621 references in the queue
There are 26975913 references in the queue
Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
    at java.lang.ref.Finalizer.register(Finalizer.java:91)
    at java.lang.Object.<init>(Object.java:37)
    at finalize.cn.tuyucheng.taketoday.CrashedFinalizable.<init>(CrashedFinalizable.java:6)
    at finalize.cn.tuyucheng.taketoday.CrashedFinalizable.main(CrashedFinalizable.java:9)

Process finished with exit code 1
```

看起来垃圾收集器没有做好它的工作——对象的数量一直在增加，直到系统崩溃。

如果我们删除终结器，引用的数量通常为0，程序将一直运行下去。

###3.3.解释

要理解为什么垃圾收集器没有按照应有的方式丢弃对象，我们需要了解JVM的内部工作方式。

创建具有终结器的对象(也称为引用对象)时，JVM会创建一个附带的java.lang.ref.Finalizer类型的引用对象。在引用对象准备好进行垃圾回收后，JVM将引用对象标记为可以处理，并将其放入引用队列中。

我们可以通过java.lang.ref.Finalizer类中的静态字段队列访问这个队列。

与此同时，一个名为Finalizer的特殊守护线程保持运行并在引用队列中查找对象。当它找到一个时，它会从队列中删除引用对象并调用引用对象的终结器。

在下一个垃圾回收周期中，引用对象将被丢弃——当它不再被引用对象引用时。

如果一个线程持续高速生产对象，这就是我们示例中发生的情况，Finalizer线程就无法跟上。最终，内存将无法存储所有对象，我们最终会遇到OutOfMemoryError。

请注意，如本节所示，以极快的速度创建对象的情况在现实生活中并不常见。然而，它证明了一个重要的点——终结器非常昂贵。

##4.无终结器示例

让我们探索一个提供相同功能但不使用finalize()方法的解决方案。请注意，下面的示例并不是替换终结器的唯一方法。

相反，它用来说明一个重要的观点：总有一些选项可以帮助我们避免终结器。

这是我们新类的声明：

```java
public class CloseableResource implements AutoCloseable {
    private BufferedReader reader;

    public CloseableResource() {
        InputStream input = this.getClass()
          .getClassLoader()
          .getResourceAsStream("file.txt");
        reader = new BufferedReader(new InputStreamReader(input));
    }

    public String readFirstLine() throws IOException {
        String firstLine = reader.readLine();
        return firstLine;
    }

    @Override
    public void close() {
        try {
            reader.close();
            System.out.println("Closed BufferedReader in the close method");
        } catch (IOException e) {
            // handle exception
        }
    }
}
```

不难看出，新的CloseableResource类与我们之前的Finalizable类之间的唯一区别是AutoCloseable接口的实现而不是终结器定义。

请注意，CloseableResource的close方法的主体与类Finalizable中的终结器的主体几乎相同。

下面是一个测试方法，它读取一个输入文件并在完成它的工作后释放资源：

```java
@Test
public void whenTryWResourcesExits_thenResourceClosed() throws IOException {
    try (CloseableResource resource = new CloseableResource()) {
        String firstLine = resource.readFirstLine();
        assertEquals("baeldung.com", firstLine);
    }
}
```

在上面的测试中，在try-with-resources语句的try块中创建了一个CloseableResource实例，因此当try-with-resources块完成执行时，该资源将自动关闭。

运行给定的测试方法，我们将看到一条消息从CloseableResource类的关闭方法打印出来。

##5.总结

在本教程中，我们重点介绍了Java中的一个核心概念——finalize方法。这在纸面上看起来很有用，但在运行时可能会产生丑陋的副作用。而且，更重要的是，总有使用终结器的替代解决方案。

需要注意的一个关键点是，从Java9开始，finalize已被弃用——最终将被删除。
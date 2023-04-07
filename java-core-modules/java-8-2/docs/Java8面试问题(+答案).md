## **一、简介**

在本教程中，我们将探讨在面试中可能会出现的一些与 JDK8 相关的问题。

Java 8 是一个包含新语言特性和库类的平台版本。这些新特性中的大多数都旨在实现更简洁、更紧凑的代码，而有些则添加了 Java 以前从未支持过的新功能。

## 延伸阅读：

## [Java 面试问题中的内存管理（+答案）](https://www.baeldung.com/java-memory-management-interview-questions)

一组流行的内存管理相关面试问题，当然还有答案。

[阅读更多](https://www.baeldung.com/java-memory-management-interview-questions)→

## [Java 集合面试题](https://www.baeldung.com/java-collections-interview-questions)

一组实用的Collections相关的Java面试题

[阅读更多](https://www.baeldung.com/java-collections-interview-questions)→

## **2. Java 8 常识**

### **Q1。Java 8 添加了哪些新特性？**

Java 8 附带了几个新特性，但最重要的是：

-   **Lambda 表达式**——一种新的语言特性，允许我们将动作视为对象
-   **方法引用**- 使我们能够通过直接使用方法名称来引用方法来定义 Lambda 表达式
-   ***可选***- 用于表达可选性的特殊包装类
-   **函数式接口**——最多有一个抽象方法的接口；可以使用 Lambda 表达式提供实现
-   **默认方法**- 使我们能够在接口中添加除抽象方法之外的完整实现
-   **Nashorn, JavaScript Engine** - 用于执行和评估 JavaScript 代码的基于 Java 的引擎
-   ***Stream\* API——**一个特殊的迭代器类，允许我们以函数式方式处理对象集合
-   **Date API** - 一种改进的、不可变的、受 JodaTime 启发的 Date API

除了这些新特性之外，在编译器和 JVM 级别都进行了大量的特性增强。

## **三、方法参考**

### **Q1。什么是方法参考？**

方法引用是一种 Java 8 构造，可用于在不调用方法的情况下引用它。它用于将方法视为 Lambda 表达式。它们仅用作语法糖来减少某些 lambda 的冗长。这样下面的代码：

```java
(o) -> o.toString();复制
```

可以变成：

```java
Object::toString();复制
```

方法引用可以通过将类或对象名称与方法名称分隔开的双冒号来标识。它有不同的变体，例如构造函数引用：

```java
String::new;复制
```

静态方法参考：

```java
String::valueOf;复制
```

绑定实例方法参考：

```java
str::toString;复制
```

未绑定实例方法参考：

```java
String::toString;复制
```

[我们可以通过此链接](https://www.codementor.io/eh3rrera/using-java-8-method-reference-du10866vx)和[此链接](https://www.baeldung.com/java-8-double-colon-operator)阅读带有完整示例的方法参考的详细说明。

### **Q2。String::Valueof 表达式的含义是什么？**

*它是对String*类的*valueOf*方法的静态方法引用。

## **4.\*可选\***

### **Q1。什么是\*可选的\*？如何使用？**

*Optional*是 Java 8 中的一个新类，它封装了一个可选值，即要么存在要么不存在的值。它是对象的包装器，我们可以将其视为零个或一个元素的容器。

*Optional*有一个特殊的*Optional.empty()*值而不是包装的*null*。因此，在许多情况下，它可以代替可空值来摆脱*NullPointerException 。*

[我们可以在这里阅读一篇关于](https://www.baeldung.com/java-optional)*Optional* 的专门文章。

*Optional*的主要目的，正如其创建者所设计的那样，是成为以前会返回*null*的方法的返回类型。此类方法需要我们编写样板代码来检查返回值，而我们有时可能会忘记进行防御性检查。在 Java 8 中，*Optional*返回类型明确要求我们以不同方式处理 null 或非 null 包装值。

例如，*Stream.min()*方法计算值流中的最小值。但是如果流是空的呢？如果不是*Optional*，该方法将返回*null*或抛出异常。

但是，它返回一个*Optional*值，该值可能是*Optional.empty()*（第二种情况）。这使我们能够轻松处理此类情况：

```java
int min1 = Arrays.stream(new int[]{1, 2, 3, 4, 5})
  .min()
  .orElse(0);
assertEquals(1, min1);

int min2 = Arrays.stream(new int[]{})
  .min()
  .orElse(0);
assertEquals(0, min2);

复制
```

值得注意的是，*Optional*不是像Scala 中的*Option 那样*的通用类。不建议我们将它作为实体类中的字段值使用，它没有实现*Serializable*接口就清楚地表明了这一点。

## **5. 功能接口**

### **Q1。描述标准库中的一些功能接口**

*java.util.function*包中有很多函数式接口。比较常见的包括但不限于：

-   *函数*——接受一个参数并返回一个结果
-   *消费者*- 它接受一个参数并且不返回任何结果（代表副作用）
-   *供应商*——它不接受参数并返回结果
-   *谓词*- 它接受一个参数并返回一个布尔值
-   *BiFunction* – 它接受两个参数并返回一个结果
-   *BinaryOperator* – 它类似于 BiFunction *，*接受两个参数并返回一个结果。两个参数和结果都是相同的类型。
-   *UnaryOperator* – 它类似于*Function*，采用单个参数并返回相同类型的结果

有关函数式接口的更多信息，请参阅文章[“Java 8 中的函数式接口”。](https://www.baeldung.com/java-8-functional-interfaces)

### **Q2。什么是功能接口？函数式接口的定义规则是什么？**

函数式接口是具有一个抽象方法的接口（*默认*方法不算在内），不多也不少。

如果需要此类接口的实例，则可以改用 Lambda 表达式。更正式地说：*函数式接口*为 lambda 表达式和方法引用提供目标类型。

这种表达式的参数和返回类型直接匹配单个抽象方法的参数和返回类型。

例如，*Runnable*接口是一个功能接口，所以不是：

```java
Thread thread = new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello World!");
    }
});复制
```

我们可以简单地做：

```java
Thread thread = new Thread(() -> System.out.println("Hello World!"));复制
```

功能接口通常用*@FunctionalInterface*注释进行注释，它提供信息并且不影响语义。

## **6.默认方法**

### **Q1。什么是默认方法以及我们何时使用它？**

默认方法是具有实现的方法，可以在接口中找到。

我们可以使用默认方法向接口添加新功能，同时保持与已经实现该接口的类的向后兼容性：

```java
public interface Vehicle {
    public void move();
    default void hoot() {
        System.out.println("peep!");
    }
}复制
```

通常当我们向接口添加一个新的抽象方法时，所有实现类都会中断，直到它们实现了新的抽象方法。在 Java 8 中，使用默认方法解决了这个问题。

例如，*Collection*接口没有*forEach*方法声明。因此，添加这样的方法只会破坏整个集合 API。

Java 8 引入了默认方法，以便*Collection接口可以具有**forEach*方法的默认实现，而无需实现此接口的类也实现相同的方法。

### **Q2。以下代码可以编译吗？**

```java
@FunctionalInterface
public interface Function2<T, U, V> {
    public V apply(T t, U u);

    default void count() {
        // increment counter
    }
}复制
```

是的，代码会编译，因为它遵循仅定义单个抽象方法的功能接口规范。第二种方法*count*是默认方法，不会增加抽象方法计数。

## **7. Lambda 表达式**

### **Q1。什么是 Lambda 表达式及其用途？**

用非常简单的术语来说，lambda 表达式是一个我们可以引用并作为对象传递的函数。

此外，lambda 表达式在 Java 中引入了函数式风格处理，有助于编写紧凑易读的代码。

因此，lambda 表达式是匿名类（例如方法参数）的自然替代品。它们的主要用途之一是定义功能接口的内联实现。

### **Q2。解释 Lambda 表达式的语法和特征**

lambda 表达式由两部分组成，参数部分和表达式部分由向前箭头分隔：

```java
params -> expressions复制
```

任何 lambda 表达式都具有以下特征：

-   **可选类型声明**——在 lambda 左侧声明参数时，我们不需要声明它们的类型，因为编译器可以从它们的值中推断出它们。所以*int param -> …*和*param ->…*都是有效的
-   **可选括号**——当只声明一个参数时，我们不需要将它放在括号中。这意味着*param -> …*和*(param) -> …*都是有效的，但是当声明了多个参数时，需要括号
-   **可选花括号**——当表达式部分只有一个语句时，不需要花括号。也就是说*param->statement*和*param->{statement;}*都是有效的，但是有多个statement的时候需要花括号
-   **可选的 return 语句**——当表达式返回一个值并用大括号括起来时，我们就不需要 return 语句。也就是说*(a, b) -> {return a+b;}*和*(a, b) -> {a+b;}*都是有效的

要阅读有关 Lambda 表达式的更多信息，请点击[此链接](https://www.tutorialspoint.com/java8/java8_lambda_expressions.htm)和[此链接](https://www.baeldung.com/java-8-lambda-expressions-tips)。

## **8.犀牛Javascript**

### **Q1。Java8 中的 Nashorn 是什么？**

[Nashorn](https://www.baeldung.com/java-nashorn)是 Java 8 附带的用于 Java 平台的新 Javascript 处理引擎。在 JDK 7 之前，Java 平台出于相同的目的使用 Mozilla Rhino 作为 Javascript 处理引擎。

Nashorn 比其前身更好地符合 ECMA 规范化 JavaScript 规范和更好的运行时性能。

### **Q2。什么是 JJS？**

在 Java 8 中，*jjs*是我们用来在控制台执行 Javascript 代码的新的可执行文件或命令行工具。

## **9. 溪流**

### **Q1。什么是流？它与收藏有何不同？**

简单来说，流是一个迭代器，其作用是接受一组操作以应用于它包含的每个元素。

流表示来自支持聚合操作*的*源（例如集合）的一系列对象。它们旨在使集合处理简单明了。与集合相反，迭代的逻辑是在流内部实现的，因此我们可以使用*map*和*flatMap*等方法来执行声明式处理。

此外，*Stream* API 是流畅的并允许流水线操作：

```java
int sum = Arrays.stream(new int[]{1, 2, 3})
  .filter(i -> i >= 2)
  .map(i -> i * 3)
  .sum();复制
```

与集合的另一个重要区别是流本质上是延迟加载和处理的。

### **Q2。中间操作和终端操作有什么区别？**

我们将流操作组合成管道来处理流。所有操作都是中间的或终端的。

中间操作是那些返回*Stream*本身的操作，允许对流进行进一步的操作。

这些操作总是惰性的，即它们不在调用点处理流。中间操作只有在有终端操作时才能处理数据。一些中间操作是*filter*、*map*和*flatMap*。

相反，终端操作终止管道并启动流处理。流在终端操作调用期间通过所有中间操作。终端操作包括*forEach*、*reduce、Collect*和*sum*。

为了说明这一点，让我们看一个有副作用的例子：

```java
public static void main(String[] args) {
    System.out.println("Stream without terminal operation");
    
    Arrays.stream(new int[] { 1, 2, 3 }).map(i -> {
        System.out.println("doubling " + i);
        return i * 2;
    });
 
    System.out.println("Stream with terminal operation");
        Arrays.stream(new int[] { 1, 2, 3 }).map(i -> {
            System.out.println("doubling " + i);
            return i * 2;
    }).sum();
}复制
```

输出将如下所示：

```plaintext
Stream without terminal operation
Stream with terminal operation
doubling 1
doubling 2
doubling 3复制
```

正如我们所见，中间操作仅在存在终端操作时触发。

### **Q3. \*Map\*和\*flatMap\*流操作有什么区别？**

*map*和*flatMap*之间的签名有所不同。一般来说，*map*操作会将其返回值包装在其序号类型中，而*flatMap*则不会。

例如，在*Optional*中，*map*操作将返回*Optional<String>*类型，而*flatMap*将返回*String*类型。

因此在映射之后，我们需要解包（读作“展平”）对象来检索值，而在平面映射之后，没有这种需要，因为对象已经展平了。我们将相同的概念应用于*Stream*中的映射和平面映射。

map和*flatMap都是中间流操作，它们接收一个函数并将该**函数*应用于流的所有元素。

不同之处在于，对于*map*，此函数返回一个值，而对于*flatMap*，此函数返回一个流。flatMap操作*将*流“扁平化”为一个。

*这是一个示例，我们使用flatMap*获取用户名和电话列表的地图，并将其“扁平化”为所有用户的电话列表：

```java
Map<String, List<String>> people = new HashMap<>();
people.put("John", Arrays.asList("555-1123", "555-3389"));
people.put("Mary", Arrays.asList("555-2243", "555-5264"));
people.put("Steve", Arrays.asList("555-6654", "555-3242"));

List<String> phones = people.values().stream()
  .flatMap(Collection::stream)
    .collect(Collectors.toList());复制
```

### **Q4. 什么是 Java 8 中的流管道？**

流水线是将操作链接在一起的概念。为此，我们将流上可能发生的操作分为两类：中间操作和终端操作。

每个中间操作在运行时都会返回一个 Stream 本身的实例。因此，我们可以设置任意数量的中间操作来处理数据，形成一个处理流水线。

然后必须有一个返回最终值并终止管道的终端操作。

## **10. Java 8 日期和时间 API**

### **Q1。告诉我们 Java 8 中新的日期和时间 API**

Java 开发人员长期存在的问题是对普通开发人员所需的日期和时间操作的支持不足。

现有的类（例如*java.util.Date*和*SimpleDateFormatter）*不是线程安全的，这会给用户带来潜在的并发问题。

糟糕的 API 设计也是旧 Java 数据 API 中的一个现实。这里只是一个简单的例子：*java.util.Date*中的年从 1900 开始，月从 1 开始，天从 0 开始，这不是很直观。

这些问题和其他几个问题导致了第三方日期和时间库的流行，例如 Joda-Time。

为了解决这些问题并在 JDK 中提供更好的支持，在 java.time 包下为 Java SE 8 设计了一个没有这些问题的新日期和时间*API*。

## **11.结论**

在本文中，我们探讨了几个偏向于 Java 8 的重要技术面试问题。这绝不是一个详尽的列表，但它包含了我们认为最有可能出现在 Java 8 的每个新特性中的问题。

即使我们刚刚起步，对 Java 8 一无所知也不是参加面试的好方法，尤其是当 Java 在简历中显得很重要时。因此，重要的是我们需要花一些时间来了解这些问题的答案，并可能进行更多研究。

祝面试顺利。
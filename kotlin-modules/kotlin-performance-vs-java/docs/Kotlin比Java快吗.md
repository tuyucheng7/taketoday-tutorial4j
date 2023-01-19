## 一、简介

Kotlin 比 Java 更具表现力和简洁性，但它有成本吗？更准确地说，选择 Kotlin 而不是 Java 是否会导致性能下降？

在大多数情况下，Kotlin 编译成与 Java 相同的字节码。类、函数、函数参数和标准流程控制运算符(例如if和for)的工作方式相同。

但是，Kotlin 和 Java 之间存在差异。例如，Kotlin 有 [内联函数](https://kotlinlang.org/docs/inline-functions.html)。如果这样的函数将 lambda 作为参数，则字节码中没有实际的 lambda。相反，当在内联函数中需要它们时，编译器会重写调用站点以调用 lambda 中的指令。所有的[集合转换函数](https://www.baeldung.com/kotlin/collection-transformations)——例如map、filter、associate、first、find any等等——都是内联函数。

除此之外，要在 Java 中对集合使用函数转换，我们必须从集合中创建一个Stream，然后使用Collector 将该Stream收集到目标集合。当我们需要对大型集合进行一系列转换时，这是有道理的。然而，当我们只需要映射我们的短集合一次并获得结果时，创建额外对象的代价与有用的有效载荷相当。

在本文中，我们将研究如何衡量 Java 和 Kotlin 代码之间的差异，并分析这种差异有多大。

## 2. Java 微基准测试工具

由于 Kotlin 编译为与 Java 相同的 JVM 字节码，我们可以使用[Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness) (JMH) 来分析 Java 和 Kotlin 代码的性能。要设置项目，我们将创建一个简单的基于 Gradle 的项目并使用一个[简洁的小插件](https://github.com/melix/jmh-gradle-plugin)连接到 JMH 框架：

让我们写一些测试用例。我们将看到的数字是在配备 32GB RAM 的 Apple M1 Pro 笔记本电脑上获得的，该笔记本电脑运行 macOS Monterey 12.1 和 OpenJDK 64 位服务器 VM，17.0.1+12-39。里程可能因其他系统和软件版本而异。

我们将从每个测试函数返回一些值并将它们放入Blackhole中。这将防止 JIT 编译器过度优化我们运行的代码。我们将使用合理数量的预热迭代以获得适当的平均结果：

```java
@Benchmark
@BenchmarkMode(Mode.Throughput)
@Fork(value = 5, warmups = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
```

结果和随后的结果描述了吞吐量：每单位时间的重复次数—— 次数越多，操作越快。时间单位因测试而异：我们既不希望每单位时间的操作数太大也不希望太小。对于实验来说，它是无关紧要的，但使用合理大小的数字更容易理解和操作。

## 3.内联高阶函数

第一种情况是内联高阶函数。我们的函数将模仿在事务下执行一个动作：我们将创建一个事务对象，打开它，执行作为参数传递的动作，然后提交事务。

处理 lambda 的 Java 方法涉及字节码中的invokedynamic指令。该指令要求 JIT 编译器创建属于实现功能接口的生成类的调用站点对象。所有这一切都发生在幕后，我们只需创建一个 lambda：

```java
public static <T> T inTransaction(JavaDatabaseTransaction.Database database, Function<JavaDatabaseTransaction.Database, T> action) {
    var transaction = new JavaDatabaseTransaction.Transaction(UUID.randomUUID());
    try {
        var result = action.apply(database);
        transaction.commit();
        return result;
    } catch (Exception ex) {
        transaction.rollback();
        throw ex;
    }
}

public static String transactedAction(Object obj) throws MalformedURLException {
    var database = new JavaDatabaseTransaction.Database(new URL("http://localhost"), "user:pass");
    return inTransaction(database, d -> UUID.randomUUID() + obj.toString());
}
```

Kotlin 的做法非常相似，但更简洁。然而，在字节码层面，情况就完全不同了。inTransaction方法只会被到调用站点，根本不会有 lambda：

```kotlin
inline fun <T> inTransaction(db: Database, action: (Database) -> T): T {
    val transaction = Transaction(id = UUID.randomUUID())
    try {
        return action(db).also { transaction.commit() }
    } catch (ex: Exception) {
        transaction.rollback()
        throw ex
    }
}

fun transactedAction(arg: Any): String {
    val database = Database(URL("http://localhost"), "user:pass")
    return inTransaction(database) { UUID.randomUUID().toString() + arg.toString() }
}
```

在实际使用数据库的情况下，这两种方法之间的差异可以忽略不计，因为这里的驱动成本将是网络 IO。但是让我们看看内联是否提供了很大的不同：

```plaintext
Benchmark                            Mode  Cnt     Score       Error   Units
KotlinVsJava.inlinedLambdaKotlin     thrpt  25     1433.691 ± 108.326  ops/ms
KotlinVsJava.lambdaJava              thrpt  25      993.428 ±  25.065  ops/ms
```

事实证明，确实如此！看起来，内联比在运行时生成动态调用站点的效率高 44% 。这意味着，对于高阶函数，尤其是在关键路径上，我们应该考虑inline。

## 4. 函数式集合转换

正如我们已经说过的，Java Stream API 比 Kotlin Collections 库函数需要多创建一个对象。让我们看看这是否值得注意。我们会将我们的字符串模型列表放入@State(Scope.Benchmark)容器中，这样 JIT 编译器就不会优化掉我们的重复操作。

Java 实现非常简短：

```kotlin
public static List<String> transformStringList(List<String> strings) {
    return strings.stream().map(s -> s + System.currentTimeMillis()).collect(Collectors.toList());
}
```

Kotlin 的更短：

```kotlin
fun transformStringList(strings: List<String>) =
    strings.map { it + System.currentTimeMillis() }
```

我们正在使用currentTimeMillis，这样每次调用函数时我们的每个字符串都会不同。这样我们就可以告诉 JIT 编译器不要优化掉我们运行的所有代码。

该运行的结果不是决定性的：

```plaintext
Benchmark                              Mode    Cnt   Score     Error    Units
KotlinVsJava.stringCollectionJava    thrpt   25    1982.486 ± 112.839 ops/ms
KotlinVsJava.stringCollectionKotlin  thrpt   25    1760.223 ± 69.072  ops/ms
```

看起来 Java 甚至快了 12%。这可以解释为 Kotlin 会执行额外的隐式检查，例如对不可为 null 的参数进行 null 检查，如果我们查看字节码，这些检查就会变得可见：

```plaintext
  public final static transformStringList(Ljava/util/List;)Ljava/util/List;
  // skip irrelevant stuff
   L0
    ALOAD 0
    LDC "strings"
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
...
```

map函数中还有额外的检查以确保正确的ArrayList大小。由于我们在函数中没有做太多其他事情，这些小事情开始显现出来。

当我们处理短集合并进行廉价转换时，额外实例化的效果应该更加明显。在这种情况下，Stream对象的创建将很明显：

```kotlin
fun mapSmallCollection() =
    (1..10).map { java.lang.String.valueOf(it) }
```

对于 Java 版本：

```java
public static List<String> transformSmallList() {
    return IntStream.range(1, 10)
      .mapToObj(String::valueOf)
      .collect(Collectors.toList());
}
```

我们可以看到差异是相反的：

```plaintext
Benchmark                              Mode    Cnt   Score     Error    Units
KotlinVsJava.smallCollectionJava     thrpt   25    15.135 ± 0.932     ops/us
KotlinVsJava.smallCollectionKotlin   thrpt   25    17.826 ± 0.332     ops/us
```

另一方面，这两种操作都非常快，差异不太可能成为实际生产代码中的重要因素。

## 5. 带有扩展运算符的可变参数

在 Java 中，变量参数构造只是语法糖。每个这样的参数实际上是一个数组。如果我们已经将数据放在数组中，我们可以直接将其用作参数：

```java
public static String concatenate(String... pieces) {
    StringBuilder sb = new StringBuilder(pieces.length  8);
    for(String p : pieces) {
        sb.append(p).append(",");
    }
    return sb.toString();
}

public static String callConcatenate(String[] pieces) {
    return concatenate(pieces);
}
```

然而，在 Kotlin 中，可变参数是一种特例。如果我们的数据已经在一个数组中，我们将不得不展开该数组：

```kotlin
fun concatenate(vararg pieces: String): String = pieces.joinToString()

fun callVarargFunction(pieces: Array<out String>) = concatenate(pieces)
```

让我们看看这是否意味着性能上的任何惩罚：

```plaintext
Benchmark                              Mode    Cnt   Score     Error    Units
KotlinVsJava.varargsJava               thrpt    25    14.653 ± 0.089    ops/us
KotlinVsJava.varargsKotlin             thrpt    25    12.468 ± 0.279    ops/us
```

事实上， Kotlin 因传播(涉及数组副本)而受到约 17% 的惩罚。这意味着在我们代码的性能关键部分，最好避免使用扩展来调用带有可变参数的函数。

## 6. 更改 Java Bean 与数据类，包括初始化

Kotlin 引入了数据类的概念并提倡大量使用不可变的val字段，这与传统的 Java 字段相反，后者可通过 setter 方法进行编辑。如果我们需要更改数据类中的字段，我们必须使用方法并将更改的字段替换为所需的值：

```kotlin
fun changeField(input: DataClass, newValue: String): DataClass = input.copy(fieldA = newValue)
```

这导致了一个新对象的实例化。让我们检查一下它是否比使用 Java 样式的可编辑字段花费更多的时间。对于实验，我们将创建一个只有一个字段的最小数据类：

```kotlin
data class DataClass(val fieldA: String)
```

我们还将使用最简单的 POJO：

```java
public class POJO {
    private String fieldA;

    public POJO(String fieldA) {
        this.fieldA = fieldA;
    }

    public String getFieldA() {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }
}
```

为了防止 JIT 过度优化测试代码，让我们将初始字段值和新值都放入@State对象中：

```java
@State(Scope.Benchmark)
public static class InputString {
    public String string1 = "ABC";
    public String string2 = "XYZ";
}
```

现在，让我们运行测试，它创建一个对象，更改它并将其传递给Blackhole：

```plaintext
Benchmark                              Mode    Cnt   Score     Error    Units
KotlinVsJava.changeFieldJava           thrpt   25    337.300 ± 1.263     ops/us 
KotlinVsJava.changeFieldKotlin         thrpt   25    351.128 ± 0.910     ops/us
```

实验表明一个对象比改变它的字段快 4%。事实上，进一步的研究表明，随着字段数量的增加，两种方法之间的性能差异会越来越大。让我们来看一个更复杂的数据类：

```kotlin
data class DataClass(
    val fieldA: String,
    val fieldB: String,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val age: Int,
    val salary: BigDecimal,
    val currency: Currency,
    val child: InnerDataClass
)

data class InnerDataClass(val fieldA: String, val fieldB: String)
```

还有一个类似的 Java POJO。那么类似基准的结果将是这样的：

```bash
Benchmark                        Mode   Cnt    Score     Error   Units
KotlinVsJava.changeFieldJava     thrpt   25    100,503 ± 1,047    ops/us
KotlinVsJava.changeFieldKotlin   thrpt   25    126,282 ± 0,232    ops/us
```

这似乎违反直觉，但让我们注意，与只有一个字段的普通数据类相比，这个数据类的整体执行速度几乎慢了三倍。然后记住我们把初始构建放到了benchmark中，还有一个字段的改变。如前所述，[final](https://www.baeldung.com/java-final-performance)关键字有时会对 performance 产生影响，而且通常影响很小但很积极。显然，构造函数成本在这个基准中占主导地位。

## 7. 更改 Java Bean 与数据类，排除初始化

如果我们使用@State 对象隔离 Kotlin 的和 Java 的修改：

```kotlin
@State(Scope.Thread)
public static class InputKotlin {
    public DataClass pojo;

    @Setup(Level.Trial)
    public void setup() {
        pojo = new DataClass(
                "ABC",
                "fieldB",
                "Baker st., 221b",
                "Marylebone",
                "London",
                (int) (31 + System.currentTimeMillis() % 17),
                new BigDecimal("30000.23"),
                Currency.getInstance("GBP"),
                new InnerDataClass("a", "b")
        );
    }

    public String string2 = "XYZ";
}

// Proper benchmark annotations
public void changeFieldKotlin_changingOnly(Blackhole blackhole, InputKotlin input) {
    blackhole.consume(DataClassKt.changeField(input.pojo, input.string2));
}
```

我们会看到另一张照片：

```bash
Benchmark                                     Mode    Cnt    Score     Error   Units
KotlinVsJava.changeFieldJava_changingOnly     thrpt   25     364,745 ± 2,470    ops/us
KotlinVsJava.changeFieldKotlin_changingOnly   thrpt   25     163,215 ± 1,235    ops/us
```

所以看起来修改比快 2.23 倍。然而，实例化一个可变对象要慢得多：我们可以创建一个不可变对象两次，并且仍然使用变异方法领先于可变对象构造函数。

总而言之，不可变的方法肯定会更慢。然而，不会慢几个数量级。其次，如果我们需要更改多个属性，那么我们会提前：对于 Koltin 数据类，它仍然是单个copy()调用，而对于可变对象，它意味着多个 setter 调用。第三，可变对象的实例化比不可变对象的实例化成本更高，因此根据整体代码，最终得分可能不利于 POJO。最后，无论如何，所有这些成本都非常小，它们将在实际应用程序中由 IO和业务逻辑主导。

因此，我们可以得出总结，使用不可变结构和而不是使用 setter 不会显着影响我们程序的性能。

## 八、总结

在本文中，我们研究了如何通过与 Java 的比较来检验关于 Kotlin 性能的假设。大多数情况下，正如预期的那样，Kotlin 的性能与 Java 的性能相当。在某些地方有小的收获，比如内联 lambda。相反，在其他方面有明显的损失，比如在可变参数参数中传播一个数组。

在所有条件都相当的情况下，很明显，将其他函数或 lambda 作为参数的内联函数是非常有益的。

然而，重要的是Kotlin 提供与 Java 几乎相同的运行时性能。它的使用不会成为生产中的问题。

我们还学习了如何快速有效地使用 JMH 框架来衡量代码的性能。通过合理的性能测试工具，我们可以在问题开始之前预测生产中的问题。这里有一个重要的要点是，许多因素都可能影响 JMH 测试，所有基准测试结果都应该持保留态度。
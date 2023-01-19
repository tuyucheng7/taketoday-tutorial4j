## 1. 简介

在本教程中，我们将 使用示例数据处理应用程序演示[Apache Crunch 。](https://crunch.apache.org/)我们将使用[MapReduce](https://en.wikipedia.org/wiki/MapReduce)框架运行此应用程序。

我们将从简要介绍一些 Apache Crunch 概念开始。然后我们将跳转到示例应用程序。在这个应用程序中，我们将进行文本处理：

-   首先，我们将从文本文件中读取行
-   稍后，我们会将它们拆分为单词并删除一些常用单词
-   然后，我们将对剩余的单词进行分组以获得唯一单词及其计数的列表
-   最后，我们将这个列表写入一个文本文件

## 2. 什么是紧缩？

MapReduce 是一种分布式并行编程框架，用于在服务器集群上处理大量数据。Hadoop 和 Spark 等软件框架实现了 MapReduce。

Crunch 提供了一个框架，用于在Java中编写、测试和运行 MapReduce 管道。 在这里，我们不直接编写 MapReduce 作业。相反，我们使用 Crunch API 定义数据管道(即执行输入、处理和输出步骤的操作)。Crunch Planner 将它们映射到 MapReduce 作业并在需要时执行它们。

因此，每个 Crunch 数据管道都由Pipeline接口的实例协调。该接口还定义了通过Source实例将数据读入管道以及将数据从管道写入Target实例的方法。

我们有 3 个接口来表示数据：

1.  PCollection—— 一个不可变的分布式元素集合
2.  PTable<K , V > – 一个不可变的、分布式的、无序的键和值的多重映射
3.  PGroupedTable<K , V > – K 类型键到Iterable V 的分布式排序映射，可以迭代一次

DoFn 是所有数据处理功能的基类。对应MapReduce中的 Mapper、 Reducer 、 Combiner 类。我们花费大部分开发时间来编写和测试使用它的逻辑计算。

现在我们对 Crunch 更加熟悉了，让我们用它来构建示例应用程序。

## 3. 建立一个紧缩项目

首先，让我们用 Maven 建立一个 Crunch 项目。我们可以通过两种方式做到这一点：

1.  在已有项目的pom.xml文件中添加需要的依赖
2.  使用原型生成入门项目

让我们快速浏览一下这两种方法。

### 3.1. Maven 依赖项

为了将 Crunch 添加到现有项目中，让我们在 pom.xml文件中添加所需的依赖项。

首先，让我们添加crunch-core库：

```xml
<dependency>
    <groupId>org.apache.crunch</groupId>
    <artifactId>crunch-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

接下来，让我们添加hadoop-client库来与 Hadoop 通信。我们使用匹配 Hadoop 安装的版本：

```xml
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-client</artifactId>
    <version>2.2.0</version>
    <scope>provided</scope>
</dependency>
```

我们可以检查 Maven Central 以获取最新版本的[crunch-core](https://search.maven.org/search?q=g:org.apache.crunch AND a:crunch-core&core=gav)和[hadoop-client](https://search.maven.org/search?q=g:org.apache.hadoop AND a:hadoop-client&core=gav)库。

### 3.2. Maven原型

另一种方法是使用 Crunch 提供的 Maven 原型快速生成启动项目：

```plaintext
mvn archetype:generate -Dfilter=org.apache.crunch:crunch-archetype

```

当上述命令提示时，我们提供 Crunch 版本和项目工件详细信息。

## 4.紧缩管道设置

设置项目后，我们需要创建一个Pipeline对象。 Crunch 有 3 个Pipeline实现：

-   MRPipeline – 在 Hadoop MapReduce 中执行
-   SparkPipeline—— 作为一系列 Spark 管道执行
-   MemPipeline—— 在客户端内存中执行，对单元测试很有用

通常，我们使用MemPipeline的实例进行开发和测试。稍后我们使用MRPipeline或SparkPipeline的实例进行实际执行。

如果我们需要内存中的管道，我们可以使用静态方法getInstance来获取MemPipeline实例：

```java
Pipeline pipeline = MemPipeline.getInstance();
```

但是现在，让我们创建一个MRPipeline实例 来使用 Hadoop 执行应用程序：

```java
Pipeline pipeline = new MRPipeline(WordCount.class, getConf());
```

## 5.读取输入数据

创建管道对象后，我们要读取输入数据。 Pipeline接口提供了一种方便的方法来从文本文件读取输入， 即 readTextFile(pathName)。

让我们调用此方法来读取输入文本文件：

```java
PCollection<String> lines = pipeline.readTextFile(inputPath);
```

上面的代码将文本文件读取为String的集合。

下一步，让我们编写一个读取输入的测试用例：

```java
@Test
public void givenPipeLine_whenTextFileRead_thenExpectedNumberOfRecordsRead() {
    Pipeline pipeline = MemPipeline.getInstance();
    PCollection<String> lines = pipeline.readTextFile(INPUT_FILE_PATH);

    assertEquals(21, lines.asCollection()
      .getValue()
      .size());
}
```

在此测试中，我们验证在读取文本文件时是否获得了预期的行数。

## 六、数据处理步骤

读取输入数据后，我们需要对其进行处理。 Crunch API 包含许多 DoFn的子类来处理常见的数据处理场景：

-   FilterFn – 根据布尔条件过滤集合的成员
-   MapFn——将每条输入记录映射到恰好一条输出记录
-   CombineFn – 将多个值合并为一个值
-   JoinFn – 执行连接，例如内连接、左外连接、右外连接和全外连接

让我们使用这些类来实现以下数据处理逻辑：

1.  将输入文件中的每一行拆分为单词
2.  删除停用词
3.  计算独特的单词

### 6.1. 将一行文本拆分为单词

首先，让我们创建Tokenizer类来将一行拆分成单词。

我们将扩展 DoFn 类。这个类有一个名为process的抽象方法。此方法处理来自PCollection的输入记录并将输出发送到Emitter。 

我们需要在这个方法中实现拆分逻辑：

```java
public class Tokenizer extends DoFn<String, String> {
    private static final Splitter SPLITTER = Splitter
      .onPattern("s+")
      .omitEmptyStrings();

    @Override
    public void process(String line, Emitter<String> emitter) {
        for (String word : SPLITTER.split(line)) {
            emitter.emit(word);
        }
    }
}

```

在上面的实现中，我们使用了[Guava库中的](https://github.com/google/guava/wiki/StringsExplained#splitter)Splitter类来从一行中提取单词。

接下来，让我们为Tokenizer类编写一个单元测试 ：

```java
@RunWith(MockitoJUnitRunner.class)
public class TokenizerUnitTest {
 
    @Mock
    private Emitter<String> emitter;

    @Test
    public void givenTokenizer_whenLineProcessed_thenOnlyExpectedWordsEmitted() {
        Tokenizer splitter = new Tokenizer();
        splitter.process("  hello  world ", emitter);

        verify(emitter).emit("hello");
        verify(emitter).emit("world");
        verifyNoMoreInteractions(emitter);
    }
}
```

上面的测试验证是否返回了正确的单词。

最后，让我们使用此类拆分从输入文本文件中读取的行。

PCollection接口的parallelDo方法将给定的DoFn应用于所有元素并返回一个新的PCollection。

让我们在 lines 集合上调用此方法并传递Tokenizer的实例：

```java
PCollection<String> words = lines.parallelDo(new Tokenizer(), Writables.strings());

```

结果，我们得到了输入文本文件中的单词列表。我们将在下一步中删除停用词。

### 6.2. 删除停用词

与上一步类似，让我们创建一个StopWordFilter类来过滤停用词。

但是，我们将扩展 FilterFn而不是DoFn。FilterFn有一个名为accept的抽象方法。我们需要在这个方法中实现过滤逻辑：

```java
public class StopWordFilter extends FilterFn<String> {

    // English stop words, borrowed from Lucene.
    private static final Set<String> STOP_WORDS = ImmutableSet
      .copyOf(new String[] { "a", "and", "are", "as", "at", "be", "but", "by",
        "for", "if", "in", "into", "is", "it", "no", "not", "of", "on",
        "or", "s", "such", "t", "that", "the", "their", "then", "there",
        "these", "they", "this", "to", "was", "will", "with" });

    @Override
    public boolean accept(String word) {
        return !STOP_WORDS.contains(word);
    }
}
```

接下来，让我们为StopWordFilter类编写单元测试：

```java
public class StopWordFilterUnitTest {

    @Test
    public void givenFilter_whenStopWordPassed_thenFalseReturned() {
        FilterFn<String> filter = new StopWordFilter();
 
        assertFalse(filter.accept("the"));
        assertFalse(filter.accept("a"));
    }

    @Test
    public void givenFilter_whenNonStopWordPassed_thenTrueReturned() {
        FilterFn<String> filter = new StopWordFilter();
 
        assertTrue(filter.accept("Hello"));
        assertTrue(filter.accept("World"));
    }

    @Test
    public void givenWordCollection_whenFiltered_thenStopWordsRemoved() {
        PCollection<String> words = MemPipeline
          .collectionOf("This", "is", "a", "test", "sentence");
        PCollection<String> noStopWords = words.filter(new StopWordFilter());

        assertEquals(ImmutableList.of("This", "test", "sentence"),
         Lists.newArrayList(noStopWords.materialize()));
    }
}
```

此测试验证过滤逻辑是否正确执行。

最后，让我们使用StopWordFilter来过滤上一步生成的单词列表。 PCollection接口的filter方法将给定的FilterFn应用于所有元素并返回一个新的PCollection。

让我们在单词集合上调用此方法并传递StopWordFilter的实例：

```java
PCollection<String> noStopWords = words.filter(new StopWordFilter());
```

结果，我们得到了过滤后的单词集合。

### 6.3. 计算独特的单词

得到过滤后的词集合后，我们要统计每个词出现的频率。 PCollection接口有许多方法来执行常见的聚合：

-   min – 返回集合中的最小元素
-   max – 返回集合中的最大元素
-   length – 返回集合中元素的数量
-   count – 返回一个PTable，其中包含集合中每个唯一元素的计数

让我们使用count方法来获取唯一单词及其计数：

```java
// The count method applies a series of Crunch primitives and returns
// a map of the unique words in the input PCollection to their counts.
PTable<String, Long> counts = noStopWords.count();
```

## 7.指定输出

作为前面步骤的结果，我们有一个单词及其计数表。我们想将此结果写入文本文件。 Pipeline接口提供了方便的 方法来写入输出：

```java
void write(PCollection<?> collection, Target target);

void write(PCollection<?> collection, Target target,
  Target.WriteMode writeMode);

<T> void writeTextFile(PCollection<T> collection, String pathName);
```

因此，让我们调用 writeTextFile方法：

```java
pipeline.writeTextFile(counts, outputPath);

```

## 8.管理管道执行

到目前为止的所有步骤都只是定义了数据管道。没有读取或处理任何输入。这是因为 Crunch 使用惰性执行模型。

在 Pipeline 接口上调用控制作业计划和执行的方法之前，它不会运行 MapReduce 作业：

-   运行 ——准备执行计划以创建所需的输出，然后同步执行
-   完成- 运行生成输出所需的任何剩余作业，然后清理创建的任何中间数据文件
-   runAsync – 类似于 run 方法，但以非阻塞方式执行

因此，让我们调用done方法将管道作为 MapReduce 作业执行：

```java
PipelineResult result = pipeline.done();

```

上述语句运行 MapReduce 作业以读取输入、处理它们并将结果写入输出目录。

## 9. 将流水线放在一起

到目前为止，我们已经开发并单元测试了读取输入数据、处理它并写入输出文件的逻辑。

接下来，让我们将它们放在一起构建整个数据管道：

```java
public int run(String[] args) throws Exception {
    String inputPath = args[0];
    String outputPath = args[1];

    // Create an object to coordinate pipeline creation and execution.
    Pipeline pipeline = new MRPipeline(WordCount.class, getConf());

    // Reference a given text file as a collection of Strings.
    PCollection<String> lines = pipeline.readTextFile(inputPath);

    // Define a function that splits each line in a PCollection of Strings into
    // a PCollection made up of the individual words in the file.
    // The second argument sets the serialization format.
    PCollection<String> words = lines.parallelDo(new Tokenizer(), Writables.strings());

    // Take the collection of words and remove known stop words.
    PCollection<String> noStopWords = words.filter(new StopWordFilter());

    // The count method applies a series of Crunch primitives and returns
    // a map of the unique words in the input PCollection to their counts.
    PTable<String, Long> counts = noStopWords.count();

    // Instruct the pipeline to write the resulting counts to a text file.
    pipeline.writeTextFile(counts, outputPath);

    // Execute the pipeline as a MapReduce.
    PipelineResult result = pipeline.done();

    return result.succeeded() ? 0 : 1;
}
```

## 10. Hadoop 启动配置

这样数据管道就准备好了。

但是，我们需要代码来启动它。因此，让我们编写启动应用程序的主要方法：

```java
public class WordCount extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new WordCount(), args);
    }
```

ToolRunner.run 从命令行 解析 Hadoop 配置并执行 MapReduce 作业。

## 11.运行应用程序

完整的应用程序现已准备就绪。让我们运行以下命令来构建它：

```plaintext
mvn package

```

作为上述命令的结果，我们在目标目录中获得了打包的应用程序和一个特殊的作业 jar。

让我们使用这个作业 jar 在 Hadoop 上执行应用程序：

```plaintext
hadoop jar target/crunch-1.0-SNAPSHOT-job.jar <input file path> <output directory>
```

应用程序读取输入文件并将结果写入输出文件。输出文件包含独特的单词及其类似于以下内容的计数：

```plaintext
[Add,1]
[Added,1]
[Admiration,1]
[Admitting,1]
[Allowance,1]
```

除了 Hadoop，我们还可以在 IDE 中运行应用程序，作为独立应用程序或作为单元测试。

## 12.总结

在本教程中，我们创建了一个在 MapReduce 上运行的数据处理应用程序。Apache Crunch 使得在Java中编写、测试和执行 MapReduce 管道变得容易。
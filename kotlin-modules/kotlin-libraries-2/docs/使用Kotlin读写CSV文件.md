## 一、简介

CSV 文件是一种多功能信息格式：它是可压缩的、人类可读的，并且在所有主要的数据表应用程序(MS Excel、Google Sheets、LibreOffice)中都受支持。更重要的是，我们可以轻松地将一个 CSV 文件拆分为多个较小的文件，或者将多个 CSV 文件合并为一个。这使得并行处理成为可能，并使数据的自动收集变得极其容易。

Kotlin 如何处理 CSV 文件？

好吧，一方面，由于 Kotlin 专注于函数式编程，因此任何批处理作业都可以轻松编写代码。在本教程中，我们将了解语言本身提供的方法。我们还将涉及促进 CSV 流式处理和批处理的库。

## 2. 使用纯 Kotlin 读写 CSV

CSV 是一种简单的格式。但是，如果我们不能保证我们的数据源是可靠的并且我们的数据主要是数字，事情很快就会变得复杂起来。这就是为什么完全有可能在纯 Kotlin 中编写一个简单的解析器，但使用其中一个库可以帮助我们涵盖许多边缘情况。

让我们找到一个示例 CSV 文件并尝试阅读它：

```plaintext
"Year", "Score", "Title"
1968,  86, "Greetings"
1970,  17, "Bloody Mama"
1970,  73, "Hi, Mom!"
```

此文件包含罗伯特·德尼罗主演的所有电影、它们的收视率以及它们的拍摄年份。假设我们想将该文件解析为一个结构：

```kotlin
data class Movie(
    val year: Year,
    val score: Int,
    val title: String,
)
```

我们可以假设一些事情：第一行将是标题，随后的每一行将具有三个字段，其中前两个将是数字。我们只需要考虑文件末尾的空行以及字段可能包含一些额外空格的可能性：

```kotlin
fun readCsv(inputStream: InputStream): List<Movie> {
    val reader = inputStream.bufferedReader()
    val header = reader.readLine()
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (year, rating, title) = it.split(',', ignoreCase = false, limit = 3)
            Movie(Year.of(year.trim().toInt()), rating.trim().toInt(), title.trim().removeSurrounding("""))
        }.toList()
}
val movies = readCsv(/Open a stream to CSV file/)
```

如果标题列成为第一个，问题就会开始出现。由于一些电影片名包含逗号(“New York, New York”)，我们不能再使用简单的拆分了。但是，在简单的情况下，此解决方案可能就足够了。

没有任何库，编写 CSV 文件实际上要容易得多：

```kotlin
fun OutputStream.writeCsv(movies: List<Movie>) {
    val writer = bufferedWriter()
    writer.write(""""Year", "Score", "Title"""")
    writer.newLine()
    movies.forEach {
        writer.write("${it.year}, ${it.score}, "${it.title}"")
        writer.newLine()
    }
    writer.flush()
}
FileOutputStream("filename.csv").apply { writeCsv(movies) }
```

当然，这个作家是高度专业化的，只为我们的电影班工作。另一方面，任何库都需要一些中间件逻辑来将特定模型转换为库编写者支持的通用类型。

让我们找一个更复杂的数据样本：

```plaintext
"Index", "Item", "Cost", "Tax", "Total"
 1, "Fruit of the Loom Girl's Socks",  7.97, 0.60,  8.57
 2, "Banana Boat Sunscreen, 8 oz",     6.68, 0.50,  7.18
```

这个文件被格式化为额外的人类可读性。然而，对于我们的算法来说，它提出了额外的挑战，因为不仅有前导空格，还有前导制表符。它还在第二列中有一个字符串字段。这样的专栏将对仅使用 Kotlin 语言工具进行解析提出挑战。

## 3.kotlin-csv 库

实际上有用于处理 CSV 的纯 Kotlin 库，特别是[kotlin-csv](https://github.com/doyaaaaaken/kotlin-csv)。然而，它们都还没有达到事实上的标准的地位。此外，kotlin-csv 在处理稍微宽松的 CSV 格式时存在一些问题，就像我们之前引用的那样。具体来说，我们注意到 kotlin-csv 库和应税商品格式存在三个问题：

1.  当使用默认设置运行时，它无法处理带有前导空格的字段中的引号 ( “”：标题“Index”、“Item”、“Cost”、“Tax”、“Total”在逗号后有空格。
2.  使用escapeChar = ''它可以正确运行一段时间，但随后会在每一行上生成一个映射，其中包含前导空格和引号的键。
3.  考虑上面第二个示例的第 2 行，它在项目名称中有一个逗号。当该库在项目名称中发现逗号时失败，假设该行有六列而不是五列。

但是，如果我们遵循严格的 CSV，那么使用 Kotlin CSV 进行解析就很容易了：

```kotlin
fun readStrictCsv(inputStream: InputStream): List<TaxableGood> = csvReader().open(inputStream) {
    readAllWithHeaderAsSequence().map {
        TaxableGood(
            it["Index"]!!.trim().toInt(),
            it["Item"]!!.trim(),
            BigDecimal(it["Cost"]),
            BigDecimal(it["Tax"]),
            BigDecimal(it["Total"])
        )
    }.toList()
}
```

这里重要的是我们不需要读取整个文件——我们正在逐行处理它。有时，这是在内存需求方面需要考虑的重要一点。

## 4. Apache CSV 库

由于对 kotlin-csv 感到失望，我们转向 JVM 世界的经典，比如 Apache Commons 库——Apache CSV。

使用 Apache CSV 库的阅读器非常简单：

```kotlin
fun readCsv(inputStream: InputStream): List<TaxableGood> =
    CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
        setIgnoreSurroundingSpaces(true)
    }.build().parse(inputStream.reader())
        .drop(1) // Dropping the header
        .map {
            TaxableGood(
                index = it[0].toInt(),
                item = it[1],
                cost = BigDecimal(it[2]),
                tax = BigDecimal(it[3]),
                total = BigDecimal(it[4])
            )
        }
```

除了DEFAULT格式外，还有其他变体。RFC4180类似于DEFAULT，但不允许空行。EXCEL允许某些列缺少标题。TDF定义了.tsv格式。这些格式还支持向整个文件添加注解的功能(将在主体之前打印)、定义注解标记以在文档本身中插入(或安全地忽略)注解行，以及其他一些设置。

让我们忽略额外的空格，我们可以将行字段作为数组元素来寻址。

写作更容易：

```kotlin
fun Writer.writeCsv(goods: List<TaxableGood>) {
    CSVFormat.DEFAULT.print(this).apply {
        printRecord("Index", "Item", "Cost", "Tax", "Total")
        goods.forEach { (index, item, cost, tax, total) -> printRecord(index, item, cost, tax, total) }
    }
}
```

然而，由于 CSV 实际上并没有“漂亮”格式的概念，我们无法将数据序列化为看起来与输入数据完全一样，并带有对齐制表符。

## 5. FasterXML 杰克逊 CSV 库

杰克逊图书馆可能不是最轻的。它需要一些设置，映射器实例很重，需要缓存。但众所周知，它非常稳定并且与 JVM 类型集成得很好。

如果我们尝试解析同一个应税商品文件，我们需要先定义映射器和文件模式：

```kotlin
val csvMapper = CsvMapper().apply {
    enable(CsvParser.Feature.TRIM_SPACES)
    enable(CsvParser.Feature.SKIP_EMPTY_LINES)
}

val schema = CsvSchema.builder()
    .addNumberColumn("Index")
    .addColumn("Item")
    .addColumn("Cost")
    .addColumn("Tax")
    .addColumn("Total")
    .build()
```

我们将CsvMapper配置为接受我们与严格格式的偏差：文件末尾的空行以及数据周围的制表符和空格。

之后，我们需要使用JsonProperty注解标记我们的数据类，因为我们的列名称与数据类的字段名称不完全匹配：

```kotlin
data class TaxableGood(
    @field:JsonProperty("Index") val index: Int,
    @field:JsonProperty("Item") val item: String?,
    @field:JsonProperty("Cost") val cost: BigDecimal?,
    @field:JsonProperty("Tax") val tax: BigDecimal?,
    @field:JsonProperty("Total") val total: BigDecimal?
) {
    constructor() : this(0, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
}
```

我们还需要Jackson 解析器的零参数构造函数。我们可以像示例中那样手动创建它，或者使用[无参数编译器插件](https://kotlinlang.org/docs/no-arg-plugin.html)。

然后我们可以打开流并读取数据：

```kotlin
fun readCsv(inputStream: InputStream): List<TaxableGood> =
    csvMapper.readerFor(TaxableGood::class.java)
        .with(schema.withSkipFirstDataRow(true))
        .readValues<TaxableGood>(inputStream)
        .readAll()
```

请注意我们如何使用withSkipFirstDataRow(true)跳过标头。写作也很简单：

```kotlin
fun OutputStream.writeCsv(goods: List<TaxableGood>) {
    csvMapper.writer().with(schema.withHeader()).writeValues(this).writeAll(goods)
}
```

多亏了模式，作者才知道列的数量和顺序。

Jackson 库的优点是我们可以将 CSV 行直接解析为数据类对象，这很容易处理。

## 六，总结

在本教程中，我们使用各种方法解析和编写 CSV。我们可以使用仅包含标准库的纯 Kotlin 来编写一个在我们的软件生命周期内不会发生太大变化的解析器。这样的解析器将在很大程度上依赖于数据源，并且需要付出巨大的努力才能使其通用和通用。

因此，Apache CSV 或 Jackson CSV 通常是更好的解决方案，为数据中的轻微不规则提供更好的支持。Apache CSV 是 Apache Commons 系列的一部分，因此非常稳定。Jackson 在标记化的行和数据类之间提供了一个很好的桥梁。在配置方面，Jackson 的要求稍微高一些，需要事先创建一个解析器和一个模式，而 Apache CSV 只需一次调用就可以了。

kotlin-csv 库可能适用于大多数 CSV 文件，但它需要严格遵守标准。
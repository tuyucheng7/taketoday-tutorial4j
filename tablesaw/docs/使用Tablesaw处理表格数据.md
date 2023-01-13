## 1. 简介

在本文中，我们将学习使用 Tablesaw 库来处理表格数据。首先，我们将导入一些数据。然后，我们将通过处理数据获得一些见解。

我们将使用[鳄梨价格](https://www.kaggle.com/datasets/neuromusic/avocado-prices)数据集。简而言之，它包含美国多个市场的牛油果价格和销量的历史数据。

## 2.在Tablesaw中导入数据

首先，我们需要导入数据。Tablesaw 支持多种格式，包括我们的数据集格式 CSV。因此，让我们从从 CSV 文件加载数据集开始：

```java
CsvReadOptions csvReadOptions =
    CsvReadOptions.builder(file)
        .separator(',')
        .header(true)
        .dateFormat(formatter)
        .build();
table = Table.read().usingOptions(csvReadOptions);
```

上面，我们通过将File对象传递给[构建器来创建](https://www.baeldung.com/creational-design-patterns#builder)CsvReadOptions类。然后，我们将描述如何通过正确配置选项对象来读取 CSV 文件。

首先，我们使用separator()方法设置列分隔符。其次，我们读取文件的第一行作为标题。第三，我们提供了一个DateTimeFormatter来正确解析日期和时间。最后，我们使用新创建的CsvReadOptions来读取表数据。

### 2.1. 验证导入的数据

让我们使用structure()方法来检查表的设计。它返回另一个包含列名、索引和数据类型的表：

```markdown
         Structure of avocado.csv         
 Index  |  Column Name   |  Column Type  |
------------------------------------------
     0  |            C0  |      INTEGER  |
     1  |          Date  |   LOCAL_DATE  |
     2  |  AveragePrice  |       DOUBLE  |
     3  |  Total Volume  |       DOUBLE  |
    ... |       ...      |       ...     |
```

接下来，让我们使用shape()方法检查它的形状：

```java
assertThat(table.shape()).isEqualTo("avocado.csv: 18249 rows X 14 cols");
```

此方法返回一个字符串，文件名后跟行数和列数。我们的数据集总共包含 18249 行数据和 14 列。

## 3. Tablesaw 内部的数据表示

Tablesaw 主要处理表格和列，它们构成了所谓的数据框的基础。简而言之，表是一组列，其中每一列都有固定的类型。表中的一行是一组值，每个值都分配给它的匹配列。

Tablesaw 支持多种[列类型](https://www.javadoc.io/static/tech.tablesaw/tablesaw-core/0.43.1/tech/tablesaw/api/ColumnType.html)。除了扩展Java中的基本类型之外，它还提供文本和时间列。

### 3.1. 文本类型

在 Tablesaw 中，有两种文本类型：TextColumn和StringColumn。第一种是通用类型，可以按原样保存任何文本。另一方面，StringColumn在存储之前将值编码为类似字典的数据结构。这样可以有效地保存数据，而不是在列内重复值。

例如，在 avocado 数据集中，区域和类型列的类型为StringColumn。它们在列向量中的重复值被更有效地存储并指向文本的相同实例：

```java
StringColumn type = table.stringColumn("type");
List<String> conventional = type.where(type.isEqualTo("conventional")).asList().stream()
    .limit(2)
    .toList();
assertThat(conventional.get(0)).isSameAs(conventional.get(1));
```

### 3.2. 时间类型

Tablesaw 中有四种时间类型可用。它们映射到等效的Java对象：DateColumn、DateTimeColumn、TimeColumn和InstantColumn。如上所示，我们可以在导入时配置如何解析这些值。

## 4. 使用列

接下来，让我们看看如何使用导入的数据并从中提取见解。例如，在 Tablesaw 中，我们可以转换单个列或处理整个表格。

### 4.1. 创建新列

让我们通过调用在每种类型的可用列上定义的静态方法.create()来创建一个新列。例如，要创建一个名为time的TimeColumn，我们这样写：

```lua
TimeColumn time = TimeColumn.create("Time");
```

然后可以使用.addColumns()方法将此列添加到表中：

```java
Table table = Table.create("test");
table.addColumns(time);
assertThat(table.columnNames()).contains("time");
```

### 4.2. 添加或修改列数据

让我们使用.append()方法将数据添加到列的末尾：

```java
DoubleColumn averagePrice = table.doubleColumn("AveragePrice");
averagePrice.append(1.123);
assertThat(averagePrice.get(averagePrice.size() - 1)).isEqualTo(1.123);
```

对于表，我们必须为每一列提供一个值，以确保所有列至少有一个值。否则，在创建具有不同大小的列的表时，它将抛出IllegalArgumentException ：

```java
DoubleColumn averagePrice2 = table.doubleColumn("AveragePrice").copy();
averagePrice2.setName("AveragePrice2");
averagePrice2.append(1.123);
assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> table.addColumns(averagePrice2));
```

我们使用.set()方法来更改列向量中的特定值。要使用它，我们必须知道我们要更改的值的索引：

```apache
stringColumn.set(2, "Baeldung");
```

从列中删除数据可能会出现问题，尤其是在表的情况下。因此，Tablesaw 不允许从列 vector 中删除值。相反，让我们使用.setMissing()将我们希望删除的值标记为丢失，并将每个值的索引传递给此方法：

```java
DoubleColumn averagePrice = table.doubleColumn("AveragePrice").setMissing(0);
assertThat(averagePrice.get(0)).isNull();
```

因此，它不会从向量中删除值持有者，而是将其设置为null。因此，向量的大小保持不变。

## 5. 数据排序

接下来，让我们对之前导入的数据进行排序。首先，我们将根据一组列对表格行进行排序。为此，我们使用.sortAscending()和.sortDescending()方法，它们接受列名。让我们排序以获取我们数据集中存在的最旧和最近的日期：

```sas
Table ascendingDateSortedTable = table.sortAscendingOn("Date");
assertThat(ascendingDateSortedTable.dateColumn("Date").get(0)).isEqualTo(LocalDate.parse("2015-01-04"));
Table descendingDateSortedTable = table.sortDescendingOn("Date");
assertThat(descendingDateSortedTable.dateColumn("Date").get(0)).isEqualTo(LocalDate.parse("2018-03-25"));
```

然而，这些方法的局限性很大。例如，我们不能混合升序和降序排序。为了解决这些限制，我们使用.sortOn()方法。它接受一组列名并默认对它们进行排序。要按降序对特定列进行排序，我们在列名前加上减号“-”。例如，让我们按年份和平均价格最高的降序对数据进行排序：

```apache
Table ascendingYearAndAveragePriceSortedTable = table.sortOn("year", "-AveragePrice");
assertThat(ascendingYearAndAveragePriceSortedTable.intColumn("year").get(0)).isEqualTo(2015);
assertThat(ascendingYearAndAveragePriceSortedTable.numberColumn("AveragePrice").get(0)).isEqualTo(2.79);
```

这些方法并不适合所有用例。对于这种情况，Tablesaw 接受Comparator<VRow>的自定义实现用于.sortOn()方法。

## 6.过滤数据

过滤器允许我们从原始表中获取数据的子集。过滤一个表会返回另一个表，我们使用.where( )和 .dropWhere()方法应用过滤器。第一个方法将返回符合我们指定条件的值或行。相反，第二种方法将丢弃它们。

要指定过滤条件，我们首先需要了解Selections。

### 6.1. 精选

Selection是一个逻辑位图。换句话说，它是一个包含布尔值的数组，用于屏蔽列向量上的值。例如，将选择应用于一列将产生另一列包含过滤后的值——例如，删除给定索引的掩码为 0 的值。此外，选择向量将与其原始列大小相同。

让我们通过获取 2017 年平均价格仅高于 2 美元的数据表来付诸实践：

```java
DateColumn dateTable = table.dateColumn("Date");
DoubleColumn averagePrice = table.doubleColumn("AveragePrice");
Selection selection = dateTable.isInYear(2017).and(averagePrice.isGreaterThan(2D));
Table table2017 = table.where(selection);
assertThat(table2017.intColumn("year")).containsOnly(2017);
assertThat(table2017.doubleColumn("AveragePrice")).allMatch(avrgPrice -> avrgPrice > 2D);
```

上面，我们使用了在 DateColumn 上定义的方法.isInYear ()和 在DoubleColumn上定义的.isGreaterThan ()。我们使用.and()方法将它们组合在类似查询的语言中。Tablesaw 提供了许多这样的内置辅助方法。因此，我们很少需要自己为简单任务构建自定义选择。对于复杂的任务，我们使用.and()、.andNot()、or()和其他[列过滤器](https://jtablesaw.github.io/tablesaw/userguide/filters#:~:text=Current list of provided column filters)将它们组合起来。

或者，我们通过创建谓词并将其传递给每列可用的.eval()方法来编写自定义过滤器。此方法返回一个我们用来过滤表或列的Selection对象。

## 7.总结数据

处理数据后，我们想从中提取一些见解。我们使用 . summarize()方法聚合数据以了解它。例如，从鳄梨数据集中提取平均价格的最小值、最大值、平均值和标准差：

```java
Table summary = table.summarize("AveragePrice", max, min, mean, stdDev).by("year");
System.out.println(summary.print());
```

首先，我们将要聚合的列名和AggregateFunction列表传递给.summarize()方法。接下来，我们使用 . 对每年的结果进行分组。通过()方法。最后，我们在标准输出上打印结果：

```markdown
                                              avocado.csv summary                                               
 year  |  Mean [AveragePrice]  |  Max [AveragePrice]  |  Min [AveragePrice]  |  Std. Deviation [AveragePrice]  |
----------------------------------------------------------------------------------------------------------------
 2015  |    1.375590382902939  |                2.79  |                0.49  |            0.37559477067238917  |
 2016  |   1.3386396011396013  |                3.25  |                0.51  |            0.39370799476072077  |
 2017  |   1.5151275777700104  |                3.17  |                0.44  |             0.4329056466203253  |
 2018  |   1.3475308641975308  |                 2.3  |                0.56  |             0.3058577391135024  |

```

Tablesaw为大多数常见操作提供[AggregateFunction 。](https://www.javadoc.io/static/tech.tablesaw/tablesaw-core/0.43.1/tech/tablesaw/aggregate/AggregateFunctions.html)或者，我们可以实现自定义AggregateFunction对象，但由于这超出了本文的范围，我们将保持简单。

## 8.保存数据

到目前为止，我们一直在将数据打印到标准输出。在运行中验证我们的结果时打印到控制台很好，但是我们需要将数据保存到文件中以使结果可以被其他人重用。所以，让我们直接在表上使用.write()方法：

```java
summary.write().csv("summary.csv");
```

上面，我们使用.csv()方法将数据保存为 CSV 格式。目前，Tablesaw 仅支持 CSV 格式和固定宽度格式，类似于.print()方法在控制台上显示的内容。此外，我们使用CsvWriterOptions自定义数据的 CSV 输出。

## 9.总结

在本文中，我们探讨了如何使用 Tablesaw 库处理表格数据。

首先，我们解释了如何导入数据。然后，我们描述了数据的内部表示以及如何使用它。接下来，我们探讨了修改导入表的结构并创建过滤器以在聚合之前提取必要的数据。最后，我们将其保存为 CSV 文件。
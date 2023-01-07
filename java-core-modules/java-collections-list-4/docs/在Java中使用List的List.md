## 1. 概述

[List](https://www.baeldung.com/tag/java-list/)是Java中非常常用的数据结构。有时候，我们可能会因为某些需求需要一个嵌套的List结构，比如List<List<T>>。

在本教程中，我们将仔细研究这个“List of Lists”数据结构并探索一些日常操作。

## 2.列表数组与列表列表

我们可以将“List of Lists”数据结构视为一个二维矩阵。因此，如果我们想要对多个List<T>对象进行分组，我们有两个选择：

-   基于数组：List<T>[]
-   基于列表：List<List<T>>

接下来，让我们看看什么时候选择哪个。

[数组](https://www.baeldung.com/java-arrays-guide)对于在O(1)时间内运行的“ get ”和“ set ”操作速度很快。但是，由于数组的长度是固定的，因此调整数组大小以插入或删除元素的代价很高。

另一方面，List在[插入和删除操作](https://www.baeldung.com/java-add-element-to-array-vs-list)上更加灵活，运行时间为O (1)。一般来说，List在“get/set”操作上比Array慢。但是一些List实现，例如[ArrayList](https://www.baeldung.com/java-arraylist)，在内部是基于数组的。因此，通常情况下， Array和ArrayList在“get/set”操作上的性能差异并不明显。

因此，在大多数情况下，我们会选择List<List<T>>数据结构以获得更好的灵活性。

当然，如果我们正在处理性能关键的应用程序，并且我们不更改第一个维度的大小——例如，我们从不添加或删除内部列表 ——我们可以考虑使用List<T>[]结构体。

我们将在本教程中主要讨论List<List<T>> 。

## 3. 列表List的常用操作

现在，让我们探讨一下List<List<T>>上的一些日常操作。

为简单起见，我们将操作List<List<String>>对象并在单元测试方法中验证结果。

此外，为了直接看到变化，我们还创建一个方法来打印List的List的内容：

```java
private void printListOfLists(List<List<String>> listOfLists) {
    System.out.println("n           List of Lists          ");
    System.out.println("-------------------------------------");
    listOfLists.forEach(innerList -> {
        String line = String.join(", ", innerList);
        System.out.println(line);
    });
}

```

接下来，我们先初始化一个列表列表。

### 3.1. 初始化列表列表

我们将数据从 CSV 文件导入到List<List<T>>对象中。我们先看一下 CSV 文件的内容：

```bash
Linux, Microsoft Windows, Mac OS, Delete Me
Kotlin, Delete Me, Java, Python
Delete Me, Mercurial, Git, Subversion
```

假设我们将文件命名为 example.csv并将其放在resources/listoflists目录下。

接下来，让我们创建一个方法来[读取文件](https://www.baeldung.com/reading-file-in-java#1-reading-a-small-file)并将数据存储在List<List<T>>对象中：

```java
private List<List<String>> getListOfListsFromCsv() throws URISyntaxException, IOException {
    List<String> lines = Files.readAllLines(Paths.get(getClass().getResource("/listoflists/example.csv")
        .toURI()));

    List<List<String>> listOfLists = new ArrayList<>();
    lines.forEach(line -> {
        List<String> innerList = new ArrayList<>(Arrays.asList(line.split(", ")));
        listOfLists.add(innerList);
    });
    return listOfLists;
}

```

在getListOfListsFromCsv方法中，我们首先将 CSV 文件中的所有行读取到List<String>对象中。然后，我们遍历 List行并将每一行 ( String ) 转换为List<String>。

最后，我们将每个转换后的List<String>对象添加到listOfLists中。因此，我们已经初始化了一个列表列表。

好奇的眼睛可能已经发现我们将Arrays.asList(..)包装在一个新的ArrayList<>()中。这是因为[Arrays.asList](https://www.baeldung.com/java-arrays-aslist-vs-new-arraylist)方法将创建一个不可变的List。但是，稍后我们将对内部列表进行一些更改。因此，我们将它包装在一个新的ArrayList对象中。

如果正确创建列表对象列表，我们应该在外部列表中包含三个元素，即 CSV 文件中的行数。

此外，每个元素都是一个内部列表，每个元素都应包含四个元素。接下来，让我们编写一个单元测试方法来验证这一点。此外，我们将打印列表的初始化列表：

```java
List<List<String>> listOfLists = getListOfListsFromCsv();

assertThat(listOfLists).hasSize(3);
assertThat(listOfLists.stream()
  .map(List::size)
  .collect(Collectors.toSet())).hasSize(1)
  .containsExactly(4);

printListOfLists(listOfLists);

```

如果我们执行该方法，则测试通过并产生输出：

```bash
           List of Lists           
-------------------------------------
Linux, Microsoft Windows, Mac OS, Delete Me
Kotlin, Delete Me, Java, Python
Delete Me, Mercurial, Git, Subversion
```

接下来，让我们对listOfLists对象进行一些更改。但是，首先，让我们看看如何将更改应用于外部列表。

### 3.2. 将更改应用于外部列表

如果我们关注外部列表，我们可以先忽略内部列表。换句话说，我们可以将List<List<String>>视为常规List<T>。

因此，更改常规List对象并不是一个挑战。我们可以调用List的方法，例如add 和remove来操作数据。

接下来，让我们向外部列表添加一个新元素：

```java
List<List<String>> listOfLists = getListOfListsFromCsv();
List<String> newList = new ArrayList<>(Arrays.asList("Slack", "Zoom", "Microsoft Teams", "Telegram"));
listOfLists.add(2, newList);

assertThat(listOfLists).hasSize(4);
assertThat(listOfLists.get(2)).containsExactly("Slack", "Zoom", "Microsoft Teams", "Telegram");

printListOfLists(listOfLists);

```

外部列表的元素实际上是一个List<String>对象。如上面的方法所示，我们创建了一个流行的在线通信实用程序列表。然后，我们将新列表添加到listOfLists中 index=2的位置。

同样，在断言之后，我们打印 listOfLists的内容：

```bash
           List of Lists           
-------------------------------------
Linux, Microsoft Windows, Mac OS, Delete Me
Kotlin, Delete Me, Java, Python
Slack, Zoom, Microsoft Teams, Telegram
Delete Me, Mercurial, Git, Subversion

```

### 3.3. 将更改应用到内部列表

最后，让我们看看如何操作内部列表。

由于listOfList是一个嵌套的List结构，我们需要首先导航到我们要更改的内部列表对象。如果我们确切知道索引，我们可以简单地使用 get方法：

```java
List<String> innerList = listOfLists.get(x);
// innerList.add(), remove() ....
```

但是，如果我们想对所有内部列表应用更改，我们可以通过循环或[Stream API](https://www.baeldung.com/java-8-streams)传递列表对象的列表。

接下来，让我们看一个从listOfLists对象中删除所有“ Delete Me ”条目的 示例：

```java
List<List<String>> listOfLists = getListOfListsFromCsv();

listOfLists.forEach(innerList -> innerList.remove("Delete Me"));

assertThat(listOfLists.stream()
    .map(List::size)
    .collect(Collectors.toSet())).hasSize(1)
    .containsExactly(3);

printListOfLists(listOfLists);

```

正如我们在上面的方法中看到的，我们通过listOfLists.forEach{ … }迭代每个内部列表，并使用 lambda 表达式从innerList中删除“ Delete Me ”条目。

如果我们执行测试，它会通过并产生以下输出：

```bash
           List of Lists           
-------------------------------------
Linux, Microsoft Windows, Mac OS
Kotlin, Java, Python
Mercurial, Git, Subversion

```

## 4。总结

在本文中，我们讨论了列表数据结构的列表。

此外，我们还通过示例解决了列表列表的常见操作。
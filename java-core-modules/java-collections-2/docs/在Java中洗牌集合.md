## 1. 概述

在这篇简短的文章中，我们将了解如何在Java中对集合进行洗牌。Java 有一个内置的方法来洗牌List对象——我们也将把它用于其他集合。

## 2. 洗牌列表

我们将使用java.util.Collections.shuffle方法，它将一个List作为输入并就地对其进行洗牌。就地，我们的意思是它打乱与输入相同的列表，而不是创建一个包含打乱元素的新列表。

让我们看一个简单的例子，展示如何洗牌List：

```java
List<String> students = Arrays.asList("Foo", "Bar", "Baz", "Qux");
Collections.shuffle(students);
```

java.util.Collections.shuffle的第二个版本也接受自定义[随机](https://www.baeldung.com/cs/randomness)源作为输入。如果我们对我们的应用程序有这样的要求，这可以用来使洗牌成为一个确定性的过程。

让我们使用第二个变体在两个列表上实现相同的洗牌：

```java
List<String> students_1 = Arrays.asList("Foo", "Bar", "Baz", "Qux");
List<String> students_2 = Arrays.asList("Foo", "Bar", "Baz", "Qux");

int seedValue = 10;

Collections.shuffle(students_1, new Random(seedValue));
Collections.shuffle(students_2, new Random(seedValue));

assertThat(students_1).isEqualTo(students_2);
```

当使用相同的随机源(从相同的种子值初始化)时，生成的随机数序列对于两次随机播放都是相同的。因此，在混洗之后，两个列表将包含完全相同顺序的元素。

## 3. 无序集合的元素混排

我们可能还想打乱其他集合，例如Set、Map或Queue，但是所有这些集合都是无序的——它们不维护任何特定的顺序。

某些实现，例如LinkedHashMap或带有比较器的Set – 确实保持固定顺序，因此我们也不能打乱它们。

但是，我们仍然可以随机访问它们的元素，方法是先将它们转换为List ，然后对这个List进行洗牌。

让我们看一个洗牌Map元素的快速示例：

```java
Map<Integer, String> studentsById = new HashMap<>();
studentsById.put(1, "Foo");
studentsById.put(2, "Bar");
studentsById.put(3, "Baz");
studentsById.put(4, "Qux");

List<Map.Entry<Integer, String>> shuffledStudentEntries
 = new ArrayList<>(studentsById.entrySet());
Collections.shuffle(shuffledStudentEntries);

List<String> shuffledStudents = shuffledStudentEntries.stream()
  .map(Map.Entry::getValue)
  .collect(Collectors.toList());
```

同样，我们可以洗牌Set的元素：

```java
Set<String> students = new HashSet<>(
  Arrays.asList("Foo", "Bar", "Baz", "Qux"));
List<String> studentList = new ArrayList<>(students);
Collections.shuffle(studentList);
```

## 4。总结

在本快速教程中，我们了解了如何使用java.util.Collections.shuffle在Java中随机播放各种集合。

这自然直接适用于List，我们也可以间接利用它来随机化其他集合中元素的顺序。我们还可以通过提供自定义随机源并使其具有确定性来控制混洗过程。
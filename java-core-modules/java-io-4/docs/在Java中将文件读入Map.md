## 1. 概述

我们知道[Map](https://www.baeldung.com/tag/java-map/)在Java中保存键值对。有时，我们可能希望加载文本文件的内容并将其转换为JavaMap。

在本快速教程中，让我们探索如何实现它。

## 二、问题简介

由于Map存储键值条目，如果我们想将文件内容导入JavaMap 对象，文件应遵循特定格式。

一个示例文件可以快速解释它：

```bash
$ cat theLordOfRings.txt
title:The Lord of the Rings: The Return of the King
director:Peter Jackson
actor:Sean Astin
actor:Ian McKellen
Gandalf and Aragorn lead the World of Men against Sauron's
army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.
```

正如我们在theLordOfRings.txt文件中看到的那样，如果我们将冒号字符视为分隔符，则大多数行都遵循“ KEY:VALUE ”模式，例如“ director:Peter Jackson ”。

因此，我们可以读取每一行，解析键和值，并将它们放在一个Map对象中。

但是，我们需要注意一些特殊情况：

-   包含分隔符的值 – 不应截断值。例如，第一行“标题：指环王：归来…… ”
-   Duplicated Keys——三种策略：覆盖现有的，丢弃后者，并根据需要将值聚合到一个列表中。例如，我们在文件中有两个“ actor ”键。
-   不遵循“ KEY:VALUE ”模式的行——应该跳过该行。例如，查看文件中的最后两行。

接下来，让我们读取此文件并将其存储在JavaMap对象中。

## 3. DupKeyOption枚举

正如我们所讨论的，对于重复键的情况，我们有三种选择：覆盖、丢弃和聚合。

此外，如果我们使用覆盖或丢弃选项，我们将返回一个类型为Map<String, String>的Map。但是，如果我们想聚合重复键的值，我们将得到Map<String, List<String>> 的结果。

那么，让我们首先探讨覆盖和丢弃场景。最后，我们将在一个独立的部分中讨论聚合选项。

为了使我们的解决方案更灵活，让我们创建一个[枚举](https://www.baeldung.com/a-guide-to-java-enums)类，以便我们可以将选项作为参数传递给我们的解决方案方法：

```java
enum DupKeyOption {
    OVERWRITE, DISCARD
}

```

## 4. 使用BufferedReader和FileReader类

我们可以结合[BufferedReader](https://www.baeldung.com/java-buffered-reader)和[FileReader](https://www.baeldung.com/java-filereader)来逐行读取文件中的内容。

### 4.1. 创建byBufferedReader方法

让我们创建一个基于BufferedReader和FileReader的方法：

```java
public static Map<String, String> byBufferedReader(String filePath, DupKeyOption dupKeyOption) {
    HashMap<String, String> map = new HashMap<>();
    String line;
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        while ((line = reader.readLine()) != null) {
            String[] keyValuePair = line.split(":", 2);
            if (keyValuePair.length > 1) {
                String key = keyValuePair[0];
                String value = keyValuePair[1];
                if (DupKeyOption.OVERWRITE == dupKeyOption) {
                    map.put(key, value);
                } else if (DupKeyOption.DISCARD == dupKeyOption) {
                    map.putIfAbsent(key, value);
                }
            } else {
                System.out.println("No Key:Value found in line, ignoring: " + line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return map;
}

```

byBufferedReader方法接受两个参数：输入文件路径和决定如何处理具有重复键的条目的dupKeyOption对象。

如上面的代码所示，我们定义了一个BufferedReader对象来从给定的输入文件中读取行。然后，我们在while循环中解析和处理每一行。让我们来看看它是如何工作的：

-   我们创建一个BufferedReader对象并使用[try-with-resources](https://www.baeldung.com/java-try-with-resources)来确保 读取器对象自动关闭
-   我们使用带有 limit 参数的[split](https://www.baeldung.com/string/split)方法，如果值部分包含冒号字符，则保持原样
-   然后if检查过滤掉与“ KEY:VALUE ”模式不匹配的行
-   如果有重复键，如果我们想采取“覆盖”策略，我们可以简单地调用map.put(key, value)
-   否则，调用[putIfAbsent](https://www.baeldung.com/java-map-computeifabsent)方法允许我们忽略后面带有重复键的条目

接下来，让我们测试该方法是否按预期工作。

### 4.2. 测试解决方案

在我们编写相应的测试方法之前，让我们初始化两个包含预期条目的[映射对象：](https://www.baeldung.com/java-initialize-hashmap)

```java
private static final Map<String, String> EXPECTED_MAP_DISCARD = Stream.of(new String[][]{
    {"title", "The Lord of the Rings: The Return of the King"},
    {"director", "Peter Jackson"},
    {"actor", "Sean Astin"}
  }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

private static final Map<String, String> EXPECTED_MAP_OVERWRITE = Stream.of(new String[][]{
...
    {"actor", "Ian McKellen"}
  }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
```

如我们所见，我们已经初始化了两个Map对象来帮助测试断言。一种是针对我们丢弃重复键的情况，另一种是针对我们覆盖它们的情况。

接下来，让我们测试一下我们的方法，看看我们是否可以获得预期的Map对象：

```java
@Test
public void givenInputFile_whenInvokeByBufferedReader_shouldGetExpectedMap() {
    Map<String, String> mapOverwrite = FileToHashMap.byBufferedReader(filePath, FileToHashMap.DupKeyOption.OVERWRITE);
    assertThat(mapOverwrite).isEqualTo(EXPECTED_MAP_OVERWRITE);

    Map<String, String> mapDiscard = FileToHashMap.byBufferedReader(filePath, FileToHashMap.DupKeyOption.DISCARD);
    assertThat(mapDiscard).isEqualTo(EXPECTED_MAP_DISCARD);
}

```

如果我们试一试，测试就会通过。所以，我们已经解决了这个问题。

## 5. 使用 Java流

[Stream](https://www.baeldung.com/java-streams)从Java8 开始就有了。另外， Files.lines方法可以方便地返回一个包含文件中所有行的Stream对象。

现在，让我们使用Stream创建一个 mothed来解决这个问题：

```java
public static Map<String, String> byStream(String filePath, DupKeyOption dupKeyOption) {
    Map<String, String> map = new HashMap<>();
    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
        lines.filter(line -> line.contains(":"))
            .forEach(line -> {
                String[] keyValuePair = line.split(":", 2);
                String key = keyValuePair[0];
                String value = keyValuePair[1];
                if (DupKeyOption.OVERWRITE == dupKeyOption) {
                    map.put(key, value);
                } else if (DupKeyOption.DISCARD == dupKeyOption) {
                    map.putIfAbsent(key, value);
                }
            });
    } catch (IOException e) {
        e.printStackTrace();
    }
    return map;
}

```

如上面的代码所示，主要逻辑与我们的byBufferedReader方法非常相似。让我们快速通过：

-   我们仍在Stream对象上使用 try-with-resources，因为Stream对象包含对打开文件的引用。我们应该通过关闭流来关闭文件。
-   过滤器方法会跳过所有不遵循“ KEY:VALUE ”模式的行。
-   forEach方法与byBufferedReader解决方案中的while块几乎相同 。

最后我们来测试下byStream方案：

```java
@Test
public void givenInputFile_whenInvokeByStream_shouldGetExpectedMap() {
    Map<String, String> mapOverwrite = FileToHashMap.byStream(filePath, FileToHashMap.DupKeyOption.OVERWRITE);
    assertThat(mapOverwrite).isEqualTo(EXPECTED_MAP_OVERWRITE);

    Map<String, String> mapDiscard = FileToHashMap.byStream(filePath, FileToHashMap.DupKeyOption.DISCARD);
    assertThat(mapDiscard).isEqualTo(EXPECTED_MAP_DISCARD);
}

```

当我们执行测试时，它也通过了。

## 6. 按键聚合值

至此，我们已经看到了覆盖和丢弃场景的解决方案。但是，正如我们所讨论的，如果需要，我们也可以通过键聚合值。因此，最后，我们将得到一个Map<String, List<String>>类型的Map对象。现在，让我们构建一个方法来实现这个需求：

```java
public static Map<String, List<String>> aggregateByKeys(String filePath) {
    Map<String, List<String>> map = new HashMap<>();
    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
        lines.filter(line -> line.contains(":"))
          .forEach(line -> {
              String[] keyValuePair = line.split(":", 2);
              String key = keyValuePair[0];
              String value = keyValuePair[1];
              if (map.containsKey(key)) {
                  map.get(key).add(value);
              } else {
                  map.put(key, Stream.of(value).collect(Collectors.toList()));
              }
          });
    } catch (IOException e) {
        e.printStackTrace();
    }
    return map;
}

```

我们使用Stream方法读取输入文件中的所有行。实现非常简单。一旦我们解析了输入行中的键和值，我们就会检查该键是否已存在于结果映射对象中。如果确实存在，我们将该值附加到现有列表中。否则，我们将包含当前值的[列表](https://www.baeldung.com/java-init-list-one-line#create-from-a-stream-java-8)[初始化](https://www.baeldung.com/java-init-list-one-line#create-from-a-stream-java-8)为单个元素：Stream.of(value).collect(Collectors.toList())。 

值得一提的是，我们不应该使用Collections.singletonList(value)或List.of(value)来初始化List。这是因为[Collections.singletonList](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#singletonList(T))和[List.of](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/List.html#of(E)) (Java 9+) 方法都返回一个不可变的L ist。也就是说，如果同一个键再次出现，我们就不能将该值追加到列表中。

接下来，让我们测试我们的方法，看看它是否能完成工作。像往常一样，我们首先创建预期结果：

```java
private static final Map<String, List<String>> EXPECTED_MAP_AGGREGATE = Stream.of(new String[][]{
      {"title", "The Lord of the Rings: The Return of the King"},
      {"director", "Peter Jackson"},
      {"actor", "Sean Astin", "Ian McKellen"}
  }).collect(Collectors.toMap(arr -> arr[0], arr -> Arrays.asList(Arrays.copyOfRange(arr, 1, arr.length))));
```

然后，测试方法本身非常简单：

```java
@Test
public void givenInputFile_whenInvokeAggregateByKeys_shouldGetExpectedMap() {
    Map<String, List<String>> mapAgg = FileToHashMap.aggregateByKeys(filePath);
    assertThat(mapAgg).isEqualTo(EXPECTED_MAP_AGGREGATE);
}
```

如果我们试一试，测试就会通过。这意味着我们的解决方案按预期工作。

## 七、总结

在本文中，我们学习了两种从文本文件中读取内容并将其保存在JavaMap对象中的方法：使用BufferedReader类和使用Stream。

此外，我们已经解决了实施三种策略来处理重复键的问题：覆盖、丢弃和聚合。
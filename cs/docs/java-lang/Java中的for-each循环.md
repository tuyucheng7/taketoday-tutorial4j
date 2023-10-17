## 1. 概述

在本教程中，我们将讨论Java中的for-each循环及其语法、工作原理和代码示例。最后，我们将了解它的优点和缺点。

## 2. 简单的for循环

**Java中的简单[for循环](https://www.baeldung.com/java-for-loop)本质上包含三个部分-初始化、布尔条件和步骤**：

```java
for (initialization; boolean-condition; step) {
    statement;
}
```

它以循环变量的初始化开始，然后是布尔表达式。如果条件为true，它会执行循环中的语句并递增/递减循环变量。否则，它终止循环。

这种模式使其稍微复杂且难以阅读。此外，**如果我们没有正确编写条件，总是有可能进入死循环**。

## 3. for-each循环

for-each循环是在Java 5中引入的。**我们也称它为增强for循环**。

这是一种专门用于遍历数组或集合的替代遍历技术。值得注意的是，它也使用了for关键字。但是，我们没有使用循环计数器变量，而是分配了一个与数组或集合类型相同的变量。

**for-each的名称表示依次遍历数组或集合的每个元素**。

### 3.1 语法

for-each循环由循环变量声明和冒号(:)组成，冒号(:)后跟数组或集合的名称：

```java
for (data_type var_name : array | collection) {
    // code
}
```

### 3.2 工作方式

对于每次迭代，for-each循环获取集合中的每个元素并将其存储在循环变量中。**因此，它为数组或集合的每个元素执行编写在循环体中的代码**。

最重要的是，遍历一直持续到数组或集合的最后一个元素。

### 3.3 示例

让我们看一个使用for-each循环遍历数组的示例：

```java
int numbers[] = { 1, 2, 3, 4, 5 };

for (int number : numbers) {
    System.out.print(number + " ");
}
```

在这里，for-each循环逐一遍历数组numbers的每个元素，直到最后。因此，**无需使用索引访问数组元素**。

现在，让我们看一些使用for-each循环遍历各种集合的示例。

让我们从一个列表开始：

```java
String[] wordsArray = { "Java ", "is ", "great!" };
List<String> wordsList = Arrays.asList(wordsArray);

for (String word : wordsList) {
    System.out.print(word + " ");
}
```

同样，我们可以遍历Set的所有元素：

```java
Set<String> wordsSet = new HashSet();
wordsSet.addAll(wordsList);

for (String word : wordsSet) {
    System.out.print(word + " ");
}
```

此外，我们还可以使用for-each循环遍历Map<K, V\>：

```java
Map<Integer, String> map = new HashMap<>();
map.put(1, "Java");
map.put(2, "is");
map.put(3, "great!");

for (Entry<Integer, String> entry : map.entrySet()) {
    System.out.println(
        "number: " + entry.getKey() +
        " - " +
        "Word: " + entry.getValue());
}
```

同样，我们可以使用for-each循环来遍历Java中的各种其他数据结构。

但是，**如果数组或集合为null，则会抛出NullPointerException**：

```java
int[] numbers = null;
for (int number : numbers) {
    System.out.print(number + " ");
}
```

上面的代码抛出一个NullPointerException：

```shell
Exception in thread "main" java.lang.NullPointerException
    at cn.tuyucheng.taketoday.core.controlstructures.loops.ForEachLoop.traverseArray(ForEachLoop.java:63)
    ..
```

**因此，我们必须在将数组或集合传递给for-each循环之前检查它是否为null**。

如果数组或集合元素为空，则for-each循环根本不会执行。

### 3.4 优点和缺点

for-each循环是Java 5中引入的重要特性之一。但是，它也有自己的优点和缺点。

for-each循环的优点是：

-   它可以帮助我们避免编程错误
-   它使代码精确且可读
-   它更容易实现
-   它避免了无限循环的机会

**由于这些好处，我们更倾向for-each循环而不是for循环，尤其是在处理数组或集合时**。

for-each循环的缺点是：

-   我们不能跳过一个元素，因为它遍历每个元素
-   无法以相反的顺序遍历
-   如果我们使用for-each循环，我们就不能修改数组
-   不可能跟踪索引
-   它比for循环有一些性能开销

## 4. 总结

在本文中，我们探讨了Java中的for-each循环及其语法、工作原理和示例。最后，我们看到了它的优点和缺点。
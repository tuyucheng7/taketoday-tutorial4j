## 1. 概述

在Java中，使用List.remove()从List中删除特定值非常简单。但是，有效地删除所有出现的值要困难得多。

在本教程中，我们将看到针对此问题的多种解决方案，描述优缺点。

为了可读性，我们在测试中使用了一个自定义的list(int…)方法，它返回一个包含我们传递的元素的ArrayList 。

## 2. 使用 while循环

因为我们知道如何删除单个元素，所以在循环中重复执行它看起来很简单：

```java
void removeAll(List<Integer> list, int element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```

但是，它没有按预期工作：

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;

// when
assertThatThrownBy(() -> removeAll(list, valueToRemove))
  .isInstanceOf(IndexOutOfBoundsException.class);
```

问题出在第 3 行：我们调用List.remove(int)，它将其参数视为索引，而不是我们要删除的值。

在上面的测试中，我们总是调用list.remove(1)，但我们要删除的元素的索引为0。调用List.remove()会将删除的元素之后的所有元素移动到更小的索引。

在这种情况下，这意味着我们删除所有元素，除了第一个。

当只剩下第一个时，索引1将是非法的。因此我们得到一个Exception。

请注意，只有当我们使用原始byte、short 、 char或int参数调用List.remove()时，我们才会面临这个问题，因为编译器在尝试找到匹配的重载方法时所做的第一件事就是扩大。

我们可以通过将值作为整数传递来更正它：

```java
void removeAll(List<Integer> list, Integer element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```

现在代码按预期工作：

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

由于List.contains()和List.remove( )都必须找到元素的第一次出现，因此这段代码会导致不必要的元素遍历。

如果我们存储第一次出现的索引，我们可以做得更好：

```java
void removeAll(List<Integer> list, Integer element) {
    int index;
    while ((index = list.indexOf(element)) >= 0) {
        list.remove(index);
    }
}
```

我们可以验证它是否有效：

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

虽然这些解决方案生成的代码简短而干净，但它们的性能仍然很差：因为我们不跟踪进度，List.remove()必须找到第一次出现的提供值才能将其删除。

此外，当我们使用ArrayList时，元素移动会导致多次引用，甚至多次重新分配后备数组。

## 3.删除直到列表改变

List.remove(E element)有一个我们还没有提到的特性：它返回一个布尔值，如果List因操作而改变，则该值为true ，因此它包含 element 。

请注意，List.remove(int index)返回 void，因为如果提供的索引有效，List总是会删除它。否则，它会抛出IndexOutOfBoundsException。

有了这个，我们可以执行删除，直到列表发生变化：

```java
void removeAll(List<Integer> list, int element) {
    while (list.remove(element));
}
```

它按预期工作：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

尽管很短，但这个实现遇到了我们在上一节中描述的相同问题。

## 3.使用 for循环

我们可以通过使用for循环遍历元素来跟踪我们的进度，如果匹配则删除当前元素：

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        }
    }
}
```

它按预期工作：

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

但是，如果我们尝试使用不同的输入，它会提供不正确的输出：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(1, 2, 3));
```

让我们逐步分析代码的工作原理：

-   我 = 0
    -   element和list.get(i)在第 3 行都等于1，因此Java进入if语句的主体，
    -   我们删除索引0处的元素，
    -   所以列表现在包含1、2和3
-   我 = 1
    -   list.get(i)返回2因为当我们从List中删除一个元素时，它会将所有后续元素移动到较小的索引

所以当我们有两个相邻的值时，我们会遇到这个问题，我们想删除它们。为了解决这个问题，我们应该维护循环变量。

当我们删除元素时减少它：

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
            i--;
        }
    }
}
```

仅当我们不删除元素时才增加它：

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size();) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        } else {
            i++;
        }
    }
}
```

请注意，在后者中，我们删除了第 2 行的语句i++。

两种解决方案都按预期工作：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

这个实现乍一看似乎是正确的。但是，它仍然存在严重的性能问题：

-   从ArrayList中删除一个元素，移动它之后的所有项目
-   在LinkedList中按索引访问元素意味着一个接一个地遍历元素，直到找到索引

## 4. 使用 for-each循环

从Java5 开始，我们可以使用for-each循环来遍历List。让我们用它来删除元素：

```java
void removeAll(List<Integer> list, int element) {
    for (Integer number : list) {
        if (Objects.equals(number, element)) {
            list.remove(number);
        }
    }
}
```

请注意，我们使用Integer作为循环变量的类型。因此我们不会得到NullPointerException。

此外，我们通过这种方式调用List.remove(E element)，它期望我们要删除的值，而不是索引。

看起来很干净，不幸的是，它不起作用：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
assertThatThrownBy(() -> removeWithForEachLoop(list, valueToRemove))
  .isInstanceOf(ConcurrentModificationException.class);
```

for-each循环使用Iterator遍历元素。但是，当我们修改List时，迭代器会进入不一致状态。因此它抛出ConcurrentModificationException。

教训是：当我们在for-each循环中访问其元素时，我们不应该修改List 。

## 5. 使用 迭代器

我们可以直接使用Iterator来遍历修改List：

```java
void removeAll(List<Integer> list, int element) {
    for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
        Integer number = i.next();
        if (Objects.equals(number, element)) {
            i.remove();
        }
    }
}
```

这样，迭代器可以跟踪列表的状态(因为它进行了修改)。结果，上面的代码按预期工作：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

由于每个List类都可以提供自己的Iterator实现，我们可以放心地假设，它以最有效的方式实现了元素遍历和删除。

然而，使用ArrayList仍然意味着大量的元素移动(可能还有数组重新分配)。此外，上面的代码稍微难读一些，因为它不同于大多数开发人员熟悉的标准for循环。

## 6.收集

在此之前，我们通过删除不需要的项目来修改原始List对象。相反，我们可以创建一个新的List并收集我们想要保留的项目：

```java
List<Integer> removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }
    return remainingElements;
}
```

由于我们在新的List对象中提供了结果，因此我们必须从方法中返回它。因此，我们需要以另一种方式使用该方法：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
List<Integer> result = removeAll(list, valueToRemove);

// then
assertThat(result).isEqualTo(list(2, 3));
```

请注意，现在我们可以使用for-each循环，因为我们不修改当前正在迭代的列表。

因为没有任何删除，所以不需要移动元素。因此，当我们使用ArrayList 时，此实现表现良好。

这个实现在某些方面与之前的不同：

-   它不会修改原始列表，但会返回一个新列表
-   该方法决定了返回列表的实现是什么，它可能与原来的不同

此外，我们可以修改我们的实现以获得旧的行为；我们清除原始列表并将收集的元素添加到其中：

```java
void removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }

    list.clear();
    list.addAll(remainingElements);
}
```

它的工作方式与之前的相同：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

由于我们不会不断修改List，因此我们不必按位置访问元素或移动它们。此外，只有两种可能的数组重新分配：当我们调用List.clear()和List.addAll()时。

## 7. 使用流 API

Java 8 引入了 lambda 表达式和流 API。有了这些强大的功能，我们可以用非常干净的代码解决我们的问题：

```java
List<Integer> removeAll(List<Integer> list, int element) {
    return list.stream()
      .filter(e -> !Objects.equals(e, element))
      .collect(Collectors.toList());
}
```

这个解决方案的工作方式相同，就像我们收集剩余元素时一样。

因此，它具有相同的特性，我们应该使用它来返回结果：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
List<Integer> result = removeAll(list, valueToRemove);

// then
assertThat(result).isEqualTo(list(2, 3));
```

请注意，我们可以使用与原始“收集”实现相同的方法将其转换为像其他解决方案一样工作。

## 8. 使用removeIf

通过 lambda 和函数接口，Java 8 也引入了一些 API 扩展。例如，List.removeIf()方法，它实现了我们在上一节中看到的内容。

它需要一个Predicate ，当我们想要删除元素时它应该返回true ，与前面的例子相反，当我们想要保留元素时我们必须返回true ：

```java
void removeAll(List<Integer> list, int element) {
    list.removeIf(n -> Objects.equals(n, element));
}
```

它像上面的其他解决方案一样工作：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;

// when
removeAll(list, valueToRemove);

// then
assertThat(list).isEqualTo(list(2, 3));
```

由于List本身实现了这个方法，我们可以放心地假设它具有可用的最佳性能。最重要的是，这个解决方案提供了所有代码中最干净的代码。

## 9.总结

在本文中，我们看到了许多解决简单问题的方法，包括不正确的方法。我们对它们进行了分析，以找到适合每种情况的最佳解决方案。
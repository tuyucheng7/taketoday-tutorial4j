## 1. 概述

在这篇简短的文章中，我们将介绍java.util.Stack类并开始研究如何使用它。

[堆栈](https://www.baeldung.com/cs/stack-data-structure)是一种通用数据结构，表示对象的 LIFO(后进先出)集合，允许在恒定时间内压入/弹出元素。

对于新的实现，我们应该支持Deque 接口及其实现。 Deque 定义了一组更完整和一致的 LIFO 操作。但是，我们可能仍然需要处理 Stack类，尤其是在遗留代码中，因此了解它很重要。

## 2. 创建堆栈

让我们首先使用默认的无参数构造函数创建Stack的空实例：

```java
@Test
public void whenStackIsCreated_thenItHasSizeZero() {
    Stack<Integer> intStack = new Stack<>();
    
    assertEquals(0, intStack.size());
}
```

这将创建一个默认容量为 10的Stack 。如果添加的元素数量超过Stack总大小，它会自动加倍。但是，它的大小在删除元素后永远不会缩小。

## 3. Stack的同步

Stack是Vector的直接子类；这意味着与其超类类似，它是一个同步实现。

但是，并不总是需要同步，在这种情况下，建议使用ArrayDeque。

## 4. 添加到堆栈

让我们首先使用push()方法将一个元素添加到Stack的顶部——该方法还会返回添加的元素：

```java
@Test
public void whenElementIsPushed_thenStackSizeIsIncreased() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(1);
    
    assertEquals(1, intStack.size());
}
```

使用push()方法与使用addElement()具有相同的效果。唯一的区别是addElement ()返回操作的结果，而不是添加的元素。

我们也可以一次添加多个元素：

```java
@Test
public void whenMultipleElementsArePushed_thenStackSizeIsIncreased() {
    Stack<Integer> intStack = new Stack<>();
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    
    boolean result = intStack.addAll(intList);
    
    assertTrue(result);
    assertEquals(7, intList.size());
}
```

## 5. 从堆栈中检索

接下来，让我们看看如何获取和删除Stack中的最后一个元素：

```java
@Test
public void whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);

    Integer element = intStack.pop();
    
    assertEquals(Integer.valueOf(5), element);
    assertTrue(intStack.isEmpty());
}
```

我们还可以获取 Stack 的最后一个元素而不删除它：

```java
@Test
public void whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);

    Integer element = intStack.peek();

    assertEquals(Integer.valueOf(5), element);
    assertEquals(1, intStack.search(5));
    assertEquals(1, intStack.size());
}
```

## 6. 在堆栈中搜索元素

### 6.1. 搜索

Stack允许我们搜索一个元素 并获取它与顶部的距离：

```java
@Test
public void whenElementIsOnStack_thenSearchReturnsItsDistanceFromTheTop() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);
    intStack.push(8);

    assertEquals(2, intStack.search(5));
}
```

结果是给定对象的索引。如果存在多个元素，则返回最接近顶部的那个的索引 。位于堆栈顶部的项目被认为位于位置 1。

如果未找到对象，search()将返回 -1。

### 6.2. 获取元素索引

要获取 Stack 上元素的索引，我们还可以使用indexOf()和lastIndexOf()方法：

```java
@Test
public void whenElementIsOnStack_thenIndexOfReturnsItsIndex() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);
    
    int indexOf = intStack.indexOf(5);
    
    assertEquals(0, indexOf);
}
```

lastIndexOf()将始终找到最接近堆栈顶部的元素的索引。这与search()非常相似——重要的区别是它返回索引，而不是与顶部的距离：

```java
@Test
public void whenMultipleElementsAreOnStack_thenIndexOfReturnsLastElementIndex() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);
    intStack.push(5);
    intStack.push(5);
    
    int lastIndexOf = intStack.lastIndexOf(5);
    
    assertEquals(2, lastIndexOf);
}
```

## 7. 从堆栈中删除元素

除了用于删除和检索元素的pop()操作之外，我们还可以使用从Vector类继承的多个操作来删除元素。

### 7.1. 删除指定元素

我们可以使用removeElement()方法删除第一次出现的给定元素：

```java
@Test
public void whenRemoveElementIsInvoked_thenElementIsRemoved() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);
    intStack.push(5);

    intStack.removeElement(5);
    
    assertEquals(1, intStack.size());
}
```

我们还可以使用removeElementAt()来删除Stack中指定索引下的元素：

```java
    @Test
    public void whenRemoveElementAtIsInvoked_thenElementIsRemoved() {
        Stack<Integer> intStack = new Stack<>();
        intStack.push(5);
        intStack.push(7);
        
        intStack.removeElementAt(1);
        
        assertEquals(-1, intStack.search(7));
    }
```

### 7.2. 删除多个元素

让我们快速看一下如何使用removeAll() API 从Stack中删除多个元素——它将Collection作为参数并从Stack中删除所有匹配的元素：

```java
@Test
public void givenElementsOnStack_whenRemoveAllIsInvoked_thenAllElementsFromCollectionAreRemoved() {
    Stack<Integer> intStack = new Stack<>();
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    intStack.addAll(intList);
    intStack.add(500);

    intStack.removeAll(intList);

    assertEquals(1, intStack.size());
    assertEquals(1, intStack.search(500));
}
```

也可以使用clear()或removeAllElements()方法从堆栈中删除所有元素；这两种方法的工作原理相同：

```java
@Test
public void whenRemoveAllElementsIsInvoked_thenAllElementsAreRemoved() {
    Stack<Integer> intStack = new Stack<>();
    intStack.push(5);
    intStack.push(7);

    intStack.removeAllElements();

    assertTrue(intStack.isEmpty());
}
```

### 7.3. 使用过滤器删除元素

我们还可以使用条件从Stack 中移除元素。让我们看看如何使用removeIf ()来执行此操作，并将过滤器表达式作为参数：

```java
@Test
public void whenRemoveIfIsInvoked_thenAllElementsSatysfyingConditionAreRemoved() {
    Stack<Integer> intStack = new Stack<>();
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    intStack.addAll(intList);
    
    intStack.removeIf(element -> element < 6);
    
    assertEquals(2, intStack.size());
}
```

## 8. 遍历堆栈

Stack允许我们同时使用Iterator和ListIterator。主要区别在于第一个允许我们在一个方向上遍历Stack而第二个允许我们在两个方向上这样做：

```java
@Test
public void whenAnotherStackCreatedWhileTraversingStack_thenStacksAreEqual() {
    Stack<Integer> intStack = new Stack<>();
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    intStack.addAll(intList);
    
    ListIterator<Integer> it = intStack.listIterator();
    
    Stack<Integer> result = new Stack<>();
    while(it.hasNext()) {
        result.push(it.next());
    }

    assertThat(result, equalTo(intStack));
}
```

Stack返回的所有迭代器都是快速失败的。

## 9.JavaStack 的 Stream API

Stack是一个集合，这意味着我们可以将它与Java8 Streams API 一起使用。将Stream与Stack一起使用类似于将它与任何其他Collection 一起使用：

```java
@Test
public void whenStackIsFiltered_allElementsNotSatisfyingFilterConditionAreDiscarded() {
    Stack<Integer> intStack = new Stack<>();
    List<Integer> inputIntList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 9, 10);
    intStack.addAll(inputIntList);

    List<Integer> filtered = intStack
      .stream()
      .filter(element -> element <= 3)
      .collect(Collectors.toList());

    assertEquals(3, filtered.size());
}
```

## 10.总结

本教程是了解Java中的这个核心类 – Stack的快速实用指南。

当然，你可以在[Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Stack.html)中探索完整的 API 。
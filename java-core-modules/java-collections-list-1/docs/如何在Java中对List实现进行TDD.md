## 1. 概述

在本教程中，我们将使用测试驱动开发 (TDD) 流程逐步完成自定义List实现。

这不是 TDD 的介绍，所以我们假设你已经对它的含义有一些基本的了解，并且有持续的兴趣去改进它。

简单地说，TDD 是一种设计工具，使我们能够借助测试来驱动我们的实现。

一个快速的免责声明——我们在这里并不专注于创建高效的实现——只是以此为借口来展示 TDD 实践。

## 2. 开始

首先，让我们为我们的类定义骨架：

```java
public class CustomList<E> implements List<E> {
    private Object[] internal = {};
    // empty implementation methods
}

```

CustomList类实现了List接口，因此它必须包含在该接口中声明的所有方法的实现。

首先，我们可以为这些方法提供空体。如果方法有返回类型，我们可以返回该类型的任意值，例如Object为null或boolean为false。

为了简洁起见，我们将省略可选方法，以及一些不经常使用的强制性方法。

## 3. TDD 循环

使用 TDD 开发我们的实现意味着我们需要首先创建测试用例，从而定义我们的实现需求。只有这样我们才能创建或修复实现代码以使这些测试通过。

以非常简化的方式，每个循环中的三个主要步骤是：

1.  编写测试——以测试的形式定义需求
2.  实现功能——让测试通过，而不用过分关注代码的优雅
3.  重构——改进代码，使其更易于阅读和维护，同时仍能通过测试

我们将从最简单的方法开始，针对List接口的某些方法完成这些 TDD 循环。

## 4. isEmpty方法

isEmpty方法可能是List接口中定义的最直接的方法。这是我们的开始实施：

```java
@Override
public boolean isEmpty() {
    return false;
}
```

这个初始方法定义足以编译。当添加越来越多的测试时，该方法的主体将“被迫”改进。

### 4.1. 第一周期

让我们编写第一个测试用例，它确保isEmpty方法在列表不包含任何元素时返回true ：

```java
@Test
public void givenEmptyList_whenIsEmpty_thenTrueIsReturned() {
    List<Object> list = new CustomList<>();

    assertTrue(list.isEmpty());
}
```

给定的测试失败，因为isEmpty方法总是返回false。我们可以通过翻转返回值让它通过：

```java
@Override
public boolean isEmpty() {
    return true;
}
```

### 4.2. 第二个周期

要确认isEmpty方法在列表不为空时返回false，我们需要至少添加一个元素：

```java
@Test
public void givenNonEmptyList_whenIsEmpty_thenFalseIsReturned() {
    List<Object> list = new CustomList<>();
    list.add(null);

    assertFalse(list.isEmpty());
}
```

现在需要实现add方法。这是我们开始的添加方法：

```java
@Override
public boolean add(E element) {
    return false;
}
```

由于未对列表的内部数据结构进行任何更改，因此此方法实现不起作用。让我们更新它以存储添加的元素：

```java
@Override
public boolean add(E element) {
    internal = new Object[] { element };
    return false;
}
```

我们的测试仍然失败，因为isEmpty方法没有得到增强。让我们这样做：

```java
@Override
public boolean isEmpty() {
    if (internal.length != 0) {
        return false;
    } else {
        return true;
    }
}
```

此时非空测试通过。

### 4.3. 重构

到目前为止我们看到的两个测试用例都通过了，但是isEmpty方法的代码可以更优雅。

让我们重构它：

```java
@Override
public boolean isEmpty() {
    return internal.length == 0;
}
```

我们可以看到测试通过了，所以isEmpty方法的实现现在已经完成了。

## 5.尺寸法

这是我们启用CustomList类编译的size方法的开始实现：

```java
@Override
public int size() {
    return 0;
}
```

### 5.1. 第一周期

使用现有的add方法，我们可以为size方法创建第一个测试，验证具有单个元素的列表的大小是否为1：

```java
@Test
public void givenListWithAnElement_whenSize_thenOneIsReturned() {
    List<Object> list = new CustomList<>();
    list.add(null);

    assertEquals(1, list.size());
}
```

测试失败，因为size方法返回0。让我们通过一个新的实现让它通过：

```java
@Override
public int size() {
    if (isEmpty()) {
        return 0;
    } else {
        return internal.length;
    }
}
```

### 5.2. 重构

我们可以重构size方法使其更优雅：

```java
@Override
public int size() {
    return internal.length;
}
```

此方法的实现现已完成。

## 6.获取方法

这是get的开始实现：

```java
@Override
public E get(int index) {
    return null;
}
```

### 6.1. 第一周期

让我们看一下此方法的第一个测试，它验证列表中单个元素的值：

```java
@Test
public void givenListWithAnElement_whenGet_thenThatElementIsReturned() {
    List<Object> list = new CustomList<>();
    list.add("baeldung");
    Object element = list.get(0);

    assertEquals("baeldung", element);
}
```

测试将通过get方法的这个实现通过：

```java
@Override
public E get(int index) {
    return (E) internal[0];
}
```

### 6.2. 改进

通常，我们会在对get方法进行额外改进之前添加更多测试。这些测试需要List接口的其他方法来实现正确的断言。

然而，这些其他方法还不够成熟，所以我们打破了 TDD 循环并创建了get方法的完整实现，这实际上并不难。

很容易想象get必须使用index参数从指定位置的内部数组中提取一个元素：

```java
@Override
public E get(int index) {
    return (E) internal[index];
}
```

## 7.添加方法

这是我们在第 4 节中创建的添加方法：

```java
@Override
public boolean add(E element) {
    internal = new Object[] { element };
    return false;
}
```

### 7.1. 第一周期

以下是验证add返回值的简单测试：

```java
@Test
public void givenEmptyList_whenElementIsAdded_thenGetReturnsThatElement() {
    List<Object> list = new CustomList<>();
    boolean succeeded = list.add(null);

    assertTrue(succeeded);
}
```

我们必须修改add方法以返回true以便测试通过：

```java
@Override
public boolean add(E element) {
    internal = new Object[] { element };
    return true;
}
```

虽然测试通过了，但add方法还没有涵盖所有情况。如果我们向列表中添加第二个元素，现有元素将丢失。

### 7.2. 第二个周期

这是另一个测试，添加了列表可以包含多个元素的要求：

```java
@Test
public void givenListWithAnElement_whenAnotherIsAdded_thenGetReturnsBoth() {
    List<Object> list = new CustomList<>();
    list.add("baeldung");
    list.add(".com");
    Object element1 = list.get(0);
    Object element2 = list.get(1);

    assertEquals("baeldung", element1);
    assertEquals(".com", element2);
}
```

测试将失败，因为当前形式的add方法不允许添加多个元素。

我们改一下实现代码：

```java
@Override
public boolean add(E element) {
    Object[] temp = Arrays.copyOf(internal, internal.length + 1);
    temp[internal.length] = element;
    internal = temp;
    return true;
}
```

实现足够优雅，因此我们不需要重构它。

## 八、总结

本教程通过测试驱动的开发过程来创建自定义列表实现的一部分。使用 TDD，我们可以逐步实现需求，同时将测试覆盖率保持在很高的水平。此外，实现保证是可测试的，因为它是为了让测试通过而创建的。

请注意，本文中创建的自定义类仅用于演示目的，不应在实际项目中采用。
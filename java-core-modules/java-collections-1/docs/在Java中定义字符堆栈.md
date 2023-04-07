## 1.概述

在本教程中，我们将讨论如何在Java中创建字符堆栈。我们将首先了解如何使用JavaAPI执行此操作，然后我们将查看一些自定义实现。

[Stack](https://www.baeldung.com/java-stack)是一种遵循LIFO(后进先出)原则的数据结构。它的一些常用方法是：

-   push(Eitem)–将一个项目推到堆栈的顶部
-   pop()–移除并返回栈顶的对象
-   peek()–返回堆栈顶部的对象而不删除它

## 2.使用JavaAPI的字符堆栈

Java有一个名为java.util.Stack的内置API。由于char是原始数据类型，不能在泛型中使用，因此我们必须使用java.lang.Character的包装类来创建Stack：

```java
Stack<Character> charStack = new Stack<>();
```

现在，我们可以在Stack中使用push、pop和peek方法。

另一方面，我们可能会被要求构建堆栈的自定义实现。因此，我们将研究几种不同的方法。

## 3.使用链表自定义实现

让我们使用LinkedList作为后端数据结构来实现一个字符栈：

```java
public class CharStack {

    private LinkedList<Character> items;

    public CharStack() {
        this.items = new LinkedList<Character>();
    }
}
```

我们创建了一个在构造函数中初始化的items变量。

现在，我们必须提供push、peek和pop方法的实现：

```java
public void push(Character item) {
    items.push(item);
}

public Character peek() {
    return items.getFirst();
}

public Character pop() {
    Iterator<Character> iter = items.iterator();
    Character item = iter.next();
    if (item != null) {
        iter.remove();
        return item;
    }
    return null;
}
```

push和peek方法使用LinkedList的内置方法。对于pop，我们首先使用Iterator来检查顶部是否有项目。如果它在那里，我们通过调用remove方法从列表中删除该项目。

## 4.使用数组自定义实现

我们还可以使用数组作为我们的数据结构：

```java
public class CharStackWithArray {

    private char[] elements;
    private int size;

    public CharStackWithArray() {
        size = 0;
        elements = new char[4];
    }

}
```

上面，我们创建了一个char数组，我们在构造函数中将其初始化，初始容量为4。此外，我们还有一个大小变量来跟踪堆栈中存在多少条记录。

现在，让我们实现push方法：

```java
public void push(char item) {
    ensureCapacity(size + 1);
    elements[size] = item;
    size++;
}

private void ensureCapacity(int newSize) {
    char newBiggerArray[];
    if (elements.length < newSize) {
        newBiggerArray = new char[elements.length  2];
        System.arraycopy(elements, 0, newBiggerArray, 0, size);
        elements = newBiggerArray;
    }
}
```

将项目推入堆栈时，我们首先需要检查我们的数组是否有存储它的容量。如果不是，我们创建一个新数组并将其大小加倍。然后我们将旧元素到新创建的数组并将其分配给我们的元素变量。

注意：关于为什么我们要将数组的大小加倍，而不是简单地增加一个大小的解释，请参阅这篇[StackOverflow帖子](https://stackoverflow.com/questions/10419250/why-double-stack-capacity-instead-of-just-increasing-it-by-fixed-amount)。

最后，让我们实现peek和pop方法：

```java
public char peek() {
    if (size == 0) {
        throw new EmptyStackException();
    }
    return elements[size - 1];
}

public char pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }
    return elements[--size];
}
```

对于这两种方法，在验证堆栈不为空后，我们返回位置size–1处的元素。对于pop，除了返回元素外，我们还将size减1。

## 5.总结

在本文中，我们了解了如何使用JavaAPI创建字符堆栈，并且看到了一些自定义实现。
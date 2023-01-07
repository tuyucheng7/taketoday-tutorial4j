## 1. 概述

Java [Stack](https://www.baeldung.com/java-stack)类实现[堆栈数据结构](https://www.baeldung.com/cs/stack-data-structure)。Java 1.6引入了[Deque](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Deque.html)接口，实现了一个“双端队列”，支持两端插入和移除元素。

现在，我们也可以将Deque接口用作 LIFO(后进先出)堆栈。此外，如果我们查看[Stack](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Stack.html)[类的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Stack.html)[Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Stack.html)，我们将看到：

>   Deque接口及其实现提供了一组更完整和一致的 LIFO 堆栈操作，应优先使用此类。

在本教程中，我们将比较JavaStack类和Deque接口。此外，我们将讨论为什么我们应该为 LIFO 堆栈使用Deque而不是Stack。

## 2.类与接口

Java 的Stack 是一个 类：

```java
public class Stack<E> extends Vector<E> { ... }
```

也就是说，如果我们要创建自己的Stack类型，就得继承java.util.Stack类。

由于Java不支持多重继承，如果我们的类已经是另一种类型的子类，有时很难扩展Stack类：

```java
public class UserActivityStack extends ActivityCollection { ... }
```

在上面的示例中，UserActivityStack类是ActivityCollection类的子类。因此，它不能同时扩展java.util.Stack类。要使用JavaStack类，我们可能需要重新设计我们的数据模型。

另一方面，Java 的Deque是一个接口：

```java
public interface Deque<E> extends Queue<E> { ... }
```

我们知道在Java中一个类可以实现多个接口。因此，实现接口比扩展继承类更灵活。

例如，我们可以轻松地让我们的 UserActivityStack实现Deque接口：

```java
public class UserActivityStack extends ActivityCollection implements Deque<UserActivity> { ... }
```

因此，从面向对象设计的角度来看，Deque接口比Stack类给我们带来了更多的灵活性。

## 3. synchronized方法和性能

我们已经看到Stack类是java.util.Vector的子类。Vector类是同步的。它使用传统的方式来实现线程安全：使其方法“同步”。”

作为其子类，Stack类也是同步的。

另一方面，Deque接口不是线程安全的。

因此，如果不需要线程安全，Deque可以为我们带来比Stack更好的性能。

## 4.迭代顺序

由于Stack和Deque都是java.util.Collection接口的子类型，因此它们也是Iterable。

然而，有趣的是，如果我们以相同的顺序将相同的元素压入Stack对象和Deque实例，它们的迭代顺序是不同的。

让我们通过例子来仔细看看它们。

首先，让我们将一些元素压入 Stack对象并检查它的迭代顺序是什么：

```java
@Test
void givenAStack_whenIterate_thenFromBottomToTop() {
    Stack<String> myStack = new Stack<>();
    myStack.push("I am at the bottom.");
    myStack.push("I am in the middle.");
    myStack.push("I am at the top.");

    Iterator<String> it = myStack.iterator();

    assertThat(it).toIterable().containsExactly(
      "I am at the bottom.",
      "I am in the middle.",
      "I am at the top.");
}

```

如果我们执行上面的测试方法，它就会通过。这意味着，当我们遍历Stack对象中的元素时，顺序是从栈底到栈顶。

接下来，让我们在Deque实例上做同样的测试 。在我们的测试中，我们将[ArrayDeque](https://www.baeldung.com/java-array-deque)类作为Deque实现。

此外，我们将使用 ArrayDeque作为后进先出堆栈：

```java
@Test
void givenADeque_whenIterate_thenFromTopToBottom() {
    Deque<String> myStack = new ArrayDeque<>();
    myStack.push("I am at the bottom.");
    myStack.push("I am in the middle.");
    myStack.push("I am at the top.");

    Iterator<String> it = myStack.iterator();

    assertThat(it).toIterable().containsExactly(
      "I am at the top.",
      "I am in the middle.",
      "I am at the bottom.");
}

```

如果我们试一试，这个测试也会通过。

所以Deque的迭代顺序是从上到下。

当我们谈论 LIFO 堆栈数据结构时，迭代堆栈中元素的正确顺序应该是从上到下。

也就是说，Deque的迭代器按照我们期望的堆栈方式工作。

## 5.无效的后进先出堆栈操作

经典的 LIFO 堆栈数据结构仅支持push()、pop()和peek()操作。Stack类和Deque接口都支持它们。到目前为止，一切都很好。

但是，不允许通过后进先出堆栈中的索引访问或操作元素， 因为它违反了后进先出规则。

在本节中，让我们看看是否可以使用Stack和Deque 调用无效的堆栈操作。

### 5.1. java.util.Stack类_

由于其父Vector 是基于数组的数据结构，因此Stack类具有通过索引访问元素的能力：

```java
@Test
void givenAStack_whenAccessByIndex_thenElementCanBeRead() {
    Stack<String> myStack = new Stack<>();
    myStack.push("I am the 1st element."); //index 0
    myStack.push("I am the 2nd element."); //index 1
    myStack.push("I am the 3rd element."); //index 2
 
    assertThat(myStack.get(0)).isEqualTo("I am the 1st element.");
}

```

如果我们运行它，测试将通过。

在测试中，我们将三个元素压入Stack对象。之后，我们要访问位于堆栈底部的元素。

遵循 LIFO 规则，我们必须弹出上面的所有元素才能访问底部元素。

但是，在这里，使用Stack对象，我们只能通过其索引访问元素。

此外，使用Stack对象，我们甚至可以通过其索引插入和删除元素。让我们创建一个测试方法来证明它：

```java
@Test
void givenAStack_whenAddOrRemoveByIndex_thenElementCanBeAddedOrRemoved() {
    Stack<String> myStack = new Stack<>();
    myStack.push("I am the 1st element.");
    myStack.push("I am the 3rd element.");

    assertThat(myStack.size()).isEqualTo(2);

    myStack.add(1, "I am the 2nd element.");
    assertThat(myStack.size()).isEqualTo(3);
    assertThat(myStack.get(1)).isEqualTo("I am the 2nd element.");

    myStack.remove(1);
    assertThat(myStack.size()).isEqualTo(2);
}

```

如果我们试一试，测试也会通过。

因此，使用Stack类，我们可以像操作数组一样操作其中的元素。这违反了 LIFO 合同。

### 5.2. java.util.Deque接口_

Deque不允许我们通过索引访问、插入或删除元素。 听起来比 Stack类好。

然而，由于Deque是一个“双端队列”，我们可以从两端插入或删除一个元素。

换句话说，当我们使用Deque作为后进先出栈时，我们可以直接从栈底插入/移除元素。

让我们构建一个测试方法，看看这是如何发生的。同样，我们将在测试中继续使用ArrayDeque类：

```java
@Test
void givenADeque_whenAddOrRemoveLastElement_thenTheLastElementCanBeAddedOrRemoved() {
    Deque<String> myStack = new ArrayDeque<>();
    myStack.push("I am the 1st element.");
    myStack.push("I am the 2nd element.");
    myStack.push("I am the 3rd element.");

    assertThat(myStack.size()).isEqualTo(3);

    //insert element to the bottom of the stack
    myStack.addLast("I am the NEW element.");
    assertThat(myStack.size()).isEqualTo(4);
    assertThat(myStack.peek()).isEqualTo("I am the 3rd element.");

    //remove element from the bottom of the stack
    String removedStr = myStack.removeLast();
    assertThat(myStack.size()).isEqualTo(3);
    assertThat(removedStr).isEqualTo("I am the NEW element.");
}

```

在测试中，首先，我们使用addLast()方法将一个新元素插入到堆栈底部。如果插入成功，我们将尝试使用removeLast()方法将其删除。

如果我们执行测试，它就会通过。

因此，Deque也不遵守 LIFO 合同。

### 5.3. 基于Deque实现LifoStack

我们可以创建一个简单的LifoStack接口来遵守 LIFO 契约：

```java
public interface LifoStack<E> extends Collection<E> {
    E peek();
    E pop();
    void push(E item);
}

```

当我们创建LifoStack 接口的实现时，我们可以包装Java标准Deque实现。

让我们创建一个ArrayLifoStack类作为例子来快速理解它：

```java
public class ArrayLifoStack<E> implements LifoStack<E> {
    private final Deque<E> deque = new ArrayDeque<>();

    @Override
    public void push(E item) {
        deque.addFirst(item);
    }

    @Override
    public E pop() {
        return deque.removeFirst();
    }

    @Override
    public E peek() {
        return deque.peekFirst();
    }

    // forward methods in Collection interface
    // to the deque object

    @Override
    public int size() {
        return deque.size();
    }
...
}
```

正如ArrayLifoStack类所示，它仅支持我们的LifoStack接口和java.util.Collection接口中定义的操作。

这样，就不会违反后进先出法则。

## 六，总结

从Java1.6 开始， java.util.Stack和java.util.Deque都可以用作 LIFO 堆栈。本文讨论了这两种类型之间的区别。

我们还分析了为什么我们应该在Stack类上使用Deque接口来处理 LIFO 堆栈。

此外，正如我们通过示例讨论的那样，Stack和Deque或多或少都违反了 LIFO 规则。

最后，我们展示了一种创建遵循 LIFO 契约的堆栈接口的方法。
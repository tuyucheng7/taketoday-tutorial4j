## 1. 概述

在本教程中，我们将了解Java中Iterable和 Iterator接口的用法以及它们之间的区别。

## 2.可迭代接口

[Iterable](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Iterable.html)接口属于java.lang包。它表示可以迭代的数据结构。

Iterable接口提供了一种生成Iterator的方法。使用Iterable时，我们无法通过索引获取元素。同样，我们也无法从数据结构中获取第一个或最后一个元素。

Java 中的所有集合都实现了Iterable接口。

### 2.1. 迭代可迭代对象

我们可以使用增强的 for循环(也称为[for -each](https://www.baeldung.com/foreach-java)循环)遍历集合中的元素。但是，在这样的语句中只能使用实现Iterable接口的对象。也可以结合使用while语句和Iterator来迭代元素。

让我们看一个使用for -each 语句迭代List中的元素的示例：

```java
List<Integer> numbers = getNumbers();
for (Integer number : numbers) {
    System.out.println(number);
}
```

同样，我们可以将forEach()方法与 lambda 表达式结合使用：

```java
List<Integer> numbers = getNumbers();
numbers.forEach(System.out::println);
```

### 2.2. 实现可迭代接口

当我们有想要迭代的自定义数据结构时， Iterable接口的自定义实现可以派上用场。

让我们首先创建一个表示购物车的类，该购物车将在数组中保存元素。我们不会直接在数组上调用for -each 循环。相反，我们将实现Iterable接口。我们不希望我们的客户依赖于所选的数据结构。如果我们为客户提供迭代的能力，我们可以轻松地使用不同的数据结构，而客户无需更改代码。

ShoppingCart类实现了Iterable接口并覆盖了它的iterate ()方法：

```java
public class ShoppingCart<E> implements Iterable<E> {

    private E[] elementData;
    private int size;

    public void add(E element) {
        ensureCapacity(size + 1);
        elementData[size++] = element;
    }

    @Override
    public Iterator<E> iterator() {
        return new ShoppingCartIterator();
    }
}
```

add()方法将元素存储在数组中。由于数组的大小和容量是固定的，我们使用ensureCapacity()方法扩展元素的最大数量。

自定义数据结构上的iterator()方法的每次调用都会生成Iterator的新实例。我们创建了一个新实例，因为迭代器负责维护当前的迭代状态。

通过提供iterator()方法的具体实现，我们可以使用增强的 for语句来迭代已实现类的对象。

现在，让我们在ShoppingCart类中创建一个内部类来表示我们的自定义迭代器：

```java
public class ShoppingCartIterator implements Iterator<E> {
    int cursor;
    int lastReturned = -1;

    public boolean hasNext() {
        return cursor != size;
    }

    public E next() {
        return getNextElement();
    }

    private E getNextElement() {
        int current = cursor;
        exist(current);

        E[] elements = ShoppingCart.this.elementData;
        validate(elements, current);

        cursor = current + 1;
        lastReturned = current;
        return elements[lastReturned];
    }
}
```

最后，让我们创建一个可迭代类的实例，并在增强的 for循环中使用它：

```java
ShoppingCart<Product> shoppingCart  = new ShoppingCart<>();

shoppingCart.add(new Product("Tuna", 42));
shoppingCart.add(new Product("Eggplant", 65));
shoppingCart.add(new Product("Salad", 45));
shoppingCart.add(new Product("Banana", 29));
 
for (Product product : shoppingCart) {
   System.out.println(product.getName());
}
```

## 3.迭代器接口

[迭代器](https://www.baeldung.com/java-iterator)是Java集合框架的成员。它属于java.util包。该接口允许我们在迭代期间从集合中检索或删除元素。

此外，它还有两个方法可以帮助迭代数据结构并检索其元素——next( )和hasNext()。

此外，它还有一个remove()方法，可以删除Iterator指向的当前元素。

最后，forEachRemaining(Consumer<? super E> action)方法对数据结构中的每个剩余元素执行给定的操作。

### 3.1. 遍历集合

让我们看看如何迭代Integer元素列表。在示例中，我们将组合while循环和方法hasNext()和next()。

List接口是Collection的一部分，因此它扩展了Iterable接口。要从集合中获取迭代器，我们只需调用iterator()方法：

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(10);
numbers.add(20);
numbers.add(30);
numbers.add(40);

Iterator<Integer> iterator = numbers.iterator();
```

此外，我们可以通过调用hasNext()方法检查迭代器是否还有剩余元素。之后，我们可以通过调用next()方法来获取一个元素：

```java
while (iterator.hasNext()) {
   System.out.println(iterator.next());
}
```

next()方法返回迭代中的下一个元素。另一方面，如果没有这样的元素，它会抛出NoSuchElementException。

### 3.2. 实现迭代器接口

现在，我们将实现Iterator接口。当我们需要使用条件元素检索遍历集合时，自定义实现会很有用。例如，我们可以使用自定义迭代器来迭代奇数或偶数。

为了说明，我们将迭代给定集合中的[素数](https://www.baeldung.com/java-prime-numbers)。正如我们所知，如果一个数只能被一个和它本身整除，则它被认为是质数。

首先，让我们创建一个包含数字元素集合的类：

```java
class Numbers {
 
    private static final List<Integer> NUMBER_LIST =
      Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
}
```

此外，让我们定义Iterator接口的具体实现：

```java
private static class PrimeIterator implements Iterator<Integer> {

    private int cursor;

    @Override
    public Integer next() {
        exist(cursor);
        return NUMBER_LIST.get(cursor++);
    }

    @Override
    public boolean hasNext() {
        if (cursor > NUMBER_LIST.size()) {
            return false;
        }

        for (int i = cursor; i < NUMBER_LIST.size(); i++) {
            if (isPrime(NUMBER_LIST.get(i))) {
                cursor = i;
                return true;
            }
        }

        return false;
    }
}
```

具体实现通常创建为内部类。此外，他们还负责维护当前的迭代状态。在上面的示例中，我们将下一个素数的当前位置存储在实例变量中。每次我们调用next()方法时，变量都会包含即将到来的素数的索引。

next()方法的任何实现都应该在没有更多元素剩余时抛出NoSuchElementException异常。否则，迭代可能会导致意外行为

让我们在Number类中定义一个方法，它返回PrimeIterator类的一个新实例：

```java
public static Iterator<Integer> iterator() {
    return new PrimeIterator();
}
```

最后，我们可以在while语句中使用自定义迭代器：

```java
Iterator<Integer> iterator = Numbers.iterator();
 
while (iterator.hasNext()) {
   System.out.println(iterator.next());
}
```

## 4. Iterable和Iterator的区别

综上所述，下表显示了Iterable和Iterator接口之间的主要区别：

|                    可迭代的                    |                         迭代器                          |
| :--------------------------------------------: | :-----------------------------------------------------: |
|     表示可以使用for -each 循环迭代的集合     |                表示可用于迭代集合的接口                 |
| 实现Iterable时，我们需要覆盖iterator()方法 | 实现Iterator时，我们需要覆盖hasNext()和next()方法 |
|                 不存储迭代状态                 |                      存储迭代状态                       |
|            不允许在迭代期间删除元素            |                 允许在迭代期间删除元素                  |

## 5.总结

在本文中，我们了解了Java中Iterable和Iterator接口的区别及其用法。
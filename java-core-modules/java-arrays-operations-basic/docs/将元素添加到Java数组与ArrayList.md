## 1. 概述

[在本教程中，我们将简要介绍Java数组和标准ArrayList](https://www.baeldung.com/java-arraylist)在内存分配方面的异同。此外，我们将了解如何在数组和ArrayList中追加和插入元素。

## 2. Java数组和ArrayList

[Java 数组](https://www.baeldung.com/java-common-array-operations)是该语言提供的一种基本数据结构。相反，ArrayList是由数组支持的List接口的实现，在Java集合框架中提供。

### 2.1. 访问和修改元素

我们可以使用方括号表示法访问和修改数组元素：

```java
System.out.println(anArray[1]);
anArray[1] = 4;
```

另一方面，ArrayList有一组访问和修改元素的方法：

```java
int n = anArrayList.get(1);
anArrayList.set(1, 4);
```

### 2.2. 固定大小与动态大小

数组和ArrayList都以类似的方式分配堆内存，但不同的是数组是固定大小的，而ArrayList的大小 是动态增加的。

由于Java数组是固定大小的，因此我们需要在实例化它时提供大小。一旦实例化，就不可能增加数组的大小。相反，我们需要创建一个调整后大小的新数组，并前一个数组中的所有元素。

ArrayList是List接口的可调整大小的数组实现——也就是说，ArrayList会随着元素的添加而动态增长。当当前元素的数量(包括要添加到 ArrayList 的新元素)大于其底层数组的最大大小时，ArrayList会增加底层数组的大小。

底层数组的增长策略取决于ArrayList的实现。但是，由于底层数组的大小无法动态增加，因此会创建一个新数组并将旧数组元素到新数组中。

添加操作具有恒定的摊销时间成本。换句话说，将n 个元素添加到ArrayList需要O(n)时间。

### 2.3. 元素类型

数组可以包含原始数据类型和非原始数据类型，具体取决于数组的定义。但是，ArrayList只能包含非原始数据类型。

当我们将具有原始数据类型的元素插入 ArrayList 时，Java编译器会自动将原始数据类型转换为其对应的对象包装类。

现在让我们看看如何在Java数组和ArrayList中追加和插入元素。

## 3.附加一个元素

正如我们已经看到的，数组的大小是固定的。

因此，要附加一个元素，首先，我们需要声明一个比旧数组大的新数组，并将元素从旧数组到新创建的数组中。之后，我们可以将新元素附加到这个新创建的数组中。

让我们看看它在不使用任何实用程序类的情况下在Java中的实现：

```java
public Integer[] addElementUsingPureJava(Integer[] srcArray, int elementToAdd) {
    Integer[] destArray = new Integer[srcArray.length+1];

    for(int i = 0; i < srcArray.length; i++) {
        destArray[i] = srcArray[i];
    }

    destArray[destArray.length - 1] = elementToAdd;
    return destArray;
}
```

或者，[Arrays ](https://www.baeldung.com/java-util-arrays)类提供了一个实用方法copyOf()，它有助于创建一个更大的新数组并从旧数组中所有元素：

```java
int[] destArray = Arrays.copyOf(srcArray, srcArray.length + 1);
```

一旦我们创建了一个新数组，我们就可以轻松地将新元素附加到数组中：

```java
destArray[destArray.length - 1] = elementToAdd;
```

另一方面，在ArrayList中附加一个元素非常容易：

```java
anArrayList.add(newElement);
```

## 4. 在索引处插入一个元素

在给定索引处插入元素而不丢失先前添加的元素在数组中不是一项简单的任务。

首先，如果数组已经包含与其大小相等的元素数，那么我们首先需要创建一个更大大小的新数组并将元素到新数组中。

此外，我们需要将指定索引之后的所有元素向右移动一个位置：

```java
public static int[] insertAnElementAtAGivenIndex(final int[] srcArray, int index, int newElement) {
    int[] destArray = new int[srcArray.length+1];
    int j = 0;
    for(int i = 0; i < destArray.length-1; i++) {

        if(i == index) {
            destArray[i] = newElement;
        } else {
            destArray[i] = srcArray[j];
            j++;
        }
    }
    return destArray;
}
```

但是，[ArrayUtils](https://www.baeldung.com/array-processing-commons-lang)类为我们提供了一种将项目插入数组的更简单的解决方案：

```java
int[] destArray = ArrayUtils.insert(2, srcArray, 77);
```

我们必须指定要插入值的索引、源数组和要插入的值。

insert ()方法返回一个包含更多元素的新数组，新元素位于指定索引处，所有剩余元素都向右移动一位。

请注意， insert()方法的最后一个参数是可变参数，因此我们可以将任意数量的项插入数组。

让我们用它在srcArray中从索引 2 开始插入三个元素：

```java
int[] destArray = ArrayUtils.insert(2, srcArray, 77, 88, 99);
```

其余元素将向右移动三个位置。

此外，这可以通过ArrayList轻松实现：

```java
anArrayList.add(index, newElement);
```

ArrayList移动元素并将元素插入到所需位置。

## 5.总结

在本文中，我们研究了Java数组和ArrayList。此外，我们还研究了两者之间的相同点和不同点。最后，我们看到了如何在数组和ArrayList中追加和插入元素。
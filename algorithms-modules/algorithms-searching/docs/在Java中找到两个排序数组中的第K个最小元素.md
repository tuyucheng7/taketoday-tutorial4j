## 1. 简介

在本文中，我们将了解如何在两个排序数组的并集中找到第k个最小的元素。

首先，我们将定义确切的问题。其次，我们将看到两个低效但直接的解决方案。第三，我们将研究基于对两个数组进行二分查找的有效解决方案。最后，我们将查看一些测试来验证我们的算法是否有效。

我们还将看到算法所有部分的Java代码片段。为简单起见，我们的实现将只对整数进行操作。然而，所描述的算法适用于所有可比较的数据类型，甚至可以使用泛型来实现。

## 2. 两个排序数组的并集中第K小的元素是什么？

### 2.1. 第K个最小的元素

要在数组中找到第k个最小元素，也称为第k阶统计量，我们通常使用[选择算法](https://www.baeldung.com/java-kth-largest-element)。然而，这些算法对单个未排序的数组进行操作，而在本文中，我们想要在两个已排序的数组中找到第k个最小的元素。

在我们看到问题的几种解决方案之前，让我们准确地定义我们想要实现的目标。为此，让我们直接进入示例。

我们有两个排序数组(a和b)，它们不一定需要具有相同数量的元素：

[![n次元问题1](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-problem-1-768x227-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-problem-1-768x227-1.png)

在这两个数组中，我们要找到第k个最小的元素。更具体地说，我们想要在组合排序后的数组中找到第k个最小的元素：

[![n次元问题2](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-problem-2-2-768x358-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-problem-2-2-768x358-1.png)

我们示例的组合和排序数组显示在 (c) 中。第一个最小的元素是3，第四个最小的元素是20。

### 2.2. 重复值

我们还需要定义如何处理重复值。一个元素可以在其中一个数组(数组a中的元素3 )中出现多次，也可以在第二个数组 ( b ) 中再次出现。

[![第 n 个元素不同 1](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-distinct-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-distinct-1.png)

如果我们只对重复计数一次，我们将按 (c) 中所示进行计数。如果我们计算一个元素的所有出现次数，我们将按 (d) 中所示进行计数。

[![第 n 个不同的元素 2](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-distinct-2-2-1024x333-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-distinct-2-2-1024x333-1.png)

在本文的其余部分，我们将按照 (d) 中所示对重复项进行计数，从而将它们视为不同的元素进行计数。

## 3. 两种简单但效率较低的方法

### 3.1. 连接然后排序两个数组

找到第k个最小元素的最简单方法是连接数组，对它们进行排序，然后返回结果数组的第k个元素：

```java
int getKthElementSorted(int[] list1, int[] list2, int k) {

    int length1 = list1.length, length2 = list2.length;
    int[] combinedArray = new int[length1 + length2];
    System.arraycopy(list1, 0, combinedArray, 0, list1.length);
    System.arraycopy(list2, 0, combinedArray, list1.length, list2.length);
    Arrays.sort(combinedArray);

    return combinedArray[k-1];
}
```

n是第一个数组的长度，m 是第二个数组的长度，我们得到组合长度c = n + m。

由于排序的复杂度为O(c log c)，因此此方法的总体复杂度为O(n log n)。

这种方法的缺点是我们需要创建数组的副本，这会导致需要更多空间。

### 3.2. 合并两个数组

[类似于Merge Sort](https://www.baeldung.com/java-merge-sort)排序算法的一步，我们可以[合并](https://www.baeldung.com/java-merge-sorted-arrays)两个数组，然后直接检索第k个元素。

合并算法的基本思想是从两个指针开始，分别指向第一个和第二个数组(a)的第一个元素。

然后我们比较指针处的两个元素(3和4)，将较小的一个(3)添加到结果中，并将该指针向前移动一个位置(b)。同样，我们比较指针处的元素并将较小的元素 ( 4 ) 添加到结果中。

我们以相同的方式继续，直到所有元素都添加到结果数组中。如果其中一个输入数组没有更多元素，我们只需将另一个输入数组的所有剩余元素到结果数组。

[![第 n 个元素合并](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-merge-1024x592-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-merge-1024x592-1.png)

如果我们不完整的数组，但当结果数组有k个元素时停止，我们可以提高性能。我们甚至不需要为组合数组创建额外的数组，而是可以只对原始数组进行操作。

这是Java中的一个实现：

```java
public static int getKthElementMerge(int[] list1, int[] list2, int k) {

    int i1 = 0, i2 = 0;

    while(i1 < list1.length && i2 < list2.length && (i1 + i2) < k) {
        if(list1[i1] < list2[i2]) {
            i1++;
        } else {
            i2++;
        }
    }

    if((i1 + i2) < k) {
        return i1 < list1.length ? list1[k - i2 - 1] : list2[k - i1 - 1]; 
    } else if(i1 > 0 && i2 > 0) {
        return Math.max(list1[i1-1], list2[i2-1]);
    } else {
        return i1 == 0 ? list2[i2-1] : list1[i1-1];
    }
}
```

很容易理解这个算法的时间复杂度是 O( k )。这种算法的一个优点是它可以很容易地适应只考虑重复元素一次。

## 4. 对两个数组进行二分查找

我们能做得比 O( k ) 更好吗？答案是我们可以。基本思想是对两个数组执行二进制搜索算法。

为此，我们需要一个数据结构来提供对其所有元素的恒定时间读取访问。在Java中，它可以是数组或ArrayList。

让我们为要实现的方法定义框架：

```java
int findKthElement(int k, int[] list1, int[] list2)
    throws NoSuchElementException, IllegalArgumentException {

    // check input (see below)

    // handle special cases (see below)

    // binary search (see below)
}
```

在这里，我们将k和两个数组作为参数传递。首先，我们将验证输入；其次，我们处理一些特殊情况，然后进行二分查找。在接下来的三个部分中，我们将以相反的顺序查看这三个步骤，因此首先我们将看到二分查找，其次是特殊情况，最后是参数验证。

### 4.1. 二进制搜索

标准的二分搜索，我们正在寻找一个特定的元素，有两种可能的结果：我们找到我们正在寻找的元素并且搜索成功，或者我们没有找到它并且搜索不成功。这在我们的例子中是不同的，我们想要找到第k个最小的元素。在这里，我们总是有一个结果。

让我们看看如何实现它。

#### 4.1.1. 从两个数组中找到正确数量的元素

我们从第一个数组中的一定数量的元素开始搜索。我们称这个数字为nElementsList1。由于我们总共需要k个元素，因此nElementsList1 的数量为：

```java
int nElementsList2 = k - nElementsList1;

```

例如，假设k = 8。我们从第一个数组的四个元素和第二个数组 (a) 的四个元素开始。

[![img](https://www.baeldung.com/wp-content/uploads/2020/08/nth%20element%20binary%20a.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-a.png)

如果第一个数组中的第 4 个元素大于第二个数组中的第 4 个元素，我们知道我们从第一个数组中取出了太多元素，可以减少nElementsList1 (b)。否则，我们知道我们取的元素太少，可以增加nElementsList1 (b')。

[![第 n 个元素二进制 b](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-b-1024x367-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-b-1024x367-1.png)

我们继续，直到达到停止标准。在我们了解那是什么之前，让我们先看看到目前为止我们所描述的代码：

```java
int right = k;
int left = = 0;
do {
    nElementsList1 = ((left + right) / 2) + 1;
    nElementsList2 = k - nElementsList1;

    if(nElementsList2 > 0) {
        if (list1[nElementsList1 - 1] > list2[nElementsList2 - 1]) {
            right = nElementsList1 - 2;
        } else {
            left = nElementsList1;
        }
    }
} while(!kthSmallesElementFound(list1, list2, nElementsList1, nElementsList2));
```

#### 4.1.2. 停止标准

我们可以在两种情况下停止。首先，如果我们从第一个数组中取出的最大元素等于我们从第二个数组中取出的最大元素 (c)，我们可以停止。在这种情况下，我们可以简单地返回该元素。

[![第 n 个元素二进制 c 2](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-c-2.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-c-2.png)

其次，如果满足以下两个条件，我们就可以停止(d)：

-   从第一个数组中获取的最大元素小于我们不从第二个数组中获取的最小元素 ( 11 < 100 )。
-   从第二个数组中取出的最大元素小于我们不从第一个数组中取出的最小元素 ( 21 < 27 )。

[![第 n 个元素二进制 d](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-d-1024x418-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-d-1024x418-1.png)

很容易想象 (d') 为什么该条件有效：我们从两个数组中获取的所有元素肯定小于两个数组中的任何其他元素。

这是停止标准的代码：

```java
private static boolean foundCorrectNumberOfElementsInBothLists(int[] list1, int[] list2, int nElementsList1, int nElementsList2) {

    // we do not take any element from the second list
    if(nElementsList2 < 1) {
        return true;
    }

    if(list1[nElementsList1-1] == list2[nElementsList2-1]) {
        return true;
    }

    if(nElementsList1 == list1.length) {
        return list1[nElementsList1-1] <= list2[nElementsList2];
    }

    if(nElementsList2 == list2.length) {
        return list2[nElementsList2-1] <= list1[nElementsList1];
    }

    return list1[nElementsList1-1] <= list2[nElementsList2] && list2[nElementsList2-1] <= list1[nElementsList1];
}
```

#### 4.1.3. 返回值

最后，我们需要返回正确的值。在这里，我们有三种可能的情况：

-   我们不从第二个数组中获取任何元素，因此目标值在第一个数组 (e) 中
-   目标值在第一个数组(e')
-   目标值在第二个数组(e”)中

[![第 n 个元素二进制 e](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-e-1024x339-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-binary-e-1024x339-1.png)

让我们在代码中看到这一点：

```java
return nElementsList2 == 0 ? list1[nElementsList1-1] : max(list1[nElementsList1-1], list2[nElementsList2-1]);
```

请注意，我们不需要处理不从第一个数组中获取任何元素的情况——我们将在稍后处理特殊情况时排除这种情况。

### 4.2. 左右边框的初始值

到目前为止，我们用k和0初始化了第一个数组的左右边界：

```java
int right = k;
int left = 0;
```

然而，根据k的值，我们需要调整这些边界。

首先，如果k超过了第一个数组的长度，我们需要取最后一个元素作为右边界。这样做的原因很简单，因为我们不能从数组中获取比现有元素更多的元素。

其次，如果k大于第二个数组中元素的数量，我们肯定知道我们至少需要从第一个数组中取出(k – length(list2)) 。例如，假设k = 7。由于第二个数组只有四个元素，我们知道我们至少需要从第一个数组中取出3 个元素，所以我们可以将L设置为2：

[![第 n 个元素左边框](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-left-border-768x266-1.png)](https://www.baeldung.com/wp-content/uploads/2020/08/nth-element-left-border-768x266-1.png)

下面是自适应左右边框的代码：

```java
// correct left boundary if k is bigger than the size of list2
int left = k < list2.length ? 0 : k - list2.length - 1;

// the inital right boundary cannot exceed the list1
int right = min(k-1, list1.length - 1);
```

### 4.3. 特殊情况的处理

在我们进行实际的二分查找之前，我们可以处理一些特殊情况，使算法稍微不那么复杂并避免异常。这是注解中带有解释的代码：

```java
// we are looking for the minimum value
if(k == 1) {
    return min(list1[0], list2[0]);
}

// we are looking for the maximum value
if(list1.length + list2.length == k) {
    return max(list1[list1.length-1], list2[list2.length-1]);
}

// swap lists if needed to make sure we take at least one element from list1
if(k <= list2.length && list2[k-1] < list1[0]) {
    int[] list1_ = list1;
    list1 = list2;
    list2 = list1_;
}
```

### 4.4. 输入验证

让我们先看看输入验证。为了防止算法失败并抛出，例如NullPointerException或ArrayIndexOutOfBoundsException， 我们要确保三个参数满足以下条件：

-   两个数组都不能为空并且至少有一个元素
-   k必须>= 0并且不能大于两个数组的总长度

这是我们在代码中的验证：

```java
void checkInput(int k, int[] list1, int[] list2) throws NoSuchElementException, IllegalArgumentException {

    if(list1 == null || list2 == null || k < 1) { 
        throw new IllegalArgumentException(); 
    }
 
    if(list1.length == 0 || list2.length == 0) { 
        throw new IllegalArgumentException(); 
    } 

    if(k > list1.length + list2.length) {
        throw new NoSuchElementException();
    }
}
```

### 4.5. 完整代码

这是我们刚刚描述的算法的完整代码：

```java
public static int findKthElement(int k, int[] list1, int[] list2) throws NoSuchElementException, IllegalArgumentException {

    checkInput(k, list1, list2);

    // we are looking for the minimum value
    if(k == 1) {
        return min(list1[0], list2[0]);
    }

    // we are looking for the maximum value
    if(list1.length + list2.length == k) {
        return max(list1[list1.length-1], list2[list2.length-1]);
    }

    // swap lists if needed to make sure we take at least one element from list1
    if(k <= list2.length && list2[k-1] < list1[0]) {
        int[] list1_ = list1;
        list1 = list2;
        list2 = list1_;
    }

    // correct left boundary if k is bigger than the size of list2
    int left = k < list2.length ? 0 : k - list2.length - 1; 

    // the inital right boundary cannot exceed the list1 
    int right = min(k-1, list1.length - 1); 

    int nElementsList1, nElementsList2; 

    // binary search 
    do { 
        nElementsList1 = ((left + right) / 2) + 1; 
        nElementsList2 = k - nElementsList1; 

        if(nElementsList2 > 0) {
            if (list1[nElementsList1 - 1] > list2[nElementsList2 - 1]) {
                right = nElementsList1 - 2;
            } else {
                left = nElementsList1;
            }
        }
    } while(!kthSmallesElementFound(list1, list2, nElementsList1, nElementsList2));

    return nElementsList2 == 0 ? list1[nElementsList1-1] : max(list1[nElementsList1-1], list2[nElementsList2-1]);
}

private static boolean foundCorrectNumberOfElementsInBothLists(int[] list1, int[] list2, int nElementsList1, int nElementsList2) {

    // we do not take any element from the second list
    if(nElementsList2 < 1) {
        return true;
    }

    if(list1[nElementsList1-1] == list2[nElementsList2-1]) {
        return true;
    }

    if(nElementsList1 == list1.length) {
        return list1[nElementsList1-1] <= list2[nElementsList2];
    }

    if(nElementsList2 == list2.length) {
        return list2[nElementsList2-1] <= list1[nElementsList1];
    }

    return list1[nElementsList1-1] <= list2[nElementsList2] && list2[nElementsList2-1] <= list1[nElementsList1];
}
```

## 5. 测试算法

在我们的 GitHub 存储库中，有许多测试用例涵盖了很多可能的输入数组以及许多极端情况。

在这里，我们只指出其中一项测试，它不是针对静态输入数组进行测试，而是将我们的双二分搜索算法的结果与简单的连接排序算法的结果进行比较。输入由两个随机数组组成：

```java
int[] sortedRandomIntArrayOfLength(int length) {
    int[] intArray = new Random().ints(length).toArray();
    Arrays.sort(intArray);
    return intArray;
}
```

以下方法执行一个测试：

```java
private void random() {

    Random random = new Random();
    int length1 = (Math.abs(random.nextInt())) % 1000 + 1;
    int length2 = (Math.abs(random.nextInt())) % 1000 + 1;

    int[] list1 = sortedRandomIntArrayOfLength(length1);
    int[] list2 = sortedRandomIntArrayOfLength(length2);

    int k = (Math.abs(random.nextInt()) + 1) % (length1 + length2);

    int result = findKthElement(k, list1, list2);
    int result2 = getKthElementSorted(list1, list2, k);
    int result3 = getKthElementMerge(list1, list2, k);

    assertEquals(result2, result);
    assertEquals(result2, result3);
}
```

我们可以调用上面的方法来运行大量这样的测试：

```java
@Test
void randomTests() {
    IntStream.range(1, 100000).forEach(i -> random());
}
```

## 六. 总结

在本文中，我们看到了如何在两个排序数组的并集中找到第k个最小元素的几种方法。首先，我们看到了一个简单直接的O(n log n)算法，然后是一个复杂度为O(n)的版本，最后是一个运行时间为O(log n)的算法。

我们看到的最后一个算法是一个很好的理论练习；然而，出于大多数实际目的，我们应该考虑使用前两种算法中的一种，它比对两个数组进行二分查找要简单得多。当然，如果性能有问题，二分查找可能是一种解决方案。
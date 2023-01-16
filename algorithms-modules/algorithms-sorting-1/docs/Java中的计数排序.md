## 1. 概述

[像归并排序](https://www.baeldung.com/java-merge-sort)这样的通用排序算法不对输入做任何假设，因此在最坏的情况下它们无法超越O(n log n) 。相反，计数排序对输入有一个假设，这使其成为线性时间排序算法。

在本教程中，我们将熟悉计数排序的机制，然后用Java实现它。

## 2.计数排序

与大多数经典排序算法相反，计数排序不会通过比较元素对给定输入进行排序。相反，它假定输入元素是 [0, k ]范围内的n 个整数。当 k = O(n) 时， 计数排序将在O(n)时间内运行。

请注意，我们不能将计数排序用作通用排序算法。然而，当输入与这个假设一致时，它会非常快！

### 2.1. 频率阵列

假设我们要对 值在 [0, 5] 范围内的输入数组进行排序：[
](https://www.baeldung.com/wp-content/uploads/2019/09/counts.png)

[![排序数组](https://www.baeldung.com/wp-content/uploads/2019/09/To-Sort-Array.png)](https://www.baeldung.com/wp-content/uploads/2019/09/To-Sort-Array.png)

首先，我们应该计算输入数组中每个数字的出现次数。如果我们用数组C表示计数，则 C[i] 表示输入数组中数字i 的频率 ：

[![计数](https://www.baeldung.com/wp-content/uploads/2019/09/counts.png)](https://www.baeldung.com/wp-content/uploads/2019/09/counts.png)

例如，由于 5 在输入数组中出现了 3 次，因此索引 5 的值等于 3。

现在给定数组 C，我们应该确定有多少元素小于或等于每个输入元素。 例如：

-   一个元素小于或等于零，或者说只有一个零值，等于C[0]
-   两个元素小于等于一个，等于 C[0] + C[1]
-   四个值小于等于二，等于 C[0] + C[1] + C[2]

因此，如果我们不断计算 C 中n 个连续元素 的总和，我们可以知道输入数组中有多少元素小于或等于数字 n-1。 无论如何，通过应用这个简单的公式，我们可以 按如下方式更新C ：

[![少于](https://www.baeldung.com/wp-content/uploads/2019/09/less-than.png)](https://www.baeldung.com/wp-content/uploads/2019/09/less-than.png)

### 2.2. 算法

现在我们可以使用辅助数组C 对输入数组进行排序。以下是计数排序的工作原理：

-   它反向迭代输入数组
-   对于每个元素 i， C[i] – 1 表示第 i个元素在排序数组中的位置。这是因为有 C[i] 个元素小于或等于 i
-   然后，它在每一轮结束时递减 C[i] 

为了对示例输入数组进行排序，我们应该首先从数字 5 开始，因为它是最后一个元素。根据C[5]， 有 11 个元素小于或等于数字 5。

因此，5 应该是排序数组中的第 11个元素，因此索引为 10：

[![无标题图](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram.png)

由于我们将 5 移动到排序数组中，因此我们应该递减 C[5]。 倒序的下一个元素是 2。因为有 4 个元素小于或等于 2，所以这个数字应该是排序数组中的第 4个元素：

[![无标题图 1](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-1.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-1.png)

同样，我们可以找到下一个元素 0 的正确位置：

[![无标题图 2](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-2.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-2.png)

如果我们继续反向迭代并适当地移动每个元素，我们最终会得到如下结果：

[![无标题图 3](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-3.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-3.png)

## 3. 计数排序——Java实现

### 3.1. 计算频率阵列

首先，给定一个输入元素数组和 k， 我们应该计算数组 C：

```java
int[] countElements(int[] input, int k) {
    int[] c = new int[k + 1];
    Arrays.fill(c, 0);

    for (int i : input) {
        c[i] += 1;
    }

    for (int i = 1; i < c.length; i++) {
	c[i] += c[i - 1];
    }

    return c;
}
```

让我们分解方法签名：

-   input 表示我们要排序的数字数组
-   输入数组是 [0, k ] 范围内的整数数组——所以k 代表 输入中的最大数
-   返回类型是表示 C 数组的整数数组

下面是countElements 方法的工作原理：

-   首先，我们初始化了 C 数组。由于 [0, k] 范围包含 k+1 个 数字，我们正在创建一个能够包含 k+1 个 数字的数组
-   然后对于 输入中的每个数字， 我们计算该数字的频率
-   最后，我们将连续的元素相加，以了解有多少元素小于或等于特定数字

此外，我们可以验证 countElements 方法是否按预期工作：

```java
@Test
void countElements_GivenAnArray_ShouldCalculateTheFrequencyArrayAsExpected() {
    int k = 5;
    int[] input = { 4, 3, 2, 5, 4, 3, 5, 1, 0, 2, 5 };
    
    int[] c = CountingSort.countElements(input, k);
    int[] expected = { 1, 2, 4, 6, 8, 11 };
    assertArrayEquals(expected, c);
}
```

### 3.2. 对输入数组进行排序

现在我们可以计算频率数组，我们应该能够对任何给定的数字集进行排序：

```java
int[] sort(int[] input, int k) {
    int[] c = countElements(input, k);

    int[] sorted = new int[input.length];
    for (int i = input.length - 1; i >= 0; i--) {
        int current = input[i];
	sorted[c[current] - 1] = current;
	c[current] -= 1;
    }

    return sorted;
}
```

以下是 sort 方法的工作原理：

-   首先，它计算 C 数组
-   然后，它反向迭代输入 数组，并为输入 中的每个元素 找到其在排序数组中的正确位置。输入 中的第 i个 元素 应该是 排序数组中的第C[i]个元素。由于Java数组是零索引的，因此C[i]-1条目是第 C[i]个元素——例如，sorted[5] 是排序数组中的第六个元素 
-   每次我们找到匹配项时，它都会减少相应的C[i] 值

同样，我们可以验证 sort 方法是否按预期工作：

```java
@Test
void sort_GivenAnArray_ShouldSortTheInputAsExpected() {
    int k = 5;
    int[] input = { 4, 3, 2, 5, 4, 3, 5, 1, 0, 2, 5 };

    int[] sorted = CountingSort.sort(input, k);

    // Our sorting algorithm and Java's should return the same result
    Arrays.sort(input);
    assertArrayEquals(input, sorted);
}
```

## 4. 重温计数排序算法

### 4.1. 复杂性分析

大多数经典的排序算法，如[归并排序](https://www.baeldung.com/java-merge-sort)，通过将输入元素相互比较来对任何给定的输入进行排序。 这些类型的排序算法称为 比较排序。在最坏的情况下，比较排序至少需要 O (n log n)来对n 个 元素 进行排序。

另一方面，计数排序不会通过比较输入元素来对输入进行排序，因此它显然不是比较排序算法。

让我们看看对输入进行排序消耗了多少时间：

-   它在O(n+k) 时间内计算 C 数组 ：它在 O(n) 中迭代一个大小为 n 的输入数组，然后 在 O(k)中迭代C—— 所以这将是O(n+k )全部的
-   在计算 C 之后， 它通过迭代输入数组并在每次迭代中执行一些原始操作来对输入进行排序。所以，实际的排序操作需要O(n)

总的来说，计数排序需要 O(n+k) 时间来运行：

```plaintext
O(n + k) + O(n) = O(2n + k) = O(n + k)
```

如果我们假设 k=O(n)， 那么计数排序算法会在线性时间内对输入进行排序。 与通用排序算法相反，计数排序对输入进行了假设，并且执行时间小于 O (n log n) 下限。

###  4.2. 稳定

刚才，我们制定了一些关于计数排序机制的奇特规则，但从未阐明背后的原因。更加具体：

-   为什么我们要反向迭代输入数组？
-   为什么我们 每次使用C[i] 时都要递减它？

让我们从头开始迭代以更好地理解第一条规则。假设我们要对一个简单的整数数组进行排序，如下所示：

[![无题图 4](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-4.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-4.png)

在第一次迭代中，我们应该找到第一个 1 的排序位置：

[![无标题图 5](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-5.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-5.png)

因此，第一次出现的数字 1 获得排序数组中的最后一个索引。跳过数字 0，让我们看看第二次出现的数字 1 会发生什么：

[![无题图 6](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-6.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-6.png)

相同值的元素在输入数组和排序数组中的出现顺序不同，所以从头开始迭代时算法[不稳定。](https://www.baeldung.com/stable-sorting-algorithms)

如果我们 在每次使用后不递减C[i] 值会怎样？让我们来看看：

[![无标题图 2 1](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-2-1.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Untitled-Diagram-2-1.png)

两次出现的数字 1 都在排序数组中排在最后。因此，如果我们在每次使用后不减少 C[i] 值，我们可能会在排序时丢失一些数字！

## 5.总结

在本教程中，首先，我们了解了计数排序的内部工作原理。然后我们用Java实现了这个排序算法，并编写了一些测试来验证它的行为。最后证明了该算法是一个时间复杂度为线性的稳定排序算法。
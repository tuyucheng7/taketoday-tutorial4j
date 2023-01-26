## 1. 问题描述

给定一个随机数数组，将给定数组的所有零移到数组的末尾。例如，如果给定的数组是{1、9、8、4、0、0、2、7、0、6、0}，
则应将其更改为{1、9、8、4、2、7、6、0、0、0}。所有其他元素的顺序应相同。预期时间复杂度为O(n)，额外空间为O(1)。

示例：

```
输入: arr[] = {1, 2, 0, 4, 3, 0, 5, 0};
输出: arr[] = {1, 2, 4, 3, 5, 0, 0, 0};

输入: arr[] = {1, 2, 0, 0, 0, 3, 6};
输出: arr[] = {1, 2, 3, 6, 0, 0, 0};
```

## 2. 算法实现

有很多方法可以解决这个问题。下面是一个简单方法。

从左到右遍历给定的数组arr。遍历时，维护数组中非零元素数量。记为count。对于每个非零元素arr[i]，
将元素放在arr[count]并增加count。完成遍历后，所有非零元素都已移动到前端，并且count为第一个0的索引。
现在我们需要做的就是运行一个循环，使数组从count到结束的所有元素都为零。

以下是上述方法的具体实现。

```java
public class MoveZero {

  public static void pushZeroToEnd(int[] arr, int n) {
    int count = 0; // 总计非0元素的个数
    for (int i = 0; i < n; i++) {
      if (arr[i] != 0)
        arr[count++] = arr[i]; // 如果arr[i]不为0，则将count索引处的值更新为arr[i]
    }
    for (int i = count; i < n; i++) // 将index索引及以后的所有元素设置为0
      arr[i] = 0;
  }
}
```

时间复杂度：O(n)，其中n是输入数组中的元素个数。

辅助空间：O(1)

## 3. 数组分区

方法很简单。我们将使用0作为pivot元素，每当我们看到非零元素时，我们都会将其与pivot元素交换。因此所有非零元素都会在前面。

```java
public class MoveZero {

  public static void pushZeroToEndUsingPartition(int[] arr) {
    int n = arr.length;
    int j = 0;
    for (int i = 0; i < n; i++) {
      if (arr[i] != 0) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        j++;
      }
    }
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)
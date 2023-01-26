## 1. 问题描述

给定长度为N的元素数组，范围从0到N–1。数组中可能不存在所有元素。如果元素不存在，那么数组中将存在-1。重新排列数组，使A[i]=i，如果i不存在，则在该位置显示-1。

示例：

```
输入: arr = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1}
输出: [-1, 1, 2, 3, 4, -1, 6, -1, -1, 9]

输入: arr = {19, 7, 0, 3, 18, 15, 12, 6, 1, 8, 11, 10, 9, 5, 13, 16, 2, 14, 17, 4}
输出: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
```

## 2. 暴力方法

1. 遍历从0到n-1的数字。
2. 现在遍历数组。
3. 如果(i==a[j]) ，则将i位置的元素替换为a[j]位置的元素。
4. 如果有任何元素使用-1代替数字，那么它将被自动替换。
5. 现在，遍历数组并检查if(a[i]!=i) ，如果为true，则将a[i]替换为-1。

以下是上述方法的具体实现：

```java
public class RearrangeArray {

  public static void fixArray(int[] arr, int n) {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i == arr[j]) {
          int temp = arr[i];
          arr[i] = arr[j];
          arr[j] = temp;
          break;
        }
      }
    }
    for (int i = 0; i < n; i++)
      if (i != arr[i])
        arr[i] = -1;
  }
}
```

时间复杂度：O(n<sup>2</sup>)

## 3. 优化方法

1. 遍历数组。
2. 检查是否a[i]=-1，如果是，则忽略它。
3. 如果a[i]!=-1，检查元素a[i]是否在正确的位置(i=a[i])。如果是，则忽略它。
4. 如果a[i]!=-1，并且元素a[i]不在其正确位置(i!=a[i])，则将其放置到正确位置，但有两个条件：
    + 要么a[i]是空的，意味着a[i]=-1，然后只让a[i]=i。
    + 或者a[i]不是空的，意味着a[i]=x，然后int y=x放入a[i]=i。现在，我们需要将y放置到正确的位置，所以从第3步开始重复。

以下是上述方法的具体实现：

```java
public class RearrangeArray {

  public static void fixArrayOptimization(int[] arr, int n) {
    for (int i = 0; i < n; i++) {
      if (arr[i] != -1 && arr[i] != i) {
        int x = arr[i];
        while (arr[x] != x && arr[x] != -1) {
          int y = arr[x];
          arr[x] = x;
          x = y;
        }
        arr[x] = x;
        if (arr[i] != i)
          arr[i] = -1;
      }
    }
  }
}
```

## 4. 使用Set

1. 将数组中存在的所有数字存储到一个集合中。
2. 遍历数组，如果对应的位置元素存在于Set中，则设置A[i]=i，否则A[i]=-1。

以下是上述方法的具体实现。

```java
public class RearrangeArray {

  public static void fixArrayUsingSet(int[] arr, int n) {
    Set<Integer> set = new HashSet<>();
    for (int i = 0; i < n; i++)
      set.add(arr[i]);
    for (int i = 0; i < n; i++) {
      if (set.contains(i))
        arr[i] = i;
      else
        arr[i] = -1;
    }
  }
}
```

## 5. 元素交换

1. 遍历数组中的元素。
2. 如果arr[i] >= 0 && arr[i] != i，则将arr[i]放在i索引处(将arr[i]与arr[arr[i]]交换)。

以下是上述方法的具体实现。

```java
public class RearrangeArray {

  public static void fixArrayUsingSwapElement(int[] arr, int n) {
    for (int i = 0; i < n; ) {
      if (i != arr[i] && arr[i] > 0) {
        int temp = arr[arr[i]];
        arr[arr[i]] = arr[i];
        arr[i] = temp;
      } else
        i++;
    }
  }
}
```

时间复杂度：O(n)
## 1. 问题描述

编写一个程序，给定一个由n个数字组成的数组a[]和另一个数字x，确定a[]中是否存在两个元素，其和为x。

示例：

```
Input: arr[] = {0, -1, 2, -3, 1}
        x= -2
Output: Pair with a given sum -2 is (-3, 1) Valid pair exists
Explanation:  If we calculate the sum of the output,1 + (-3) = -2

Input: arr[] = {1, -2, 1, 0, 5}
       x = 0
Output: No valid pair exists for 0
```

## 2. 简单实现

方法：使用简单的逻辑，通过计算数组的元素本身。

```java
public class CheckPair {

  public static boolean checkPair(int[] A, int size, int x) {
    for (int i = 0; i < size - 1; i++) {
      for (int j = i + 1; j < size; j++) {
        if (A[i] + A[j] == x)
          return true;
      }
    }
    return false;
  }
}
```

时间复杂度：O(n<sup>2</sup>)

辅助空间：O(1)

## 3. 排序和双指针

解决此问题的一个棘手方法是使用双指针技术。但要使用双指针技术，必须对数组进行排序。数组排序后，可以使用两个指针l和r，分别指向数组的开始和结束索引。

如果x大于这两个指针所在元素的和，则向右移动l指针；如果两个指针所在元素大于x，则向左移动r指针。让我们通过一个例子来理解这一点。

```
设数组为{1, 4, 45, 6, 10, -8}，x为16

数组排序后
A = {-8, 1, 4, 6, 10, 45}

现在，当r，l元素的和小于x时递增“l”，当r，l元素的和大于x时递减“r”。

这是因为当r,l的和小于x时，为了得到可以增加对总和的数字，开始从左向右移动(也对数组排序)，从而得到“l++”，反之亦然。
初始l = 0, r = 5 
A[l] + A[r] ( -8 + 45) > 16 递减r。现在r=4
A[l] + A[r] ( -8 + 10) 递增l。现在l=1
A[l] + A[r] ( 1 + 10) 递增l。现在l=2
A[l] + A[r] ( 4 + 10) 递增l。现在l=3
A[l] + A[r] ( 6 + 10) == 16 ===> Found candidates (return 1)
```

注意：如果有多个匹配的，则此算法只报告一个。可以很容易地为此扩展。

算法：

1. hasArrayTwoCandidates (A[], ar_size, sum)
2. 升序排序数组
3. 初始化两个索引变量以查找排序数组中的元素。
    1. 首先初始化到最左边的索引：l=0
    2. 初始化第二个最右边的索引：r=ar_size-1
4. while循环 l< r.
    1. If (A[l] + A[r] == sum) 返回1
    2. Else if( A[l] + A[r] < sum ) l++
    3. Else r--
5. 整个数组中没有匹配的-返回0

以下是上述方法的具体实现：

```java
public class CheckPair {

  public static boolean checkPairUsingTwoPointer(int[] A, int arr_size, int sum) {
    int l = 0;
    int r = arr_size - 1;
    Arrays.sort(A);
    while (l < r) {
      if (A[l] + A[r] == sum)
        return true;
      if (A[l] + A[r] < sum)
        l++;
      else
        r--;
    }
    return false;
  }
}
```

复杂度分析：

+ 时间复杂度：取决于我们使用的排序算法。
    + 如果使用归并排序或堆排序，则在最坏的情况下为(-)(nlogn)。
    + 如果使用快速排序，则在最坏情况下为O(n<sup>2</sup>)。
+ 辅助空间：这也取决于排序算法。归并排序的辅助空间为O(n)，堆排序的辅助空间为O(1)。

## 4. 哈希

这个问题可以通过使用哈希技术有效地解决。使用hash_set检查当前数组值x(let)，如果存在值target_sum - x，则将其添加到hash_set给出target_sum。这可以在恒定时间内完成。让我们看看下面的例子。

```
arr[] = {0, -1, 2, -3, 1} 
sum = -2 

现在开始遍历：
步骤1：对于“0”，没有有效的数字“-2”，因此将“0”存储在hash_set中。
步骤2：对于“-1”，没有有效的数字“-1”，因此将“-1”存储在hash_set中。
步骤3：对于“2”，没有有效的数字“-4”，因此将“2”存储在hash_set中。
步骤4：对于“-3”，没有有效的数字“1”，因此将“-3”存储在hash_set中。
步骤5：对于“1”，有一个有效数字“-3”，所以答案是1，-3。
```

算法：

1. 初始一个空的HashSet，记为s
2. 对A[]中的每个元素A[i]执行以下操作
    1. 如果存在了s[x – A[i]]则打印对(A[i], x – A[i])
    2. 将A[i]添加到s中

以下为上述算法的实现：

```java
public class CheckPair {

  public static boolean checkPairUsingHashing(int[] A, int sum) {
    HashSet<Integer> s = new HashSet();
    for (int i = 0; i < A.length; i++) {
      if (s.contains(sum - A[i]))
        return true;
      s.add(A[i]);
    }
    return false;
  }
}
```

复杂度分析：

时间复杂度：O(n)，整个数组只需要遍历一次。

辅助空间：O(n)，HashSet用于存储数组元素。

注意：即使数字的范围包括负数+如果这对数字是由数组中重复出现两次的数字组成的，该解决方案也会起作用，例如：数组=[3,4,3]；配对=(3,3)；targetSum=6。

## 5. 使用小于x元素的余数。

这个想法是计算除以x时有余数的元素，即0到x-1，每个余数分别计算。假设我们有x为6，那么小于6且余数加起来为6的数字相加时的总和为 6。例如，数组中有元素[2, 4]。2 % 6 = 2 + 4 % 6 = 4，这些余数加起来就是6。

像这样，我们必须检查余数为(1,5),(2,4),(3,3)的对。如果我们有一个或多个余数为1的元素和一个或多个余数为5的元素，那么我们肯定得到一个和为6。
这里我们不考虑(0,6)，因为结果对的元素应该小于6。当涉及到(3,3)时，我们必须检查我们是否有两个余数为3的元素，那么我们可以说“存在一个和为x的对”。

算法：

1. 创建大小为x的数组rem。
2. 将rem所有元素初始化为0.
3. 遍历给定的数组
    + 如果arr[i]小于x，执行以下操作：
        + r=arr[i] % x 这样做是为了得到余数。
        + rem[r]=rem[r]+1。即增加除以x时余数为r的元素的数量。
4. 现在，从1到x / 2遍历rem数组。
    + If(rem[i]> 0 and rem[x-i]>0) 退出循环。这意味着我们有一对在相加会产生x。
5. 现在，当我们在上述循环中到达x / 2
    + 如果x是偶数，为了得到一对，我们应该有两个余数为x/2的元素。
        + If rem[x / 2] > 1 返回true，否则返回false。
    + 如果x是奇数，它将有一个单独的对x - x / 2
        + If rem[x/2]>1并且rem[x-x/2]>1 返回true，否则返回false。

上述算法的实现如下：


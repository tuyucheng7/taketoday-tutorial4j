## 1. 问题描述

创建表示两个栈的数据结构TwoStacks。TwoStacks的实现应该只使用一个数组，
即TwoStacks应该使用相同的数组来存储元素。以下函数必须由TwoStacks支持。

+ push1(int x) -> puah x到第1个栈。
+ push2(int x) -> puah x到第2个栈。
+ pop1() –> 从第1个栈中弹出一个元素并返回弹出的元素。
+ pop2() –> 从第2个栈中弹出一个元素并返回弹出的元素。

TwoStacks的实现应该节省空间。

## 2. 实现一(将空间分成两半)

实现TwoStacks的一种简单方法是将数组分成两半，并将半空间分配给两个栈，即，对于stack1使用arr[0]到arr[n/2]，
对于stack2使用arr[(n/2)+1]到arr[n-1]，其中arr[]是用于实现TwoStacks的数组，数组大小为n。

这种方法的问题是对数组空间的使用效率低下。即使arr[]中有可用空间，栈push操作也可能导致栈溢出。
例如，假设数组大小为6，我们将3个元素push到stack1，并且不向stack2 push任何内容。
当我们将第4个元素压入stack1时，即使数组中还有3个元素的空间，也会发生溢出。

```java
public class TwoStacks {
  int[] arr;
  int size;
  int top1, top2;

  public TwoStacks(int n) {
    arr = new int[n];
    size = n;
    top1 = n / 2 + 1;
    top2 = n / 2;
  }

  void push1(int x) {
    if (top1 > 0) {
      top1--;
      arr[top1] = x;
    } else {
      System.out.print("Stack Overflow" + " By element :" + x + "n");
    }
  }

  void push2(int x) {
    if (top2 < size - 1) {
      top2++;
      arr[top2] = x;
    } else {
      System.out.print("Stack Overflow" + " By element :" + x + "n");
    }
  }

  int pop1() {
    if (top1 <= size / 2) {
      int x = arr[top1];
      top1++;
      return x;
    } else {
      System.out.print("Stack UnderFlow");
    }
    return 0;
  }

  int pop2() {
    if (top2 >= size / 2 + 1) {
      int x = arr[top2];
      top2--;
      return x;
    } else {
      System.out.print("Stack UnderFlow");
    }
    return 1;
  }
}
```

+ 时间复杂度：
    + push操作：O(1)
    + pop操作：O(1)
+ 空间复杂度：O(n)：使用数组实现栈。

## 3. 实现二(优化空间)

这种方法有效地利用了可用空间。如果arr[]中有可用空间，则不会导致溢出。这个想法是从arr[]的两端分配栈空间。
stack1从最左边的元素开始，stack1中的第一个元素被压入索引0。stack2从最右边开始，
stack2中的第一个元素被压入索引(n-1)。两个栈都以相反的方向增长(或缩小)。
要检查溢出，我们只需要检查两个栈顶部元素之间的空间，具体实现如下所示。

```java
public class TwoStacksSpaceOptimized {
  int[] arr;
  int size;
  int top1, top2;

  public TwoStacksSpaceOptimized(int n) {
    size = n;
    arr = new int[n];
    top1 = -1;
    top2 = size;
  }

  void push1(int x) {
    if (top1 < top2 - 1) {
      top1++;
      arr[top1] = x;
    } else {
      System.out.println("Stack Overflow");
    }
  }

  void push2(int x) {
    if (top2 - 1 > top1) {
      top2--;
      arr[top2] = x;
    } else {
      System.out.println("Stack Overflow");
    }
  }

  int pop1() {
    if (top1 >= 0) {
      int x = arr[top1];
      top1--;
      return x;
    } else {
      System.out.println("Stack Underflow");
    }
    return 0;
  }

  int pop2() {
    if (top2 < size) {
      int x = arr[top2];
      top2++;
      return x;
    } else {
      System.out.println("Stack Underflow");
    }
    return 0;
  }
}
```

+ 时间复杂度：
    + push操作：O(1)
    + pop操作：O(1)
+ 空间复杂度：O(n)：使用数组实现栈，它是一种空间优化方法。
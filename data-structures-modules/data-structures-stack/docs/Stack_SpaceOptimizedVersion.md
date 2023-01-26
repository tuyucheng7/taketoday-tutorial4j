## 1. 问题描述

设计一个支持所有栈操作的数据结构SpecialStack，如push()、pop()、isEmpty()、isFull()和一个额外的操作getMin()，
它应该从SpecialStack返回最小元素。SpecialStack的所有这些操作都必须是O(1)复杂度。要实现SpecialStack，
你应该只使用标准的Stack数据结构，而不要使用其他数据结构，如数组、列表等等。

示例：

```
考虑以下的SpecialStack
16  --> TOP
15
29
19
18

当调用getMin()时，它应该返回15，这是当前栈中的最小元素。

如果我们在栈上弹出两次，栈变为
29  --> TOP
19
18

当再次调用getMin()时，它应该返回18，这是当前栈中的最小元素。
```

## 2. 算法实现

使用两个栈：一个存储实际栈元素，另一个作为辅助栈存储最小值。其思想是以这样一种方式执行push()和pop()操作，
即辅助栈的顶部总是最小的。让我们看看push()和pop()操作是如何工作的。

+ push(int x)：将元素x插入SpecialStack

1. push x到第一个栈(包含实际元素的栈)
2. 将x与第二个栈(辅助栈)的顶部元素进行比较。设辅助栈的顶部元素为y。
    1. 如果x小于y，则将x push到辅助栈。
    2. 如果x大于y，则将y push到辅助栈。

+ int pop()：从SpecialStack中移除元素并返回移除的元素

1. 从辅助栈中弹出顶部元素。
2. 从实际栈中弹出顶部元素并返回它。步骤1是必要的，以确保辅助栈也为将来的操作进行了更新。

+ int getMin()：返回SpecialStack中的最小元素

1. 返回辅助栈的顶部元素。

我们可以看到，上述所有操作都是O(1)时间复杂度。

让我们看一个例子。让我们假设两个栈最初都是空的，并且18、19、29、15和16被依次插入到SpecialStack中。

```
当我们插入18时，两个栈都变为以下：
实际栈
18 <--- top
辅助栈
18 <--- top

当我们插入19时，两个栈都变为以下：
实际栈
19 <--- top
18
辅助栈
18 <--- top
18

当我们插入29时，两个栈都变为以下：
实际栈
29 <--- top
19
18
辅助栈
18 <--- top
18
18

当我们插入15时，两个栈都变为以下：
实际栈
15 <--- top     
29
19 
18

辅助栈
15 <--- top
18
18
18

当我们插入16时，两个栈都变为以下：
实际栈
16 <--- top     
15
29
19 
18

辅助栈
15 <--- top
15
18
18
18
```

以下是SpecialStack类的实现。在下面的实现中，SpecialStack继承自Stack，并有一个Stack对象min作为辅助栈。

```java
public class SpecialStack extends Stack<Integer> {
  Stack<Integer> min = new Stack<>();

  void push(int x) {
    if (isEmpty()) {
      super.push(x);
      min.push(x);
    } else {
      super.push(x);
      Integer y = min.pop();
      min.push(y);
      if (x < y)
        min.push(x);
      else
        min.push(y);
    }
  }

  public Integer pop() {
    Integer x = super.pop();
    min.pop();
    return x;
  }

  int getMin() {
    Integer x = min.pop();
    min.push(x);
    return x;
  }
}
```

+ 时间复杂度：
    + 对于插入操作：O(1)：push元素到栈中时间恒定。
    + 对于删除操作：O(1)：从栈中pop元素时间恒定。
    + 对于getMin操作：O(1)：因为我们使用了一个辅助栈，它的顶部是最小元素。
+ 空间复杂度：O(n)：使用辅助栈存储最小值。

## 3. 空间优化

上述方法可以优化。我们可以限制辅助栈中的元素数量。只有当实际栈的传入元素小于或等于辅助栈的顶部时，
我们才能push。同样在弹出时，如果弹出元素等于辅助栈的顶部，则移除辅助栈的顶部元素。以下是push()和pop()的修改实现。

```java
public class SpecialStack extends Stack<Integer> {
  Stack<Integer> min = new Stack<>();

  void push(int x) {
    if (isEmpty()) {
      super.push(x);
      min.push(x);
    } else {
      super.push(x);
      Integer y = min.pop();
      min.push(y);
      if (x <= y)
        min.push(x);
    }
  }

  public Integer pop() {
    Integer x = super.pop();
    Integer y = min.pop();
    if (x != y)
      min.push(y);
    return x;
  }

  int getMin() {
    Integer x = min.pop();
    min.push(x);
    return x;
  }
}
```

+ 时间复杂度：
    + 对于插入操作：O(1)：push元素到栈中时间恒定。
    + 对于删除操作：O(1)：从栈中pop元素时间恒定。
    + 对于getMin操作：O(1)：因为我们使用了一个辅助栈，它的顶部是最小元素。
+ 空间复杂度：O(n)：最坏情况下的复杂度与前一种方法相同，但在其他情况下，由于忽略了重复，其占用的空间将略小于上述方法。

## 4. 进一步优化O(1)时间复杂度和O(1)空间复杂度解决方案：

上述方法可以进一步优化，解决方案可以在O(1)时间复杂度和O(1)空间复杂度下工作。这个想法是存储最小元素直到当前插入以及所有元素作为DUMMY_VALUE的提醒，并将实际元素存储为DUMMY_VALUE的倍数。

例如，在将元素“e”压入栈时，将其存储为(e  DUMMY_VALUE + minFoundSoFar)，这样我们就知道在插入“e”时栈中的最小值是多少。

要弹出实际值，只需返回e / DUMMY_VALUE并将新的最小值设置为(minFoundSoFar % DUMMY_VALUE)。

注意：如果我们尝试在栈中插入DUMMY_VALUE，以下方法将失败，因此我们必须仔细选择DUMMY_VALUE。

假设将以下元素插入到栈中：3 2 6 1 8 5

d是dummy value。

s是栈。

top是栈的顶部元素。

min 是插入/删除元素时的最小值。

以下步骤显示了上述变量在任何时刻的当前状态：

```
1. s.push(3)
   min = 3 // 此处栈为空时更新的最小值
   s = {3  d + 3)
   top = (3  d + 3) / d = 3
   
2. s.push(2)
   min = 2 // 将最小值更新为当前元素
   s = {3  d + 3 -> 2  d + 2}
   top = (2  d + 2) / d = 2
   
3. s.push(6)
   min = 2
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2}
   top = (6  d + 2) / d = 6

4. s.push(1);
   min = 1 // 将最小值更新为当期元素
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2 -> 1  d + 1}
   top = (1  d + 1) / d = 1
 
5. s.push(8);
   min = 1
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2 -> 1  d + 1 -> 8  d + 1}
   top = (8  d + 1) / d = 8

6. s.push(5);
   min = 1
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2 -> 1  d + 1 -> 8  d + 1 -> 5  d + 1}
   top = (5  d + 1) / d = 5
   
7. s.pop();
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2 -> 1  d + 1 -> 8  d + 1 -> 5  d + 1}
   top = (5  d + 1) / d = 5
   min = (8  d + 1) % d = 1 // min始终是栈中第二个顶部元素的余数。
   
8. s.pop();
   s = {3  d + 3 -> 2  d + 2-> 6  d + 2 -> 1  d + 1 -> 8  d + 1}
   top = (8  d + 1) / d = 8
   min = (1  d + 1) % d = 1

9. s.pop()
   s = {3  d + 3 -> 2  d + 2 -> 6  d + 2 -> 1  d + 1} 
   top = (1  d + 1) / d = 1
   min = (6  d + 2) % d = 2
   
10. s.pop()
    s = {3  d + 3 -> 2  d + 2 -> 6  d + 2}
    top = (6  d + 2) / d = 6
    min = (2  d + 2) % d = 2

11. s.pop()
    s = {3  d + 3 -> 2  d + 2}
    top = (2  d + 2) / d = 2
    min = (3  d + 3) % d = 3

12. s.pop()
    s = {3  d + 3}
    top = (3  d + 3) / d = 3
    min  = -1 // 因为堆现在为空
```
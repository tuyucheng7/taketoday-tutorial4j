## 1. 概述

在本文中，我们将介绍几种用Java打印三角形的方法。

当然，有许多类型的三角形。在这里，我们将只探索其中的几个：直角三角形和等腰三角形。

## 2. 构建直角三角形

直角三角形是我们要研究的最简单的三角形。让我们快速看一下我们想要获得的输出：

```





```

在这里，我们注意到三角形由5行组成，每行的星数等于当前行数。这个规律可以概括为：对于从1到N的每一行，我们必须打印r个星，其中r是当前行，N是总行数。

那么，让我们使用两个for循环来构建三角形：

```
public static String printARightTriangle(int N) {
  StringBuilder result = new StringBuilder();
  for (int i = 1; i <= N; i++) {
    for (int j = 1; j <= i; j++){
      result.append(" ");
    }
    result.append(System.lineSeparator());
  }
  return result.toString();
}
```

## 3. 构建等腰三角形

现在，让我们看看等腰三角形的形式：

```
    
   
  
 

```

我们注意到，除了星星之外，我们还需要为每一行打印一些空格。因此，我们必须弄清楚每行必须打印多少个空格和星号。当然，空格和星星的数量取决于当前行。

首先，我们看到第一行需要打印4个空格，当我们沿着三角形向下时，我们需要3个空格、2个空格、1个空格，最后一行没有空格。一般来说，我们需要为每行打印N–r个空格。

其次，与第一个例子相比，我们看到这里需要打印奇数个星星：1、3、5、7...

因此，我们需要为每行打印2  r – 1颗星。

### 3.1 使用嵌套for循环

根据以上观察，我们编写第二个示例：

```
public static String printAnIsoscelesTriangle(int N) {
  StringBuilder result = new StringBuilder();
  for (int r = 1; r <= N; r++) {
    for (int sp = 1; sp <= N - r; sp++) {
      result.append(" ");
    }
    for (int c = 1; c <= 2  r - 1; c++) {
      result.append("");
    }
    result.append(System.lineSeparator());
  }
  return result.toString();
}
```

### 3.2 使用单个for循环

实际上，我们有另一种只包含一个for循环的方法--它使用Apache-Commons-Lang3库。

我们将使用for循环来迭代三角形的行，就像我们在前面的示例中所做的那样。然后，我们将使用StringUtils.repeat()方法为每一行生成必要的字符：

```
public static String printAnIsoscelesTriangleUsingStringUtils(int N) {
  StringBuilder result = new StringBuilder();
  for (int r = 1; r <= N; r++) {
    result.append(StringUtils.repeat(' ', N - r));
    result.append(StringUtils.repeat('', 2  r - 1));
    result.append(System.lineSeparator());
  }
  return result.toString();
}
```

或者，我们可以使用subString()方法。

我们可以提取上面的StringUtils.repeat()方法来构建一个辅助字符串，然后在其上使用String.substring()方法。辅助字符串是打印三角形行所需的最大空格数和最大星数的串联。

观察前面的示例，我们注意到第一行最多需要N–1个空格，最后一行最多需要N  2 – 1个星星：

```
String helperString = StringUtils.repeat(' ', N - 1) + StringUtils.repeat('', N  2 - 1);
```

例如，当N=5，r=3时，我们需要打印“  ”，它包含在helperString变量中。我们需要做的就是为subString()方法找到正确的公式。

现在，让我们看看完整的示例：

```
public static String printAnIsoscelesTriangleUsingSubstring(int N) {
  StringBuilder result = new StringBuilder();
  String helperString = StringUtils.repeat(' ', N - 1) + StringUtils.repeat('', N  2 - 1);
  for (int r = 0; r < N; r++) {
    result.append(helperString.substring(r, N + 2  r));
    result.append(System.lineSeparator());
  }
  return result.toString();
}
```

同样，只需稍作一些修改，我们就可以将三角形打印倒过来。

## 4. 复杂度

如果我们再看一下第一个例子，我们会注意到有一个外循环和一个内循环，每个循环最多有N个步骤。
因此，时间复杂度为O(N<sup>2</sup>)，其中N是三角形的行数。

第二个例子很相似，唯一的区别是我们有两个内部循环，它们是连续的，不会增加时间复杂度。

然而，第三个示例只使用了一个包含N步的for循环。但是，在每一步，我们都在辅助字符串上调用StringUtils.repeat()方法或subString()方法，
每个都有O(N)复杂度。因此，整体时间复杂度保持不变。

最后，如果我们讨论的是辅助空间，我们可以很快意识到，对于所有示例，复杂度都存在于StringBuilder变量中。
通过将整个三角形添加到result变量中，我们的复杂度不能低于O(N<sup>2</sup>)。

当然，如果我们直接打印字符，前两个示例的空间复杂度是恒定的。但是，第三个示例使用了辅助字符串，空间复杂度为O(N)。

## 5. 总结

在本文中，我们学习了如何在Java中打印两种常见类型的三角形。

首先，我们研究了直角三角形，这是我们可以在Java中打印的最简单的三角形类型。然后，我们探索了两种构建等腰三角形的方法。
第一个只使用for循环，另一个利用StringUtils.repeat()和String.subString()方法，帮助我们编写更少的代码。

最后，我们分析了每个示例的时间和空间复杂度。
## 1. 简介

在本教程中，我们将探讨在Java中打印三角形的几种方法。

自然地，有许多类型的三角形。在这里，我们将只探索其中的几个：直角三角形和等腰三角形。

## 2. 构建直角三角形

直角三角形是我们要研究的最简单的三角形。让我们快速浏览一下我们想要获得的输出：

```plaintext





```

在这里，我们注意到三角形由 5 行组成，每行的星数等于当前行号。当然，这个观察可以推广：对于从 1 到N的每一行，我们必须打印r星，其中r是当前行，N是总行数。

因此，让我们使用两个for循环构建三角形：

```java
public static String printARightTriangle(int N) {
    StringBuilder result = new StringBuilder();
    for (int r = 1; r <= N; r++) {
        for (int j = 1; j <= r; j++) {
            result.append("");
        }
        result.append(System.lineSeparator());
    }
    return result.toString();
}
```

## 3. 构建等腰三角形

现在，让我们看一下等腰三角形的形状：

```plaintext
    
   
  
 

```

在这种情况下我们看到了什么？我们注意到，除了星星之外，我们还需要为每一行打印一些空格。所以，我们必须弄清楚我们必须为每一行打印多少个空格和星星。当然，空格和星号的数量取决于当前行。

首先，我们看到我们需要为第一行打印 4 个空格，当我们沿着三角形向下打印时，我们需要 3 个空格、2 个空格、1 个空格，最后一行根本不需要空格。一般而言，我们需要为每一行打印N – r个空格。

其次，与第一个例子相比，我们意识到这里需要奇数个星星：1, 3, 5, 7 ...

因此，我们需要为每一行打印rx 2 – 1 stars 。

### 3.1. 使用嵌套for循环

基于以上观察，让我们创建第二个示例：

```java
public static String printAnIsoscelesTriangle(int N) {
    StringBuilder result = new StringBuilder();
    for (int r = 1; r <= N; r++) {
        for (int sp = 1; sp <= N - r; sp++) {
            result.append(" ");
        }
        for (int c = 1; c <= (r  2) - 1; c++) {
            result.append("");
        }
        result.append(System.lineSeparator());
    }
    return result.toString();
}
```

### 3.2. 使用单个for循环

实际上，我们还有另一种 只包含一个for循环的方法——它使用[Apache Commons Lang 3 库](https://www.baeldung.com/java-commons-lang-3)。

我们将使用 for 循环遍历三角形的行，就像我们在前面的示例中所做的那样。然后，我们将使用StringUtils.repeat()方法为每一行生成必要的字符：

```java
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

或者，我们可以使用[substring()](https://www.baeldung.com/java-substring)[方法](https://www.baeldung.com/java-substring)做一个巧妙的技巧。

我们可以提取上面的StringUtils.repeat()方法来构建辅助字符串，然后在其上应用String.substring()方法。辅助字符串是我们打印三角形行所需的最大空格数和最大星数的串联。

查看前面的示例，我们注意到第一行需要最大数量的N-1 个空格，最后一行需要最大数量的N x 2-1 个星号：

```java
String helperString = StringUtils.repeat(' ', N - 1) + StringUtils.repeat('', N  2 - 1);
// for N = 10, helperString = "    "
```

例如，当N = 5且r = 3时，我们需要打印包含在helperString变量中的“”。我们需要做的就是为substring() 方法找到正确的公式。

现在，让我们看看完整的例子：

```java
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

同样，只需多做一点工作，我们就可以将三角形打印倒置。

## 4. 复杂性

如果我们再看一下第一个例子，我们会注意到一个外循环和一个内循环，每个循环都有最多N步。因此，我们有O(N^2)时间复杂度，其中N是三角形的行数。

第二个例子类似——唯一的区别是我们有两个内部循环，它们是顺序的并且不会增加时间复杂度。

然而，第三个示例仅使用了一个包含N个步骤的for循环。但是，在每一步中，我们都在辅助字符串上调用StringUtils.repeat()方法或substring()方法，每个方法都具有O(N)复杂度。因此，总体时间复杂度保持不变。

最后，如果我们谈论辅助空间，我们可以很快意识到，对于所有示例，复杂性都在[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)变量中。通过将整个三角形添加到结果变量，我们的复杂度不能低于O(N^2) 。

当然，如果我们直接打印字符，那么前两个示例的空间复杂度将保持不变。但是，第三个示例使用辅助字符串，空间复杂度为O(N)。

## 5.总结

在本教程中，我们学习了如何在Java中打印两种常见的三角形。

首先，我们研究了直角三角形，这是我们可以用Java打印的最简单的三角形类型。然后，我们探索了两种构建等腰三角形的方法。第一个只使用for循环，另一个利用StringUtils.repeat()和String.substring()方法，帮助我们编写更少的代码。

最后，我们分析了每个示例的时间和空间复杂度。
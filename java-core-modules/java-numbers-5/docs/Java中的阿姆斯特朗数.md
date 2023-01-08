## 1. 概述

在本快速教程中，我们将了解什么是 Armstrong 编号以及如何通过创建Java程序来检查和查找它们。

## 二、问题简介

首先，让我们了解什么是阿姆斯壮数。

给定一个 n 位正整数i ，如果 其各位数字的n 次方之和等于 i ，则该整数i是阿姆斯特朗数。Armstrong 编号形成[OEIS 序列 A005188](https://oeis.org/A005188#:~:text=A005188 - OEIS&text=(Greetings from The On-Line Encyclopedia of Integer Sequences!)&text=Armstrong (or pluperfect%2C or Plus,th powers of their digits.&text=A finite sequence%2C the 88th and last term being 115132219018763992565095597973971522401.)。

举几个例子可以帮助我们快速理解阿姆斯壮数：

-   1 : pow(1,1) = 1 -> 1 是阿姆斯特朗数。
-   123 : pow(1, 3) + pow(2, 3) + pow(3, 3) = 1 + 8 +27 = 36 != 123 -> 123 不是阿姆斯特朗数。
-   1634 : pow(1, 4) + pow(6, 4) + pow(3, 4) + pow(4, 4) = 1 + 1296 + 81 + 256 = 1643 -> 1634 是阿姆斯特朗数。

因此，我们希望有一个Java程序来方便地检查给定的数字是否是阿姆斯壮数字。此外，我们希望生成小于给定限制的 OEIS 序列 A005188。

为简单起见，我们将使用单元测试断言来验证我们的方法是否按预期工作。

## 三、解决问题的思路

现在我们了解了阿姆斯特朗数，让我们研究这个问题并考虑解决它的想法。

首先，生成一个带有极限的 OEIS 序列 A005188 可以转化为从 0 到给定极限并找出所有阿姆斯特朗数。如果我们有一种方法来检查一个整数是否是阿姆斯壮数，那么很容易从整数范围中过滤掉非阿姆斯壮数并得到所需的序列。

因此，首要问题是创建 Armstrong 编号检查方法。一个简单的检查方法是分两步：

-   第 1 步 – 将给定整数分解为数字列表，例如12345 -> [1, 2, 3, 4, 5]
-   步骤 2 – 对于列表中的每个数字，计算pow(digit, list.size())，然后对结果求和，最后将总和与初始给定的整数进行比较

接下来，让我们将想法转化为Java代码。

## 4. 创建阿姆斯壮数法

正如我们所讨论的，让我们首先将给定的整数转换为数字列表：

```java
static List<Integer> digitsInList(int n) {
    List<Integer> list = new ArrayList<>();
    while (n > 0) {
        list.add(n % 10);
        n = n / 10;
    }
    return list;
}

```

如上面的代码所示，我们 在[while](https://www.baeldung.com/java-while-loop)循环中从n中提取数字。在每一步中，我们通过n % 10取一个数字，然后将数字缩小n = n / 10。

或者，我们可以将数字转换为字符串并使用[split()](https://www.baeldung.com/string/split)方法获取字符串中的数字列表。然后，最后，我们可以再次将每个数字转换回整数。在这里，我们没有采用这种方法。

现在我们已经创建了 check 方法，我们可以进入第 2 步：pow()计算和求和：

```java
static boolean isArmstrong(int n) {
    if (n < 0) {
        return false;
    }
    List<Integer> digitsList = digitsInList(n);
    int len = digitsList.size();
    int sum = digitsList.stream()
      .mapToInt(d -> (int) Math.pow(d, len))
      .sum();
    return n == sum;
}

```

正如我们在 isArmstrong() 检查方法中看到的那样，我们使用了Java[Stream](https://www.baeldung.com/java-8-streams)的mapToInt()方法将每个数字转换为 pow()计算后的结果，然后 将列表中的结果[求和](https://www.baeldung.com/java-stream-sum#using-intstreamsum)。

最后，我们将总和与初始整数进行比较，以确定该数字是否为阿姆斯特朗数。

值得一提的是，我们也可以将mapToInt()和sum()方法调用合并为一个 [reduce()](https://www.baeldung.com/java-stream-reduce)调用：

```java
int sum = digits.stream()
  .reduce(0, (subtotal, digit) -> subtotal + (int) Math.pow(digit, len));
```

接下来，让我们创建一个方法来生成 OEIS 序列 A005188 到一个限制：

```java
static List<Integer> getA005188Sequence(int limit) {
    if (limit < 0) {
        throw new IllegalArgumentException("The limit cannot be a negative number.");
    }
    return IntStream.range(0, limit)
      .boxed()
      .filter(ArmstrongNumberUtil::isArmstrong)
      .collect(Collectors.toList());
}

```

正如我们在上面的代码中看到的，我们再次使用 Stream API 来过滤 Armstrong 号码并生成序列。

## 5. 测试

现在，让我们创建一些测试来验证我们的方法是否按预期工作。首先，让我们从一些测试数据开始：

```java
static final Map<Integer, Boolean> ARMSTRONG_MAP = ImmutableMap.of(
  0, true,
  1, true,
  2, true,
  153, true,
  370, true,
  407, true,
  42, false,
  777, false,
  12345, false);

```

现在，让我们将上面Map中的每个数字传递给我们的检查方法，看看是否返回预期结果：

```java
ARMSTRONG_MAP.forEach((number, result) -> assertEquals(result, ArmstrongNumberUtil.isArmstrong(number)));

```

如果我们运行测试，它就会通过。因此，检查方法可以正确完成工作。

接下来，让我们准备两个预期序列并测试getA005188Sequence()是否也按预期工作：

```java
List<Integer> A005188_SEQ_1K = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 153, 370, 371, 407);
List<Integer> A005188_SEQ_10K = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 153, 370, 371, 407, 1634, 8208, 9474);

assertEquals(A005188_SEQ_1K, ArmstrongNumberUtil.getA005188Sequence(1000));
assertEquals(A005188_SEQ_10K, ArmstrongNumberUtil.getA005188Sequence(10000));

```

如果我们试一试，我们的测试就会通过。

## 六，总结

在本文中，我们讨论了阿姆斯特朗数是什么。此外，我们还创建了方法来检查整数是否为阿姆斯壮数，并生成达到给定限制的 OEIS 序列 A005188。
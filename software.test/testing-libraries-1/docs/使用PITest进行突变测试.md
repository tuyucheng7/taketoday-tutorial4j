## 1. 概述

软件测试是指用于评估软件应用程序功能的技术。在本文中，我们将讨论软件测试行业中使用的一些指标，例如**代码覆盖率**和**突变测试**，并对如何使用[PITest库](http://pitest.org/)执行突变测试进行简要介绍。

为了简单起见，我们将基于一个基本的回文函数进行此演示。请注意，回文是一个向后和向前读取内容一致的字符串。

## 2. Maven依赖

正如你在Maven依赖项配置中看到的那样，我们将使用JUnit运行我们的测试，并使用**PITest**库将**突变体**引入我们的代码中-别担心，我们稍后会看到什么是突变体。你始终可以通过[此链接](https://central.sonatype.com/artifact/org.pitest/pitest-parent/1.11.4)针对Maven中央仓库查找最新的依赖项版本。

```xml
<dependency>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-parent</artifactId>
    <version>1.1.10</version>
    <type>pom</type>
</dependency>
```

为了启动并运行PITest库，我们还需要在pom.xml配置文件中包含pitest-maven插件：

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.1.10</version>
    <configuration>
        <targetClasses>
            <param>cn.tuyucheng.taketoday.mutation.*</param>
        </targetClasses>
        <targetTests>
            <param>cn.tuyucheng.taketoday.mutation.*</param>
        </targetTests>
    </configuration>
</plugin>
```

## 3. 项目设置

现在我们已经配置了Maven依赖项，让我们定义一个isPalindrome方法，它的作用的是检查一个字符串是否为回文：

```java
public boolean isPalindrome(String inputString) {
    if (inputString.length() == 0) {
        return true;
    } else {
        char firstChar = inputString.charAt(0);
        char lastChar = inputString.charAt(inputString.length() - 1);
        String mid = inputString.substring(1, inputString.length() - 1);
        return (firstChar == lastChar) && isPalindrome(mid);
    }
}
```

我们现在需要的只是一个简单的JUnit测试，以确保我们的实现以预期的方式工作：

```java
@Test
public void whenPalindrom_thenAccept() {
    Palindrome palindromeTester = new Palindrome();
    assertTrue(palindromeTester.isPalindrome("noon"));
}
```

到目前为止一切顺利，我们已准备好将测试用例作为JUnit测试成功运行。

接下来，在本文中，我们将重点介绍使用PITest库的**代码和突变覆盖率**。

## 4. 代码覆盖率

[代码覆盖率](https://www.baeldung.com/cs/code-coverage)已在软件行业中广泛使用，以衡量在自动化测试期间代码**执行路径**的百分比。

我们可以使用Eclipse IDE上提供的[Eclemma](http://www.eclemma.org/index.html)等工具根据执行路径来衡量有效的代码覆盖率。或者如果你使用Intellij IDEA，则它可以通过自带的生成代码覆盖率的方式运行测试。

在运行具有代码覆盖率的TestPalindrome之后，我们可以轻松地实现100%的覆盖率分数-请注意isPalindrome是递归的，因此很明显空输入长度检查无论如何都会被覆盖。

不幸的是，代码覆盖率指标有时可能非常无效，因为100%的代码覆盖率分数仅意味着所有代码行都至少执行了一次，但它没有说明**测试的准确性或用例完整性**。因此，这就是突变测试的意义所在。

## 5. 突变覆盖率

突变测试是一种用于**提高测试的充分性和识别代码中缺陷**的测试技术。这个想法是动态更改生产代码并导致测试失败。

> 好的测试应该失败

代码中的每个更改都称为**突变体**，它会导致程序的更改版本，称为**突变**。

我们说，如果突变可能导致测试失败，则**将其杀死**。我们还说，如果突变体不能影响测试的行为，那么突变体就会存活下来。

现在让我们使用Maven运行测试，goal选项设置为：org.pitest:pitest-maven:mutationCoverage。

我们可以在**target/pit-test/YYYYMMDDHHMI**目录下查看HTML格式的报告：

- 100%路径覆盖率：7/7
- 57%突变覆盖率：4/7

显然，我们的测试扫描了所有的执行路径，因此路径覆盖率是100%。另一方面，PITest库引入了**7个突变体**，其中会导致失败的4个突变体被杀死，但有3个幸存下来。

我们可以查看**cn.tuyucheng.taketoday.mutation/Palindrome.java.html**报告以获取有关创建的突变体的更多详细信息：

<img src="../assets/img_10.png">

------



------

这些是运行突变覆盖率测试时默认**处于活动状态的突变体**：

- INCREMENTS_MUTATOR
- VOID_METHOD_CALL_MUTATOR
- RETURN_VALS_MUTATOR
- MATH_MUTATOR
- NEGATE_CONDITIONALS_MUTATOR
- INVERT_NEGS_MUTATOR
- CONDITIONALS_BOUNDARY_MUTATOR

有关PITest突变体的更多详细信息，你可以查看官方[文档页面](http://pitest.org/quickstart/mutators/)链接。

我们的突变覆盖率分数反映了**测试用例的缺乏**，因为我们无法确保我们的isPalindrome函数拒绝非回文和近回文字符串输入。

## 6. 提高突变分数

现在我们知道了什么是突变，我们需要通过**杀死幸存的突变体**来提高我们的突变分数。

让我们以第6行的第一个突变“否定条件”为例。突变体幸存下来，因为即使我们更改代码片段：

```java
if (inputString.length() == 0) {
    return true;
}
```

为：

```java
if (inputString.length() != 0) {
    return true;
}
```

测试也会通过，这就是突变存活的原因。这个想法是实施一个新的测试，如果引入了突变体，该测试将失败。对于剩余的突变体也可以这样做。

```java
@Test
public void whenNotPalindrom_thanReject() {
    Palindrome palindromeTester = new Palindrome();
    assertFalse(palindromeTester.isPalindrome("box"));
}

@Test
public void whenNearPalindrom_thanReject() {
    Palindrome palindromeTester = new Palindrome();
    assertFalse(palindromeTester.isPalindrome("neon"));
}
```

现在我们可以使用突变覆盖率插件运行我们的测试，以确保所有突变都被杀死，正如我们在target目录中生成的PITest报告中所看到的那样。

- 100%路径覆盖率：7/7
- 100%突变覆盖率：7/7

## 7. PITest测试配置

突变测试有时可能会占用大量资源，因此我们需要进行适当的配置以提高测试效率。我们可以使用**targetClasses**标签来定义要突变的类列表。突变测试不能应用于现实世界项目中的所有类，因为它既费时又对资源至关重要。

定义你计划在突变测试期间使用的突变体也很重要，以便最大限度地减少执行测试所需的计算资源：

```xml
<configuration>
    <targetClasses>
        <param>cn.tuyucheng.taketoday.mutation.*</param>
    </targetClasses>
    <targetTests>
        <param>cn.tuyucheng.taketoday.mutation.*</param>
    </targetTests>
    <mutators>
        <mutator>CONSTRUCTOR_CALLS</mutator>
        <mutator>VOID_METHOD_CALLS</mutator>
        <mutator>RETURN_VALS</mutator>
        <mutator>NON_VOID_METHOD_CALLS</mutator>
    </mutators>
</configuration>
```

此外，PITest库提供了多种可用于**自定义测试策略的选项**，例如，你可以使用maxMutationsPerClass选项指定类引入的最大突变体数。有关PITest配置的更多详细信息，请参阅官方[Maven快速入门指南](http://pitest.org/quickstart/maven/)。

## 8. 总结

请注意，代码覆盖率仍然是一个重要的指标，但有时它不足以保证代码经过良好的测试。因此，在本文中，我们使用**PITest库**介绍了**突变测试**，作为一种确保测试质量和认可测试用例的更复杂的方法。

我们还了解了如何在提高**突变覆盖率分数**的同时分析基本的PITest报告。

即使突变测试揭示了代码中的缺陷，也应该明智地使用它，因为这是一个**极其昂贵且耗时**的过程。
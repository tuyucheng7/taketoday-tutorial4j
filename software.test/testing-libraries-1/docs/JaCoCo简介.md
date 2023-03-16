## 1. 概述

**代码覆盖率**是一种[软件指标](https://www.baeldung.com/cs/code-coverage)，用于衡量在自动化测试期间执行了多少行代码。

在本教程中，我们将介绍使用**JaCoCo**(Java项目的代码覆盖率报告生成器)的一些实际方面。

## 延伸阅读

### [SonarQube和JaCoCo的代码覆盖率](https://www.baeldung.com/sonarqube-jacoco-code-coverage)

使用SonarQube和JaCoCo测量代码覆盖率的指南。

[阅读更多](https://www.baeldung.com/sonarqube-jacoco-code-coverage)→

### [Jacoco报告的排除](https://www.baeldung.com/jacoco-report-exclude)

了解如何从Jacoco的测试覆盖率报告中排除文件。

[阅读更多](https://www.baeldung.com/jacoco-report-exclude)→

### [Java单元测试的最佳实践](https://www.baeldung.com/java-unit-testing-best-practices)

了解在Java中进行单元测试的最佳实践。

[阅读更多](https://www.baeldung.com/java-unit-testing-best-practices)→

## 2. Maven配置

为了启动并运行JaCoCo，我们需要在我们的pom.xml文件中声明以下[jacoco-maven-plugin](https://central.sonatype.com/artifact/org.jacoco/jacoco-maven-plugin/0.8.8)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.7.7.201606060606</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3. 代码覆盖率报告

在我们开始研究JaCoCo的代码覆盖能力之前，我们需要有一个代码示例。下面是一个简单的Java方法，用于检查字符串是否为回文：

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

现在我们只需要一个简单的JUnit测试：

```java
@Test
public void whenEmptyString_thenAccept() {
    Palindrome palindromeTester = new Palindrome();
    assertTrue(palindromeTester.isPalindrome(""));
}
```

使用JUnit运行测试将自动启动JaCoCo代理。它将在target目录target/jacoco.exec中创建**二进制格式**的覆盖率报告。

显然，我们不能人为地解释这种格式输出，但其他工具和插件可以，例如[SonarQube](https://docs.sonarqube.org/latest/analysis/coverage/)。

好消息是我们可以使用jacoco:report目标来生成多种格式的可读代码覆盖率报告，例如HTML、CSV和XML。

例如，我们可以查看target/site/jacoco/index.html页面，看看生成的报告是什么样子的：

<img src="../assets/img_11.png">

按照报告中提供的链接Palindrome.java，我们可以深入了解每个Java类的更详细视图：

<img src="../assets/img_12.png" align="left">

请注意，借助[EclEmma Eclipse插件](http://www.eclemma.org/installation.html)，我们可以在Eclipse中使用JaCoCo以零配置直接管理代码覆盖率。

## 4. 报告分析

我们的报告显示指令覆盖率为21%、分支覆盖率为16%，**圈复杂度**为3/5，依此类推。

JaCoCo在报告中显示的38条指令指的是**字节码指令**，而不是普通的Java代码指令。

JaCoCo报告通过使用带有颜色的菱形作为分支，使用背景颜色表示行，帮助我们直观地分析代码覆盖率：

- **红色菱形**表示在测试阶段没有执行任何分支
- **黄色菱形**表示代码被部分覆盖-某些分支没有被执行
- **绿色菱形**表示测试期间所有分支都已执行

相同的颜色代码适用于背景颜色，但适用于行覆盖率。

JaCoCo主要提供了三个重要的指标：

- **行覆盖率**根据测试调用的Java字节码指令数量反映了已执行的代码量
- **分支覆盖率**显示代码中已执行分支的百分比，通常与if/else和switch语句相关
- **圈复杂度**通过线性组合给出覆盖一段代码中所有可能路径所需的路径数来反映代码的复杂性

举一个简单的例子，如果代码中没有if或switch语句，则圈复杂度将为1，因为我们只需要一个执行路径就可以覆盖整个代码。

通常，圈复杂度反映了为了覆盖整个代码我们需要实施的测试用例的数量。

## 5. 概念分解

**JaCoCo作为Java代理运行，它负责在运行测试时检测字节码**。JaCoCo对每条指令进行钻取，并显示在每次测试期间执行了哪些行。

为了收集覆盖率数据，JaCoCo使用[ASM](http://asm.ow2.org/)进行动态代码检测，并在此过程中从[JVM工具接口](https://docs.oracle.com/en/java/javase/11/docs/specs/jvmti.html)接收事件：

<img src="../assets/img_13.png">

也可以在服务器模式下运行JaCoCo代理。在这种情况下，我们可以以jacoco:dump作为目标运行我们的测试，以启动dump请求。

我们可以通过[官方文档链接](http://www.eclemma.org/jacoco/trunk/doc/implementation.html)来获取有关JaCoCo设计的更多详细信息。

## 6. 代码覆盖率分数

现在我们对JaCoCo的工作原理有了一些了解，让我们提高我们的代码覆盖率分数。

为了实现100%的代码覆盖率，我们需要引入测试来覆盖初始报告中显示的缺失部分：

```java
@Test
public void whenPalindrom_thenAccept() {
    Palindrome palindromeTester = new Palindrome();
    assertTrue(palindromeTester.isPalindrome("noon"));
}
    
@Test
public void whenNearPalindrom_thanReject(){
    Palindrome palindromeTester = new Palindrome();
    assertFalse(palindromeTester.isPalindrome("neon"));
}
```

现在我们有足够的测试来覆盖我们的整个代码，但为了确保这一点，我们运行Maven命令mvn jacoco:report来发布覆盖率报告：

<img src="../assets/img_14.png">

可以看到，我们代码中的所有行/分支/路径都被完全覆盖：

<img src="../assets/img_15.png" align="left">

在现实世界的项目中，随着开发的进一步发展，我们需要跟踪代码覆盖率分数。

JaCoCo提供了一种简单的方法来声明覆盖率应满足的**最低要求**，否则构建将失败。

我们可以通过在pom.xml文件中添加以下check目标来做到这一点：

```xml
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.50</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

如我们所见，我们将行覆盖率的最低分数限制为50%。

jacoco:check目标绑定到verify阶段，因此我们可以运行Maven命令mvn clean verify来检查测试覆盖率是否达到指定的标准，日志将显示如下内容：

```shell
[ERROR] Failed to execute goal org.jacoco:jacoco-maven-plugin:0.7.7.201606060606:check (jacoco-check) on project mutation-testing: Coverage checks have not been met.
```

## 7. 总结

在本文中，我们学习了如何使用JaCoCo Maven插件为Java项目生成代码覆盖率报告。

但请记住，**100%的代码覆盖率并不一定反映测试的有效性**，因为它只反映了测试期间执行的代码量。在之前的文章中我们讨论了[突变测试](https://www.baeldung.com/java-mutation-testing-with-pitest)。与普通代码覆盖率相比，这是一种更复杂的方法来跟踪测试的有效性。
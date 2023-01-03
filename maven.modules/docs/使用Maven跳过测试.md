## 1. 概述

跳过测试通常不是一个好主意，但是，在某些情况下它可能会有用-也许当我们正在开发新代码并希望运行测试未通过或未编译的中间构建时。

只有在这些情况下，我们可能会跳过测试以避免编译和运行它们的开销。当然，请注意，不运行测试可能会导致不良的编码实践。

在本快速教程中，我们将探讨**使用Maven跳过测试的所有可能命令和选项**。

## 2. Maven生命周期

在详细了解如何跳过测试之前，**我们必须了解测试何时编译或运行**。在关于[Maven目标和阶段](https://www.baeldung.com/maven-goals-phases)的文章中，我们更深入地介绍了Maven生命周期的概念，但出于本文的目的，重要的是要知道Maven可以：

1.  忽略测试
2.  编译测试
3.  运行测试

在我们的示例中，我们将使用package阶段，其中包括编译和运行测试。本教程中探讨的选项属于[Maven Surefire插件](https://maven.apache.org/surefire/maven-surefire-plugin/)。

## 3. 使用命令行标志

### 3.1 跳过测试编译

首先，让我们看一个无法编译的测试示例：

```java
@Test
public void thisDoesntCompile() {
    tuyucheng;
}
```

当我们运行命令行命令时：

```bash
mvn package
```

我们会得到一个错误：

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] /Users/taketoday/skip-tests/src/test/java/cn/tuyucheng/taketoday/skiptests/PowServiceTest.java:[11,9] not a statement
[INFO] 1 error
```

因此，让我们探讨如何**跳过测试源的编译阶段**。在Maven中，我们可以使用maven.test.skip标志：

```bash
mvn -Dmaven.test.skip package
```

结果，测试源代码没有被编译，因此也没有被执行。

### 3.2 跳过测试执行

作为第二种选择，让我们看看**如何编译测试文件夹但跳过运行过程**。这对于我们没有更改方法或类的签名但更改了业务逻辑并因此破坏了测试的情况很有用。让我们考虑一个像下面这样的人为设计的测试用例，它总是会失败：

```java
@Test
public void thisTestFails() {
    fail("This is a failed test case");
}
```

由于我们包含了语句fail()，如果我们运行package阶段，构建将失败并出现错误：

```java
[ERROR] Failures:
[ERROR]   PowServiceTest.thisTestFails:16 This is a failed test case
[INFO]
[ERROR] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0
```

**假设我们想跳过运行测试，但我们仍然想编译它们**。在这种情况下，我们可以使用-DskipTests标志：

```java
mvn -DskipTests package
```

打包阶段将成功。此外，在Maven中，有一个专门用于运行集成测试的插件，称为[maven failsafe插件](https://maven.apache.org/surefire-archives/surefire-2.17/maven-failsafe-plugin/examples/skipping-test.html)。-DskipTests将跳过单元测试(surefire)和集成测试(failsafe)的执行。**为了跳过集成测试，我们可以传递-DskipITs系统属性**。

最后，值得一提的是现在已弃用的标志-Dmaven.test.skip.exec也会编译测试类但不会运行它们。

## 4. 使用Maven配置

如果我们需要长时间排除编译或运行测试，我们可以修改pom.xml文件以包含正确的配置。

### 4.1 跳过测试编译

正如我们在上一节中所做的那样，让我们检查一下如何**避免编译测试文件夹**。在这种情况下，我们将使用pom.xml文件，让我们添加以下属性：

```xml
<properties>
    <maven.test.skip>true</maven.test.skip>
</properties>
```

请记住，**我们可以通过在命令行中添加相反的标志来覆盖该值**：

```bash
mvn -Dmaven.test.skip=false package
```

### 4.2 跳过测试执行

同样，作为第二步，让我们探讨**如何构建测试文件夹，但使用Maven配置跳过测试执行**。为此，我们必须使用以下属性配置Maven Surefire插件：

```xml
<properties>
    <tests.skip>true</tests.skip>
</properties>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <skipTests>${tests.skip}</skipTests>
    </configuration>
</plugin>
```

Maven属性tests.skip是我们之前定义的自定义属性，因此，如果我们想执行测试，我们可以覆盖它：

```bash
mvn -Dtests.skip=false package
```

## 4. 总结

在这个快速教程中，我们探讨了Maven提供的所有替代方案，以跳过编译和/或运行测试。

我们浏览了Maven命令行选项和Maven配置选项。
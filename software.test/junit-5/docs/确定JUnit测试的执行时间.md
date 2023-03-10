## 1. 概述

我们的构建经常为我们的项目运行大量自动化测试用例，这些测试包括单元测试和集成测试。**如果测试套件的执行需要很长时间，我们可能希望优化我们的测试代码或定位执行时间过长的测试**。

在本教程中，我们将学习几种确定测试用例和测试套件执行时间的方法。

## 2. JUnit示例

为了演示报告执行时间，让我们使用来自测试金字塔不同层的一些示例测试用例。我们将使用Thread.sleep()模拟测试用例持续时间。

我们将在[JUnit 5](https://www.baeldung.com/junit-5)中实现我们的示例。但是，等效的工具和技术也适用于使用JUnit 4编写的测试用例。

首先，这是一个简单的单元测试：

```java
@Test
void someUnitTest() {
    assertTrue(doSomething());
}

private boolean doSomething() {
    return true;
}
```

其次，我们可能有一个需要更多时间执行的集成测试：

```java
@Test
void someIntegrationTest() throws Exception {
    Thread.sleep(5000);
    assertTrue(doSomething());
}
```

最后，我们可以模拟一个缓慢的端到端用户场景：

```java
@Test
void someEndToEndTest() throws Exception {
    Thread.sleep(10000);
    assertTrue(doSomething());
}
```

在本文的其余部分，**我们将执行这些测试用例并确定它们的执行时间**。

## 3. IDE JUnit Runner

获取JUnit测试执行时间的最快方法是**使用我们的IDE**，由于大多数IDE都带有嵌入式JUnit Runner，因此它们会执行并报告测试结果。

两个最常用的IDE，IntelliJ IDEA和Eclipse，都嵌入了JUnit Runner。

### 3.1 IntelliJ JUnit Runner

Intellij允许我们在Run/Debug Configuration的帮助下执行JUnit测试用例。一旦我们执行了测试，Run工具窗口就会显示测试状态以及执行时间：

<img src="../assets/img_2.png">

由于我们执行了所有三个示例测试用例，因此我们**可以看到总执行时间以及每个测试用例所花费的时间**。

我们可能还需要保存此类报告以供将来参考。**IntelliJ允许我们以HTML或XML格式导出该测试报告**，导出报告功能在上面屏幕截图的工具栏上突出显示。

### 3.2 Eclipse JUnit Runner

**Eclipse也提供了一个嵌入式JUnit Runner**。我们可以执行，在测试结果窗口中查看单个测试用例或整个测试套件的执行时间：

<img src="../assets/img_6.png">

但是，与IntelliJ测试运行器相比，我们无法从 Eclipse 导出报告。

## 4. Maven Surefire插件

[Maven Surefire插件](https://www.baeldung.com/maven-surefire-plugin)用于在构建生命周期的测试(test)阶段执行单元测试。Surefire插件是默认Maven配置的一部分。但是，如果需要使用特定版本或[额外配置](https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html)，我们可以在pom.xml中声明它：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M3</version>
    <configuration>
        <excludes>
            <exclude>**/*IntegrationTest.java</exclude>
        </excludes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.3.2</version>
        </dependency>
    </dependencies>
</plugin>
```

使用Maven进行测试时，可以通过三种方式来获取JUnit测试的执行时间。我们将在接下来的小节中检查每一个。

### 4.1 Maven构建日志

Surefire在构建日志中显示每个测试用例的执行状态和时间：

```shell
[INFO] Running cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 15.003 s 
- in cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest
```

在这里，它显示了测试类中所有三个测试用例的总执行时间。

### 4.2 Surefire测试报告

Surefire插件还会生成.txt和.xml格式的测试执行摘要，这些一般**存放在项目的target目录中**。Surefire遵循两种文本报告的标准格式：

<img src="../assets/img_3.png">

---------------------------------------------------------------------------------------------------

```text
-------------------------------------------------------------------------------
Test set: cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest
-------------------------------------------------------------------------------
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 15.016 s - in cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest
```

和XML报告：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<testsuite xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:noNamespaceSchemaLocation="https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report-3.0.xsd"
           version="3.0"
           name="cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest"
           time="15.016"
           tests="3" errors="0" skipped="0" failures="0">
    <testcase name="someEndToEndTest" classname="cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest"
              time="10.008"/>
    <testcase name="someIntegrationTest" classname="cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest"
              time="5.003"/>
    <testcase name="someUnitTest" classname="cn.tuyucheng.taketoday.execution.time.SampleExecutionTimeUnitTest"
              time="0.001"/>
</testsuite>
```

虽然文本格式更有可读性，但XML格式是机器可读的，并且**可以导入以在HTML和其他工具中进行可视化**。

### 4.3 Surefire HTML报告

我们还可以使用[maven-surefire-report-plugin](https://search.maven.org/search?q=g:org.apache.maven.plugins%20AND%20a:maven-surefire-report-plugin&core=gav)插件在浏览器中查看HTML格式的测试报告：

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.0.0-M3</version>
        </plugin>
    </plugins>
</reporting>
```

我们现在可以执行mvn命令来生成报告：

1. mvn surefire-report:report：执行测试并生成HTML报告
2. mvn site：为上一步生成的HTML添加CSS样式

执行成功后，可以在target/site目录下找到surefire-report.html文件，使用浏览器打开可以浏览测试报告：

<img src="../assets/img_4.png">

该报告显示类或包中所有测试用例的执行时间以及每个测试用例所花费的时间。

## 5. 总结

在本文中，我们讨论了确定JUnit测试执行时间的各种方法，其中最直接的方法是使用我们的IDE的JUnit Runner。

然后，我们使用maven-surefire-plugin以文本、XML和HTML格式存档测试报告。
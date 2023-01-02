## 1. 概述

通常，我们在Maven构建期间使用Maven surefire插件执行测试。本文将介绍如何使用该插件运行单个测试类或测试方法。

## 2. 问题简介

Maven surefire插件的使用非常简单，它只有一个goal，就是test。因此，在默认配置下，**我们可以通过命令"mvn test"来执行项目中的所有测试**。

但有时，我们可能只想要执行一个测试类，或者一个测试方法。在本文中，我们将以JUnit 5作为测试工具，介绍如何实现它。

## 3. 项目案例

为了以更直观的方式显示测试结果，我们创建两个简单的测试类：

```java
class TheFirstUnitTest {
    private static final Logger logger = LoggerFactory.getLogger(TheFirstUnitTest.class);

    @Test
    void whenTestCase_thenPass() {
        logger.info("Running a dummyTest");
    }
}
```

```java
class TheSecondUnitTest {
    private static final Logger logger = LoggerFactory.getLogger(TheSecondUnitTest.class);

    @Test
    void whenTestCase1_thenPrintTest1_1() {
        logger.info("Running When Case1： test1_1");
    }

    @Test
    void whenTestCase1_thenPrintTest1_2() {
        logger.info("Running When Case1： test1_2");
    }

    @Test
    void whenTestCase1_thenPrintTest1_3() {
        logger.info("Running When Case1： test1_3");
    }

    @Test
    void whenTestCase2_thenPrintTest2_1() {
        logger.info("Running When Case2： test2_1");
    }
}
```

在TheFirstUnitTest类中，我们只有一个测试方法，而TheSecondUnitTest类包含四个测试方法。并且所有的方法名称都遵循"when...then..."模式。

为了简单起见，我们只是在每个测试方法输出一行日志，表明该方法被调用。

现在，如果我们运行mvn test，将执行所有测试：

<img src="../assets/img.png">

接下来，我们告诉Maven只执行指定的测试。

## 4. 执行单个测试类

surefire插件提供了一个"test"参数，我们可以使用它来指定要执行的测试类或方法。

**如果我们想要执行单个测试类，我们可以执行命令mvn test -DTest="TestClassName"**。

例如，我们可以将-Dtest="TheFirstUnitTest"传递给mvn命令，以仅执行TheFirstUnitTest：

```java
$ mvn test -Dtest="TheFirstUnitTest"
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest
21:21:33.379 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest - Running a dummyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.063 s - in cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest
[INFO] 
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.341 s
[INFO] Finished at: 2022-11-03T21:21:33+08:00
[INFO] ------------------------------------------------------------------------
```

从输出中可以看到，只执行了我们传递给"test"参数的测试类。

## 5. 执行单个测试方法

此外，**我们可以通过向mvn命令传递-Dtest="TestClassName#TestMethodName"参数，让surefire插件执行单个测试方法**。

现在，我们执行TheSecondUnitTest类中的whenTestCase2_thenPrintTest2_1()方法：

```java
$ mvn test -Dtest="TheSecondUnitTest#whenTestCase2_thenPrintTest2_1"    
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
21:25:25.924 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest - Running When Case2: test2_1
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.06 s - in cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
[INFO] 
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.529 s
[INFO] Finished at: 2022-11-03T21:25:26+08:00
[INFO] ------------------------------------------------------------------------
```

可以看到，这一次只执行了指定的测试方法。

## 6. 关于test参数的更多信息

到目前为止，我们演示了如何通过相应地提供test参数值来执行单个测试类或测试方法。实际上，surefire插件允许我们以不同的格式设置test参数的值，以便更灵活地执行测试。

下面，我们列出一些常用的格式：

+ 按名称执行多个测试类：-Dtest="TestClassName1，TestClassName2，TestClassName3..."
+ 按名称模式执行多个测试类：-Dtest="\*ServiceUnitTest"或者-Dtest="The\*UnitTest，Controller\*Test"
+ 按名称指定多个测试方法：-Dtest="ClassName#method1+method2"
+ 按名称模式指定多个方法名称：-Dtest="ClassName#whenSomethingHappens_\*"

最后，让我们再看一个例子，假设我们只想执行TheSecondUnitTest类中的所有"WhentTestCase1..."方法。

因此，按照我们上面讲解的，我们可以使用-Dtest="TheSecondUnitTest#whenTestCase1\*"参数实现这个目的：

```java
$ mvn test -Dtest="TheSecondUnitTest#whenTestCase1*"
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
21:31:14.047 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest - Running When Case1: test1_1
21:31:14.060 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest - Running When Case1: test1_2
21:31:14.061 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest - Running When Case1: test1_3
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.065 s - in cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
[INFO] 
[INFO] Results:
[INFO]
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.360 s
[INFO] Finished at: 2022-11-03T21:31:14+08:00
[INFO] ------------------------------------------------------------------------
```

从输出中可以看到，只执行了与指定名称模式匹配的三个测试方法。

## 7. 总结

Maven surefire插件提供了一个test参数，它允许我们在选择要执行的测试时有很大的灵活性。在本文中，我们介绍了一些常用的test参数值格式。此外，我们还通过案例说明了如何使用Maven仅运行指定的测试类或测试方法。

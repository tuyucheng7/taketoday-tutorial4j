## 1. 概述

在本教程中，我们将演示**如何直接从Java代码运行JUnit测试**。在某些情况下这种方法可能会派上用场。

如果你是JUnit的新手，或者如果你想升级到JUnit 5，你可以查看我们关于该主题的[许多教程](https://www.baeldung.com/junit)中的一些。

## 2. Maven依赖

我们需要几个基本的依赖来运行JUnit 4和JUnit 5测试：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.9.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-launcher</artifactId>
        <version>1.2.0</version>
    </dependency>
</dependencies>

// for JUnit 4
<dependency> 
    <groupId>junit</groupId> 
    <artifactId>junit</artifactId> 
    <version>4.12</version> 
    <scope>test</scope> 
</dependency>
```

 可以在Maven Central上找到最新版本的[JUnit 4](https://central.sonatype.com/artifact/junit/junit/4.13.2)、[JUnit 5](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.9.2)和[JUnit Platform Launcher](https://central.sonatype.com/artifact/org.junit.platform/junit-platform-launcher/1.9.2)。

## 3. 运行JUnit 4测试

### 3.1 测试场景

对于JUnit 4和JUnit 5，我们将设置一些“占位符”测试类，这足以演示我们的示例：

```java
public class Junit4FirstUnitTest {

    @Test
    public void whenThis_thenThat() {
        assertTrue(true);
    }

    @Test
    public void whenSomething_thenSomething() {
        assertTrue(true);
    }

    @Test
    public void whenSomethingElse_thenSomethingElse() {
        assertTrue(true);
    }
}
```

```java
public class Junit4SecondUnitTest {

    @Test
    public void whenSomething_thenSomething() {
        assertTrue(true);
    }

    public void whenSomethingElse_thenSomethingElse() {
        assertTrue(true);
    }
}
```

当使用JUnit 4时，我们创建测试类，为每个测试方法添加Junit 4中的@Test注解。

我们还可以添加其他有用的注解，例如@Before或@After，但这不在本教程的讨论范围内。

### 3.2 运行单个测试类

要从Java代码运行JUnit 4测试，我们可以使用JUnitCore类(添加了TextListener对象，用于在System.out中显示输出)：

```java
public class RunJunit4TestsFromJava {

    public static void main(String[] args) {
        JUnitCore jUnit = new JUnitCore();
        jUnit.addListener(new TextListener(System.out));
        jUnit.run(Junit4FirstUnitTest.class);
    }
}
```

在控制台上，我们将看到一条非常简单的消息，表明测试成功：

```shell
...
Time: 0.006

OK (3 tests)
```

### 3.3 运行多个测试类

如果我们想使用JUnit 4**指定多个测试类**，我们可以使用与运行单个类相同的代码，只需添加额外的类：

```java
JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));
Result result = junit.run(Junit4FirstUnitTest.class, Junit4SecondUnitTest.class);
resultReport(result);
```

请注意，运行结果存储在JUnit的Result类的一个实例中，我们使用一个简单的工具方法将其打印出来：

```java
public static void resultReport(Result result) {
    System.out.println("Finished. Result: Failures: " +
        result.getFailureCount() + ". Ignored: " +
        result.getIgnoreCount() + ". Tests run: " +
        result.getRunCount() + ". Time: " +
        result.getRunTime() + "ms.");
}
```

### 3.4 运行测试套件

如果我们需要对一些测试类进行分组以便隔离运行它们，我们可以创建一个测试套件类。这只是一个空类，我们使用@Suite.SuiteClasses注解指定该测试套件需要运行的所有类：

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({Junit4FirstUnitTest.class, Junit4SecondUnitTest.class})
public class Junit4TestSuite {
}
```

为了运行这些测试，我们将再次使用与以前相同的代码，这次我们指定的是一个测试套件类：

```java
JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));
Result result = junit.run(Junit4TestSuite.class);
resultReport(result);
```

### 3.5 运行重复测试

JUnit的一个有趣功能是我们可以通过**创建RepeatedTest的实例来重复测试**，这在我们测试随机值或性能检查时非常有用。

在下一个示例中，我们指定Junit4FirstUnitTest运行测试五次：

```java
Test test = new JUnit4TestAdapter(Junit4FirstUnitTest.class);
RepeatedTest repeatedTest = new RepeatedTest(test, 5);

JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));

junit.run(repeatedTest);
```

在这里，我们使用JUnit4TestAdapter作为测试类的包装器。

我们甚至可以通过重复测试以编程方式创建测试套件：

```java
TestSuite mySuite = new ActiveTestSuite();

JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));

mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(Junit4FirstUnitTest.class), 5));
mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(Junit4SecondUnitTest.class), 3));

junit.run(mySuite);
```

## 4. 运行JUnit 5测试

### 4.1 测试场景

对于JUnit 5，我们将使用与之前的演示相同的示例测试类 – JUnit5FirstUnitTest和JUnit5SecondUnitTest，但由于JUnit框架的版本不同，有一些细微差别，例如@Test和断言方法的包。

### 4.2 运行单个测试

要从Java代码运行JUnit 5测试，我们将设置LauncherDiscoveryRequest的一个实例。它使用一个名为LauncherDiscoveryRequestBuilder构建器类，我们必须在其中设置包选择器和测试类名称过滤器，以获取我们想要运行的所有测试类。

然后这个LauncherDiscoveryRequest与Launcher相关联，在执行测试之前，我们还将设置一个测试计划和一个执行监听器。

这两个都将提供有关要执行的测试和结果的信息：

```java
public class RunJUnit5TestsFromJava {
    SummaryGeneratingListener listener = new SummaryGeneratingListener();

    public void runOne() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
              .selectors(selectClass(Junit5FirstUnitTest.class))
              .build();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }
}
```

### 4.3 运行多个测试类

我们可以为LauncherDiscoveryRequest设置选择器和过滤器以运行多个测试类。

让我们看看如何设置包选择器和测试类名过滤器，以获取我们想要运行的所有测试类：

```java
public void runAll() {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectPackage("cn.tuyucheng.taketoday.runfromjava"))
        .filters(includeClassNamePatterns(".*Test"))
        .build();
    Launcher launcher = LauncherFactory.create();
    TestPlan testPlan = launcher.discover(request);
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);
}
```

### 4.4 测试输出

在main()方法中，我们调用我们的类，我们还使用监听器来获取结果详细信息。这次结果存储为TestExecutionSummary。

提取其信息的最简单方法是打印到控制台输出流：

```java
public static void main(String[] args) {
    RunJUnit5TestsFromJava runner = new RunJUnit5TestsFromJava();
    runner.runAll();

    TestExecutionSummary summary = runner.listener.getSummary();
    summary.printTo(new PrintWriter(System.out));
}
```

这将为我们提供测试运行的详细信息：

```shell
Test run finished after 111 ms
[         8 containers found      ]
[         0 containers skipped    ]
[         8 containers started    ]
[         0 containers aborted    ]
[         8 containers successful ]
[         0 containers failed     ]
[        23 tests found           ]
[         0 tests skipped         ]
[        23 tests started         ]
[         0 tests aborted         ]
[        23 tests successful      ]
[         0 tests failed          ]
```

## 5. 总结

在本文中，我们展示了如何从Java代码以编程方式运行JUnit测试，涵盖JUnit 4以及该测试框架的最新JUnit 5版本。
## 1. 概述

在本教程中，我们介绍**如何直接从Java代码运行JUnit测试**，在某些情况下这种方法会派上用场。

## 2. Maven依赖

我们需要一些基本的依赖项来运行JUnit 4和JUnit 5测试：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.8.1</version>
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

## 3. 运行JUnit 4测试

### 3.1 测试场景

对于JUnit 4和JUnit 5，我们创建两个最基本的测试类用于演示：

```java
public class FirstUnitTest {

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
public class SecondUnitTest {

    @Test
    public void whenSomething_thenSomething() {
        assertTrue(true);
    }

    @Test
    public void whensomethingElse_thenSomethingElse() {
        assertTrue(true);
    }
}
```

当使用JUnit 4时，我们创建测试类，为每个测试方法添加@Test注解。我们还可以添加其他有用的注解，例如@Before或@After，但这些不在本教程的讨论范围内。

### 3.2 运行单个测试类

要从Java代码运行JUnit测试，我们可以使用JUnitCore类(添加了TextListener类，用于在System.out中显示输出)：

```java
JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));
junit.run(FirstUnitTest.class);
```

在控制台上，我们会看到一条非常简单的消息，表明测试成功：

```shell
Running one test class:
..
Time: 0.019
OK (2 tests)
```

### 3.3 运行多个测试类

如果我们想用JUnit 4运行多个测试类，我们可以使用与运行单个类相同的代码，并简单地添加额外的类：

```java
JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));

Result result = junit.run(FirstUnitTest.class, SecondUnitTest.class);
resultReport(result);
```

请注意，测试结果存储在JUnit的Result类的一个实例中，我们使用一个简单的工具方法将其打印出来：

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

如果我们需要对一些测试类进行分组以便单独运行它们，我们可以创建一个测试套件类。这只是一个空类，我们在其中使用JUnit注解指定所有类：

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({FirstUnitTest.class, SecondUnitTest.class})
public class MyTestSuite {
}
```

要运行这些测试，我们再次使用与以前相同的代码：

```java
JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));
Result result = junit.run(MyTestSuite.class);
resultReport(result);
```

### 3.5 运行重复测试

JUnit的一个有用特性是我们可以通过创建RepeatedTest的实例来重复执行测试，这在我们测试随机值或性能检查时非常有用。

在下面的示例中，我们从MergeListsTest运行测试五次：

```java
Test test = new JUnit4TestAdapter(FirstUnitTest.class);
RepeatedTest repeatedTest = new RepeatedTest(test, 5);

JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));

junit.run(repeatedTest);
```

在这里，我们使用JUnit4TestAdapter作为测试类的包装器。

我们甚至可以以编程方式创建套件，同时应用重复测试：

```java
TestSuite mySuite = new ActiveTestSuite();

JUnitCore junit = new JUnitCore();
junit.addListener(new TextListener(System.out));

mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(FirstUnitTest.class), 5));
mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(SecondUnitTest.class), 3));

junit.run(mySuite);
```

## 4. 运行JUnit 5测试

### 4.1 测试场景

对于JUnit 5，我们使用与之前演示的相同测试类FirstUnitTest和SecondUnitTest，由于JUnit 5是不同版本的测试框架，因此有一些细微差别，例如@Test和断言方法的包。

### 4.2 运行单个测试类

要从Java代码运行JUnit 5测试，我们需要设置LauncherDiscoveryRequest的一个实例。它使用一个构建器类，我们必须在其中设置包选择器和测试类名称过滤器，以获取我们想要运行的所有测试类。然后这个LauncherDiscoveryRequest与一个启动器Launcher相关联，在执行测试之前，我们还将设置一个TestPlan和一个执行监听器。

这两个都将提供有关要执行的测试和结果的信息：

```java
public class RunJUnit5TestsFromJava {
    SummaryGeneratingListener listener = new SummaryGeneratingListener();

    public void runOne() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(FirstUnitTest.class))
            .build();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }
    // main method...
}
```

### 4.3 运行多个测试类

我们可以为运行多个测试类的LauncherDiscoveryRequest设置选择器和过滤器。

下面是一个设置包选择器和测试类名称过滤器的例子，用于获取我们想要运行的所有测试类：

```java
public void runAll() {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectPackage("cn.tuyucheng.taketoday.junit5.runfromjava"))
        .filters(includeClassNamePatterns(".Test"))
        .build();
    Launcher launcher = LauncherFactory.create();
    TestPlan testPlan = launcher.discover(request);
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);
}
```

### 4.4 测试输出

在main()方法中，我们调用我们的类，并使用监听器来获取结果详细信息，最终测试结果存储为TestExecutionSummary。

提取其信息的最简单方法是打印到控制台输出流：

```java
public static void main(String[] args) {
    RunJUnit5TestsFromJava runner = new RunJUnit5TestsFromJava();
    runner.runAll();

    TestExecutionSummary summary = runner.listener.getSummary();
    summary.printTo(new PrintWriter(System.out));
}
```

这会为我们提供测试运行的详细信息：

```shell
Test run finished after 177 ms
[         7 containers found      ]
[         0 containers skipped    ]
[         7 containers started    ]
[         0 containers aborted    ]
[         7 containers successful ]
[         0 containers failed     ]
[        10 tests found           ]
[         0 tests skipped         ]
[        10 tests started         ]
[         0 tests aborted         ]
[        10 tests successful      ]
[         0 tests failed          ]
```

## 5. 总结

在本文中，我们演示了如何从Java代码以编程方式运行JUnit测试，涵盖JUnit 4以及该测试框架的最新JUnit 5版本。
## 1. 概述

在本教程中，我们将了解**如何直接从命令行运行[JUnit 5测试](https://www.baeldung.com/junit)**。

## 2. 测试场景

之前，我们介绍了如何[以编程方式运行JUnit测试](https://www.baeldung.com/junit-tests-run-programmatically-from-java)。对于我们的示例，我们将使用相同的JUnit测试：

```java
class FirstUnitTest {

    @Test
    void whenThis_thenThat() {
        assertTrue(true);
    }

    @Test
    void whenSomething_thenSomething() {
        assertTrue(true);
    }

    @Test
    void whenSomethingElse_thenSomethingElse() {
        assertTrue(true);
    }
}
```

```java
class SecondUnitTest {

    @Test
    void whenSomething_thenSomething() {
        assertTrue(true);
    }

    @Test
    void whenSomethingElse_thenSomethingElse() {
        assertTrue(true);
    }
}
```

## 3. 运行JUnit 5测试

我们可以**使用JUnit的控制台启动器运行JUnit 5测试用例**。这个jar的可执行文件可以从Maven仓库下载，位于[junit-platform-console-standalone](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/)目录下。

此外，我们还需要一个包含所有已编译类的目录。

```shell
$ mkdir target
```

让我们看看如何使用控制台启动器运行不同的测试用例。

### 3.1 运行单个测试类

在运行测试类之前，让我们先编译它：

```shell
$ javac -d target -cp target:junit-platform-console-standalone-1.7.2.jar src/test/java/cn/tuyucheng/taketoday/commandline/FirstUnitTest.java
```

现在，我们将使用JUnit控制台启动器运行已编译的测试类：

```shell
$ java -jar junit-platform-console-standalone-1.7.2.jar --class-path target --select-class cn.tuyucheng.taketoday.commandline.FirstUnitTest
```

这将为我们提供测试运行结果：

```shell
Test run finished after 60 ms
[         3 containers found      ]
[         0 containers skipped    ]
[         3 containers started    ]
[         0 containers aborted    ]
[         3 containers successful ]
[         0 containers failed     ]
[         3 tests found           ]
[         0 tests skipped         ]
[         3 tests started         ]
[         0 tests aborted         ]
[         3 tests successful      ]
[         0 tests failed          ]
```

### 3.2 运行多个测试类

同样，让我们编译要运行的测试类：

```shell
$ javac -d target -cp target:junit-platform-console-standalone-1.7.2.jar src/test/java/cn/tuyucheng/taketoday/commandline/FirstUnitTest.java src/test/java/cn/tuyucheng/taketoday/commandline/SecondUnitTest.java 
```

现在，我们将使用控制台启动器运行已编译的测试类：

```shell
$ java -jar junit-platform-console-standalone-1.7.2.jar --class-path target --select-class cn.tuyucheng.taketoday.commandline.FirstUnitTest --select-class cn.tuyucheng.taketoday.commandline.SecondUnitTest
```

我们的结果现在表明所有五种测试方法都是成功的：

```shell
Test run finished after 68 ms
...
[         5 tests found           ]
...
[         5 tests successful      ]
[         0 tests failed          ]
```

### 3.3 运行包中的所有测试类

要运行包中的所有测试类，让我们编译包中存在的所有测试类：

```shell
$ javac -d target -cp target:junit-platform-console-standalone-1.7.2.jar src/test/java/cn/tuyucheng/taketoday/commandline/*.java
```

同样，我们将运行包中的已编译测试类：

```shell
$ java -jar junit-platform-console-standalone-1.7.2.jar --class-path target --select-package cn.tuyucheng.taketoday.commandline
...
Test run finished after 68 ms
...
[         5 tests found           ]
...
[         5 tests successful      ]
[         0 tests failed          ]
```

### 3.4 运行所有测试类

让我们运行所有的测试用例：

```shell
$ java -jar junit-platform-console-standalone-1.7.2.jar --class-path target  --scan-class-path
...
Test run finished after 68 ms
...
[         5 tests found           ]
...
[         5 tests successful      ]
[         0 tests failed          ]
```

## 4. 使用Maven运行JUnit

如果我们使用[Maven](https://www.baeldung.com/maven)作为我们的构建工具，我们可以直接从命令行执行测试用例。

### 4.1 运行单个测试用例

要在控制台上运行单个测试用例，让我们通过指定测试类名称来执行以下命令：

```shell
$ mvn test -Dtest=SecondUnitTest 
```

这将为我们提供测试运行结果：

```bash
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.SecondUnitTest 
[INFO] 
[INFO] Results:
[INFO]
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------ 
[INFO] BUILD SUCCESS 
[INFO] ------------------------------------------------------------------------ 
[INFO] Total time: 7.211 s [INFO] Finished at: 2021-08-02T23:13:41+05:30
[INFO] ------------------------------------------------------------------------
```

### 4.2 运行多个测试用例

要在控制台上运行多个测试用例，让我们执行命令，指定我们要执行的所有测试类的名称：

```bash
$ mvn test -Dtest=FirstUnitTest,SecondUnitTest
...
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.SecondUnitTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.FirstUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.211 s
[INFO] Finished at: 2021-08-02T23:13:41+05:30
[INFO] ------------------------------------------------------------------------
```

### 4.3 运行包中的所有测试用例

要在控制台上运行包中的所有测试用例，我们需要将包名称指定为命令的一部分：

```bash
$ mvn test -Dtest="cn.tuyucheng.taketoday.commandline.**"
...
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.SecondUnitTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.FirstUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.211 s
[INFO] Finished at: 2021-08-02T23:13:41+05:30
[INFO] ------------------------------------------------------------------------
```

### 4.4 运行所有测试用例

最后，要在控制台上使用Maven运行所有测试用例，我们只需执行mvn clean test：

```bash
$ mvn clean test
...
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.SecondUnitTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 s - in cn.tuyucheng.taketoday.commandline.FirstUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.211 s
[INFO] Finished at: 2021-08-02T23:13:41+05:30
[INFO] ------------------------------------------------------------------------
```

## 5. 总结

在本文中，我们学习了如何直接从命令行运行JUnit测试，涵盖了使用和不使用Maven的JUnit 5。
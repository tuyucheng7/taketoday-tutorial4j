## 1. 概述

在本教程中，我们将介绍如何直接从命令行运行JUnit 5测试。

## 2. 测试场景

这里使用两个简单的测试类作为演示：

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

我们可以使用JUnit的控制台启动器运行JUnit 5测试用例。这个jar包可以从Maven仓库下载，
位于[junit-platform-console-standalone](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/)
目录下。

此外，我们需要一个包含所有已编译的class文件的目录。

让我们看看如何使用控制台启动器运行不同的测试用例。

### 3.1 运行单个测试类

在运行测试类之前，让我们编译它：


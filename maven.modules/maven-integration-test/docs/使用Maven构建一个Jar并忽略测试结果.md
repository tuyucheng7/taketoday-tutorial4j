## 1. 概述

本快速指南展示了如何在忽略测试结果的情况下使用Maven构建jar。

默认情况下，Maven在构建项目时自动运行单元测试。但是，**在极少数情况下可以跳过测试，无论测试结果如何，我们都需要构建项目**。

## 2. 构建项目

让我们创建一个简单的项目，其中还包括一个小的测试用例：

```java
public class TestFail {
    @Test
    public void whenMessageAssigned_thenItIsNotNull() {
        String message = "hello there";
        assertNotNull(message);
    }
}
```

让我们通过执行以下Maven命令来构建一个jar文件：

```bash
mvn package
```

**这将导致编译源代码并在/target目录下生成一个maven-1.0.0.jar文件**。

现在，让我们稍微改变一下测试，以便测试开始失败。

```java
@Test
public void whenMessageAssigned_thenItIsNotNull() {
    String message = null;
    assertNotNull(message);
}
```

这一次，当我们再次尝试运行`mvn package`命令时，构建失败并且没有创建maven-1.0.0.jar文件。

这意味着，**如果我们的应用程序中有一个失败的测试，除非我们修复测试，否则我们无法提供可执行文件**。

那么我们如何解决这个问题呢？

## 3. Maven参数

Maven有自己的参数来处理这个问题：

-   **`-Dmaven.test.failure.ignore=true`忽略测试执行期间发生的任何失败**
-   **`-Dmaven.test.skip=true`不会编译测试**
-   **`-fn`，`-fae`无论测试结果如何，构建都不会失败**

让我们运行`mvn package -Dmaven.test.skip=true`命令并查看结果：

```bash
[INFO] Tests are skipped.
[INFO] BUILD SUCCESS
```

这意味着将在不编译测试的情况下构建项目。

现在让我们运行`mvn package -Dmaven.test.failure.ignore=true`命令：

```bash
[INFO] Running testfail.TestFail
[ERROR] whenMessageAssigned_thenItIsNotNull java.lang.AssertionError
[INFO] BUILD SUCCESS
```

我们的单元测试在断言时失败，但构建成功。

最后，让我们测试`-fn`和`-fae`选项。`package -fn`和`package -fae`命令都会构建jar文件并生成BUILD SUCCESS输出，而不管whenMessageAssigned_thenItIsNotNull()测试是否失败。

**在多模块项目的情况下，应该使用`-fn`选项。`-fae`继续测试失败的模块，但跳过所有相关模块**。 

## 4. Maven Surefire插件

另一种实现我们目标的简便方法是使用Maven的Surefire插件。

有关Surefire插件的扩展概述，请参阅[本文](https://www.baeldung.com/maven-surefire-plugin)。

**要忽略测试失败，我们可以简单地将<testFailureIgnore\>属性设置为true**：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven.surefire.version}</version>
    <configuration>
        <includes>
            <include>TestFail.java</include>
        </includes>
        <testFailureIgnore>true</testFailureIgnore>
    </configuration>
</plugin>
```

现在，让我们看看`package`命令的输出：

```bash
[INFO]  T E S T S
[INFO] Running testfail.TestFail
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, <<< FAILURE! - in testfail.TestFail
```

从运行的测试输出中，我们可以看到TestFail类失败了。但是进一步看，我们看到BUILD SUCCESS消息也在那里，并且编译了maven-1.0.0.jar文件。

根据我们的需要，我们可以完全跳过运行测试。为此，我们可以将<testFailureIgnore\>一行替换为：

```xml
<skipTests>true</skipTests>
```

或者设置命令行参数-DskipTests，这将编译测试类但完全跳过测试执行。

## 5. 总结

**在这篇文章中，我们学习了如何使用Maven构建我们的项目而不考虑测试结果**，我们通过了跳过失败测试或完全排除测试编译的实际示例。
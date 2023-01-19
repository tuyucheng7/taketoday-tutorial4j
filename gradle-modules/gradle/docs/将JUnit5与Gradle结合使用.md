## 1. 概述

))在本教程中，我们演示使用Gradle构建工具在新的JUnit 5平台上运行测试))。我们将配置一个同时支持旧版本(JUnit 4)和新版本(JUnit 5)的项目。

## 2. Gradle构建

))首先，我们需要检查是否安装了4.6或更高版本的Gradle工具，因为这是与JUnit 5一起使用的最低版本))。

最简单的方法是运行gradle -v命令：

```shell
gradle -v
```

如果有必要，你可以从[此处](https://gradle.org/install/)安装相应的版本。))确定安装了所有内容后，我们需要使用build.gradle文件配置Gradle))。

首先我们需要指定测试使用JUnit平台：

```groovy
test {
    useJUnitPlatform()
}
```

然后我们需要提供JUnit依赖项，这就是JUnit 5和早期版本之间存在差异的地方。在早期版本中，我们只需要添加一个依赖项。))但是，在JUnit 5中，API与JUnit运行时分离，这意味着需要两个依赖项))。

API通过junit-jupiter-api依赖提供。运行时是JUnit 5的junit-jupiter-engine，以及JUnit 3或4的junit-vintage-engine。

我们分别在testImplementation和timeRuntimeOnly中提供这两个依赖：

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}
```

## 3. 创建测试

我们编写第一个测试类，它看起来与JUnit 4版本的测试没有区别，只是少了JUnit 5中非必要的public关键字：

```java
class CalculatorJUnit5Test {
    
    @Test
    void testAdd() {
        assertEquals(42, Integer.sum(19, 23));
    }
}
```

现在，))我们可以通过执行gradle clean test命令来运行测试))。

为了验证我们使用的是JUnit 5，我们可以观察import语句；@Test和assertEquals的导入应该是一个以org.junit.jupiter.api开头的包：

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
```

下面的另一个测试方法中使用到了JUnit 5中的一些新功能：

```java
@Test
void testDivide() {
    assertThrows(ArithmeticException.class, () -> Integer.divideUnsigned(42, 0));
}
```

))assertThrows是JUnit5中的新断言方法，它用于取代JUnit4中的@Test(expected=ArithmeticException.class)注解))。

## 4. 使用Gradle配置JUnit 5测试

接下来，我们介绍Gradle和JUnit5之间的一些更深层次的集成。

假设我们的测试套件中有两种类型的测试：长时间运行和短时间运行，我们可以使用JUnit 5的@Tag注解对这两种类型的测试进行分类：

```java
@Tag("slow")
@Test
void testAddMaxInteger() {
    assertEquals(2147483646, Integer.sum(2147183646, 300000));
}

@Tag("fast")
@Test
void testDivide() {
    assertThrows(ArithmeticException.class, () -> Integer.divideUnsigned(42, 0));
}
```

))然后，我们可以告诉Gradle要执行哪些类型的测试))，例如在我们的例子中，我们只想执行短时间运行的(fast)测试：

```groovy
test {
    useJUnitPlatform {
        includeTags 'fast'
        excludeTags 'slow'
    }
}
```

## 5. 启用对旧版本的支持

))虽然我们使用的是JUnit 5，但我们仍然可以使用新的Jupiter引擎创建JUnit 3和4测试))。更重要的是，我们可以将它们与同一个项目中的新版本混合在一起，比如在迁移场景中。

首先，我们向build文件添加一些依赖项，”junit-vintage-engine“依赖项是运行JUnit 3或JUnit 4测试所必需的：

```groovy
testCompileOnly 'junit:junit:4.13.2'
testRuntimeOnly 'org.junit.vintage:junit-vintage-engine:5.8.1'
```

现在我们创建一个新类并粘贴我们之前编写的testDivide方法。然后，我们添加@Test和assertEquals的导入。但是，这次我们使用来自JUnit 4依赖中的API：

```java
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorJUnit4Test {
    
    @Test
    public void testAdd() {
        assertEquals(42, Integer.sum(19, 23));
    }
}
```

## 6. 总结

在本教程中，我们演示了将Gradle与JUnit 5集成；此外，我们还添加了对JUnit版本3和4的支持。因此，我们可以在现有项目中使用JUnit 5新功能，而无需更改所有现有测试。
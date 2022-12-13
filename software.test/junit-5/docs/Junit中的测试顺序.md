## 1. 概述

**默认情况下，**JUnit使用确定性但不可预测的顺序**(MethodSorters.DEFAULT)运行测试**。

在大多数情况下，这种行为是完全可以接受的。但是在某些情况下，我们需要强制测试以特定的顺序执行。

## 2. JUnit 5中的测试顺序

在JUnit 5中，我们可以使用@TestMethodOrder注解来控制测试的执行顺序，该注解可以指定一个MethodOrderer的实现类。

我们可以定义自己的MethodOrderer，或者我们可以选择三个内置排序器之一：

1. 字母数字顺序
2. @Order注解
3. 随机顺序

### 2.1 使用字母数字顺序

JUnit 5带有一组内置的MethodOrderer实现，可以按字母数字顺序运行测试。

例如，**它提供了MethodOrderer.MethodName以根据测试方法的名称和形式参数列表对测试方法进行排序**：

```java
class AlphanumericOrderUnitTest {
    private static final StringBuilder output = new StringBuilder("");

    @Test
    void myATest() {
        output.append("A");
    }

    @Test
    void myBTest() {
        output.append("B");
    }

    @Test
    void myaTest() {
        output.append("a");
    }

    @AfterAll
    static void assertOutput() {
        assertEquals("ABa", output.toString());
    }
}
```

类似地，我们可以使用**MethodOrderer.DisplayName根据方法的显示名称以字母数字方式对其排序**。

MethodOrderer.Alphanumeric是另一种选择。但是，此实现在JUnit5 5.7版本已被弃用，并将在6.0中删除。

### 2.2 使用@Order注解

我们可以使用@Order注解来强制测试以特定顺序运行。

在下面的示例中，首先将运行firstTest()，然后是secondTest()，最后运行thirdTest()：

```java
@TestMethodOrder(OrderAnnotation.class)
class OrderAnnotationUnitTest {

    private static final StringBuilder output = new StringBuilder();

    @Test
    @Order(1)
    void firstTest() {
        output.append("a");
    }

    @Test
    @Order(2)
    void secondTest() {
        output.append("b");
    }

    @Test
    @Order(3)
    void thirdTest() {
        output.append("c");
    }

    @AfterAll
    static void assertOutput() {
        assertEquals("abc", output.toString());
    }
}
```

### 2.3 使用随机顺序

我们还可以使用MethodOrderer.Random实现对测试方法进行伪随机排序：

```java
@TestMethodOrder(MethodOrderer.Random.class)
class RandomOrderUnitTest {

    private static final StringBuilder output = new StringBuilder();

    @Test
    void myATest() {
        output.append("A");
    }

    @Test
    void myBTest() {
        output.append("B");
    }

    @Test
    void myCTest() {
        output.append("C");
    }

    @AfterAll
    static void assertOutput() {
        assertEquals("ACB", output.toString());
    }
}
```

**事实上，JUnit 5使用System.nanoTime()作为默认种子来对测试方法进行排序。这意味着在可重复测试中，方法的执行顺序可能不同**。

但是，我们可以使用junit.jupiter.execution.order.random.seed属性配置自定义种子来创建可重复测试的生成。

我们可以在junit-platform.properties文件中指定自定义种子的值：

```properties
#src/test/resources/junit-platform.properties
junit.jupiter.execution.order.random.seed=100
```

### 2.4 自定义顺序

最后，**我们可以通过实现MethodOrderer接口来使用我们自己的自定义顺序**。

在我们的CustomOrder中，我们将根据测试名称以不区分大小写的字母数字顺序对测试进行排序：

```java
public class CustomOrder implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort((MethodDescriptor m1, MethodDescriptor m2) ->
                m1.getMethod().getName().compareToIgnoreCase(m2.getMethod().getName()));
    }
}
```

然后我们将使用CustomOrder以myATest()、myaTest()和myBTest()的顺序运行与前面示例中的相同测试：

```java
@TestMethodOrder(CustomOrder.class)
class CustomOrderUnitTest {

    private static final StringBuilder output = new StringBuilder("");

    @Test
    void myATest() {
        output.append("A");
    }

    @Test
    void myBTest() {
        output.append("B");
    }

    @Test
    void myaTest() {
        output.append("a");
    }

    @AfterAll
    static void assertOutput() {
        assertEquals("AaB", output.toString());
    }
}
```

### 2.5 设置默认的顺序

JUnit 5提供了一种通过junit.jupiter.testmethod.order.default参数设置默认方法排序器的便捷方式。

同样，我们可以在junit-platform.properties文件中配置我们的参数：

```properties
#src/test/resources/junit-platform.properties
junit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$DisplayName
```

**默认排序器将作用于所有没有显示使用@TestMethodOrder注解的测试类**。

另一个重要的点是，指定的类必须实现MethodOrderer接口。

## 3. JUnit 4中的测试顺序

对于那些仍在使用JUnit 4的用户，用于排序测试的API略有不同。

### 3.1 使用MethodSorters.Default

此默认策略使用其哈希码比较测试方法，在哈希冲突的情况下，使用字典顺序：

```java
@FixMethodOrder(MethodSorters.DEFAULT)
public class DefaultOrderOfExecutionUnitTest {
    private static final StringBuilder output = new StringBuilder();

    @Test
    public void secondTest() {
        output.append("b");
    }

    @Test
    public void thirdTest() {
        output.append("c");
    }

    @Test
    public void firstTest() {
        output.append("a");
    }

    @AfterClass
    public static void assertOutput() {
        assertEquals(output.toString(), "cab");
    }
}
```

当我们运行上面的测试类时，我们可以看到测试通过。

### 3.2 使用MethodSorters.JVM

另一种排序策略是MethodSorters.JVM，**此策略采用自然的JVM排序，每次运行可能不同**：

```java
@FixMethodOrder(MethodSorters.JVM)
public class JVMOrderOfExecutionUnitTest {

    private static final StringBuilder output = new StringBuilder();

    @Test
    public void secondTest() {
        output.append("b");
    }

    @Test
    public void thirdTest() {
        output.append("c");
    }

    @Test
    public void firstTest() {
        output.append("a");
    }
}
```

每次我们在这个类中执行测试时，都会得到不同的结果。

### 3.3 使用MethodSorters.NAME_ASCENDING

最后，该策略可用于按字典顺序运行测试：

```java
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NameAscendingOrderOfExecutionUnitTest {
    private static final StringBuilder output = new StringBuilder();

    @AfterClass
    public static void assertOutput() {
        assertEquals(output.toString(), "abc");
    }

    @Test
    public void secondTest() {
        output.append("b");
    }

    @Test
    public void thirdTest() {
        output.append("c");
    }

    @Test
    public void firstTest() {
        output.append("a");
    }
}
```

## 4. 总结

在这篇文章中，我们介绍了在JUnit中指定测试执行顺序的方法。
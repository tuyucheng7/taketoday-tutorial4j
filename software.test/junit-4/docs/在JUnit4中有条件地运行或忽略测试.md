## 1. 概述

假设我们对一些依赖于操作系统的代码进行了测试，并且只有当我们的测试机器在Linux上运行时才应该运行。如果它在任何其他操作系统上运行，我们希望测试不会失败，而是在运行时被忽略。

第一种方法可能是使用几个if语句来使用System类属性检查此条件。当然，这有效，但JUnit提供有一个更简洁、更优雅的方法。

在这个简短的教程中，我们介绍如何使用[Assume](https://junit.org/junit4/javadoc/4.12/org/junit/Assume.html)类在JUnit 4中有条件地运行或忽略测试。

## 2. Assume类

**该类提供了一组方法来支持基于特定条件的条件测试执行**。只有在满足所有这些条件时，我们的测试才会运行。**如果不满足，JUnit将跳过它的执行并在测试报告中将其标记为通过**。后者是与Assert类的主要区别，在Assert类中，失败条件会导致测试以失败结束。

需要注意的重要一点是，我们为Assume类描述的行为是默认的JUnit Runner独有的。对于[自定义Runner]()，情况可能会有所不同。

最后，与Assert一样，我们可以在@Before或@BeforeClass注解方法中或在@Test方法本身中调用Assume方法。

现在我们通过一些例子来了解Assume类中最有用的方法。对于以下所有示例，我们假设getOsName()返回Linux。

### 2.1 使用assumeThat

assumeThat()方法检查状态(在本例中为getOsName())是否满足传入的匹配器的条件：

```java
@Test
public void whenAssumeThatAndOSIsLinux_thenRunTest() {
	assumeThat(getOsName(), is("Linux"));
    
	assertEquals("run", "RUN".toLowerCase());
}
```

**在这个例子中，我们检查了getOsName()是否等于Linux。当getOsName()返回Linux时，测试将被运行**。请注意，我们这里使用[Hamcrest匹配器]()方法is(T)作为匹配器。

### 2.2 使用assumeTrue 

类似地，我们可以使用assumeTrue()方法指定一个布尔表达式，该表达式的计算结果必须为true才能运行测试。如果它的计算结果为false，测试将被忽略：

```java
private boolean isExpectedOS(String osName) {
    return "Linux".equals(osName);
}

@Test 
public void whenAssumeTrueAndOSIsLinux_thenRunTest() {
    assumeTrue(isExpectedOS(getOsName()));
 
    assertEquals("run", "RUN".toLowerCase());
}
```

在这种情况下，isExpectedOs()返回true。因此，测试运行的条件已经满足，测试将被运行。

### 2.3 使用assumeFalse

最后，我们可以使用相反的assumeFalse()方法来指定一个布尔表达式，该表达式的计算结果必须为false才能运行测试。如果它的计算结果为true，测试将被忽略：

```java
@Test
public void whenAssumeFalseAndOSIsLinux_thenIgnore() {
    assumeFalse(isExpectedOS(getOsName()));

    assertEquals("run", "RUN".toLowerCase());
}
```

在这种情况下，由于isExpectedOs()也返回true，因此尚未满足测试运行的条件，测试将被忽略。

### 2.4 使用assumeNotNull

当我们想忽略某个表达式是否为null的测试时，我们可以使用assumeNotNull()方法：

```java
@Test
public void whenAssumeNotNullAndNotNullOSVersion_thenRun() {
    assumeNotNull(getOsName());

    assertEquals("run", "RUN".toLowerCase());
}
```

由于getOsName()返回一个非空值，因此已满足测试运行的条件，测试将被运行。

### 2.5 使用assumeNoException

最后，如果抛出异常，我们可能希望忽略测试。为此，我们可以使用assumeNoException()：

```java
@Test
public void whenAssumeNoExceptionAndExceptionThrown_thenIgnore() {
    assertEquals("everything ok", "EVERYTHING OK".toLowerCase());
    String t = null;
    try {
        t.charAt(0);
    } catch(NullPointerException npe){
        assumeNoException(npe);
    }
    
    assertEquals("run", "RUN".toLowerCase());
}
```

本例中，由于t为null会抛出NullPointerException异常，因此不满足测试运行的条件，测试将被忽略。

## 3. 我们应该在哪里调用assumeXXX？

**需要注意的是，assumeXXX方法的行为取决于我们将它们放在测试中的位置**。

让我们稍微修改一下我们的assumeThat示例，让assertEquals()调用先进行。另外，让assertEquals()失败：

```java
@Test
public void whenAssumeFalseAndOSIsLinux_thenIgnore() {
    assertEquals("run", "RUN");
    assumeFalse(isExpectedOS(getOsName()));
}
```

当我们运行这个例子时，得到的结果为：

```shell
org.junit.ComparisonFailure: 
Expected :run
Actual   :RUN
```

**在这种情况下，我们的测试不会被忽略，因为它在我们到达assumeThat()调用之前就已经失败了。所有assumeXXX方法都会发生同样的情况。因此，我们需要确保将它们放在测试方法中的正确位置**。

## 4. 总结

在这个简短的教程中，我们了解了如何使用JUnit 4中的Assume类有条件地决定是否应该运行测试。如果我们使用的是JUnit 5，它也可以在5.4或更高版本中使用。
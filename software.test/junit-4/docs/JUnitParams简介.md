## 1. 概述

在本文中，我们介绍[JUnitParams](https://github.com/Pragmatists/JUnitParams)库及其用法。简而言之，**这个库提供了JUnit测试中测试方法的简单参数化**。

在某些情况下，在多个测试之间唯一发生变化的是参数。JUnit本身具有参数化支持，而JUnitParams显著改进了该功能。

## 2. Maven依赖

要在我们的项目中使用JUnitParams，我们需要将它添加到pom.xml中：

```xml
<dependency>
    <groupId>pl.pragmatists</groupId>
    <artifactId>JUnitParams</artifactId>
    <version>1.1.0</version>
</dependency>
```

## 3. 测试场景

我们创建一个类，它执行两个整数的安全相加。如果结果溢出，它应该返回Integer.MAX_VALUE，如果结果下溢，它应该返回Integer.MIN_VALUE：

```java
public class SafeAdditionUtil {

	public int safeAdd(int a, int b) {
		long result = ((long) a) + b;
		if (result > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else if (result < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return (int) result;
	}
}
```

## 4. 构建一个简单的测试方法

我们需要针对输入值的不同组合测试方法实现，以确保实现适用于所有可能的场景。JUnitParams提供了多种实现参数化测试创建的方法。

首先，我们采用最少编码的基本方法，看看它是如何完成的。然后，我们可以看到使用JUnitParams实现测试场景的其他可能方法是什么：

```java
@RunWith(JUnitParamsRunner.class)
public class SafeAdditionUtilTest {

    private SafeAdditionUtil serviceUnderTest = new SafeAdditionUtil();

    @Test
    @Parameters({
            "1, 2, 3",
            "-10, 30, 20",
            "15, -5, 10",
            "-5, -10, -15" }
    )
    public void whenWithAnnotationProvidedParams_thenSafeAdd(int a, int b, int expectedValue) {
        assertEquals(expectedValue, serviceUnderTest.safeAdd(a, b));
    }
}
```

现在让我们看看这个测试类与常规的JUnit测试类有何不同。

我们注意到的第一件事是类注解中有一个不同的测试Runner-JUnitParamsRunner。然后可以看到测试方法使用带有输入参数数组的@Parameters注解进行标注，它指示将用于测试我们的Service方法的不同测试场景。

如果我们使用Maven运行测试，我们可以看到运行了四个测试用例，而不只是一个：

```shell
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running cn.tuyucheng.taketoday.junitparams.SafeAdditionUtilTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.068 sec - in cn.tuyucheng.taketoday.junitparams.SafeAdditionUtilTest

Results :

Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

## 5. 不同类型的测试方法参数化

如果我们有很多可能的场景需要测试，那么直接在注解中提供测试参数肯定不是最可读的方式。JUnitParams提供了一组我们可以用来创建参数化测试的不同方法：

-   直接在@Parameters注解中(在上面的例子中使用)
-   使用注解中定义的命名测试方法
-   使用由测试方法名称映射的方法
-   注解中定义的命名测试类
-   使用CSV文件

### 5.1 直接使用@Parameters注解

在之前的例子中我们已经使用过了这种方法。需要记住的是，我们应该提供一个参数字符串数组。在参数字符串中，每个参数由逗号分隔。

例如，数组的形式为{"1, 2, 3", "-10, 30, 20"}，则"1, 2, 3"表示为一组参数。

这种方法的局限性在于我们只能提供原始类型和String作为测试参数，不能将对象作为测试方法参数提交。

### 5.2 参数方法

我们可以使用类中的另一个方法提供测试方法参数，下面来看一个例子：

```java
@Test
@Parameters(method = "parametersToTestAdd")
public void whenWithNamedMethod_thenSafeAdd(int a, int b, int expectedValue) {
	assertEquals(expectedValue, serviceUnderTest.safeAdd(a, b));
}

private Object[] parametersToTestAdd() {
	return new Object[]{
			new Object[]{1, 2, 3},
			new Object[]{-10, 30, 20},
			new Object[]{Integer.MAX_VALUE, 2, Integer.MAX_VALUE},
			new Object[]{Integer.MIN_VALUE, -8, Integer.MIN_VALUE}
	};
}
```

测试方法使用@Parameters(method = "parametersToTestAdd")注解进行标注，它通过运行引用的方法来获取参数。

测试参数提供者方法的规范应返回一个Object数组作为结果。如果具有给定名称的方法不可用，则测试用例将失败并显示错误：

```java
java.lang.RuntimeException: Could not find method: bogusMethodName so no params were used.
```

### 5.3 按测试方法名称映射的方法

如果我们没有在@Parameters注解中指定任何内容，JUnitParams会尝试根据测试方法名称加载测试数据提供程序方法。方法名构造为"parametersFor"+<测试方法名>：

```java
@Test
@Parameters
public void whenWithnoParam_thenLoadByNameSafeAdd(int a, int b, int expectedValue) {
	assertEquals(expectedValue, serviceUnderTest.safeAdd(a, b));
}

private Object[] parametersForWhenWithnoParam_thenLoadByNameSafeAdd() {
	return new Object[]{
			new Object[]{1, 2, 3},
			new Object[]{-10, 30, 20},
			new Object[]{Integer.MAX_VALUE, 2, Integer.MAX_VALUE},
			new Object[]{Integer.MIN_VALUE, -8, Integer.MIN_VALUE}
	};
}
```

在上面的示例中，测试方法的名称是whenWithnoParam_thenLoadByNameSafeAdd()。因此，当执行测试方法时，它会查找名称为parametersForWhenWithnoParam_thenLoadByNameSafeAdd()的数据提供程序方法。

由于该方法存在，它将从中加载数据并运行测试。**如果没有与所需名称匹配的方法，则测试将失败，如上例所示**。

### 5.4 在注解中定义的命名测试类

类似于我们在前面的示例中引用数据提供程序方法的方式，我们可以引用一个单独的类来为我们的测试提供数据：

```java
public class TestDataProvider {

    public static Object[] provideBasicData() {
        return new Object[]{
                new Object[]{1, 2, 3},
                new Object[]{-10, 30, 20},
                new Object[]{15, -5, 10},
                new Object[]{-5, -10, -15}
        };
    }

    public static Object[] provideEdgeCaseData() {
        return new Object[]{
                new Object[]{Integer.MAX_VALUE, 2, Integer.MAX_VALUE},
                new Object[]{Integer.MIN_VALUE, -2, Integer.MIN_VALUE}
        };
    }
}

@Test
@Parameters(source = TestDataProvider.class)
public void whenWithNamedClass_thenSafeAdd(int a, int b, int expectedValue) {
	assertEquals(expectedValue, serviceUnderTest.safeAdd(a, b));
}
```

假设方法名称以“provide”开头，我们可以在一个类中拥有任意数量的测试数据提供者。如果是这样，执行程序将选取这些方法并返回数据。

如果类中没有方法满足该要求，即使这些方法返回一个Object数组，那么这些方法也会被忽略。

### 5.5 使用CSV文件

我们可以使用外部CSV文件来加载测试数据。如果可能的测试用例数量非常多，或者测试用例经常更改，这会有所帮助。可以在不影响测试代码的情况下进行更改。

假设我们有一个包含测试数据的CSV文件JunitParamsTestParameters.csv：

```csv
1,2,3
-10, 30, 20
15, -5, 10
-5, -10, -15
```

下面是使用该文件作为测试数据来源的简单案例：

```java
@Test
@FileParameters("src/test/resources/JunitParamsTestParameters.csv")
public void whenWithCsvFile_thenSafeAdd(int a, int b, int expectedValue) {
	assertEquals(expectedValue, serviceUnderTest.safeAdd(a, b));
}
```

这种方法的一个限制是无法传递复杂的对象，只有原始数据类型和String是有效的。

## 6. 总结

在本教程中，我们简要介绍了如何利用JUnitParams提供的多种方式运行参数测试。
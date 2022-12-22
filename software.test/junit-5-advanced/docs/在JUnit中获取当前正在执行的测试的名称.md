## 1. 概述

当使用JUnit时，我们可能需要我们测试方法来访问它们的名称。这可能有助于定位错误消息，尤其是对于使用系统生成名称的测试。

在这个教程中，我们介绍如何在JUnit 4和JUnit 5中获取当前测试用例的名称。

## 2. Junit 5

让我们看两个场景。首先，我们将了解如何访问单个测试的名称。
此名称通常是可预测的，因为它可能是方法的名称或@DisplayName注解的值。但是，如果我们使用参数化测试或显示名称生成器，那么我们可能需要知道JUnit提供的名称。

**JUnit 5可以将一个TestInfo对象注入到我们的测试方法参数中，通过它我们可以获取当前测试用例的名称**。

### 2.1 单独的测试

```java
class JUnit5SimpleTestNameUnitTest {

    private boolean oddCheck(int number) {
        return (number % 2 != 0);
    }

    @Test
    void givenNumbers_whenOddCheck_thenVerify(TestInfo testInfo) {
        System.out.println("displayName = " + testInfo.getDisplayName());
        int number = 5;
        assertTrue(oddCheck(number));
    }
}
```

**在这里，我们使用TestInfo接口的getDisplayName()方法来获取测试方法的名称**。运行测试时，我们可以看到如下输出：

```text
displayName = givenNumbers_whenOddCheck_thenVerify(TestInfo)
```

### 2.2 参数化测试

让我们通过一个参数化测试来举例。在这里，我们使用@ParameterizedTest注解的name属性向JUnit描述如何为我们的测试生成一个名称：

```java
class Junit5ParameterizedTestNameUnitTest {

    private TestInfo testInfo;

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    private boolean oddCheck(int number) {
        return (number % 2 != 0);
    }

    @ParameterizedTest(name = "givenNumbers_whenOddCheck_thenVerify {0}")
    @ValueSource(ints = {1, 3, 5, -3, 15})
    void givenNumbers_whenOddCheck_thenVerify(int number) {
        System.out.println("displayName = " + testInfo.getDisplayName());
        assertTrue(oddCheck(number));
    }
}
```

我们应该注意，与单个测试方法不同，我们不能将TestInfo作为测试方法的形参。这是因为参数化测试的方法参数必须与参数化数据相关。
为了解决这个问题，我们需要**通过@BeforeEach方法将TestInfo对象保存在测试类的一个实例变量中**。

当我们运行测试时，我们会得到以下输出：

```text
displayName = givenNumbers_whenOddCheck_thenVerify 15
displayName = givenNumbers_whenOddCheck_thenVerify 5
displayName = givenNumbers_whenOddCheck_thenVerify 1
displayName = givenNumbers_whenOddCheck_thenVerify 3
displayName = givenNumbers_whenOddCheck_thenVerify -3
```

## 3. Junit 4

**JUnit 4可以在我们的测试中指定一个TestName对象**，TestName是一个JUnit Rule。
这些Rule是作为JUnit测试执行的一部分执行的，使用它可以显示当前正在运行的测试的详细信息。

### 3.1 单独的测试

让我们看一个单独的测试：

```java
public class JUnit4SimpleTestNameUnitTest {

    @Rule
    public TestName name = new TestName();

    private static String sortCharacters(String s) {
        char[] charArray = s.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }

    @Test
    public void givenString_whenSort_thenVerifySortForString() {
        System.out.println("displayName = " + name.getMethodName());
        String s = "abc";
        assertEquals(s, sortCharacters("cba"));
    }
}
```

如上所示，**我们可以使用TestName的getMethodName()方法来获取测试的名称**。

当我们运行测试后，输出如下：

```text
displayName = givenString_whenSort_thenVerifySortForString
```

### 3.2 参数化测试

现在，让我们使用相同的方法来显示为参数化测试生成的测试名称。首先，我们需要使用特殊的Runner对测试类进行标注：

```java

@RunWith(Parameterized.class)
public class JUnit4ParameterizedTestNameUnitTest {

}
```

然后，我们可以使用TestName Rule以及用于分配当前测试参数值的字段和构造函数来实现测试：

```java
public class JUnit4ParameterizedTestNameUnitTest {

    private final String input;
    private final String expected;

    @Rule
    public TestName name = new TestName();

    public JUnit4ParameterizedTestNameUnitTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> suppliedData() {
        return Arrays.asList(new Object[][]{{"abc", "abc"}, {"cba", "abc"}, {"onm", "mno"}, {"a", "a"}, {"zyx", "xyz"},});
    }

    private static String sortCharacters(String s) {
        char[] charArray = s.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }

    @Test
    public void givenString_whenSort_thenVerifySortForString() {
        System.out.println("displayName = " + name.getMethodName());
        assertEquals(expected, sortCharacters(input));
    }
}
```

在这个测试中，我们提供了包含input字符串和expected字符串的测试数据集合，这是通过suppliedData()方法完成的。
该方法用@Parameterized.Parameters注解标注，此注解还允许我们描述测试名称。

当我们运行测试时，TestName Rule会给出每个测试的名称：

```text
displayName = givenString_whenSort_thenVerifySortForString[abc]
displayName = givenString_whenSort_thenVerifySortForString[cba]
displayName = givenString_whenSort_thenVerifySortForString[onm]
displayName = givenString_whenSort_thenVerifySortForString[a]
displayName = givenString_whenSort_thenVerifySortForString[zyx]
```

## 4. 总结

在本文中，我们介绍了如何在JUnit 4和JUnit 5中获取当前测试的名称，并针对单一测试和参数测试分别做了演示。
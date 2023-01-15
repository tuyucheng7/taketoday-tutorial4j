## 1. 概述

有时在编写单元测试时，我们可能需要测试直接与[System]()类交互的代码。通常在命令行工具等应用程序中直接调用[System.exit]()或使用System.in读取参数。

**在本教程中，我们介绍一个名为[System Rules](https://stefanbirkner.github.io/systemo-rules/)的简洁外部库的最常见功能，该库提供了一组[JUnit Rule]()，用于测试使用System类的代码**。

## 2. Maven依赖

首先，我们将[System Rule](https://search.maven.org/classic/#search|ga|1|a%3A"system-rules")依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-rules</artifactId>
    <version>1.19.0</version>
</dependency>
```

并添加[System Lambda](https://github.com/stefanbirkner/system-lambda)依赖项：

```xml
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-lambda</artifactId>
    <version>1.1.0</version>
</dependency>
```

**由于System Rule不直接支持[JUnit 5]()，因此我们添加了最后一个依赖项**。这提供了在测试中使用的System Lambda包装器方法。有一个基于[Extension]()的替代方案，称为[System Stubs]()。

## 3. 使用系统属性

快速回顾一下，Java平台使用[Properties]()对象来提供有关本地系统和配置的信息。我们可以很容易地打印出属性值：

```java
System.getProperties()
        .forEach((key, value) -> System.out.println(key + ": " + value));
```

如我们所见，属性包括当前用户、Java运行时的当前版本和文件路径名分隔符等信息：

```shell
java.version: 1.8.0_341
file.separator: \
user.home: C:\Users\tuyuc
os.name: Windows 11
...
```

我们还可以使用System.setProperty方法设置自己的系统属性。在使用我们的测试中的系统属性时应该小心，因为这些属性是JVM全局的。例如，如果我们设置了一个系统属性，我们应该确保在测试完成或发生故障时将该属性恢复为其原始值。这有时会导致繁琐的设置和拆除代码。但是，如果我们忽略这样做，可能会导致我们的测试出现意想不到的副作用。

在下一节中，我们介绍如何以简洁明了的方式提供、清理并确保在测试完成后恢复系统属性值。

## 4. 提供系统属性

假设我们有一个系统属性log_dir，其中包含应该写入日志的位置，并且我们的应用程序在启动时设置该位置：

```java
System.setProperty("log_dir", "/tmp/tuyucheng/logs");
```

### 4.1 提供单一属性

现在让我们考虑一下，从我们的单元测试中，我们想要提供一个不同的值。我们可以使用ProvideSystemPropertyRule来做到这一点：

```java
public class ProvidesSystemPropertyWithRuleUnitTest {

    @Rule
    public final ProvideSystemProperty providesSystemPropertyRule = new ProvideSystemProperty("log_dir", "test/resources");

    @Test
    public void givenProvideSystemProperty_whenGetLogDir_thenLogDirIsProvidedSuccessfully() {
        assertEquals("log_dir should be provided", "test/resources", System.getProperty("log_dir"));
    }
    // unit test definition continues
}
```

**使用ProvideSystemProperty Rule，我们可以为给定的系统属性设置任意值，以便在测试中使用**。在此示例中，我们将log_dir属性设置为我们的test/resources目录，并从我们的单元测试中简单地断言测试属性值已成功设置。

如果我们在测试类完成时打印出log_dir属性的值：

```java
@AfterClass
public static void tearDownAfterClass() throws Exception {
    System.out.println(System.getProperty("log_dir"));
}
```

我们可以看到属性的值已恢复到其原始值：

```shell
/tmp/tuyucheng/logs
```

### 4.2 提供多个属性

如果我们需要提供多个属性，我们可以使用and方法将测试所需的任意多个属性值链接在一起：

```java
@Rule
public final ProvideSystemProperty providesSystemPropertyRule = new ProvideSystemProperty("log_dir", "test/resources").and("another_property", "another_value")
```

### 4.3 从文件提供属性

同样，我们也可以使用ProvideSystemProperty Rule从文件或类路径资源中提供属性：

```java
@Rule
public final ProvideSystemProperty providesSystemPropertyFromFileRule = ProvideSystemProperty.fromResource("/test.properties");

@Test
public void givenProvideSystemPropertyFromFile_whenGetName_thenNameIsProvidedSuccessfully() {
    assertEquals("name should be provided", "tuyucheng", System.getProperty("name"));
    assertEquals("version should be provided", "1.0.0", System.getProperty("version"));
}
```

在上面的示例中，我们假设类路径中有一个test.properties文件：

```properties
name=tuyucheng
version=1.0.0
```

### 4.4 使用JUnit 5和Lambda提供属性

正如我们之前提到的，我们还可以使用该库的System Lambda版本来实现与JUnit 5兼容的测试。让我们看看如何使用这个版本的库来实现我们的测试：

```java
class ProvidesSystemPropertyUnitTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        System.setProperty("log_dir", "/tmp/tuyucheng/logs");
    }

    @Test
    void givenSetSystemProperty_whenGetLogDir_thenLogDirIsProvidedSuccessfully() throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty("log_dir", "test/resources");
            assertEquals("test/resources", System.getProperty("log_dir"), "log_dir should be provided");
        });

        assertEquals("/tmp/tuyucheng/logs", System.getProperty("log_dir"), "log_dir should be provided");
    }
}
```

在这个版本中，我们可以使用restoreSystemProperties方法来执行given语句。**在given语句中，我们可以设置并提供系统属性所需的值**。在这个方法执行完成后，我们可以看到，log_dir的值与/tmp/tuyucheng/logs之前的值相同。

遗憾的是，没有内置支持使用restoreSystemProperties方法从文件中提供属性。

## 5. 清除系统属性

有时我们可能想在测试开始时清除一组系统属性，并在测试结束时恢复它们的原始值，而不管测试是通过还是失败。为此，我们可以使用ClearSystemProperties Rule：

```java
public class ClearSystemPropertiesWithRuleUnitTest {

    @Rule
    public final ClearSystemProperties userNameIsClearedRule = new ClearSystemProperties("user.name");

    @Test
    public void givenClearUsernameProperty_whenGetUserName_thenNull() {
        assertNull(System.getProperty("user.name"));
    }
}
```

系统属性user.name是预定义的系统属性之一，其中包含用户帐户名称。正如在上面的单元测试中所预期的那样，我们清除了这个属性并在我们的测试中断言它是空的。

**方便的是，我们还可以将多个属性名称传递给ClearSystemProperties构造函数**。

## 6. Mock System.in

有时，我们可能会创建从System.in读取的交互式命令行应用程序。

对于本节，我们演示一个非常简单的示例，它从标准输入中读取名字和姓氏并将它们拼接在一起：

```java
private String getFullname() {
	try (Scanner scanner = new Scanner(System.in)) {
		String firstName = scanner.next();
		String surname = scanner.next();
		return String.join(" ", firstName, surname);
	}
}
```

System Rules包含TextFromStandardInputStream Rule，我们可以使用该Rule来指定调用System.in时应提供的输入行：

```java
@Rule
public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

@Test
public void givenTwoNames_whenSystemInMock_thenNamesJoinedTogether() {
    systemInMock.provideLines("Jonathan", "Cook");
    assertEquals("Names should be concatenated", "Jonathan Cook", getFullname());
}
```

**我们通过使用provideLines方法来实现这一点，该方法接收[可变参数]()来启用指定多个值**。

在此示例中，我们在调用getFullname方法之前提供了两个值，其中引用了System.in。每次调用scanner.next()时都会返回我们提供的两个值。

下面使用System Lambda的JUnit 5版本的测试实现同样的效果：

```java
@Test
void givenTwoNames_whenSystemInMock_thenNamesJoinedTogether() throws Exception {
	withTextFromSystemIn("Jonathan", "Cook").execute(() ->
			assertEquals("Jonathan Cook", getFullname(), "Names should be concatenated"));
}
```

**在这个变体中，我们使用类似名称的withTextFromSystemIn方法，它允许我们指定提供的System.in值**。在这两种情况下，重要的是当测试完成后，System.in的原始值将被恢复。

## 7. 测试System.out和System.err

在之前的教程中，我们介绍了如何使用System Rule对[System.out.println()]()进行单元测试。

方便的是，我们可以应用几乎相同的方法来测试与标准错误流交互的代码。这次我们使用SystemErrRule：

```java
@Rule
public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

@Test
public void givenSystemErrRule_whenInvokePrintln_thenLogSuccess() {
    printError("An Error occurred tuyucheng Readers!!");

    Assert.assertEquals("An Error occurred tuyucheng Readers!!", systemErrRule.getLog().trim());
}

private void printError(String output) {
    System.err.println(output);
}
```

使用SystemErrRule，我们可以拦截对System.err的写入。首先，我们通过调用Rule的enableLog方法来开始记录写入System.err的所有内容。然后我们简单地调用getLog来获取写入System.err的文本，因为我们调用了enableLog。

下面我们实现JUnit5版本的测试：

```java
@Test
void givenTapSystemErr_whenInvokePrintln_thenOutputIsReturnedSuccessfully() throws Exception {

    String text = tapSystemErr(() -> printError("An error occurred tuyucheng Readers!!"));

    assertEquals("An error occurred tuyucheng Readers!!", text.trim());
}
```

****在这个版本中，我们使用了tapSystemErr方法，该方法执行语句并允许我们捕获传递给System.err的内容。

## 8. 处理System.exit

**命令行应用程序通常通过调用System.exit来终止。如果我们想测试这样的应用程序，很可能我们的测试在遇到调用System.exit的代码时会在它完成之前异常终止**。

值得庆幸的是，System Rule提供了一个巧妙的解决方案来使用ExpectedSystemExitRule来处理这个问题：

```java
@Rule
public final ExpectedSystemExit exitRule = ExpectedSystemExit.none();

@Test
public void givenSystemExitRule_whenAppCallsSystemExit_thenExitRuleWorkssAsExpected() {
    exitRule.expectSystemExitWithStatus(1);
    exit();
}

private void exit() {
    System.exit(1);
}
```

使用ExpectedSystemExitRule，我们可以从测试中指定预期的System.exit()调用。在这个简单的示例中，我们还使用expectSystemExitWithStatus方法检查预期的状态码。

**我们可以使用catchSystemExit方法在JUnit 5版本中实现类似的功能**：

```java
@Test
void givenCatchSystemExit_whenAppCallsSystemExit_thenStatusIsReturnedSuccessfully() throws Exception {
    int statusCode = catchSystemExit(this::exit);
    
    assertEquals("status code should be 1:", 1, statusCode);
}
```

## 9. 总结

在本教程中，我们详细介绍了System Rule库。首先，我们解释了如何测试使用系统属性的代码。然后介绍了如何测试标准输出和标准输入。最后，我们介绍了如何处理从测试中调用System.exit的代码。

System Rule库还支持从我们的测试中提供环境变量和特殊安全管理器。请务必查看完整[文档](https://stefanbirkner.github.io/system-rules/)以了解详细信息。
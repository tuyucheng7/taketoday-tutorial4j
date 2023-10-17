## 1. 概述

main ()方法充当每个Java应用程序的起点，并且根据应用程序类型，它可能看起来有所不同。对于常规 Web 应用程序，main()方法将负责上下文启动，但对于某些控制台应用程序，我们会将业务逻辑放入其中。

测试main()方法非常复杂，因为我们有一个静态方法，它只接受字符串参数并且不返回任何内容。

在本文中，我们将了解如何测试主要方法，重点关注命令行参数和输入流。

## 2.Maven依赖

对于本教程，我们需要几个测试库(Junit 和 Mockito)以及 Apache Commons CLI 才能使用参数：

```xml
<dependency>
     <groupId>commons-cli</groupId>
     <artifactId>commons-cli</artifactId>
     <version>1.5.0</version>
 </dependency>
 <dependency>
     <groupId>org.junit.jupiter</groupId>
     <artifactId>junit-jupiter-api</artifactId>
     <version>5.10.0</version>
     <scope>test</scope>
 </dependency>
 <dependency>
     <groupId>org.mockito</groupId>
     <artifactId>mockito-core</artifactId>
     <version>5.5.0</version>
     <scope>test</scope>
 </dependency>
```

我们可以在 Maven 中央存储库中找到最新版本的[JUnit](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api)、[Mockito](https://mvnrepository.com/artifact/org.mockito/mockito-core)和[Apache Commons CLI](https://mvnrepository.com/artifact/commons-cli/commons-cli)。

## 3. 设置场景

为了说明main()方法测试，让我们定义一个实际场景。想象一下，我们的任务是开发一个简单的应用程序，旨在计算所提供数字的总和。它应该能够从控制台或文件中交互地读取输入，具体取决于提供的参数。程序输入包含一系列数字。

根据我们的场景，程序应该根据用户定义的参数动态调整其行为，从而执行不同的工作流程。

### 3.1. 使用 Apache Commons CLI 定义程序参数

我们需要为所描述的场景定义两个基本参数：“i”和“f”。“i”选项指定具有两个可能值(FILE 和 CONSOLE)的输入源。同时，“f”选项允许我们指定要读取的文件名，并且仅当“i”选项指定为FILE时才有效。

为了简化我们与这些参数的交互，我们可以依赖Apache Commons CLI库。该工具不仅可以验证参数，还可以方便值解析。下面是如何使用 Apache 的选项构建器定义“i”选项的说明：

```java
Option inputTypeOption = Option.builder("i")
  .longOpt("input")
  .required(true)
  .desc("The input type")
  .type(InputType.class)
  .hasArg()
  .build();
```

一旦我们定义了选项，Apache Commons CLI将帮助解析输入参数以分支业务逻辑的工作流程：

```java
Options options = getOptions();
CommandLineParser parser = new DefaultParser();
CommandLine commandLine = parser.parse(options, args);

if (commandLine.hasOption("i")) {
    System.out.print("Option i is present. The value is: " + commandLine.getOptionValue("i") + " \n");
    String optionValue = commandLine.getOptionValue("i");
    InputType inputType = InputType.valueOf(optionValue);

    String fileName = null;
    if (commandLine.hasOption("f")) {
        fileName = commandLine.getOptionValue("f");
    }
    String inputString = inputReader.read(inputType, fileName);
    int calculatedSum = calculator.calculateSum(inputString);
}
```

为了保持清晰和简单，我们将职责分为不同的类别。InputType枚举封装了可能的输入参数值。InputReader类根据InputType检索输入字符串，而Calculator 则根据解析的字符串计算总和。

有了这样的分离，我们就可以保留一个简单的主类，如下所示：

```java
public static void main(String[] args) {

    Bootstrapper bootstrapper = new Bootstrapper(new InputReader(), new Calculator());

    bootstrapper.processRequest(args);
}
```

## 4. 如何测试Main方法

main()方法的签名和行为与我们在应用程序中使用的常规方法不同。因此，我们需要结合多种特定于测试静态方法、void 方法、输入流和参数的测试策略。

我们将在下面的段落中介绍每个概念，但让我们首先看看如何构建main()方法的业务逻辑。

当我们开发一个新应用程序时，我们可以完全控制它的架构，那么main()方法不应该有任何复杂的逻辑，而不是初始化所需的工作流程。有了这样的架构，我们就可以对每个工作流程部分进行适当的单元测试(Bootstrapper、InputReader和Calculator可以单独测试)。

另一方面，当涉及到具有历史记录的旧应用程序时，事情可能会变得有点棘手。尤其是当以前的开发人员将大量业务逻辑直接放置在主类的静态上下文中时。遗留代码并不总是可以更改，我们应该使用已经编写的内容。

### 4.1. 如何测试静态方法

过去，使用Mockito处理静态上下文是一个相当大的挑战，通常需要使用[PowerMockito](https://github.com/jayway/powermock/wiki/MockitoUsage)等库。然而，在最新版本的Mockito中，这个限制已经被克服。随着3.4.0版本中Mockito.mockStatic的引入，我们现在可以轻松地Mock和验证静态方法，而无需额外的库。这一增强简化了涉及静态方法的测试场景，使我们的测试过程更加精简和高效。

使用[MockedStatic ](https://www.baeldung.com/mockito-mock-static-methods)我们可以执行与常规 Mock 相同的操作：

```java
try (MockedStatic<SimpleMain> mockedStatic = Mockito.mockStatic(StaticMain.class)) {
    mockedStatic.verify(() -> StaticMain.calculateSum(stringArgumentCaptor.capture()));
    mockedStatic.when(() -> StaticMain.calculateSum(any())).thenReturn(24);
}
```

为了强制MockedStatic 作为 Spy 工作，我们需要添加一个配置参数：

```java
MockedStatic<StaticMain> mockedStatic = Mockito.mockStatic(StaticMain.class, Mockito.CALLS_REAL_METHODS)
```

一旦我们根据需要配置了MockedStatic ，我们就可以彻底测试静态方法。

### 4.2. 如何测试 void 方法

遵循功能开发方法，方法应符合几个要求。它们应该是独立的，不应该修改传入的参数，并且应该返回处理结果。

通过这种行为，我们可以轻松地根据返回结果验证编写单元测试。然而，测试 void 方法则不同，焦点转移到方法执行引起的副作用和状态变化上。

### 4.3. 如何测试程序参数

我们可以像任何其他标准Java方法一样从测试类调用main()方法。要使用不同的参数集评估其行为，我们只需在调用期间提供这些参数即可。

考虑到上一段中的选项定义，我们可以使用一个短参数-i来调用main()：

```java
String[] arguments = new String[] { "-i", "CONSOLE" };
SimpleMain.main(arguments);
```

另外，我们可以使用长形式的-i参数来调用 main 方法：

```java
String[] arguments = new String[] { "--input", "CONSOLE" };
SimpleMain.main(arguments);
```

### 4.4. 如何测试数据输入流

从控制台读取通常是用System.in构建的：

```java
private static String readFromConsole() {
    System.out.println("Enter values for calculation: \n");
    return new Scanner(System.in).nextLine();
}
```

System.in是主机环境指定的“标准”输入流，通常对应于键盘输入。我们无法在测试中提供键盘输入，但我们可以更改System.in引用的流类型：

```java
InputStream fips = new ByteArrayInputStream("1 2 3".getBytes());
System.setIn(fips);
```

在此示例中，我们更改了默认输入类型，以便应用程序将从ByteArrayInputStream读取并且不会继续等待用户输入。

我们可以在测试中使用任何其他类型的InputStream ，例如，我们可以从文件中读取：

```java
InputStream fips = getClass().getClassLoader().getResourceAsStream("test-input.txt");
System.setIn(fips);
```

此外，使用相同的方法，我们可以替换输出流以验证程序写入的内容：

```java
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
PrintStream out = new PrintStream(byteArrayOutputStream);
System.setOut(out);
```

使用这种方法，我们将看不到控制台输出，因为System.out会将所有数据发送到ByteArrayOutputStream而不是控制台。

## 5. 完整的测试示例

让我们结合前面段落中收集的所有知识来编写完整的测试。这些是我们要执行的步骤：

1. 嘲笑我们的主阶级是间谍
2. 将输入参数定义为字符串数组
3. 替换System.in中的默认流
4. 验证程序是否在静态上下文中调用所有必需的方法，或者程序是否将必要的结果写入控制台。
5. 将System.in和System.out流替换回原始流，以便流替换不会影响其他测试

在此示例中，我们对StaticMain类进行了测试，其中所有逻辑都放置在静态上下文中。我们用ByteArrayInputStream替换System.in并基于verify() 构建我们的验证：

```java
@Test
public void givenArgumentAsConsoleInput_WhenReadFromSubstitutedByteArrayInputStream_ThenSuccessfullyCalculate() throws IOException {
    String[] arguments = new String[] { "-i", "CONSOLE" };
    try (MockedStatic mockedStatic = Mockito.mockStatic(StaticMain.class, Mockito.CALLS_REAL_METHODS); 
      InputStream fips = new ByteArrayInputStream("1 2 3".getBytes())) {

        InputStream original = System.in;

        System.setIn(fips);

        ArgumentCaptor stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        StaticMain.main(arguments);

        mockedStatic.verify(() -> StaticMain.calculateSum(stringArgumentCaptor.capture()));

        System.setIn(original);
    }
}
```

我们可以对SimpleMain类使用稍微不同的策略，因为在这里我们通过其他类分发了所有业务逻辑。

在这种情况下，我们甚至不需要MockSimpleMain类，因为里面没有其他方法。我们将System.in替换为文件流，并根据传播到ByteArrayOutputStream 的控制台输出构建验证：

```java
@Test
public void givenArgumentAsConsoleInput_WhenReadFromSubstitutedFileStream_ThenSuccessfullyCalculate() throws IOException {
    String[] arguments = new String[] { "-i", "CONSOLE" };

    InputStream fips = getClass().getClassLoader().getResourceAsStream("test-input.txt");
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(byteArrayOutputStream);

    System.setIn(fips);
    System.setOut(out);

    SimpleMain.main(arguments);

    String consoleOutput = byteArrayOutputStream.toString(Charset.defaultCharset());
    assertTrue(consoleOutput.contains("Calculated sum: 10"));

    fips.close();
    out.close();
}
```

## 六，总结

在本文中，我们探讨了几种主要方法设计及其相应的测试方法。我们已经介绍了静态和 void 方法的测试、处理参数以及更改默认系统流。
## 1. 概述

在进行单元测试时，我们有时可能希望测试通过System.out.println()写入[标准输出流](https://www.baeldung.com/linux/pipes-redirection#standard-io)的消息。

虽然我们通常更喜欢使用[日志框架](https://www.baeldung.com/java-logging-intro)而不是与标准输出流直接交互，但有时这是不可能的。

在这个快速教程中，**我们将了解使用JUnit对System.out.println()进行单元测试的几种方法**。

## 2. 简单的打印方法

在本教程中，我们测试的重点将是一个写入标准输出流的简单方法：

```java
private void print(String output) {
    System.out.println(output);
}
```

提醒一下，out变量是一个公共静态最终的PrintStream对象，它表示用于系统范围使用的标准输出流。

## 3. 使用核心Java

现在让我们看看如何编写一个单元测试来检查我们传递给println方法的内容。但是，在编写实际的单元测试之前，我们需要在测试中提供一些初始化：

```java
private final PrintStream standardOut = System.out;
private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

@Before
public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
}
```

**在setUp方法中，我们将标准输出流重新分配给带有ByteArrayOutputStream的新PrintStream**。正如我们将看到的，这个输出流是现在将打印值的地方：

```java
@Test
public void givenSystemOutRedirection_whenInvokePrintln_thenOutputCaptorSuccess() {
    print("Hello Tuyucheng Readers!!");
        
    Assert.assertEquals("Hello Tuyucheng Readers!!", outputStreamCaptor.toString().trim());
}
```

在使用所选文本调用print方法后，我们可以验证outputStreamCaptor是否包含我们期望的内容。**我们调用trim方法来删除System.out.println()添加的新行**。

由于标准输出流是系统其他部分使用的共享静态资源，因此**我们应该注意在测试终止时将其恢复到原始状态**：

```java
@After
public void tearDown() {
    System.setOut(standardOut);
}
```

这确保了我们以后在其他测试中不会得到任何不必要的副作用。

## 4. 使用System Rules

在本节中，**我们介绍一个名为[System Rules](https://stefanbirkner.github.io/system-rules/)的简洁外部库，它提供了一组JUnit Rule，用于测试使用System类的代码**。

让我们首先将[system-rules](https://central.sonatype.com/artifact/com.github.stefanbirkner/system-rules/1.19.0)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-rules</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```

现在，我们可以继续使用库提供的SystemOutRule编写测试：

```java
@Rule
public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

@Test
public void givenSystemOutRule_whenInvokePrintln_thenLogSuccess() {
    print("Hello Tuyucheng Readers!!");

    Assert.assertEquals("Hello Tuyucheng Readers!!", systemOutRule.getLog().trim());
}
```

**使用SystemOutRule，我们可以拦截对System.out的写入**。首先，我们通过调用SystemOutRule上的enableLog方法来开始记录写入System.out的所有内容。然后我们简单地调用getLog来获取写入System.out的文本，因为我们调用了enableLog。

此Rule还包括一个方便的方法，该方法返回始终将行分隔符为\n的日志。

```java
Assert.assertEquals("Hello Tuyucheng Readers!!\n", systemOutRule.getLogWithNormalizedLineSeparator());
```

## 5. 将System Rules与JUnit 5和Lambda使用

在[JUnit 5](https://www.baeldung.com/junit-5)中，Rule模型被[Extension](https://www.baeldung.com/junit-5-extensions)取代。幸运的是，上一节中介绍的system-rules库有一个与JUnit 5一起使用的[变体](https://github.com/stefanbirkner/system-lambda)。

System-Lambda可以从[Maven Central](https://central.sonatype.com/artifact/com.github.stefanbirkner/system-lambda/1.2.1)找到。所以我们可以继续将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-lambda</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

现在让我们使用这个版本的库来实现我们的测试：

```java
@Test
void givenTapSystemOut_whenInvokePrintln_thenOutputIsReturnedSuccessfully() throws Exception {
    String text = tapSystemOut(() -> print("Hello Tuyucheng Readers!!"));

    assertEquals("Hello Tuyucheng Readers!!", text.trim());
}
```

在这个版本中，**我们使用tapSystemOut方法，该方法执行语句并允许我们捕获传递给System.out的内容**。

## 6. 总结

在本教程中，我们介绍了几种测试System.out.println的方法。在第一种方法中，我们看到了如何使用核心Java重定向我们写入标准输出流的位置。

然后我们了解了如何使用一个名为System-Rules的外部库，首先使用JUnit 4 Rule，然后使用Lambda。

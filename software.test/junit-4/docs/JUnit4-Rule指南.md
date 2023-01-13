## 1. 概述

在本教程中，我们介绍[JUnit 4](https://junit.org/junit4/)库提供的Rule功能。

在介绍发行版提供的最重要的基本Rule之前，我们首先介绍JUnit Rule模型。此外，我们还将了解如何编写和使用我们自己的自定义JUnit Rule。

**请注意，如果你使用的是JUnit 5，则Rule已被[Extension模型]()取代**。

## 2. JUnit 4 Rule介绍

**JUnit 4 Rule提供了一种灵活的机制，通过围绕测试用例执行运行一些代码来增强测试**。从某种意义上说，它类似于在我们的测试类中使用[@Before和@After注解]()。

假设我们想在测试设置期间连接到外部资源(例如数据库)，然后在测试完成后关闭连接。如果我们想在多个测试中使用该数据库，那么可能会在多个测试中重复相同的代码。

**通过使用Rule，我们可以将所有内容隔离在一个地方，并轻松地从多个测试类中重用这些代码**。

## 3. 使用JUnit 4 Rule

那么我们如何使用Rule呢？我们可以按照以下简单步骤使用JUnit 4 Rule：

-   在我们的测试类中添加一个公共字段，并确保该字段的类型是org.junit.rules.TestRule接口的子类型
-   使用@Rule注解对字段进行标注

## 4. Maven依赖

首先，我们添加使用JUnit所必须的JUnit 4库：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

## 5.分发中提供的Rule

**当然，JUnit提供了许多有用的预定义Rule作为库的一部分，我们可以在org.junit.rules包中找到所有这些Rule**。


### 5.1 TemporaryFolder

在测试时，我们经常需要访问临时文件或文件夹。但是，管理这些文件的创建和删除可能很麻烦。**使用TemporaryFolderRule，我们可以管理在测试方法终止时应删除的文件和文件夹的创建**：

```java
@Rule
public TemporaryFolder tmpFolder = new TemporaryFolder();

@Test
public void givenTempFolderRule_whenNewFile_thenFileIsCreated() throws IOException {
	File testFile = tmpFolder.newFile("test-file.txt");
    
	assertTrue("The file should have been created: ", testFile.isFile());
	assertEquals("Temp folder and test file should match: ", tmpFolder.getRoot(), testFile.getParentFile());
}
```

如我们所见，我们首先定义了TemporaryFolder Rule tmpFolder。接下来，我们的测试方法在临时文件夹中创建一个名为test-file.txt的文件。然后我们检查文件是否已经创建并存在于它应该存在的位置。

测试完成后，应删除临时文件夹和文件。**但是，此Rule不会检查删除是否成功**。

在这个类中还有一些其他有用的方法值得一提：

-   ```java
    newFile()
    ```

    如果我们不提供任何文件名，则此方法会创建一个随机命名的新文件。

-   ```java
    newFolder(String... folderNames)
    ```

    要创建递归深层临时文件夹，我们可以使用此方法。

-   ```java
    newFolder()
    ```

    同样，newFolder()方法创建一个随机命名的新文件夹。

值得一提的是，从4.13版本开始，TemporaryFolder Rule允许验证已删除的资源：

```java
@Rule 
public TemporaryFolder folder = TemporaryFolder.builder().assureDeletion().build();
```

如果无法删除资源，则测试失败并出现AssertionError。

最后，在[JUnit 5]()中，我们可以使用[临时目录扩展]()实现相同的功能。

### 5.2 ExpectedException

顾名思义，我们可以使用ExpectedException Rule来验证某些代码是否抛出了预期的异常：

```java
@Rule
public final ExpectedException thrown = ExpectedException.none();

@Test
public void givenIllegalArgument_whenExceptionThrown_thenMessageAndCauseMatches() {
	thrown.expect(IllegalArgumentException.class);
	thrown.expectCause(isA(NullPointerException.class));
	thrown.expectMessage("This is illegal");
    
	throw new IllegalArgumentException("This is illegal", new NullPointerException());
}
```

正如我们在上面的示例中所看到的，我们首先声明了ExpectedException Rule。然后，在我们的测试中，我们断言抛出了IllegalArgumentException。

**使用此Rule，我们还可以验证异常的一些其他属性，例如消息和原因**。

有关使用JUnit测试异常的深入指南，请阅读关于如何[断言异常]()的快速指南。

### 5.3 TestName

**简而言之，TestName Rule提供给定测试方法中的当前测试名称**：

```java
@Rule public TestName name = new TestName();

@Test
public void givenAddition_whenPrintingTestName_thenTestNameIsDisplayed() {
	LOG.info("Executing: {}", name.getMethodName());
	assertEquals("givenAddition_whenPrintingTestName_thenTestNameIsDisplayed", name.getMethodName());
}
```

在这个简单的例子中，当我们运行单元测试时，我们应该在输出中看到测试名称：

```shell
INFO  [c.t.taketoday.rules.RulesUnitTest] >>> Executing: givenAddition_whenPrintingTestName_thenTestNameIsDisplayed
```

### 5.4 Timeout

**此Rule提供了一种有用的替代方法，可以替代在单个@Test注解上使用timeout参数**。

下面，我们使用此Rule为测试类中的所有测试方法设置全局超时：

```java
@Rule
public Timeout globalTimeout = Timeout.seconds(10);

@Test
public void givenLongRunningTest_whenTimout_thenTestFails() throws InterruptedException {
	TimeUnit.SECONDS.sleep(20);
}
```

在上面的简单示例中，**我们首先为所有测试方法定义10秒的全局超时**，然后我们特意定义了一个需要超过10秒的测试。

当我们运行这个测试时，应该看到测试失败：

```shell
org.junit.runners.model.TestTimedOutException: test timed out after 10 seconds
...
```

### 5.5 ErrorCollector

**此Rule允许在发现第一个问题后继续执行测试**。

下面，我们使用此Rule收集所有错误并在测试终止时一次性报告所有错误：

```java
@Rule 
public final ErrorCollector errorCollector = new ErrorCollector();

@Test
public void givenMultipleErrors_whenTestRuns_thenCollectorReportsErrors() {
	errorCollector.addError(new Throwable("First thing went wrong!"));
	errorCollector.addError(new Throwable("Another thing went wrong!"));
    
	errorCollector.checkThat("Hello World", not(containsString("ERROR!")));
}
```

在上面的例子中，我们向收集器添加了两个错误。**当我们运行测试时，执行会继续，但测试最终会失败**。

在输出中，我们可以看到报告的两个错误：

```shell
java.lang.Throwable: First thing went wrong!
...
java.lang.Throwable: Another thing went wrong!
```

### 5.6 Verifier

**Verifier Rule是一个抽象基类，当我们希望验证测试中的一些其他行为时，可以使用它**。事实上，我们在上一节看到的ErrorCollector Rule扩展了这个类。

下面是一个定义我们自己的验证器的简单示例：

```java
private List messageLog = new ArrayList();

@Rule
public Verifier verifier = new Verifier() {
	@Override
	public void verify() {
		assertFalse("Message Log is not Empty!", messageLog.isEmpty());
	}
};
```

在这里，我们定义了一个新的Verifier并重写了verify()方法以添加一些额外的验证逻辑。在这个简单的示例中，我们只是检查示例中的messageLog是否为空。

现在，当我们运行单元测试并添加一条消息时，我们应该看到我们的验证器已被应用：

```java
@Test
public void givenNewMessage_whenVerified_thenMessageLogNotEmpty() {
    // ...
	messageLog.add("There is a new message!");
}
```

### 5.7 DisableOnDebug

**有时我们可能希望在调试时禁用Rule**。例如，在调试时通常需要禁用超时Rule，以避免我们的测试在我们有时间正确调试之前超时和失败。

这就是DisableOnDebug Rule的作用，它允许我们在调试时标记某些要禁用的Rule：

```java
@Rule
public DisableOnDebug disableTimeout = new DisableOnDebug(Timeout.seconds(30));
```

在上面的例子中我们可以看到，为了使用此Rule，**我们只需将要禁用的Rule传递给构造函数即可**。

这个Rule的主要好处是，我们可以禁用Rule，而无需在调试期间对测试类进行任何修改。

### 5.8 ExternalResource

通常，在编写集成测试时，我们可能希望在测试之前设置外部资源并在测试后将其拆除。值得庆幸的是，JUnit为此提供了另一个方便的基类。

**我们可以扩展抽象类ExternalResource来设置测试前的外部资源，例如文件或数据库连接**。事实上，我们之前看到的TemporaryFolder Rule扩展了ExternalResource。

下面是一个扩展该类的简单示例：

```java
@Rule
public final ExternalResource externalResource = new ExternalResource() {
    @Override
    protected void before() throws Throwable {
        // code to set up a specific external resource.
    };
    
    @Override
    protected void after() {
        // code to tear down the external resource
    };
};
```

在这个例子中，当我们定义一个外部资源时，我们只需要重写before()方法和after()方法来设置和拆除我们的外部资源。

## 6. 在类级别应用Rule

到目前为止，我们看过的所有示例都适用于单个测试用例方法。**但是，有时我们可能希望在测试类级别应用Rule**，我们可以通过使用@ClassRule注解来实现这一点。

这个注解的工作方式与@Rule非常相似，只是它将Rule应用在整个测试中，**主要区别在于我们用于类级别Rule的字段必须是静态的**：

```java
@ClassRule
public static TemporaryFolder globalFolder = new TemporaryFolder();
```

## 7. 自定义JUnit Rule

正如我们所见，JUnit 4提供了许多开箱即用的有用Rule，当然，我们可以定义自己的自定义Rule。**要编写自定义Rule，我们需要实现TestRule接口**。

下面是一个自定义Logger和测试方法名称Rule的示例：

```java
public class TestMethodNameLogger implements TestRule {

	private static final Logger LOG = LoggerFactory.getLogger(TestMethodNameLogger.class);

	@Override
	public Statement apply(Statement base, Description description) {
		logInfo("Before test", description);
		try {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					base.evaluate();
				}
			};
		} finally {
			logInfo("After test", description);
		}
	}

	private void logInfo(String msg, Description description) {
		LOG.info(msg + description.getMethodName());
	}
}
```

如我们所见，TestRule接口包含一个名为apply(Statement, Description)的方法，我们必须重写该方法并返回Statement的实例。Statement表示我们在JUnit运行时中的测试，**当我们调用evaluate()方法时，它会执行我们的测试**。

在此示例中，我们再测试前后记录日志，并从Description对象中包含单个测试的方法名称。

## 8. 使用Rule链

**在最后一节中，我们介绍如何使用RuleChain Rule对多个测试Rule进行排序**：

```java
@Rule
public RuleChain chain = RuleChain.outerRule(new MessageLogger("First rule"))
		.around(new MessageLogger("Second rule"))
		.around(new MessageLogger("Third rule"));
```

在上面的例子中，我们创建了一个包含三个Rule的链，这些Rule简单地打印出传递给每个MessageLogger构造函数的消息。

当我们运行测试时，可以看到这些Rule是以特定顺序运行的：

```shell
Starting: First rule 
Starting: Second rule 
Starting: Third rule 
Finished: Third rule 
Finished: Second rule 
Finished: First rule 
```

## 9. 总结

在本教程中，我们详细探讨了JUnit 4中的Rule。首先，我们解释了什么是Rule以及如何使用它们；接下来，我们深入了解了JUnit开箱即用的Rule；最后，我们介绍了如何定义自己的自定义Rule以及如何将Rule链接在一起。
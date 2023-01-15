## 1. 概述

当我们的软件依赖于环境变量、系统属性等系统资源或使用System.exit等进程级操作时，可能很难测试我们的软件。

Java没有提供设置环境变量的直接方法，我们冒着在一个测试中设置的值影响另一个测试执行的风险。同样，我们可能会发现自己避免为可能执行System.exit的代码编写JUnit测试，因为它有可能会中止测试。

[System Rules和System Lambda库](SystemRule库指南.md)是这些问题的早期解决方案。在本教程中，我们介绍一个名为[System Stubs](https://github.com/webcompere/system-stubs)的System Lambda新分支，它提供了JUnit 5替代方案。

## 2. 为什么是System Stubs？

### 2.1 System Lambda不是JUnit插件

最初的System Rules库只能用于JUnit 4，它仍然可以用于JUnit 5下的 [JUnit Vintage]()，但这需要继续创建JUnit 4测试。该库的创建者开发了一个名为[System Lambda](https://www.tuyucheng.com/java-system-rules-junit)的与测试框架无关的版本，旨在在每个测试方法中使用：

```java
@Test
void aSingleSystemLambda() throws Exception {
    restoreSystemProperties(() -> {
        System.setProperty("log_dir", "test/resources");
        assertEquals("test/resources", System.getProperty("log_dir"));
    });

    // more test code here
}
```

测试代码表示为lambda，传递给设置必要stub的方法。清理发生在控制权返回给测试方法的其余部分之前。尽管这在某些情况下效果很好，但这种方法有一些缺点。

### 2.2 避免额外的代码

System Lambda方法的好处是它的工厂类中有一些常见的配方用于执行特定类型的测试。但是，当我们想要在许多测试用例中使用它时，这会导致一些代码膨胀。

首先，即使测试代码本身不会抛出检查异常，包装器方法也会抛出异常，因此所有方法都要强制throws Exception。其次，在多个测试中设置相同的规则需要重复代码。每个测试都需要独立执行相同的配置。

但是，当我们尝试一次设置多个工具时，这种方法最繁琐的方面就出现了。假设我们要设置一些环境变量和系统属性。在我们的测试代码开始之前，我们最终需要两层嵌套：

```java
@Test
void multipleSystemLambdas() throws Exception {
    restoreSystemProperties(() -> {
        withEnvironmentVariable("URL", "https://www.tuyucheng.com")
            .execute(() -> {
                System.setProperty("log_dir", "test/resources");
                assertEquals("test/resources", System.getProperty("log_dir"));
                assertEquals("https://www.tuyucheng.com", System.getenv("URL"));
            });
    });
}
```

这就是JUnit插件或扩展可以帮助我们减少测试中需要的代码量的地方。

### 2.3 使用更少的样板

我们应该期望能够用最少的样板代码来编写我们的测试：

```java
@SystemStub
private EnvironmentVariables environmentVariables = ...;

@SystemStub
private SystemProperties restoreSystemProperties;

@Test
void multipleSystemStubs() {
    System.setProperty("log_dir", "test/resources");
    assertEquals("test/resources", System.getProperty("log_dir"));
    assertEquals("https://www.tuyucheng.com", System.getenv("ADDRESS"));
}
```

这种方法由SystemStubs JUnit 5扩展提供，允许我们用更少的代码编写测试。

### 2.4 测试生命周期钩子

当唯一可用的工具是[执行模式](https://java-design-patterns.com/patterns/execute-around/)时，不可能将stub行为挂钩到测试生命周期的所有部分。当尝试将其与其他JUnit扩展(例如@SpringBootTest)结合使用时，这尤其具有挑战性。

如果我们想围绕Spring Boot测试设置一些环境变量，那么我们无法合理地将整个测试生态系统嵌入到单个测试方法中。我们需要一种方法来激活围绕测试套件的测试设置。

使用System Lambda采用的方法，这是永远不可能实现的，这也是开发System Stubs的主要原因之一。

### 2.5 鼓励动态属性

其他用于设置系统属性的框架，例如[JUnit Pioneer](https://junit-pioneer.org/)，强调在编译时已知的配置。在现代测试中，我们可能使用[Testcontainers]()或[Wiremock]()，我们需要在这些工具启动后根据随机运行时设置来设置系统属性。这最适合用于可在整个测试生命周期中使用的测试库。

### 2.6 更多可配置性

拥有现成的测试配方是有益的，例如catchSystemExit，它围绕测试代码来完成单个工作。但是，这依赖于测试库开发人员来提供我们可能需要的各种配置选项。

按组合进行配置更加灵活，并且是新System Stubs实现的重要组成部分。

但是，System Stubs支持来自System Lambda的原始测试构造，以实现向后兼容性。此外，它还提供了一个新的JUnit 5扩展、一组JUnit 4 Rule以及更多配置选项。虽然基于原始代码，但它已经过大量重构和模块化，以提供更丰富的功能集。

接下来让我们深入地了解它。

## 3. 入门

### 3.1 依赖项

[JUnit 5扩展]()需要一个合适最新的[JUnit 5](https://search.maven.org/artifact/org.junit.jupiter/junit-jupiter)版本：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

下面我们将所有[System Rules](https://search.maven.org/search?q=system-stubs&g=uk.org.webcompere)库依赖项添加到我们的pom.xml中：

```xml
<!-- for testing with only lambda pattern -->
<dependency>
    <groupId>uk.org.webcompere</groupId>
    <artifactId>system-stubs-core</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>

<!-- for JUnit 4 -->
<dependency>
    <groupId>uk.org.webcompere</groupId>
    <artifactId>system-stubs-junit4</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>

<!-- for JUnit 5 -->
<dependency>
    <groupId>uk.org.webcompere</groupId>
    <artifactId>system-stubs-jupiter</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```

我们应该注意，我们只需要为我们正在使用的测试框架导入尽可能多的这些。实际上，后两者都传递性地包括核心依赖。

### 3.2 JUnit 4环境变量

我们可以通过在EnvironmentVariablesRule类型的测试类中声明JUnit 4 @Rule注解字段来控制环境变量。这将在我们的测试运行时由JUnit 4激活，并允许我们在测试中设置环境变量：

```java
@Rule
public EnvironmentVariablesRule environmentVariablesRule = new EnvironmentVariablesRule();

@Test
public void givenEnvironmentCanBeModified_whenSetEnvironment_thenItIsSet() {
    environmentVariablesRule.set("ENV", "value1");

    assertThat(System.getenv("ENV")).isEqualTo("value1");
}
```

在实践中，我们可能更喜欢在@Before方法中设置环境变量值，以便可以在所有测试之间共享设置：

```java
@Before
public void before() {
    environmentVariablesRule.set("ENV", "value1")
        .set("ENV2", "value2");
}
```

**这里要注意使用流式的set方法**，通过方法链接可以很容易地设置多个值。

我们还可以使用EnvironmentVariablesRule对象的构造函数来提供构造值：

```java
@Rule
public EnvironmentVariablesRule environmentVariablesRule = new EnvironmentVariablesRule("ENV", "value1", "ENV2", "value2");
```

构造函数有多个重载，允许以不同的形式提供变量。上面例子中的重载允许使用varargs提供任意数量的name-value对。

每个System Stubs JUnit 4 Rule都是核心存根对象之一的子类。它们也可以在整个测试类的生命周期中使用，在静态字段上使用[@ClassRule注解]()，这将导致它们在第一次测试之前被激活，然后在最后一次测试之后被清理。

### 3.3 JUnit 5环境变量

在JUnit 5测试中使用System Stubs对象之前，我们必须将扩展添加到我们的测试类中：

```java
@ExtendWith(SystemStubsExtension.class)
class EnvironmentVariablesJUnit5 {
    // tests
}
```

然后我们可以在测试类中创建一个字段供JUnit 5为我们管理。我们使用@SystemStub对此进行标注，以便扩展知道激活它：

```java
@SystemStub
private EnvironmentVariables environmentVariables;
```

该扩展将仅管理标有@SystemStub的对象，这允许我们根据需要在测试中手动使用其他System Stubs对象。

在这里，我们没有提供存根对象的任何构造。扩展为我们构造了一个，就像[Mockito扩展]()构造mock一样。

现在，我们可以使用该对象来帮助我们在其中一个测试中设置环境变量：

```java
@Test
void givenEnvironmentCanBeModified_whenSetEnvironment_thenItIsSet() {
    environmentVariables.set("ENV", "value1");

    assertThat(System.getenv("ENV")).isEqualTo("value1");
}
```

如果我们想从测试方法之外提供适用于所有测试的环境变量，我们可以在@BeforeEach方法中执行此操作，或者可以使用EnvironmentVariables的构造函数来设置我们的值：

```java
@SystemStub
private EnvironmentVariables environmentVariables = new EnvironmentVariables("ENV", "value1");
```

与EnvironmentVariablesRule一样，构造函数有几个重载，允许我们通过多种方式设置所需的变量。如果我们愿意，我们也可以使用set方法以流式的方式设置值：

```java
@SystemStub
private EnvironmentVariables environmentVariables = new EnvironmentVariables()
        .set("ENV", "value1")
        .set("ENV2", "value2");
```

我们还可以将字段设置为静态字段，以便将它们作为@BeforeAll/@AfterAll生命周期的一部分进行管理。

### 3.4 JUnit 5参数注入

虽然在我们所有的测试中使用存根对象时将它们放在字段中很有用，但我们可能更愿意只将它们用于选定的对象。这可以通过JUnit 5参数注入来实现：

```java
@Test
void givenEnvironmentCanBeModified(EnvironmentVariables environmentVariables) {
    environmentVariables.set("ENV", "value1");

    assertThat(System.getenv("ENV")).isEqualTo("value1");
}
```

在这种情况下，EnvironmentVariables对象是使用其默认构造函数为我们构建的，允许我们在单个测试中使用它。该对象也已被激活，以便它可以在运行时环境中运行，测试完成后将对其进行清理。

所有System Stubs对象都有一个默认构造函数，并且能够在运行时重新配置。我们可以在测试中注入尽可能多的东西。

### 3.5 执行环境变量

用于创建存根的原始System Lambda外观方法也可通过SystemStubs类获得。在内部，它们是通过创建存根对象的实例来实现的。有时从配方返回的对象是用于进一步配置和使用的存根对象：

```java
withEnvironmentVariable("ENV3", "val")
    .execute(() -> {
        assertThat(System.getenv("ENV3")).isEqualTo("val");
    });
```

在幕后，withEnvironmentVariable相当于：

```java
return new EnvironmentVariables().set("ENV3", "val");
```

**execute方法对于所有SystemStub对象都是通用的**。它设置对象定义的存根，然后执行传入的lambda。之后，它清理并将控制权返回给周围的测试。

如果测试代码返回一个值，则可以通过execute返回该值：

```java
String extracted = new EnvironmentVariables("PROXY", "none")
        .execute(() -> System.getenv("PROXY"));

assertThat(extracted).isEqualTo("none");
```

当我们正在测试的代码需要访问环境设置来构造某些内容时，这可能很有用。它通常用于测试AWS Lambda处理程序等内容，这些处理程序通常通过环境变量进行配置。

这种模式对于偶尔测试的优点是我们必须显式地设置存根，只在需要的地方。因此，它可以更加精确和可见。但是，它不允许我们在测试之间共享设置，并且可能更冗长。

### 3.6 多个System Stubs

我们已经演示了JUnit 4和JUnit 5插件如何为我们构建和激活存根对象。如果有多个存根，它们将由框架代码适当地设置和拆除。

然而，当我们为循环执行模式构造存根对象时，我们需要我们的测试代码在所有对象中运行。

这可以使用with/execute方法来实现。这些工作通过从与单个执行一起使用的多个存根对象创建复合来工作：

```java
with(new EnvironmentVariables("FOO", "bar"), new SystemProperties("prop", "val"))
        .execute(() -> {
            assertThat(System.getenv("FOO")).isEqualTo("bar");
            assertThat(System.getProperty("prop")).isEqualTo("val");
        });
```

现在我们已经看到了使用 System Stubs 对象的一般形式，无论有没有 JUnit 框架支持，让我们看看库的其余功能。

## 4.系统属性

我们可以 在 Java 中随时调用System.setProperty 。但是，这存在将设置从一个测试泄漏到另一个测试的风险。SystemProperties存根的主要目的 是在测试完成后将系统属性恢复为其原始设置。但是，在测试开始之前，定义应该使用哪些系统属性的通用设置代码也很有用。

### 4.1 JUnit 4系统属性

通过将规则添加到 JUnit 4 测试类，我们可以将每个测试与在其他测试方法中进行的任何System.setProperty调用隔离开来。我们还可以通过构造函数提供一些前期属性：

```java
@Rule
public SystemPropertiesRule systemProperties =
  new SystemPropertiesRule("db.connection", "false");
```

有了这个对象，我们还可以在 JUnit @Before方法中设置一些额外的属性：

```java
@Before
public void before() {
    systemProperties.set("before.prop", "before");
}
```

如果我们愿意，我们也可以在测试主体中使用set方法或使用System.setProperty 。我们只能在创建SystemPropertiesRule或 @Before方法中使用set，因为它将设置存储在规则中，以供以后应用。

### 4.2 JUnit 5系统属性

我们有两个使用SystemProperties对象的主要用例 。我们可能希望在每个测试用例之后重置系统属性，或者我们可能希望在一个中心位置准备一些公共系统属性供每个测试用例使用。

恢复系统属性需要我们将 JUnit 5 扩展和 SystemProperties字段添加到我们的测试类中：

```java
@ExtendWith(SystemStubsExtension.class)
class RestoreSystemProperties {
    @SystemStub
    private SystemProperties systemProperties;

}
```

现在，每个测试都将在之后清理它更改的任何系统属性。

我们也可以通过参数注入对选定的测试执行此操作：

```java
@Test
void willRestorePropertiesAfter(SystemProperties systemProperties) {

}

```

如果我们希望测试在其中设置属性，那么我们可以在 SystemProperties 对象的构造中分配这些属性或使用@BeforeEach方法：

```java
@ExtendWith(SystemStubsExtension.class)
class SetSomeSystemProperties {
    @SystemStub
    private SystemProperties systemProperties;

    @BeforeEach
    void before() {
        systemProperties.set("beforeProperty", "before");
    }
}
```

同样，让我们注意 JUnit 5 测试需要使用 @ExtendWith(SystemStubsExtension.class) 进行注解。如果我们不在初始化列表中提供新语句，扩展将创建 System Stubs 对象。

### 4.3 执行周围的系统属性

SystemStubs类提供了一个restoreSystemProperties方法，允许我们运行具有恢复属性的测试代码： 

```java
restoreSystemProperties(() -> {
    // test code
    System.setProperty("unrestored", "true");
});

assertThat(System.getProperty("unrestored")).isNull();
```

这需要一个不返回任何内容的 lambda。如果我们希望使用通用的设置函数来创建属性，从测试方法中获取返回值，或者通过with / execute将SystemProperties与其他存根结合起来，那么我们可以显式地创建对象：

```java
String result = new SystemProperties()
  .execute(() -> {
      System.setProperty("unrestored", "true");
      return "it works";
  });

assertThat(result).isEqualTo("it works");
assertThat(System.getProperty("unrestored")).isNull();
```

### 4.4 文件中的属性

SystemProperties和 EnvironmentVariables对象都 可以从 Map构造。这允许提供 Java 的 Properties对象作为系统属性或环境变量的来源。

PropertySource类中有一些辅助方法， 用于从文件或资源中加载 Java 属性。这些属性文件是名称/值对：

```plaintext
name=tuyucheng
version=1.0
```

我们可以 使用 fromResource函数从资源test.properties加载：

```java
SystemProperties systemProperties =
  new SystemProperties(PropertySource.fromResource("test.properties"));

```

PropertySource中对于其他来源也有类似的便利方法 ，例如 fromFile或 fromInputStream。

## 5. System Out和System Err

当我们的应用程序写入System.out 时，可能很难测试。这有时可以通过使用接口作为输出目标并在测试时模拟它来解决：

```java
interface LogOutput {
   void write(String line);
}

class Component {
    private LogOutput log;

    public void method() {
        log.write("Some output");
    }
}
```

像这样的技术适用于Mockito 模拟，但如果我们可以捕获System.out本身，则不是必需的。

### 5.1 JUnit 4 SystemOutRule和SystemErrRule

为了在JUnit 4测试中将输出捕获到System.out，我们添加了SystemOutRule：

```java
@Rule
public SystemOutRule systemOutRule = new SystemOutRule();
```

之后， 可以在测试中读取System.out的任何输出：

```java
System.out.println("line1");
System.out.println("line2");

assertThat(systemOutRule.getLines())
    .containsExactly("line1", "line2");
```

我们可以选择文本格式，上面的例子使用了getLines提供 的Stream<String\>。我们也可以选择获取整个文本块：

```java
assertThat(systemOutRule.getText())
    .startsWith("line1");
```

但是，我们应该注意，此文本将具有因平台而异的换行符。我们可以使用规范化形式在每个平台上用\n替换换行符 ：

```java
assertThat(systemOutRule.getLinesNormalized())
    .isEqualTo("line1nline2n");
```

SystemErrRule对System.err的工作方式与其对应的System.out相同：

```java
@Rule
public SystemErrRule systemErrRule = new SystemErrRule();

@Test
public void whenCodeWritesToSystemErr_itCanBeRead() {
    System.err.println("line1");
    System.err.println("line2");

    assertThat(systemErrRule.getLines())
        .containsExactly("line1", "line2");
}
```

还有一个SystemErrAndOutRule类，它同时将System.out和System.err挖掘到一个缓冲区中。

### 5.2 JUnit 5示例

与其他System Stubs对象一样，我们只需要声明SystemOut或SystemErr类型的字段或参数。这将为我们提供输出的捕获：

```java
@SystemStub
private SystemOut systemOut;

@SystemStub
private SystemErr systemErr;

@Test
void whenWriteToOutput_thenItCanBeAsserted() {
    System.out.println("to out");
    System.err.println("to err");

    assertThat(systemOut.getLines()).containsExactly("to out");
    assertThat(systemErr.getLines()).containsExactly("to err");
}
```

我们还可以使用SystemErrAndOut类将两组输出定向到同一个缓冲区。

### 5.3 执行示例

SystemStubs门面提供了一些用于点击输出并将其作为 String返回的函数：

```java
@Test
void givenTapOutput_thenGetOutput() throws Exception {
    String output = tapSystemOutNormalized(() -> {
        System.out.println("a");
        System.out.println("b");
    });

    assertThat(output).isEqualTo("anbn");
}
```

我们应该注意到，这些方法没有提供像原始对象本身那样丰富的接口。输出的捕获不能轻易地与其他存根结合起来，例如设置环境变量。

但是，可以直接使用SystemOut、SystemErr和SystemErrAndOut对象。例如，我们可以将它们与一些SystemProperties结合起来：

```java
SystemOut systemOut = new SystemOut();
SystemProperties systemProperties = new SystemProperties("a", "!");
with(systemOut, systemProperties)
    .execute(()  -> {
        System.out.println("a: " + System.getProperty("a"));
    });

assertThat(systemOut.getLines()).containsExactly("a: !");
```

### 5.4 静音

有时我们的目标不是捕获输出，而是防止它弄乱我们的测试运行日志。我们可以使用muteSystemOut或muteSystemErr函数来实现这一点：

```java
muteSystemOut(() -> {
    System.out.println("nothing is output");
});
```

我们可以通过JUnit 4 SystemOutRule在所有测试中实现相同的目标：

```java
@Rule
public SystemOutRule systemOutRule = new SystemOutRule(new NoopStream());
```

在JUnit 5中，我们可以使用相同的技术：

```java
@SystemStub
private SystemOut systemOut = new SystemOut(new NoopStream());
```

### 5.5 定制

正如我们所见，拦截输出有几种变体。它们都在库中共享一个公共基类。为方便起见，一些辅助方法和类型(如SystemErrAndOut)有助于做一些常见的事情。但是，库本身很容易定制。

我们可以提供自己的目标来捕获输出作为Output的实现。我们已经在第一个示例中看到了输出类TapStream的使用。NoopStream用于静音。我们还有DisallowWriteStream如果有东西写入它会抛出错误：

```java
// throws an exception:
new SystemOut(new DisallowWriteStream())
    .execute(() -> System.out.println("boo"));
```

## 6. 模拟系统

我们可能有一个读取stdin输入的应用程序。测试这可能涉及将算法提取到从任何InputStream读取的函数中，然后用预先准备好的输入流提供给它。一般来说，模块化代码更好，所以这是一个很好的模式。

但是，如果我们只测试核心功能，我们就会失去对提供System.in作为源代码的测试覆盖率。

无论如何，构建我们自己的流是不方便的。幸运的是，System Stubs为所有这些提供了解决方案。

### 6.1 测试输入流

System Stubs提供一系列AltInputStream类作为从InputStream读取的任何代码的替代输入：

```java
LinesAltStream testInput = new LinesAltStream("line1", "line2");

Scanner scanner = new Scanner(testInput);
assertThat(scanner.nextLine()).isEqualTo("line1");
```

在这个例子中，我们使用了一个字符串数组来构造LinesAltStream，但我们可以从Stream<String\>提供输入，允许它与任何文本数据源一起使用，而不必一次将其全部加载到内存中。

### 6.2 JUnit 4示例

我们可以使用SystemInRule在JUnit 4测试中提供输入行：

```java
@Rule
public SystemInRule systemInRule = new SystemInRule("line1", "line2", "line3");
```

然后，测试代码可以从System.in读取此输入：

```java
@Test
public void givenInput_canReadFirstLine() {
    assertThat(new Scanner(System.in).nextLine())
        .isEqualTo("line1");
}
```

### 6.3 JUnit 5示例

对于JUnit 5测试，我们创建一个SystemIn字段：

```java
@SystemStub
private SystemIn systemIn = new SystemIn("line1", "line2", "line3");
```

然后我们的测试将运行System.in提供这些行作为输入。

### 6.4 执行示例

SystemStubs门面提供withTextFromSystemIn作为工厂方法，它创建一个SystemIn对象以与其执行方法一起使用：

```java
withTextFromSystemIn("line1", "line2", "line3")
    .execute(() -> {
        assertThat(new Scanner(System.in).nextLine())
            .isEqualTo("line1");
    });
```

### 6.5 定制

SystemIn对象可以在构造时或在测试中运行时添加更多功能。

我们可以调用andExceptionThrownOnInputEnd，这会导致从System.in读取文本时抛出异常。这可以模拟从文件中读取的中断。

我们还可以使用setInputStream将输入流设置为来自任何InputStream，例如FileInputStream。我们还有LinesAltStream和TextAltStream，它们对输入文本进行操作。

## 7. 模拟系统.退出

如前所述，如果我们的代码可以调用System.exit，它可能会产生危险且难以调试的测试错误。我们对System.exit进行存根的目的之一是意外调用可跟踪的错误。另一个动机是测试软件的有意退出。

### 7.1 JUnit 4示例

让我们将SystemExitRule添加到测试类作为安全措施，以防止任何System.exit停止JVM：

```java
@Rule
public SystemExitRule systemExitRule = new SystemExitRule();
```

不过，我们也不妨看看是否使用了正确的退出代码。为此，我们需要断言代码抛出AbortExecutionException，这是调用System.exit的System Stubs信号。

```java
@Test
public void whenExit_thenExitCodeIsAvailable() {
    assertThatThrownBy(() -> {
        System.exit(123);
    }).isInstanceOf(AbortExecutionException.class);

    assertThat(systemExitRule.getExitCode()).isEqualTo(123);
}
```

在这个例子中，我们使用了来自AssertJ的assertThatThrownBy来捕捉和检查异常信号退出发生。然后我们从SystemExitRule中查看getExitCode以断言退出代码。

### 7.2 JUnit 5示例

对于JUnit 5测试，我们声明@SystemStub字段：

```java
@SystemStub
private SystemExit systemExit;
```

然后我们以与JUnit 4中的SystemExitRule相同的方式使用SystemExit类。鉴于SystemExitRule类是SystemExit的子类，它们具有相同的接口。

### 7.3 执行示例

SystemStubs类提供了catchSystemExit，它内部使用了SystemExit的执行函数：

```java
int exitCode = catchSystemExit(() -> {
    System.exit(123);
});
assertThat(exitCode).isEqualTo(123);
```

与JUnit插件示例相比，此代码不会抛出异常来指示系统退出。相反，它会捕获错误并记录退出代码。使用外观方法，它返回退出代码。

当我们直接使用execute方法时，会捕捉到退出，退出代码设置在SystemExit对象内部。然后我们可以调用getExitCode来获取退出代码，如果没有退出代码，则为null。

## 8. JUnit 5中的自定义测试资源

JUnit 4已经提供了一个[简单的结构来创建测试规则](https://www.tuyucheng.com/junit-4-rules#defining-a-custom-junit-rule)，就像System Stubs中使用的规则一样。如果我们想为某些资源创建一个新的测试规则，通过设置和拆卸，我们可以继承ExternalResource并提供before和after方法的覆盖。

JUnit 5具有更复杂的资源管理模式。对于简单的用例，可以使用System Stubs库作为起点。SystemStubsExtension对任何满足TestResource接口的东西进行操作。

### 8.1 创建测试资源

我们可以创建一个TestResource的子类，然后像使用System Stubs一样使用我们的自定义对象。需要注意的是，如果我们想使用字段和参数的自动创建，我们需要提供一个默认的构造函数。

假设我们想为一些测试打开到数据库的连接并在之后关闭它：

```java
public class FakeDatabaseTestResource implements TestResource {
    // let's pretend this is a database connection
    private String databaseConnection = "closed";

    @Override
    public void setup() throws Exception {
        databaseConnection = "open";
    }

    @Override
    public void teardown() throws Exception {
        databaseConnection = "closed";
    }

    public String getDatabaseConnection() {
        return databaseConnection;
    }
}
```

我们使用databaseConnection字符串作为数据库连接等资源的说明。我们在setup和teardown方法中修改资源的状态。

### 8.2 Execute-Around是内置的

现在让我们尝试将其与执行模式一起使用：

```java
FakeDatabaseTestResource fake = new FakeDatabaseTestResource();
assertThat(fake.getDatabaseConnection()).isEqualTo("closed");

fake.execute(() -> {
    assertThat(fake.getDatabaseConnection()).isEqualTo("open");
});
```

正如我们所见，TestResource接口赋予了它其他对象的执行能力。

### 8.3 JUnit 5测试中的自定义TestResource

我们也可以在JUnit 5测试中使用它：

```java
@ExtendWith(SystemStubsExtension.class)
class FakeDatabaseJUnit5UnitTest {

    @Test
    void useFakeDatabase(FakeDatabaseTestResource fakeDatabase) {
        assertThat(fakeDatabase.getDatabaseConnection()).isEqualTo("open");
    }
}
```

因此，很容易创建遵循System Stubs设计的其他测试对象。

## 9. JUnit 5 Spring测试的环境和属性覆盖

为Spring测试设置环境变量可能很困难。我们可能会为集成测试[编写一个自定义规则](https://www.tuyucheng.com/spring-boot-testcontainers-integration-test)来设置一些系统属性供 Spring 使用。

我们也可以使用[ApplicationContextInitializer类来插入我们的Spring Context](https://www.tuyucheng.com/spring-tests-override-properties#testPropertySourceUtils)，为测试提供额外的属性。

由于许多Spring应用程序由系统属性或环境变量覆盖控制，因此使用System Stubs在外部测试中设置这些可能更容易，而Spring测试作为内部类运行。

System Stubs文档中提供了一个[完整的示例。](https://github.com/webcompere/system-stubs/blob/master/system-stubs-jupiter/src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/SpringAppWithDynamicPropertiesTest.java)我们首先创建一个外部类：

```java
@ExtendWith(SystemStubsExtension.class)
public class SpringAppWithDynamicPropertiesTest {

    // sets the environment before Spring even starts
    @SystemStub
    private static EnvironmentVariables environmentVariables;
}
```

在这种情况下，@SystemStub字段是静态的，并在@BeforeAll方法中初始化：

```java
@BeforeAll
static void beforeAll() {
     String baseUrl = ...;

     environmentVariables.set("SERVER_URL", baseUrl);
}
```

测试生命周期中的这一点允许在Spring测试运行之前创建一些全局资源并将其应用于运行环境。

然后，我们可以将Spring测试放入@Nested类中。这导致它仅在设置父类时运行：

```java
@Nested
@SpringBootTest(classes = {RestApi.class, App.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InnerSpringTest {
    @LocalServerPort
    private int serverPort;

    // Test methods
}
```

Spring上下文是根据外部类中的@SystemStub对象设置的环境状态创建的。

这种技术还允许我们控制任何其他库的配置，这些库依赖于可能在Spring Beans后面运行的系统属性或环境变量的状态。

这可以让我们挂钩到测试生命周期，以便在Spring测试运行之前修改代理设置或HTTP连接池参数等内容。

## 10. 总结

在本文中，我们研究了能够模拟系统资源的重要性，以及System Stubs如何通过其JUnit 4和JUnit 5插件以最少的代码重复实现复杂的存根配置。

我们在测试中看到了如何提供和隔离环境变量和系统属性。然后我们研究了捕获输出并控制标准流上的输入。我们还研究了捕获和断言对System.exit的调用。

最后，我们研究了如何创建自定义测试资源以及如何将System Stubs与Spring一起使用。
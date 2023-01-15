## 1. 概述

在本教程中，我们将了解[Spock](https://www.baeldung.com/groovy-spock)扩展。

有时，我们可能需要修改或增强规范的生命周期。例如，我们想添加一些条件执行、重试随机失败的集成测试等等。为此，我们可以使用 Spock 的扩展机制。

Spock 有各种各样的扩展，我们可以将它们连接到规范的生命周期中。

让我们发现如何使用最常见的扩展。

## 2. Maven依赖

在开始之前，让我们设置我们的[Maven 依赖项](https://search.maven.org/classic/#search|ga|1| (g%3A"org.spockframework" AND a%3A"spock-core") OR (g%3A"org.codehaus.groovy" AND a%3A"groovy-all"))：

```xml
<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>z
    <version>1.3-groovy-2.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.7</version>
    <scope>test</scope>
</dependency>
```

## 3. 基于注解的扩展

Spock的大部分内置扩展都是基于注解的。

我们可以在规范类或特性上添加注释以触发特定行为。

### 3.1。@忽视

有时我们需要忽略一些特征方法或规范类。比如，我们可能需要尽快合并我们的更改，但持续集成仍然失败。我们可以忽略一些规范，仍然可以成功合并。

我们可以在方法级别使用@Ignore来跳过单个规范方法：

```groovy
@Ignore
def "I won't be executed"() {
    expect:
    true
}
```

Spock 不会执行这个测试方法。大多数 IDE 会将测试标记为已跳过。

此外，我们可以在类级别上使用@Ignore ：

```groovy
@Ignore
class IgnoreTest extends Specification
```

我们可以简单地提供一个为什么我们的测试套件或方法被忽略的原因：

```groovy
@Ignore("probably no longer needed")
```

### 3.2. @IgnoreRest

同样，我们可以忽略除一个之外的所有规范，我们可以使用@IgnoreRest注释进行标记：

```groovy
def "I won't run"() { }

@IgnoreRest
def 'I will run'() { }

def "I won't run too"() { }
```

### 3.3. @IgnoreIf

有时，我们想有条件地忽略一两个测试。在这种情况下，我们可以使用@IgnoreIf，它接受一个谓词作为参数：

```groovy
@IgnoreIf({System.getProperty("os.name").contains("windows")})
def "I won't run on windows"() { }
```

Spock 提供了一组属性和辅助类，以使我们的谓词更易于阅读和编写：

-   os – 有关操作系统的信息（请参阅spock.util.environment.OperatingSystem）。
-   jvm – JVM 的信息（参见spock.util.environment.Jvm）。
-   sys – 地图中的系统属性。
-   env - 地图中的环境变量。

我们可以在整个使用os 属性的过程中重写前面的示例。实际上，它是带有一些有用方法的spock.util.environment.OperatingSystem类，例如isWindows()：

```groovy
@IgnoreIf({ os.isWindows() })
def "I'm using Spock helper classes to run only on windows"() {}
```

注意，Spock 使用System.getProperty(...) underhood。主要目标是提供清晰的界面，而不是定义复杂的规则和条件。

此外，与前面的示例一样，我们可以在类级别应用@IgnoreIf注释。

### 3.4. @需要

有时，从@IgnoreIf 反转我们的谓词逻辑会更容易。在这种情况下，我们可以使用@Requires：

```groovy
@Requires({ System.getProperty("os.name").contains("windows") })
def "I will run only on Windows"()
```

因此，虽然 @Requires使该测试仅在操作系统为 Windows 时运行，但 @IgnoreIf 使用相同的谓词使测试仅在操作系统 不是Windows 时运行。

一般来说， 最好说测试将在什么条件下执行，而不是什么时候被忽略。

### 3.5. @PendingFeature

在TDD 中， 我们首先编写测试。然后，我们需要编写代码以使这些测试通过。在某些情况下，我们需要在实现该功能之前提交我们的测试。

这是@PendingFeature 的一个很好的用例：

```groovy
@PendingFeature
def 'test for not implemented yet feature. Maybe in the future it will pass'()
```

@Ignore和@PendingFeature之间有一个主要区别。在@PedingFeature 中，执行测试，但忽略任何失败。

如果带有@PendingFeature 标记的测试没有错误结束，则将报告为失败，以提醒删除注释。

这样，我们最初可以忽略未实现功能的失败，但在未来，这些规范将成为正常测试的一部分，而不是永远被忽略。

### 3.6. @逐步

我们可以使用@Stepwise注解按给定顺序执行规范的方法：

```groovy
def 'I will run as first'() { }

def 'I will run as second'() { }
```

一般来说，测试应该是确定性的。一个人不应该依赖另一个人。这就是为什么我们应该避免使用@Stepwise 注解。

但如果必须，我们需要注意@Stepwise不会覆盖@Ignore、@IgnoreRest或@IgnoreIf的行为。我们应该小心将这些注释与@Stepwise结合起来。

### 3.7. @暂停

我们可以限制一个规范的单个方法的执行时间并提前失败：

```groovy
@Timeout(1)
def 'I have one second to finish'() { }
```

请注意，这是单次迭代的超时时间，不包括在夹具方法中花费的时间。

默认情况下，spock.lang.Timeout使用秒作为基本时间单位。但是，我们可以指定其他时间单位：

```groovy
@Timeout(value = 200, unit = TimeUnit.SECONDS)
def 'I will fail after 200 millis'() { }
```

类级别的@Timeout与分别将其应用于每个功能方法具有相同的效果：

```groovy
@Timeout(5)
class ExampleTest extends Specification {

    @Timeout(1)
    def 'I have one second to finish'() {

    }

    def 'I will have 5 seconds timeout'() {}
}
```

在单个规范方法上使用@Timeout总是会覆盖类级别。

### 3.8. @重试

有时，我们可以进行一些非确定性的集成测试。由于异步处理或其他HTTP客户端响应等原因，这些可能会在某些运行中失败。此外，带有构建和 CI 的远程服务器将失败并迫使我们运行测试并再次构建。

为了避免这种情况，我们可以在方法或类级别上使用@Retry 注解来重复失败的测试：

```groovy
@Retry
def 'I will retry three times'() { }
```

默认情况下，它将重试 3 次。

确定我们应该重试测试的条件非常有用。我们可以指定例外列表：

```groovy
@Retry(exceptions = [RuntimeException])
def 'I will retry only on RuntimeException'() { }
```

或者当有特定的异常消息时：

```groovy
@Retry(condition = { failure.message.contains('error') })
def 'I will retry with a specific message'() { }
```

延迟重试非常有用：

```groovy
@Retry(delay = 1000)
def 'I will retry after 1000 millis'() { }
```

最后，几乎像往常一样，我们可以在类级别指定重试：

```groovy
@Retry
class RetryTest extends Specification
```

### 3.9. @RestoreSystemProperties

我们可以使用@RestoreSystemProperties操作环境变量。

此注释在应用时会保存变量的当前状态并在之后恢复它们。它还包括设置或清理方法：

```groovy
@RestoreSystemProperties
def 'all environment variables will be saved before execution and restored after tests'() {
    given:
    System.setProperty('os.name', 'Mac OS')
}
```

请注意，当我们操作系统属性时，我们不应该同时运行测试。我们的测试可能是不确定的。

### 3.10。人性化的标题

我们可以使用@Title注解添加一个人性化的测试标题：

```groovy
@Title("This title is easy to read for humans")
class CustomTitleTest extends Specification
```

类似地，我们可以使用@Narrative注释和多行Groovy S字符串添加规范描述：

```groovy
@Narrative("""
    as a user
    i want to save favourite items 
    and then get the list of them
""")
class NarrativeDescriptionTest extends Specification
```

### 3.11。@看

要链接一个或多个外部引用，我们可以使用 @See注解：

```groovy
@See("https://example.org")
def 'Look at the reference'()
```

要传递多个链接，我们可以使用 Groovy []操作数来创建一个列表：

```groovy
@See(["https://example.org/first", "https://example.org/first"])
def 'Look at the references'()
```

### 3.12。@问题

我们可以表示一个特征方法是指一个问题或多个问题：

```groovy
@Issue("https://jira.org/issues/LO-531")
def 'single issue'() {

}

@Issue(["https://jira.org/issues/LO-531", "http://jira.org/issues/LO-123"])
def 'multiple issues'()
```

### 3.13。@主题

最后，我们可以用@Subject指明哪个类是被测类：

```groovy
@Subject
ItemService itemService // initialization here...
```

目前，它仅供参考。

## 4. 配置扩展

我们可以在 Spock 配置文件中配置一些扩展。这包括描述每个扩展的行为方式。

通常，我们在 Groovy 中创建一个配置文件， 例如SpockConfig.groovy。

当然，Spock 需要找到我们的配置文件。首先，它从spock.configuration 系统属性中读取一个自定义位置，然后尝试在类路径中查找该文件。找不到时，它会转到文件系统中的某个位置。如果仍未找到，则在测试执行类路径中查找SpockConfig.groovy 。

最终，Spock 转到 Spock 用户主目录，它只是我们主目录中的一个目录.spock 。我们可以通过设置名为spock.user.home 的系统属性或环境变量SPOCK_USER_HOME 来更改此目录。

对于我们的示例，我们将创建一个文件SpockConfig .groovy并将其放在类路径中（src/test/resources/SpockConfig.Groovy）。

### 4.1。过滤堆栈跟踪

通过使用配置文件，我们可以过滤（或不过滤）堆栈跟踪：

```groovy
runner {
    filterStackTrace false
}
```

默认值为真。

为了看看它是如何工作和实践的，让我们创建一个抛出RuntimeException 的简单测试：

```groovy
def 'stacktrace'() {
    expect:
    throw new RuntimeException("blabla")
}
```

当filterStackTrace 设置为 false 时，我们将在输出中看到：

```plaintext
java.lang.RuntimeException: blabla

  at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
  at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
  at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
  at org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:83)
  at org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrapNoCoerce.callConstructor(ConstructorSite.java:105)
  at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:60)
  at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:235)
  at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:247)
  // 34 more lines in the stack trace...

```

通过将此属性设置为true，我们将获得：

```plaintext
java.lang.RuntimeException: blabla

  at extensions.StackTraceTest.stacktrace(StackTraceTest.groovy:10)
```

尽管请记住，有时查看完整的堆栈跟踪很有用。

### 4.2. Spock 配置文件中的条件特性

有时，我们可能需要有条件地过滤堆栈跟踪。例如，我们需要在持续集成工具中查看完整的堆栈跟踪，但这在我们的本地机器上不是必需的。

我们可以添加一个简单的条件，例如基于环境变量：

```groovy
if (System.getenv("FILTER_STACKTRACE") == null) {   
    filterStackTrace false
}
```

Spock 配置文件是一个 Groovy 文件，因此它可以包含 Groovy 代码片段。

### 4.3. @Issue中的前缀和 URL

之前，我们讨论了@Issue注解。我们也可以使用配置文件来配置它，通过使用issueUrlPrefix 定义一个公共 URL 部分。

另一个属性是issueNamePrefix。然后，每个@Issue 值前面都有issueNamePrefix 属性。

我们需要在报告中添加这两个属性：

```plaintext
report {
    issueNamePrefix 'Bug '
    issueUrlPrefix 'https://jira.org/issues/'
}
```

### 4.4. 优化运行顺序

另一个非常有用的工具是optimizeRunOrder。Spock 可以记住哪些规范失败了，以及它需要执行功能方法的频率和时间。

基于这些知识，Spock将首先运行上次运行失败的功能。首先，它将更连续地执行失败的规范。此外，最快的规格将首先运行。

可以在 配置文件中启用此行为。为了启用优化器，我们使用optimizeRunOrder 属性：

```groovy
runner {
  optimizeRunOrder true
}
```

默认情况下，运行顺序的优化器被禁用。

### 4.5. 包括和不包括规格

Spock 可以排除或包含某些规格。我们可以依靠应用于规范类的类、超类、接口或注释。基于特征级别的注释，该库能够排除或包括单个特征。

我们可以使用exclude属性简单地从类TimeoutTest中排除一个测试套件：

```groovy
import extensions.TimeoutTest

runner {
    exclude TimeoutTest
}
```

TimeoutTest 及其所有子类将被排除。如果TimeoutTest是应用于规范类的注释，则该规范将被排除。

我们可以分别指定注解和基类：

```plaintext
import extensions.TimeoutTest
import spock.lang.Issue
    exclude {
        baseClass TimeoutTest
        annotation Issue
}
```

上面的示例将排除带有@Issue 注释的测试类或方法以及TimeoutTest或其任何子类。

要包含任何规范，我们只需使用include 属性。我们可以像exclude一样定义include的规则。

### 4.6. 创建报告

根据测试结果和先前已知的注释，我们可以使用Spock 生成报告。此外，此报告将包含@Title、@See、@Issue 和 @Narrative值等内容。

我们可以在配置文件中启用生成报告。默认情况下，它不会生成报告。

我们所要做的就是传递一些属性的值：

```groovy
report {
    enabled true
    logFileDir '.'
    logFileName 'report.json'
    logFileSuffix new Date().format('yyyy-MM-dd')
}
```

上面的属性是：

-   启用 - 是否应该生成报告
-   logFileDir – 报告目录
-   logFileName – 报告的名称
-   logFileSuffix – 每个生成的报告基本名称的后缀，用破折号分隔

当我们将enabled设置为true 时，必须设置logFileDir和logFileName 属性。logFileSuffix是可选的。

我们还可以在系统属性中设置所有这些：enabled、spock.logFileDir、spock.logFileName和spock.logFileSuffix。

## 5. 结论

在本文中，我们描述了最常见的 Spock 扩展。

我们知道大部分都是基于注解的。此外，我们学习了如何创建Spock 配置文件，以及可用的配置选项是什么。简而言之，我们新获得的知识对于编写有效且易于阅读的测试非常有帮助。
## 1. 概述

在本教程中，我们将介绍在JUnit 5中如何使用注解有条件地执行测试。

这些注解来自junit-jupiter库的condition包，允许我们指定测试应该或不应该运行的不同类型的条件。

## 2. 操作系统条件

有时，我们需要根据测试所运行的操作系统(OS)更改我们的测试场景，在这些情况下，@EnabledOnOs注解能派上用场。

@EnabledOnOs的使用很简单，我们只需要给它一个OS(枚举)类型的值。此外，当我们想要针对多个操作系统时，我们可以传递一个数组。

例如，假设我们希望只在Windows和macOS上运行测试：

```java
class ConditionalAnnotationsUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalAnnotationsUnitTest.class);

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    void shouldRunBothWindowsAndMac() {
        LOGGER.debug("runs on Windows and Mac");
    }
}
```

与@EnabledOnOs作用相反的是@DisabledOnOs。顾名思义，它根据指定的OS类型参数禁用测试：

```java
@Test
@DisabledOnOs(OS.LINUX)
void shouldNotRunAtLinux() {
    LOGGER.info("will not run on Linux");
}
```

## 3. Java运行时环境条件

我们还可以使用@EnableOnJre和@DisableOnJre注解将我们的测试定位为在特定JRE版本上运行，这些注解也支持指定一个数组来启用或禁用多个Java版本：

```java
@Test
@EnabledOnJre({JRE.JAVA_10, JRE.JAVA_11})
void shouldOnlyRunOnJava10And11() {
    LOGGER.info("runs with java 10 and 11");
}
```

从JUnit 5.6版本开始，我们可以使用@EnabledForJreRange注解为特定范围内的JRE版本启用测试：

```java
@Test
@EnabledForJreRange(min = JRE.JAVA_8, max = JRE.JAVA_17)
void shouldOnlyRunOnJava8UntilJava17() {
    LOGGER.info("runs with Java 8, 9, 10, 11, 12, 13, 14, 15, 16, 17");
}
```

默认情况下，最小值是JAVA_8，最大值是可能的最大JRE版本。还有一个@DisabledForJreRange注解可以禁用对特定Java版本范围的测试：

```java
@Test
@DisabledForJreRange(min = JRE.JAVA_14, max = JRE.JAVA_15)
void shouldNotBeRunOnJava14AndJava15() {
    LOGGER.info("Should not be run on Java 14 and 15.");
}
```

此外，如果我们想禁用使用8、9、10、11、12、13、14、15、16、17、18以外的Java版本运行的测试，我们可以使用JRE枚举的OTHER属性：

```java
@Test
@DisabledOnJre(JRE.OTHER)
void thisTestOnlyRunsWithUpToDateJREs() {
    LOGGER.info("this test will only run on Java 17");
}
```

## 4. 系统属性条件

如果我们想基于JVM系统属性启用测试，我们可以使用@EnabledIfSystemProperty注解。

要使用它，我们必须提供named和matches参数。named参数用于指定确切的系统属性名，matches用于使用正则表达式定义属性值的模式。

例如，假设我们希望仅在虚拟机供应商名称以“Oracle”开头时才运行测试：

```java
@Test
@EnabledIfEnvironmentVariable(named = "java.vm.vendor", matches = "Oracle.*")
void onlyIfVendorNameStartsWithOracle() {
    LOGGER.info("runs only if vendor name starts with Oracle");
}
```

同样，我们可以使用@DisabledIfSystemProperty来禁用基于JVM系统属性的测试：

```java
@Test
@DisabledIfSystemProperty(named = "file.separator", matches = "[/]")
void disabledIfFileSeparatorIsSlash() {
    LOGGER.info("Will not run if file.separator property is /");
}
```

## 5. 环境变量条件

我们还可以使用@EnabledIfEnvironmentVariable和@DisabledIfEnvironmentVariable注解为我们的测试指定环境变量条件。

而且，就像系统属性条件的注解一样，这些注解也使用两个参数named和matched，用于指定环境变量名称和正则表达式以匹配环境变量值：

```java
@Test
@EnabledIfEnvironmentVariable(named = "GDMSESSION", matches = "ubuntu")
void onlyRunOnUbuntuServer() {
    LOGGER.info("only runs if GDMSESSION is ubuntu");
}

@Test
@DisabledIfEnvironmentVariable(named = "LC_TIME", matches = ".*UTF-8.")
void shouldNotRunWhenTimeIsNotUTF8() {
    LOGGER.info("will not run if environment variable LC_TIME is UTF-8");
}
```

## 6. 基于脚本的条件

### 6.1 弃用说明

基于脚本的条件API及其实现在JUnit 5.5中已弃用，并从JUnit 5.6中删除。为了达到相同的效果，建议使用内置条件的组合或创建ExecutionCondition的自定义实现。

### 6.2 Conditions

在JUnit 5.6之前，我们可以通过在@EnabledIf和@DisabledIf注解中编写脚本来指定测试的运行条件。

这些注解包含三个参数：

+ value：包含要运行的实际脚本
+ engine(可选)：指定要使用的脚本引擎，默认值为Oracle Nashorn
+ reason(可选)：用于日志记录目的，指定JUnit在测试失败时应打印的消息

让我们来看一个简单的例子，其中我们只指定了一行脚本，注解中没有配置其他参数：

```java
@Test
@EnabledIf("'FR' == systemProperty.get('user.country')")
public void onlyFrenchPeopleWillRunThisMethod() {
    LOGGER.debug("will run only if user.country is FR");
}
```

此外，@DisabledIf的用法完全相同：

```java
@Test
@DisabledIf("java.lang.System.getProperty('os.name').toLowerCase().contains('mac')")
public void shouldNotRunOnMacOS() {
    LOGGER.debug("will not run if our os.name is mac");
}
```

**此外，我们可以使用value参数编写多行脚本**。

让我们写一个简短的例子来在运行测试之前检查月份的名称。

我们将使用支持的占位符定义一个句子来表示原因：

+ {annotation}：表示注解实例的字符串
+ {script}：在value参数中计算的脚本文本
+ {result}：表示脚本计算的返回值的字符串

对于这种情况，我们将在value参数和engine和reason的值中使用多行脚本：

```java
@Test
@EnabledIf(value = {
        "load('nashorn:mozilla_compat.js')",
        "importPackage(java.time)",
        "",
        "var thisMonth = LocalDate.now().getMonth().name()",
        "var february = Month.FEBRUARY.name()",
        "thisMonth.equals(february)"
},
        engine = "nashorn",
        reason = "On {annotation}, with script: {script}, result is: {result}")
public void onlyRunsInFebruary() {
    // ...
}
```

在编写脚本时，我们可以使用多个脚本绑定：

+ systemEnvironment：访问系统环境变量
+ systemProperty：访问系统属性变量
+ junitConfigurationParameter：访问配置参数
+ junitDisplayName：测试或容器的显示名称
+ junitTags：访问测试或容器上的标签
+ anotherUniqueId：获取测试或容器的唯一id

最后，让我们看另一个例子，看看如何将脚本与绑定一起使用：

```java
@Test
@DisabledIf("systemEnvironment.get('XPC_SERVICE_NAME') != null && systemEnvironment.get('XPC_SERVICE_NAME').contains('intellij')")
public void notValidForIntelliJ() {
    // this method will not run on intelliJ
}
```

## 7. 创建自定义条件注解

JUnit 5提供的一个非常强大的功能是我们能够创建自定义注解，**我们可以通过使用现有条件注解的组合来定义自定义条件注解**。

例如，假设我们想要定义所有测试以针对具有特定JRE版本的特定操作系统类型运行，我们可以为此编写一个自定义注解：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@DisabledOnOs({OS.SOLARIS, OS.OTHER})
@EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11, JRE.JAVA_17})
@interface ThisTestWillOnlyRunAtLinuxAndMacWithJava8Or11Or17 {
}

@ThisTestWillOnlyRunAtLinuxAndMacWithJava8Or11Or17
void someSuperTestMethodHere() {
    LOGGER.info("this method will run with java8, 11, 17 and Linux or macOS or Windows.");
}
```

此外，我们可以使用基于脚本的注解来创建自定义注解：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@DisabledIf("Math.random() >= 0.5")
@interface CoinToss {
}

@RepeatedTest(2)
@CoinToss
void gamble() {
    LOGGER.debug("This tests run status is a gamble with %50 rate");
}
```

## 8. 总结

在本文中，我们通过一些实际例子介绍了如何在JUnit 5中使用注解执行条件测试。此外，我们介绍了如何创建自定义条件注解。

要了解有关条件测试的更多信息，我们可以阅读[JUnit 5官方文档](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution)。
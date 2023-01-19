## 1. 概述

在本教程中，我们将学习如何使用 [JCommander](https://github.com/cbeust/jcommander) 来解析命令行参数。我们将在构建一个简单的命令行应用程序时探索它的几个特性。

## 2. 为什么选择 JCommander？

“因为生命太短暂，无法解析命令行参数” ——Cédric Beust

JCommander 由 Cédric Beust 创建，是一个用于解析命令行参数的基于注解的库 。它可以减少构建命令行应用程序的工作量，并帮助我们为他们提供良好的用户体验。

使用 JCommander，我们可以卸载解析、验证和类型转换等棘手的任务，让我们能够专注于我们的应用程序逻辑。

## 3.设置JCommander

### 3.1. Maven 配置

让我们首先在pom.xml中添加[jcommander](https://search.maven.org/search?q=g:com.beust AND a: jcommander)依赖项：

```xml
<dependency>
    <groupId>com.beust</groupId>
    <artifactId>jcommander</artifactId>
    <version>1.78</version>
</dependency>
```

### 3.2. 你好世界

让我们创建一个简单的HelloWorldApp ，它接受名为name的单个输入并打印问候语 “Hello <name>”。

由于JCommander 将命令行参数绑定到Java类中的字段，我们将首先定义一个HelloWorldArgs类，其字段名称用 @Parameter注解：

```java
class HelloWorldArgs {

    @Parameter(
      names = "--name",
      description = "User name",
      required = true
    )
    private String name;
}
```

现在，让我们使用JCommander类来解析命令行参数并在我们的HelloWorldArgs对象中分配字段：

```java
HelloWorldArgs jArgs = new HelloWorldArgs();
JCommander helloCmd = JCommander.newBuilder()
  .addObject(jArgs)
  .build();
helloCmd.parse(args);
System.out.println("Hello " + jArgs.getName());
```

最后，让我们从控制台使用相同的参数调用主类：

```shell
$ java HelloWorldApp --name JavaWorld
Hello JavaWorld
```

## 4. 在 JCommander 中构建一个真实的应用程序

现在我们已经启动并运行了，让我们考虑一个更复杂的用例——一个与计费应用程序(如[Stripe](https://www.baeldung.com/java-stripe-api) )交互的命令行 API 客户端，特别是[计量](https://stripe.com/docs/billing/subscriptions/metered-billing)(或基于使用的)计费场景。此第三方计费服务管理我们的订阅和发票。

假设我们正在经营 SaaS 业务，我们的客户购买我们服务的订阅，并按每月对我们服务的 API 调用次数付费。我们将在我们的客户端执行两个操作：

-   submit：针对给定订阅提交客户的使用数量和单价
-   fetch：根据客户当月部分或全部订阅的消费获取客户的费用——我们可以将这些费用汇总到所有订阅或按每个订阅逐项列出

我们将在了解库的功能时构建 API 客户端。

让我们开始！

## 5.定义参数

让我们从定义我们的应用程序可以使用的参数开始。

### 5.1. @Parameter注解_

[用@Parameter](https://jcommander.org/)注解字段 告诉 JCommander 将匹配的命令行参数绑定到它。@Parameter有描述主要参数的属性，比如：

-   名称—— 选项的一个或多个名称，例如“–name”或“-n”
-   描述 ——选项背后的含义，以帮助最终用户
-   required – 该选项是否强制，默认为false
-   arity——选项消耗的附加参数的数量

让我们在计量计费场景中配置一个参数customerId ：

```java
@Parameter(
  names = { "--customer", "-C" },
  description = "Id of the Customer who's using the services",
  arity = 1,
  required = true
)
String customerId;

```

现在，让我们使用新的“–customer”参数执行我们的命令：

```bash
$ java App --customer cust0000001A
Read CustomerId: cust0000001A.

```

同样，我们可以使用更短的“-C”参数来达到同样的效果：

```bash
$ java App -C cust0000001A
Read CustomerId: cust0000001A.

```

### 5.2. 必需参数

在强制参数的情况下，如果用户未指定参数，应用程序将退出并抛出ParameterException ：

```bash
$ java App
Exception in thread "main" com.beust.jcommander.ParameterException:
  The following option is required: [--customer | -C]
```

我们应该注意，一般来说，解析参数时的任何错误都会导致 JCommander 中出现[ParameterException](https://jcommander.org/#_exception) 。

## 6.内置类型

### 6.1. IStringConverter接口

JCommander 执行从命令行String输入到我们参数类中的Java类型的类型转换。IStringConverter接口处理参数从String到任意类型[的类型转换。](https://jcommander.org/#_custom_types_converters_and_splitters)因此，所有 JCommander 的内置转换器都实现了这个接口。

开箱即用的 JCommander 支持常见数据类型，例如String、Integer、Boolean、BigDecimal和Enum。

### 6.2. 单元类型

Arity 与选项消耗的附加参数的数量有关。JCommander 的内置参数类型的默认元数为 one ， Boolean和List除外。因此，常见类型(例如 String、Integer、BigDecimal、 Long和Enum)是单元数类型。

### 6.3. 布尔类型

boolean或Boolean类型的字段不需要任何附加参数—— 这些选项的元数为零。

让我们看一个例子。也许我们想为客户获取费用，按订阅逐项列出。我们可以添加一个布尔字段itemized，默认情况下为false：

```java
@Parameter(
  names = { "--itemized" }
)
private boolean itemized;

```

我们的应用程序将返回itemized设置为 false的汇总费用。当我们使用 逐项参数调用命令行时，我们将字段设置为true：

```bash
$ java App --itemized
Read flag itemized: true.

```

这很有效，除非我们有一个我们总是想要 itemized charges 的用例，除非另有说明。我们可以将参数更改为notItemized，但能够提供false作为itemized的值可能会更清楚。

让我们通过为字段使用默认值true并将其 arity设置为 1 来引入此行为：

```java
@Parameter(
  names = { "--itemized" },
  arity = 1
)
private boolean itemized = true;

```

现在，当我们指定选项时，该值将设置为false：

```bash
$ java App --itemized false
Read flag itemized: false.

```

## 7.列表类型

JCommander 提供了几种将参数绑定到列表 字段的方法。

### 7.1. 多次指定参数

假设我们只想获取客户订阅的一部分的费用：

```java
@Parameter(
  names = { "--subscription", "-S" }
)
private List<String> subscriptionIds;

```

该字段不是强制性的，如果未提供该参数，应用程序将获取所有订阅的费用。但是，我们可以通过多次使用参数名称来指定多个订阅：

```bash
$ java App -S subscriptionA001 -S subscriptionA002 -S subscriptionA003
Read Subscriptions: [subscriptionA001, subscriptionA002, subscriptionA003].

```

### 7.2. 使用拆分器绑定列表

让我们尝试通过传递逗号分隔的String来绑定列表，而不是多次指定选项：

```bash
$ java App -S subscriptionA001,subscriptionA002,subscriptionA003
Read Subscriptions: [subscriptionA001, subscriptionA002, subscriptionA003].

```

这使用单个参数值 (arity = 1) 来表示列表。JCommander 将使用 CommaParameterSplitter 类将逗号分隔的String绑定到我们的List。

### 7.3. 使用自定义拆分器绑定列表

我们可以通过实现IParameterSplitter接口来覆盖默认的拆分器：

```java
class ColonParameterSplitter implements IParameterSplitter {

    @Override
    public List split(String value) {
        return asList(value.split(":"));
    }
}
```

然后将实现映射到@Parameter中的splitter属性 ：

```java
@Parameter(
  names = { "--subscription", "-S" },
  splitter = ColonParameterSplitter.class
)
private List<String> subscriptionIds;

```

让我们试试看：

```bash
$ java App -S "subscriptionA001:subscriptionA002:subscriptionA003"
Read Subscriptions: [subscriptionA001, subscriptionA002, subscriptionA003].

```

### 7.4. 变量列表

变量 arity 允许我们声明 可以采用不确定参数的列表，直到下一个选项。我们可以将属性variableArity设置为 true来指定此行为。

让我们试试这个来解析订阅：

```java
@Parameter(
  names = { "--subscription", "-S" },
  variableArity = true
)
private List<String> subscriptionIds;

```

当我们运行命令时：

```bash
$ java App -S subscriptionA001 subscriptionA002 subscriptionA003 --itemized
Read Subscriptions: [subscriptionA001, subscriptionA002, subscriptionA003].

```

JCommander 将选项“-S”之后的所有输入参数绑定到列表字段，直到下一个选项或命令结束。

### 7.5. 固定 Arity列表

到目前为止，我们已经看到了无限列表，我们可以在其中传递任意数量的列表项。有时，我们可能希望限制传递给列表字段的项目数。为此，我们可以为List字段指定一个整数 arity 值以使其有界 ：

```java
@Parameter(
  names = { "--subscription", "-S" },
  arity = 2
)
private List<String> subscriptionIds;

```

Fixed arity 强制检查传递给List选项的参数数量，并 在出现违规情况时抛出ParameterException ：

```bash
$ java App -S subscriptionA001 subscriptionA002 subscriptionA003
Was passed main parameter 'subscriptionA003' but no main parameter was defined in your arg class

```

错误消息表明，由于 JCommander 只需要两个参数，因此它尝试将额外的输入参数“subscriptionA003”解析为下一个选项。

## 8.自定义类型

我们还可以通过编写自定义转换器来绑定参数。与内置转换器一样，自定义转换器必须实现IStringConverter接口。

让我们编写一个转换器来解析 [ISO8601 时间戳](https://en.wikipedia.org/wiki/ISO_8601#Combined_date_and_time_representations)：

```java
class ISO8601TimestampConverter implements IStringConverter<Instant> {

    private static final DateTimeFormatter TS_FORMATTER = 
      DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    @Override
    public Instant convert(String value) {
        try {
            return LocalDateTime
              .parse(value, TS_FORMATTER)
              .atOffset(ZoneOffset.UTC)
              .toInstant();
        } catch (DateTimeParseException e) {
            throw new ParameterException("Invalid timestamp");
        }
    }
}

```

此代码将解析输入String并返回Instant ，如果存在转换错误则抛出ParameterException 。我们可以通过使用@Parameter 中的 converter属性 将它绑定到[Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)类型的字段来使用这个转换器：

```java
@Parameter(
  names = { "--timestamp" },
  converter = ISO8601TimestampConverter.class
)
private Instant timestamp;

```

让我们看看它的实际效果：

```bash
$ java App --timestamp 2019-10-03T10:58:00
Read timestamp: 2019-10-03T10:58:00Z.
```

## 9. 验证参数

JCommander 提供了一些默认验证：

-   是否提供了必需的参数
-   如果指定的参数数量与字段的元数匹配
-   每个String参数是否可以转换成对应字段的类型

此外，我们可能希望添加自定义验证。例如，假设客户 ID 必须是 [UUID](https://www.baeldung.com/java-uuid)。

[我们可以为实现接口IParameterValidator](https://jcommander.org/#_parameter_validation)的客户字段编写一个验证器：

```java
class UUIDValidator implements IParameterValidator {

    private static final String UUID_REGEX = 
      "[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (!isValidUUID(value)) {
            throw new ParameterException(
              "String parameter " + value + " is not a valid UUID.");
        }
    }

    private boolean isValidUUID(String value) {
        return Pattern.compile(UUID_REGEX)
          .matcher(value)
          .matches();
    }
}

```

然后，我们可以将它与参数的validateWith属性挂钩：

```java
@Parameter(
  names = { "--customer", "-C" },
  validateWith = UUIDValidator.class
)
private String customerId;

```

如果我们使用非 UUID 客户 ID 调用命令，应用程序将退出并显示一条验证失败消息：

```bash
$ java App --C customer001
String parameter customer001 is not a valid UUID.

```

## 10. 子命令

现在我们已经了解了参数绑定，让我们把所有东西放在一起来构建我们的命令。

在 JCommander 中，我们可以支持多个命令，称为子命令，每个命令都有一组不同的选项。

### 10.1. @Parameters注解

我们可以使用[@Parameters](https://jcommander.org/#_main_parameter)来定义子命令。 @Parameters包含 用于标识命令的属性 commandNames 。

让我们将提交和获取建模 为子命令：

```java
@Parameters(
  commandNames = { "submit" },
  commandDescription = "Submit usage for a given customer and subscription, " +
    "accepts one usage item"
)
class SubmitUsageCommand {
    //...
}

@Parameters(
  commandNames = { "fetch" },
  commandDescription = "Fetch charges for a customer in the current month, " +
    "can be itemized or aggregated"
)
class FetchCurrentChargesCommand {
    //...
}

```

JCommander 使用@Parameters 中的属性来配置子命令，例如：

-   commandNames – 子命令的名称；将命令行参数绑定到用@Parameters注解的类
-   commandDescription – 记录子命令的用途

### 10.2. 向JCommander添加子命令

我们使用 addCommand方法将子命令添加到JCommander：

```java
SubmitUsageCommand submitUsageCmd = new SubmitUsageCommand();
FetchCurrentChargesCommand fetchChargesCmd = new FetchCurrentChargesCommand();

JCommander jc = JCommander.newBuilder()
  .addCommand(submitUsageCmd)
  .addCommand(fetchChargesCmd)
  .build();

```

addCommand方法使用在@Parameters 注解的commandNames 属性中指定的各自名称注册子命令。

### 10.3. 解析子命令

要访问用户选择的命令，我们必须首先解析参数：

```java
jc.parse(args);

```

接下来，我们可以使用getParsedCommand提取子命令：

```java
String parsedCmdStr = jc.getParsedCommand();

```

除了识别命令之外，JCommander 还将其余的命令行参数绑定到子命令中它们的字段。现在，我们只需要调用我们想要使用的命令：

```java
switch (parsedCmdStr) {
    case "submit":
        submitUsageCmd.submit();
        break;

    case "fetch":
        fetchChargesCmd.fetch();
        break;

    default:
        System.err.println("Invalid command: " + parsedCmdStr);
}

```

## 11. JCommander 使用帮助

我们可以调用usage来呈现使用指南。这是我们的应用程序使用的所有选项的摘要。在我们的应用程序中，我们可以调用主命令的用法，或者分别调用“提交”和“获取”这两个命令中的每一个。

用法显示可以通过多种方式帮助我们：显示帮助选项和在错误处理期间。

### 11.1. 显示帮助选项

我们可以使用布尔参数以及将属性 help设置为true在我们的命令中绑定帮助选项：

```java
@Parameter(names = "--help", help = true)
private boolean help;

```

然后，我们可以检测参数中是否传递了“–help”，并调用usage：

```java
if (cmd.help) {
  jc.usage();
}

```

让我们看看“提交”子命令的帮助输出：

```bash
$ java App submit --help
Usage: submit [options]
  Options:
   --customer, -C     Id of the Customer who's using the services
   --subscription, -S Id of the Subscription that was purchased
   --quantity         Used quantity; reported quantity is added over the 
                       billing period
   --pricing-type, -P Pricing type of the usage reported (values: [PRE_RATED, 
                       UNRATED]) 
   --timestamp        Timestamp of the usage event, must lie in the current 
                       billing period
    --price            If PRE_RATED, unit price to be applied per unit of 
                       usage quantity reported

```

usage方法使用 @Parameter属性(例如description)来显示有用的摘要。标有星号 () 的参数是必需的。

### 11.2. 错误处理

我们可以捕获ParameterException并调用用法来帮助用户理解为什么他们的输入不正确。ParameterException 包含 JCommander实例以显示帮助：

```java
try {
  jc.parse(args);

} catch (ParameterException e) {
  System.err.println(e.getLocalizedMessage());
  jc.usage();
}

```

## 12. 总结

在本教程中，我们使用 JCommander 构建命令行应用程序。虽然我们介绍了许多主要功能，但官方[文档](http://jcommander.org/)中还有更多内容。
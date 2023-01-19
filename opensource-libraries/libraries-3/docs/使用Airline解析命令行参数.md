## 1. 简介

在本教程中，我们将介绍[Airline——](https://rvesse.github.io/airline/)一个用于构建命令行界面 (CLI) 的注解驱动的Java库。

## 2.场景

在构建命令行应用程序时，很自然地会创建一个简单的界面以允许用户根据需要塑造输出。几乎每个人都使用过 Git CLI，并且可以感受到它的强大而简单。唉，在构建这样的界面时，很少有工具能派上用场。

该航空公司 旨在减少通常与Java中的 CLI 相关联的样板代码，因为大多数常见行为都可以通过注解和零用户代码实现。

我们将实施一个小型Java程序，该程序将利用 Airline 的功能来模拟通用 CLI。它将公开用于设置程序配置的用户命令，例如定义数据库 URL、凭据和记录器详细信息。我们还将深入了解我们的库，并使用比其基础知识更多的内容来探索它是否可以处理一些复杂性。

## 3.设置

首先，让我们将[Airline](https://search.maven.org/search?q=g:com.github.rvesse a:airline)依赖项添加到我们的pom.xm l 中：

```xml
<dependency>
    <groupId>com.github.rvesse</groupId>
    <artifactId>airline</artifactId>
    <version>2.7.2</version>
</dependency>

```

## 4. 一个简单的 CLI

让我们为应用程序创建入口点——CommandLine类：

```java
@Cli(name = "baeldung-cli",
  description = "Baeldung Airline Tutorial",
  defaultCommand = Help.class)
public class CommandLine {
    public static void main(String[] args) {
        Cli<Runnable> cli = new Cli<>(CommandLine.class);
        Runnable cmd = cli.parse(args);
        cmd.run();
    }
}
```

通过一个简单的@Cli注解，我们定义了将在我们的应用程序上运行的默认命令——Help命令。

Help类作为 Airline 库的一部分出现，并使用-h或–help选项公开默认帮助命令。

就这样，基本设置就完成了。

## 5. 我们的第一个命令

让我们实现我们的第一个命令，一个简单的LoggingCommand类，它将控制我们日志的详细程度。我们将使用@Command注解该类，以确保在用户调用setup-log时应用正确的命令：

```java
@Command(name = "setup-log", description = "Setup our log")
public class LoggingCommand implements Runnable {

    @Inject
    private HelpOption<LoggingCommand> help;
	
    @Option(name = { "-v", "--verbose" }, 
      description = "Set log verbosity on/off")
    private boolean verbose = false;

    @Override
    public void run() {
        if (!help.showHelpIfRequested())
            System.out.println("Verbosity: " + verbose);
        }
    }
}
```

让我们仔细看看我们的示例命令。

首先，我们已经设置了一个描述，这样我们的助手，由于注入，将在请求时显示我们的命令选项。

然后我们声明了一个布尔变量verbose，并用@Option 对其进行了注解，为其命名、描述，以及一个别名-v/–verbose 来表示我们控制冗长的命令行选项。

最后，在run方法中，我们指示我们的命令在用户请求帮助时停止。

到目前为止，一切都很好。现在，我们需要通过修改@Cli注解将我们的新命令添加到主界面：

```java
@Cli(name = "baeldung-cli",
description = "Baeldung Airline Tutorial",
defaultCommand = Help.class,
commands = { LoggingCommand.class, Help.class })
public class CommandLine {
    public static void main(String[] args) {
        Cli<Runnable> cli = new Cli<>(CommandLine.class);
        Runnable cmd = cli.parse(args);
        cmd.run();
    }
}

```

现在，如果我们将setup-log -v传递给我们的程序，它将运行我们的逻辑。

## 6.约束和更多

我们已经看到 Airline 如何完美地生成 CLI，但是……还有更多！

我们可以为我们的参数指定约束(或限制)来处理允许的值、要求或依赖性等。

我们将创建一个DatabaseSetupCommand类，它将响应setup-db命令；和我们之前做的一样，但我们会添加一些香料。

首先，我们将请求数据库类型，通过@AllowedRawValues只接受 3 个有效值：

```java
@AllowedRawValues(allowedValues = { "mysql", "postgresql", "mongodb" })
@Option(type = OptionType.COMMAND,
  name = {"-d", "--database"},
  description = "Type of RDBMS.",
  title = "RDBMS type: mysql|postgresql|mongodb")
protected String rdbmsMode;
```

当使用数据库连接时，毫无疑问，用户应该提供一个端点和一些凭证来访问它。我们将让 CLI 通过一个(URL 模式)或多个参数(主机模式)来处理这个问题。为此，我们将使用@MutuallyExclusiveWith注解，用相同的标签标记每个参数：

```java
@Option(type = OptionType.COMMAND,
  name = {"--rdbms:url", "--url"},
  description = "URL to use for connection to RDBMS.",
  title = "RDBMS URL")
@MutuallyExclusiveWith(tag="mode")
@Pattern(pattern="^(http://.):(d)(.)u=(.)&p=(.)")
protected String rdbmsUrl = "";
	
@Option(type = OptionType.COMMAND,
  name = {"--rdbms:host", "--host"},
  description = "Host to use for connection to RDBMS.",
  title = "RDBMS host")
@MutuallyExclusiveWith(tag="mode")
protected String rdbmsHost = "";

```

请注意，我们使用了@Pattern装饰器，它可以帮助我们定义 URL 字符串格式。

如果我们查看项目文档，我们会找到其他有价值的工具来处理需求、事件、允许值、特定情况等，使我们能够定义我们的自定义规则。

最后，如果用户选择了主机模式，我们应该要求他们提供他们的凭据。这样，一个选项依赖于另一个选项。我们可以使用@RequiredOnlyIf注解实现此行为：

```java
@RequiredOnlyIf(names={"--rdbms:host", "--host"})
@Option(type = OptionType.COMMAND,
  name = {"--rdbms:user", "-u", "--user"},
  description = "User for login to RDBMS.",
  title = "RDBMS user")
protected String rdbmsUser;

@RequiredOnlyIf(names={"--rdbms:host", "--host"})
@Option(type = OptionType.COMMAND,
  name = {"--rdbms:password", "--password"},
  description = "Password for login to RDBMS.",
  title = "RDBMS password")
protected String rdbmsPassword;

```

如果我们需要使用一些驱动程序来处理数据库连接怎么办？而且，假设我们需要在一个参数中接收多个值。我们可以将选项类型更改为OptionType.ARGUMENTS 或者 - 甚至更好 - 接受一个值列表：

```java
@Option(type = OptionType.COMMAND,
  name = {"--driver", "--jars"},
  description = "List of drivers",
  title = "--driver <PATH_TO_YOUR_JAR> --driver <PATH_TO_YOUR_JAR>")
protected List<String> jars = new ArrayList<>();
```

现在，我们不要忘记将数据库设置命令添加到我们的主类中。否则，它将无法在 CLI 上使用。

## 7.运行

我们做到了！我们完成了我们的项目，现在我们可以运行它了。

正如预期的那样，在不传递任何参数的情况下，将调用帮助：

```bash
$ baeldung-cli

usage: baeldung-cli <command> [ <args> ]

Commands are:
    help        Display help information
    setup-db    Setup our database
    setup-log   Setup our log

See 'baeldung-cli help <command>' for more information on a specific command.
```

如果我们改为执行setup-log –help，我们会得到：

```bash
$ baeldung-cli setup-log --help

NAME
        baeldung-cli setup-log - Setup our log

SYNOPSIS
        baeldung-cli setup-log [ {-h | --help} ] [ {-v | --verbose} ]

OPTIONS
        -h, --help
            Display help information

        -v, --verbose
            Set log verbosity on/off
```

最后，为这些命令提供参数将运行正确的业务逻辑。

## 八. 总结

在本文中，我们用很少的代码构建了一个简单但功能强大的命令行界面。

Airline 库以其强大的功能简化了 CLI，为我们提供了一个通用的、干净的和可重用的基础设施。它允许我们开发人员专注于我们的业务逻辑，而不是花时间设计应该是微不足道的东西。
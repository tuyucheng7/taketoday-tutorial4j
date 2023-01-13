## 1. 概述

Log4j 2 使用 Appenders 和 Layouts 等[插件来格式化和输出日志。](https://www.baeldung.com/log4j2-appenders-layouts-filters)这些被称为核心插件，Log4j 2 提供了[很多选项](https://logging.apache.org/log4j/2.x/manual/plugins.html)供我们选择。

但是，在某些情况下，我们可能还需要扩展现有的插件甚至编写自定义插件。

在本教程中，我们将使用 Log4j 2 扩展机制来实现自定义插件。

## 2. 扩展 Log4j 2 插件

Log4j 2 中的插件大致分为五类：

1.  核心插件
2.  转换器
3.  主要供应商
4.  查询
5.  类型转换器

Log4j 2 允许我们使用通用机制在上述所有类别中实现自定义插件。此外，它还允许我们使用相同的方法扩展现有插件。

在 Log4j 1.x 中，扩展现有插件的唯一方法是覆盖其实现类。另一方面，Log4j 2 通过使用@Plugin 注解类可以更轻松地扩展现有插件。

在以下部分中，我们将在其中几个类别中实现自定义插件。

## 3.核心插件

### 3.1. 实现自定义核心插件

Appenders、Layouts 和 Filters 等关键元素在 Log4j 2 中被称为核心插件。尽管此类插件的列表多种多样，但在某些情况下，我们可能需要实现自定义核心插件。例如，考虑一个仅将日志记录写入内存中的List的[ListAppender](https://www.baeldung.com/log4j2-custom-appender)：

```java
@Plugin(name = "ListAppender", 
  category = Core.CATEGORY_NAME, 
  elementType = Appender.ELEMENT_TYPE)
public class ListAppender extends AbstractAppender {

    private List<LogEvent> logList;

    protected ListAppender(String name, Filter filter) {
        super(name, filter, null);
        logList = Collections.synchronizedList(new ArrayList<>());
    }

    @PluginFactory
    public static ListAppender createAppender(
      @PluginAttribute("name") String name, @PluginElement("Filter") final Filter filter) {
        return new ListAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().isLessSpecificThan(Level.WARN)) {
            error("Unable to log less than WARN level.");
            return;
        }
        logList.add(event);
    }
}
```

我们用@Plugin 注解了类，允许我们命名我们的插件。此外，参数用 @PluginAttribute 注解。 过滤器或布局等嵌套元素作为@PluginElement 传递。 现在我们可以使用相同的名称在配置中引用这个插件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration xmlns:xi="http://www.w3.org/2001/XInclude"
    packages="com.baeldung" status="WARN">
    <Appenders>
        <ListAppender name="ListAppender">
            <BurstFilter level="INFO" rate="16" maxBurst="100"/>
        </MapAppender>
    </Appenders>
    <Loggers
        <Root level="DEBUG">
            <AppenderRef ref="ConsoleAppender" />
            <AppenderRef ref="ListAppender" />
        </Root>
    </Loggers>
</Configuration>
```

### 3.2. 插件构建器

上一节中的示例相当简单，只接受一个参数 名称。 一般来说，像 appender 这样的核心插件要复杂得多，通常接受几个可配置的参数。

例如，考虑一个将日志写入 Kafka 的附加程序：

```xml
<Kafka2 name="KafkaLogger" ip ="127.0.0.1" port="9010" topic="log" partition="p-1">
    <PatternLayout pattern="%pid%style{%message}{red}%n" />
</Kafka2>
```

为了实现这样的附加程序，Log4j 2 提供了一个基于[Builder模式](https://www.baeldung.com/creational-design-patterns)的插件构建器实现：

```java
@Plugin(name = "Kafka2", category = Core.CATEGORY_NAME)
public class KafkaAppender extends AbstractAppender {

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<KafkaAppender> {

        @PluginBuilderAttribute("name")
        @Required
        private String name;

        @PluginBuilderAttribute("ip")
        private String ipAddress;

        // ... additional properties

        // ... getters and setters

        @Override
        public KafkaAppender build() {
            return new KafkaAppender(
              getName(), getFilter(), getLayout(), true, new KafkaBroker(ipAddress, port, topic, partition));
        }
    }

    private KafkaBroker broker;

    private KafkaAppender(String name, Filter filter, Layout<? extends Serializable> layout, 
      boolean ignoreExceptions, KafkaBroker broker) {
        super(name, filter, layout, ignoreExceptions);
        this.broker = broker;
    }

    @Override
    public void append(LogEvent event) {
        connectAndSendToKafka(broker, event);
    }
}
```

总之，我们引入了一个 Builder 类，并用@PluginBuilderAttribute注解了参数。 因此， KafkaAppender从上面显示的配置中接受 Kafka 连接参数。

### 3.3. 扩展现有插件

我们还可以扩展 Log4j 2 中现有的核心插件。我们可以通过为我们的插件提供与现有插件相同的名称来实现这一点。例如，如果我们扩展RollingFileAppender：

```java
@Plugin(name = "RollingFile", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class RollingFileAppender extends AbstractAppender {

    public RollingFileAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }
    @Override
    public void append(LogEvent event) {
    }
}
```

值得注意的是，我们现在有两个同名的 appender。在这种情况下，Log4j 2 将使用最先发现的附加程序。我们将在后面的部分中看到更多关于插件发现的信息。

请注意 Log4j 2 不鼓励使用相同名称的多个插件。最好改为实现自定义插件并在日志记录配置中使用它。

## 4.转换器插件

布局是 Log4j 2 中的一个强大插件。 它允许我们为日志定义输出结构。例如，我们可以使用[JsonLayout](https://www.baeldung.com/java-log-json-output) 以 JSON 格式编写日志。

另一个这样的插件是 [PatternLayout](https://www.baeldung.com/java-logging-intro)。 在某些情况下，应用程序希望在每个日志语句中发布线程 ID、线程名称或时间戳等信息。PatternLayout 插件允许我们通过配置中的转换模式字符串嵌入[此类细节：](https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout)

```xml
<Configuration status="debug" name="baeldung" packages="">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %p %m%n"/>
        </Console>
    </Appenders>
</Configuration>
```

这里， %d 是转换模式。Log4j 2 通过理解转换模式的DatePatternConverter转换此 %d 模式，并将其替换为格式化的日期或时间戳。

现在假设在 Docker 容器内运行的应用程序想要在每个日志语句中打印容器名称。为此，我们将实现一个 DockerPatterConverter 并更改上面的配置以包含转换字符串：

```java
@Plugin(name = "DockerPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"docker", "container"})
public class DockerPatternConverter extends LogEventPatternConverter {

    private DockerPatternConverter(String[] options) {
        super("Docker", "docker");
    }

    public static DockerPatternConverter newInstance(String[] options) {
        return new DockerPatternConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        toAppendTo.append(dockerContainer());
    }

    private String dockerContainer() {
        return "container-1";
    }
}
```

所以我们实现了一个 类似于日期模式的自定义DockerPatternConverter 。它将用 Docker 容器的名称替换转换模式。

这个插件类似于我们之前实现的核心插件。值得注意的是，只有一个注解与上一个插件不同。@ConverterKeys 注解接受此插件的转换模式。

因此，此插件会将%docker 或%container模式字符串转换为运行应用程序的容器名称：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration xmlns:xi="http://www.w3.org/2001/XInclude" packages="com.baeldung" status="WARN">
    <Appenders>
        <xi:include href="log4j2-includes/console-appender_pattern-layout_colored.xml" />
        <Console name="DockerConsoleLogger" target="SYSTEM_OUT">
            <PatternLayout pattern="%pid %docker %container" />
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="com.baeldung.logging.log4j2.plugins" level="INFO">
            <AppenderRef ref="DockerConsoleLogger" />
        </Logger>
    </Loggers>
</Configuration>
```

## 5.查找插件

查找插件用于在 Log4j 2 配置文件中添加动态值。它们允许应用程序将运行时值嵌入配置文件中的某些属性。通过在文件系统、数据库等各种来源中进行基于键的查找来添加该值。

一个这样的插件是 DateLookupPlugin，它允许用应用程序的当前系统日期替换日期模式：

```xml
<RollingFile name="Rolling-File" fileName="${filename}" 
  filePattern="target/rolling1/test1-$${date:MM-dd-yyyy}.%i.log.gz">
    <PatternLayout>
        <pattern>%d %p %c{1.} [%t] %m%n</pattern>
    </PatternLayout>
    <SizeBasedTriggeringPolicy size="500" />
</RollingFile>
```

在此示例配置文件中， RollingFileAppender 使用 日期 查找，其中输出将采用 MM-dd-yyyy 格式。因此，Log4j 2 将日志写入带有日期后缀的输出文件。

与其他插件类似，Log4j 2 提供了很多[查找](https://logging.apache.org/log4j/2.x/manual/lookups.html)的来源。此外，如果需要新的来源，它可以很容易地实现自定义查找：

```java
@Plugin(name = "kafka", category = StrLookup.CATEGORY)
public class KafkaLookup implements StrLookup {

    @Override
    public String lookup(String key) {
        return getFromKafka(key);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return getFromKafka(key);
    }

    private String getFromKafka(String topicName) {
        return "topic1-p1";
    }
}
```

因此KafkaLookup将通过查询 Kafka 主题来解析该值。我们现在将从配置中传递主题名称：

```xml
<RollingFile name="Rolling-File" fileName="${filename}" 
  filePattern="target/rolling1/test1-$${kafka:topic-1}.%i.log.gz">
    <PatternLayout>
        <pattern>%d %p %c{1.} [%t] %m%n</pattern>
    </PatternLayout>
    <SizeBasedTriggeringPolicy size="500" />
</RollingFile>
```

我们将之前示例中的 日期 查找替换为将查询topic-1的 Kafka 查找。

由于 Log4j 2 仅调用查找插件的默认构造函数，因此我们没有像在早期插件中那样实现@PluginFactory 。

## 6.插件发现

最后，让我们了解 Log4j 2 如何发现应用程序中的插件。正如我们在上面的例子中看到的，我们给每个插件一个唯一的名字。此名称充当键，Log4j 2 将其解析为插件类。

Log4j 2 执行查找以解析插件类有一个特定的顺序：

1.  log4j2-core库中的序列化插件列表文件 。具体来说，这个 jar 里面打包了一个Log4j2Plugins.dat来列出默认的 Log4j 2 插件
2.  来自 OSGi 包的类似 Log4j2Plugins.dat 文件
3.  log4j.plugin.packages系统属性中以逗号分隔的包列表 
4.  在[编程式 Log4j 2 配置](https://www.baeldung.com/log4j2-programmatic-config)中，我们可以调用PluginManager.addPackages()方法来添加包名称列表
5.  可以在 Log4j 2 配置文件中添加以逗号分隔的包列表

作为先决条件，必须启用注解处理以允许 Log4j 2 通过@Plugin 注解中给定 的名称 解析插件。

由于 Log4j 2 使用名称来查找插件，因此上述顺序变得很重要。例如，如果我们有两个同名的插件，Log4j 2 将发现首先解析的插件。因此，如果我们需要扩展 Log4j 2 中现有的插件， 我们必须将插件打包在一个单独的 jar 中，并将其放在 log4j2-core.jar之前。

## 七. 总结

在本文中，我们查看了 Log4j 2 中的大类插件。我们讨论过，即使存在详尽的现有插件列表，我们也可能需要为某些用例实现自定义插件。

后来，我们查看了一些有用插件的自定义实现。此外，我们看到了 Log4j 2 如何允许我们命名这些插件并随后在配置文件中使用这个插件名称。最后，我们讨论了 Log4j 2 如何根据这个名称解析插件。
## 1. 简介

在本教程中，我们将学习如何创建自定义 Log4j2 appender。如果你正在寻找 Log4j2 的介绍，请查看[这篇文章](https://www.baeldung.com/log4j2-appenders-layouts-filters)。

Log4j2 附带了许多内置的附加程序，可用于各种目的，例如记录到文件、数据库、套接字或 NoSQL 数据库。

但是，根据应用程序需求，可能需要自定义附加程序。

Log4j2 是 Log4j 的升级版本，相比 Log4j 有明显的改进。因此，我们将使用 Log4j2 框架来演示自定义附加程序的创建。

## 2.Maven 设置

我们需要在pom.xml中添加log4j-core依赖项：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.11.0</version>
</dependency>
```

最新版本 的 log4j-core可以在 [这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")找到。

## 3.自定义Appender

我们可以通过两种方式来实现我们的自定义 appender。第一种是实现Appender接口，第二种是扩展 AbstractAppender 类。第二种方法提供了一种简单的方法来实现我们自己的自定义附加程序，这就是我们将使用的方法。

对于这个例子，我们将创建一个MapAppender。我们将捕获日志事件并将它们存储在一个 Concurrent HashMap 中，并带有键的时间戳。

下面是我们如何创建 MapAppender：

```java
@Plugin(
  name = "MapAppender", 
  category = Core.CATEGORY_NAME, 
  elementType = Appender.ELEMENT_TYPE)
public class MapAppender extends AbstractAppender {

    private ConcurrentMap<String, LogEvent> eventMap = new ConcurrentHashMap<>();

    protected MapAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static MapAppender createAppender(
      @PluginAttribute("name") String name, 
      @PluginElement("Filter") Filter filter) {
        return new MapAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        eventMap.put(Instant.now().toString(), event);
    }
}
```

我们已经用@Plugin 注解对类进行了注解，这表明我们的 appender 是一个插件。

插件的名称表示我们将在配置中提供的名称以使用此附加程序。类别指定我们放置插件的类别。elementType是 appender 。

我们还需要一个将创建 appender 的工厂方法。我们的createAppender方法用于此目的，并使用@PluginFactory注解进行注解。

在这里，我们通过调用受保护的构造函数来初始化我们的附加程序，并将布局作为 null 传递，因为我们不会在配置文件中提供任何布局，并且我们希望框架解析默认布局。

接下来，我们覆盖了 具有处理LogEvent的实际逻辑的append方法。在我们的例子中，append 方法将LogEvent放入我们的eventMap 中。 

## 4.配置

现在我们已经有了 MapAppender ，我们需要一个lo4j2.xml 配置文件来使用这个 appender 进行日志记录。

以下是我们如何在log4j2.xml文件中定义配置部分：

```xml
<Configuration xmlns:xi="http://www.w3.org/2001/XInclude" packages="com.baeldung" status="WARN">
```

请注意，packages 属性应引用包含你的自定义 appender 的包。

接下来，在我们的 appender 部分，我们定义了 appender。以下是我们如何将自定义 appender 添加到配置中的 appender 列表：

```xml
<MapAppender name="MapAppender" />
```

最后一部分是在我们的 Loggers 部分中实际使用 appender。对于我们的实现，我们使用MapAppender作为根记录器并将其定义在根部分。

这是它是如何完成的：

```xml
<Root level="DEBUG">
    <AppenderRef ref="MapAppender" />
</Root>
```

## 5.错误处理

要在记录事件时处理错误，我们可以使用从AbstractAppender继承 的错误方法。

例如，如果我们不想记录日志级别低于WARN 的事件。

我们可以使用AbstractAppender的错误 方法来记录错误消息。这是我们班级的做法：

```java
public void append(LogEvent event) {
    if (event.getLevel().isLessSpecificThan(Level.WARN)) {
        error("Unable to log less than WARN level.");
        return;
    }
    eventMap.put(Instant.now().toString(), event);
}
```

观察我们的append方法现在发生了怎样的变化。我们检查事件的级别是否大于 WARN，如果低于WARN ，我们会提前返回。

## 六. 总结

在本文中，我们了解了如何为 Log4j2 实现自定义附加程序。

虽然有许多使用 Log4j2 提供的 appender 来记录我们的数据的内置方法，但我们在这个框架中也有工具，使我们能够根据我们的应用程序需要创建我们自己的 appender。
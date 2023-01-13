## 1. 简介

在本文中，我们将探索创建自定义 Logback appender。如果你正在寻找Java日志记录的介绍，请查看[这篇文章](https://www.baeldung.com/java-logging-intro)。

Logback 附带许多写入标准输出、文件系统或数据库的内置附加程序。这个框架架构的美妙之处在于它的模块化，这意味着我们可以轻松地对其进行定制。

在本教程中，我们将重点关注logback-classic，它需要以下 Maven 依赖项：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

此依赖项的最新版本可在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"ch.qos.logback" AND a%3A"logback-classic")上获得。

## 2. 基本的 Logback Appenders

Logback 提供了我们可以扩展的基类来创建自定义的 appender。

Appender是所有 appender 必须实现的通用接口。通用类型是ILoggingEvent或AccessEvent，具体取决于我们分别使用的是logback-classic还是logback-access。

我们的自定义 appender 应该扩展AppenderBase或UnsynchronizedAppenderBase ，它们都实现Appender并处理过滤器和状态消息等功能。

AppenderBase是线程安全的；UnsynchronizedAppenderBase子类负责管理它们的线程安全。

正如ConsoleAppender和FileAppender都扩展了OutputStreamAppender并调用了超级方法setOutputStream()一样，如果自定义 appender正在写入OutputStream ，则它应该是OutputStreamAppender的子类。

## 3.自定义Appender

对于我们的自定义示例，我们将创建一个名为MapAppender的玩具附加程序。此附加程序会将所有日志记录事件插入到Concurrent HashMap中，并带有键的时间戳。首先，我们将子类化AppenderBase并使用ILoggingEvent作为通用类型：

```java
public class MapAppender extends AppenderBase<ILoggingEvent> {

    private ConcurrentMap<String, ILoggingEvent> eventMap 
      = new ConcurrentHashMap<>();

    @Override
    protected void append(ILoggingEvent event) {
        eventMap.put(String.valueOf(System.currentTimeMillis()), event);
    }
    
    public Map<String, ILoggingEvent> getEventMap() {
        return eventMap;
    }
}
```

接下来，为了使MapAppender开始接收日志记录事件，让我们将其添加为配置文件logback.xml中的附加程序：

```xml
<configuration>
    <appender name="map" class="com.baeldung.logback.MapAppender"/>
    <root level="info">
        <appender-ref ref="map"/>
    </root>
</configuration>
```

## 4. 设置属性

Logback 使用 JavaBeans 内省来分析附加程序上设置的属性。我们的自定义 appender 将需要 getter 和 setter 方法以允许内省器查找和设置这些属性。

让我们向MapAppender添加一个属性，为eventMap的键提供一个前缀：

```java
public class MapAppender extends AppenderBase<ILoggingEvent> {

    //...

    private String prefix;

    @Override
    protected void append(ILoggingEvent event) {
        eventMap.put(prefix + System.currentTimeMillis(), event);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    //...

}
```

接下来，在我们的配置中添加一个属性来设置这个前缀：

```xml
<configuration debug="true">

    <appender name="map" class="com.baeldung.logback.MapAppender">
        <prefix>test</prefix>
    </appender>

    //...

</configuration>
```

## 5.错误处理

要在创建和配置我们的自定义 appender 期间处理错误，我们可以使用从AppenderBase继承的方法。

例如，当 prefix 属性为 null 或空字符串时，MapAppender可以调用addError ()并提前返回：

```java
public class MapAppender extends AppenderBase<ILoggingEvent> {

    //...

    @Override
    protected void append(final ILoggingEvent event) {
        if (prefix == null || "".equals(prefix)) {
            addError("Prefix is not set for MapAppender.");
            return;
        }

        eventMap.put(prefix + System.currentTimeMillis(), event);
    }

    //...

}
```

当在我们的配置中打开调试标志时，我们将在控制台中看到一个错误，提醒我们尚未设置前缀属性：

```xml
<configuration debug="true">

    //...

</configuration>
```

## 六. 总结

在本快速教程中，我们重点介绍了如何为 Logback 实现自定义附加程序。
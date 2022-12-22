## 1. 概述

在本教程中，我们介绍如何**在JUnit测试中涵盖生成的日志**。

**我们将使用slf4j-api和logback实现，并创建一个可用于日志断言的自定义appender组件**。

## 2. Maven依赖

首先我们需要添加logback依赖项，由于它原生实现了slf4j-api，它会通过Maven传递性自动下载并注入到项目中：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>. 
    <version>1.2.7</version>
</dependency>
```

AssertJ在测试时提供了非常有用的功能，所以我们也将它的依赖添加到项目中：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.21.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 基本业务功能

现在，让我们创建一个对象，该对象将生成我们可以测试的日志。

我们的BusinessWorker对象只公开一个方法，此方法为每个日志级别生成一个内容相同的日志。
虽然这种方法在现实世界中不是很有用，但它可以很好地用于我们的测试目的：

```java
public class BusinessWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessWorker.class);

    public void generateLogs(String msg) {
        LOGGER.trace(msg);
        LOGGER.debug(msg);
        LOGGER.info(msg);
        LOGGER.warn(msg);
        LOGGER.error(msg);
    }
}
```

## 4. 测试日志

我们要生成日志，所以我们需要在src/test/resources文件夹中创建一个logback.xml文件，将所有日志重定向到CONSOLE appender：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %boldMagenta(%d{HH:mm:ss.SSS}) %boldYellow([%thread]) %highlight(%-5level) %boldGreen([%logger{36}]) >>> %boldCyan(%msg) %n
            </Pattern>
        </layout>
    </appender>
    <root level="error">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

### 4.1 MemoryAppender

现在，让我们创建一个**将日志保存在内存中的自定义Appender**，我们将**扩展logback提供的ListAppender<ILoggingEvent\>**，并添加一些有用的方法：

```java
public class MemoryAppender extends ListAppender<ILoggingEvent> {
	public void reset() {
		this.list.clear();
	}

	public boolean contains(String string, Level level) {
		return this.list.stream()
				.anyMatch(event -> event.toString().contains(string) && event.getLevel().equals(level));
	}

	public int countEventsForLogger(String loggerName) {
		return (int) this.list.stream()
				.filter(event -> event.getLoggerName().contains(loggerName))
				.count();
	}

	public List<ILoggingEvent> search(String string) {
		return this.list.stream()
				.filter(event -> event.toString().contains(string))
				.collect(Collectors.toList());
	}

	public List<ILoggingEvent> search(String string, Level level) {
		return this.list.stream()
				.filter(event -> event.toString().contains(string) && event.getLevel().equals(level))
				.collect(Collectors.toList());
	}

	public int getSize() {
		return this.list.size();
	}

	public List<ILoggingEvent> getLoggedEvents() {
		return Collections.unmodifiableList(this.list);
	}
}
```

MemoryAppender类处理由日志系统自动填充的集合，它公开了多种方法，以涵盖广泛的测试目的：

+ reset()：清除集合
+ contains(msg, level)：仅当集合包含与指定内容和日志级别匹配的ILoggingEvent时才返回true
+ countEventForLoggers(loggerName)：返回loggerName生成的ILoggingEvent的数量
+ search(msg)：返回与特定内容匹配的ILoggingEvent集合
+ search(msg, level)：返回与指定内容和日志级别匹配的ILoggingEvent集合
+ getSize()：返回ILoggingEvents的数量
+ getLoggedEvents()：返回ILoggingEvent元素的不可修改视图

### 4.2 单元测试

接下来，我们为BusinessWorker创建一个JUnit测试。我们将MemoryAppender声明为一个字段，并以编程方式将其注入日志系统；然后，我们将启动appender。

对于我们的测试，我们将日志级别设置为DEBUG：

```java
class BusinessWorkerUnitTest {
	private static MemoryAppender memoryAppender;
	private static final String LOGGER_NAME = "cn.tuyucheng.taketoday.junit.log";
	private static final String MSG = "This is a test message!!!";

	@BeforeEach
	void setup() {
		Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
		memoryAppender = new MemoryAppender();
		memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		logger.setLevel(Level.DEBUG);
		logger.addAppender(memoryAppender);
		memoryAppender.start();
	}
}
```

现在我们可以创建一个简单的测试，在其中实例化BusinessWorker类并调用generateLogs()方法，然后我们可以对它生成的日志进行断言：

```java
@Test
void test() {
	BusinessWorker worker = new BusinessWorker();
	worker.generateLogs(MSG);
    
	// I check that I only have 4 messages (all but trace)
	assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(4);
	// I look for a specific message at a specific level, and I only have 1
	assertThat(memoryAppender.search(MSG, Level.INFO).size()).isEqualTo(1);
	// I check that the entry that is not present is the trace level
	assertThat(memoryAppender.contains(MSG, Level.TRACE)).isFalse();
}
```

该测试使用MemoryAppender的三个功能：

+ 已生成四条日志：每个日志级别应包含一条日志，并过滤TRACE级别
+ 只有一条日志包含严重级别为INFO的内容消息
+ 没有包含内容消息和级别为TRACE的日志

如果我们计划在生成大量日志时在同一个测试类中使用该类的同一个实例，那么内存使用率将会飙升，
**我们可以在每次测试之前调用MemoryAppender.clear()方法来释放内存并避免OutOfMemoryException**。

在此示例中，我们将保留日志的范围缩小到LOGGER_NAME包，我们将其定义为“cn.tuyucheng.taketoday.junit.log”。
**也可以使用LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)保留所有日志，但我们应该尽可能避免这种情况，因为它会消耗大量内存**。

## 5. 总结

在本教程中，我们演示了如何在单元测试中涵盖日志的生成。
## 1. 简介

在本教程中，我们介绍Java 9中新引入的Logging API，并通过一些案例来涵盖最常见的情况。

Java中引入此API是为了提供一种通用机制来处理所有平台日志，并公开可由库和应用程序自定义的服务接口。这样，JDK平台日志就可以和应用程序使用同一个日志框架，减少项目依赖。

## 2. 创建自定义实现

在本节中，我们介绍我们必须实现以创建新记录器的Logging API的主要类，通过实现一个将所有日志打印到控制台的简单记录器来做到这一点。

### 2.1 创建记录器

我们必须创建的主要类是Logger，这个类至少要实现System.Logger接口和以下四个方法：

-   getName()：返回记录器的名称，JDK将使用它来按名称创建记录器
-   isLoggable()：指示记录器启用的级别
-   log()：它是将日志打印到应用程序使用的任何底层系统的方法，在我们的例子中是控制台。有2个log()方法可以实现，每个方法接收不同的参数

下面是具体的实现外观：

```java
public class ConsoleLogger implements System.Logger {

	@Override
	public String getName() {
		return "ConsoleLogger";
	}

	@Override
	public boolean isLoggable(Level level) {
		return true;
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		System.out.printf("ConsoleLogger [%s]: %s - %s%n", level, msg, thrown);
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		System.out.printf("ConsoleLogger [%s]: %s%n", level, MessageFormat.format(format, params));
	}
}
```

我们的ConsoleLogger类重写了上面提到的四个方法，getName()方法返回一个字符串，而isLoggable()方法在所有情况下都返回true，最后，我们有两个输出到控制台的log()方法。

### 2.2 创建LoggerFinder

一旦我们创建了记录器，我们需要实现一个LoggerFinder来创建我们的ConsoleLogger实例。

为此，我们必须扩展抽象类System.LoggerFinder并实现getLogger()方法：

```java
public class CustomLoggerFinder extends System.LoggerFinder {

	@Override
	public System.Logger getLogger(String name, Module module) {
		return new ConsoleLogger();
	}
}
```

在这种情况下，我们总是返回我们的ConsoleLogger。

最后，我们需要将LoggerFinder注册为服务，以便它可以被JDK发现。如果我们不提供实现，默认情况下将使用SimpleConsoleLogger。

JDK用来加载实现的机制是ServiceLoader，[你可以在本教程](https://www.baeldung.com/java-spi)中找到有关它的更多信息。

由于我们使用的是Java 9，所以我们将把类打包在一个模块中，并在module-info.java文件中注册我们的服务：

```java
module cn.tuyucheng.taketoday.logging {
	provides java.lang.System.LoggerFinder
			with cn.tuyucheng.taketoday.logging.CustomLoggerFinder;
	exports cn.tuyucheng.taketoday.logging;
}
```

有关Java模块的更多信息，请查看[此其他教程](https://www.baeldung.com/java-9-modularity)。

### 2.3 测试我们的例子

为了测试我们的示例，让我们创建另一个用作应用程序的模块，此模块只包含调用我们的服务实现的主类。

此类通过调用System.getLogger()方法获取我们的ConsoleLogger的实例：

```java
public class MainApp {

	private static final System.Logger LOGGER = System.getLogger("MainApp");

	public static void main(String[] args) {
		LOGGER.log(Level.ERROR, "error test");
		LOGGER.log(Level.INFO, "info test");
	}
}
```

在内部，JDK将获取我们的CustomLoggerFinder实现并创建ConsoleLogger实例。

然后，我们为这个模块创建module-info文件：

```java
module cn.tuyucheng.taketoday.logging.app {
}
```

此时，我们的项目结构如下：

```powershell
├── src
│   ├── modules
│   │   ├── cn.tuyucheng.taketoday.logging
│   │   │   ├── cn
│   │   │   │   └── tuyucheng
|   |   |   |       └── taketoday 
│   │   │   │           └── logging
│   │   │   │               ├── ConsoleLogger.java
│   │   │   │               └── CustomLoggerFinder.java
│   │   │   └── module-info.java
│   │   ├── cn.tuyucheng.taketoday.logging.app
│   │   │   ├── cn
│   │   │   │   └── tuyucheng
│   │   │   │       └── taketoday
|   |   |   |           └── logging
│   │   │   │               └── app
│   │   │   │                   └── MainApp.java
│   │   │   └── module-info.java
└──
```

然后，编译我们的两个模块，并将它们放在mods目录中：

```shell
# 编译logging模块
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging src/modules/cn.tuyucheng.taketoday.logging/module-info.java        src/modules/cn.tuyucheng.taketoday.logging/cn/tuyucheng/taketoday/logging/.java

# 编译logging slf4j模块
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging.slf4j src/modules/cn.tuyucheng.taketoday.logging.slf4j/module-info.java src/modules/cn.tuyucheng.taketoday.logging.slf4j/cn/tuyucheng/taketoday/logging/slf4j/.java
```

最后，我们运行app模块的Main类：

```shell
# 运行logging app
java --module-path mods -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp
```

如果我们观察控制台输出，我们可以看到日志是使用我们的ConsoleLogger打印的：

```shell
ConsoleLogger [ERROR]: error test
ConsoleLogger [INFO]: info test
```

## 3. 添加外部日志框架

在我们之前的示例中，我们将所有消息记录到控制台，这与默认记录器的操作相同。Java 9中Logging API最有用的用途之一是让应用程序将JDK日志路由到应用程序正在使用的同一个日志框架，这就是我们在本节中要做的事情。

我们将创建一个使用SLF4J作为日志门面和Logback作为日志框架的新模块。由于我们已经在上一节中解释了基础知识，现在我们专注于如何添加外部日志框架。

### 3.1 使用SLF4J的自定义实现

首先，我们将实现另一个Logger，它将为每个实例创建一个新的SLF4J记录器：

```java
public class Slf4jLogger implements System.Logger {

    private final String name;
    private final Logger logger;

    public Slf4jLogger(String name) {
        this.name = name;
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public String getName() {
        return name;
    }
    
    //...
}
```

请注意，这个Logger是org.slf4j.Logger。

对于其余的方法，我们依赖于SLF4J Logger实例上的实现。因此，如果启用了SLF4J记录器，我们的记录器将被启用：

```java
@Override
public boolean isLoggable(Level level) {
	switch (level) {
		case OFF:
			return false;
		case TRACE:
			return logger.isTraceEnabled();
		case DEBUG:
			return logger.isDebugEnabled();
		case INFO:
			return logger.isInfoEnabled();
		case WARNING:
			return logger.isWarnEnabled();
		case ERROR:
			return logger.isErrorEnabled();
		case ALL:
		default:
			return true;
	}
}
```

并且log方法将根据使用的日志级别调用适当的SLF4J记录器方法：

```java
@Override
public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
	if (!isLoggable(level)) {
		return;
	}
	switch (level) {
		case TRACE:
			logger.trace(msg, thrown);
			break;
		case DEBUG:
			logger.debug(msg, thrown);
			break;
		case INFO:
			logger.info(msg, thrown);
			break;
		case WARNING:
			logger.warn(msg, thrown);
			break;
		case ERROR:
			logger.error(msg, thrown);
			break;
		case ALL:
		default:
			logger.info(msg, thrown);
	}
}

@Override
public void log(Level level, ResourceBundle bundle, String format, Object... params)
	if (!isLoggable(level)) {
		return;
	}
	String message = MessageFormat.format(format, params);
	switch (level) {
		case TRACE:
			logger.trace(message);
			break;
		case DEBUG:
			logger.debug(message);
			break;
		case INFO:
			logger.info(message);
			break;
		case WARNING:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		case ALL:
		default:
			logger.info(message);
	}
}
```

最后，我们创建一个使用Slf4jLogger的新LoggerFinder：

```java
public class Slf4jLoggerFinder extends System.LoggerFinder {

	@Override
	public System.Logger getLogger(String name, Module module) {
		return new Slf4jLogger(name);
	}
}
```

### 3.2 模块配置

一旦我们实现了所有类，接下来就是在模块中注册我们的服务并添加SLF4J模块的依赖项：

```java
module cn.tuyucheng.taketoday.logging.slf4j {
	requires org.slf4j;
	provides java.lang.System.LoggerFinder
			with cn.tuyucheng.taketoday.logging.slf4j.Slf4jLoggerFinder;
	exports cn.tuyucheng.taketoday.logging.slf4j;
}
```

该模块的结构如下：

```powershell
├── src
│   ├── modules
│   │   ├── cn.tuyucheng.taketoday.logging.slf4j
│   │   │   ├── cn
│   │   │   │   └── tuyucheng
|   |   |   |       └── taketoday
│   │   │   │           └── logging
│   │   │   │               └── slf4j
│   │   │   │                   ├── Slf4jLoggerFinder.java
│   │   │   │                   └── Slf4jLogger.java
│   │   │   └── module-info.java
└──
```

现在我们可以像上一节那样将此模块编译到mods目录中。

请注意，我们必须将slf4j-api jar放在mods目录中才能编译此模块。另外，请记住使用库的[模块化版本](https://search.maven.org/search?q=g:org.slf4j%20AND%20a:slf4j-api)。

### 3.3 添加Logback

至此，我们还需要添加Logback依赖项和配置；为此，请将logback-classic和logback-core的jar包放在mods目录中，和以前一样，我们必须确保我们使用的是[模块化版本](https://search.maven.org/search?q=g:ch.qos.logback)的库。

最后，让我们创建一个Logback配置文件并将其放在我们的mods目录中：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<pattern>
                %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} -- %msg%n
			</pattern>
		</encoder>
	</appender>

	<root>
		<appender-ref ref="STDOUT"/>
	</root>
</configuration>
```

### 3.4 运行我们的应用程序

此时，我们可以使用SLF4J模块运行我们的应用程序。在这种情况下，我们还需要指定Logback配置文件：

```shell
java --module-path mods 
  -Dlogback.configurationFile=mods/logback.xml 
  -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp
```

最后，如果我们观察输出，我们可以看到日志是使用我们的Logback配置打印的：

```shell
2022-11-09 14:02:40 [main] ERROR MainApp -- error test
2022-11-09 14:02:40 [main] INFO  MainApp -- info test
```

## 4. 总结

我们在本文中演示了如何使用新的平台日志记录器API在Java 9中创建自定义记录器。此外，我们还使用外部日志框架实现了一个示例，这是这个新API最有用的用例之一。
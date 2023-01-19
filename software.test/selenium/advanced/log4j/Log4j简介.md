欢迎你阅读有关 Log4j 的系列文章。这是一个非常重要的现成 API，可用于在你的测试中创建日志记录基础结构。让我们从 Log4j 介绍开始。

## 为什么日志记录在任何应用程序中都很重要？

日志记录对任何应用程序都非常重要。它可以帮助我们收集有关应用程序运行情况的信息，还可以帮助我们在发生任何故障时进行调试。

## 什么是 Log4j？

Log4j 是一个出色的日志记录 API，可在 Java 和 .net 框架上使用。优点是：

-   Log4j 允许你拥有非常好的日志记录基础结构，而无需付出任何努力。
-   Log4j 使你能够在不同级别(跟踪、调试、信息、警告、错误和致命)对日志进行分类。
-   Log4j 使你能够将日志定向到不同的输出。例如到文件、控制台或数据库。
-   Log4j 使你能够定义输出日志的格式。
-   Log4j 使你能够编写异步日志，这有助于提高应用程序的性能。
-   Log4j 中的记录器遵循可能对你的应用程序很方便的类层次结构。

如果你无法理解其中任何一点，请不要担心。当我们接近 Log4j 教程系列的结尾时，事情会变得更加清晰。

## 日志4j

Log4j 由四个主要组件组成

-   日志管理器
-   伐木工
-   附加程序
-   布局

随之而来的是一些附加组件，这些组件将在以下教程的各个标题中进行介绍。

## 日志管理器

这是帮助我们获取具有不同名称和层次结构的记录器的静态类。你可以将LogManager视为生产记录器对象的工厂。示例代码将是：

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	//mainLogger is a logger object that we got from LogManager. All loggers are 
        //using this method only. We can consider LogManager as a factory to create
        //Logger objects
	static Logger mainLogger = LogManager.getLogger(SampleEntry.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicConfigurator.configure();
		mainLogger.info("This is just a logger");	

	}
}
```



## 记录器

这是一个帮助你在不同日志记录级别记录信息的类。在上面的示例代码中，你可以看到我们使用 LogManager 静态类创建了一个名为 mainLogger 的记录器。现在我们可以用它来写日志了。如你所见，我们有 mainLogger.info(" Comments that you want to log ") 语句来记录字符串。

## 附加程序

Appenders 是帮助 Logger 对象将日志写入不同输出的对象。Appenders 可以指定文件、控制台或数据库作为输出位置。

在此代码示例中，你将看到我们使用控制台附加程序来打印日志，就像我们使用 System.out 或 System.err 所做的那样。

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	//All the loggers that can be used
	static Logger mainLogger = LogManager.getLogger(SampleEntry.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicConfigurator.configure();

		//Create a console appender and attach it to our mainLogger
		ConsoleAppender appender = new ConsoleAppender();
		mainLogger.addAppender(appender);
		mainLogger.info("This is just a logger");	
	}
}
```



## 布局

布局类帮助我们定义日志信息在输出中的显示方式。这是使用PatternLayout类更改日志格式的示例代码：

```java
package Log4jSample;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class SampleEntry {

	//All the loggers that can be used
	static Logger mainLogger = LogManager.getLogger(SampleEntry.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicConfigurator.configure();
		ConsoleAppender appender = new ConsoleAppender();
		appender.activateOptions();
		PatternLayout layoutHelper = new PatternLayout();
		layoutHelper.setConversionPattern("%-5p [%t]: %m%n");
		layoutHelper.activateOptions();
        //mainLogger.getAppender("ConsoleAppender").setLayout(layoutHelper);	
		appender.setLayout(layoutHelper);
		mainLogger.addAppender(appender);
		//Create a console appender and attach it to our mainLogger
		mainLogger.info("Pattern 1 is displayed like this");
		layoutHelper.setConversionPattern("%C %m%n");
		mainLogger.info("Pattern 2 is displayed like this");

	}

}
```

控制台上的预期输出将是

INFO [main]: Pattern 1 显示如下 Log4jSample.SampleEntry

模式2是这样显示的

这是对 Log4j API 中不同组件的简要说明。让我们分别了解每个组件。
欢迎阅读Log4j 记录器系列教程的下一章。我希望你至少阅读过[Log4j 简介](https://toolsqa.com/selenium-webdriver/log4j-introduction/)和[LogManager，](https://toolsqa.com/selenium-webdriver/log4j-logmanager/)

让我们开始吧。Log4j组件列表中的下一个对象是Logger类。这是你需要的最重要的课程。这是允许你将信息记录到所需日志位置的对象，可以是控制台、文件甚至数据库。

记录器对象遵循类似于任何 OOP 语言中的类层次结构的层次结构。Logger层次结构的命名约定在名称中。每个对象名称决定它遵循哪个层次结构。例如，我们有一个名为“Main.Utility”的记录器。所以 Utility 是 Main 的孩子，Main 是 Utility 的父亲。此外，所有记录器都派生自根记录器。实际的层次结构将是root.Main.Utility，其中 root 是 Utility 的祖先和 Main 的父亲。这可以在图表中显示为

这些关系由 LogManager 类管理。让我们用一个例子来说明它

![等级制度](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Heirarchy.png)

```java
package Log4jSample;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	

      Logger chance = LogManager.getLogger(SampleEntry.class.getName());	
      Logger logger1 = LogManager.getLogger("Child1");
      Logger logger1Child = logger1.getLogger("Child1.ChildOfLogger1");
      Logger loggerGrandChild = LogManager.getLogger("Child1.ChildOfLogger1.GrandChild");

      System.out.println("logger1's full name is " + logger1.getParent().getName());
      System.out.println("logger1Child's full name is " + logger1Child.getParent().getName());
      System.out.println("loggerGrandChild's full name is " + loggerGrandChild.getParent().getName());

	}
}
```

输出将是

![日志管理器输出](https://www.toolsqa.com/gallery/selnium%20webdriver/2.LogManagerOutPut.png)

如你所见，logger1 是 logger1Child 的父级和loggerGrandChild 的祖父级。这就是我们如何根据应用程序需要创建记录器对象的层次结构。

## 记录级别

Logger 类具有以下打印方法，可帮助你记录信息。

- 痕迹

- 调试

- 信息

- 警告

- 错误

- 致命的

因此，假设你想打印一个调试日志，你只需说Logger.Debug(" This is a debug log ")即可。你可以选择使用任何其他重载的 Logger.Debug()方法。所有这些打印语句都称为级别。

### 问题来了，为什么我们需要日志级别？

每个日志级别都需要特定类型的信息，例如调试级别需要记录该信息，这可能有助于程序员在出现故障时调试应用程序。类似地，错误级别期望使用该级别记录所有错误。

你可以使用Logger.setLevel方法设置记录器的日志级别。一旦你设置了记录器的日志级别，只有具有该级别和更高级别的记录器才会被记录。日志级别具有以下顺序 TRACE < DEBUG < INFO < WARN < ERROR < FATAL。

让我们通过一个例子来理解这一点，在下面的代码中，我们将级别设置为DEBUG first 而不是WARN。你将看到只会记录该级别或更高级别的日志。这是代码示例。

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

     BasicConfigurator.configure();
     Logger Mylogger = LogManager.getLogger("DebugLogger");

     //Setting up the log level of both loggers
      Mylogger.setLevel(Level.DEBUG);

      Mylogger.trace("This is the trace log - DEBUG");
      Mylogger.debug("This is debug log - DEBUG");
      Mylogger.info("This is info log - DEBUG");
      Mylogger.warn("This is Warn log - DEBUG");
      Mylogger.error("This is error log - DEBUG");
      Mylogger.fatal("This is Fatal log - DEBUG");

      Mylogger.setLevel(Level.WARN);
      Mylogger.trace("This is the trace log - WARN");
      Mylogger.debug("This is debug log - WARN");
      Mylogger.info("This is info log - WARN");
      Mylogger.warn("This is Warn log - WARN");
      Mylogger.error("This is error log - WARN");
      Mylogger.fatal("This is Fatal log - WARN");      
	}
}
```

输出看起来像这样

![日志级别](https://www.toolsqa.com/gallery/selnium%20webdriver/3.LogLevel.png)

你可以看到，当日志级别为 DEBUG 时，将显示所有 DEBUG 到 FATAL 的日志。一旦将日志级别设置为 WARN，就会显示从 WARNS 到 FATAL 的所有日志。

## 日志级别继承

正如前面章节所讨论的，我们知道记录器遵循层次结构。同样，日志级别也遵循层次结构，我的意思是，如果未定义记录器的级别层次结构，则从父级级别中选择它。假设我们有两个记录器

LoggerParent和LoggerParent.Child，假设 Logger LoggerParent的日志级别设置为LoggerParent.setLevel(Level.WARN)以发出警告。现在，如果我们不设置 Logger 子级的级别，那么默认的 Child 日志记录级别将设置为Level.WARN，这是其父级的级别。

让我们用代码示例来看一下

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	 BasicConfigurator.configure();
     Logger LoggerParent = LogManager.getLogger("LoggerParent");
     Logger LoggerChild = LogManager.getLogger("LoggerParent.Child");
     //Setting up the log level of both loggers
      LoggerParent.setLevel(Level.WARN);      
     LoggerParent.trace("This is the trace log - PARENT");
     LoggerParent.debug("This is debug log - PARENT");
     LoggerParent.info("This is info log - PARENT");
     LoggerParent.warn("This is Warn log - PARENT");
     LoggerParent.error("This is error log - PARENT");
     LoggerParent.fatal("This is Fatal log - PARENT");
     LoggerChild.trace("This is the trace log - CHILD");
     LoggerChild.debug("This is debug log - CHILD");
     LoggerChild.info("This is info log - CHILD");
     LoggerChild.warn("This is Warn log - CHILD");
     LoggerChild.error("This is error log - CHILD");
     LoggerChild.fatal("This is Fatal log - CHILD");      
	}
}
```

这个样本的输出是

![日志级别层次结构](https://www.toolsqa.com/gallery/selnium%20webdriver/4.LogLevelHierarchy.png)

## 记录运行时异常

这是 Logger 类的一个非常重要的特性，它使你能够将异常传递到输出。这在我们有意捕获异常但又想记录有关异常的信息的情况下特别方便。每个打印方法(TRACE，DEBUG .... FATAL)都有一个重载，即Logger.Debug(Object message，Throwable t)，当然我们只是以 .Debug 为例，这允许我们传递异常。让我们看看使用代码示例有何好处

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	 BasicConfigurator.configure();
     Logger LoggerParent = LogManager.getLogger("LoggerParent");
	     try
	     {
	    	 // We will get a divide by zero exception her
	    	 int x = 200 / 0;
	     }
	     catch(Exception exp)
	     {
	       LoggerParent.warn("Following exception was raised", exp);	 
	     }     
	}
}
```

如你所见，在输出中你将看到正在记录的异常详细信息。这些信息在调试时非常有用。需要注意的重要一点是异常消息和异常的行号是自动打印的。以上代码的输出是

![异常记录器](https://www.toolsqa.com/gallery/selnium%20webdriver/5.ExceptionLogger.png)

我希望本教程能让你对 Logger 类有所了解。如果你有任何意见，请给我发电子邮件。希望在下一个教程中见到你。
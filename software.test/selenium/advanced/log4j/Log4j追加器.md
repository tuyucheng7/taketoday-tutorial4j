欢迎阅读有关 Log4j Appenders 的本教程。这是 Log4j 教程的延续，我希望你已经阅读了我们之前关于[Introduction](https://toolsqa.com/selenium-webdriver/log4j-introduction/)、  [LogManager](https://toolsqa.com/selenium-webdriver/log4j-logmanager/)和[Loggers](https://toolsqa.com/selenium-webdriver/log4j-loggers/)的教程。

## 附加程序

Appenders是将日志传送到所需目的地的Log4j对象。例如， ConsoleAppender会将日志传送到控制台，而FileAppender会传送到日志文件。我们在Log4j中有许多类型的Appenders ，今天我们将介绍的是

-   文件追加器
-   ConsoleAppender
-   JDBCAppender

## 文件追加器

几乎所有时候我们都想将数据记录在文件中，而不是在控制台上打印出来。这是出于显而易见的原因，我们想要一份日志的副本，以便我们可以保留它以供参考并浏览它以查找问题。每当你需要登录文件时，你将使用FileAppender。此代码示例向你解释如何创建FileAppender对象，然后将其设置为所需的记录器。

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 BasicConfigurator.configure();
	     Logger OurLogger = LogManager.getLogger("OurLogger");

	     //create a FileAppender object and 
	     //associate the file name to which you want the logs
	     //to be directed to.
	     //You will also have to set a layout also, here
	     //We have chosen a SimpleLayout
	     FileAppender fileAppender = new FileAppender();
	     fileAppender.setFile("logfile.txt");
	     fileAppender.setLayout(new SimpleLayout());

	     //Add the appender to our Logger Object. 
	     //from now onwards all the logs will be directed
	     //to file mentioned by FileAppender
	     OurLogger.addAppender(fileAppender);
	     fileAppender.activateOptions();

	     //lets print some log 10 times
	     int x = 0;
	     while(x < 11){
		     OurLogger.debug("This is just a log that I want to print " + x);
		     x++;
	     }

	}
}
```

创建appender时，你必须添加要选择的 LayOut。在我们的例子中，我们选择了 SimpleLayout()。此外，每当你在Appender对象中进行更改时，例如，添加文件路径或添加必须调用.activateOptions() 的布局，activateOptions()将激活之前设置的选项。这很重要，因为直到.activateOptions()才会有机会Appender对象。你将在 eclipse 工作区的项目文件夹中找到日志文件。此外，这是日志在日志文件中的样子：

```java
DEBUG - This is just a log that I want to print 0
DEBUG - This is just a log that I want to print 1
DEBUG - This is just a log that I want to print 2
DEBUG - This is just a log that I want to print 3
DEBUG - This is just a log that I want to print 4
DEBUG - This is just a log that I want to print 5
DEBUG - This is just a log that I want to print 6
DEBUG - This is just a log that I want to print 7
DEBUG - This is just a log that I want to print 8
DEBUG - This is just a log that I want to print 9
DEBUG - This is just a log that I want to print 10
```

## 控制台附加程序

出于测试目的，你可能希望将输出日志重定向到控制台。实际上ConsoleAppender将日志定向到System.err和System.out流。这些流也由控制台读取，因此输出也显示在控制台上。让我们看看如何使用ConsoleAppender对象的代码示例

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 BasicConfigurator.configure();
	     Logger OurLogger = LogManager.getLogger("OurLogger");

	     //create a ConsoleAppender object 
	     //You will also have to set a layout also, here
	     //We have chosen a SimpleLayout
	     ConsoleAppender ConsoleAppender = new ConsoleAppender();
	     ConsoleAppender.setLayout(new SimpleLayout());

	     //Add the appender to our Logger Object. 
	     //from now onwards all the logs will be directed
	     //to file mentioned by FileAppender
	     OurLogger.addAppender(ConsoleAppender);
	     ConsoleAppender.activateOptions();

	     //lets print some log 10 times
	     int x = 0;
	     while(x < 11){
	    	 OurLogger.debug("This is just a log that I want to print " + x);
		     x++;
	     } 
  }
}
```

这样你会把所有的日志都重定向到控制台，并且可以在控制台看到输出。

## JDBC 附加程序

JDBCAppenders用于将日志写入数据库。这些附加程序接受数据库连接凭据以连接到数据库。让我们看一个代码示例来了解JDBCAppenders是如何工作

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.jdbc.JDBCAppender;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 BasicConfigurator.configure();
	     Logger OurLogger = LogManager.getLogger("OurLogger");

	     //create a JDBCAppender class instance
	     JDBCAppender dataBaseAppender = new JDBCAppender();
	     //Provide connection details to the 
	     //Database appender
	     dataBaseAppender.setURL("jdbc:mysql://localhost/test"); //Connection url
	     dataBaseAppender.setUser("User1"); //Username for the DB connection
	     dataBaseAppender.setPassword("ThisPassword"); //Password for the DB connection
	     dataBaseAppender.setDriver("com.mysql.jdbc.Driver"); // Drivers to use to connect to DB

	     //set the sql insert statement that you want to use
	     dataBaseAppender.setSql("INSERT INTO LOGS VALUES ('%x', now() ,'%C','%p','%m'");   

         //activate the new options
	     dataBaseAppender.activateOptions();

	     //Attach the appender to the Logger
	     OurLogger.addAppender(dataBaseAppender);

	     int x = 0;
	     while(x < 11){
	    	 OurLogger.debug("This is just a log that I want to print " + x);
		     x++;
	     } 
  }
}
```

此代码解释了如何设置JDBCAppender对象并将其用于日志记录。看到代码后，你会注意到我们必须将插入语句提供给JDBCAppender。该语句用于将日志插入所需的数据库表中。我们使用了语句INSERT INTO LOGS VALUES ('%x', now() ,'%C','%p','%m')它表示日志被插入到名为LOGS的表中。不要担心插入语句中的%x %C和其他类似术语。我们将在下一个名为Layouts 的主题中介绍它们 Appenders是一个非常大的主题，我们刚刚介绍了 3 种最常用的Appender类型。我们还有很多其他可以使用的附加程序。我鼓励你从官方Log4j文档中阅读更多关于appender的信息。我希望本教程对你有所帮助。
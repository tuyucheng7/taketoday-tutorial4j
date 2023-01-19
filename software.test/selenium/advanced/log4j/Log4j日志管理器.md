继续有关 Log4j 的系列教程，让我们尝试了解什么是 Log4j LogManager 以及我们如何使用它。

LogManager顾名思义，是所有记录器对象的管理器。这是你在创建 Logger 对象时引用的静态类。LogManager还保留应用程序创建的所有记录器的列表。如果我要总结一下， LogManager做了以下工作

- 创建 Logger 对象的实例。

- 存储所有创建的记录器对象的引用。

- 允许在代码的不同部分重用相同的记录器对象。

让我们看看这个示例代码。这显示了我们如何从LogManager 创建不同的记录器实例。

```java
package Log4jSample;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {

                //This is how we create a logger object
		Logger logger1 = LogManager.getLogger("Logger1");
		Logger logger2 = LogManager.getLogger("Logger2");
		Logger logger3 = LogManager.getLogger("Logger3");
		BasicConfigurator.configure();
		logger1.info("This is logger 1");
		logger2.info("This is logger 2");
		logger3.info("This is logger 3");		
	}
}
```

我们还可以使用getCurrentLoggers()方法在特定实例中检索LogManager内的所有记录器对象。这是代码示例

```java
package Log4jSample;

import java.util.Enumeration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Logger logger1 = LogManager.getLogger("Logger1");
		Logger logger2 = LogManager.getLogger("Logger2");
		Logger logger3 = LogManager.getLogger("Logger3");
		BasicConfigurator.configure();
		logger1.info("This is logger 1");
		logger2.info("This is logger 2");
		logger3.info("This is logger 3");	

		Enumeration loggers = LogManager.getCurrentLoggers();
		while(loggers.hasMoreElements())
		{
			logger3.info("Logger name is " + loggers.nextElement().getName());			
		}
	}
}
```

LogManager的一个非常重要的属性，它允许我们通过名称检索现有的记录器对象。此外，如果我们尝试创建一个与现有记录器对象同名的记录器对象，它将传递现有记录器对象的引用，而不是创建一个。这可以在下面的代码中显示

```java
package Log4jSample;

import java.util.Enumeration;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Logger logger1 = LogManager.getLogger("Logger1");
		Logger logger2 = LogManager.getLogger("Logger2");
		Logger logger3 = LogManager.getLogger("Logger3");
		BasicConfigurator.configure();
		logger1.info("This is logger 1");
		logger2.info("This is logger 2");
		logger3.info("This is logger 3");	

		Logger logger1_2 = LogManager.getLogger("Logger1");
		Logger logger1_3 = LogManager.getLogger("Logger1");
		//You will see that LogManager doesnt create a new instance of logger
		//Object with name Logger1, instead passes on the reference to the 
		//existing Logger1 object. We can confirm this with following lines
		logger1_2.info("The name of this logger is " + logger1_2.getName());

        if(logger1_3.equals(logger1))
        {
        	logger1_3.info("Both objects are same");
        }
        else
        {
        	logger1_3.info("Logger1 and logger1_2 are different objects");
        }

	}
}
```

这段代码的输出是

```java
1 [main] INFO Logger1 - This is logger 1
2 [main] INFO Logger2 - This is logger 2
2 [main] INFO Logger3 - This is logger 3
2 [main] INFO Logger1 - The name of this logger is Logger1
2 [main] INFO Logger1 - Both objects are same
```

从最后两行输出可以看出，logger1_2 和 logger1_3 都指向在程序开始时创建的同一个记录器对象。这样我们就可以在测试代码的不同部分重用同一个记录器。

## 根记录器对象

应用程序中的所有记录器都遵循一个层次结构，其中根记录器位于层次结构的顶部。你创建的任何记录器都可以追溯到根记录器。

记录器遵循类似于类层次结构的层次结构。任何全名为 abc 的记录器都将 b 作为父亲，a 作为祖先。为了在层次结构中显示根记录器，这就是记录器对象层次结构 Root.ab 的方式，其中 Root 是 b 的祖先。要证明所有记录器都是根记录器的子项，请参见以下代码。

```java
package Log4jSample;

import java.util.Enumeration;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SampleEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Logger logger1 = LogManager.getLogger("Logger1");
		Logger logger2 = LogManager.getLogger("Logger2");
		Logger logger3 = LogManager.getLogger("Logger3");
		BasicConfigurator.configure();
		logger1.info("This is logger 1");
		logger2.info("This is logger 2");
		logger3.info("This is logger 3");	

		Logger rootLogger  = LogManager.getRootLogger();
		Logger rootOf1 = logger1.getRootLogger();

		if(rootOf1.equals(rootLogger))
		{
			rootOf1.info("Both rootLogger and rootOf1 are same objects");	
			rootOf1.info("The Name of the root logger is " + rootLogger.getName());
		}
		else{
		     rootOf1.info("Both rootLogger and rootOf1 are different objects");
		}		
	}
}
```

希望本教程能帮助你了解 LogManager 的使用和工作。请继续关注有关 Log4j 的更多教程。
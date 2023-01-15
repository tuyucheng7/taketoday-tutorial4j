## 1. 概述

Java正在不断发展，并努力为JDK添加新功能。如果我们想在我们的API中使用这些功能，那么这可能会迫使下游依赖项升级其JDK版本。

有时，为了保持兼容，我们不得不等待使用新的语言特性。

不过，在本教程中，我们将了解Multi-Release JAR(MRJAR)以及它们如何同时包含与不同JDK版本兼容的实现。

## 2. 简单示例

让我们看一下一个名为DateHelper的工具类，它有一个检查是否为闰年的方法。我们假设它是使用JDK 7编写并构建为在JRE 7+上运行的：

```java
public class DateHelper {

	private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

	public static boolean checkIfLeapYear(String dateStr) throws Exception {
		logger.info("Checking for leap year usingJava1 calendar API");
        
		Calendar cal = Calendar.getInstance();
		cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
		int year = cal.get(Calendar.YEAR);
        
		return (new GregorianCalendar()).isLeapYear(year);
	}
}
```

checkIfLeapYear方法将由我们的测试应用程序的main方法调用：

```java
public class App {
    
    public static void main(String[] args) throws Exception {
        String dateToCheck = args[0];
        boolean isLeapYear = DateHelper.checkIfLeapYear(dateToCheck);
        logger.info("Date given " + dateToCheck + " is leap year: " + isLeapYear);
    }
}
```

我们知道Java 8有一种更简洁的方式来解析日期。所以，我们想利用这一点并重写我们的逻辑。为此，我们需要切换到JDK 8+。但是，这意味着我们的模块将停止在最初为其编写的JRE 7上工作。除非绝对需要，否则我们肯定是不希望发生这种情况。

## 3. 多版本Jar文件

Java 9中的解决方案是保持原始类不变，而是使用新的JDK创建一个新版本并将它们打包在一起。在运行时，JVM(版本9或更高版本)将调用这两个版本中的任何一个，并优先选择JVM支持的最高版本。

例如，如果MRJAR包含同一类的Java版本7(默认)、9和10，则JVM 10+将执行版本10，而JVM 9将执行版本9。在这两种情况下，默认版本都不会执行，因为该JVM存在更合适的版本。

请注意，新版本类的公共定义应与原始版本完全匹配。换句话说，我们不允许添加任何新版本专有的新公共API。

## 4. 文件夹结构

由于Java中的类通过名称直接映射到文件，因此不可能在同一位置创建新版本的DateHelper类。为此，我们需要在单独的文件夹中创建它们。

让我们首先创建一个与java处于同一级别的文件夹java9，之后，我们复制DateHelper.java，保留其包文件夹结构并将其放在java9中：

```java
src/
    main/
        java/
            cn/
                tuyucheng/
                    taketoday/
                        multireleaseapp/
                            App.java
                            DateHelper.java
        java9/
            cn/
                tuyucheng/
                    taketoday/
                        multireleaseapp/
                            DateHelper.java
```

一些还不支持MRJAR的IDE可能会为重复的DateHelper.java类抛出错误。

我们将在另一个教程中[讨论如何将它与Maven等构建工具集成。](https://maven.apache.org/plugins/maven-compiler-plugin/multirelease.html)现在，我们只关注基本层面。

## 5. 代码更改

让我们重写java9文件夹中DateHelper类的逻辑：

```java
public class DateHelper {

	private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

	public static boolean checkIfLeapYear(String dateStr) throws Exception {
		logger.info("Checking for leap year usingJava9 Date Api");
		return LocalDate.parse(dateStr).isLeapYear();
	}
}
```

请注意，我们没有对这个类中的公共方法签名进行任何更改，而只是更改了内部逻辑。同时，我们没有添加任何新的公共方法。这非常重要，因为如果不遵循这两个规则，jar包的创建将失败。

## 6. Java中的交叉编译

交叉编译是Java中的一个特性，它可以编译文件以在早期版本上运行，这意味着我们不需要安装单独的JDK版本。

让我们使用JDK 9或更高版本编译我们的类。

首先，为Java 7平台编译旧代码：

```bash
javac --release 7 -d classes src/main/java/cn/tuyucheng/taketoday/multireleaseapp/.java
```

其次，为Java 9平台编译新代码：

```bash
javac --release 9 -d classes-9 src/main/java9/cn/tuyucheng/taketoday/multireleaseapp/.java
```

release参数用于指示Java编译器和目标JRE的版本。

## 7. 创建MRJAR

最后，使用JDK 9+版本创建MRJAR文件：

```bash
jar --create --file target/mrjar.jar --main-class cn.tuyucheng.taketoday.multireleaseapp.App
  -C classes . --release 9 -C classes-9 .
```

注意：以上命令都是在模块根路径下(java-9-new-features)执行；

<img src="../assets/img.png">

release选项后跟文件夹名称，使该文件夹的内容打包在jar文件中的版本号值下：

```shell
cn/
    tuyucheng/
        taketoday/
            multireleaseapp/
                App.class
                DateHelper.class
META-INF/
    versions/
        9/
            cn/
                tuyucheng/
                    taketoday/
                        multireleaseapp/
                            DateHelper.class
    MANIFEST.MF
```

MANIFEST.MF文件包含一个可以让JVM知道这是一个MRJAR文件的属性集：

```plaintext
Multi-Release: true
```

因此，JVM在运行时加载适当的类。较旧的JVM会忽略表示这是MRJAR文件的新属性，并将其视为普通JAR文件。

## 8. 测试

最后，让我们用Java 7或8测试我们的jar：

```bash
> java -jar target/mrjar.jar "2012-09-22"
Checking for leap year usingJava1 calendar API 
Date given 2012-09-22 is leap year: true
```

然后，我们针对Java 9或更高版本再次测试jar：

```bash
> java -jar target/mrjar.jar "2012-09-22"
Checking for leap year usingJava9 Date Api
Date given 2012-09-22 is leap year: true
```

## 9. 总结

在本文中，我们通过一个简单的示例了解了如何创建多版本jar文件。
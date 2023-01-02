## 1. 概述

在这个简短的教程中，我们将了解如何使用Maven从任何Java类运行任意[main方法](https://www.baeldung.com/java-main-method)。

## 2. exec-maven-plugin

假设我们有以下类：

```java
public class Exec {

	private static final Logger LOGGER = LoggerFactory.getLogger(Exec.class);

	public static void main(String[] args) {
		LOGGER.info("Running the main method");
		if (args.length > 0) {
			LOGGER.info("List of arguments: {}", Arrays.toString(args));
		}
	}
}
```

我们希望通过Maven从命令行执行它的main()方法。

**为了做到这一点，我们可以使用**[exec-maven-plugin](https://www.mojohaus.org/exec-maven-plugin/)**插件。更具体地说，这个插件的exec:java**[目标](https://www.baeldung.com/maven-goals-phases)**执行提供的java类，并将封闭项目的依赖项作为类路径**。

要执行Exec类的main()方法，我们必须将类的完全限定类名传递给插件：

```shell
mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec"

02:26:45.112 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
```

如上所示，我们使用exec.mainClass系统属性来传递全限定类名。

此外，我们必须确保在运行main方法之前类路径已准备就绪，这就是我们在执行main方法之前编译源代码的原因。

我们可以用普通java和javac实现同样的功能。但是，当我们使用非常大的类路径时，这可能会很麻烦。相反，**当使用这个插件时，Maven会负责自动填充类路径**。

## 3. 传递参数

我们还可以将参数从命令行传递给main方法。为此，我们可以使用exec.args系统属性:

```shell
mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec" -Dexec.args="First Second"

02:31:08.235 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
02:31:08.236 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [First, Second]
```

如上所示，我们传递一个以空格分隔的参数列表。此外，我们可以通过exec.arguments系统参数使用逗号分隔的参数列表：

```shell
mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec" -Dexec.arguments="Hello World,Bye"

02:32:25.616 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
02:32:25.618 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [Hello World, Bye]
```

当我们想在参数本身中使用分隔符(空格或逗号)时，这两种方法会很有用。

## 4. 自定义配置

我们还可以在 pom.xml中显式声明插件依赖项，这样，我们就可以使用自定义和默认配置。

例如，我们可以在插件的<configuration\>标签中指定一个默认的主类：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <mainClass>cn.tuyucheng.taketoday.main.Exec</mainClass>
    </configuration>
</plugin>
```

现在，如果我们不指定该类的完全限定名，将会使用cn.tuyucheng.taketoday.main.Exec。

```shell
mvn compile exec:java
02:33:14.197 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
```

但是，我们仍然可以通过显式指定exec.mainClass覆盖此默认配置。

此外，我们还可以在<configuration\>标签中指定默认程序参数：

```xml
<configuration>
    <mainClass>cn.tuyucheng.taketoday.main.Exec</mainClass>
    <arguments>
        <argument>First</argument>
        <argument>Second</argument>
        <argument>Third</argument>
    </arguments>
</configuration>
```

这样我们就不需要在命令行上传递这些参数：

```shell
mvn clean compile exec:java

[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ maven-exec-plugin ---
 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [First, Second, Third]
```

除了这些配置之外，[官方文档](https://www.mojohaus.org/exec-maven-plugin/java-mojo.html)中还介绍了更多可用的配置。

## 5. 总结

在这篇简短的文章中，我们介绍了如何通过exec-maven-plugin插件从命令行运行main方法。
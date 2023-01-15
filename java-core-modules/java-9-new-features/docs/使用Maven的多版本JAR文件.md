## 1. 简介

Java 9为我们带来的新特性之一是构建[多版本JAR(MRJAR)](多版本Jar文件.md)的能力。正如[JDK Enhancement Proposal](https://openjdk.java.net/jeps/238)所说，这允许我们在同一个JAR中拥有不同的Java特定版本的类。

在本教程中，我们介绍如何使用Maven配置MRJAR文件。

## 2. Maven

Maven是Java生态系统中最常用的构建工具之一；其功能之一是将项目打包到JAR中。在接下来的部分中，我们将探讨如何使用它来构建MRJAR。

## 3. 示例项目

让我们从一个基本的例子开始。首先，我们将定义一个打印当前使用的Java版本的类；在Java 9之前，我们可以使用的方法之一是System.getProperty方法：

```java
public class DefaultVersion implements Version {

	@Override
	public String version() {
		return System.getProperty("java.version");
	}
}
```

从Java 9开始，我们可以使用Runtime类中的新version方法：

```java
public class DefaultVersion implements Version {

	@Override
	public String version() {
		return Runtime.version().toString();
	}
}
```

使用这个方法，我们可以得到一个[Runtime.Version](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Runtime.Version.html)类实例，它为我们提供了有关新版本字符串方案格式中使用的JVM的信息。

另外，让我们添加一个App类来输出版本：

```java
public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		logger.info(String.format("Running on %s", new DefaultVersion().version()));
	}
}
```

最后，让我们将每个版本的DefaultVersion放到它自己的src/main目录结构中：

```powershell
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cn
│   │   │       └── tuyucheng
|   |   |           └── taketoday
│   │   │               └── multireleaseapp
│   │   │                   ├── DefaultVersion.java
│   │   │                   └── App.java
│   │   └── java9
│   │       └── cn
│   │           └── tuyucheng
|   |               └── taketoday
│   │                   └── multireleaseapp
│   │                       └── DefaultVersion.java
```

## 4. 配置

要从上面的类配置MRJAR，我们需要使用两个Maven插件：Compiler插件和JAR插件。

### 4.1 Maven编译器插件

在Maven编译器插件中，我们需要为我们要打包的每个Java版本配置一个<execution>。在这种情况下，我们添加两个：

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<executions>
				<execution>
					<id>compile-java-8</id>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<source>1.8</source>
						<target>1.8</target>
						<compileSourceRoots>
							<compileSourceRoot>${project.basedir}/src/main/java8</compileSourceRoot>
						</compileSourceRoots>
					</configuration>
				</execution>
				<execution>
					<id>compile-java-9</id>
					<phase>compile</phase>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<release>9</release>
						<compileSourceRoots>
							<compileSourceRoot>${project.basedir}/src/main/java9</compileSourceRoot>
						</compileSourceRoots>
						<outputDirectory>${project.build.outputDirectory}/META-INF/versions/9
						</outputDirectory>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

我们将使用第一个<execution> compile-java-8来编译Java 8类，并使用compile-java-9 <execution>来编译Java 9类。

此外，我们还需要为Java 9版本配置compileSourceRoot和outputDirectory标签以及相应的文件夹。

但是，从[maven-compiler-plugin 3.7.1](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#multiReleaseOutput)开始，我们不需要手动设置输出目录。相反，我们所要做的就是启用 multiReleaseOutput属性：

```xml
<configuration> 
    <release>9</release> 
    <compileSourceRoots> 
        <compileSourceRoot>${project.basedir}/src/main/java9</compileSourceRoot> 
    </compileSourceRoots> 
    <multiReleaseOutput>true</multiReleaseOutput>
</configuration>
```

当设置为true时，编译器插件将所有特定于版本的类移动至META-INF/versions/${release}目录。请注意，我们必须在这里将release标签设置为所需的Java版本，否则编译器插件会失败。

### 4.2 Maven JAR插件

我们使用JAR插件在MANIFEST文件中将Multi-Release属性设置为true。通过这种配置，Java运行时将在我们的 JAR文件的META-INF/versions文件夹中查找特定于版本的类；否则，仅使用基类。

下面添加maven-jar-plugin配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <archive>
            <manifestEntries>
                <Multi-Release>true</Multi-Release>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```

## 5. 测试

是时候测试我们生成的JAR文件了。当我们使用Java 8执行时，我们将看到以下输出：

```shell
[main] INFO cn.tuyucheng.taketoday.multireleaseapp.App - Running on 1.8.0_252
```

但是如果我们使用Java 17执行，我们会看到：

```shell
[main] INFO cn.tuyucheng.taketoday.multireleaseapp.App - Running on 14.0.1+7
```

正如我们所见，现在它使用了新的输出格式。请注意，尽管我们的MRJAR是使用Java 9构建的，但它与多个主要的 Java平台版本兼容。

## 6. 总结

在这个简短的教程中，我们演示了如何配置Maven工具来生成一个简单的MRJAR。